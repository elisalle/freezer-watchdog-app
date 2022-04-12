package com.example.freezerwatchdog

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Button
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

        btnGetData.setOnClickListener {
            // Use launch and pass Dispatchers.Main to tell that
            // the result of this Coroutine is expected on the main thread.
            val refreshJob = launch(Dispatchers.IO) {
                val apiData = getFreezerSystemStatus("JH95Q")
                arrayData.clear()
                if (apiData != null) {
                    for (i in apiData) {
                        arrayData.add(
                            ItemsViewModel(
                                i.freezer_id.toString(),
                                if (i.status == true) "Open!" else "Closed"
                            )
                        )
                    }
                }
            }
            launch(Dispatchers.Main) {
                refreshJob.join()
                adapter.notifyDataSetChanged()
            }
        }

    }
}
