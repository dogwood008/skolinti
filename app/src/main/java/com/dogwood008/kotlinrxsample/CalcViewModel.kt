package com.dogwood008.kotlinrxsample

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.ObservableField
import android.util.Log
import rx.subjects.PublishSubject

class CalcViewModel : BaseObservable() {
    companion object {
        private val TAG = CalcViewModel::class.java.simpleName
    }

    val tenKeySubject = PublishSubject.create<Int>()!!
    val tenKeyObservable = tenKeySubject.asObservable()!!
    val enterKeySubject = PublishSubject.create<Unit>()!!
    val enterKeyObservable = enterKeySubject.asObservable()!!
    val bsKeySubject = PublishSubject.create<Unit>()!!
    val bsKeyObservable = bsKeySubject.asObservable()!!

    @get:Bindable
    var display = ObservableField<String>("")
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