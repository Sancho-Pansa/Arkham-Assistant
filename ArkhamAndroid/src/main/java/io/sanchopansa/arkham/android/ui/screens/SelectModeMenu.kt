package io.sanchopansa.arkham.android.ui.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.sanchopansa.arkham.android.R
import io.sanchopansa.arkham.android.ui.PlayerSheetVm

@Composable
fun SelectModeMenu(
    playerVm: PlayerSheetVm,
    contentModifier: Modifier
) {
    StartupMenu(contentModifier) {
        Button(
            onClick = playerVm::showPlayerMenu,
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