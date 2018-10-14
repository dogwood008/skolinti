package com.dogwood008.kotlinrxsample

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.content.Context
import android.content.res.Resources
import android.databinding.*
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.daimajia.numberprogressbar.NumberProgressBar
import io.reactivex.subjects.PublishSubject


@BindingAdapter("custom:progress_current")
fun NumberProgressBar.setProgressCurrent(current: Int) {
    this.progress = current
}

@BindingAdapter("custom:drawable_background")
fun View.setDrawableBackground(colorResourceId: Int) {
    val TAG = CalcViewModel::class.java.simpleName
    Log.d(TAG, colorResourceId.toString())
    this.background = this.context.getDrawable(colorResourceId)
}

@BindingAdapter("custom:resource_id")
fun TextView.setResourceId(stringResourceId: Int) {
    val TAG = CalcViewModel::class.java.simpleName
    val string = try {
        resources.getString(stringResourceId)
    } catch (_e: Resources.NotFoundException) {
        ""
    }
    Log.d(TAG, string)
    this.text = string
}

@BindingAdapter("custom:srcCompat")
fun ImageView.srcCompat(resourceId: Int) {
    this.setImageResource(resourceId)
}

typealias ObservableString = ObservableField<String>

class CalcViewModel(@Suppress("UNUSED_PARAMETER") application: Application) :
        AndroidViewModel(Application()), Observable {

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
     * property that changes should be marked with the // @Bindable annotation to
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

    val onTenkeyDown = PublishSubject.create<String>() //MutableLiveData<String>()
    val onEnterKeyDown = PublishSubject.create<Context>()
    val onBSKeyDown = PublishSubject.create<Context>()
    val onTakeAwayState = PublishSubject.create<Unit>()
    val onReturnBackState = PublishSubject.create<Unit>()

    var display = ObservableString("")
    var displayVisibility = ObservableInt(View.GONE)
    var message = ObservableString("Message")
    var messageTextId = ObservableInt(R.string.prompt_select_mode)
    var subMessage = ObservableString("")
    var subMessageTextId = ObservableInt(-1)
    var subMessageVisibility = ObservableInt(View.GONE)
    var bgColorResource = ObservableInt(R.color.white)
    var progress = ObservableInt(100)
    var history = ObservableString("")
    var userId = ObservableString("")
    var explainImageResourceId = ObservableInt(R.drawable.read_user_card_code)
        set(value) {
            field = value
            Log.d(TAG, "explainImageResourceId: $value")
        }

    var state = ObservableString("welcome")
        set(value) {
            field = value
            Log.d(TAG, "state: $value")
        }

    var mode = ObservableString("")
        set(value) {
            field = value
            Log.d(TAG, "mode: $value")
        }

    fun takeAway() {
        onTakeAwayState.onNext(Unit)
    }

    fun returnBack() {
        onReturnBackState.onNext(Unit)
    }

    private fun setState() {
        StatesBase.getInstance(this).call()
    }
}