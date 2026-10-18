package dev.redfox.obsidian.platform

import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.PersistableBundle

/**
 * Puts [text] on the clipboard under [label]. A [sensitive] clip (a one-time code, a password)
 * is hidden from the system's copy preview and clipboard history.
 */
public fun Context.copyToClipboard(label: String, text: String, sensitive: Boolean = false) {
    val clip = ClipData.newPlainText(label, text)
    if (sensitive) {
        // The constant arrived in Android 13; Android 12 reads the same key.
        val key = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) ClipDescription.EXTRA_IS_SENSITIVE else "android.content.extra.IS_SENSITIVE"
        clip.description.extras = PersistableBundle().apply { putBoolean(key, true) }
    }
    getSystemService(ClipboardManager::class.java).setPrimaryClip(clip)
}
