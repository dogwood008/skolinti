package com.dogwood008.kotlinrxsample

import android.content.Context
import android.preference.PreferenceManager
import android.view.KeyEvent

private val POST_ENDPOINT_URL: String = "admin_endpoint"
private val END_OF_INPUT_CODE: String = "admin_eof_code"
private val ADMIN_PIN: String = "admin_pin"

fun postEndpointUrl(context: Context): String? {
    return PreferenceManager.getDefaultSharedPreferences(context)
            .getString(POST_ENDPOINT_URL, "")
}

fun endOfInputCodes(context: Context): Int {
    return PreferenceManager.getDefaultSharedPreferences(context)
            .getString(END_OF_INPUT_CODE, KeyEvent.KEYCODE_ENTER.toString())!!.toInt()
}

fun adminPIN(context: Context): String? {
    return PreferenceManager.getDefaultSharedPreferences(context)
            .getString(ADMIN_PIN, "0000")
}
