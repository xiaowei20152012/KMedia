/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.media.learn.util

import android.app.Activity
import android.util.SparseIntArray
import androidx.annotation.IdRes
import androidx.annotation.StyleRes
import com.media.learn.util.ThemeUtils.applyThemeOverlay

/** Utils for theme themeOverlays.  */
object ThemeOverlayUtils {
    const val NO_THEME_OVERLAY = 0
    private val themeOverlays = SparseIntArray()
    fun setThemeOverlay(@IdRes id: Int, @StyleRes themeOverlay: Int) {
        if (themeOverlay == NO_THEME_OVERLAY) {
            themeOverlays.delete(id)
        } else {
            themeOverlays.put(id, themeOverlay)
        }
    }

    fun clearThemeOverlay(@IdRes id: Int) {
        themeOverlays.delete(id)
    }

    fun clearThemeOverlays(activity: Activity) {
        themeOverlays.clear()
        activity.recreate()
    }

    fun getThemeOverlay(@IdRes id: Int): Int {
        return themeOverlays[id]
    }

    fun applyThemeOverlays(activity: Activity?) {
        for (i in 0 until themeOverlays.size()) {
            applyThemeOverlay(activity!!, themeOverlays.valueAt(i))
        }
    }
}