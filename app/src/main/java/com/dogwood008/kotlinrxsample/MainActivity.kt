package com.dogwood008.kotlinrxsample

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dogwood008.kotlinrxsample.databinding.HomeFragmentBinding
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.squareup.moshi.Moshi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.internal.operators.observable.ObservableJust
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.container, HomeFragment())
                    .commit()
        }
    }

    class HomeFragment : Fragment() {
        private lateinit var binding: HomeFragmentBinding

        companion object {
            private val TAG = HomeFragment::class.java.simpleName
            private const val TYPE_USER = "user"
            private const val TYPE_DEVICE = "device"
            private const val MAX_HISTORY_SIZE = 8
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            Log.d(TAG, "onCreate")
        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            binding = DataBindingUtil.inflate(inflater, R.layout.home_fragment, container, false)
            binding.viewModel = CalcViewModel()
            binding.viewModel!!.message.set(getString(R.string.default_message))
            setEvents()
            val view = binding.root
            view.setOnKeyListener { _view, _keyCode, event -> dispatchKeyEvent(event) }
            Log.d(TAG, "onCreateView")
            return view
        }

        private fun setEvents() {
            binding.viewModel!!.tenKeySubject
                    .doOnNext {
                        val prevValue = binding.viewModel!!.display
                        binding.viewModel!!.display = prevValue + it
                    }
                    .subscribe()
            binding.viewModel!!.bsKeySubject
                    .doOnNext {
                        val prevValue = binding.viewModel!!.display
                        binding.viewModel!!.display = prevValue.slice(0..prevValue.length - 2)
                    }
                    .subscribe()
            binding.viewModel!!.enterKeySubject
                    .doOnNext {
                        val value = binding.viewModel!!.display
                        binding.viewModel!!.display = ""
                        Log.d(TAG, value)
                        when {
                            value == adminPIN(context!!) -> {
                                val settingIntent = Intent(context!!, SettingsActivity::class.java)
                                startActivity(settingIntent)
                            }
                            isUserCode(value) -> {
                                showLockerPIN()
                                appendHistory(value, TYPE_USER)
                                postToSlack(value)
                            }
                            else -> {
                                postToSlack(value)
                                appendHistory(value, TYPE_DEVICE)
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
            val slackEndpoint = postEndpointUrl(context!!).toString()

            slackEndpoint.httpPost().header(header)
                    .body(requestAdapter.toJson(payload)).responseString { _request, _response, result ->
                        when (result) {
                            is Result.Failure -> {
                                Toast.makeText(context!!, result.getException().toString(), Toast.LENGTH_LONG).show()
                            }
                        }
                    }
        }

        private fun showToast(text: Any) {
            Toast.makeText(context!!, text.toString(), Toast.LENGTH_SHORT).show()
            Log.d(TAG, text.toString())
        }

        private fun dispatchKeyEvent(event: KeyEvent?): Boolean {
            Log.d(TAG, event?.keyCode.toString())
            val keyCodeOfZero = KeyEvent.KEYCODE_0
            if (event?.action != KeyEvent.ACTION_DOWN) {
                return false
            }

            when (event.keyCode) {
                KeyEvent.KEYCODE_0, KeyEvent.KEYCODE_1, KeyEvent.KEYCODE_2, KeyEvent.KEYCODE_3,
                KeyEvent.KEYCODE_4, KeyEvent.KEYCODE_5, KeyEvent.KEYCODE_6, KeyEvent.KEYCODE_7,
                KeyEvent.KEYCODE_8, KeyEvent.KEYCODE_9 -> {
                    binding.viewModel!!.tenKeySubject.onNext(event.keyCode - keyCodeOfZero)
                }
                endOfInputCodes(context!!) -> {
                    binding.viewModel!!.enterKeySubject.onNext(Unit)
                }
                KeyEvent.KEYCODE_DEL -> {
                    binding.viewModel!!.bsKeySubject.onNext(Unit)
                }
            }
            return false
        }

        private fun isUserCode(input: String): Boolean {
            val userPrefix = userCodePrefix(context!!)!!
            return userPrefix == input.slice(0 until userPrefix.length)
        }

        private fun showLockerPIN() {
            val lockerPin = lockerPIN(context!!)
            if (lockerPin == null || lockerPin.isEmpty() ||
                    binding.numberProgressBar.visibility == View.VISIBLE) {
                return
            }
            binding.viewModel!!.message.set("Please open locker by $lockerPin")
            binding.viewModel!!.progress.set(100)
            binding.numberProgressBar.visibility = View.VISIBLE

            val disposable = io.reactivex.disposables.SerialDisposable()
            val subscription = io.reactivex.Observable
                    .interval(100, TimeUnit.MILLISECONDS)
                    .take(101)
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMap { ObservableJust(it.toInt()) }
                    .doOnNext {
                        binding.viewModel!!.progress.set(100 - it)
                        if (it >= 100) {
                            binding.viewModel!!.message.set(getString(R.string.default_message))
                            binding.numberProgressBar.visibility = View.INVISIBLE
                            disposable.dispose()
                        }
                    }
                    .subscribe()
            disposable.set(subscription)
        }

        private fun appendHistory(code: String, type: String) {
            var list = binding.viewModel!!.history.get()!!
            when (type) {
                TYPE_USER -> {
                    list = "User: $code\n$list"
                }
                TYPE_DEVICE -> {
                    list = "Device: $code\n$list"
                }
            }

            val splitList = list.split("\n")
            if (splitList.size > MAX_HISTORY_SIZE + 1) {
                list = splitList.slice(0 until MAX_HISTORY_SIZE).joinToString("\n")
            }
            binding.viewModel!!.history.set(list)
        }
    }
}
