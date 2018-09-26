package com.dogwood008.kotlinrxsample

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.dogwood008.kotlinrxsample.databinding.ActivityMainBinding
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.squareup.moshi.Moshi

class MainActivity : AppCompatActivity() {
    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private val SLACK_ENDPOINT = "http://localhost8000" // TODO: Use SharedPreference
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = CalcViewModel()
        setEvents()
    }

    private fun setEvents() {
        binding.viewModel!!.tenKeyObservable
                .doOnNext {
                    val prevValue = binding.viewModel!!.display.get()
                    binding.viewModel!!.display.set(prevValue * 10 + it)
                }
                .subscribe()
        binding.viewModel!!.bsKeyObservable
                .doOnNext {
                    val prevValue = binding.viewModel!!.display.get()
                    binding.viewModel!!.display.set(prevValue / 10)
                }
                .subscribe()
        binding.viewModel!!.enterKeyObservable
                .doOnNext {
                    val value = binding.viewModel!!.display.get()
                    Log.d(TAG, value.toString())
                }
                .doOnNext {
                    val value = binding.viewModel!!.display.get()
                    if (value == 1234) {
                        val settingIntent = Intent(this, SettingsActivity::class.java)
                        startActivity(settingIntent)
                    } else {
                        postToSlack(value)
                    }
                }
                .subscribe()
    }

    private fun postToSlack(text: Any) {
        val moshi = Moshi.Builder().build()
        val requestAdapter = moshi.adapter(Slack::class.java)
        val header: HashMap<String, String> = hashMapOf("Content-Type" to "application/json")
        val payload = Slack(text = text.toString())

        SLACK_ENDPOINT.httpPost().header(header)
                .body(requestAdapter.toJson(payload)).responseString { _request, _response, result ->
            when (result) {
                is Result.Failure -> {
                    Toast.makeText(this, result.getException().toString(), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showToast(text: Any) {
        Toast.makeText(this, text.toString(), Toast.LENGTH_SHORT).show()
        Log.d(TAG, text.toString())
    }
}
