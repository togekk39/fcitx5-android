/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * SPDX-FileCopyrightText: Copyright 2026 Fcitx5 for Android Contributors
 */
package org.fcitx.fcitx5.android.input.keyboard

import android.text.InputType
import org.fcitx.fcitx5.android.core.InputMethodEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ChewingKeyboardTest {
    private fun ime(uniqueName: String, addon: String = "") = InputMethodEntry(
        uniqueName, "", "", "", "", "", addon, false
    )

    @Test
    fun daChenMappingIsCompleteAndUsesAscii() {
        val mapping = ChewingKeyboard.DaChenMapping.flatten().toMap()
        assertEquals(41, mapping.size)
        assertEquals(
            "1234567890-qwertyuiopasdfghjkl;zxcvbnm,./",
            ChewingKeyboard.DaChenMapping.flatten().joinToString("") { it.second }
        )
        assertTrue(mapping.values.all { value -> value.length == 1 && value[0].code < 128 })
        assertEquals("1", mapping["ㄅ"])
        assertEquals("6", mapping["ˊ"])
        assertEquals("7", mapping["˙"])
        assertEquals("/", mapping["ㄥ"])
    }

    @Test
    fun detectsChewingByUniqueNameOrAddon() {
        assertTrue(ChewingKeyboard.isChewing(ime("chewing")))
        assertTrue(ChewingKeyboard.isChewing(ime("variant", "chewing")))
        assertFalse(ChewingKeyboard.isChewing(ime("keyboard-us", "keyboard")))
    }

    @Test
    fun keyboardSelectionPreservesNumericLayouts() {
        val chewing = ime("chewing", "chewing")
        assertEquals(
            ChewingKeyboard.Name,
            KeyboardWindow.selectKeyboard(InputType.TYPE_CLASS_TEXT, chewing)
        )
        assertEquals(
            NumberKeyboard.Name,
            KeyboardWindow.selectKeyboard(InputType.TYPE_CLASS_NUMBER, chewing)
        )
        assertEquals(
            NumberKeyboard.Name,
            KeyboardWindow.selectKeyboard(InputType.TYPE_CLASS_PHONE, chewing)
        )
        assertEquals(
            TextKeyboard.Name,
            KeyboardWindow.selectKeyboard(InputType.TYPE_CLASS_TEXT, ime("keyboard-us"))
        )
    }
}
