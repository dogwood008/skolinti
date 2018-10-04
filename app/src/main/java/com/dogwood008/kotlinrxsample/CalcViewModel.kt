package com.dogwood008.kotlinrxsample

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.content.Context
import android.databinding.*
import com.daimajia.numberprogressbar.NumberProgressBar
import io.reactivex.subjects.PublishSubject

@BindingAdapter("custom:progress_current")
fun NumberProgressBar.setProgressCurrent(current: Int) {
    this.progress = current
}

class CalcViewModel(application: Application) : AndroidViewModel(Application()), Observable {
    private var app: Application = application

    // https://developer.android.com/topic/libraries/data-binding/architecture
    private val callbacks: PropertyChangeRegistry = PropertyChangeRegistry()

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.remove(callback)
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.add(callback)
    }

    /**
     * Notifies observers that a specific property has changed. The getter for the
     * property that changes should be marked with the @Bindable annotation to
     * generate a field in the BR class to be used as the fieldId parameter.
     *
     * @param fieldId The generated BR id for the Bindable field.
     */
    fun notifyPropertyChanged(fieldId: Int) {
        callbacks.notifyCallbacks(this, fieldId, null)
    }

    companion object {
        private val TAG = CalcViewModel::class.java.simpleName
        private const val MAX_CODE_LENGTH = 15
    }

    val tenKeySubject = PublishSubject.create<Int>()
    val enterKeySubject = PublishSubject.create<Context>()
    val bsKeySubject = PublishSubject.create<Context>()
    val takeAwaySubject = PublishSubject.create<Context>()
    val returnBackSubject = PublishSubject.create<Context>()

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
    var subMessage = ObservableField<String>("")
        set(value) {
            field = value
            notifyPropertyChanged(BR.subMessage)
        }

    @Bindable
    var state = ObservableField<String>()
        set(value) {
            field = value
            //notifyPropertyChanged(BR.subMessage)
        }

    @Bindable
    var message = ObservableField<String>("Message")
        set(value) {
            field = value
            notifyPropertyChanged(BR.message)
        }

    @Bindable
    var progress = ObservableInt(100)
        set(value) {
            field = value
            notifyPropertyChanged(BR.progress)
        }

    @Bindable
    var history = ObservableField<String>("")
        set(value) {
            field = value
            notifyPropertyChanged(BR.history)
        }

    fun takeAway() {
        takeAwaySubject.onNext(app)
    }

    fun returnBack() {
        returnBackSubject.onNext(app)
    }
}