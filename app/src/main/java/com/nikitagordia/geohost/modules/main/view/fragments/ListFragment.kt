package com.nikitagordia.geohost.modules.main.view.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by nikitagordia on 6/11/18.
 */

class ListFragment : Fragment() {

    private lateinit var list: RecyclerView
    lateinit var adapter: ListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        list = RecyclerView(context)
        adapter = ListAdapter(context!!)
        list.layoutManager = LinearLayoutManager(context)
        list.adapter = adapter

        return list
    }

    companion object {

        fun getInstance() = ListFragment()
    }
}