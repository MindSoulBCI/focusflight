package com.example.picosmokedemo.p7c91e42a.domain

import kotlin.math.*

enum class InteractionMode(val title:String,val detail:String,val allowsDrag:Boolean,val allowsButtons:Boolean,val usesFocus:Boolean) {
 SPACE("空间拖动", "捏合并拖动车身调整位置；按开始后匀速飞行。",true,false,false),
 BUTTONS("按钮操控", "使用转向、升降按钮操作；按开始后匀速飞行。",false,true,false),
 FOCUS("专注力辅助", "保留空间拖动和按钮；专注力越高，推进越强。",true,true,true)
}

data class FlightState(val x: Float=0f, val y: Float=1.5f, val z: Float=-2.5f,
 val heading: Float=-35f, val flying:Boolean=false, val speed:Float=0f,
 val focus:Float?=65f, val gain:Float=1f, val simulated:Boolean=true,
 val status:String="就绪", val source:String="模拟数据", val mode:InteractionMode=InteractionMode.FOCUS)

class FlightEngine {
 var state = FlightState(); private set
 var interactionLocked=false; private set
 @Synchronized fun chooseInteraction(open:Boolean){interactionLocked=open;if(open)pause()}
 @Synchronized fun setInteraction(mode:InteractionMode){pause();state=state.copy(mode=mode,gain=1f);interactionLocked=false}
 @Synchronized fun drag(dx:Float,dy:Float,dz:Float){if(!interactionLocked && state.mode.allowsDrag)move(dx,dy,dz)}
 @Synchronized fun nudge(dy:Float){if(!interactionLocked && state.mode.allowsButtons)move(0f,dy,0f)}
 private var simulatedFocus=65f
 private var external: Pair<Float,Long>?=null
 @Synchronized fun submitBrainLink(percent:Float, quality:Float, elapsedMs:Long):Boolean {
  if(!percent.isFinite() || percent !in 0f..100f || !quality.isFinite() || quality !in 0.5f..1f || elapsedMs<0) return false
  if(external!=null && elapsedMs < external!!.second) return false
  external=percent to elapsedMs; return true
 }
 @Synchronized fun disconnect(){external=null}
 @Synchronized fun setFocus(v:Float){if(v.isFinite()) simulatedFocus=v.coerceIn(0f,100f)}
 @Synchronized fun toggleSource(){state=state.copy(simulated=!state.simulated,gain=1f,focus=null);external=null}
 @Synchronized fun toggleFlight(){if(interactionLocked)return;state=state.copy(flying=!state.flying,speed=0f,status=if(state.flying) "暂停悬停" else "飞行中")}
 @Synchronized fun pause(){state=state.copy(flying=false,speed=0f,status="暂停悬停")}
 @Volatile var resetRevision=0;private set
 @Synchronized fun reset(){resetRevision++;state=FlightState(mode=state.mode,simulated=state.simulated,focus=if(state.simulated) simulatedFocus else null)}
 @Synchronized fun turn(degrees:Float){if(interactionLocked || !state.mode.allowsButtons)return;state=state.copy(heading=(state.heading+degrees)%360f)}
 @Synchronized fun move(dx:Float,dy:Float,dz:Float){
  if(!dx.isFinite() || !dy.isFinite() || !dz.isFinite())return
  pause();state=state.copy(x=(state.x+dx).coerceIn(-1.5f,1.5f),y=(state.y+dy).coerceIn(.7f,2.4f),z=(state.z+dz).coerceIn(-4.5f,-1.5f))
 }
 @Synchronized fun tick(seconds:Float,now:Long):FlightState {
  val dt=if(seconds.isFinite())seconds.coerceIn(0f,.05f) else 0f
  val sample=external
  val focus=if(state.simulated)simulatedFocus else sample?.takeIf{now-it.second in 0..2000}?.first
  val target=1f+(focus?:0f)/100f
  val gain=if(focus==null || !state.mode.usesFocus)1f else state.gain+(target-state.gain)*(dt*3f).coerceAtMost(1f)
  val speed=if(state.flying).25f*gain else 0f
  val a=state.heading*PI.toFloat()/180f
  val x=state.x+sin(a)*speed*dt; val z=state.z-cos(a)*speed*dt
  val boundary=x !in -1.5f..1.5f || z !in -4.5f..-1.5f
  state=state.copy(x=x.coerceIn(-1.5f,1.5f),z=z.coerceIn(-4.5f,-1.5f),focus=focus,gain=gain,
   flying=state.flying&&!boundary,speed=if(boundary)0f else speed,
   status=if(boundary)"到达边界 · 请转向或复位" else state.status,
   source=if(state.simulated)"模拟数据" else if(sample==null)"BrainLink 未连接" else if(focus==null)"BrainLink 数据过期" else "BrainLink 实时数据")
  return state
 }
}
