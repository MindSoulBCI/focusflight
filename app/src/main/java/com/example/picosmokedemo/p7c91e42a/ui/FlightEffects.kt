package com.example.picosmokedemo.p7c91e42a.ui
import com.pico.spatial.core.ecs.Entity
import com.pico.spatial.core.ecs.TransformComponent
import com.pico.spatial.core.math.Vector3
import com.pico.spatial.core.math.EulerAngles
import com.example.picosmokedemo.p7c91e42a.domain.FlightState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.sin
import kotlin.math.cos

/** Native mesh effects adapted from upstream effects.js, with bounded reusable entities. */
internal class FlightEffects {
 private data class Jet(val entity:Entity,val rear:Boolean,val phase:Float)
 private val jets=mutableListOf<Jet>()
 private val sparks=mutableListOf<Entity>()
 private val rings=mutableListOf<Entity>()
 suspend fun attach(root:Entity){
  val prototypes=mutableMapOf<String,Entity>()
  suspend fun load(name:String):Entity {
   val existing=prototypes[name]
   if(existing!=null)return checkNotNull(existing.clone(Entity.CloneOptions(recursive=true,shouldShareMaterialInstance=true)))
   return withContext(Dispatchers.IO){Entity.load("asset://models/$name.glb")}.also{prototypes[name]=it}
  }
  for(x in listOf(-.257f,.301f))for(z in listOf(-.189f,.189f)){
   val jet=load("jet_plume")
   jet.components[TransformComponent::class.java]!!.setPosition(Vector3(x,-.102f,z))
   root.addChild(jet);jets.add(Jet(jet,false,jets.size.toFloat()))
   val ring=load("lift_ring")
   ring.components[TransformComponent::class.java]!!.setPosition(Vector3(x,-.33f,z))
   root.addChild(ring);rings.add(ring)
  }
  for(z in listOf(-.121f,-.040f,.040f,.121f)){
   val jet=load("jet_plume")
   jet.components[TransformComponent::class.java]!!.apply{
    setPosition(Vector3(-.435f,-.034f,z));setEulerAngles(EulerAngles(0f,0f,-90f))
   }
   root.addChild(jet);jets.add(Jet(jet,true,jets.size.toFloat()))
  }
  repeat(16){val spark=load("jet_spark");root.addChild(spark);sparks.add(spark)}
 }
 fun update(time:Float,s:FlightState){
  val throttle=if(s.flying)(.45f+.55f*(s.gain-1f)) else .18f
  jets.forEach{j->
   val pulse=1f+.09f*sin(time*23f+j.phase)
   val length=if(j.rear)(.14f+throttle*.95f) else (.28f+throttle*.25f)
   val width=if(j.rear).34f+throttle*.22f else .6f
   j.entity.components[TransformComponent::class.java]!!.setScaleVector(Vector3(width,length*pulse,width))
  }
  rings.forEachIndexed{i,e->val q=((time*.8f+i*.25f)%1f);e.components[TransformComponent::class.java]!!.apply{
   setScaleVector(Vector3(.2f+q*.7f,.2f+q*.7f,.2f+q*.7f))
   setPosition(Vector3(if(i<2)-.257f else .301f,-.18f-q*.4f,if(i%2==0)-.189f else .189f))
  }}
  sparks.forEachIndexed{i,e->val q=(time*(.7f+throttle)+i/16f)%1f;e.components[TransformComponent::class.java]!!.apply{
   setPosition(Vector3(-.45f-q*(.35f+throttle),-.035f+sin(i*2.4f)*q*.08f,cos(i*2.4f)*(.08f+q*.08f)))
   setEulerAngles(EulerAngles(0f,0f,90f))
   val size=if(s.flying)(1f-q)*.7f else (1f-q)*.14f
   setScaleVector(Vector3(size,size*(1f+throttle*3f),size))
  }}
 }
}
