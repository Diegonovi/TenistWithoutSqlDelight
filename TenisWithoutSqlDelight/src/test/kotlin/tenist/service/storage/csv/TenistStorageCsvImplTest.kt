package tenist.service.storage.csv

import org.example.tenist.errors.TenistError
import org.example.tenist.models.Dexterity
import org.example.tenist.models.Tenist
import org.example.tenist.service.storage.csv.TenistStorageCsvImpl
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Files
import java.time.LocalDate
import java.time.LocalDateTime

class TenistStorageCsvImplTest {

    private val file = Files.createTempFile("", ".csv").toFile()
    private val tenistStorageCsv = TenistStorageCsvImpl()

    @AfterEach
    fun tearDown() {
        Files.deleteIfExists(file.toPath())
    }

    @BeforeEach
    fun setUp() {
        file.writeText(
            "id,nombre,pais,altura,peso,puntos,mano,fecha_nacimiento\n" +
                    "1,Novak Djokovic,Serbia,188,77,12030,DIESTRO,1987-05-22\n" +
                    "2,Daniil Medvedev,Rusia,198,83,10370,DIESTRO,1996-02-11\n" +
                    "3,Rafael Nadal,España,185,85,8270,ZURDO,1986-06-03"
        )
    }

    @Test
    fun `import should return Ok when importing a list of tenists`() {
        // Act
        val result = tenistStorageCsv.import(file)

        // Assert
        assertTrue(result.isOk)
        assertEquals(3, result.value.size)

        val novak = result.value.firstOrNull { it.name == "Novak Djokovic" }
        val rafa = result.value.firstOrNull { it.name == "Rafael Nadal" }

        assertNotNull(novak)
        assertEquals("Serbia", novak?.country)
        assertEquals(77, novak?.weight)
        assertEquals(LocalDate.parse("1987-05-22"), novak?.birthDate)

        assertNotNull(rafa)
        assertEquals("España", rafa?.country)
        assertEquals(85, rafa?.weight)
        assertEquals(LocalDate.parse("1986-06-03"), rafa?.birthDate)
    }

    @Test
    fun `import should return error if CSV is incorrectly formatted`() {
        // Arrange
        file.writeText("id,nombre,pais\n1,InvalidData")

        // Act
        val result = tenistStorageCsv.import(file)

        // Assert
        assertTrue(result.isErr)
        assertTrue(result.error is TenistError.ImportError)
        assertTrue(result.error.msg.contains("Error al importar los tenistas"))
    }

    @Test
    fun `import should return error if file is empty`() {
        // Arrange
        file.writeText("")

        // Act
        val result = tenistStorageCsv.import(file)

        // Assert
        assertTrue(result.isErr)
        assertTrue(result.error is TenistError.ImportError)
        assertTrue(result.error.msg.contains("Error al importar los tenistas"))
    }

    @Test
    fun `import should return error if file does not exist`() {
        // Arrange
        val nonExistentFile = File("non_existent.csv")

        // Act
        val result = tenistStorageCsv.import(nonExistentFile)

        // Assert
        assertTrue(result.isErr)
        assertTrue(result.error is TenistError.ImportError)
        assertTrue(result.error.msg.contains("Error al importar los tenistas"))
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
        val result = tenistStorageCsv.export(file, tenists)

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
        val result = tenistStorageCsv.export(file, tenists)

        file.setWritable(true) // Para que la pueda borrar una vez se acabe el test
        // Assert
        assertTrue(result.isErr)
        assertTrue(result.error is TenistError.ExportError)
        assertTrue(result.error.msg.contains("Error al exportar los tenistas"))
    }
}
