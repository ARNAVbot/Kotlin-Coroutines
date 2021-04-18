package com.example.kotlincoroutines

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlincoroutines.databinding.ActivtyNetworkTimeoutBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO

//NOTE: MAny coroutines can do work on the same thread
class NetworkTimeoutActivity: AppCompatActivity() {

    private lateinit var binding : ActivtyNetworkTimeoutBinding

    private val RESULT_1 = "Result #1"
    private val RESULT_2 = "Result #2"
    val JOB_TIMEOUT = 1900L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivtyNetworkTimeoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            setNewText("Click!")

            CoroutineScope(IO).launch {
                fakeApiRequest()
            }
        }
    }

    private suspend fun fakeApiRequest() {
        withContext(IO) {
            // The following sets a timeout on the encapsulated coroutine
            val job = withTimeoutOrNull(JOB_TIMEOUT) {

                val result = getResult1FromApi()
                println("debug result #1: ${result}")
                setTextOnMainThread("Got $result")

                val result2 = getResult2FromApi()
                setTextOnMainThread("Got $result2")
            }
            // if there was another job here, then this second job would have to wait
            // until the previous above job completes ,becuause of the withTimeoutOrNull method.
            // If the above job was launched normally, using launch , then the following code would have executed immediately

            if(job == null) {
                val cancelMessage = "Cancelling job... Job took longer than timepout"
                println("debug: $cancelMessage")
                setTextOnMainThread(cancelMessage)
            }
        }
    }

    private suspend fun getResult1FromApi(): String {
        logThread("getResult1FromApi")

        // The following delay only delays this coroutine on the thread.
        // It does not delay the thread
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


    private fun setNewText(input:String) {
        val newText = binding.textIew.text.toString() + "\n$input"
        binding.textIew.text = newText
    }

    private suspend fun setTextOnMainThread(input: String) {
        // withContext takes the current coroutine and automatically creates a coroutine scope for you and switches to the thread that u specify
        withContext(Dispatchers.Main) {
            setNewText(input)
        }
    }

    private fun logThread(methodName: String) {
        println("debug : ${methodName} : ${Thread.currentThread().name}")
    }


}