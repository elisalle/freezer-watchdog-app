package com.example.freezerwatchdog

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*

const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.main_toolbar))

        suspend fun getFreezerSystemStatus(system_id: String): List<SystemStatusModel>? {
            // Try catch block to handle exceptions when calling the API.
            try {
                val response = ApiAdapter.apiClient.getFreezerSystemStatus(system_id)
                // Check if response was successful.
                check(response.isSuccessful && response.body() != null) { "No data was returned by the API!" }
                // Check for null
                return response.body()!!
            } catch (e: Exception) {
                // Show API error. This is the error raised by the client.
                Log.e(TAG, e.message.toString())
                Looper.prepare()
                Toast.makeText(this@MainActivity,
                    "Exception Occurred: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                Looper.loop()
            }
            return null
        }

        // getting the recyclerview by its id
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)

        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(this)

        // ArrayList of class ItemsViewModel
        val arrayData = ArrayList<ItemsViewModel>()

        // This will pass the ArrayList to our Adapter
        val adapter = RecyclerViewAdapter(arrayData)

        // Setting the Adapter with the recyclerview
        recyclerview.adapter = adapter
        Log.i("job", "done")

        Toast.makeText(this@MainActivity,
            "Please refresh to show freezer data",
            Toast.LENGTH_LONG
        ).show()

        // Defining the refresh button
        val btnGetData = findViewById<Button>(R.id.btnGetData)

        val progressBar = findViewById<ProgressBar>(R.id.loadingSpinner)

        val freezerOpenStatusText = resources.getString(R.string.freezer_open_status)
        val freezerClosedStatusText = resources.getString(R.string.freezer_closed_status)

        btnGetData.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            // Use launch and pass Dispatchers.Main to tell that
            // the result of this Coroutine is expected on the main thread.
            val refreshJob = launch(Dispatchers.IO) {
                delay(2000)
                val apiData = getFreezerSystemStatus("JH95Q")
                arrayData.clear()
                if (apiData != null) {
                    for (i in apiData) {
                        arrayData.add(
                            ItemsViewModel(
                                i.freezer_id.toString(),
                                if (i.status == true) freezerOpenStatusText else freezerClosedStatusText
                            )
                        )
                    }
                }
            }
            launch(Dispatchers.Main) {
                refreshJob.join()
                progressBar.visibility = View.GONE
                adapter.notifyDataSetChanged()
            }
        }

    }
}
