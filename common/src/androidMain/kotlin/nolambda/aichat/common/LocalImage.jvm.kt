package nolambda.aichat.common // ktlint-disable filename

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

// Use of the function getIdentifier is discouraged, but we need to use it since the drawable names are defined in the common code for both
// platforms and on each platform we need to get the drawable according to provided name.
@OptIn(ExperimentalResourceApi::class)
@SuppressLint("ComposableNaming", "DiscouragedApi")
@Composable
internal actual fun __LocalImage(imageResourceName: String, modifier: Modifier, contentDescription: String?) {
    Image(
        painter = painterResource(imageResourceName),
        contentDescription = null,
        modifier = modifier
    )
}
