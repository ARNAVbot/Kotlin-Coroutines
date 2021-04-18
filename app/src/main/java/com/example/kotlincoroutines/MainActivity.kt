package com.example.kotlincoroutines

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kotlincoroutines.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val RESULT_1 = "Result #1"
    private val RESULT_2 = "Result #2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {

            //launching coroutine here
            // it can be IO/MAIN/DEFAULT
            // IO = netowrk or io operations
            // default = cpu intensive tasks
            CoroutineScope(IO).launch {
                fakeApiRequest()
            }
        }

        binding.moveToSecondActivity.setOnClickListener {
            val intent = Intent(this, NetworkTimeoutActivity::class.java)
            startActivity(intent)
        }

        binding.moveToParalledActivity.setOnClickListener {
            val intent = Intent(this, ParallelBackgroundTasksActivity::class.java)
            startActivity(intent)
        }

        binding.moveToSequentailActivity.setOnClickListener {
            val intent = Intent(this, SequentialActivity::class.java)
            startActivity(intent)
        }

        binding.moveToRunBlockingActivity.setOnClickListener {
            val intent = Intent(this, RunBlockingActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setNewText(input:String) {
        val newText = binding.textIew.text.toString() + "\n$input"
        binding.textIew.text = newText
    }

    private suspend fun setTextOnMainThread(input: String) {
        // withContext takes the current coroutine and automatically creates a coroutine scope for you and switches to the thread that u specify
        withContext(Main) {
            setNewText(input)
        }
    }

    private suspend fun fakeApiRequest() {
        // the following call is synchronous
        val result1 = getResult1FromApi()
        println("debug: $result1")
        setTextOnMainThread(result1)

        // this again gets called on the separate thread
        // can be checked by the logThread that we put in getResult2FromApi
        val result2 = getResult2FromApi()
        setTextOnMainThread(result2)
    }

    // suspend keyword indicates that this function can be asynchronous in nature.
    // Hence, it must be called from any other suspend function or another coroutine block.
    private suspend fun getResult1FromApi(): String {
        logThread("getResult1FromApi")

        // The following delay only delays this coroutine on the thread.
        // It does not delay the thread
        // This just delays the coroutine. Thread.sleep delays the thread
        delay(1000)

        return RESULT_1

        // The following delays the Thread.
        // Should never call this in a coroutine as other coroutune might be running
        // on this thread. and hence this will delay everything else too.
//        Thread.sleep(1000)
    }

    private suspend fun getResult2FromApi() : String {
        logThread("getResult2FromApi")
        delay(1000)
        return RESULT_2
    }

    private fun logThread(methodName: String) {
        println("debug : ${methodName} : ${Thread.currentThread().name}")
    }
}