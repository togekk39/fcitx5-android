/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * SPDX-FileCopyrightText: Copyright 2026 Fcitx5 for Android Contributors
 */
package org.fcitx.fcitx5.android.input.keyboard

import android.annotation.SuppressLint
import android.content.Context
import org.fcitx.fcitx5.android.R
import org.fcitx.fcitx5.android.core.InputMethodEntry
import org.fcitx.fcitx5.android.data.theme.Theme
import splitties.views.imageResource

@SuppressLint("ViewConstructor")
class SymbolKeyboard(context: Context, theme: Theme, private val spec: KeyboardLayoutSpec) :
    BaseKeyboard(context, theme, KeyboardLayoutRegistry.symbolLayout(spec)) {
    private val space: TextKeyView by lazy { findViewById(R.id.button_space) }
    private val returnKey: ImageKeyView by lazy { findViewById(R.id.button_return) }

    override fun onInputMethodUpdate(ime: InputMethodEntry) {
        space.mainText.text = spec.spaceLabel
    }

    override fun onReturnDrawableUpdate(returnDrawable: Int) {
        returnKey.img.imageResource = returnDrawable
    }
}
