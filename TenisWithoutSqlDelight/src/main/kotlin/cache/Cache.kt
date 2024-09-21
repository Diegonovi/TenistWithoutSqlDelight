package org.example.cache

import com.github.michaelbull.result.Result
import org.example.cache.errors.CacheError

interface Cache <T,K> {
    fun get(key: K): T?
    fun put(key: K, value: T): T
    fun remove(key: K): T?
    fun clear()
}