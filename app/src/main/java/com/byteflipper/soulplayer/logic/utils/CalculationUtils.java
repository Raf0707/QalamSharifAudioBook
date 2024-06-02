package com.byteflipper.soulplayer.logic.utils;

import android.graphics.Color;

import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CalculationUtils {

    /**
     * [convertDurationToTimeStamp] makes a string format
     * of duration (presumably long) converts into timestamp
     * like 300 to 5:00.
     *
     * @param duration Duration in milliseconds.
     * @return Timestamp string in the format "mm:ss".
     */
    public static String convertDurationToTimeStamp(long duration) {
        long minutes = duration / 1000 / 60;
        long seconds = duration / 1000 - minutes * 60;
        if (seconds < 10) {
            return minutes + ":0" + seconds;
        }
        return minutes + ":" + seconds;
    }

    /**
     * convertUnixTimestampToMonthDay:
     *   Converts unix timestamp to Month - Day format.
     *
     * @param unixTimestamp Unix timestamp in seconds.
     * @return Date string in the format "MM-dd".
     */
    public static String convertUnixTimestampToMonthDay(long unixTimestamp) {
        return new SimpleDateFormat(
                "MM-dd",
                Locale.getDefault()
        ).format(
                new Date(unixTimestamp * 1000)
        );
    }

    /**
     * Set the alpha component of `color` to be `alpha`.
     *
     * @param color The color to adjust.
     * @param alpha The new alpha component (0-255).
     * @return The color with the adjusted alpha component.
     */
    @ColorInt
    public static int setAlphaComponent(
            @ColorInt int color,
            @IntRange(from = 0x0, to = 0xFF) int alpha
    ) {
        if (alpha < 0 || alpha > 255) {
            throw new IllegalArgumentException("alpha must be between 0 and 255.");
        }
        return (color & 0x00ffffff) | (alpha << 24);
    }

    private static float constrain(float amount, float low, float high) {
        return amount < low ? low : Math.min(amount, high);
    }

    public static float lerp(float start, float stop, float amount) {
        return start + (stop - start) * amount;
    }

    /**
     * Returns the interpolation scalar (s) that satisfies the equation:
     * `value = lerp(a, b, s)`
     *
     * If `a == b`, then this function will return 0.
     *
     * @param a     The starting value.
     * @param b     The ending value.
     * @param value The interpolated value.
     * @return The interpolation scalar.
     */
    public static float lerpInv(float a, float b, float value) {
        return a != b ? (value - a) / (b - a) : 0.0f;
    }

    /**
     * Returns the single argument constrained between [0.0, 1.0].
     *
     * @param value The value to constrain.
     * @return The constrained value.
     */
    private static float saturate(float value) {
        return constrain(value, 0.0f, 1.0f);
    }

    /**
     * Returns the saturated (constrained between [0, 1]) result of [lerpInv].
     *
     * @param a     The starting value.
     * @param b     The ending value.
     * @param value The interpolated value.
     * @return The saturated interpolation scalar.
     */
    public static float lerpInvSat(float a, float b, float value) {
        return saturate(lerpInv(a, b, value));
    }
}