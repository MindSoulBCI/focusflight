package com.example.picosmokedemo.p7c91e42a.ui

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pico.spatial.ui.design.*
import com.pico.spatial.ui.platform.containers.LocalSpatialNavigator
import com.example.picosmokedemo.p7c91e42a.data.FlightSession
import com.example.picosmokedemo.p7c91e42a.domain.*
import kotlinx.coroutines.delay

/** A separate native glass window, synchronized with the shared-space vehicle volume. */
@Composable fun FlightConsole(){
 val e=FlightSession.engine
 val trainer=FlightSession.training
 val nav=LocalSpatialNavigator.current
 var s by remember {mutableStateOf(e.state)}
 var t by remember {mutableStateOf(trainer.state)}
 LaunchedEffect(Unit){while(true){s=e.state;t=trainer.state;delay(50)}}
 Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(32.dp),verticalArrangement=Arrangement.spacedBy(16.dp)){
  Text("MindSoul · 念力飞车",color=PicoTheme.colorScheme.labelPrimary,style=PicoTheme.typography.titleLarge.copy(fontFamily=FontFamily.SansSerif,fontWeight=FontWeight.Bold))
  Text("脑机接口操作系统 · 娱乐游戏",color=PicoTheme.colorScheme.labelSecondary)
  Button(onClick={e.pause();trainer.pause();nav.openWindowContainer("MindSoulAccount",tag="account")}){Text("账户与头像")}
  Button(onClick={nav.openWindowContainer("CoinScore",tag="coins")}){Text("金币得分与补充")}
  Text("共享空间飞行实验室 · ${s.source}",color=PicoTheme.colorScheme.labelSecondary)
  Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){
   Metric("专注力", "${s.focus?.toInt()?.toString()?:"—"}%")
   Metric("推进倍率", "×%.2f".format(s.gain))
   Metric("速度 m/s", "%.2f".format(s.speed*.18f))
  }
  Text("${s.mode.title} · ${s.status}",color=PicoTheme.colorScheme.labelSecondary)
  Row(horizontalArrangement=Arrangement.spacedBy(12.dp)){
   Button(onClick={e.turn(-15f)},enabled=s.mode.allowsButtons){Text("左转")}
   Button(onClick={e.turn(15f)},enabled=s.mode.allowsButtons){Text("右转")}
   Button(onClick={e.nudge(.15f)},enabled=s.mode.allowsButtons){Text("上升")}
   Button(onClick={e.nudge(-.15f)},enabled=s.mode.allowsButtons){Text("下降")}
  }
  Row(horizontalArrangement=Arrangement.spacedBy(12.dp)){
   Button(onClick=e::toggleFlight,enabled=FlightSession.ready){Text(if(s.flying)"悬停" else "开始飞行")}
   Button(onClick=e::reset){Text("复位")}
   Button(onClick={e.chooseInteraction(true);nav.openWindowContainer("FlightInteraction",tag="interaction")}){Text("交互方式")}
  }
  Text(if(s.simulated)"模拟专注力 · 拖动滑条体验推进变化" else "BrainLink 数据接口 · 等待硬件",color=PicoTheme.colorScheme.labelSecondary)
  Slider(value=s.focus?:0f,onValueChange=e::setFocus,valueRange=0f..100f,enabled=s.simulated)
  Button(onClick={trainer.pause();e.toggleSource()}){Text(if(s.simulated)"切换 BrainLink" else "返回模拟模式")}
  Text("空间停靠训练",color=PicoTheme.colorScheme.labelPrimary,style=PicoTheme.typography.titleLarge)
  Text("把飞车拖到蓝色光环上方并悬停。连续停稳后目标会移动；也可用转向和推进飞向目标。保持身体与视角不动，只移动飞车。",color=PicoTheme.colorScheme.labelSecondary)
  Row(horizontalArrangement=Arrangement.spacedBy(12.dp)){
   TrainingCourse.entries.forEach{course->Button(onClick={e.reset();trainer.start(course,s.simulated)},enabled=FlightSession.ready&&!t.running){Text(course.title)}}
  }
  Text("${t.course.title} · 剩余 ${t.remaining/60}:${(t.remaining%60).toString().padStart(2,'0')} · 停靠 ${t.docked} 次",color=PicoTheme.colorScheme.labelPrimary)
  Text(if(t.finished)"训练结束 · ${if(t.simulated)"含模拟数据" else "BrainLink 数据"} · 平均专注力 ${t.average?.toString()?:"—"}%" else if(t.running)"停稳进度 ${(t.dwell/t.course.dwell*100).toInt()}% · 距离目标 ≤ ${(t.course.radius*18).toInt()} 厘米" else "每节 3 / 4 / 5 分钟，完整体验约 12 分钟。",color=PicoTheme.colorScheme.labelSecondary)
  Row(horizontalArrangement=Arrangement.spacedBy(12.dp)){
   Button(onClick={if(t.running){trainer.pause();e.pause()}else trainer.resume()},enabled=!t.finished&&t.elapsed>0f){Text(if(t.running)"暂停训练" else "继续训练")}
   Button(onClick={trainer.finish();e.pause()},enabled=t.running||t.elapsed>0f&&!t.finished){Text("结束并结算")}
  }
  Text("玩法演示，不提供医疗或认知能力评估。关闭或离开应用会暂停；训练结果保留至本次应用退出。",color=PicoTheme.colorScheme.labelSecondary,style=PicoTheme.typography.bodySmall)
 }
}
@Composable private fun Metric(label:String,value:String){
 Column {
  Text(label,color=PicoTheme.colorScheme.labelSecondary)
  Text(value,color=PicoTheme.colorScheme.labelPrimary,style=PicoTheme.typography.titleLarge.copy(fontFamily=FontFamily.Monospace,fontWeight=FontWeight.Bold,fontSize=38.sp))
 }
}
