package com.nikitagordia.geohost.modules.main.view.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nikitagordia.geohost.modules.main.view.MainActivity
import com.nikitagordia.geohost.modules.main.viewmodel.Action
import com.nikitagordia.geohost.modules.main.viewmodel.Event

/**
 * Created by nikitagordia on 6/11/18.
 */

class ListFragment : Fragment() {

    private lateinit var list: RecyclerView
    lateinit var adapter: ListAdapter
    var isFirst = true
    var key: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        isFirst = true
        list = RecyclerView(context)
        adapter = ListAdapter(context!!)
        adapter.key = key
        list.layoutManager = LinearLayoutManager(context)
        list.adapter = adapter

        val le = (activity as MainActivity).lastEvent
        if (le != null) onEvent(le)

        return list
    }

    override fun onStop() {
        super.onStop()
        adapter.clear()
    }

    fun updateKey(key: String?) {
        if (key == null) return
        this.key = key
        if (context == null) return
        adapter.key = key
    }

    fun onEvent(t: Event) {
        if (context == null) return
        if (isFirst) {
            isFirst = false
            for (i in t.users)
                adapter.onUserAdd(i)
            return
        }
        when(t.event) {
            Action.ADD -> adapter.onUserAdd(t.u)
            Action.REMOVE -> adapter.onUserRemove(t.u)
            Action.CHANGE -> adapter.onUserChange(t.u)
        }
    }

    companion object {

        fun getInstance() = ListFragment()
    }
}