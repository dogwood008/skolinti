package com.dogwood008.kotlinrxsample

import android.content.Context
import android.preference.PreferenceManager

private val POST_ENDPOINT_URL: String = "admin_endpoint"

fun postEndpointUrl(context: Context): String? {
    return PreferenceManager.getDefaultSharedPreferences(context)
            .getString(POST_ENDPOINT_URL, "")
}
