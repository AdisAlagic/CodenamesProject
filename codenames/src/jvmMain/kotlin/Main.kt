import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.adisalagic.codenames.client.components.*

@Composable
@Preview
fun App(onCloseClick: () -> Unit, onCollapseClick: () -> Unit) {
    MaterialTheme() {
        Scaffold(
            backgroundColor = Color(0xFF202020)
        ) {
            Column {
                WindowControls(
                    onCloseClick = onCloseClick,
                    onCollapseClick = onCollapseClick
                )
                MainFrame()
            }

        }
    }
}

fun main() = application {
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
