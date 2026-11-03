package com.example.picosmokedemo.p7c91e42a.domain

import kotlin.math.*
import kotlin.random.Random

data class SpaceCoin(val slot:Int,val x:Float,val y:Float,val z:Float)
data class CoinState(val coins:List<SpaceCoin> = emptyList(),val score:Int=0,val spawned:Int=0)
/** Positions are volume-local metres. One immutable snapshot is shared by both windows. */
class CoinEngine(private val random:Random=Random.Default){
 @Volatile var state=CoinState();private set
 @Volatile var soundEnabled=true
 private var previous:FlightState?=null
 init{add(20,FlightState())}
 @Synchronized fun add(count:Int=20,car:FlightState):Int{
  val list=state.coins.toMutableList()
  val wanted=count.coerceIn(0,60-list.size)
  var added=0
  for(slot in 0 until 60){
   if(added>=wanted)break
   if(list.any{it.slot==slot})continue
   var candidate:SpaceCoin?=null
   for(attempt in 0 until 2000){
    val c=SpaceCoin(slot,(random.nextFloat()*3f-1.5f)*.18f,(random.nextFloat()*1.7f-.8f)*.18f,(random.nextFloat()*3f-1.5f)*.18f)
    if(!touches(c,car,.045f)&&list.none{(it.x-c.x).pow(2)+(it.y-c.y).pow(2)+(it.z-c.z).pow(2)<.045f.pow(2)}){candidate=c;break}
   }
   candidate?.let{list.add(it);added++}
  }
  state=state.copy(coins=list.toList(),spawned=state.spawned+added)
  return added
 }
 @Synchronized fun restart(car:FlightState){state=CoinState();previous=car;add(20,car)}
 @Synchronized fun teleport(car:FlightState){previous=car}
 @Synchronized fun update(car:FlightState):Int{
  val old=previous;previous=car
  if(old==null)return 0
  val distance=sqrt((car.x-old.x).pow(2)+(car.y-old.y).pow(2)+(car.z-old.z).pow(2))*.18f
  if(distance<.00001f&&abs(car.heading-old.heading)<.01f)return 0
  val steps=max(1,ceil(distance/.015f).toInt()).coerceAtMost(100)
  val hit=state.coins.filter{coin->(0..steps).any{i->
   val t=i.toFloat()/steps
   touches(coin,car.copy(x=old.x+(car.x-old.x)*t,y=old.y+(car.y-old.y)*t,z=old.z+(car.z-old.z)*t,heading=old.heading+(car.heading-old.heading)*t))
  }}
  if(hit.isNotEmpty())state=state.copy(coins=state.coins-hit.toSet(),score=state.score+hit.size)
  return hit.size
 }
 private fun touches(c:SpaceCoin,s:FlightState,pad:Float=.022f):Boolean{
  val dx=c.x-s.x*.18f;val dz=c.z-(s.z+3f)*.18f
  val a=Math.toRadians((90f-s.heading).toDouble()).toFloat()
  val lx=cos(a)*dx-sin(a)*dz;val lz=sin(a)*dx+cos(a)*dz
  return abs(lx)<=.243f+pad&&abs(c.y-(s.y-1.5f)*.18f)<=.0648f+pad&&abs(lz)<=.1404f+pad
 }
}
