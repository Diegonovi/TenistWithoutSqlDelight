package tenist.validator

import org.example.tenist.errors.TenistError
import org.example.tenist.models.Dexterity
import org.example.tenist.models.Tenist
import org.example.tenist.validator.TenisValidatorImpl
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate

class TenistValidatorTest {

    private val tenistValidator = TenisValidatorImpl()

    private fun createTestTenist(
        name: String = "Valid Name",
        country: String = "Valid Country",
        weight: Int = 80,
        height: Double = 1.85,
    ): Tenist {
        return Tenist(
            name = name,
            country = country,
            weight = weight,
            height = height,
            dominantHand = Dexterity.DIESTRO,
            points = 5000,
            birthDate = LocalDate.of(1990, 1, 1)
        )
    }

    @Test
    fun `validate should return error when name is empty`() {
        // Arrange
        val tenist = createTestTenist(name = "")

        // Act
        val result = tenistValidator.validate(tenist)

        // Assert
        assertTrue(result.isErr)
        assertEquals(TenistError.InvalidTenist("El nombre no puede estar vacío.")::class, result.error::class)
    }

    @Test
    fun `validate should return error when country is empty`() {
        // Arrange
        val tenist = createTestTenist(country = "")

        // Act
        val result = tenistValidator.validate(tenist)

        // Assert
        assertTrue(result.isErr)
        assertEquals(TenistError.InvalidTenist("No puede tener el pais vacío")::class, result.error::class)
    }

    @Test
    fun `validate should return error when weight is less than 0`() {
        // Arrange
        val tenist = createTestTenist(weight = -1)

        // Act
        val result = tenistValidator.validate(tenist)

        // Assert
        assertTrue(result.isErr)
        assertEquals(TenistError.InvalidTenist("El peso del tenista Valid Name no es correcta.")::class, result.error::class)
    }

    @Test
    fun `validate should return error when weight is more than 400`() {
        // Arrange
        val tenist = createTestTenist(weight = 401)

        // Act
        val result = tenistValidator.validate(tenist)

        // Assert
        assertTrue(result.isErr)
        assertEquals(TenistError.InvalidTenist("El peso del tenista Valid Name no es correcta.")::class, result.error::class)
    }

    @Test
    fun `validate should return error when height is less than 0`() {
        // Arrange
        val tenist = createTestTenist(height = -1.0)

        // Act
        val result = tenistValidator.validate(tenist)

        // Assert
        assertTrue(result.isErr)
        assertEquals(TenistError.InvalidTenist("La altura del tenista es incorrecta")::class, result.error::class)
    }

    @Test
    fun `validate should return error when height is more than 400`() {
        // Arrange
        val tenist = createTestTenist(height = 401.0)

        // Act
        val result = tenistValidator.validate(tenist)

        // Assert
        assertTrue(result.isErr)
        assertEquals(TenistError.InvalidTenist("La altura del tenista es incorrecta")::class, result.error::class)
    }

    @Test
    fun `validate should return Ok when tenist is valid`() {
        // Arrange
        val tenist = createTestTenist()

        // Act
        val result = tenistValidator.validate(tenist)

        // Assert
        assertTrue(result.isOk)
        assertEquals(tenist, result.value)
    }
}
