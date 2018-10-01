package com.dogwood008.kotlinrxsample

import android.databinding.*
import com.daimajia.numberprogressbar.NumberProgressBar
import io.reactivex.subjects.PublishSubject

@BindingAdapter("custom:progress_current")
fun NumberProgressBar.setProgressCurrent(current: Int) {
    this.progress = current
}

class CalcViewModel : BaseObservable() {
    companion object {
        private val TAG = CalcViewModel::class.java.simpleName
        private const val MAX_CODE_LENGTH = 15
    }

    val tenKeySubject = PublishSubject.create<Int>()
    val enterKeySubject = PublishSubject.create<Unit>()
    val bsKeySubject = PublishSubject.create<Unit>()
    val takeAwaySubject = PublishSubject.create<Unit>()
    val returnBackSubject = PublishSubject.create<Unit>()

    @get:Bindable
    var display = ""
        set(value) {
            if (value.length > MAX_CODE_LENGTH) {
                return
            }
            field = value
            notifyPropertyChanged(BR.display)
        }

    @Bindable
    var subDisplay = ObservableField<String>("")

    @Bindable
    val message = ObservableField<String>("Message")

    @Bindable
    val progress = ObservableInt(100)

    @Bindable
    val history = ObservableField<String>("")

    fun takeAway() {
        takeAwaySubject.onNext(Unit)
    }

    fun returnBack() {
        returnBackSubject.onNext(Unit)
    }
}