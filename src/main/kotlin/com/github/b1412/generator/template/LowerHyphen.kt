package com.github.b1412.generator.template

import com.github.b1412.generator.ext.Utils
import freemarker.template.SimpleScalar
import freemarker.template.TemplateMethodModelEx

class LowerHyphen : TemplateMethodModelEx {
    override fun exec(args: List<*>?): Any {
        if (args == null || args.isEmpty()) {
            throw RuntimeException("missing arg")
        }
        val simpleScalar = args[0] as SimpleScalar
        val content = simpleScalar.asString
        return Utils.lowerHyphen(content)
    }
}