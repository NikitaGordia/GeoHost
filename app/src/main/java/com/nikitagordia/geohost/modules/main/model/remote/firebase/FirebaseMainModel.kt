package com.nikitagordia.geohost.modules.main.model.remote.firebase

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.nikitagordia.geohost.modules.main.model.MainModelInterface
import com.nikitagordia.geohost.modules.main.model.MainModelSubscriber
import com.nikitagordia.geohost.modules.main.model.data.Position
import com.nikitagordia.geohost.modules.main.model.data.User

/**
 * Created by nikitagordia on 6/11/18.
 */

class FirebaseMainModel : MainModelInterface {

    private val NAME = "name"
    private val USERS = "users"
    private val POSITION = "position"
    private val LAT = "lat"
    private val LON = "lon"

    private val db = FirebaseDatabase.getInstance().getReference(USERS)

    private val listener = ChildListener()

    override fun online(name: String, subscriber: MainModelSubscriber): String? {
        val key = db.push().key ?: return null
        listener.subscriber = subscriber
        db.addChildEventListener(listener)
        db.child(key).child(NAME).setValue(name)
        return key
    }

    override fun offline(key: String) {
        db.removeEventListener(listener)
        db.child(key).removeValue()
    }

    override fun changeName(key: String, name: String) {
        db.child(key).child(NAME).setValue(name)
    }

    override fun changeLocation(key: String, pos: Position) {
        db.child(key).child(POSITION).child(LON).setValue(pos.lon)
        db.child(key).child(POSITION).child(LAT).setValue(pos.lat)
    }

    inner class ChildListener : ChildEventListener {

        var subscriber: MainModelSubscriber? = null

        override fun onCancelled(p0: DatabaseError?) {}

        override fun onChildMoved(p0: DataSnapshot?, p1: String?) {}

        override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
            subscriber?.apply { p0?.apply { onUserChange(getU(p0)) } }
        }

        override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
            subscriber?.apply { p0?.apply { onUserAdd(getU(p0)) } }
        }

        override fun onChildRemoved(p0: DataSnapshot?) {
            subscriber?.apply { p0?.apply { onUserRemove(getU(p0)) } }
        }

        private fun getU(p0: DataSnapshot) : User {
            val u = User(p0.key, p0.child(NAME).value.toString())
            if (p0.child(POSITION).child(LON).value != null && p0.child(POSITION).child(LAT).value != null) u.position = Position(p0.child(POSITION).child(LON).value as Double, p0.child(POSITION).child(LAT).value as Double)
            return u
        }
    }
}