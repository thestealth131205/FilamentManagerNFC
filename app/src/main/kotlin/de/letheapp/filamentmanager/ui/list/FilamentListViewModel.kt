package de.letheapp.filamentmanager.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.letheapp.filamentmanager.data.db.FilamentDao
import de.letheapp.filamentmanager.data.db.FilamentEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FilamentListViewModel(private val dao: FilamentDao) : ViewModel() {

    val searchQuery = MutableStateFlow("")

    val filtered: StateFlow<List<FilamentEntity>> =
        combine(dao.observeAll(), searchQuery) { all, query ->
            if (query.isBlank()) {
                all
            } else {
                all.filter {
                    it.brand.contains(query, ignoreCase = true) ||
                        it.type.contains(query, ignoreCase = true) ||
                        (it.colorName?.contains(query, ignoreCase = true) == true) ||
                        (it.sku?.contains(query, ignoreCase = true) == true)
                }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun onSearchChanged(query: String) {
        searchQuery.value = query
    }

    fun delete(filament: FilamentEntity) {
        viewModelScope.launch { dao.delete(filament) }
    }
}
