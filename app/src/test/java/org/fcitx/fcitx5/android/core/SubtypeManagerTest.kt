/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * SPDX-FileCopyrightText: Copyright 2026 Fcitx5 for Android Contributors
 */

package org.fcitx.fcitx5.android.core

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SubtypeManagerTest {

    @Test
    fun `latin keyboard subtypes are ASCII capable`() {
        assertTrue(SubtypeManager.isAsciiCapable("keyboard-us"))
        assertTrue(SubtypeManager.isAsciiCapable("keyboard-es-419"))
        assertTrue(SubtypeManager.isAsciiCapable("keyboard-pt-br"))
        assertTrue(SubtypeManager.isAsciiCapable("keyboard-fr-qwerty"))
    }

    @Test
    fun `Persian and non-keyboard subtypes are not ASCII capable`() {
        assertFalse(SubtypeManager.isAsciiCapable("keyboard-fa"))
        assertFalse(SubtypeManager.isAsciiCapable("rime"))
    }
}
