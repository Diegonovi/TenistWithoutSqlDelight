package org.example.tenist.service.database

import com.github.michaelbull.result.*
import org.example.cache.Cache
import org.example.tenist.errors.TenistError
import org.example.tenist.models.Tenist
import org.example.tenist.repository.TenistRepository
import org.example.tenist.validator.TenistValidator
import org.koin.core.annotation.Singleton
import org.lighthousegames.logging.logging

val logger = logging()

@Singleton
class TenistServiceImpl(
private val cache: Cache<Int, Tenist>,
private val tenistRepository: TenistRepository,
private val tenistValidator: TenistValidator
) : TenistService {

    /**
     * Guarda un nuevo tenista.
     * @param tenist El tenista a guardar.
     * @return un [Result] que contiene el tenista guardado o un error [TenistError].
     */
    override fun save(tenist: Tenist): Result<Tenist, TenistError> {
        logger.debug { "Guardando tenista con ID: ${tenist.id}" }
        tenistValidator.validate(tenist)
            .onSuccess {
                if (cache.get(tenist.id) == null && tenistRepository.get(tenist.id) == null){
                    tenistRepository.create(tenist)?.let {
                        cache.put(it.id,it)
                        return Ok(it)
                    }
                }else {
                    logger.error { "No se pudo crear el tenista con ID: ${tenist.id} porque ya existe en la BBDD" }
                    return Err(TenistError.TenistAlreadyExists("El tenista con ID: ${tenist.id} ya existe"))
                }
            }
        logger.error { "No se pudo crear el tenista con ID: ${tenist.id} porque no es válido" }
        return Err(TenistError.InvalidTenist("El tenista con ID: ${tenist.id} no es válido"))
    }

    /**
     * Elimina un tenista.
     * @param tenist El tenista a eliminar.
     * @return un [Result] con [Unit] si fue exitoso o un error [TenistError].
     */
    override fun delete(tenist: Tenist): Result<Unit, TenistError> {
        logger.debug { "Eliminando tenista con ID: ${tenist.id}" }
        cache.get(tenist.id)?.let {
            tenistRepository.delete(tenist.id)?.let { //Si está en la caché
                cache.remove(tenist.id)
                return Ok(Unit)
            }
        }
        tenistRepository.get(tenist.id)?.let { // Si no está en la caché, pero sí está en la BBDD
            tenistRepository.delete(tenist.id)?.let {
                cache.remove(tenist.id)
                return Ok(Unit)
            }
        }
        logger.error { "No se pudo eliminar el tenista con ID: ${tenist.id} porque no existe" } // Si no está en ninguna parte
        return Err(TenistError.TenistDoesNotExist("No se pudo eliminar el tenista con ID: ${tenist.id}")) //Si no está en ninguna parte
    }

    /**
     * Busca un tenista por su ID.
     * @param id El ID del tenista a buscar.
     * @return un [Result] que contiene el tenista encontrado o un error [TenistError].
     */
    override fun findById(id: Int): Result<Tenist, TenistError> {
        logger.debug { "Buscando tenista con ID: $id" }
        cache.get(id)?.let { // Si está en la caché
            return Ok(it)
        }
        tenistRepository.get(id)?.let { // Si no está en la caché, pero sí está en la BBDD
            cache.put(it.id, it)
            return Ok(it)
        }
        logger.error { "No se pudo encontrar el tenista con ID: $id" } // Si no está en ninguna parte
        return Err(TenistError.TenistDoesNotExist("No se pudo encontrar el tenista con ID: $id")) // Si no está en ninguna parte
    }

    /**
     * Actualiza un tenista existente.
     * @param tenist El tenista a actualizar.
     * @return un [Result] que contiene el tenista actualizado o un error [TenistError].
     */
    override fun update(tenist: Tenist): Result<Tenist, TenistError> {
        logger.debug { "Actualizando tenista con ID: ${tenist.id}" }
        tenistValidator.validate(tenist)
            .onFailure {
                logger.error { "No se pudo actualizar el tenista con ID: ${tenist.id} porque no es válido" }
                return Err(TenistError.InvalidTenist("El tenista con ID: $tenist no es válido"))
            }
        cache.get(tenist.id)?.let { // Si está en la caché
            tenistRepository.update(tenist)?.let {
                cache.remove(it.id)
                cache.put(it.id, it)
                return Ok(it)
            }
        }
        tenistRepository.get(tenist.id)?.let { // Si no está en la caché pero si en la BBDD
            tenistRepository.update(tenist)?.let {
                cache.remove(it.id)
                cache.put(it.id, it)
                return Ok(it)
            }
        }
        logger.error { "No se pudo actualizar el tenista con ID: ${tenist.id} porque no existe" } // Si no está en ninguna parte
        return Err(TenistError.TenistDoesNotExist("El tenista con ID: ${tenist.id} no existe"))
    }

    /**
     * Busca todos los tenistas.
     * @return un [Result] que contiene la lista de tenistas o un error [TenistError].
     */
    override fun findAll(): Result<List<Tenist>, TenistError> {
        logger.debug { "Buscando todos los tenistas" }
        return Ok(tenistRepository.getAll())
    }
}