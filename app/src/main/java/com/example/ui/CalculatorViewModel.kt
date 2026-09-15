package com.example.ui

import androidx.lifecycle.ViewModel
import com.example.math.ExpressionParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CalculatorViewModel : ViewModel() {
    private val _expression = MutableStateFlow("")
    val expression: StateFlow<String> = _expression.asStateFlow()

    private val _result = MutableStateFlow("")
    val result: StateFlow<String> = _result.asStateFlow()

    private val _isRadians = MutableStateFlow(true)
    val isRadians: StateFlow<Boolean> = _isRadians.asStateFlow()
    
    private val _history = MutableStateFlow<List<HistoryItem>>(emptyList())
    val history: StateFlow<List<HistoryItem>> = _history.asStateFlow()

    private val parser = ExpressionParser()

    fun onAction(action: CalculatorAction) {
        when (action) {
            is CalculatorAction.Number -> append(action.number.toString())
            is CalculatorAction.Operator -> append(action.operator)
            is CalculatorAction.Clear -> clear()
            is CalculatorAction.Delete -> delete()
            is CalculatorAction.Calculate -> calculate()
            is CalculatorAction.Function -> append(action.function + "(")
            is CalculatorAction.Constant -> append(action.constant)
            is CalculatorAction.ToggleRadDeg -> _isRadians.update { !it }
            is CalculatorAction.Append -> append(action.text)
        }
    }

    private fun append(str: String) {
        _expression.update { it + str }
        tryCalculateLive()
    }

    private fun clear() {
        _expression.value = ""
        _result.value = ""
    }

    private fun delete() {
        _expression.update { if (it.isNotEmpty()) it.dropLast(1) else "" }
        if (_expression.value.isEmpty()) {
            _result.value = ""
        } else {
            tryCalculateLive()
        }
    }

    private fun calculate() {
        try {
            val res = evaluateExpression(_expression.value)
            val resStr = formatResult(res)
            
            val currentExpr = _expression.value
            if (currentExpr.isNotEmpty() && currentExpr != resStr) {
                _history.update { currentList ->
                    val newList = mutableListOf(HistoryItem(currentExpr, resStr))
                    newList.addAll(currentList)
                    newList.take(10)
                }
            }
            
            _expression.value = resStr
            _result.value = ""
        } catch (e: Exception) {
            // Keep current result or show error
            _result.value = "Error"
        }
    }

    private fun tryCalculateLive() {
        try {
            val res = evaluateExpression(_expression.value)
            _result.value = formatResult(res)
        } catch (e: Exception) {
            // Do not update result if expression is incomplete
        }
    }

    private fun evaluateExpression(expr: String): Double {
        val sanitized = expr.replace("×", "*")
                            .replace("÷", "/")
                            .replace("−", "-")
        return parser.evaluate(sanitized, _isRadians.value)
    }

    private fun formatResult(res: Double): String {
        return if (res % 1.0 == 0.0) {
            res.toLong().toString()
        } else {
            res.toString()
        }
    }
}

data class HistoryItem(val expression: String, val result: String)

sealed class CalculatorAction {
    data class Number(val number: Int) : CalculatorAction()
    data class Operator(val operator: String) : CalculatorAction()
    object Clear : CalculatorAction()
    object Delete : CalculatorAction()
    object Calculate : CalculatorAction()
    data class Function(val function: String) : CalculatorAction()
    data class Constant(val constant: String) : CalculatorAction()
    object ToggleRadDeg : CalculatorAction()
    data class Append(val text: String) : CalculatorAction()
}
