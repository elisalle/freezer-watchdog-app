package com.example.freezerwatchdog

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*

const val LOG_TAG = "MainActivity"

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.preferences -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


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
                Log.e(LOG_TAG, e.message.toString())
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

        val freezerOpenStatusText = resources.getString(R.string.freezer_open_status)
        val freezerClosedStatusText = resources.getString(R.string.freezer_closed_status)

        val mySwipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swiperefresh)
        fun refreshFreezerView () {
            // progressBar.visibility = View.VISIBLE
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
                // progressBar.visibility = View.GONE
                adapter.notifyDataSetChanged()
                mySwipeRefreshLayout.setRefreshing(false)
            }
        }

        // Defining the refresh gesture
        mySwipeRefreshLayout.setOnRefreshListener {
            Log.i(LOG_TAG, "refreshFreezerView called from SwipeRefreshLayout")

            // This method performs the actual data-refresh operation.
            // The method calls setRefreshing(false) when it's finished.
            refreshFreezerView()
        }

        Snackbar.make(
            findViewById(android.R.id.content),
            resources.getString(R.string.refresh_prompt_text),
            Snackbar.LENGTH_LONG
        )
            .setAction(
                resources.getString(R.string.refresh_button_text), View.OnClickListener {
                    Log.i(LOG_TAG, "refreshFreezerView called from Snackbar")
                    mySwipeRefreshLayout.setRefreshing(true)
                    refreshFreezerView()
                }
            )
            .show()

    }
}
