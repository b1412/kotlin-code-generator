package com.github.b1412.generator.task

import com.github.b1412.generator.task.processor.MultipleTaskProcessor

open class MultipleTask(
    folder: FilenameProcessor,
    templatePath: String,
    replaceFile: Boolean = true,
    entityExtProcessors: List<EntityExtProcessor> = listOf(),
    ignoreEntities: List<String> = listOf()
) : Task(
    folder = folder,
    taskType = MultipleTaskProcessor(),
    templatePath = templatePath,
    replaceFile = replaceFile,
    entityExtProcessors = entityExtProcessors,
    ignoreEntities = ignoreEntities
)
