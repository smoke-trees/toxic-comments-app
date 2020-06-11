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
                predictButton.isEnabled = true
                val resArr = classifier.classifyText(text)
                resultText.text = "Result:\n" +
                        "\nToxic: ${resArr[0]}" +
                        "\nSevere Toxic: ${resArr[1]}" +
                        "\nObscene: ${resArr[2]}" +
                        "\nThreat: ${resArr[3]}" +
                        "\nInsult: ${resArr[4]}" +
                        "\nIdentity Hate: ${resArr[5]}"
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mJob.cancel()
    }
}