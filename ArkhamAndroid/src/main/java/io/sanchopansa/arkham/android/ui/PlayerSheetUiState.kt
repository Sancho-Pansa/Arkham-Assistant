package io.sanchopansa.arkham.android.ui

import io.sanchopansa.arkham.android.ui.models.InvestigatorUi

sealed interface PlayerSheetUiState {
    object Loading : PlayerSheetUiState
    data class Ready(val investigatorUi: InvestigatorUi, val revision: Long = 0) :
        PlayerSheetUiState
}