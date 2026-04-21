package com.accommodation.ui.filter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accommodation.data.database.entities.UserPreferences
import com.accommodation.data.repository.PreferencesRepository
import com.accommodation.domain.FilterParams
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FilterViewModel(private val prefsRepo: PreferencesRepository) : ViewModel() {
    private val _params = MutableStateFlow(FilterParams())
    val params: StateFlow<FilterParams> = _params

    fun update(params: FilterParams) { _params.value = params }

    fun savePreferences(userId: Int) = viewModelScope.launch {
        val p = _params.value
        prefsRepo.save(UserPreferences(
            userId = userId,
            minPrice = p.minPrice,
            maxPrice = p.maxPrice,
            location = p.location,
            availabilityDate = p.date
        ))
    }

    fun clear() { _params.value = FilterParams() }
}
