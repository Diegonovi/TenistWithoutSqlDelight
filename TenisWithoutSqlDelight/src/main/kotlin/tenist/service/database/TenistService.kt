package org.example.tenist.service.database

import com.github.michaelbull.result.Result
import org.example.tenist.errors.TenistError
import org.example.tenist.models.Tenist

interface TenistService {
    fun save(tenist: Tenist) : Result<Tenist, TenistError>
    fun delete(tenist: Tenist) : Result<Unit, TenistError>
    fun findById(id: Int) : Result<Tenist, TenistError>
    fun update(tenist: Tenist) : Result<Tenist, TenistError>
    fun findAll() : Result<List<Tenist>, TenistError>
}