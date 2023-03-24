package nolambda.aichat.common

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
internal actual fun __LocalImage(imageResourceName: String, modifier: Modifier, contentDescription: String?) {
    Image(
        painter = painterResource(imageResourceName),
        contentDescription = null,
        modifier = modifier,
    )
}
