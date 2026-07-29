/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * SPDX-FileCopyrightText: Copyright 2026 Fcitx5 for Android Contributors
 */
package org.fcitx.fcitx5.android.input.keyboard

import android.annotation.SuppressLint
import android.content.Context
import org.fcitx.fcitx5.android.R
import org.fcitx.fcitx5.android.core.FcitxKeyMapping
import org.fcitx.fcitx5.android.core.InputMethodEntry
import org.fcitx.fcitx5.android.core.KeyStates
import org.fcitx.fcitx5.android.core.KeySym
import org.fcitx.fcitx5.android.data.theme.Theme
import org.fcitx.fcitx5.android.input.picker.PickerWindow
import splitties.views.imageResource

/** The fixed Standard (DaChen) keyboard used by the Chewing input method. */
@SuppressLint("ViewConstructor")
class ChewingKeyboard(context: Context, theme: Theme) : BaseKeyboard(context, theme, Layout) {
    companion object {
        const val Name = "Chewing"
        const val InputMethodName = "chewing"

        /** DaChen tone keys complete a syllable, after which Chewing needs Down to open choices. */
        internal val ToneKeys = setOf("3", "4", "6", "7")

        val DaChenMapping: List<List<Pair<String, String>>> = listOf(
            listOf(
                "ㄅ" to "1", "ㄉ" to "2", "ˇ" to "3", "ˋ" to "4", "ㄓ" to "5",
                "ˊ" to "6", "˙" to "7", "ㄚ" to "8", "ㄞ" to "9", "ㄢ" to "0", "ㄦ" to "-"
            ),
            listOf(
                "ㄆ" to "q", "ㄊ" to "w", "ㄍ" to "e", "ㄐ" to "r", "ㄔ" to "t",
                "ㄗ" to "y", "ㄧ" to "u", "ㄛ" to "i", "ㄟ" to "o", "ㄣ" to "p"
            ),
            listOf(
                "ㄇ" to "a", "ㄋ" to "s", "ㄎ" to "d", "ㄑ" to "f", "ㄕ" to "g",
                "ㄘ" to "h", "ㄨ" to "j", "ㄜ" to "k", "ㄠ" to "l", "ㄤ" to ";"
            ),
            listOf(
                "ㄈ" to "z", "ㄌ" to "x", "ㄏ" to "c", "ㄒ" to "v", "ㄖ" to "b",
                "ㄙ" to "n", "ㄩ" to "m", "ㄝ" to ",", "ㄡ" to ".", "ㄥ" to "/"
            )
        )

        private fun daChenKey(label: String, ascii: String, width: Float) = KeyDef(
            KeyDef.Appearance.Text(label, textSize = 22f, percentWidth = width),
            setOf(KeyDef.Behavior.Press(KeyAction.FcitxKeyAction(ascii))),
            arrayOf(KeyDef.Popup.Preview(label))
        )

        internal fun punctuationKey(label: String) = KeyDef(
            KeyDef.Appearance.Text(
                label,
                textSize = 23f,
                percentWidth = 0.1f,
                variant = KeyDef.Appearance.Variant.Alternative
            ),
            // Literal commit is intentional: comma, period, and slash are DaChen phonetic
            // keys, so sending their ASCII key events here would enter ㄝ, ㄡ, or ㄥ.
            setOf(KeyDef.Behavior.Press(KeyAction.CommitAction(label))),
            arrayOf(KeyDef.Popup.Preview(label))
        )

        val Layout: List<List<KeyDef>> = DaChenMapping.map { row ->
            val width = 1f / row.size
            row.map { (label, ascii) -> daChenKey(label, ascii, width) }
        } + listOf(
            listOf(
                LayoutSwitchKey("?123", NumberKeyboard.Name, percentWidth = 0.15f),
                punctuationKey("，"),
                LanguageKey(),
                SpaceKey(),
                punctuationKey("。"),
                LayoutSwitchKey("符", PickerWindow.Key.Symbol.name, percentWidth = 0.1f),
                BackspaceKey(),
                ReturnKey()
            )
        )

        fun isChewing(ime: InputMethodEntry): Boolean =
            ime.uniqueName == InputMethodName || ime.addon == InputMethodName
    }

    private val space: TextKeyView by lazy { findViewById(R.id.button_space) }
    private val returnKey: ImageKeyView by lazy { findViewById(R.id.button_return) }

    override fun onAction(action: KeyAction, source: KeyActionListener.Source) {
        super.onAction(action, source)
        if (source == KeyActionListener.Source.Keyboard &&
            action is KeyAction.FcitxKeyAction && action.act in ToneKeys
        ) {
            // libchewing does not publish its alternatives immediately after a tone. Down
            // enters candidate selection; that candidate-list update then follows the same
            // InputBroadcaster -> KawaiiBar -> HorizontalCandidate path as every other IME.
            super.onAction(
                KeyAction.SymAction(
                    KeySym(FcitxKeyMapping.FcitxKey_Down),
                    KeyStates.Virtual
                ),
                source
            )
        }
    }

    override fun onInputMethodUpdate(ime: InputMethodEntry) {
        space.mainText.text = ime.displayName
    }

    override fun onReturnDrawableUpdate(returnDrawable: Int) {
        returnKey.img.imageResource = returnDrawable
    }
}
