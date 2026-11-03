package com.example.picosmokedemo.p7c91e42a.domain
import org.junit.Assert.*
import org.junit.Test
class TrainingEngineTest {
 @Test fun dockingRequiresStationaryDwell(){
  val t=TrainingEngine();t.start(TrainingCourse.INTRO,true)
  val p=t.state.target;val f=FlightState(x=p.x,y=p.y,z=p.z)
  repeat(30){t.tick(.05f,f.copy(flying=true),false)};assertEquals(0,t.state.docked)
  repeat(21){t.tick(.05f,f,false)};assertEquals(1,t.state.docked);assertNotEquals(p,t.state.target)
 }
 @Test fun lockAndPauseDoNotConsumeTime(){
  val t=TrainingEngine();t.start(TrainingCourse.PRECISION,true)
  t.tick(.05f,FlightState(),true);assertEquals(0f,t.state.elapsed,0f)
  t.pause();t.tick(.05f,FlightState(),false);assertEquals(0f,t.state.elapsed,0f)
 }
 @Test fun finishAtDurationAndDoesNotRestart(){
  val t=TrainingEngine();t.start(TrainingCourse.INTRO,false)
  repeat(3602){t.tick(.05f,FlightState(focus=null,simulated=false),false)}
  assertTrue(t.state.finished);assertFalse(t.state.running);assertNull(t.state.average)
  t.resume();assertFalse(t.state.running)
 }
 @Test fun resultLabelsSimulationAndAveragesOnlyAvailableData(){
  val t=TrainingEngine();t.start(TrainingCourse.INTRO,false)
  repeat(20){t.tick(.05f,FlightState(focus=80f,simulated=false),false)}
  repeat(20){t.tick(.05f,FlightState(focus=null,simulated=true),false)}
  t.finish();assertEquals(80,t.state.average!!);assertTrue(t.state.simulated)
 }
}
