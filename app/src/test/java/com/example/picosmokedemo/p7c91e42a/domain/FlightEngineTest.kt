package com.example.picosmokedemo.p7c91e42a.domain
import org.junit.Assert.*
import org.junit.Test
class FlightEngineTest {
 @Test fun focusRaisesSpeed(){
  val e=FlightEngine();e.setFocus(100f);e.toggleFlight()
  repeat(60){e.tick(.016f,it*16L)}
  assertTrue(e.state.gain>1.9f);assertTrue(e.state.speed>.47f)
 }
 @Test fun staleExternalRemovesBoost(){
  val e=FlightEngine();e.toggleSource();assertTrue(e.submitBrainLink(100f,1f,0))
  repeat(60){e.tick(.016f,it*16L)}
  assertTrue(e.state.gain>1.9f);e.tick(.016f,2100)
  assertNull(e.state.focus);assertEquals(1f,e.state.gain,0f)
 }
 @Test fun invalidAndOldPacketsRejected(){
  val e=FlightEngine();assertFalse(e.submitBrainLink(Float.NaN,1f,0))
  assertFalse(e.submitBrainLink(101f,1f,0));assertFalse(e.submitBrainLink(80f,.1f,0))
  assertTrue(e.submitBrainLink(80f,1f,20));assertFalse(e.submitBrainLink(90f,1f,19))
 }
 @Test fun boundaryStopsFlight(){
  val e=FlightEngine();e.turn(35f);e.toggleFlight();repeat(1000){e.tick(.05f,it*50L)}
  assertEquals(-4.5f,e.state.z,0f);assertFalse(e.state.flying);assertEquals(0f,e.state.speed,0f)
 }
 @Test fun dragPausesAndClamps(){
  val e=FlightEngine();e.toggleFlight();e.move(50f,50f,50f)
  assertFalse(e.state.flying);assertEquals(1.5f,e.state.x,0f);assertEquals(2.4f,e.state.y,0f);assertEquals(-1.5f,e.state.z,0f)
 }
 @Test fun resumeDoesNotJumpAfterLongFrame(){
  val e=FlightEngine();e.toggleFlight();e.tick(100f,100000)
  assertTrue(e.state.z> -2.53f)
 }
 @Test fun sourceSwitchClearsPreviousSample(){
  val e=FlightEngine();e.submitBrainLink(99f,1f,1);e.toggleSource();e.tick(.05f,2)
  assertNull(e.state.focus);assertEquals("BrainLink 未连接",e.state.source)
 }
 @Test fun resetStopsAndRecenters(){
  val e=FlightEngine();e.move(1f,1f,1f);e.turn(90f);e.toggleFlight();e.reset()
  assertEquals(FlightState(),e.state)
 }
 @Test fun modalPausesAndBlocksFlightAndDragging(){
  val e=FlightEngine();e.toggleFlight();e.chooseInteraction(true);val before=e.state
  e.toggleFlight();e.drag(1f,0f,0f)
  assertFalse(e.state.flying);assertEquals(before.x,e.state.x,0f)
 }
 @Test fun buttonsModeDisablesDragAndFocusBoost(){
  val e=FlightEngine();e.setInteraction(InteractionMode.BUTTONS);e.setFocus(100f)
  e.drag(1f,0f,0f);e.toggleFlight();repeat(20){e.tick(.05f,it*50L)}
  assertEquals(1f,e.state.gain,0f);assertEquals(.25f,e.state.speed,0f)
  e.pause();val x=e.state.x;e.drag(1f,0f,0f);assertEquals(x,e.state.x,0f)
 }
 @Test fun spaceModeDisablesButtonsAndResetKeepsMode(){
  val e=FlightEngine();e.setInteraction(InteractionMode.SPACE);val before=e.state
  e.turn(90f);e.nudge(.15f);assertEquals(before.heading,e.state.heading,0f);assertEquals(before.y,e.state.y,0f)
  e.drag(.2f,0f,0f);assertEquals(.2f,e.state.x,0f)
  e.reset();assertEquals(InteractionMode.SPACE,e.state.mode)
 }
}
