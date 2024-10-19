package net.clahey.golfscore.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp

@Composable
fun AboutScreen() {
    val uriHandler = LocalUriHandler.current

    @Composable
    fun AnnotatedString.Builder.withLink(
        link: String,
        block: AnnotatedString.Builder.() -> Unit,
    ) {
        val link = LinkAnnotation.Url(link, TextLinkStyles(SpanStyle(color = Color.Blue))) {
            val url = (it as LinkAnnotation.Url).url
            // log some metrics
            uriHandler.openUri(url)
        }
        withLink(link) {
            block()
        }
    }

    Column {
        Text(
            "GolfScore",
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp)
        )
        HorizontalDivider()
        Text("By Your Average Chris", Modifier.padding(8.dp))
        Text(buildAnnotatedString {
            append("Source code available at ")
            withLink("https://github.com/clahey/golf-score") { append("github.com/clahey/golf-score") }
        }, Modifier.padding(8.dp))
        HorizontalDivider()

        Text(
            buildAnnotatedString {
                append("Icon based on  ")
                withLink("https://creazilla.com/media/clipart/14597/golf-hole") { append("https://creazilla.com/media/clipart/14597/golf-hole") }
            }, Modifier.padding(8.dp)
        )
    }
}