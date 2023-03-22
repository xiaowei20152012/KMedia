package com.media.learn.util

import android.view.Window
import androidx.core.view.WindowCompat


object Util {
    /**
     * Sets whether the decor view should fit root-level content views for
     * {@link WindowInsetsCompat}.
     * <p>
     * If set to {@code false}, the framework will not fit the content view to the insets and will
     * just pass through the {@link WindowInsetsCompat} to the content view.
     * </p>
     * <p>
     * Please note: using the {@link View#setSystemUiVisibility(int)} API in your app can
     * conflict with this method. Please discontinue use of {@link View#setSystemUiVisibility(int)}.
     * </p>
     *
     * @param window                 The current window.
     * @param decorFitsSystemWindows Whether the decor view should fit root-level content views for
     *                               insets.
     */
    fun setDecorFitsSystemWindows(
        window: Window,
        decorFitsSystemWindows: Boolean
    ) {
        WindowCompat.setDecorFitsSystemWindows(window, decorFitsSystemWindows)
    }
}