package com.example.picosmokedemo.p7c91e42a.data

import android.os.SystemClock
import com.example.picosmokedemo.p7c91e42a.domain.FlightEngine

/** Integration boundary for a future official BrainLink SDK callback. No BLE protocol is assumed. */
class BrainLinkAdapter(private val engine:FlightEngine) {
 fun onAttention(percent:Float, normalizedSignalQuality:Float):Boolean =
  engine.submitBrainLink(percent, normalizedSignalQuality, SystemClock.elapsedRealtime())
 fun onDisconnected()=engine.disconnect()
}
