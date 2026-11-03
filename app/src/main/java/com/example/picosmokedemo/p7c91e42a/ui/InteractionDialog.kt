package com.example.picosmokedemo.p7c91e42a.ui

import androidx.compose.runtime.*
import com.pico.spatial.ui.platform.containers.LocalSpatialNavigator
import com.example.picosmokedemo.p7c91e42a.data.FlightSession
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pico.spatial.ui.design.*
import com.example.picosmokedemo.p7c91e42a.domain.InteractionMode

/** The native window owns the only glass surface; content stays transparent. */
@Composable internal fun InteractionDialog(current:InteractionMode,onDismiss:()->Unit,onConfirm:(InteractionMode)->Unit){
 var selected by remember(current){mutableStateOf(current)}
 Column(Modifier.fillMaxSize().padding(40.dp),verticalArrangement=Arrangement.spacedBy(20.dp)){
  Text("念力飞车 · 飞行准备",color=PicoTheme.colorScheme.labelSecondary,style=PicoTheme.typography.bodyMedium)
  Text("选择交互方式",color=PicoTheme.colorScheme.labelPrimary,style=PicoTheme.typography.titleLarge.copy(fontFamily=FontFamily.SansSerif,fontWeight=FontWeight.Bold))
  Text("飞车已悬停 · 确认后开始你的空间试飞",color=PicoTheme.colorScheme.labelSecondary,style=PicoTheme.typography.bodyMedium)
  InteractionMode.entries.forEach{mode->
   Option(modifier=Modifier.fillMaxWidth(),selected=selected==mode,onSelectChange={selected=mode}){
    Text(mode.title,style=PicoTheme.typography.titleMedium.copy(fontFamily=FontFamily.SansSerif))
   }
  }
  Text(selected.detail,color=PicoTheme.colorScheme.labelPrimary,style=PicoTheme.typography.bodyMedium)
  Text(if(selected.usesFocus) "模拟专注力已就绪 · BrainLink 待接入" else "匀速飞行 · 专注力不参与加速",color=PicoTheme.colorScheme.labelSecondary,style=PicoTheme.typography.bodySmall)
  Spacer(Modifier.weight(1f))
  Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(16.dp)){
   Button(onClick=onDismiss){Text("取消")}
   Button(onClick={onConfirm(selected)}){Text("确认选择")}
  }
 }
}

@Composable internal fun InteractionWindow(){
 val navigator=LocalSpatialNavigator.current
 val engine=FlightSession.engine
 var visible by remember { mutableStateOf(true) }
 DisposableEffect(Unit){engine.chooseInteraction(true);onDispose{engine.chooseInteraction(false)}}
 fun close(){visible=false;engine.chooseInteraction(false);navigator.closeWindowContainer()}
 Box(Modifier.fillMaxSize()){
  if(visible)InteractionDialog(engine.state.mode,onDismiss={close()},onConfirm={engine.setInteraction(it);close()})
 }
}
