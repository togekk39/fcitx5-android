/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * SPDX-FileCopyrightText: Copyright 2026 Fcitx5 for Android Contributors
 */
package org.fcitx.fcitx5.android.input.keyboard

import org.fcitx.fcitx5.android.input.keyboard.KeyDef.Appearance.Variant
import org.fcitx.fcitx5.android.input.picker.PickerWindow

data class KeyboardLayoutSpec(
    val name: String,
    val rows: List<List<String>>,
    val shifted: Map<String, String>,
    val popups: Map<String, Array<String>>,
    val numbers: List<String>,
    val symbols: List<List<String>>,
    val rtl: Boolean,
    val spaceLabel: String,
    val shiftedIsCase: Boolean = true,
    val punctuation: Pair<String, String> = "," to "."
) {
    val symbolLayoutName = "$name Symbols"
}

/** Declarative layouts used by every input method provided by androidkeyboard. */
object KeyboardLayoutRegistry {
    private fun latin(
        name: String,
        secondRow: List<String> = "ASDFGHJKL".map(Char::toString),
        popups: Map<String, Array<String>>,
        spaceLabel: String,
        symbols: List<List<String>>
    ) = KeyboardLayoutSpec(
        name = name,
        rows = listOf(
            "QWERTYUIOP".map(Char::toString),
            secondRow,
            "ZXCVBNM".map(Char::toString)
        ),
        shifted = (('A'..'Z').associate { it.toString() to it.toString() }) +
            secondRow.associateWith { it.uppercase() },
        popups = popups,
        numbers = (1..9).map(Int::toString) + "0",
        symbols = symbols,
        rtl = false,
        spaceLabel = spaceLabel
    )

    val English = latin(
        "English QWERTY",
        popups = emptyMap(),
        spaceLabel = "English",
        symbols = listOf(
            listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"),
            listOf("@", "#", "$", "%", "&", "-", "+", "(", ")"),
            listOf("*", "\"", "'", ":", ";", "!", "?")
        )
    )

    val Spanish = latin(
        "Latin American Spanish QWERTY",
        secondRow = "ASDFGHJKL".map(Char::toString) + "Ñ",
        popups = mapOf(
            "A" to arrayOf("á"), "E" to arrayOf("é"), "I" to arrayOf("í"),
            "O" to arrayOf("ó"), "U" to arrayOf("ú", "ü"),
            "?" to arrayOf("¿"), "!" to arrayOf("¡")
        ),
        spaceLabel = "Español",
        symbols = listOf(
            listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"),
            listOf("@", "#", "$", "%", "&", "-", "+", "(", ")"),
            listOf("¿", "?", "¡", "!", "\"", "'", ":", ";")
        )
    ).copy(shifted = mapOf("Ñ" to "Ñ"))

    val Portuguese = latin(
        "Brazilian Portuguese QWERTY",
        secondRow = "ASDFGHJKL".map(Char::toString) + "Ç",
        popups = mapOf(
            "A" to arrayOf("á", "à", "â", "ã"), "E" to arrayOf("é", "ê"),
            "I" to arrayOf("í"), "O" to arrayOf("ó", "ô", "õ"),
            "U" to arrayOf("ú", "ü"), "C" to arrayOf("ç")
        ),
        spaceLabel = "Português",
        symbols = English.symbols
    ).copy(shifted = mapOf("Ç" to "Ç"))

    val French = latin(
        "French QWERTY",
        popups = mapOf(
            "A" to arrayOf("à", "â", "ä"), "E" to arrayOf("é", "è", "ê", "ë"),
            "I" to arrayOf("î", "ï"), "O" to arrayOf("ô", "ö", "œ"),
            "U" to arrayOf("ù", "û", "ü"), "C" to arrayOf("ç"),
            "Y" to arrayOf("ÿ"), "\"" to arrayOf("«", "»"), "'" to arrayOf("’")
        ),
        spaceLabel = "Français",
        symbols = listOf(
            English.symbols[0], English.symbols[1],
            listOf("«", "»", "\"", "'", "’", ":", ";", "!", "?")
        )
    )

    val Persian = KeyboardLayoutSpec(
        name = "Persian Standard",
        rows = listOf(
            listOf("ض", "ص", "ث", "ق", "ف", "غ", "ع", "ه", "خ", "ح", "ج", "چ"),
            listOf("ش", "س", "ی", "ب", "ل", "ا", "ت", "ن", "م", "ک", "گ"),
            listOf("ظ", "ط", "ز", "ر", "ذ", "د", "پ", "و")
        ),
        shifted = mapOf(
            "ض" to "ْ", "ص" to "ٌ", "ث" to "ٍ", "ق" to "ً", "ف" to "ُ",
            "غ" to "ِ", "ع" to "َ", "ه" to "ّ", "خ" to "[", "ح" to "]",
            "ج" to "}", "چ" to "{", "ش" to "ؤ", "س" to "ئ", "ی" to "ى",
            "ب" to "إ", "ل" to "أ", "ا" to "آ", "ت" to "ة", "ن" to "»",
            "م" to "«", "ک" to ":", "گ" to "؛", "ظ" to "ۀ", "ط" to "ٓ",
            "ز" to "ژ", "ر" to "ٰ", "ذ" to "ٔ", "د" to "ء", "پ" to "ـ", "و" to "،"
        ),
        popups = mapOf(
            " " to arrayOf("\u200C"),
            "۰" to arrayOf("0"), "۱" to arrayOf("1"), "۲" to arrayOf("2"),
            "۳" to arrayOf("3"), "۴" to arrayOf("4"), "۵" to arrayOf("5"),
            "۶" to arrayOf("6"), "۷" to arrayOf("7"), "۸" to arrayOf("8"),
            "۹" to arrayOf("9")
        ),
        numbers = listOf("۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹", "۰"),
        symbols = listOf(
            listOf("۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹", "۰"),
            listOf("@", "#", "٪", "&", "-", "+", "(", ")"),
            listOf("،", "؛", "؟", "!", "«", "»", "نیم‌فاصله")
        ),
        rtl = true,
        spaceLabel = "فارسی",
        shiftedIsCase = false,
        punctuation = "،" to "؟"
    )

    private val byInputMethod = mapOf(
        "keyboard-us" to English,
        "keyboard-es-419" to Spanish,
        "keyboard-pt-br" to Portuguese,
        "keyboard-fr-qwerty" to French,
        "keyboard-fa" to Persian
    )

    fun isRegisteredInputMethod(uniqueName: String) = byInputMethod.containsKey(uniqueName)

    fun forInputMethod(uniqueName: String) = byInputMethod[uniqueName] ?: English

    fun textLayout(spec: KeyboardLayoutSpec): List<List<KeyDef>> {
        val rows: MutableList<List<KeyDef>> = spec.rows.mapIndexed { rowIndex, row ->
            val letterWidth =
                if (rowIndex == spec.rows.lastIndex) 0.7f / row.size else 1f / row.size
            val keys = row.mapIndexed { index, character ->
                val alt = if (rowIndex == 0) spec.numbers.getOrNull(index).orEmpty() else ""
                layoutKey(character, alt, spec.popups[character.uppercase()], letterWidth)
            }.toMutableList<KeyDef>()
            if (rowIndex == spec.rows.lastIndex) {
                keys.add(0, CapsKey())
                keys.add(BackspaceKey())
            }
            keys
        }.toMutableList()
        rows += listOf(
            LayoutSwitchKey("?123", spec.symbolLayoutName),
            CommaKey(
                percentWidth = 0.1f,
                variant = Variant.Alternative,
                displayText = spec.punctuation.first
            ),
            LanguageKey(), SpaceKey(spec.spaceLabel),
            layoutKey(spec.punctuation.second, "", spec.popups[spec.punctuation.second]), ReturnKey()
        )
        return rows.inLayoutDirection(spec)
    }

    fun symbolLayout(spec: KeyboardLayoutSpec): List<List<KeyDef>> =
        (spec.symbols.mapIndexed { rowIndex, row ->
            val symbolWidth =
                if (rowIndex == spec.symbols.lastIndex) 0.85f / row.size else 0.1f
            row.map { value ->
                val output = if (value == "نیم‌فاصله") "\u200C" else value
                val popup = spec.popups[value]?.let { KeyDef.Popup.Keyboard.Explicit(it) }
                    ?: KeyDef.Popup.Keyboard.Preset(value)
                SymbolKey(
                    output,
                    percentWidth = symbolWidth,
                    popup = arrayOf(KeyDef.Popup.Preview(value), popup)
                ).let { key ->
                    if (output == value) key else LabeledSymbolKey(value, output, symbolWidth)
                }
            } + if (rowIndex == spec.symbols.lastIndex) listOf(BackspaceKey()) else emptyList()
        } + listOf(
            listOf(
                LayoutSwitchKey("ABC", TextKeyboard.Name), LanguageKey(),
                LayoutSwitchKey("!?#", PickerWindow.Key.Symbol.name),
                SpaceKey(spec.spaceLabel), ReturnKey()
            )
        )).inLayoutDirection(spec)

    private fun List<List<KeyDef>>.inLayoutDirection(spec: KeyboardLayoutSpec) =
        if (spec.rtl) map(List<KeyDef>::reversed) else this

    private fun layoutKey(
        character: String,
        alt: String,
        popup: Array<String>?,
        percentWidth: Float = 0.1f
    ): KeyDef =
        AlphabetKey(
            character, alt, percentWidth = percentWidth,
            popup = arrayOf(
                if (alt.isEmpty()) KeyDef.Popup.Preview(character)
                else KeyDef.Popup.AltPreview(character, alt),
                popup?.let { KeyDef.Popup.Keyboard.Explicit(it) }
                    ?: KeyDef.Popup.Keyboard.Preset(character)
            )
        )
}

class LabeledSymbolKey(label: String, output: String, percentWidth: Float = 0.1f) : KeyDef(
    KeyDef.Appearance.Text(label, 16f, percentWidth = percentWidth, variant = Variant.Normal),
    setOf(KeyDef.Behavior.Press(KeyAction.FcitxKeyAction(output))),
    arrayOf(KeyDef.Popup.Preview(label))
)
