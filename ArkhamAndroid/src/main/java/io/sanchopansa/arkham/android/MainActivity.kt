package io.sanchopansa.arkham.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.sanchopansa.arkham.android.ui.PlayerSessionStore
import io.sanchopansa.arkham.android.ui.PlayerSheet
import io.sanchopansa.arkham.android.ui.PlayerSheetUiState
import io.sanchopansa.arkham.android.ui.PlayerSheetVm
import io.sanchopansa.arkham.android.ui.theme.AndroidTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val jsonSource = AndroidJsonSource(applicationContext)
        val sessionStore = PlayerSessionStore(applicationContext)
        val playerVm: PlayerSheetVm by viewModels()
        playerVm.initialize(jsonSource, sessionStore)
        enableEdgeToEdge()
        setContent {
            AndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val contentModifier = Modifier.padding(innerPadding)
                    when (val state = playerVm.uiState) {
                        PlayerSheetUiState.Loading -> CircularProgressIndicator(contentModifier)
                        PlayerSheetUiState.ChoosingMode -> {
                            Column(
                                modifier = contentModifier
                                    .fillMaxSize()
                                    .padding(24.dp),
                                verticalArrangement = Arrangement.spacedBy(
                                    12.dp,
                                    Alignment.CenterVertically
                                )
                            ) {
                                Button(
                                    onClick = { playerVm.loadInvestigatorChoices(jsonSource) },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(stringResource(R.string.player_mode))
                                }
                                OutlinedButton(
                                    onClick = {},
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = false
                                ) {
                                    Text(stringResource(R.string.game_master_mode))
                                }
                            }
                        }

                        PlayerSheetUiState.SessionLoadFailed -> PlayerSheet(
                            modifier = contentModifier,
                            playerSheetVm = playerVm,
                            onRetry = { playerVm.initialize(jsonSource, sessionStore) }
                        )

                        PlayerSheetUiState.InvestigatorLoadFailed -> {
                            Column(contentModifier.padding(16.dp)) {
                                Text(stringResource(R.string.investigators_load_error))
                                Button(onClick = { playerVm.loadInvestigatorChoices(jsonSource) }) {
                                    Text(stringResource(R.string.retry_investigator_load))
                                }
                            }
                        }

                        is PlayerSheetUiState.SelectingInvestigator -> {
                            LazyColumn(
                                modifier = contentModifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(1.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                contentPadding = PaddingValues(horizontal = 50.dp)
                            ) {
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
                                        modifier = Modifier
                                            .clickable {
                                                playerVm.selectInvestigator(investigator)
                                            }
                                            .border(
                                                width = 1.dp,
                                                color = Color.LightGray,
                                                shape = CutCornerShape(5.dp)
                                            )
                                    )
                                }
                            }
                        }

                        is PlayerSheetUiState.Ready -> PlayerSheet(
                            modifier = contentModifier,
                            playerSheetVm = playerVm,
                            onRetry = playerVm::retrySave
                        )
                    }
                }
            }
        }
    }
}
