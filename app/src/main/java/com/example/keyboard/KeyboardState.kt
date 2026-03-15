package com.example.keyboard

class KeyboardState {
    private var isShiftPressed: Boolean = false
    private var isCapsLockActive: Boolean = false

    // Method to toggle shift state
    fun toggleShift() {
        isShiftPressed = !isShiftPressed
    }

    // Method to set caps lock state
    fun setCapsLock(active: Boolean) {
        isCapsLockActive = active
    }

    // Check if shift is pressed
    fun isShiftPressed(): Boolean {
        return isShiftPressed
    }

    // Check if caps lock is active
    fun isCapsLockActive(): Boolean {
        return isCapsLockActive
    }
}