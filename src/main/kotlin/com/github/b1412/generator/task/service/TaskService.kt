package com.github.b1412.generator.task.service

import com.github.b1412.generator.entity.CodeEntity
import com.github.b1412.generator.entity.CodeProject
import com.github.b1412.generator.ext.asMap
import com.github.b1412.generator.task.Task
import java.io.File


object TaskService {
    fun processTask(codeProject: CodeProject, task: Task): Pair<Task, List<String>> {
        val paths: List<String>
        val scope = mutableMapOf<String, Any>()
        val codeProjectMap = codeProject.asMap().toMutableMap()
        task.projectExtProcessors.forEach {
            codeProjectMap += (it.invoke(task, codeProject))
        }
        scope["project"] = codeProjectMap
        task.templateHelper = codeProject.templateEngine
        task.templateHelper!!.putAll(scope)
        paths = task.run(codeProject, scope)
        return Pair(task, paths)
    }

    fun processTemplate(
        codeProject: CodeProject,
        codeEntity: CodeEntity?,
        task: Task,
        root: Map<String, Any>
    ): List<String> {
        if (task.multiFiles.isEmpty()) {
            val templateFilename = task.templatePath
            var folder = task.folder(codeProject, codeEntity)
            folder = task.targetPath + File.separator + folder

            val folderDir = File(folder).parentFile
            if (!folderDir.exists()) {
                folderDir.mkdirs()
            }
            val outputFilename = folder
            val outputFile = File(outputFilename)
            if (task.replaceFile || !outputFile.exists()) {
                task.templateHelper!!.exec(templateFilename, outputFilename)
            }
            return listOf(outputFilename)
        } else {
            return task.multiFiles.map {
                it.forEach { e ->
                    task.templateHelper!!.put(e.key, e.value)
                }
                val templateFilename = task.templatePath
                var folder = task.folder(codeProject, codeEntity)
                folder = task.targetPath + File.separator + folder
                val folderDir = File(folder).parentFile
                if (!folderDir.exists()) {
                    folderDir.mkdirs()
                }
                val outputFilename = folder
                val outputFile = File(outputFilename)
                if (task.replaceFile || !outputFile.exists()) {
                    task.templateHelper!!.exec(templateFilename, outputFilename)
                }
                outputFilename
            }
        }
    }
}
