package com.github.b1412.generator.ext

import com.github.b1412.generator.entity.CodeEntity
import com.google.common.base.CaseFormat

object Utils {
    @JvmStatic
    fun hyphen(source: String): String {
        return CaseFormat.UPPER_CAMEL.converterTo(CaseFormat.LOWER_HYPHEN).convert(source) as String
    }

    @JvmStatic
    fun lowerHyphen(source: String): String {
        return CaseFormat.UPPER_CAMEL.converterTo(CaseFormat.LOWER_HYPHEN).convert(source) as String
    }

    @JvmStatic
    fun lowerUnderscore(source: String): String {
        return CaseFormat.UPPER_CAMEL.converterTo(CaseFormat.LOWER_UNDERSCORE).convert(source) as String
    }

    @JvmStatic
    fun lowerCamel(source: String): String {
        return CaseFormat.UPPER_CAMEL.converterTo(CaseFormat.LOWER_CAMEL).convert(source) as String
    }

    @JvmStatic
    fun upperCamel(source: String): String {
        return CaseFormat.UPPER_CAMEL.converterTo(CaseFormat.UPPER_CAMEL).convert(source) as String
    }

    @JvmStatic
    fun spacedCapital2LowerHyphen(source: String): String {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, source.split(" ").joinToString(""))
    }

    @JvmStatic
    fun spacedCapital2LowerCamel(source: String): String {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, source.split(" ").joinToString(""))
    }

    @JvmStatic
    fun spacedCapital(source: String): String {
        return CaseFormat.LOWER_CAMEL.converterTo(CaseFormat.LOWER_UNDERSCORE)
            .convert(source)!!
            .split("_").joinToString(" ") { it.capitalize() }
    }

    @JvmStatic
    fun upperUderscore(source: String): String {
        return CaseFormat.UPPER_CAMEL.converterTo(CaseFormat.UPPER_UNDERSCORE).convert(source) as String
    }


    @JvmStatic
    fun findCodeEntity(entities: List<CodeEntity>, name: String): CodeEntity? {
        return entities.firstOrNull { it.name == name }
    }
}

