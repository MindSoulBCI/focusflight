package com.example.picosmokedemo.p7c91e42a

import android.content.Intent
import com.example.picosmokedemo.p7c91e42a.platform.LaunchActivity
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("cn.mindsoul.focusflight", appContext.packageName)
    }
    @Test
    fun launcherRemainsAlive() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val intent = Intent(instrumentation.targetContext, LaunchActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val activity = instrumentation.startActivitySync(intent)
        instrumentation.waitForIdleSync()
        instrumentation.runOnMainSync {
            assertFalse(activity.isFinishing)
            assertFalse(activity.isDestroyed)
            activity.finish()
        }
    }
}
