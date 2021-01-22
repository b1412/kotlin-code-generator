package com.github.b1412.generator.entity

import com.github.b1412.generator.metadata.ExcelFeature

data class TestEntity(
    @ExcelFeature(index = 1)
    val age: Int
): BaseEntity()