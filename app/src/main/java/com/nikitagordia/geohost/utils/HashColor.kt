package com.nikitagordia.geohost.utils

/**
 * Created by nikitagordia on 6/13/18.
 */

object HashColor {

    private val HASH_KEY = 83L
    private val MOD: Long = 1000_000_000 + 7

    fun hash(s: String): Float {
        var res = 0L
        var power = 1L
        for (i in s) {
            power = (power * HASH_KEY) % MOD
            res = (((i.toInt() * power) % MOD + res) % MOD)
        }
        return (res % 360).toFloat()
    }
}