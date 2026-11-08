package io.sanchopansa.arkham.android.ui

import android.content.Context
import android.util.AtomicFile
import org.json.JSONObject
import java.io.File

/** A single atomic, versioned snapshot. Draft skill positions never reach this store. */
class PlayerSessionStore(context: Context, fileName: String = "player-session.json") {
    private val file = AtomicFile(File(context.filesDir, fileName))
    fun read(): JSONObject? {
        if (!file.baseFile.exists() && !File(file.baseFile.path + ".bak").exists()) return null
        val json = JSONObject(file.openRead().bufferedReader().use { it.readText() })
        require(json.getInt("version") == 1) { "Неизвестная версия сохранения" }
        return json
    }

    fun write(snapshot: JSONObject) {
        val stream = file.startWrite()
        try {
            stream.write(snapshot.toString().toByteArray(Charsets.UTF_8))
            file.finishWrite(stream)
        } catch (e: Exception) {
            file.failWrite(stream); throw e
        }
    }
}
