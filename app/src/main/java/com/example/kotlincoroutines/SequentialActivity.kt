package com.example.kotlincoroutines

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlincoroutines.databinding.ActivityParallelBackgroundTasksBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlin.system.measureTimeMillis

// using coroutines to run tasks in parallel and give a combined output when all tasks are complete
class SequentialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityParallelBackgroundTasksBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityParallelBackgroundTasksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.acbButton.setOnClickListener {
            setNewText("Clicked!")
            fakeApiRequest()
        }
    }

    private fun fakeApiRequest() {
        CoroutineScope(IO).launch {
            val executionTime = measureTimeMillis {
                val result1 = async {
                    println("debug: launching job1 on Thread: ${Thread.currentThread().name}")
                    // the value returned from below method is set in result1
                    getResult1FromApi()
                }.await()

                val result2 = async {
                    println("debug: launching job2 on Thread: ${Thread.currentThread().name}")
                    try {
                        getResult2FromApi(result1)
                        // to test how exception will be thrown , uncomment the following line and comment the above line
//                        getResult2FromApi(result1 = "eee")
                    } catch (e: CancellationException) {
                        e.message
                    }
                }.await()

                println("debug: got result 2 : $result2")
            }
            println("debug: total execution time : $executionTime")
        }
    }

    fun setNewText(input: String) {
        val newText = binding.actvText.text.toString() + "\n$input"
        binding.actvText.text = newText
    }

    private suspend fun setTextOnMainThread(input: String) {
        withContext(Main) {
            setNewText(input = input)
        }
    }

    private suspend fun getResult1FromApi(): String {
        delay(1000)
        return "Result 1"
    }

    private suspend fun getResult2FromApi(result1: String): String {
        delay(1900)
        if(result1.equals("Result 1")) {
            return "Result 2"
        }
        throw CancellationException("Result 1 was incoreect")
    }
}