package com.example.myapplication.data.repository

import com.example.myapplication.data.model.Coffee
import com.example.myapplication.data.model.CoffeeType
import com.example.myapplication.data.model.toCoffee
import com.example.myapplication.data.remote.CoffeeApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class CoffeeRepository {

    /**
     * Récupère les cafés chauds + froids.
     * Les deux appels partent en parallèle.
     * Si un seul des deux échoue, on garde l'autre.
     */
    suspend fun getAllCoffees(): List<Coffee> = coroutineScope {

        val results = CoffeeType.entries
            .map { type -> async { runCatching { fetch(type) } } }
            .awaitAll()

        if (results.all { it.isFailure }) {
            throw results.first().exceptionOrNull()
                ?: Exception("Erreur réseau")
        }

        results.mapNotNull { it.getOrNull() }.flatten().distinctBy { it.key }
    }


    /**
     * Récupère un café à partir de son type et de son id.
     * (Les ids ne sont uniques que dans un même type :
     * hot/2 et iced/2 sont deux cafés différents.)
     */
    suspend fun getCoffee(
        type: CoffeeType,
        id: String
    ): Coffee? {

        return try {

            fetch(type).firstOrNull { it.id == id }

        } catch (e: Exception) {

            null
        }
    }


    private suspend fun fetch(
        type: CoffeeType
    ): List<Coffee> {

        return CoffeeApi
            .getCoffees(type)
            .mapNotNull { it.toCoffee(type) }
    }
}
