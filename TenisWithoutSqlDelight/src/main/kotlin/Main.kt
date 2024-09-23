package org.example

import org.example.config.AppConfig
import org.example.database.DatabaseManager
import org.example.tenist.models.Dexterity
import org.example.tenist.models.Tenist
import org.example.tenist.repository.TenistRepositoryImpl
import org.koin.core.annotation.KoinExperimentalAPI
import java.time.LocalDate

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
@OptIn(KoinExperimentalAPI::class)
fun main() {
    val repo = TenistRepositoryImpl(
        DatabaseManager(AppConfig())
    )

    val tenists = listOf(
        Tenist(
            name = "Rafael Nadal",
            country = "Spain",
            weight = 85,
            height = 1.85,
            dominantHand = Dexterity.LEFTHANDED,
            points = 12000,
            birthDate = LocalDate.of(1986, 6, 3)
        ),
        Tenist(
            name = "Roger Federer",
            country = "Switzerland",
            weight = 85,
            height = 1.85,
            dominantHand = Dexterity.RIGHTHANDED,
            points = 11000,
            birthDate = LocalDate.of(1981, 8, 8)
        ),
        Tenist(
            name = "Novak Djokovic",
            country = "Serbia",
            weight = 77,
            height = 1.88,
            dominantHand = Dexterity.RIGHTHANDED,
            points = 14000,
            birthDate = LocalDate.of(1987, 5, 22)
        ),
        Tenist(
            name = "Serena Williams",
            country = "USA",
            weight = 70,
            height = 1.75,
            dominantHand = Dexterity.RIGHTHANDED,
            points = 8000,
            birthDate = LocalDate.of(1981, 9, 26)
        ),
        Tenist(
            name = "Simona Halep",
            country = "Romania",
            weight = 60,
            height = 1.68,
            dominantHand = Dexterity.RIGHTHANDED,
            points = 7000,
            birthDate = LocalDate.of(1991, 9, 27)
        )
    )

    tenists.forEach {
        repo.create(it)
        println("${it.name} saved successfully")
    }

}