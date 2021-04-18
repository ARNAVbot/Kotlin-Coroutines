package com.example.kotlincoroutines

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlincoroutines.databinding.ActivityParallelBackgroundTasksBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlin.system.measureTimeMillis

// using coroutines to run tasks in parallel and give a combined output when all tasks are complete
class ParallelBackgroundTasksActivity : AppCompatActivity() {

    private lateinit var binding: ActivityParallelBackgroundTasksBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityParallelBackgroundTasksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.acbButton.setOnClickListener {
            setNewText("Clicked!")
//            fakeApiRequest()
            fakeApiRequest2()
        }
    }

    // METHOD 1 to run job in parallel
    private fun fakeApiRequest() {
        val startTime = System.currentTimeMillis()

        val parentJob = CoroutineScope(IO).launch {

            val job1 = launch {
                // the following method can only be called inside a coroutine
                val time1 = measureTimeMillis {
                    println("debug: launching job 1 in thread ${Thread.currentThread().name}")
                    val result1 = getResult1FromApi()
                    setTextOnMainThread(result1)
                }
                println("debug: completed job in $time1 ms")
            }

            // if i call job1.join() , then it will wait for job1 to finish and then execute the below code.
            // However, apart from that , the following code below will run immediately


            // the folliwing job will kickstart immediately after the above job1 is launched
            val job2 = launch {
                // the following method can only be called inside a coroutine
                val time2 = measureTimeMillis {
                    // the thread in which this runs can be different from the one in which the job 1 runs. They both run on IO threads. Bt threads could be different
                    println("debug: launching job 2 in thread ${Thread.currentThread().name}")
                    val result2 = getResult2FromApi()
                    setTextOnMainThread(result2)
                }
                println("debug: completed job in $time2 ms")
            }
        }

        parentJob.invokeOnCompletion {
            // this function is invoked when the job is completed
            println("debug : total elapsed time = ${System.currentTimeMillis() - startTime}")
        }
    }

    // METHOD 2 to run jobs in parallel
    private fun fakeApiRequest2() {
        CoroutineScope(IO).launch {
            val executionTime = measureTimeMillis {
                //launching getResult1FromApi in async manner
                val result1 : Deferred<String> = async {
                    println("debug: launching job1 : ${Thread.currentThread().name}")
                    getResult1FromApi()
                }

                //launching getResult2FromApi in async manner
                val result2 : Deferred<String> = async {
                    println("debug: launching job2 : ${Thread.currentThread().name}")
                    getResult2FromApi()
                }

                // await waits for the result
                setTextOnMainThread("Got ${result1.await()}")
                setTextOnMainThread("Got ${result2.await()}")
            }
            println("debug: Total time elapsed : $executionTime")
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

    private suspend fun getResult2FromApi(): String {
        delay(1900)
        return "Result 2"
    }
}