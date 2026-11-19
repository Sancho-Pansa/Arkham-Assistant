package io.sanchopansa.arkham.android

import android.content.Context
import io.sanchopansa.arkham.api.JsonSource
import java.io.InputStream

class AndroidJsonSource(private val context: Context) : JsonSource {
    override fun openGameData(filename: String): InputStream = context.assets.open(filename)
}
