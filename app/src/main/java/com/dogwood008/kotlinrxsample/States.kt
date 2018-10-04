package com.dogwood008.kotlinrxsample

import android.content.res.Resources
import android.view.View
import com.dogwood008.kotlinrxsample.databinding.HomeFragmentBinding

abstract class StatesBase(protected val binding: HomeFragmentBinding) {

    companion object {
        fun createFromString(binding: HomeFragmentBinding): StatesBase {
            val state = binding.viewModel!!.state.get()
            when (state) {
                "welcome" -> {
                    return WelcomeStates(binding)
                }
                "takeAway" -> {
                    return TakeAwayStates(binding)
                }
                "returnBack" -> {
                    return ReturnBackStates(binding)
                }
            }
            return WelcomeStates(binding)
        }
    }

    var mainMessage: String
        set(value) {
            binding.viewModel!!.message.set(value)
        }
        get() = binding.viewModel!!.message.get()!!

    var subMessage: String
        set(value) {
            binding.viewModel!!.subMessage.set(value)
        }
        get() = binding.viewModel!!.subMessage.get()!!

    var bgColor: Int = 0
        set(value) {
            binding.viewModel!!.bgColorResource.set(value)
        }

    var takeAwayButtonElevation: Int
        set(value) {
            binding.takeAwayButton.elevation = convertDpToPixel(value.toFloat())
        }
        get() = binding.takeAwayButton.elevation.toInt()

    var returnBackButtonElevation: Int
        set(value) {
            binding.returnBackButton.elevation = convertDpToPixel(value.toFloat())
        }
        get() = binding.returnBackButton.elevation.toInt()

    protected fun takeAwayMode() {
        takeAwayButtonElevation = 0
        returnBackButtonElevation = 8
        mainMessage = binding.messageTextView.resources.getString(R.string.prompt_scan)
        subMessage = binding.subMessageTextView.resources.getString(R.string.sub_message_user_id)
        bgColor = R.color.takeAwayBg
    }

    protected fun returnBackMode() {
        takeAwayButtonElevation = 8
        returnBackButtonElevation = 0
        mainMessage = binding.messageTextView.resources.getString(R.string.prompt_scan)
        subMessage = binding.subMessageTextView.resources.getString(R.string.sub_message_user_id)
        bgColor = R.color.returnBackBg
    }

    abstract fun call()

    private fun convertDpToPixel(dp: Float): Float {
        val metrics = Resources.getSystem().displayMetrics
        val px = dp * (metrics.densityDpi / 160f)
        return Math.round(px).toFloat()
    }

    class WelcomeStates(binding: HomeFragmentBinding) : StatesBase(binding) {
        public override fun call() {
            mainMessage = binding.messageTextView.resources.getString(R.string.prompt_select_type)
            binding.subMessageTextView.visibility = View.GONE
            binding.displayTextView.visibility = View.GONE
        }
    }

    class TakeAwayStates(binding: HomeFragmentBinding) : StatesBase(binding) {
        public override fun call() {
            mainMessage = binding.messageTextView.resources.getString(R.string.prompt_scan)
            binding.subMessageTextView.visibility = View.VISIBLE
            subMessage = binding.subMessageTextView.resources.getString(R.string.sub_message_user_id)
            binding.displayTextView.visibility = View.VISIBLE
            takeAwayMode()
            binding.viewModel!!.state.set("takeAway")
        }
    }

    class ReturnBackStates(binding: HomeFragmentBinding) : StatesBase(binding) {
        public override fun call() {
            mainMessage = binding.messageTextView.resources.getString(R.string.prompt_scan)
            binding.subMessageTextView.visibility = View.VISIBLE
            subMessage = binding.subMessageTextView.resources.getString(R.string.sub_message_user_id)
            binding.displayTextView.visibility = View.VISIBLE
            returnBackMode()
            binding.viewModel!!.state.set("returnBack")
        }
    }

}