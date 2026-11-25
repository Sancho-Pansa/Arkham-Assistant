package io.sanchopansa.arkham.android.ui

import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso.pressBack
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import io.sanchopansa.arkham.android.MainActivity
import org.junit.After
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class InvestigatorSelectionTest {
    @get:Rule
    val compose = createEmptyComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private var cleanSession = false

    @Before
    fun requireCleanSession() {
        assertNull(PlayerSessionStore(context).read())
        cleanSession = true
    }

    @After
    fun removeTestSession() {
        if (cleanSession) context.deleteFile("player-session.json")
    }

    @Test
    fun selectionAndChangedStatsSurviveActivityRecreationAndBackgrounding() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            compose.onNodeWithText("Режим ведущего").assertIsNotEnabled()
            compose.onNodeWithText("Выберите сыщика").assertDoesNotExist()
            compose.onNodeWithText("Режим игрока").performClick()
            compose.onNodeWithText("Восстановить предыдущую партию").assertIsNotEnabled()
            compose.onNodeWithText("Начать новую партию").performClick()
            pressBack()
            compose.onNodeWithText("Начать новую партию").assertExists()
            pressBack()
            compose.onNodeWithText("Режим игрока").performClick()
            compose.onNodeWithText("Начать новую партию").performClick()
            scenario.recreate()
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
    fun freshActivityOffersToRestoreTheSavedSession() {
        ActivityScenario.launch(MainActivity::class.java).use {
            compose.onNodeWithText("Режим игрока").performClick()
            compose.onNodeWithText("Начать новую партию").performClick()
            compose.onNodeWithText("Боб Дженкинс").performClick()
            compose.onNodeWithText("Начать игру").performClick()
        }

        ActivityScenario.launch(MainActivity::class.java).use {
            compose.onNodeWithText("Режим игрока").performClick()
            compose.onNodeWithText("Восстановить предыдущую партию").assertIsEnabled()
            compose.onNodeWithText("Начать новую партию").performClick()
            compose.onNodeWithText("Выберите сыщика").assertExists()
            pressBack()
            compose.onNodeWithText("Восстановить предыдущую партию").performClick()
            compose.onNodeWithText("Боб Дженкинс").assertExists()
            compose.onNodeWithText("Передышка").assertExists()
        }
    }
}
