package com.github.b1412.generator.entity


import arrow.core.getOrElse
import arrow.core.toOption
import com.github.b1412.generator.ext.Utils
import com.github.b1412.generator.findClasses
import com.github.b1412.generator.metadata.*
import org.hibernate.validator.constraints.Range
import java.lang.reflect.Field
import java.lang.reflect.ParameterizedType
import javax.persistence.Column
import javax.persistence.Id
import javax.validation.constraints.*

data class CodeEntity(

    var nativeClass: Class<*>,
    var fields: List<CodeField> = listOf(),
    var id: Int? = null,
    var code: Int = 0,
    var name: String = "",

    var display: String? = null,
    var security: Boolean = true,
    var excelImport: Boolean = false,
    var excelExport: Boolean = false,
    var tree: Boolean = false,

    var permissions: List<CodePermission> = mutableListOf(),
    var menus: List<CodeMenu> = mutableListOf(),
    var css: String? = null,

    var embeddedEntity: MutableList<CodeEntity> = mutableListOf(),
    var embeddedList: MutableList<String> = mutableListOf(),
    var primaryKeyInList: Boolean = false,

    var action: CodeAction? = null,
    var toolbar: CodeToolbar? = null
)

fun String.remainLastIndexOf(s: String): String {
    val index = this.lastIndexOf(s)
    return this.substring(0, index.inc())
}


val entityCode: MutableMap<String, Int> = mutableMapOf()

fun scanForCodeEntities(path: String, clazz: Class<*>, projectId: Int): List<CodeEntity> {
    val entities = path.split(",").flatMap { findClasses(clazz, it) }
    entities.mapIndexed { index, c -> entityCode.put(c.name, index.inc() + projectId * 100) }
    return entities
        .asSequence()
        .filter {
            it.getDeclaredAnnotation(EntityFeature::class.java).toOption().map { it.generated }.getOrElse { true }
        }.map { entityClass2CodeEntity(it, clazz) }
        .toList()
}

fun entityClass2CodeEntity(clazz: Class<*>, base: Class<*>): CodeEntity {
    var codeEntity = CodeEntity(
        name = clazz.simpleName,
        nativeClass = clazz
    )

    codeEntity.permissions = clazz.getAnnotationsByType(PermissionFeature::class.java)
        .map { CodePermission(role = it.role, rule = it.rule, httpMethod = it.httpMethods) }

    val ignoredFields = listOf("serialVersionUID", "Companion")
    clazz.declaredAnnotations.forEach {
        when (it) {
            is EntityFeature -> {
                codeEntity = codeEntity.copy(
                    code = entityCode[clazz.name]!!,
                    security = it.security,
                    embeddedList = it.embeddedList.split(",").toMutableList(),
                    tree = it.tree,
                    excelImport = it.excelImport,
                    excelExport = it.excelExport,
                    primaryKeyInList = it.primaryKeyInList
                )
            }
            is ExcelFeature -> {
                codeEntity = codeEntity.copy(
                    excelExport = it.exportable,
                    excelImport = it.importable,
                )
            }
        }
    }
    val allFields = clazz.declaredFields + clazz.superclass.declaredFields
    val (rawFields, embeddedEntity) = allFields
        .filter { field: Field -> ignoredFields.all { ignoreField -> ignoreField != field.name } }
        .partition { field ->
            field.getAnnotation(FieldFeature::class.java).toOption().map { it.embeddedEntity.not() }.getOrElse { true }
        }
    val columns: MutableMap<String, MutableList<CodeColumn>> = mutableMapOf()

    val fields = rawFields.map { field ->
        var codeField = CodeField(
            nativeField = field,
            name = field.name,
            label = field.getAnnotation(FieldFeature::class.java).toOption().map {
                if (it.label.isNotBlank()) {
                    it.label
                } else {
                    Utils.spacedCapital(field.name)
                }
            }.getOrElse { Utils.spacedCapital(field.name) },

            type = when {
                List::class.java.isAssignableFrom(field.type) -> {
                    val name = (field.genericType as ParameterizedType).actualTypeArguments[0]
                        .typeName
                    val index = name.lastIndexOf(".")
                    FieldType(
                        name = "List",
                        element = name.substring(index.inc())

                    )
                }
                base.isAssignableFrom(field.type) ->
                    FieldType(name = "Entity", element = field.type.simpleName)
                else -> FieldType(name = field.type.simpleName)
            }
        )
        field.declaredAnnotations.forEach { fieldAnnotation ->
            when (fieldAnnotation) {
                is NotNull -> {
                    codeField = codeField.copy(required = true)
                }
                is Size -> {
                    codeField = codeField.copy(sizeMin = fieldAnnotation.min, sizeMax = fieldAnnotation.max)
                }
                is Max -> {
                    codeField = codeField.copy(rangeMax = fieldAnnotation.value)
                }
                is Min -> {
                    codeField = codeField.copy(rangeMin = fieldAnnotation.value)
                }
                is Range -> {
                    codeField = codeField.copy(rangeMin = fieldAnnotation.min, rangeMax = fieldAnnotation.max)
                }
                is Pattern -> {
                    codeField = codeField.copy(pattern = fieldAnnotation.regexp)
                }
                is Future -> {
                    codeField = codeField.copy(future = true)
                }
                is Past -> {
                    codeField = codeField.copy(past = true)
                }
                is Column -> {
                    codeField = codeField.copy(
                        unique = fieldAnnotation.unique
                    )
                }
                is Id -> codeField = codeField.copy(primaryKey = true)
                is FieldFeature -> {
                    codeField = codeField.copy(
                        searchable = fieldAnnotation.searchable,
                        sortable = fieldAnnotation.sortable,
                        readonly = fieldAnnotation.readonly,
                        switch = fieldAnnotation.switch,
                        selectOne = fieldAnnotation.selectOne,
                        selectMany = fieldAnnotation.selectMany,
                        addDynamicMany = fieldAnnotation.addDynamicMany,
                        hiddenInForm = fieldAnnotation.hiddenInForm,
                        hiddenInSubForm = fieldAnnotation.hiddenInSubForm,
                        hiddenInList = fieldAnnotation.hiddenInList,
                        limit = fieldAnnotation.limit,
                        textarea = fieldAnnotation.textarea,
                        rows = fieldAnnotation.rows,
                        cols = fieldAnnotation.cols,
                        richText = fieldAnnotation.richText,
                        display = fieldAnnotation.display.split(",").filter { it.isNotEmpty() },
                        weight = fieldAnnotation.weight,
                        range = fieldAnnotation.range
                    )
                }
                is ExcelFeature -> {
                    codeField = codeField.copy(
                        column = if (fieldAnnotation.column.isNotBlank()) {
                            fieldAnnotation.column
                        } else {
                            field.name
                        },
                        header = if (fieldAnnotation.header.isNotBlank()) {
                            fieldAnnotation.header
                        } else {
                            field.name
                        },
                        exportable = fieldAnnotation.exportable,
                        importable = fieldAnnotation.importable,
                        excelIndex = fieldAnnotation.index,
                    )
                }
                is AttachmentFeature -> {
                    codeField = codeField.copy(
                        attachment = true,
                        attachmentConfig = AttachmentConfig(
                            maxFiles = fieldAnnotation.maxFiles
                        )
                    )
                }
            }
        }
        val columnFeatures = field.getDeclaredAnnotationsByType(ColumnFeature::class.java)

        columnFeatures.forEach { columnFeature ->
            val list = columns.getOrDefault(columnFeature.menuName, mutableListOf())
            list.add(
                CodeColumn(
                    asc = columnFeature.isAsc,
                    active = columnFeature.isActive,
                    columnDisplay = if (columnFeature.columnDisplay.isNotBlank()) {
                        columnFeature.columnDisplay
                    } else {
                        Utils.spacedCapital(field.name)
                    },
                    columnModel = if (columnFeature.columnModel.isNotBlank()) {
                        columnFeature.columnModel
                    } else {
                        Utils.lowerCamel(field.name)
                    },
                    sortable = columnFeature.isSortable,
                    display = columnFeature.display,
                    hiddenInList = columnFeature.hiddenInList,
                    hiddenInForm = columnFeature.hiddenInForm
                )
            )
            columns[columnFeature.menuName] = list
        }
        codeField
    }

    clazz.getAnnotation(UserColumns::class.java)?.value?.forEach { userColumn ->

        /*  val codeField = if ("id" == userColumn.name) {
              CodeField(
                      type = FieldType("Long"),
                      name = userColumn.name,
                      hiddenInList = true,
                      hiddenInForm = true
              )
          } else {
              CodeField(
                      type = FieldType("String"),
                      name = userColumn.name,
                      hiddenInList = false,
                      hiddenInForm = false
              )
          }
          fields += codeField*/
        userColumn.columnFeatures.forEach { columnFeature ->
            val list = columns.getOrDefault(columnFeature.menuName, mutableListOf())
            list.add(
                CodeColumn(
                    asc = columnFeature.isAsc,
                    active = columnFeature.isActive,
                    columnDisplay = if (columnFeature.columnDisplay.isNotBlank()) {
                        columnFeature.columnDisplay
                    } else {
                        Utils.spacedCapital(userColumn.name)
                    },
                    columnModel = if (columnFeature.columnModel.isNotBlank()) {
                        columnFeature.columnModel
                    } else {
                        Utils.lowerCamel(userColumn.name)
                    },
                    sortable = columnFeature.isSortable,
                    display = columnFeature.display,
                    hiddenInList = columnFeature.hiddenInList,
                    hiddenInForm = columnFeature.hiddenInForm
                )
            )
            columns[columnFeature.menuName] = list
        }
    }
    codeEntity.fields = fields
    codeEntity.embeddedEntity = embeddedEntity.map { entityClass2CodeEntity(it.type, base) }.toMutableList()
    codeEntity.menus.forEach {
        val v = columns[it.name]
        if (v != null) {
            it.columns.addAll(v)
        }
    }
    return codeEntity

}

fun main() {
    val clazz = "com.github.b1412.email.entity.Attachment"
    val index = clazz.lastIndexOf(".")
    println(clazz.substring(index.inc()))
}
