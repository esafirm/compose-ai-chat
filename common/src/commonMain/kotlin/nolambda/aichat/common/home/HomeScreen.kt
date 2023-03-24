package nolambda.aichat.common.home

import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import nolambda.aichat.common.home.view.ChatView
import nolambda.aichat.common.home.view.HomeDrawerView
import nolambda.aichat.common.home.view.HomeTopBarView

class HomeScreen : Screen {

    @Composable
    override fun Content() {
        val scaffoldState = rememberScaffoldState()
        val model = rememberScreenModel { HomeScreenModel() }

        val state by model.state.collectAsState()

        Scaffold(
            scaffoldState = scaffoldState,
            topBar = { HomeTopBarView(scaffoldState) },
            drawerContent = { HomeDrawerView() },
        ) {
            ChatView(
                messages = state.messages,
                streamedMessage = state.streamedMessage
            ) { prompt ->
                model.ask(prompt)
            }
        }
    }
}
