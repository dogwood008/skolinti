package com.dogwood008.kotlinrxsample

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.ObservableInt
import android.util.Log
import rx.subjects.PublishSubject

class CalcViewModel : BaseObservable() {
    companion object {
        private val TAG = CalcViewModel::class.java.simpleName
    }

    private val tenKeySubject = PublishSubject.create<Int>()
    val tenKeyObservable = tenKeySubject.asObservable()!!
    private val enterKeySubject = PublishSubject.create<Unit>()
    val enterKeyObservable = enterKeySubject.asObservable()!!
    private val bsKeySubject = PublishSubject.create<Unit>()
    val bsKeyObservable = bsKeySubject.asObservable()!!

    @get:Bindable
    var display = ObservableInt(0)
        set(value) {
            field = value
            notifyPropertyChanged(BR.display)
        }

    fun onClickTenKey(num: Int) {
        Log.d(TAG, num.toString())
        tenKeySubject.onNext(num)
    }

    fun onClickEnterKey() {
        enterKeySubject.onNext(Unit)
    }

    fun onClickBSKey() {
        bsKeySubject.onNext(Unit)
    }
}