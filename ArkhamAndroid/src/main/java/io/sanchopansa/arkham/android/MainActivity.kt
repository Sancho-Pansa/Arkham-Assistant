package io.sanchopansa.arkham.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.sanchopansa.arkham.android.ui.PlayerSheet
import io.sanchopansa.arkham.android.ui.PlayerSheetUiState
import io.sanchopansa.arkham.android.ui.PlayerSheetVm
import io.sanchopansa.arkham.android.ui.theme.AndroidTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val jsonSource = AndroidJsonSource(applicationContext)
        val playerVm: PlayerSheetVm by viewModels()
        playerVm.loadInvestigatorChoices(jsonSource)
        enableEdgeToEdge()
        setContent {
            AndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val contentModifier = Modifier.padding(innerPadding)
                    when (val state = playerVm.uiState) {
                        PlayerSheetUiState.Loading -> CircularProgressIndicator(contentModifier)
                        PlayerSheetUiState.InvestigatorLoadFailed -> {
                            Column(contentModifier.padding(16.dp)) {
                                Text(stringResource(R.string.investigators_load_error))
                                Button(onClick = { playerVm.loadInvestigatorChoices(jsonSource) }) {
                                    Text(stringResource(R.string.retry_investigator_load))
                                }
                            }
                        }

                        is PlayerSheetUiState.SelectingInvestigator -> {
                            LazyColumn(contentModifier.fillMaxSize()) {
                                item {
                                    Text(
                                        text = stringResource(R.string.choose_investigator),
                                        modifier = Modifier.padding(16.dp),
                                        style = MaterialTheme.typography.headlineSmall
                                    )
                                }
                                items(state.investigators) { investigator ->
                                    ListItem(
                                        headlineContent = { Text(investigator.name) },
                                        modifier = Modifier.clickable {
                                            playerVm.selectInvestigator(investigator)
                                        }
                                    )
                                }
                            }
                        }

                        is PlayerSheetUiState.Ready -> PlayerSheet(
                            modifier = contentModifier,
                            playerSheetVm = playerVm
                        )
                    }
                }
            }
        }
    }
}
