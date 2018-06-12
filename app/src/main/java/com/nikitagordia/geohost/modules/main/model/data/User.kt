package com.nikitagordia.geohost.modules.main.model.data

/**
 * Created by nikitagordia on 6/12/18.
 */

data class User(val key: String, var name: String) {

    fun update(u: User) {
        name = u.name
    }

    override fun equals(other: Any?): Boolean {
        if (other !is User) return false
        return other.key == key
    }
}