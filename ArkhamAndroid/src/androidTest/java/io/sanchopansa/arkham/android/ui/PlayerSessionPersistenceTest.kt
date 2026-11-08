package io.sanchopansa.arkham.android.ui

import androidx.test.platform.app.InstrumentationRegistry
import io.sanchopansa.arkham.android.AndroidJsonSource
import io.sanchopansa.arkham.core.Phase
import io.sanchopansa.arkham.factories.JsonGameFactory
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PlayerSessionPersistenceTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val fileName = "player-session-test.json"
    private fun player() =
        JsonGameFactory(AndroidJsonSource(context)).createVault().investigators.single { it.name == "Аманда Шарп" }

    private fun store() = PlayerSessionStore(context, fileName)

    @Before
    fun clean() {
        context.deleteFile(fileName); context.deleteFile("$fileName.bak")
    }

    @After
    fun finish() = clean()
    private fun load() = PlayerSheetVm().apply { loadInvestigator(player(), store()) }

    @Test
    fun committedStateAndCombatSurviveRestart() {
        val vm = load(); vm.moveSkill(
            0,
            3
        ); vm.startGame(); vm.changeHealth(-1); vm.changeSanity(-1)
        vm.confirmSkills(); vm.nextPhase(); vm.startCombat()
        val restored = load()
        assertTrue(restored.session!!.isCombat); assertEquals(
            Phase.MOVEMENT,
            restored.session!!.phase
        )
        assertEquals(
            3,
            restored.session!!.positions[0]
        ); assertTrue(restored.session!!.isSkillsConfirmed)
        val original = (vm.uiState as PlayerSheetUiState.Ready).investigatorUi
        val actual = (restored.uiState as PlayerSheetUiState.Ready).investigatorUi
        assertEquals(original, actual)
        restored.endCombat(); assertFalse(load().session!!.isCombat)
    }

    @Test
    fun draftIsDiscardedAndTransitionsCanBeCancelledOrDiscarded() {
        val vm = load(); vm.startGame(); vm.moveSkill(0, 1)
        assertEquals(0, load().session!!.positions[0])
        vm.nextPhase(); assertTrue(vm.pendingTransition); vm.cancelTransition()
        assertEquals(Phase.UPKEEP, vm.session!!.phase); assertTrue(vm.session!!.isDirty)
        vm.nextPhase(); vm.resolveTransition(false)
        assertEquals(Phase.MOVEMENT, load().session!!.phase)
        assertEquals(0, load().session!!.positions[0])
    }

    @Test
    fun savingOnTransitionPersistsPositions() {
        val vm = load(); vm.startGame(); vm.moveSkill(0, 1); vm.nextPhase(); vm.resolveTransition(
            true
        )
        assertEquals(
            1,
            load().session!!.positions[0]
        ); assertTrue(load().session!!.isSkillsConfirmed)
    }

    @Test
    fun corruptSaveIsPreserved() {
        context.openFileOutput(fileName, 0).use { it.write("broken".toByteArray()) }
        val vm = load(); assertNotNull(vm.error); assertNull(vm.session)
        assertEquals(
            "broken",
            context.openFileInput(fileName).bufferedReader().use { it.readText() })
    }

    @Test
    fun saveFailureCanBeRetriedDuringCombat() {
        val vm = load(); vm.startGame(); vm.nextPhase()
        val target = java.io.File(context.filesDir, fileName)
        assertTrue(target.delete()); assertTrue(target.mkdir())
        vm.startCombat(); assertNotNull(vm.error); assertTrue(vm.session!!.isCombat)
        assertTrue(target.delete())
        vm.retry(player(), store()); assertNull(vm.error)
        assertTrue(load().session!!.isCombat)
    }
}
