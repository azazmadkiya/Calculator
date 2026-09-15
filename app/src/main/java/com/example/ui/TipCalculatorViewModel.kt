package com.example.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TipCalculatorViewModel : ViewModel() {
    private val _subtotal = MutableStateFlow("0")
    val subtotal: StateFlow<String> = _subtotal.asStateFlow()

    private val _tipPercent = MutableStateFlow(15)
    val tipPercent: StateFlow<Int> = _tipPercent.asStateFlow()

    private val _people = MutableStateFlow(5)
    val people: StateFlow<Int> = _people.asStateFlow()

    private val _isEnteringSubtotal = MutableStateFlow(true)
    val isEnteringSubtotal: StateFlow<Boolean> = _isEnteringSubtotal.asStateFlow()

    fun setEnteringSubtotal(isSubtotal: Boolean) {
        _isEnteringSubtotal.value = isSubtotal
    }

    fun onKeyPadPress(key: String) {
        if (_isEnteringSubtotal.value) {
            _subtotal.update { current ->
                if (key == "C") "0"
                else if (key == "DEL") if (current.length > 1) current.dropLast(1) else "0"
                else if (key == ".") if (current.contains(".")) current else current + "."
                else if (current == "0") key else current + key
            }
        }
    }

    fun updateTipPercent(percent: Int) {
        _tipPercent.value = percent
    }

    fun updatePeople(people: Int) {
        _people.value = people
    }
}
