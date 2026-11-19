package io.sanchopansa.arkham.android.ui

import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import io.sanchopansa.arkham.android.MainActivity
import org.junit.Rule
import org.junit.Test

class InvestigatorSelectionTest {
    @get:Rule
    val compose = createEmptyComposeRule()

    @Test
    fun selectionAndChangedStatsSurviveActivityRecreationAndBackgrounding() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            compose.onNodeWithText("Аманда Шарп").assertExists()
            compose.onNodeWithText("Боб Дженкинс").performClick()
            compose.onNodeWithText("Начальная расстановка").assertExists()
            compose.onAllNodesWithContentDescription("Minus Sign")[0].performClick()

            scenario.recreate()
            scenario.moveToState(Lifecycle.State.CREATED)
            scenario.moveToState(Lifecycle.State.RESUMED)

            compose.onNodeWithText("Боб Дженкинс").assertExists()
            compose.onNodeWithText("5 / 6", substring = true).assertExists()
        }
    }

    @Test
    fun freshActivityAfterClosingThePreviousOneOffersSelectionAgain() {
        ActivityScenario.launch(MainActivity::class.java).use {
            compose.onNodeWithText("Боб Дженкинс").performClick()
            compose.onNodeWithText("Начать игру").performClick()
        }

        ActivityScenario.launch(MainActivity::class.java).use {
            compose.onNodeWithText("Выберите сыщика").assertExists()
            compose.onNodeWithText("Боб Дженкинс").assertExists()
        }
    }
}
