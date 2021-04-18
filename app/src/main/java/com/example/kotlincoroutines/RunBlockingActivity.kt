package com.example.kotlincoroutines

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlincoroutines.databinding.ActivityParallelBackgroundTasksBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

// using run blocking with coroutines
class RunBlockingActivity : AppCompatActivity() {

    private lateinit var binding : ActivityParallelBackgroundTasksBinding
    private val TAG = "RUN_BLOCKING_ACTIVITY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityParallelBackgroundTasksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.acbButton.setOnClickListener {
            main()
        }
    }

    fun main() {
        CoroutineScope(Main).launch {
            println("$TAG : Starting job in thread : ${Thread.currentThread().name}")

            val result1 = getResult()
            println("$TAG : result 1 = $result1")

            val result2 = getResult()
            println("$TAG : result 2 = $result2")

            val result3 = getResult()
            println("$TAG : result 3 = $result3")

            val result4 = getResult()
            println("$TAG : result 4 = $result4")

            val result5 = getResult()
            println("$TAG : result 5 = $result5")
        }

        CoroutineScope(Main).launch {
            delay(1000)
            runBlocking {
                println("$TAG : starting blocking thread on ${Thread.currentThread().name }")
                delay(4000)
                println("$TAG : completed blocking thread")
            }
        }
    }

    private suspend fun getResult() : Int {
        delay(1000)
        return Random.nextInt(0,100)
    }
}