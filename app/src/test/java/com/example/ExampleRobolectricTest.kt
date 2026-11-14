package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.WeightUnit
import com.example.util.DndManager
import com.example.viewmodel.ScaleViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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

    @Test
    fun `test screen lock toggles and blocks unit and hold changes`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val vm = ScaleViewModel(app)

        assertFalse(vm.uiState.value.isScreenLocked)
        assertEquals(WeightUnit.GRAMS, vm.uiState.value.selectedUnit)

        // Enable screen lock
        vm.toggleScreenLock()
        assertTrue(vm.uiState.value.isScreenLocked)

        // Controls are locked while isScreenLocked is true
        vm.setUnit(WeightUnit.OUNCES)
        vm.toggleHold()
        assertEquals(WeightUnit.GRAMS, vm.uiState.value.selectedUnit)
        assertFalse(vm.uiState.value.isHeld)

        // Unlock screen controls
        vm.toggleScreenLock()
        assertFalse(vm.uiState.value.isScreenLocked)

        vm.setUnit(WeightUnit.OUNCES)
        assertEquals(WeightUnit.OUNCES, vm.uiState.value.selectedUnit)
    }

    @Test
    fun `test about me urls match expected destinations`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        assertEquals("https://ko-fi.com/jhor23", context.getString(R.string.about_me_url_kofi))
        assertEquals("https://github.com/JHor23/Weigh-High", context.getString(R.string.about_me_url_github))
        assertEquals("https://github.com/JHor23", context.getString(R.string.about_me_url_projects))
    }
}
