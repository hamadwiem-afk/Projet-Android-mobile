package com.example.myapplication.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull

/**
 * Les deux endpoints de l'API Coffee :
 * https://api.sampleapis.com/coffee/hot
 * https://api.sampleapis.com/coffee/iced
 */
enum class CoffeeType(val path: String, val label: String) {
    HOT("hot", "Café chaud"),
    ICED("iced", "Café froid");

    companion object {
        fun fromPath(path: String): CoffeeType =
            entries.firstOrNull { it.path == path } ?: HOT
    }
}

/**
 * Objet brut renvoyé par l'API.
 *
 * L'API est publique et contient quelques entrées de test mal formées
 * (id en texte, ingredients en String au lieu d'une liste...).
 * On lit donc id et ingredients en JsonElement puis on nettoie dans toCoffee().
 */
@Serializable
data class CoffeeDto(
    val id: JsonElement? = null,
    val title: String? = null,
    val description: String? = null,
    val image: String? = null,
    val ingredients: JsonElement? = null
)

/**
 * Modèle propre utilisé par l'application.
 * (@Serializable : on le sauvegarde tel quel dans les favoris et le panier.)
 */
@Serializable
data class Coffee(
    val id: String,
    val type: CoffeeType,
    val title: String,
    val description: String,
    val image: String?,
    val ingredients: List<String>
) {
    /** Clé unique : les ids ne sont uniques que dans un même type. */
    val key: String
        get() = "${type.path}-$id"
}

/**
 * Une ligne du panier.
 */
@Serializable
data class CartItem(
    val coffee: Coffee,
    val quantity: Int
)

/**
 * Convertit le DTO en Coffee, ou null si l'entrée est inutilisable.
 */
fun CoffeeDto.toCoffee(type: CoffeeType): Coffee? {

    val idValue = (id as? JsonPrimitive)
        ?.contentOrNull
        ?.toIntOrNull()
        ?: return null

    val name = title?.trim().orEmpty()

    if (
        name.isBlank() ||
        name.equals("test", ignoreCase = true) ||
        name.equals("string", ignoreCase = true)
    ) return null

    val ingredientList = (ingredients as? JsonArray)
        ?.mapNotNull { (it as? JsonPrimitive)?.contentOrNull?.trim() }
        ?.filter { it.isNotBlank() }
        .orEmpty()

    if (ingredientList.isEmpty()) return null

    return Coffee(
        id = idValue.toString(),
        type = type,
        title = name,
        description = description?.trim().orEmpty(),
        image = image?.takeIf { it.startsWith("http") },
        ingredients = ingredientList
    )
}

/**
 * Coordonnées saisies sur la page de commande.
 */
@Serializable
data class ContactInfo(
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val address: String = ""
)
