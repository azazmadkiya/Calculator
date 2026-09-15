package com.example.math

import kotlin.math.*

class ExpressionParser {
    fun evaluate(expression: String, isRadians: Boolean = true): Double {
        return object : Any() {
            var pos = -1
            var ch = 0

            fun nextChar() {
                ch = if (++pos < expression.length) expression[pos].code else -1
            }

            fun eat(charToEat: Int): Boolean {
                while (ch == ' '.code) nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < expression.length) throw RuntimeException("Unexpected: " + ch.toChar())
                return x
            }

            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    when {
                        eat('+'.code) -> x += parseTerm() // addition
                        eat('-'.code) -> x -= parseTerm() // subtraction
                        else -> return x
                    }
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    when {
                        eat('*'.code) -> x *= parseFactor() // multiplication
                        eat('/'.code) -> x /= parseFactor() // division
                        eat('%'.code) -> x %= parseFactor() // modulo
                        else -> return x
                    }
                }
            }

            fun parseFactor(): Double {
                if (eat('+'.code)) return parseFactor() // unary plus
                if (eat('-'.code)) return -parseFactor() // unary minus

                var x: Double
                val startPos = this.pos
                if (eat('('.code)) { // parentheses
                    x = parseExpression()
                    eat(')'.code)
                } else if ((ch >= '0'.code && ch <= '9'.code) || ch == '.'.code) { // numbers
                    while ((ch >= '0'.code && ch <= '9'.code) || ch == '.'.code) nextChar()
                    x = expression.substring(startPos, this.pos).toDouble()
                } else if (ch >= 'a'.code && ch <= 'z'.code || ch == 'π'.code || ch == 'e'.code) { // functions
                    while (ch >= 'a'.code && ch <= 'z'.code || ch == 'π'.code || ch == 'e'.code) nextChar()
                    val func = expression.substring(startPos, this.pos)
                    x = if (func == "π") PI
                        else if (func == "e") E
                        else parseFactor()
                    
                    val factor = if (isRadians) 1.0 else (PI / 180.0)

                    x = when (func) {
                        "sqrt", "√" -> sqrt(x)
                        "sin" -> sin(x * factor)
                        "cos" -> cos(x * factor)
                        "tan" -> tan(x * factor)
                        "ln" -> ln(x)
                        "log" -> log10(x)
                        "π" -> PI
                        "e" -> E
                        else -> throw RuntimeException("Unknown function: $func")
                    }
                } else {
                    throw RuntimeException("Unexpected: " + ch.toChar())
                }

                if (eat('^'.code)) x = x.pow(parseFactor()) // exponentiation

                return x
            }
        }.parse()
    }
}
