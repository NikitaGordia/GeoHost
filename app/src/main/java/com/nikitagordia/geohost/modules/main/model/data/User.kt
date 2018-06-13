package com.nikitagordia.geohost.modules.main.model.data

/**
 * Created by nikitagordia on 6/12/18.
 */

data class User(val key: String, var name: String) {

    var position: Position? = null

    fun update(u: User) {
        name = u.name
        if (u.position != null) position = u.position
    }

    override fun equals(other: Any?): Boolean {
        if (other !is User) return false
        return other.key == key
    }
}

class Position(val lon: Double, val lat: Double)