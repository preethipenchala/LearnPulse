package com.learnpulse.ui.util

import kotlin.math.pow
import kotlin.math.round

fun Double.toFixed(decimals: Int): String {
    require(decimals >= 0) { "decimals must be >= 0" }

    val factor = 10.0.pow(decimals)
    val rounded = round(this * factor) / factor

    if (decimals == 0) {
        return rounded.toLong().toString()
    }

    val parts = rounded.toString().split('.')
    val integerPart = parts[0]
    val fractionPart = parts.getOrElse(1) { "" }.padEnd(decimals, '0').take(decimals)

    return "$integerPart.$fractionPart"
}
