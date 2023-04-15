package nolambda.aichat.common

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import nolambda.aichat.common.home.HomeScreen

@Composable
fun App() {
    Navigator(HomeScreen())
}
