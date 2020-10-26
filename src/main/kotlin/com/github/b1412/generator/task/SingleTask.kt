package com.github.b1412.generator.task

import com.github.b1412.generator.task.processor.SingleTaskProcessor

open class SingleTask(
        folder: FilenameProcessor,
        filename: FilenameProcessor,
        templatePath: String,
        replaceFile: Boolean = true,
        projectExtProcessors: List<ProjectExtProcessor> = listOf()

) : Task(
        folder = folder,
        filename = filename,
        taskType = SingleTaskProcessor(),
        templatePath = templatePath,
        replaceFile = replaceFile,
        projectExtProcessors = projectExtProcessors
)
