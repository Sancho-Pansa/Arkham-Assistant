package io.sanchopansa.arkham.mechanics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.sanchopansa.arkham.core.Phase;
import io.sanchopansa.arkham.core.PlayerSession;

class PlayerSessionTest {
    @Test
    void draftUsesSharedBudgetAndCanBeReset() {
        var s = new PlayerSession(2, new int[]{0, 0, 0});
        assertTrue(s.setPosition(0, 3));
        s.startGame();
        assertTrue(s.setPosition(0, 2));
        assertTrue(s.setPosition(1, 1));
        assertFalse(s.setPosition(2, 1));
        assertTrue(s.setPosition(0, 3));
        assertEquals(1, s.getRemainingFocus());
        s.resetDraft();
        assertFalse(s.isDirty());
        s.confirmSkills();
        assertFalse(s.setPosition(1, 1));
    }

    @Test
    void phasesAndCombatAndRestore() {
        var s = new PlayerSession(2, new int[]{0, 0, 0});
        s.startGame();
        assertThrows(IllegalStateException.class, s::startCombat);
        s.nextPhase();
        s.startCombat();
        assertThrows(IllegalStateException.class, s::nextPhase);
        var restored = PlayerSession.restore(2, s.getCommittedPositions(), false, s.getPhase(), false, true);
        assertTrue(restored.isCombat());
        restored.endCombat();
        for (int i = 0; i < 4; i++) restored.nextPhase();
        assertEquals(Phase.UPKEEP, restored.getPhase());
        assertTrue(restored.canEditSkills());
    }

    @Test
    void dirtyDraftMustBeResolvedAndIsNotRestored() {
        var s = new PlayerSession(2, new int[]{0, 0, 0});
        s.startGame();
        s.setPosition(0, 1);
        assertThrows(IllegalStateException.class, s::nextPhase);
        var restored = PlayerSession.restore(2, s.getCommittedPositions(), false, s.getPhase(), false, false);
        assertEquals(0, restored.getPositions()[0]);
        assertThrows(IllegalArgumentException.class, () -> PlayerSession.restore(2, new int[]{0, 0, 0}, false, Phase.ANY, false, false));
    }

    @Test
    void queriesDoNotMutateAndSnapshotsAreDefensive() {
        var s = new PlayerSession(0, new int[]{0, 0, 0});
        assertTrue(s.canSetPosition(0, 3));
        assertEquals(0, s.getPositions()[0]);
        s.startGame();
        assertFalse(s.canSetPosition(0, 1));
        assertFalse(s.setPosition(0, 1));
        var positions = s.getCommittedPositions();
        positions[0] = 3;
        assertEquals(0, s.getCommittedPositions()[0]);
        assertFalse(s.canSetPosition(3, 0));
        assertFalse(s.canSetPosition(0, 4));
    }
}
