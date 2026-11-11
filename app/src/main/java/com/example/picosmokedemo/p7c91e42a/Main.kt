package com.example.picosmokedemo.p7c91e42a
import androidx.compose.ui.unit.dp
import com.pico.spatial.ui.design.PicoTheme
import com.pico.spatial.ui.foundation.dsl.*
import com.example.picosmokedemo.p7c91e42a.ui.FlightScreen
import com.example.picosmokedemo.p7c91e42a.ui.InteractionWindow
import com.example.picosmokedemo.p7c91e42a.ui.FlightConsole
import com.example.picosmokedemo.p7c91e42a.ui.AccountWindow
import com.example.picosmokedemo.p7c91e42a.ui.LoginWindow
fun mainApp(scope: SpatialAppScope) = with(scope) {
 WindowContainer(id="MindSoulLogin",form=Form.Planar,defaultSize=WindowContainerSize(560.dp,720.dp)) { PicoTheme { LoginWindow() } }
 WindowContainer(id="MindSoulAccount",form=Form.Planar,defaultSize=WindowContainerSize(780.dp,920.dp)) { PicoTheme { AccountWindow() } }
 WindowContainer(id="CoinScore",form=Form.Planar,defaultSize=WindowContainerSize(620.dp,660.dp),placement={containers.firstOrNull{it.id=="FocusFlightVolume"}?.let{Placement.left(it,16.dp)}?:Placement.none()}) { PicoTheme { com.example.picosmokedemo.p7c91e42a.ui.CoinWindow() } }
 DefaultWindowContainer { PicoTheme { FlightScreen() } }
 WindowContainer(id="FlightConsole",form=Form.Planar,defaultSize=WindowContainerSize(660.dp,820.dp),placement={containers.firstOrNull{it.id=="FocusFlightVolume"}?.let{Placement.right(it,16.dp)}?:Placement.none()}) { PicoTheme { FlightConsole() } }
 WindowContainer(id="FlightInteraction",form=Form.Planar,defaultSize=WindowContainerSize(720.dp,660.dp)) {
  PicoTheme { InteractionWindow() }
 }
}
