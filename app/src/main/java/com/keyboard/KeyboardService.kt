package com.keyboard

import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo

class KeyboardService : InputMethodService() {

    private lateinit var keyboardView: KeyboardView
    private var isShiftOn = false
    private var isCtrlOn = false

    override fun onCreateInputView(): View {
        keyboardView = KeyboardView(this)
        keyboardView.onKeyPress = { key -> handleKeyPress(key) }
        keyboardView.onSwipeDown = { handleSwipeDown() }
        return keyboardView
    }

    private fun handleKeyPress(key: String) {
        val ic = currentInputConnection ?: return

        when {
            isCtrlOn -> {
                when (key.lowercase()) {
                    "c" -> sendDownUpKeyEvents(KeyEvent.KEYCODE_COPY)
                    "v" -> sendDownUpKeyEvents(KeyEvent.KEYCODE_PASTE)
                    "x" -> sendDownUpKeyEvents(KeyEvent.KEYCODE_CUT)
                    "z" -> sendDownUpKeyEvents(KeyEvent.KEYCODE_UNDO)
                    "a" -> sendDownUpKeyEvents(KeyEvent.KEYCODE_A)
                    else -> ic.commitText(key, 1)
                }
                isCtrlOn = false
                keyboardView.updateModifierState(isShiftOn, isCtrlOn)
            }
            key == "SHIFT" -> {
                isShiftOn = !isShiftOn
                keyboardView.updateModifierState(isShiftOn, isCtrlOn)
            }
            key == "CTRL" -> {
                isCtrlOn = !isCtrlOn
                keyboardView.updateModifierState(isShiftOn, isCtrlOn)
            }
            key == "DEL" -> ic.deleteSurroundingText(1, 0)
            key == "ENTER" -> ic.commitText("\n", 1)
            key == "SPACE" -> ic.commitText(" ", 1)
            else -> {
                val char = if (isShiftOn) key.uppercase() else key.lowercase()
                ic.commitText(char, 1)
                if (isShiftOn) {
                    isShiftOn = false
                    keyboardView.updateModifierState(isShiftOn, isCtrlOn)
                }
            }
        }
    }

    private fun handleSwipeDown() {
        val ic = currentInputConnection ?: return
        isShiftOn = true
        keyboardView.updateModifierState(isShiftOn, isCtrlOn)
    }
}