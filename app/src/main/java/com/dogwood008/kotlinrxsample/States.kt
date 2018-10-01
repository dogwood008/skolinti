package com.dogwood008.kotlinrxsample

import android.content.Context
import android.content.res.Resources
import android.support.v4.content.res.ResourcesCompat
import android.view.View
import com.dogwood008.kotlinrxsample.databinding.HomeFragmentBinding

abstract class StatesBase(context: Context, binding: HomeFragmentBinding) {
    protected val context = context
    protected val binding = binding

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
            binding.root.background = ResourcesCompat.getDrawable(
                    context.resources, value, null)
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
        mainMessage = context.getString(R.string.prompt_scan)
        subMessage = context.getString(R.string.sub_message_user_id)
        binding.root.background = context.resources.getDrawable(R.color.takeAwayBg, null)
    }

    protected fun returnBackMode() {
        takeAwayButtonElevation = 8
        returnBackButtonElevation = 0
        mainMessage = context.getString(R.string.prompt_scan)
        subMessage = context.getString(R.string.sub_message_user_id)
        binding.root.background = context.resources.getDrawable(R.color.returnBackBg, null)
    }

    protected abstract fun call()

    private fun convertDpToPixel(dp: Float): Float {
        val metrics = Resources.getSystem().displayMetrics
        val px = dp * (metrics.densityDpi / 160f)
        return Math.round(px).toFloat()
    }

    class WelcomeStates(context: Context, binding: HomeFragmentBinding) : StatesBase(context, binding) {
        public override fun call() {
            mainMessage = context.getString(R.string.prompt_select_type)
            binding.subMessageTextView.visibility = View.GONE
            binding.displayTextView.visibility = View.GONE
        }
    }

    class TakeAwayStates(context: Context, binding: HomeFragmentBinding) : StatesBase(context, binding) {
        public override fun call() {
            mainMessage = context.getString(R.string.prompt_scan)
            binding.subMessageTextView.visibility = View.VISIBLE
            subMessage = context.getString(R.string.sub_message_user_id)
            binding.displayTextView.visibility = View.VISIBLE
            takeAwayMode()
        }
    }

    class ReturnBackStates(context: Context, binding: HomeFragmentBinding) : StatesBase(context, binding) {
        public override fun call() {
            mainMessage = context.getString(R.string.prompt_scan)
            binding.subMessageTextView.visibility = View.VISIBLE
            subMessage = context.getString(R.string.sub_message_user_id)
            binding.displayTextView.visibility = View.VISIBLE
            returnBackMode()
        }
    }

}