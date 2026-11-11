package com.example.picosmokedemo.p7c91e42a.ui

import androidx.compose.runtime.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.pico.spatial.ui.design.*
import com.pico.spatial.ui.platform.containers.LocalSpatialNavigator
import com.example.picosmokedemo.p7c91e42a.R
import com.example.picosmokedemo.p7c91e42a.account.*
import kotlinx.coroutines.*

@Composable fun AccountWindow(){
 val context=LocalContext.current
 val navigator=LocalSpatialNavigator.current
 val service=remember{AccountStore.service(context)}
 val scope=rememberCoroutineScope()
 val session=AccountStore.session
 var name by remember{mutableStateOf(session?.profile?.displayName?:AccountStore.guestName)}
 var avatar by remember{mutableStateOf(session?.profile?.avatar?:AccountStore.guestAvatar)}
 var busy by remember{mutableStateOf(false)}
 var message by remember{mutableStateOf("")}
 LaunchedEffect(Unit){AccountStore.loadGuest(context);if(session==null){name=AccountStore.guestName;avatar=AccountStore.guestAvatar}}
 fun run(block:suspend ()->Unit){
  if(busy)return
  busy=true;message=""
  scope.launch{try{block()}catch(e:CancellationException){throw e}catch(e:AccountFailure){message=e.message?:"操作失败，请重试。"}catch(_:Exception){message="账号服务响应异常，请稍后重试。"}finally{busy=false}}
 }
 fun enter(){navigator.openWindowContainer("CoinScore",tag="coins");navigator.openWindowContainer("FlightConsole",tag="console");navigator.closeWindowContainer()}
 Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(32.dp),verticalArrangement=Arrangement.spacedBy(16.dp)){
  Row(horizontalArrangement=Arrangement.spacedBy(16.dp),verticalAlignment=androidx.compose.ui.Alignment.CenterVertically){
   Image(painterResource(R.drawable.mindsoul_flight_logo),"MindSoul 空间飞车 Logo",modifier=Modifier.size(72.dp))
   Text("MindSoul",style=PicoTheme.typography.titleLarge,color=PicoTheme.colorScheme.labelPrimary)
  }
  Text("脑机接口操作系统 · 娱乐游戏",color=PicoTheme.colorScheme.labelSecondary)
  Text("念力飞车 · 头像与资料",style=PicoTheme.typography.titleMedium,color=PicoTheme.colorScheme.labelPrimary)
  Text(if(session==null)"游客资料 · 仅保存于本机" else session.profile.email,color=PicoTheme.colorScheme.labelSecondary)
  AvatarIcon(avatar,72)
  TextField(value=name,onValueChange={if(it.length<=24)name=it},placeholder={Text("昵称（1–24 字）")},singleLine=true,enabled=!busy,modifier=Modifier.fillMaxWidth())
  Text("选择头像",color=PicoTheme.colorScheme.labelPrimary)
  Row(horizontalArrangement=Arrangement.spacedBy(12.dp)){
   AccountRules.avatars.forEach{item->
    Option(modifier=Modifier.weight(1f),selected=avatar==item,onSelectChange={if(!busy)avatar=item}){
     Column(verticalArrangement=Arrangement.spacedBy(8.dp)){AvatarIcon(item,32);Text(avatarName(item))}
    }
   }
  }
  Button(enabled=!busy,onClick={run{
   if(!AccountRules.validProfile(name,avatar))throw AccountFailure("请填写 1–24 字昵称并选择头像。")
   if(session==null)AccountStore.saveGuest(context,name,avatar) else AccountStore.update(service.updateProfile(session.accessToken,name.trim(),avatar))
   message=if(session==null)"游客头像与昵称已保存到本机。" else "头像与资料已更新。"
  }}){Text("保存资料")}
  Button(enabled=!busy,onClick={enter()}){Text("进入飞车")}
  if(session!=null)Button(enabled=!busy,onClick={run{
   try{service.logout(session.accessToken)}catch(_:AccountFailure){}
   AccountStore.signOut()
   message="已退出登录。"
  }}){Text("退出登录")}
  else Button(enabled=!busy,onClick={navigator.openWindowContainer("MindSoulLogin",tag="login")}){Text("登录 / 注册")}
  if(message.isNotEmpty())Text(message,color=PicoTheme.colorScheme.labelPrimary)
  Text("游客不是已注册账户。当前版本不在本地保存密码或验证码；登录会话仅保留在内存，重启后需重新登录。",style=PicoTheme.typography.bodySmall,color=PicoTheme.colorScheme.labelSecondary)
 }
}
private fun avatarName(id:String)=when(id){"orbit"->"星环";"spark"->"星芒";"explorer"->"探索者";else->"飞行员"}
@Composable internal fun AvatarIcon(id:String,size:Int=32){
 val resource=when(id){"orbit"->R.drawable.avatar_orbit;"spark"->R.drawable.avatar_spark;"explorer"->R.drawable.avatar_explorer;else->R.drawable.avatar_pilot}
 Icon(painter=painterResource(resource),contentDescription=avatarName(id),modifier=Modifier.size(size.dp),tint=PicoTheme.colorScheme.labelPrimary)
}
