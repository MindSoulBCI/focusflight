package com.example.picosmokedemo.p7c91e42a.ui
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pico.spatial.ui.design.*
import com.example.picosmokedemo.p7c91e42a.data.FlightSession
import kotlinx.coroutines.delay

@Composable fun CoinWindow(){
 val game=FlightSession.coins
 var state by remember{mutableStateOf(game.state)}
 var sound by remember{mutableStateOf(game.soundEnabled)}
 Column(Modifier.fillMaxSize().padding(32.dp),verticalArrangement=Arrangement.spacedBy(20.dp)){
  Text("空中寻金",style=PicoTheme.typography.titleLarge,color=PicoTheme.colorScheme.labelPrimary)
  Text("${state.score} 分",style=PicoTheme.typography.titleLarge,color=PicoTheme.colorScheme.labelPrimary)
  Text("已收集 ${state.score} 枚 · 空中剩余 ${state.coins.size} 枚",color=PicoTheme.colorScheme.labelSecondary)
  Text(if(state.coins.isEmpty())"全部收集完成！补充金币，继续挑战。" else "驾驶或拖动飞车碰触金币，每枚 +1 分。",color=PicoTheme.colorScheme.labelSecondary)
  Button(onClick={game.add(20,FlightSession.engine.state)},enabled=state.coins.size<60){Text("增加 20 枚金币")}
  Button(onClick={game.restart(FlightSession.engine.state)}){Text("重新开始 · 清零并生成 20 枚")}
  Button(onClick={sound=!sound;game.soundEnabled=sound}){Text(if(sound)"金币音效：开启" else "金币音效：关闭")}
  Text("最多同时 60 枚 · 成绩保留至本次应用退出",color=PicoTheme.colorScheme.labelSecondary)
 }
 LaunchedEffect(Unit){while(true){state=game.state;delay(50)}}
}
