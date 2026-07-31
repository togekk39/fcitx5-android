/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * SPDX-FileCopyrightText: Copyright 2026 Fcitx5 for Android Contributors
 */
package org.fcitx.fcitx5.android.input.keyboard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class KeyboardLayoutRegistryTest {
    @Test
    fun registeredLayoutsAreDistinctAndComplete() {
        val ids = listOf(
            "keyboard-us", "keyboard-es-419", "keyboard-pt-br",
            "keyboard-fr-qwerty", "keyboard-fa"
        )
        assertEquals(ids.size, ids.map(KeyboardLayoutRegistry::forInputMethod).toSet().size)
    }

    @Test
    fun identifiesRegistryFallbacks() {
        assertTrue(KeyboardLayoutRegistry.isRegisteredInputMethod("keyboard-us"))
        assertTrue(KeyboardLayoutRegistry.isRegisteredInputMethod("keyboard-fa"))
        assertFalse(KeyboardLayoutRegistry.isRegisteredInputMethod("rime"))
        assertFalse(KeyboardLayoutRegistry.isRegisteredInputMethod("anthy"))
    }

    @Test
    fun spanishHasDedicatedEnyeAndRequiredCharacters() {
        val layout = KeyboardLayoutRegistry.Spanish
        assertTrue(layout.rows.flatten().contains("Ñ"))
        assertEquals("Ñ", layout.shifted["Ñ"])
        assertContains(layout, "ñÑáéíóúü¿¡")
    }

    @Test
    fun brazilianPortugueseHasCedillaAndAccents() {
        val layout = KeyboardLayoutRegistry.Portuguese
        assertTrue(layout.rows[1].contains("Ç"))
        assertEquals("Ç", layout.shifted["Ç"])
        assertContains(layout, "çÇáàâãéêíóôõú")
    }

    @Test
    fun frenchIsQwertyAndHasFrenchTypography() {
        val layout = KeyboardLayoutRegistry.French
        assertEquals("QWERTYUIOP", layout.rows.first().joinToString(""))
        assertFalse(layout.rows.first().joinToString("").startsWith("AZERTY"))
        assertContains(layout, "éèêëàâîïôùûüçœ«»’")
    }

    @Test
    fun persianUsesPersianCodePointsAndLocalizedSymbols() {
        val layout = KeyboardLayoutRegistry.Persian
        val primary = layout.rows.flatten().joinToString("")
        assertTrue(layout.rtl)
        assertTrue(primary.contains('\u06cc'))
        assertTrue(primary.contains('\u06a9'))
        assertFalse(primary.contains('\u064a'))
        assertFalse(primary.contains('\u0643'))
        assertTrue(layout.popups[" "]!!.contains("\u200c"))
        assertEquals("۰۱۲۳۴۵۶۷۸۹", layout.numbers.joinToString(""))
        assertContains(layout, "،؛؟")
        assertTrue(layout.shifted.isNotEmpty())
    }

    @Test
    fun switchingRegistryDoesNotLeakState() {
        val sequence = listOf("keyboard-us", "keyboard-fa", "chewing", "keyboard-fr-qwerty", "keyboard-us")
            .map(KeyboardLayoutRegistry::forInputMethod)
        assertEquals(listOf(false, true, false, false, false), sequence.map { it.rtl })
        assertEquals("English QWERTY", sequence.last().name)
    }

    @Test
    fun latinNumberSwipeLayersUseOneThroughZeroOrder() {
        listOf(
            KeyboardLayoutRegistry.English,
            KeyboardLayoutRegistry.Spanish,
            KeyboardLayoutRegistry.Portuguese,
            KeyboardLayoutRegistry.French
        ).forEach { layout ->
            assertEquals("1234567890", layout.numbers.joinToString(""))
        }
    }

    @Test
    fun alphabetRowsWithoutAlternativesDoNotCreateSwipeActions() {
        val rows = KeyboardLayoutRegistry.textLayout(KeyboardLayoutRegistry.English)

        rows.drop(1).take(2).flatten().filterIsInstance<AlphabetKey>().forEach { key ->
            assertTrue(key.punctuation.isEmpty())
            assertFalse(key.behaviors.any { it is KeyDef.Behavior.Swipe })
        }
    }

    @Test
    fun lastAlphabeticRowReservesWidthForCapsAndBackspace() {
        val row = KeyboardLayoutRegistry.textLayout(KeyboardLayoutRegistry.English)[2]

        val totalWidth = row.sumOf { it.appearance.percentWidth.toDouble() }.toFloat()
        assertEquals(1f, totalWidth, 0.0001f)
        assertEquals(0.15f, row.first().appearance.percentWidth, 0f)
        assertEquals(0.15f, row.last().appearance.percentWidth, 0f)
    }

    @Test
    fun bottomRowKeepsCommaUtilityMenuForEveryLayout() {
        listOf(
            KeyboardLayoutRegistry.English,
            KeyboardLayoutRegistry.Spanish,
            KeyboardLayoutRegistry.Portuguese,
            KeyboardLayoutRegistry.French,
            KeyboardLayoutRegistry.Persian
        ).forEach { layout ->
            val comma = KeyboardLayoutRegistry.textLayout(layout).last()[1]
            assertTrue(comma is CommaKey)
            assertTrue(comma.popup.orEmpty().any { it is KeyDef.Popup.Menu })
        }
    }

    @Test
    fun symbolLayoutsKeepBackspaceOnTheLastSymbolRow() {
        listOf(
            KeyboardLayoutRegistry.English,
            KeyboardLayoutRegistry.Spanish,
            KeyboardLayoutRegistry.Portuguese,
            KeyboardLayoutRegistry.French,
            KeyboardLayoutRegistry.Persian
        ).forEach { layout ->
            val row = KeyboardLayoutRegistry.symbolLayout(layout)[layout.symbols.lastIndex]

            assertTrue(row.last() is BackspaceKey)
            assertEquals(1f, row.sumOf { it.appearance.percentWidth.toDouble() }.toFloat(), 0.0001f)
        }
    }

    private fun assertContains(layout: KeyboardLayoutSpec, expected: String) {
        val available = buildString {
            append(layout.rows.flatten().joinToString("").lowercase())
            append(layout.shifted.values.joinToString(""))
            append(layout.popups.values.flatMap(Array<String>::asList).joinToString(""))
            append(layout.symbols.flatten().joinToString(""))
        }
        expected.forEach { assertTrue("Missing $it from ${layout.name}", available.contains(it)) }
    }
}
