/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * SPDX-FileCopyrightText: Copyright 2026 Fcitx5 for Android Contributors
 */
package org.fcitx.fcitx5.android.input.picker

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PickerDataTest {
    @Test
    fun fullWidthSymbolsAreTheDefaultAndIncludeChinesePunctuation() {
        val (category, symbols) = PickerData.Symbol.first()

        assertEquals("[全]", category.label)
        assertTrue(
            symbols.toSet().containsAll(
                setOf(
                    "「", "」", "『", "』", "（", "）",
                    "〈", "〉", "《", "》", "【", "】"
                )
            )
        )
    }
}
