package com.nikitagordia.geohost.modules.main.view.fragments

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.nikitagordia.geohost.R
import com.nikitagordia.geohost.databinding.ItemUserBinding
import com.nikitagordia.geohost.modules.main.model.MainModelSubscriber
import com.nikitagordia.geohost.modules.main.model.data.User

/**
 * Created by nikitagordia on 6/12/18.
 */

class ListAdapter(private val context: Context) : RecyclerView.Adapter<ListAdapter.UserHolder>(), MainModelSubscriber {

    val users = mutableListOf<User>()
    var key: String? = null

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) = UserHolder(ItemUserBinding.inflate(LayoutInflater.from(context), parent, false))

    override fun getItemCount() = users.size

    override fun onBindViewHolder(holder: UserHolder?, position: Int) { holder?.onBind(users[position]) }

    override fun onUserAdd(u: User) {
        users += u
        notifyItemInserted(users.size - 1)
    }

    private fun search(u: User): Int {
        var pos = 0
        users.forEachIndexed { index, user -> if (user == u) { pos = index; return@forEachIndexed } }
        return pos
    }

    override fun onUserRemove(u: User) {
        var pos = search(u)
        users.removeAt(pos)
        notifyItemRemoved(pos)
    }

    override fun onUserChange(u: User) {
        updateUser(u)
    }

    override fun onChangeLocation(u: User) {
        updateUser(u)
    }

    private fun updateUser(u: User) {
        var pos = search(u)
        users[pos].update(u)
        notifyItemChanged(pos)
    }

    fun clear() {
        val sz = users.size
        users.clear()
        notifyItemRangeRemoved(0, sz)
    }

    inner class UserHolder(private val bind: ItemUserBinding) : RecyclerView.ViewHolder(bind.root) {

        fun onBind(u: User) {
            if (u.key == key) bind.name.text = u.name + " (" + context.resources.getString(R.string.you) + ")" else bind.name.text = u.name
            if (u.position == null) {
                bind.location.text = context.resources.getString(R.string.searching)
            } else {
                bind.location.text = u.position?.lon.toString() + " : " + u.position?.lat.toString()
            }
        }
    }
}