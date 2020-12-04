package com.github.b1412.generator.task

import com.github.b1412.generator.task.processor.SingleTaskProcessor

open class SingleTask(
    filePath: FilenameProcessor,
    templatePath: String,
    replaceFile: Boolean = true,
    projectExtProcessors: List<ProjectExtProcessor> = listOf(),
    ignoreEntities: List<String> = listOf()
) : Task(
    filePath = filePath,
    taskType = SingleTaskProcessor(),
    templatePath = templatePath,
    replaceFile = replaceFile,
    projectExtProcessors = projectExtProcessors,
    ignoreEntities = ignoreEntities
)
