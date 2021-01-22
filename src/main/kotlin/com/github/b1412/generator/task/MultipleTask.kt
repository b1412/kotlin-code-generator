package com.github.b1412.generator.task

import com.github.b1412.generator.task.processor.MultipleTaskProcessor

open class MultipleTask(
    filePath: FilenameProcessor,
    templatePath: String,
    replaceFile: Boolean = true,
    entityExtProcessors: List<EntityExtProcessor> = listOf(),
    fieldExtProcessors: List<FieldExtProcessor> = listOf(),
    ignoreEntities: List<String> = listOf()
) : Task(
    filePath = filePath,
    taskType = MultipleTaskProcessor(),
    templatePath = templatePath,
    replaceFile = replaceFile,
    entityExtProcessors = entityExtProcessors,
    fieldExtProcessors = fieldExtProcessors,
    ignoreEntities = ignoreEntities
)
