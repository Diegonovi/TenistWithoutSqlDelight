package org.example.database

import org.example.config.AppConfig
import org.lighthousegames.logging.logging
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

private val logger = logging()

class DatabaseManager(
    private val config : AppConfig
) {
    lateinit var connection: Connection

    init {
        try {
            //Establece la conexión y crea el fichero
            connection = DriverManager.getConnection(config.databaseUrl)
            if (config.databaseInit){
                val sqlScript = ClassLoader.getSystemResource("database.sql").readText()
                connection.createStatement().use { statement ->
                    statement.execute(sqlScript)
                }
                logger.info{ "Base de datos creada" }
            }
        } catch (e: SQLException) {
            logger.error{ "Error conectándose con base de datos: ${e.message}" }
        }
    }


}