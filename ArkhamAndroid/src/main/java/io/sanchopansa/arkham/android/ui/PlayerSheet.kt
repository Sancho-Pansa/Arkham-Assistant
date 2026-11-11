package io.sanchopansa.arkham.android.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.sanchopansa.arkham.core.Phase

@Composable
fun PlayerSheet(
    modifier: Modifier = Modifier,
    playerSheetVm: PlayerSheetVm,
    onRetry: () -> Unit = {}
) {
    val vm = playerSheetVm
    val state = vm.uiState
    val error = vm.error
    Column(
        modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (error != null) {
            Text(error); Button(onClick = onRetry) { Text("Повторить") }
        }
        if (state !is PlayerSheetUiState.Ready) return@Column
        val p = state.investigatorUi
        val s = vm.session ?: return@Column
        Text(p.name, style = MaterialTheme.typography.headlineMedium)
        Text(
            if (s.isSetup) "Начальная расстановка" else when (s.phase) {
                Phase.UPKEEP -> "Передышка"
                Phase.MOVEMENT -> "Движение"
                Phase.ARKHAM_ENCOUNTER -> "Контакты"
                Phase.OTHER_WORLD_ENCOUNTER -> "Контакты Иных Миров"
                else -> "Миф"
            }
        )
        StatStepper(
            header = "Здоровье",
            value = p.stamina,
            max = p.maxStamina,
            minusAction = { vm.changeHealth(-1) },
            plusAction = { vm.changeHealth(1) })
        StatStepper(
            header = "Рассудок",
            value = p.sanity,
            max = p.maxSanity,
            minusAction = { vm.changeSanity(-1) },
            plusAction = { vm.changeSanity(1) })
        Text("Деньги: ${p.money} · Улики: ${p.clueTokens}")
        Row {
            TextButton(onClick = { vm.changeMoney(-1) }) { Text("−$") }
            TextButton(onClick = { vm.changeMoney(1) }) { Text("+$") }
            TextButton(onClick = { vm.changeClueTokens(-1) }) { Text("− улика") }
            TextButton(onClick = { vm.changeClueTokens(1) }) { Text("+ улика") }
        }
        Text(if (s.isSetup) "Свободная расстановка навыков" else if (s.isSkillsConfirmed) "Навыки" else "Доступная концентрация: ${s.remainingFocus}")
        val skills = listOf(p.speedSneak, p.fightWill, p.loreLuck)
        skills.forEachIndexed { index, skill ->
            BlueRedSlider(
                minBlue = skill.topValue,
                blueLabel = skill.topLabel,
                maxRed = skill.bottomValue,
                redLabel = skill.bottomLabel,
                initialIndex = s.positions[index],
                enabled = s.canEditSkills() && error == null,
                canSelect = { position -> s.canSetPosition(index, position) },
                onSelect = { vm.moveSkill(index, it) })
        }
        if (s.isSetup) Button(
            onClick = vm::startGame,
            enabled = error == null
        ) { Text("Начать игру") }
        else {
            if (s.canEditSkills()) {
                Button(
                    onClick = vm::confirmSkills,
                    enabled = error == null
                ) { Text("Сохранить положение навыков") }
                TextButton(
                    onClick = vm::resetSkills,
                    enabled = s.isDirty && error == null
                ) { Text("Сбросить") }
            }
            Button(
                onClick = vm::nextPhase,
                enabled = !s.isCombat && error == null
            ) { Text("Следующая фаза") }
            Button(
                onClick = vm::startCombat,
                enabled = s.phase != Phase.UPKEEP && !s.isCombat && error == null
            ) { Text("Бой") }
        }
        if (s.isCombat) {
            BackHandler { /* Combat must be ended explicitly. */ }
            AlertDialog(
                onDismissRequest = {}, title = { Text("Бой") }, text = {
                    Column {
                        Text("Механика боя появится позже.")
                        if (error != null) {
                            Text(error); TextButton(onClick = onRetry) { Text("Повторить") }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = vm::endCombat,
                        enabled = error == null
                    ) { Text("Завершить бой") }
                })
        }
        if (vm.pendingTransition) AlertDialog(
            onDismissRequest = vm::cancelTransition,
            title = { Text("Сохранить положение навыков?") },
            confirmButton = { TextButton(onClick = { vm.resolveTransition(true) }) { Text("Сохранить и перейти") } },
            dismissButton = {
                Column {
                    TextButton(onClick = { vm.resolveTransition(false) }) { Text("Сбросить и перейти") }
                    TextButton(onClick = vm::cancelTransition) { Text("Остаться") }
                }
            })
    }
}