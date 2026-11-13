package de.letheapp.filamentmanager.ui.info

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import de.letheapp.filamentmanager.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoScreen() {
    val uriHandler = LocalUriHandler.current

    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.nav_info)) }) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp)) {
            Text(stringResource(R.string.app_name))
            Text(stringResource(R.string.info_developer))
            ClickableText(
                text = AnnotatedString(stringResource(R.string.info_website)),
                onClick = { uriHandler.openUri("https://letheapp.de") },
            )
            ClickableText(
                text = AnnotatedString(stringResource(R.string.info_repo)),
                onClick = { uriHandler.openUri("https://github.com/thestealth131205/FilamentManagerNFC") },
            )
        }
    }
}
