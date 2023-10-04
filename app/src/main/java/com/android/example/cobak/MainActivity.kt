package com.android.example.cobak

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class MainActivity : AppCompatActivity() {

    private lateinit var editTextMessage: EditText
    private lateinit var buttonSend: Button
    private lateinit var textViewResponse: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inisialisasi elemen-elemen tampilan
        editTextMessage = findViewById(R.id.editTextMessage)
        buttonSend = findViewById(R.id.buttonSend)
        textViewResponse = findViewById(R.id.textViewResponse)

        // Menghubungkan onClickListener ke tombol Kirim
        buttonSend.setOnClickListener {
            val userMessage = editTextMessage.text.toString()
            sendUserMessage(userMessage)
        }
    }

    @SuppressLint("SetTextI18n")
    @OptIn(DelicateCoroutinesApi::class)
    private fun sendUserMessage(userMessage: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val url = "https://8cjrhv6h-5000.asse.devtunnels.ms/api/bot"
                val mediaType = "application/json".toMediaTypeOrNull()
                val requestBody = "{\"user_message\":\"$userMessage\"}".toRequestBody(mediaType)

                val client = OkHttpClient()
                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val botResponse = responseBody?.split("\"response\":\"")?.get(1)?.split("\"")?.get(0)
                    runOnUiThread {
                        textViewResponse.text = "Bot Response: $botResponse"
                    }
                } else {
                    val errorMessage = "Error: ${response.code} - ${response.body?.string()}"
                    runOnUiThread {
                        textViewResponse.text = errorMessage
                    }
                }
            } catch (e: Exception) {
                val errorMessage = "Error: ${e.message}"
                runOnUiThread {
                    textViewResponse.text = errorMessage
                }
            }
        }
    }
}
