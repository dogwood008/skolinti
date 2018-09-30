package com.dogwood008.kotlinrxsample

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import com.dogwood008.kotlinrxsample.databinding.ActivityMainBinding
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.squareup.moshi.Moshi
import java.util.*

class MainActivity : AppCompatActivity() {
    companion object {
        private val TAG = MainActivity::class.java.simpleName
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
                    binding.viewModel!!.display.set(prevValue + it)
                }
                .subscribe()
        binding.viewModel!!.bsKeyObservable
                .doOnNext {
                    val prevValue = binding.viewModel!!.display.get()
                    binding.viewModel!!.display.set(prevValue!!.slice(0..prevValue.length - 2))
                }
                .subscribe()
        binding.viewModel!!.enterKeyObservable
                .doOnNext {
                    val value = binding.viewModel!!.display.get()
                    Log.d(TAG, value.toString())
                    when {
                        value == adminPIN(this) -> {
                            val settingIntent = Intent(this, SettingsActivity::class.java)
                            startActivity(settingIntent)
                        }
                        isUserCode(value!!) -> {
                            Log.d(TAG, "UserCode")
                            showLockerPIN()
                        }
                        else -> {
                            postToSlack(value)
                        }
                    }
                }
                .subscribe()
    }

    private fun postToSlack(text: Any) {
        val moshi = Moshi.Builder().build()
        val requestAdapter = moshi.adapter(Slack::class.java)
        val header: HashMap<String, String> = hashMapOf("Content-Type" to "application/json")
        val payload = Slack(text = text.toString())
        val slackEndpoint = postEndpointUrl(this).toString()

        slackEndpoint.httpPost().header(header)
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

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        Log.d(TAG, event?.keyCode.toString())
        val keyCodeOfZero = KeyEvent.KEYCODE_0
        if (event?.action != KeyEvent.ACTION_DOWN) {
            return super.dispatchKeyEvent(event)
        }

        when (event.keyCode) {
            KeyEvent.KEYCODE_0, KeyEvent.KEYCODE_1, KeyEvent.KEYCODE_2, KeyEvent.KEYCODE_3,
            KeyEvent.KEYCODE_4, KeyEvent.KEYCODE_5, KeyEvent.KEYCODE_6, KeyEvent.KEYCODE_7,
            KeyEvent.KEYCODE_8, KeyEvent.KEYCODE_9 -> {
                binding.viewModel!!.tenKeySubject.onNext(event.keyCode - keyCodeOfZero)
            }
            endOfInputCodes(this) -> {
                binding.viewModel!!.enterKeySubject.onNext(Unit)
            }
            KeyEvent.KEYCODE_DEL -> {
                binding.viewModel!!.bsKeySubject.onNext(Unit)
            }
        }
        return super.dispatchKeyEvent(event)
    }

    private fun isUserCode(input: String): Boolean {
        val userPrefix = userCodePrefix(this)!!
        return userPrefix == input.slice(0 until userPrefix.length)
    }

    private fun showLockerPIN() {
        binding.viewModel!!.message.set("Please open locker by ${lockerPIN(this)}")
        Handler().postDelayed({
            binding.viewModel!!.message.set("message")
        }, 1000 * 5)
    }
}
