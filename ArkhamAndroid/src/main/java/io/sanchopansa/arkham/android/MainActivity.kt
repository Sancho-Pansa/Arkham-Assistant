package io.sanchopansa.arkham.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.sanchopansa.arkham.android.ui.PlayerSessionStore
import io.sanchopansa.arkham.android.ui.PlayerSheet
import io.sanchopansa.arkham.android.ui.PlayerSheetUiState
import io.sanchopansa.arkham.android.ui.PlayerSheetVm
import io.sanchopansa.arkham.android.ui.screens.InvestigatorSelect
import io.sanchopansa.arkham.android.ui.screens.PlayerSaveSelect
import io.sanchopansa.arkham.android.ui.screens.SelectModeMenu
import io.sanchopansa.arkham.android.ui.theme.AndroidTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val jsonSource = AndroidJsonSource(applicationContext)
        val sessionStore = PlayerSessionStore(applicationContext)
        val playerVm: PlayerSheetVm by viewModels()
        playerVm.initialize(sessionStore)
        enableEdgeToEdge()
        setContent {
            AndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val contentModifier = Modifier.padding(innerPadding)
                    when (val state = playerVm.uiState) {
                        PlayerSheetUiState.Loading -> CircularProgressIndicator(contentModifier)
                        PlayerSheetUiState.ChoosingMode -> {
                            SelectModeMenu(playerVm, contentModifier)
                        }

                        is PlayerSheetUiState.ChoosingPlayerAction -> {
                            PlayerSaveSelect(
                                playerVm,
                                state,
                                jsonSource,
                                contentModifier
                            )
                        }

                        PlayerSheetUiState.SessionLoadFailed -> {
                            BackHandler(onBack = playerVm::showPlayerMenu)
                            PlayerSheet(
                                modifier = contentModifier,
                                playerSheetVm = playerVm,
                                onRetry = { playerVm.restoreSession(jsonSource) }
                            )
                        }

                        PlayerSheetUiState.InvestigatorLoadFailed -> {
                            BackHandler(onBack = playerVm::showPlayerMenu)
                            Column(contentModifier.padding(16.dp)) {
                                Text(stringResource(R.string.investigators_load_error))
                                Button(onClick = { playerVm.loadInvestigatorChoices(jsonSource) }) {
                                    Text(stringResource(R.string.retry_investigator_load))
                                }
                            }
                        }

                        is PlayerSheetUiState.SelectingInvestigator -> {
                            InvestigatorSelect(
                                playerVm,
                                state,
                                contentModifier
                            )
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