package com.dogwood008.kotlinrxsample

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    private lateinit var viewModel: CalcViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(CalcViewModel::class.java)
        displayTextView.text = viewModel.display.toString()
        setEvents()
    }

    private fun setEvents() {
        val keys = mutableMapOf(
                tenKey0Button to 0, tenKey1Button to 1, tenKey2Button to 2,
                tenKey3Button to 3, tenKey4Button to 4, tenKey5Button to 5,
                tenKey6Button to 6, tenKey7Button to 7, tenKey8Button to 8,
                tenKey9Button to 9)
        for (kv in keys) {
            RxView.clicks(kv.key)
                    .flatMap { Observable.fromCallable { kv.value } }
                    .subscribe { appendNum(it) }
        }
    }

    private fun appendNum(num: Int) {
        viewModel.display += num
        RxTextView.text(displayTextView).accept(viewModel.display.toString())
    }

    private fun showToast(text: Any) {
        Toast.makeText(this, text.toString(), Toast.LENGTH_SHORT).show()
        Log.d(TAG, text.toString())
    }
}
