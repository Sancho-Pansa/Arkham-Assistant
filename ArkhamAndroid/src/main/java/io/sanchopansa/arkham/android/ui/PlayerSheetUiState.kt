package io.sanchopansa.arkham.android.ui

import io.sanchopansa.arkham.android.ui.models.InvestigatorUi
import io.sanchopansa.arkham.core.investigators.Investigator

sealed interface PlayerSheetUiState {
    object Loading : PlayerSheetUiState
    object ChoosingMode : PlayerSheetUiState
    data class ChoosingPlayerAction(val canRestore: Boolean) : PlayerSheetUiState
    object SessionLoadFailed : PlayerSheetUiState
    object InvestigatorLoadFailed : PlayerSheetUiState
    data class SelectingInvestigator(
        val investigators: List<Investigator>,
        val firstVisibleItemIndex: Int = 0,
        val firstVisibleItemScrollOffset: Int = 0
    ) : PlayerSheetUiState

    data class ExaminingInvestigator(
        val investigator: Investigator,
        val selection: SelectingInvestigator
    ) : PlayerSheetUiState

    data class Ready(
        val investigatorUi: InvestigatorUi,
        val skillSliders: List<SkillSliderUiState>,
        val remainingFocus: Int,
        val canEditSkills: Boolean,
        val revision: Long
    ) : PlayerSheetUiState
}

data class SkillSliderUiState(
    val selectedPosition: Int,
    val availablePositions: Set<Int>
)
