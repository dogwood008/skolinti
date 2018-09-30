package com.dogwood008.kotlinrxsample

import android.databinding.*
import android.util.Log
import com.daimajia.numberprogressbar.NumberProgressBar
import io.reactivex.subjects.PublishSubject

@BindingAdapter("custom:progress_current")
fun NumberProgressBar.setProgressCurrent(current: Int) {
    this.progress = current
}

class CalcViewModel : BaseObservable() {
    companion object {
        private val TAG = CalcViewModel::class.java.simpleName
    }

    val tenKeySubject = PublishSubject.create<Int>()
    val enterKeySubject = PublishSubject.create<Unit>()
    val bsKeySubject = PublishSubject.create<Unit>()

    @get:Bindable
    var display = ObservableField<String>("")
        set(value) {
            field = value
            notifyPropertyChanged(BR.display)
        }

    @get:Bindable
    var message = ObservableField<String>("Message")
        set(value) {
            field = value
            notifyPropertyChanged(BR.message)
        }

    @get:Bindable
    var progress = ObservableInt(100)
        set(value) {
            field = value
            notifyPropertyChanged(BR.progress)
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