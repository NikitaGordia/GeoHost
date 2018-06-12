package com.nikitagordia.geohost.modules.main.model.remote.firebase

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.nikitagordia.geohost.modules.main.model.MainModelInterface
import com.nikitagordia.geohost.modules.main.model.MainModelSubscriber
import com.nikitagordia.geohost.modules.main.model.data.User

/**
 * Created by nikitagordia on 6/11/18.
 */

class FirebaseMainModel : MainModelInterface {

    val NAME = "name"
    val USERS = "users"

    val db = FirebaseDatabase.getInstance().getReference(USERS)

    val listener = ChildListener()

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

    inner class ChildListener : ChildEventListener {

        var subscriber: MainModelSubscriber? = null

        override fun onCancelled(p0: DatabaseError?) {}

        override fun onChildMoved(p0: DataSnapshot?, p1: String?) {}

        override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
            subscriber?.apply { onUserChange(User(p0!!.key, p0.child(NAME).value.toString())) }
        }

        override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
            subscriber?.apply { onUserAdd(User(p0!!.key, p0.child(NAME).value.toString())) }
        }

        override fun onChildRemoved(p0: DataSnapshot?) {
            subscriber?.apply { onUserRemove(User(p0!!.key, p0.child(NAME).value.toString())) }
        }
    }
}