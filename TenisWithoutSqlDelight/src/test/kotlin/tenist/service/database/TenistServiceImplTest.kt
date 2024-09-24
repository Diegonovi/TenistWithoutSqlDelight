package tenist.service.database

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import org.example.cache.Cache
import org.example.tenist.errors.TenistError
import org.example.tenist.models.Dexterity
import org.example.tenist.models.Tenist
import org.example.tenist.repository.TenistRepository
import org.example.tenist.service.database.TenistServiceImpl
import org.example.tenist.validator.TenistValidator
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.kotlin.whenever
import org.junit.jupiter.api.BeforeEach
import org.mockito.kotlin.verify
import org.mockito.quality.Strictness
import java.time.LocalDate
import kotlin.test.Test

@ExtendWith(MockitoExtension::class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TenistServiceImplTest {

    @Mock
    private lateinit var tenistRepository: TenistRepository

    @Mock
    private lateinit var tenistValidator: TenistValidator

    @Mock
    private lateinit var cache: Cache<Int, Tenist>

    private lateinit var tenistService: TenistServiceImpl

    @BeforeEach
    fun setUp() {
        tenistService = TenistServiceImpl(cache, tenistRepository, tenistValidator)
    }

    private fun createTestTenist(): Tenist {
        return Tenist(
            name = "Test Player",
            country = "Testland",
            weight = 80,
            height = 1.85,
            dominantHand = Dexterity.DIESTRO,
            points = 5000,
            birthDate = LocalDate.of(1990, 1, 1)
        )
    }

    @Test
    fun `save should return TenistAlreadyExistsError if tenist exists in cache`() {
        // Arrange
        val tenist = createTestTenist()
        whenever(tenistValidator.validate(tenist)).thenReturn(Ok(tenist))
        whenever(cache.get(tenist.id)).thenReturn(tenist)

        // Act
        val result = tenistService.save(tenist)

        // Assert
        assertTrue(result.isErr)
        assertEquals(TenistError.TenistAlreadyExists::class, result.error::class)
    }

    @Test
    fun `save should return TenistAlreadyExistsError if tenist exists in repository`() {
        // Arrange
        val tenist = createTestTenist()
        whenever(tenistValidator.validate(tenist)).thenReturn(Ok(tenist))
        whenever(cache.get(tenist.id)).thenReturn(null)
        whenever(tenistRepository.get(tenist.id)).thenReturn(tenist)

        // Act
        val result = tenistService.save(tenist)

        // Assert
        assertTrue(result.isErr)
        assertEquals(TenistError.TenistAlreadyExists::class, result.error::class)
    }

    @Test
    fun `save should return InvalidTenistError if tenist is not valid`() {
        // Arrange
        val tenist = createTestTenist()
        whenever(tenistValidator.validate(tenist)).thenReturn(Err(TenistError.InvalidTenist("Invalid")))

        // Act
        val result = tenistService.save(tenist)

        // Assert
        assertTrue(result.isErr)
        assertEquals(TenistError.InvalidTenist::class, result.error::class)
    }

    @Test
    fun `save should store tenist in cache and repository if valid`() {
        // Arrange
        val tenist = createTestTenist()
        whenever(tenistValidator.validate(tenist)).thenReturn(Ok(tenist))
        whenever(cache.get(tenist.id)).thenReturn(null)
        whenever(tenistRepository.get(tenist.id)).thenReturn(null)
        whenever(tenistRepository.create(tenist)).thenReturn(tenist)

        // Act
        val result = tenistService.save(tenist)

        // Assert
        assertTrue(result.isOk)
        verify(cache).put(tenist.id, tenist)
        verify(tenistRepository).create(tenist)
    }

    @Test
    fun `findAll should return all tenists from repository`() {
        // Arrange
        val tenists = listOf(createTestTenist())
        whenever(tenistRepository.getAll()).thenReturn(tenists)

        // Act
        val result = tenistService.findAll()

        // Assert
        assertTrue(result.isOk)
        assertEquals(tenists, result.value)
        verify(tenistRepository).getAll()
    }

    @Test
    fun `findById should return tenist from cache if present`() {
        // Arrange
        val tenist = createTestTenist()
        whenever(cache.get(tenist.id)).thenReturn(tenist)

        // Act
        val result = tenistService.findById(tenist.id)

        // Assert
        assertTrue(result.isOk)
        assertEquals(tenist, result.value)
        verify(cache).get(tenist.id)
    }

    @Test
    fun `findById should return tenist from repository if not in cache`() {
        // Arrange
        val tenist = createTestTenist()
        whenever(cache.get(tenist.id)).thenReturn(null)
        whenever(tenistRepository.get(tenist.id)).thenReturn(tenist)

        // Act
        val result = tenistService.findById(tenist.id)

        // Assert
        assertTrue(result.isOk)
        assertEquals(tenist, result.value)
        verify(cache).put(tenist.id, tenist)
    }

    @Test
    fun `findById should return TenistDoesNotExist if tenist does not exist`() {
        // Arrange
        val tenistId = 1
        whenever(cache.get(tenistId)).thenReturn(null)
        whenever(tenistRepository.get(tenistId)).thenReturn(null)

        // Act
        val result = tenistService.findById(tenistId)

        // Assert
        assertTrue(result.isErr)
        assertEquals(TenistError.TenistDoesNotExist::class, result.error::class)
    }

    @Test
    fun `delete should remove tenist from cache and repository if exists`() {
        // Arrange
        val tenist = createTestTenist()
        whenever(tenistRepository.get(tenist.id)).thenReturn(tenist)
        whenever(tenistRepository.delete(tenist.id)).thenReturn(tenist)

        // Act
        val result = tenistService.delete(tenist)

        // Assert
        assertTrue(result.isOk)
        verify(cache).remove(tenist.id)
        verify(tenistRepository).delete(tenist.id)
    }

    @Test
    fun `delete should return TenistDoesNotExist if tenist does not exist`() {
        // Arrange
        val tenist = createTestTenist()
        whenever(tenistRepository.get(tenist.id)).thenReturn(null)

        // Act
        val result = tenistService.delete(tenist)

        // Assert
        assertTrue(result.isErr)
        assertEquals(TenistError.TenistDoesNotExist::class, result.error::class)
    }

    @Test
    fun `update should update tenist in cache and repository if valid`() {
        // Arrange
        val tenist = createTestTenist()
        whenever(tenistValidator.validate(tenist)).thenReturn(Ok(tenist))
        whenever(tenistRepository.get(tenist.id)).thenReturn(tenist)
        whenever(tenistRepository.update(tenist)).thenReturn(tenist)

        // Act
        val result = tenistService.update(tenist)

        // Assert
        assertTrue(result.isOk)
        assertEquals(tenist, result.value)
        verify(cache).remove(tenist.id)
        verify(tenistRepository).update(tenist)
    }

    @Test
    fun `update should return InvalidTenist if tenist is not valid`() {
        // Arrange
        val tenist = createTestTenist()
        whenever(tenistValidator.validate(tenist)).thenReturn(Err(TenistError.InvalidTenist("Invalid")))

        // Act
        val result = tenistService.update(tenist)

        // Assert
        assertTrue(result.isErr)
        assertEquals(TenistError.InvalidTenist::class, result.error::class)
    }

    @Test
    fun `update should return TenistDoesNotExist if tenist does not exist`() {
        // Arrange
        val tenist = createTestTenist()
        whenever(tenistRepository.get(tenist.id)).thenReturn(null)

        // Act
        val result = tenistService.update(tenist)

        // Assert
        assertTrue(result.isErr)
        assertEquals(TenistError.TenistDoesNotExist::class, result.error::class)
    }
}
