package io.sanchopansa.arkham.android.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.sanchopansa.arkham.android.R
import io.sanchopansa.arkham.android.ui.models.InvestigatorUi

@Composable
fun InvestigatorDetails(
    investigator: InvestigatorUi,
    onSelect: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)
    Column(modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column {
                Text(investigator.name, style = MaterialTheme.typography.headlineMedium)
                Text(investigator.title, style = MaterialTheme.typography.titleMedium)
            }

            Text(stringResource(R.string.investigator_health, investigator.stamina))
            Text(stringResource(R.string.investigator_sanity, investigator.sanity))

            Column {
                Text(
                    stringResource(R.string.investigator_skills),
                    style = MaterialTheme.typography.titleLarge
                )
                listOf(
                    investigator.speedSneak,
                    investigator.fightWill,
                    investigator.loreLuck
                ).forEach { skill ->
                    BlueRedTable(
                        skill.topLabel,
                        skill.topValue,
                        skill.bottomLabel,
                        skill.bottomValue
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    stringResource(R.string.investigator_possessions),
                    style = MaterialTheme.typography.titleLarge
                )
                Text(stringResource(R.string.investigator_money, investigator.money))
                Text(stringResource(R.string.investigator_clues, investigator.clueTokens))
                Text(stringResource(R.string.investigator_resources_placeholder))
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    stringResource(R.string.investigator_ability),
                    style = MaterialTheme.typography.titleLarge
                )
                Text(investigator.abilityName, style = MaterialTheme.typography.titleMedium)
                Text(investigator.abilityDescription)
            }

            Text(stringResource(R.string.investigator_location_placeholder))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.investigator_examine_back))
            }
            Button(onClick = onSelect, modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.investigator_examine_select))
            }
        }
    }
}

@Composable
private fun BlueRedTable(
    blueLabel: String, blueRange: Int,
    redLabel: String, redRange: Int
) {
    TableRow(blueLabel, blueRange, 1, Modifier, Color.Blue)
    TableRow(redLabel, redRange, -1,Modifier, Color.Red)
    Spacer(Modifier.height(5.dp))
}

@Composable
private fun TableRow(title: String, start: Int, step: Int, modifier: Modifier, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier.fillMaxWidth(0.8f)) {
        Text(text = title, modifier = Modifier.weight(0.6f), fontWeight = FontWeight.Bold, color = color)
        repeat(4) { index ->
            Text(
                text = "${start + step * index}",
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(0.1f),
                color = color
            )
        }
    }
}