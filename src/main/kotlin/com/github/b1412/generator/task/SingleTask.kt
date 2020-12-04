package com.github.b1412.generator.task

import com.github.b1412.generator.task.processor.SingleTaskProcessor

open class SingleTask(
    folder: FilenameProcessor,
    templatePath: String,
    replaceFile: Boolean = true,
    projectExtProcessors: List<ProjectExtProcessor> = listOf(),
    ignoreEntities: List<String> = listOf()
) : Task(
    folder = folder,
    taskType = SingleTaskProcessor(),
    templatePath = templatePath,
    replaceFile = replaceFile,
    projectExtProcessors = projectExtProcessors,
    ignoreEntities = ignoreEntities
)
