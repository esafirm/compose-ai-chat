package nolambda.aichat.common.home.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import nolambda.aichat.common.LocalImage
import nolambda.aichat.common.LocalImages
import nolambda.aichat.common.home.model.Message

@Composable
internal fun ChatItemView(
    actor: Message.Actor,
    text: String,
    modifier: Modifier = Modifier,
) {

    Row(
        modifier = modifier.composed {
            if (actor == Message.Actor.Person) {
                Modifier.background(Color.LightGray.copy(0.5F))
                    .padding(16.dp)
                    .fillMaxWidth()
            } else {
                Modifier.padding(16.dp)
                    .fillMaxWidth()
            }
        }
    ) {

        val image = when (actor) {
            Message.Actor.Person -> LocalImages.Person
            Message.Actor.Bot -> LocalImages.Robot
        }

        LocalImage(
            imageResourceName = image,
            contentDescription = "Actor",
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(text = text)
    }
}
