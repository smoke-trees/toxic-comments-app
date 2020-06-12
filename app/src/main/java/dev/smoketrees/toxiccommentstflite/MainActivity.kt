package dev.smoketrees.toxiccommentstflite

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var mJob: Job
    override val coroutineContext: CoroutineContext
        get() = mJob + Dispatchers.Main

    private val labels =
        listOf("Toxic", "Severe Toxic", "Obscene", "Threat", "Insult", "Identity Hate")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mJob = Job()

        predictButton.isEnabled = false

        val classifier = Classifier(this)

        val meh = async {
            withContext(Dispatchers.IO) {
                classifier.init()
                withContext(Dispatchers.Main) {
                    predictButton.isEnabled = true
                }
            }
        }

        predictButton.setOnClickListener {
            if (inputText.text.isEmpty()) {
                Toast.makeText(this, "Text cant be empty", Toast.LENGTH_SHORT).show()
            } else {
                predictButton.isEnabled = false
                val text = inputText.text.toString().toLowerCase(Locale.ROOT).trim()
                val resArr = classifier.classifyText(text)
                predictButton.isEnabled = true
                updateResultUI(resArr)
            }
        }
    }

    private fun updateResultUI(res: FloatArray) {
        val sortedRes = res.sorted().reversed()
        val labelList = mutableListOf<String>()
        for (i in 0..2) {
            val index = res.indexOf(sortedRes[i])
            labelList.add(labels[index])
        }
        val labelViews = listOf(firstLabel, secondLabel, thirdLabel)
        val valueViews = listOf(firstValue, secondValue, thirdValue)
        for (i in 0..2) {
            labelViews[i].text = labelList[i]
            valueViews[i].text = sortedRes[i].toString()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mJob.cancel()
    }
}