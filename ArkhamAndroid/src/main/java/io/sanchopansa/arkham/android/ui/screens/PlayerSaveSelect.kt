package io.sanchopansa.arkham.android.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.sanchopansa.arkham.android.AndroidJsonSource
import io.sanchopansa.arkham.android.R
import io.sanchopansa.arkham.android.ui.PlayerSheetUiState
import io.sanchopansa.arkham.android.ui.PlayerSheetVm

/**
 * Окно выбора старого сохранения или начала новой партии.
 */
@Composable
fun PlayerSaveSelect(
    playerVm: PlayerSheetVm,
    state: PlayerSheetUiState.ChoosingPlayerAction,
    jsonSource: AndroidJsonSource,
    contentModifier: Modifier
) {
    BackHandler(onBack = playerVm::showModeMenu)
    StartupMenu(contentModifier) {
        Button(
            onClick = { playerVm.loadInvestigatorChoices(jsonSource) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.new_game))
        }
        OutlinedButton(
            onClick = { playerVm.restoreSession(jsonSource) },
            modifier = Modifier.fillMaxWidth(),
            enabled = state.canRestore
        ) {
            Text(stringResource(R.string.restore_game))
        }
    }
}