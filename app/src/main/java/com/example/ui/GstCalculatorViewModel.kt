package com.example.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GstCalculatorViewModel : ViewModel() {
    private val _amount = MutableStateFlow("0")
    val amount: StateFlow<String> = _amount.asStateFlow()

    private val _gstPercent = MutableStateFlow(18)
    val gstPercent: StateFlow<Int> = _gstPercent.asStateFlow()

    private val _isAddingGst = MutableStateFlow(true)
    val isAddingGst: StateFlow<Boolean> = _isAddingGst.asStateFlow()

    fun onKeyPadPress(key: String) {
        _amount.update { current ->
            if (key == "C") "0"
            else if (key == "DEL") if (current.length > 1) current.dropLast(1) else "0"
            else if (key == ".") if (current.contains(".")) current else current + "."
            else if (current == "0") key else current + key
        }
    }

    fun updateGstPercent(percent: Int) {
        _gstPercent.value = percent
    }

    fun setIsAddingGst(isAdding: Boolean) {
        _isAddingGst.value = isAdding
    }
}
