package com.github.b1412.generator.task

import com.github.b1412.generator.task.processor.MultipleTaskProcessor

open class MultipleTask(
    filePath: FilenameProcessor,
    templatePath: String,
    replaceFile: Boolean = true,
    entityExtProcessors: List<EntityExtProcessor> = listOf(),
    fieldExtProcessor: FieldExtProcessor? = null,
    ignoreEntities: List<String> = listOf()
) : Task(
    filePath = filePath,
    taskType = MultipleTaskProcessor(),
    templatePath = templatePath,
    replaceFile = replaceFile,
    entityExtProcessors = entityExtProcessors,
    fieldExtProcessor = fieldExtProcessor,
    ignoreEntities = ignoreEntities
)
