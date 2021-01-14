/*
 * Copyright (c) 2020 Razeware LLC
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 * 
 * This project and source code may use libraries or frameworks that are
 * released under various Open-Source licenses. Use of those libraries and
 * frameworks are governed by their own individual licenses.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.raywenderlich.android.drinkit

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Main Screen
 */
class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    // Switch to AppTheme for displaying the activity
    setTheme(R.style.AppTheme)

    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

      // Your code
      button_retrieve_token.setOnClickListener {
        // Get token
        if ( checkGooglePlayServices() ) {
          // [START retrieve_current_token]
          FirebaseInstanceId.getInstance().instanceId
              .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                  Log.w(TAG, getString(R.string.token_error), task.exception)
                  return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token

                // Log and toast
                val msg = getString(R.string.token_prefix, token)
                Log.d(TAG, msg)
                Toast.makeText(baseContext, msg, Toast.LENGTH_LONG).show()
              })
          // [END retrieve_current_token]
        } else {
          //You won't be able to send notifications to this device
          Log.w(TAG, "Device doesn't have google play services")
        }
      }

    val bundle = intent.extras
    if (bundle != null) { //bundle must contain all info sent in "data" field of the notification
      text_view_notification.text = bundle.getString("text")
    }
  }


  override fun onStart() {
    super.onStart()
    LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver,
        IntentFilter("MyData")
    )
  }

  override fun onStop() {
    super.onStop()
    LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver)
  }

  private val messageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
      text_view_notification.text = intent.extras?.getString("message")
    }
  }

  private fun checkGooglePlayServices(): Boolean {
    val status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
    return if (status != ConnectionResult.SUCCESS) {
      Log.e(TAG, "Error")
      // ask user to update google play services.
      false
    } else {
      Log.i(TAG, "Google play services updated")
      true
    }
  }

  companion object {
    private const val TAG = "MainActivity"
  }
}
