package com.example.picosmokedemo.p7c91e42a.domain

import kotlin.math.sqrt
import kotlin.math.roundToInt

enum class TrainingCourse(val title:String,val seconds:Float,val radius:Float,val dwell:Float){
 INTRO("01 · 悬停入门",180f,.38f,1f),
 PRECISION("02 · 精准停靠",240f,.28f,2f),
 ENDURANCE("03 · 专注巡航",300f,.22f,3f)
}
data class FlightTarget(val x:Float,val y:Float,val z:Float)
data class TrainingState(val course:TrainingCourse=TrainingCourse.INTRO,val running:Boolean=false,val finished:Boolean=false,
 val elapsed:Float=0f,val docked:Int=0,val dwell:Float=0f,val focusSum:Float=0f,val focusSeconds:Float=0f,val simulated:Boolean=true){
 val remaining:Int get()=(course.seconds-elapsed).coerceAtLeast(0f).toInt()
 val average:Int? get()=if(focusSeconds>0f)(focusSum/focusSeconds).roundToInt() else null
 val target:FlightTarget get()=targets[docked%targets.size]
 companion object {val targets=listOf(FlightTarget(.55f,1.5f,-2.5f),FlightTarget(-.55f,1.8f,-3f),FlightTarget(0f,1.1f,-3.5f),FlightTarget(.7f,1.7f,-3.3f),FlightTarget(-.6f,1.3f,-2.3f))}
}
/** Spatial docking is scored from vehicle position; no medical or cognitive score is inferred. */
class TrainingEngine {
 var state=TrainingState();private set
 @Synchronized fun start(course:TrainingCourse,simulated:Boolean){state=TrainingState(course=course,running=true,simulated=simulated)}
 @Synchronized fun pause(){state=state.copy(running=false,dwell=0f)}
 @Synchronized fun resume(){if(!state.finished)state=state.copy(running=true)}
 @Synchronized fun finish(){state=state.copy(running=false,finished=true,dwell=0f)}
 @Synchronized fun tick(seconds:Float,flight:FlightState,locked:Boolean){
  if(!state.running||locked)return
  val dt=if(seconds.isFinite())seconds.coerceIn(0f,.05f) else 0f
  val target=state.target
  val d=sqrt((flight.x-target.x)*(flight.x-target.x)+(flight.y-target.y)*(flight.y-target.y)+(flight.z-target.z)*(flight.z-target.z))
  val hold=if(d<=state.course.radius&&!flight.flying)state.dwell+dt else 0f
  val docked=hold>=state.course.dwell
  val elapsed=(state.elapsed+dt).coerceAtMost(state.course.seconds)
  val focus=flight.focus
  state=state.copy(elapsed=elapsed,docked=state.docked+if(docked)1 else 0,dwell=if(docked)0f else hold,
   focusSum=state.focusSum+(focus?:0f)*dt,focusSeconds=state.focusSeconds+if(focus!=null)dt else 0f,
   simulated=state.simulated||flight.simulated,running=elapsed<state.course.seconds,finished=elapsed>=state.course.seconds)
 }
}
