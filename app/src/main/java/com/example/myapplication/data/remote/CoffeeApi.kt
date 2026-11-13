package com.example.myapplication.data.remote

import com.example.myapplication.data.model.CoffeeDto
import com.example.myapplication.data.model.CoffeeType
import io.ktor.client.call.body
import io.ktor.client.request.get

object CoffeeApi {

    private const val BASE_URL =
        "https://api.sampleapis.com/coffee"

    /**
     * Récupère tous les cafés d'un type.
     *
     * Exemples :
     * GET /coffee/hot
     * GET /coffee/iced
     */
    suspend fun getCoffees(
        type: CoffeeType
    ): List<CoffeeDto> {

        return KtorClient.client
            .get("$BASE_URL/${type.path}")
            .body()
    }
}
