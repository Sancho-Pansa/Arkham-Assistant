package io.sanchopansa.arkham.android.ui

import io.sanchopansa.arkham.android.ui.models.InvestigatorUi
import io.sanchopansa.arkham.core.investigators.Investigator

sealed interface PlayerSheetUiState {
    object Loading : PlayerSheetUiState
    object ChoosingMode : PlayerSheetUiState
    data class ChoosingPlayerAction(val canRestore: Boolean) : PlayerSheetUiState
    object SessionLoadFailed : PlayerSheetUiState
    object InvestigatorLoadFailed : PlayerSheetUiState
    data class SelectingInvestigator(val investigators: List<Investigator>) : PlayerSheetUiState
    data class Ready(val investigatorUi: InvestigatorUi, val revision: Long = 0) :
        PlayerSheetUiState
}
