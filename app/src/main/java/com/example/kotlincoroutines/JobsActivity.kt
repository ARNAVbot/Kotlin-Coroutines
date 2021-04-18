package com.example.kotlincoroutines

import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlincoroutines.databinding.ActivityJobsBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class JobsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJobsBinding

    private val PROGRESS_MAX = 100
    private val PROGRES_START = 0
    private val JOB_TIME = 4000 //ms
    private lateinit var job: CompletableJob

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJobsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.jobButton.setOnClickListener {
            //TODO: check what this means -> !::
            if(!::job.isInitialized) {
                initJob()
            }
            binding.jobProgressBar.startJobOrCancel(job)
        }
    }

    // example of extension function
    fun ProgressBar.startJobOrCancel(job: Job) {
        if(this.progress > 0) {
            println("${job} is already active.Canceling.....")
            resetJob()
        } else {
            binding.jobButton.text = "Cancel job#1"
            // the following creates a whole new independent job on a whole new context and cancelling the job does not affect any other job at all.
            // Hence, cancelling this coroutine wont cancel any other coroutine runnig on the same IO thread.
            // This is because the context of the following is different from the context of other coroutines running on IO
            CoroutineScope(IO + job).launch {
                // executed on background thread
                println("coroutine ${this} is activiated with job ${job}")

                for(i in PROGRES_START.. PROGRESS_MAX) {
                    delay((JOB_TIME/PROGRESS_MAX).toLong())
                    this@startJobOrCancel.progress = i
                }
            updateJobCompleteTextView("Job is complete")
            }
        }
    }

    private fun updateJobCompleteTextView(text: String) {
        GlobalScope.launch(Main) {
            binding.jobCompleteText.text = text
        }
    }

    private fun resetJob() {
        if(job.isActive || job.isCompleted) {
            // a job which is cancelled like this CANNOT be used again
            job.cancel(CancellationException("Reseting job"))
        }
        initJob()
    }

    fun initJob() {
        updateJobCompleteTextView("Start Job #1")
        job = Job()
        job.invokeOnCompletion {
             it?.message.let {
                 var msg = it
                 if(msg.isNullOrBlank()) {
                     msg = "Unknown cancellation error"
                 }
                 println("${job} was cancelled. Reason : $msg")
                 showToast(msg)
             }
        }
        binding.jobProgressBar.max = PROGRESS_MAX
        binding.jobProgressBar.progress = PROGRES_START
    }

    fun showToast(text: String) {
        //The following gaurantees that no matter wherever u are, the following Toast will always be shown
        GlobalScope.launch(Main) {
            Toast.makeText(this@JobsActivity, text, Toast.LENGTH_SHORT).show()
        }
    }
}