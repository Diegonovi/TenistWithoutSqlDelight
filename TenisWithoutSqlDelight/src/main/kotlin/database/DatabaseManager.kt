package org.example.database

import org.example.config.AppConfig
import org.koin.core.annotation.Singleton
import org.lighthousegames.logging.logging
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

private val logger = logging()

@Singleton
class DatabaseManager(
    private val config : AppConfig
) {
    var connection: Connection? = null

    init {
        try {
            //Establece la conexión y crea el fichero
            if (connection == null) {
                connection = DriverManager.getConnection(config.databaseUrl)
            }
            connection = DriverManager.getConnection(config.databaseUrl)
            if (config.databaseRemoveData) {
                val query = "DELETE FROM Tenist"
                connection!!.createStatement().use { statement ->
                    statement.execute(query)
                    logger.info{ "Datos eliminados" }
                }
            }
            if (config.databaseInit){
                val sqlScript = ClassLoader.getSystemResource("tables.sql").readText()
                connection!!.createStatement().use { statement ->
                    statement.execute(sqlScript)
                }
                logger.info{ "Base de datos creada" }
            }
        } catch (e: SQLException) {
            logger.error{ "Error conectándose con base de datos: ${e.message}" }
        }
    }
}