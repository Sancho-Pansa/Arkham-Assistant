package io.sanchopansa.arkham.android.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.sanchopansa.arkham.android.R
import io.sanchopansa.arkham.android.ui.PlayerSheetUiState
import io.sanchopansa.arkham.android.ui.PlayerSheetVm

@Composable
fun InvestigatorSelect(
    playerVm: PlayerSheetVm,
    state: PlayerSheetUiState.SelectingInvestigator,
    contentModifier: Modifier
) {
    BackHandler(onBack = playerVm::showPlayerMenu)
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