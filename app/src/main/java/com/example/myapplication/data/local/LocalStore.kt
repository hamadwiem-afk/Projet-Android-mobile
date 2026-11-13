package com.example.myapplication.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.myapplication.data.model.CartItem
import com.example.myapplication.data.model.Coffee
import com.example.myapplication.data.model.ContactInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

/**
 * Favoris + panier, partagés par tous les écrans
 * et sauvegardés dans SharedPreferences (JSON).
 * À initialiser une fois dans MainActivity : LocalStore.init(this)
 */
object LocalStore {

    private const val PREFS_NAME = "coffee_store"
    private const val KEY_FAVORITES = "favorites"
    private const val KEY_CART = "cart"
    private const val KEY_CONTACT = "contact"

    private var prefs: SharedPreferences? = null

    private val json = Json { ignoreUnknownKeys = true }

    private val coffeeListSerializer =
        ListSerializer(Coffee.serializer())

    private val cartListSerializer =
        ListSerializer(CartItem.serializer())

    // =====================================================
    // ÉTATS
    // =====================================================

    private val _favorites =
        MutableStateFlow<List<Coffee>>(emptyList())

    val favorites: StateFlow<List<Coffee>> =
        _favorites.asStateFlow()

    private val _cart =
        MutableStateFlow<List<CartItem>>(emptyList())

    val cart: StateFlow<List<CartItem>> =
        _cart.asStateFlow()

    private val _contact =
        MutableStateFlow(ContactInfo())

    /** Dernières coordonnées saisies (pour pré-remplir la commande). */
    val contact: StateFlow<ContactInfo> =
        _contact.asStateFlow()

    // =====================================================
    // INITIALISATION
    // =====================================================

    fun init(context: Context) {

        if (prefs != null) return

        val p = context.applicationContext
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        prefs = p

        _favorites.value = load(p, KEY_FAVORITES, coffeeListSerializer)
        _cart.value = load(p, KEY_CART, cartListSerializer)

        p.getString(KEY_CONTACT, null)?.let { raw ->
            try {
                _contact.value =
                    json.decodeFromString(ContactInfo.serializer(), raw)
            } catch (e: Exception) {
                // coordonnées illisibles : on repart de zéro
            }
        }
    }

    // =====================================================
    // FAVORIS
    // =====================================================

    fun toggleFavorite(coffee: Coffee) {

        _favorites.update { list ->

            if (list.any { it.key == coffee.key }) {
                list.filterNot { it.key == coffee.key }
            } else {
                list + coffee
            }
        }

        save(KEY_FAVORITES, coffeeListSerializer, _favorites.value)
    }

    // =====================================================
    // PANIER
    // =====================================================

    fun addToCart(coffee: Coffee, quantity: Int = 1) {

        if (quantity <= 0) return

        _cart.update { list ->

            val existing = list.firstOrNull {
                it.coffee.key == coffee.key
            }

            if (existing == null) {

                list + CartItem(coffee, quantity)

            } else {

                list.map {
                    if (it.coffee.key == coffee.key) {
                        it.copy(quantity = it.quantity + quantity)
                    } else {
                        it
                    }
                }
            }
        }

        saveCart()
    }

    fun changeQuantity(key: String, delta: Int) {

        _cart.update { list ->

            list.mapNotNull { item ->

                if (item.coffee.key != key) {

                    item

                } else {

                    val newQuantity = item.quantity + delta

                    if (newQuantity <= 0) null
                    else item.copy(quantity = newQuantity)
                }
            }
        }

        saveCart()
    }

    fun removeFromCart(key: String) {

        _cart.update { list ->
            list.filterNot { it.coffee.key == key }
        }

        saveCart()
    }

    fun clearCart() {

        _cart.value = emptyList()

        saveCart()
    }

    // =====================================================
    // COORDONNÉES
    // =====================================================

    fun saveContact(info: ContactInfo) {

        _contact.value = info

        prefs?.edit()
            ?.putString(
                KEY_CONTACT,
                json.encodeToString(ContactInfo.serializer(), info)
            )
            ?.apply()
    }

    // =====================================================
    // OUTILS
    // =====================================================

    private fun saveCart() {
        save(KEY_CART, cartListSerializer, _cart.value)
    }

    private fun <T> save(
        key: String,
        serializer: KSerializer<List<T>>,
        value: List<T>
    ) {
        prefs?.edit()
            ?.putString(key, json.encodeToString(serializer, value))
            ?.apply()
    }

    private fun <T> load(
        p: SharedPreferences,
        key: String,
        serializer: KSerializer<List<T>>
    ): List<T> {

        val raw = p.getString(key, null) ?: return emptyList()

        return try {
            json.decodeFromString(serializer, raw)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
