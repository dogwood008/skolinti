package com.dogwood008.kotlinrxsample

import android.content.res.Resources
import android.util.Log
import android.view.View

abstract class StatesBase(protected val viewModel: CalcViewModel) {

    companion object {
        private val TAG = StatesBase::class.java.simpleName
        fun getInstance(viewModel: CalcViewModel): StatesBase {
            val state = viewModel.state.get()
            Log.d(TAG, state)
            when (state) {
                WelcomeStates.STATE_NAME -> {
                    return WelcomeStates(viewModel)
                }
                UserIDScanPromptStates.STATE_NAME -> {
                    val mode = viewModel.mode.get()!!
                    Log.d(TAG, mode)
                    return UserIDScanPromptStates(viewModel, mode)
                }
            }
            return WelcomeStates(viewModel)
        }
    }

    var mainMessage: String
        set(value) {
            viewModel.message.set(value)
        }
        get() = viewModel.message.get()!!

    var mainMessageTextId: Int
        set(value) {
            viewModel.messageTextId.set(value)
        }
        get() = viewModel.messageTextId.get()

    var subMessageTextId: Int
        set(value) {
            viewModel.subMessageTextId.set(value)
        }
        get() = viewModel.subMessageTextId.get()

    var subMessageVisibility: Int
        set(value) {
            viewModel.subMessageVisibility.set(value)
        }
        get() = viewModel.subMessageVisibility.get()

    var bgColor: Int = 0
        set(value) {
            viewModel.bgColorResource.set(value)
        }

    var displayVisibility: Int
        set(value) {
            viewModel.displayVisibility.set(value)
        }
        get() = viewModel.displayVisibility.get()


    var takeAwayButtonElevation: Int = 0
        set(value) {
            // FIXME:
            //   binding.takeAwayButton.elevation = convertDpToPixel(value.toFloat())
        }
    //get(){field} = binding.takeAwayButton.elevation.toInt()

    var returnBackButtonElevation: Int = 0
        set(value) {
            // FIXME:
            //binding.returnBackButton.elevation = convertDpToPixel(value.toFloat())
        }
    //get() = binding.returnBackButton.elevation.toInt()

    var state: String
        set(value) {
            viewModel.state.set(value)
        }
        get() {
            return viewModel.state.get()!!
        }

    var mode: String
        set(value) {
            viewModel.mode.set(value)
        }
        get() {
            return viewModel.mode.get()!!
        }

    var explainImageResourceId: Int
        set(value) {
            viewModel.explainImageResourceId.set(value)
        }
        get() {
            return viewModel.explainImageResourceId.get()
        }

    protected fun takeAwayMode() {
        takeAwayButtonElevation = 0
        returnBackButtonElevation = 8
        mainMessage = ""//FIXME: binding.messageTextView.resources.getString(R.string.prompt_scan)
        bgColor = R.color.takeAwayBg
    }

    protected fun returnBackMode() {
        takeAwayButtonElevation = 8
        returnBackButtonElevation = 0
        mainMessage = ""//FIXME: binding.messageTextView.resources.getString(R.string.prompt_scan)
        bgColor = R.color.returnBackBg
    }

    abstract fun call()

    abstract fun state(): String

    private fun convertDpToPixel(dp: Float): Float {
        val metrics = Resources.getSystem().displayMetrics
        val px = dp * (metrics.densityDpi / 160f)
        return Math.round(px).toFloat()
    }

    class WelcomeStates(viewModel: CalcViewModel, setValue: Boolean = false) : StatesBase(viewModel) {
        companion object {
            const val STATE_NAME = "welcome"
        }

        override fun state(): String {
            return STATE_NAME
        }

        init {
            if (setValue) {
                super.state = state()
            }
        }

        override fun call() {
            mainMessageTextId = R.string.prompt_select_mode
            subMessageVisibility = View.GONE
            displayVisibility = View.GONE
            explainImageResourceId = R.drawable.ic_launcher_foreground
        }
    }

    class UserIDScanPromptStates(viewModel: CalcViewModel,
                                 mode: String = "takeAway",
                                 setValue: Boolean = false) : StatesBase(viewModel) {
        companion object {
            const val STATE_NAME = "userIdScanPromptStates"
            const val MODE_TAKE_AWAY = "takeAway"
            const val MODE_RETURN_BACK = "returnBack"
        }

        override fun state(): String {
            return STATE_NAME
        }

        init {
            if (setValue) {
                super.state = state()
                super.mode = mode
            }
        }

        override fun call() {
            mainMessageTextId = R.string.prompt_user_id_scan
            subMessageTextId = R.string.sub_message_user_id
            subMessageVisibility = View.VISIBLE
            displayVisibility = View.VISIBLE
            explainImageResourceId = R.drawable.read_user_card_code
            when (mode) {
                UserIDScanPromptStates.MODE_TAKE_AWAY -> {
                    takeAwayMode()
                }
                UserIDScanPromptStates.MODE_RETURN_BACK -> {
                    returnBackMode()
                }
                else -> {
                    throw RuntimeException("Invalid mode given: $mode")
                }
            }
        }
    }
}