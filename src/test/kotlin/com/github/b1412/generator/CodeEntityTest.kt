package com.github.b1412.generator

import com.github.b1412.generator.entity.BaseEntity
import com.github.b1412.generator.entity.TestEntity
import com.github.b1412.generator.entity.entityClass2CodeEntity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CodeEntityTest {
    @Test
    fun testNativeEntity() {
        val results = entityClass2CodeEntity(TestEntity::class.java, BaseEntity::class.java)
        assertThat(results.nativeClass).isEqualTo(TestEntity::class.java)
    }
}