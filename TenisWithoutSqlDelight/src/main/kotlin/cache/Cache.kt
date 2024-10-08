package org.example.cache

import org.example.config.AppConfig
import org.lighthousegames.logging.logging

private val logger = logging()

class Cache<K,T> (
    private val config : AppConfig
) {
    private val cache = mutableMapOf<K, T>()

    fun get(key: K): T? {
        logger.debug { "Obteniendo valor de la cache" }
        return if (cache.containsKey(key)) {
            cache.getValue(key)
        } else {
            return null
        }
    }

    fun put(key: K, value: T): T {
        logger.debug { "Guardando valor en la cache" }
        if (cache.size >= config.cacheSize && !cache.containsKey(key)) {
            logger.debug { "Eliminando valor de la cache" }
            cache.remove(cache.keys.first())
        }
        cache[key] = value
        return value
    }

    fun remove(key: K): T?{
        logger.debug { "Eliminando valor de la cache" }
        return if (cache.containsKey(key)) {
            cache.remove(key)
        } else {
            return null
        }
    }

    fun clear() {
        logger.debug { "Limpiando cache" }
        cache.clear()
    }

}