package net.clahey.golfscore.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink

@Composable
fun AboutScreen() {
    Column {
        Text("Minigolf ScoreCard", style = MaterialTheme.typography.displayMedium, modifier = Modifier.align(Alignment.CenterHorizontally))
        HorizontalDivider()
        Text("By Your Average Chris")
        HorizontalDivider()

        // Display a link in the text and log metrics whenever user clicks on it. In that case we handle
        // the link using openUri method of the LocalUriHandler
        val uriHandler = LocalUriHandler.current
        Text(
            buildAnnotatedString {
                append("Icon based on  ")
                val link =
                    LinkAnnotation.Url(
                        "https://creazilla.com/media/clipart/14597/golf-hole",
                        TextLinkStyles(SpanStyle(color = Color.Blue))
                    ) {
                        val url = (it as LinkAnnotation.Url).url
                        // log some metrics
                        uriHandler.openUri(url)
                    }
                withLink(link) { append("https://creazilla.com/media/clipart/14597/golf-hole") }
            }
        )
    }
}