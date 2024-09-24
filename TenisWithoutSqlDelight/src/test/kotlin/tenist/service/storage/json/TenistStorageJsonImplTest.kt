package tenist.service.storage.json

import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Files
import java.time.LocalDate
import java.time.LocalDateTime
import org.example.tenist.errors.TenistError
import org.example.tenist.models.Dexterity
import org.example.tenist.models.Tenist
import org.example.tenist.service.storage.json.TenistStorageJsonImpl

class TenistStorageJsonImplTest {

    private val file = Files.createTempFile("", ".json").toFile()
    private val tenistStorageJson = TenistStorageJsonImpl()

    @AfterEach
    fun tearDown() {
        Files.deleteIfExists(file.toPath())
    }

    @BeforeEach
    fun setUp() {
        file.writeText("""
            [
                {
                    "id": 1,
                    "name": "Novak Djokovic",
                    "country": "Serbia",
                    "height": 188.0,
                    "weight": 77,
                    "points": 12030,
                    "dominantHand": "DIESTRO",
                    "birthDate": "1987-05-22",
                    "createdAt": "2024-09-23T10:00:00",
                    "updatedAt": "2024-09-23T10:00:00"
                },
                {
                    "id": 2,
                    "name": "Daniil Medvedev",
                    "country": "Rusia",
                    "height": 198.0,
                    "weight": 83,
                    "points": 10370,
                    "dominantHand": "DIESTRO",
                    "birthDate": "1996-02-11",
                    "createdAt": "2024-09-23T10:00:00",
                    "updatedAt": "2024-09-23T10:00:00"
                }
            ]
        """.trimIndent())
    }

    @Test
    fun `import should return Ok when importing a list of tenists`() {
        // Act
        val result = tenistStorageJson.import(file)

        // Assert
        assertTrue(result.isOk)
        assertEquals(2, result.value.size)

        val novak = result.value.firstOrNull { it.name == "Novak Djokovic" }
        val daniil = result.value.firstOrNull { it.name == "Daniil Medvedev" }

        assertNotNull(novak)
        assertEquals("Serbia", novak?.country)
        assertEquals(77, novak?.weight)
        assertEquals(LocalDate.parse("1987-05-22"), novak?.birthDate)

        assertNotNull(daniil)
        assertEquals("Rusia", daniil?.country)
        assertEquals(83, daniil?.weight)
        assertEquals(LocalDate.parse("1996-02-11"), daniil?.birthDate)
    }

    @Test
    fun `import should return error if JSON is incorrectly formatted`() {
        // Arrange
        file.writeText("[{ \"id\": 1, \"name\": \"InvalidData\" }]")

        // Act
        val result = tenistStorageJson.import(file)

        // Assert
        assertTrue(result.isErr)
        assertTrue(result.error is TenistError.ImportError)
        assertTrue(result.error.msg.contains("No se ha podido exportar"))
    }

    @Test
    fun `import should return error if file is empty`() {
        // Arrange
        file.writeText("")

        // Act
        val result = tenistStorageJson.import(file)

        // Assert
        assertTrue(result.isErr)
        assertTrue(result.error is TenistError.ImportError)
        assertTrue(result.error.msg.contains("No se ha podido exportar"))
    }

    @Test
    fun `import should return error if file does not exist`() {
        // Arrange
        val nonExistentFile = File("non_existent.json")

        // Act
        val result = tenistStorageJson.import(nonExistentFile)

        // Assert
        assertTrue(result.isErr)
        assertTrue(result.error is TenistError.ImportError)
        assertTrue(result.error.msg.contains("No se ha podido exportar"))
    }

    @Test
    fun `export should return Ok when exporting a list of tenists`() {
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
            )
        )

        // Act
        val result = tenistStorageJson.export(file, tenists)

        // Assert
        assertTrue(result.isOk)
        val exportedContent = file.readText()
        assertTrue(exportedContent.contains("Novak Djokovic"))
        assertTrue(exportedContent.contains("Serbia"))
        assertTrue(exportedContent.contains("1987-05-22"))
    }

    @Test
    fun `export should return error if file cannot be written`() {
        // Arrange
        val tenists = emptyList<Tenist>()
        file.setReadOnly()

        // Act
        val result = tenistStorageJson.export(file, tenists)

        file.setWritable(true) // To clean up the file afterward
        // Assert
        assertTrue(result.isErr)
        assertTrue(result.error is TenistError.ExportError)
        assertTrue(result.error.msg.contains("No se ha podido exportar el fichero JSON"))
    }
}
