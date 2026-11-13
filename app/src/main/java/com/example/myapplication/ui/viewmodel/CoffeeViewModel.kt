package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.local.LocalStore
import com.example.myapplication.data.model.CartItem
import com.example.myapplication.data.model.Coffee
import com.example.myapplication.data.model.CoffeeType
import com.example.myapplication.data.model.ContactInfo
import com.example.myapplication.data.repository.CoffeeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CoffeeViewModel : ViewModel() {

    private val repository = CoffeeRepository()

    // =====================================================
    // TOUS LES CAFÉS
    // =====================================================

    private val _allCoffees =
        MutableStateFlow<List<Coffee>>(emptyList())

    private val _coffees =
        MutableStateFlow<List<Coffee>>(emptyList())

    val coffees: StateFlow<List<Coffee>> =
        _coffees.asStateFlow()

    // =====================================================
    // FILTRES (type de café + texte de recherche)
    // =====================================================

    private val _typeFilter =
        MutableStateFlow<CoffeeType?>(null)

    val typeFilter: StateFlow<CoffeeType?> =
        _typeFilter.asStateFlow()

    private var searchQuery = ""

    // =====================================================
    // CAFÉ SÉLECTIONNÉ
    // =====================================================

    private val _selectedCoffee =
        MutableStateFlow<Coffee?>(null)

    val selectedCoffee: StateFlow<Coffee?> =
        _selectedCoffee.asStateFlow()

    // =====================================================
    // CHARGEMENT / ERREUR
    // =====================================================

    private val _isLoading =
        MutableStateFlow(false)

    val isLoading: StateFlow<Boolean> =
        _isLoading.asStateFlow()

    private val _error =
        MutableStateFlow<String?>(null)

    val error: StateFlow<String?> =
        _error.asStateFlow()

    // =====================================================
    // FAVORIS + PANIER (partagés entre tous les écrans)
    // =====================================================

    val favorites: StateFlow<List<Coffee>> =
        LocalStore.favorites

    val cart: StateFlow<List<CartItem>> =
        LocalStore.cart

    fun toggleFavorite(coffee: Coffee) =
        LocalStore.toggleFavorite(coffee)

    fun addToCart(coffee: Coffee, quantity: Int = 1) =
        LocalStore.addToCart(coffee, quantity)

    fun changeQuantity(key: String, delta: Int) =
        LocalStore.changeQuantity(key, delta)

    fun removeFromCart(key: String) =
        LocalStore.removeFromCart(key)

    fun clearCart() =
        LocalStore.clearCart()

    // =====================================================
    // COMMANDE
    // =====================================================

    val contact: StateFlow<ContactInfo> =
        LocalStore.contact

    /**
     * Valide la commande : enregistre les coordonnées
     * (pour la prochaine fois) puis vide le panier.
     */
    fun placeOrder(info: ContactInfo) {

        LocalStore.saveContact(info)

        LocalStore.clearCart()
    }

    // =====================================================
    // CHARGER TOUS LES CAFÉS
    // =====================================================

    fun loadAllCoffees() {

        if (_allCoffees.value.isNotEmpty()) {

            applyFilters()

            return
        }

        viewModelScope.launch {

            _isLoading.value = true
            _error.value = null

            try {

                val result =
                    repository.getAllCoffees()

                if (result.isNotEmpty()) {

                    _allCoffees.value = result
                    applyFilters()

                } else {

                    _error.value =
                        "Aucun café trouvé."
                }

            } catch (e: Exception) {

                _error.value =
                    "Impossible de charger les cafés."

            } finally {

                _isLoading.value = false
            }
        }
    }

    // =====================================================
    // RECHERCHE (locale : l'API n'a pas d'endpoint de recherche)
    // =====================================================

    fun searchCoffees(
        name: String
    ) {

        searchQuery = name.trim()

        applyFilters()
    }

    // =====================================================
    // FILTRE PAR TYPE (null = tous, HOT, ICED)
    // =====================================================

    fun selectTypeFilter(
        type: CoffeeType?
    ) {

        _typeFilter.value = type

        applyFilters()
    }

    private fun applyFilters() {

        _coffees.value =
            _allCoffees.value.filter { coffee ->

                val matchesType =
                    _typeFilter.value == null ||
                            coffee.type == _typeFilter.value

                val matchesText =
                    searchQuery.isBlank() ||
                            coffee.title.contains(
                                searchQuery,
                                ignoreCase = true
                            )

                matchesType && matchesText
            }
    }

    // =====================================================
    // CHARGER UN CAFÉ PAR TYPE + ID
    // =====================================================

    fun loadCoffee(
        typePath: String,
        id: String
    ) {

        viewModelScope.launch {

            _isLoading.value = true
            _error.value = null
            _selectedCoffee.value = null

            try {

                val result =
                    repository.getCoffee(
                        CoffeeType.fromPath(typePath),
                        id
                    )

                if (result != null) {

                    _selectedCoffee.value = result

                } else {

                    _error.value =
                        "Café introuvable."
                }

            } catch (e: Exception) {

                _error.value =
                    "Impossible de charger le café."

            } finally {

                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
