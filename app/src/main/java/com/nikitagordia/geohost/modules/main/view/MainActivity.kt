package com.nikitagordia.geohost.modules.main.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.nikitagordia.geohost.R
import com.nikitagordia.geohost.databinding.ActivityMainBinding
import com.nikitagordia.geohost.modules.main.view.fragments.ListFragment
import com.nikitagordia.geohost.modules.main.view.fragments.MapFragment
import com.nikitagordia.geohost.modules.main.viewmodel.Action
import com.nikitagordia.geohost.modules.main.viewmodel.Event
import com.nikitagordia.geohost.modules.main.viewmodel.MainViewModel
import com.nikitagordia.geohost.modules.main.viewmodel.MainViewModelInterface
import com.nikitagordia.geohost.utils.PreferencesManager

class MainActivity : AppCompatActivity() {

    private val mapFragment = MapFragment.getInstance()
    private val listFragment = ListFragment.getInstance()

    private var isMap = true
    private var edited = false
    private var isFirst = true

    private lateinit var bind: ActivityMainBinding
    private lateinit var vm: MainViewModelInterface
    private lateinit var pref: PreferencesManager
    private lateinit var name: String
    private lateinit var key: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        vm = ViewModelProviders.of(this).get(MainViewModel::class.java)
        pref = PreferencesManager(this, resources.getString(R.string.quest))

        bind.name.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!edited) {
                    edited = true
                    Snackbar.make(bind.coordinatorLayout, resources.getString(R.string.change_name), Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.go, View.OnClickListener {
                                if (name == "") name = resources.getString(R.string.quest)
                                edited = false
                                vm.changeName(name)
                                pref.setName(name)
                            }).show()
                }
                name = s.toString()
            }
        })

        vm.events.observe(this, object : Observer<Event> {
            override fun onChanged(t: Event?) {
                if (t == null) return
                if (isFirst) {
                    isFirst = false
                    for (i in t.users)
                        listFragment.adapter.onUserAdd(i)
                    return
                }
                when(t.event) {
                    Action.ADD -> listFragment.adapter.onUserAdd(t.u)
                    Action.REMOVE -> listFragment.adapter.onUserRemove(t.u)
                    Action.CHANGE -> listFragment.adapter.onUserChange(t.u)
                }
            }
        })

        isMap = false
        updateFragments()
    }

    private fun updateFragments() {
        if (isMap) supportFragmentManager.beginTransaction().replace(R.id.container, mapFragment).commit()
            else supportFragmentManager.beginTransaction().replace(R.id.container, listFragment).commit()
    }

    override fun onStart() {
        super.onStart()
        name = bind.name.text.toString()
        if (name == "") name = pref.getName()
        key = vm.online(name)
        listFragment.adapter.key = key
    }

    override fun onPause() {
        super.onPause()
        vm.offline()
        isFirst = true
        listFragment.adapter.clear()
    }
}
