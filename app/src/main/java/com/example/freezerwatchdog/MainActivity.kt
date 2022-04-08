package com.example.freezerwatchdog

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnGetData = findViewById<Button>(R.id.btnGetData)

        btnGetData.setOnClickListener {
            // Use launch and pass Dispatchers.Main to tell that
            // the result of this Coroutine is expected on the main thread.
            launch(Dispatchers.Main) {
                // Try catch block to handle exceptions when calling the API.
                try {
                    val response = ApiAdapter.apiClient.getFreezerSystemStatus("JH95Q")
                    // Check if response was successful.
                    if (response.isSuccessful && response.body() != null) {
                        val data = response.body()!!
                        // Check for null
                        data.let { data_checked ->
                            // Load URL into the ImageView using Coil.
                            Log.i(TAG, data_checked.toString())
                        }
                    } else {
                        // Show API error.
                        Log.e(TAG, "error ${response.message()}")
                        Toast.makeText(
                            this@MainActivity,
                            "Error Occurred: ${response.message()}",
                            Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    // Show API error. This is the error raised by the client.
                    Log.e(TAG, "Exception ${e.message}")
                    Toast.makeText(this@MainActivity,
                        "Exception Occurred: ${e.message}",
                        Toast.LENGTH_LONG).show()
                }
            }
        }

        // getting the recyclerview by its id
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)

        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(this)

        // ArrayList of class ItemsViewModel
        val data = ArrayList<ItemsViewModel>()

        // This loop will create 20 Views containing
        // the image with the count of view
        for (i in 1..20) {
            data.add(
                ItemsViewModel(
                    "Freezer $i",
                    "Open!"
                )
            )
        }

        // This will pass the ArrayList to our Adapter
        val adapter = RecyclerViewAdapter(data)

        // Setting the Adapter with the recyclerview
        recyclerview.adapter = adapter

    }
}
