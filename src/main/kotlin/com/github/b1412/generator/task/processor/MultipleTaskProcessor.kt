package com.github.b1412.generator.task.processor

import com.github.b1412.generator.entity.CodeProject
import com.github.b1412.generator.ext.asMap
import com.github.b1412.generator.task.Task
import com.github.b1412.generator.task.processor.ITaskProcessor
import com.github.b1412.generator.task.service.TaskService
import com.google.common.collect.Lists

class MultipleTaskProcessor : ITaskProcessor {
    override fun run(codeProject: CodeProject, task: Task, context: MutableMap<String, Any>): List<String> {
        val paths = Lists.newArrayList<String>()
        for (codeEntity in codeProject.entities) {
            if (task.ignoreEntities.any { it == codeEntity.name }) {
                continue
            }
            val codeEntityMap = codeEntity.asMap().toMutableMap()
            task.entityExtProcessors.forEach {
                val result = it.invoke(task, codeProject, codeEntity)
                codeEntityMap += result
            }
            val fieldProcessor = task.fieldExtProcessor
            if (fieldProcessor != null) {
                val fieldsMap = codeEntity.fields.map {
                    it.asMap().toMutableMap() + fieldProcessor.invoke(task, it)
                }
                codeEntityMap["fields"] = fieldsMap
            }

            codeProject.templateEngine.put("entity", codeEntityMap)
            paths.addAll(TaskService.processTemplate(codeProject, codeEntity, task, context))
        }
        return paths
    }
}