package com.example.papb.ui.theme

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import android.content.Context

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ThemeViewModel(application: Application) : AndroidViewModel(application) {
    private val dataStore = application.dataStore
    private val themeKey = booleanPreferencesKey("theme_setting")

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode

    init {
        // Load saved theme preference in background
        viewModelScope.launch {
            dataStore.data.collect { preferences ->
                _isDarkMode.value = preferences[themeKey] ?: false
            }
        }
    }

    fun toggleTheme() {
        viewModelScope.launch {
            _isDarkMode.value = !_isDarkMode.value
            dataStore.edit { preferences ->
                preferences[themeKey] = _isDarkMode.value
            }
        }
    }
}
