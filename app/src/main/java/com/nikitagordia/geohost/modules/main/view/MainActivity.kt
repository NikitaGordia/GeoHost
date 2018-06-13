package com.nikitagordia.geohost.modules.main.view

import android.Manifest
import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.google.android.gms.location.*
import com.nikitagordia.geohost.R
import com.nikitagordia.geohost.databinding.ActivityMainBinding
import com.nikitagordia.geohost.modules.main.model.data.Position
import com.nikitagordia.geohost.modules.main.view.fragments.ListFragment
import com.nikitagordia.geohost.modules.main.view.fragments.UsersMapFragment
import com.nikitagordia.geohost.modules.main.viewmodel.Event
import com.nikitagordia.geohost.modules.main.viewmodel.MainViewModel
import com.nikitagordia.geohost.modules.main.viewmodel.MainViewModelInterface
import com.nikitagordia.geohost.utils.PreferencesManager

class MainActivity : AppCompatActivity() {

    private val LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION
    private val LOCATION_REQUEST_CODE = 153
    private val LOCATION_UPDATE_INTERVAL = 10000L

    private val mapFragment = UsersMapFragment.getInstance()
    private val listFragment = ListFragment.getInstance()

    private var isMap = false
    private var edited = false

    private lateinit var bind: ActivityMainBinding
    private lateinit var vm: MainViewModelInterface
    private lateinit var pref: PreferencesManager
    private lateinit var name: String
    private var key: String? = null

    var lastEvent: Event? = null

    private lateinit var client: FusedLocationProviderClient
    private val callback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult?) {
            if (p0 == null) return
            vm.changeLocation(Position(p0.locations.last().longitude, p0.locations.last().latitude))
        }
        override fun onLocationAvailability(p0: LocationAvailability?) {
            if (p0 != null && !p0.isLocationAvailable) Toast.makeText(this@MainActivity, resources.getString(R.string.check_your_gps), Toast.LENGTH_SHORT).show()
        }
    }
    private val request = LocationRequest().apply {
        interval = LOCATION_UPDATE_INTERVAL
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        vm = ViewModelProviders.of(this).get(MainViewModel::class.java)
        pref = PreferencesManager(this)

        bind.name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length == 0) return
                if (!edited) {
                    edited = true
                    Snackbar.make(bind.coordinatorLayout, resources.getString(R.string.change_name), Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.go, {
                                edited = false
                                vm.changeName(name)
                                pref.setName(name)
                                bind.name.apply {
                                    setText("")
                                    hint = name
                                }
                            }).show()
                }
                name = s.toString()
            }
        })

        vm.events.observe(this, object : Observer<Event> {
            override fun onChanged(t: Event?) {
                if (t == null) return
                lastEvent = t
                listFragment.onEvent(t)
                mapFragment.onEvent(t)
            }
        })

        bind.switchMode.setOnClickListener {
            if (isMap) isMap = false else isMap = true
            updateFragments()
        }
        updateFragments()
    }

    private fun updateFragments() {
        if (isMap) {
            bind.switchMode.setText(R.string.title_list)
            supportFragmentManager.beginTransaction().replace(R.id.container, mapFragment).commit()
        } else {
            bind.switchMode.setText(R.string.title_maps)
            supportFragmentManager.beginTransaction().replace(R.id.container, listFragment).commit()
            listFragment.updateKey(key)
        }
    }

    override fun onStart() {
        super.onStart()
        setupName()
        key = vm.online(name)

        listFragment.updateKey(key)
        setupLocation()
    }

    override fun onStop() {
        super.onStop()
        client.removeLocationUpdates(callback)
        vm.offline()
    }

    private fun setupLocation() {
        client = LocationServices.getFusedLocationProviderClient(this)
        updateCallback()
    }

    @SuppressLint("MissingPermission")
    private fun updateCallback() {
        if (!checkPermissions(this)) {
            requestPermissions( Array(1) { LOCATION_PERMISSION }, LOCATION_REQUEST_CODE )
        } else client.requestLocationUpdates(request, callback, null)
    }

    private fun setupName() {
        name = bind.name.text.toString()
        if (name.isEmpty()) name = pref.getName(resources.getString(R.string.guest))
        bind.name.hint = name
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_REQUEST_CODE && permissions[0] == LOCATION_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) updateCallback()
    }

    private fun checkPermissions(cxt: Context) = Build.VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(cxt, LOCATION_PERMISSION) == PackageManager.PERMISSION_GRANTED

}
