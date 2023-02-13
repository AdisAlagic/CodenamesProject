import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.adisalagic.codenames.client.components.LoginScreen
import com.adisalagic.codenames.client.components.MainFrame
import com.adisalagic.codenames.client.components.WindowControls
import com.adisalagic.codenames.client.viewmodels.LoginViewModel
import com.adisalagic.codenames.client.viewmodels.ViewModelsStore


@Composable
@Preview
fun App(onCloseClick: () -> Unit, onCollapseClick: () -> Unit) {
    MaterialTheme {
        Scaffold(
            backgroundColor = Color(0xFF202020)
        ) {
            Column {
                val loginViewModel = ViewModelsStore.loginViewModel
                val data by loginViewModel.state.collectAsState()
                WindowControls(
                    onCloseClick = onCloseClick,
                    onCollapseClick = onCollapseClick
                )
                if (data.connectionState == LoginViewModel.ConnectionState.CONNECTED){
                    MainFrame()
                }else{
                    LoginScreen()
                }
            }

        }
    }
}

fun main() = application {
    System.setProperty("log4j2.disable.jmx", true.toString());
    var windowState by remember {
        mutableStateOf(
            WindowState(
                placement = WindowPlacement.Maximized,
            )
        )
    }
    Window(
        onCloseRequest = ::exitApplication,
        resizable = false,
        title = "Codenames",
        icon = painterResource("CN.png"),
        undecorated = true,
        state = windowState
    ) {
        App(
            onCloseClick = this@application::exitApplication,
            onCollapseClick = {
                windowState = WindowState(
                    placement = WindowPlacement.Maximized,
                    isMinimized = true
                )
            }
        )
    }
}
