package com.example.servicestestapp

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import com.example.servicestestapp.service.MyService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var rebindOnReturn = false

    private var myService: MyService? = null
    private var isServiceBound: Boolean = false
        set(value) {
            field = value
            tvServiceState.text = if (value) "Service is bound now" else "Service is not bound"
        }
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as MyService.MyServiceBinder
            myService = binder.getService()
            isServiceBound = true
            Log.d("Activity", "onServiceConnected")
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isServiceBound = false
            Log.d("Activity", "onServiceDisconnected")
        }
    }

    override fun onStart() {
        super.onStart()
        if (rebindOnReturn) {
            bindMyService()
        }
    }

    override fun onStop() {
        super.onStop()
        myService?.startService()
        unbindMyService()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setListeners()
    }

    private fun setListeners() {
        btnStartService.setOnClickListener { startMyService() }
        btnStopService.setOnClickListener { stopMyService() }
        btnBindService.setOnClickListener { bindMyService() }
        btnUnbindService.setOnClickListener { unbindMyService() }
        btnShowNotification.setOnClickListener { showTextNotification() }
        ctvRebindOnReturn.setOnClickListener {
            rebindOnReturn = !rebindOnReturn
            ctvRebindOnReturn.isChecked = rebindOnReturn
        }
        etNotificationText.setOnEditorActionListener { view, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) view.clearFocus()
            false
        }
    }

    private fun startMyService() {
        startService(Intent(this, MyService::class.java))
    }

    private fun stopMyService() {
        stopService(Intent(this, MyService::class.java))
    }

    private fun bindMyService() {
        bindService(Intent(this, MyService::class.java), connection, Context.BIND_AUTO_CREATE)
    }

    private fun unbindMyService() {
        try {
            if (isServiceBound) {
                unbindService(connection)
                isServiceBound = false
                myService = null
            } /*else Toast.makeText(this, "Service not bound", Toast.LENGTH_SHORT).show()*/
        } catch (e: Throwable) {
            Toast.makeText(this, "Unbind error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showTextNotification() {
        val notificationText =
            etNotificationText.text?.toString()?.takeIf { it.isNotBlank() }
                ?: "Text field was empty"
        myService?.showNotification(notificationText) ?: let {
            Toast.makeText(this, "Service not bound", Toast.LENGTH_SHORT).show()
        }
    }


}