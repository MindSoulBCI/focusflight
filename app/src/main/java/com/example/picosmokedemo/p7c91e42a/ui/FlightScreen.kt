package com.example.picosmokedemo.p7c91e42a.ui

import android.os.SystemClock
import com.pico.spatial.ui.platform.containers.LocalSpatialNavigator
import androidx.compose.runtime.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.pico.spatial.core.math.Color4
import kotlin.math.sin
import kotlin.math.cos
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pico.spatial.ui.design.*
import com.pico.spatial.ui.foundation.content.SpatialView

import com.pico.spatial.ui.foundation.gesture.*
import com.pico.spatial.ui.platform.LocalPhysicalLengthConverter
import com.pico.spatial.ui.platform.LengthUnit
import com.pico.spatial.core.ecs.*
import com.pico.spatial.core.ecs.resource.*
import com.pico.spatial.core.math.Vector3
import com.pico.spatial.core.math.EulerAngles
import com.example.picosmokedemo.p7c91e42a.domain.*
import com.example.picosmokedemo.p7c91e42a.data.FlightSession
import kotlinx.coroutines.*

@Composable fun FlightScreen() {
 val navigator=LocalSpatialNavigator.current
 LaunchedEffect(Unit){
  navigator.openWindowContainer("FlightConsole",tag="console")
  navigator.openWindowContainer("CoinScore",tag="coins")
  navigator.openWindowContainer("MindSoulLogin",tag="login")
 }
 var targetRing by remember { mutableStateOf<Entity?>(null) }
 val engine=remember { FlightSession.engine }
 val brainLink=remember { FlightSession.brainLink }
 val lifecycleOwner=LocalLifecycleOwner.current
 var state by remember { mutableStateOf(engine.state) }
 var car by remember { mutableStateOf<Entity?>(null) }
 val effects=remember { FlightEffects() }
 var elapsed by remember { mutableFloatStateOf(0f) }
 var modelStatus by remember { mutableStateOf("模型加载中") }
 val context=LocalContext.current
 val coinEntities=remember{mutableListOf<Entity>()}
 var coinRoot by remember{mutableStateOf<Entity?>(null)}
 val sounds=remember{android.media.SoundPool.Builder().setMaxStreams(4).setAudioAttributes(android.media.AudioAttributes.Builder().setUsage(android.media.AudioAttributes.USAGE_GAME).build()).build()}
 var soundReady by remember{mutableStateOf(false)}
 var soundId by remember{mutableIntStateOf(0)}
 DisposableEffect(sounds){sounds.setOnLoadCompleteListener{_,_,status->soundReady=status==0};soundId=sounds.load(context,com.example.picosmokedemo.p7c91e42a.R.raw.coin_pickup,1);onDispose{sounds.release()}}
 DisposableEffect(coinRoot){val owned=coinRoot;onDispose{owned?.destroy();coinEntities.clear()}}
 val density=LocalDensity.current
 val converter=LocalPhysicalLengthConverter.current
 LaunchedEffect(engine) {
  var resetRevision=engine.resetRevision
  var last=withFrameNanos{it}
  while(isActive){val now=withFrameNanos{it};val dt=((now-last)/1_000_000_000f).coerceIn(0f,.05f);state=engine.tick(dt,SystemClock.elapsedRealtime());FlightSession.training.tick(dt,state,engine.interactionLocked);if(resetRevision!=engine.resetRevision){FlightSession.coins.teleport(state);resetRevision=engine.resetRevision};if(FlightSession.ready&&!engine.interactionLocked&&FlightSession.coins.update(state)>0&&soundReady&&FlightSession.coins.soundEnabled){sounds.play(soundId,.7f,.7f,1,0,1f)};elapsed+=dt;last=now}
 }
 DisposableEffect(lifecycleOwner,engine){
  val observer=LifecycleEventObserver { _, event ->
   if(event==Lifecycle.Event.ON_PAUSE || event==Lifecycle.Event.ON_STOP){engine.pause();FlightSession.training.pause();brainLink.onDisconnected()}
  }
  lifecycleOwner.lifecycle.addObserver(observer)
  onDispose{lifecycleOwner.lifecycle.removeObserver(observer);engine.pause();FlightSession.training.pause();brainLink.onDisconnected()}
 }
 DisposableEffect(car){val owned=car;onDispose{if(owned!=null){owned.destroy();FlightSession.ready=false}}}
 SpatialView(modifier=Modifier.fillMaxSize().pointerInput(engine){
  detectSpatialDragGesture(context=context,targetedToEntity=TargetEntity.any{it.getName()=="FlightCar"}) {
   fun meters(v:Float)=converter.dpToLength(with(density){v.toDp()},LengthUnit.Meters)/.18f
   engine.drag(meters(it.dragAmount.x),-meters(it.dragAmount.y),meters(it.dragAmount.z))
  }
 }, update={_,_->
  car?.components?.get(TransformComponent::class.java)?.apply{
   setPosition(Vector3(state.x*.18f,(state.y-1.5f + .014f*sin(elapsed*2.5f))*.18f,(state.z+3f)*.18f))
   setEulerAngles(EulerAngles(0f,90f-state.heading,if(state.flying) -2f*sin(elapsed*1.5f) else 0f))
  }
  val training=FlightSession.training.state
  targetRing?.components?.get(TransformComponent::class.java)?.setPosition(if(training.running) Vector3(training.target.x*.18f,(training.target.y-1.65f)*.18f,(training.target.z+3f)*.18f) else Vector3(0f,-20f,0f))
  val active=FlightSession.coins.state.coins.associateBy{it.slot}
  coinEntities.forEachIndexed{i,e->e.components[TransformComponent::class.java]!!.apply{val c=active[i];setPosition(if(c==null)Vector3(0f,-20f,0f) else Vector3(c.x,c.y,c.z));setEulerAngles(EulerAngles(0f,elapsed*90f+i*17f,0f))}}
  effects.update(elapsed,state)
 }, initial={content,attachments->
  try {
   val coinsRoot=Entity();coinRoot=coinsRoot;content.addEntity(coinsRoot)
   val coinPrototype=withContext(Dispatchers.IO){Entity.load("asset://models/coin.glb")}
   repeat(60){i->val coin=if(i==0)coinPrototype else checkNotNull(coinPrototype.clone(Entity.CloneOptions(recursive=true,shouldShareMaterialInstance=true)));coin.components[TransformComponent::class.java]!!.setPosition(Vector3(0f,-20f,0f));coinsRoot.addChild(coin);coinEntities.add(coin)}
   FlightSession.coins.teleport(engine.state)
   val marker=withContext(Dispatchers.IO){Entity.load("asset://models/lift_ring.glb")}
   marker.components[TransformComponent::class.java]!!.apply{scaleBy(.0975f);setPosition(Vector3(0f,-20f,0f))}
   content.addEntity(marker);targetRing=marker
   val sunlight=Entity().apply {
    components.set(DirectionalLightComponent(Color4(1f,.88f,.73f,1f),1600f))
    components[TransformComponent::class.java]!!.setEulerAngles(EulerAngles(-45f,-25f,0f))
   }
   content.addEntity(sunlight)
   val fill=Entity().apply {
    components.set(DirectionalLightComponent(Color4(.38f,.65f,1f,1f),650f))
    components[TransformComponent::class.java]!!.setEulerAngles(EulerAngles(-20f,145f,0f))
   }
   content.addEntity(fill)
   val model=withContext(Dispatchers.IO){Entity.load("asset://models/hover_car.glb")}
   val root=Entity();root.setName("FlightCar")
   model.components[TransformComponent::class.java]!!.apply{scaleBy(.9f/4.67f);setPosition(Vector3(.022f,-.121f,0f))}
   root.addChild(model)
   root.components.set(InteractableComponent())
   root.components.set(CollisionComponent(collisionShape=listOf(ShapeResource.createBox(Vector3(.9f,.24f,.52f))),physicsMaterial=PhysicsMaterialResource()))
   root.components[TransformComponent::class.java]!!.apply{scaleBy(.54f);setPosition(Vector3(0f,0f,.09f))}
   effects.attach(root)
   effects.update(0f,engine.state)
   root.components[TransformComponent::class.java]!!.setEulerAngles(EulerAngles(0f,90f-engine.state.heading,0f))
   content.addEntity(root);car=root;FlightSession.ready=true
   modelStatus="空间飞车已就绪"
  } catch(e:CancellationException){throw e} catch(e:Exception){modelStatus="模型加载失败：${e.message}"}
 })
}

