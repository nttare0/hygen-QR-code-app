package com.example.ui.neumorphic

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView

/**
 * CompositionLocal controlling whether tactile haptic feedback is active globally.
 */
val LocalHapticsEnabled = compositionLocalOf { true }

/**
 * Provides a remembered [TactileHaptics] controller bound to the current Compose [LocalView]
 * and honoring [LocalHapticsEnabled].
 */
@Composable
fun rememberTactileHaptics(): TactileHaptics {
    val context = LocalContext.current
    val view = LocalView.current
    val isEnabled = LocalHapticsEnabled.current
    return remember(context, view, isEnabled) {
        TactileHaptics(
            context = context,
            view = view,
            isEnabledProvider = { isEnabled }
        )
    }
}

/**
 * Tactile haptic helper for subtle physical feedback:
 * - Soft press & release clicks for buttons, keypad keys, and tab items
 * - Crisp switch detents for toggles (Dark mode, Freeze card, Biometric unlock)
 * - Distinct dual-pulse harmonic confirmation for successful transactions & deposits
 */
class TactileHaptics(
    private val context: Context,
    private val view: View,
    private val isEnabledProvider: () -> Boolean = { true }
) {
    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            manager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    fun pressDown() {
        if (!isEnabledProvider()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (vibrator?.hasVibrator() == true) {
                    vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
                } else {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                }
            } else {
                vibrateOneShot(durationMs = 10L, amplitude = 45)
            }
        } catch (_: Exception) {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }
    }

    fun tapClick() {
        if (!isEnabledProvider()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (vibrator?.hasVibrator() == true) {
                    vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
                } else {
                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                }
            } else {
                vibrateOneShot(durationMs = 14L, amplitude = 75)
            }
        } catch (_: Exception) {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        }
    }

    fun toggleSwitch(checked: Boolean) {
        if (!isEnabledProvider()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val feedbackConstant = if (checked) {
                    HapticFeedbackConstants.CONFIRM
                } else {
                    HapticFeedbackConstants.CLOCK_TICK
                }
                val performed = view.performHapticFeedback(feedbackConstant)
                if (!performed) {
                    vibrateOneShot(durationMs = if (checked) 18L else 12L, amplitude = if (checked) 110 else 65)
                }
            } else {
                vibrateOneShot(durationMs = if (checked) 18L else 12L, amplitude = if (checked) 110 else 65)
            }
        } catch (_: Exception) {
            view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
        }
    }

    fun biometricScanPulse() {
        if (!isEnabledProvider()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && vibrator?.hasVibrator() == true) {
                vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))
            } else {
                vibrateOneShot(durationMs = 24L, amplitude = 135)
            }
        } catch (_: Exception) {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
        }
    }

    fun transactionSuccess() {
        if (!isEnabledProvider()) return
        try {
            val vib = vibrator
            if (vib != null && vib.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val timings = longArrayOf(0L, 22L, 65L, 38L)
                    val amplitudes = intArrayOf(0, 95, 0, 175)
                    val effect = if (vib.hasAmplitudeControl()) {
                        VibrationEffect.createWaveform(timings, amplitudes, -1)
                    } else {
                        VibrationEffect.createWaveform(longArrayOf(0L, 20L, 65L, 35L), -1)
                    }
                    vib.vibrate(effect)
                } else {
                    @Suppress("DEPRECATION")
                    vib.vibrate(longArrayOf(0L, 20L, 65L, 35L), -1)
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
            } else {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            }
        } catch (_: Exception) {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        }
    }

    private fun vibrateOneShot(durationMs: Long, amplitude: Int) {
        val vib = vibrator ?: return
        if (!vib.hasVibrator()) {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val safeAmplitude = if (vib.hasAmplitudeControl()) {
                amplitude.coerceIn(1, 255)
            } else {
                VibrationEffect.DEFAULT_AMPLITUDE
            }
            vib.vibrate(VibrationEffect.createOneShot(durationMs, safeAmplitude))
        } else {
            @Suppress("DEPRECATION")
            vib.vibrate(durationMs)
        }
    }
}
