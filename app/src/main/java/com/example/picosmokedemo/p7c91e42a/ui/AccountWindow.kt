package com.example.picosmokedemo.p7c91e42a.ui

import androidx.compose.runtime.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.pico.spatial.ui.design.*
import com.pico.spatial.ui.platform.containers.LocalSpatialNavigator
import com.example.picosmokedemo.p7c91e42a.R
import com.example.picosmokedemo.p7c91e42a.account.*
import kotlinx.coroutines.*

private enum class AccountPage(val title:String){PASSWORD("密码登录"),CODE("验证码登录"),REGISTER("邮箱注册"),RESET("找回密码"),PROFILE("头像与资料")}
@Composable fun AccountWindow(){
 val context=LocalContext.current
 val navigator=LocalSpatialNavigator.current
 val service=remember{AccountStore.service(context)}
 val scope=rememberCoroutineScope()
 var page by remember{mutableStateOf(if(AccountStore.session==null)AccountPage.PASSWORD else AccountPage.PROFILE)}
 var email by remember{mutableStateOf("")}
 var password by remember{mutableStateOf("")}
 var confirm by remember{mutableStateOf("")}
 var code by remember{mutableStateOf("")}
 var visiblePassword by remember{mutableStateOf(false)}
 var busy by remember{mutableStateOf(false)}
 var message by remember{mutableStateOf("")}
 var retrySeconds by remember{mutableIntStateOf(0)}
 val session=AccountStore.session
 var name by remember{mutableStateOf(session?.profile?.displayName?:AccountStore.guestName)}
 var avatar by remember{mutableStateOf(session?.profile?.avatar?:AccountStore.guestAvatar)}
 LaunchedEffect(Unit){AccountStore.loadGuest(context);if(session==null){name=AccountStore.guestName;avatar=AccountStore.guestAvatar}}
 LaunchedEffect(retrySeconds){if(retrySeconds>0){delay(1000);retrySeconds--}}
 fun switch(target:AccountPage){if(!busy){page=target;password="";confirm="";code="";visiblePassword=false;message=""}}
 fun run(block:suspend ()->Unit){
  if(busy)return
  busy=true;message=""
  scope.launch{try{block()}catch(e:CancellationException){throw e}catch(e:AccountFailure){message=e.message?:"操作失败，请重试。"}catch(_:Exception){message="账号服务响应异常，请稍后重试。"}finally{busy=false}}
 }
 fun requireEmail():String {val value=AccountRules.email(email);if(!AccountRules.validEmail(value))throw AccountFailure("请输入有效的邮箱地址。");return value}
 fun requireCode(){if(!AccountRules.validCode(code))throw AccountFailure("请输入邮件中的 6 位数字验证码。")}
 fun requirePassword(){if(!AccountRules.validPassword(password))throw AccountFailure("密码需为 8–128 个字符。");if(password!=confirm)throw AccountFailure("两次输入的密码不一致。")}
 fun enter(){navigator.openWindowContainer("CoinScore",tag="coins");navigator.openWindowContainer("FlightConsole",tag="console");navigator.closeWindowContainer()}
 Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(32.dp),verticalArrangement=Arrangement.spacedBy(16.dp)){
  Row(horizontalArrangement=Arrangement.spacedBy(16.dp),verticalAlignment=androidx.compose.ui.Alignment.CenterVertically){
   Image(painterResource(R.drawable.mindsoul_flight_logo),"MindSoul 空间飞车 Logo",modifier=Modifier.size(72.dp))
   Text("MindSoul",style=PicoTheme.typography.titleLarge,color=PicoTheme.colorScheme.labelPrimary)
  }
  Text("脑机接口操作系统 · 娱乐游戏",color=PicoTheme.colorScheme.labelSecondary)
  Text("念力飞车 · ${page.title}",style=PicoTheme.typography.titleMedium,color=PicoTheme.colorScheme.labelPrimary)
  if(!service.configured)Text("账号服务未配置 · 可先以游客体验。邮箱验证、注册和密码找回将在接入后端后启用。",color=PicoTheme.colorScheme.labelSecondary)
  if(page==AccountPage.PROFILE){
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
    try{service.logout(session.accessToken);message="已退出登录。"}catch(e:AccountFailure){message="本机已退出；服务器会话注销未确认。"}finally{AccountStore.signOut();password="";code="";page=AccountPage.PASSWORD}
   }}){Text("退出登录")}
   else Button(enabled=!busy,onClick={switch(AccountPage.PASSWORD)}){Text("返回登录")}
  }else{
   Row(horizontalArrangement=Arrangement.spacedBy(12.dp)){
    Button(enabled=!busy,onClick={switch(AccountPage.PASSWORD)}){Text("密码登录")}
    Button(enabled=!busy,onClick={switch(AccountPage.CODE)}){Text("验证码登录")}
    Button(enabled=!busy,onClick={switch(AccountPage.REGISTER)}){Text("注册")}
   }
   Text("邮箱账户",color=PicoTheme.colorScheme.labelPrimary)
   TextField(value=email,onValueChange={if(it.length<=254)email=it},placeholder={Text("name@example.com")},singleLine=true,enabled=!busy,keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Email),modifier=Modifier.fillMaxWidth())
   if(page!=AccountPage.PASSWORD){
    Row(horizontalArrangement=Arrangement.spacedBy(12.dp)){
     TextField(value=code,onValueChange={if(it.length<=6&&it.all(Char::isDigit))code=it},placeholder={Text("6 位邮箱验证码")},singleLine=true,enabled=!busy,keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Number),modifier=Modifier.weight(1f))
     Button(enabled=!busy&&retrySeconds==0,onClick={run{
      val value=requireEmail()
      val purpose=when(page){AccountPage.REGISTER->CodePurpose.REGISTER;AccountPage.RESET->CodePurpose.RESET;else->CodePurpose.LOGIN}
      retrySeconds=service.sendCode(value,purpose)
      message="请求已受理。若邮箱符合条件，将收到验证码，请检查收件箱与垃圾邮件。"
     }}){Text(if(retrySeconds>0)"${retrySeconds} 秒后重发" else "获取验证码")}
    }
   }
   if(page!=AccountPage.CODE){
    Text(if(page==AccountPage.RESET)"设置新密码" else "密码",color=PicoTheme.colorScheme.labelPrimary)
    TextField(value=password,onValueChange={if(it.length<=128)password=it},placeholder={Text("8–128 个字符")},singleLine=true,enabled=!busy,visualTransformation=if(visiblePassword)VisualTransformation.None else PasswordVisualTransformation(),keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Password),modifier=Modifier.fillMaxWidth())
    Button(enabled=!busy,onClick={visiblePassword=!visiblePassword}){Text(if(visiblePassword)"隐藏密码" else "显示密码")}
    if(page==AccountPage.REGISTER||page==AccountPage.RESET)TextField(value=confirm,onValueChange={if(it.length<=128)confirm=it},placeholder={Text("再次输入密码")},singleLine=true,enabled=!busy,visualTransformation=PasswordVisualTransformation(),keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Password),modifier=Modifier.fillMaxWidth())
   }
   Button(enabled=!busy,onClick={run{
    val value=requireEmail()
    if(page==AccountPage.RESET){requireCode();requirePassword();service.resetPassword(value,code,password);password="";confirm="";code="";page=AccountPage.PASSWORD;message="密码已重置，请使用新密码登录。"}
    else{
     val result=when(page){
      AccountPage.REGISTER->{requireCode();requirePassword();service.register(value,password,code)}
      AccountPage.CODE->{requireCode();service.codeLogin(value,code)}
      else->{if(password.isEmpty())throw AccountFailure("请输入密码。");service.passwordLogin(value,password)}
     }
     AccountStore.signIn(result);password="";confirm="";code="";name=result.profile.displayName;avatar=result.profile.avatar;page=AccountPage.PROFILE;message="登录成功，可以设置头像并进入飞车。"
    }
   }}){Text(if(busy)"处理中…" else when(page){AccountPage.REGISTER->"验证邮箱并注册";AccountPage.RESET->"重置密码";else->"登录"})}
   if(page==AccountPage.PASSWORD)Button(enabled=!busy,onClick={switch(AccountPage.RESET)}){Text("忘记密码？")}
   Row(horizontalArrangement=Arrangement.spacedBy(12.dp)){
    Button(enabled=!busy,onClick={enter()}){Text("游客试玩")}
    Button(enabled=!busy,onClick={switch(AccountPage.PROFILE)}){Text("设置游客头像")}
   }
  }
  if(message.isNotEmpty())Text(message,color=PicoTheme.colorScheme.labelPrimary)
  Text("游客不是已注册账户。当前版本不在本地保存密码或验证码；登录会话仅保留在内存，重启后需重新登录。",style=PicoTheme.typography.bodySmall,color=PicoTheme.colorScheme.labelSecondary)
 }
}
private fun avatarName(id:String)=when(id){"orbit"->"星环";"spark"->"星芒";"explorer"->"探索者";else->"飞行员"}
@Composable internal fun AvatarIcon(id:String,size:Int=32){
 val resource=when(id){"orbit"->R.drawable.avatar_orbit;"spark"->R.drawable.avatar_spark;"explorer"->R.drawable.avatar_explorer;else->R.drawable.avatar_pilot}
 Icon(painter=painterResource(resource),contentDescription=avatarName(id),modifier=Modifier.size(size.dp),tint=PicoTheme.colorScheme.labelPrimary)
}
