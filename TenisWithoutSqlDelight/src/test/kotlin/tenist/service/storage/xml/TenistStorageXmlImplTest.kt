package tenist.service.storage.xml

import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.nio.file.Files
import org.example.tenist.errors.TenistError
import org.example.tenist.models.Dexterity
import org.example.tenist.models.Tenist
import org.example.tenist.service.storage.xml.TenistStorageXmlImpl
import java.time.LocalDate
import java.time.LocalDateTime

class TenistStorageXmlImplTest {

    private val file = Files.createTempFile("", ".xml").toFile()
    private val tenistStorageXml = TenistStorageXmlImpl()

    @AfterEach
    fun tearDown() {
        Files.deleteIfExists(file.toPath())
    }

    @Test
    fun `export should return Ok when exporting a list of tenists to XML`() {
        // Arrange
        val tenists = listOf(
            Tenist(
                id = 1,
                name = "Novak Djokovic",
                country = "Serbia",
                height = 188.0,
                weight = 77,
                points = 12030,
                dominantHand = Dexterity.DIESTRO,
                birthDate = LocalDate.of(1987, 5, 22),
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            ),
            Tenist(
                id = 2,
                name = "Daniil Medvedev",
                country = "Rusia",
                height = 198.0,
                weight = 83,
                points = 10370,
                dominantHand = Dexterity.DIESTRO,
                birthDate = LocalDate.of(1996, 2, 11),
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        )

        // Act
        val result = tenistStorageXml.export(file, tenists)

        // Assert
        assertTrue(result.isOk)
        val exportedContent = file.readText()
        assertTrue(exportedContent.contains("Novak Djokovic"))
        assertTrue(exportedContent.contains("Serbia"))
        assertTrue(exportedContent.contains("Daniil Medvedev"))
        assertTrue(exportedContent.contains("Rusia"))
    }

    @Test
    fun `export should return error if file cannot be written`() {
        // Arrange
        val tenists = emptyList<Tenist>()
        file.setReadOnly()

        // Act
        val result = tenistStorageXml.export(file, tenists)

        file.setWritable(true) // To clean up after the test

        // Assert
        assertTrue(result.isErr)
        assertTrue(result.error is TenistError.ExportError)
        assertTrue(result.error.msg.contains("No se ha podido exportar el fichero XML"))
    }
}
