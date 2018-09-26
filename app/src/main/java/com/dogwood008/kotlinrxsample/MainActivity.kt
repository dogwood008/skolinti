package com.dogwood008.kotlinrxsample

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.dogwood008.kotlinrxsample.databinding.ActivityMainBinding

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
                .subscribe()
    }

    private fun showToast(text: Any) {
        Toast.makeText(this, text.toString(), Toast.LENGTH_SHORT).show()
        Log.d(TAG, text.toString())
    }
}
