package com.example.chartapplication.utils

import android.content.Context

/**
 * Reading json from assets
 *
 * @return String value with json
 */
fun Context.jsonFromAssets(fileName: String) : String {
    val inputStream = assets.open(fileName)
    val size = inputStream.available()
    val buffer = ByteArray(size)
    inputStream.read(buffer)
    inputStream.close()
    return String(buffer, Charsets.UTF_8)
}