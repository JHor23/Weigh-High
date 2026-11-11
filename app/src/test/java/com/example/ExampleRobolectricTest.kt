package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.WeightUnit
import com.example.util.DndManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Weigh High", appName)
    }

    @Test
    fun `test weight unit formatting and conversion`() {
        val massGrams = 5.00f
        assertEquals("5.00", WeightUnit.GRAMS.format(massGrams))
        assertEquals("25.00", WeightUnit.CARATS.format(massGrams))
        assertEquals("5000", WeightUnit.MILLIGRAMS.format(massGrams))
        assertTrue(WeightUnit.OUNCES.format(massGrams).startsWith("0.17"))
    }

    @Test
    fun `test dnd manager intent`() {
        val intent = DndManager.getDndSettingsIntent()
        assertNotNull(intent.action)
    }

    @Test
    fun `test portrait orientation locked in manifest`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val pm = context.packageManager
        val activityInfo = pm.getActivityInfo(
            android.content.ComponentName(context, MainActivity::class.java),
            0
        )
        assertEquals(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, activityInfo.screenOrientation)
    }
}
