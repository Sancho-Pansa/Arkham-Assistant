package io.sanchopansa.arkham.core;

import java.util.Arrays;

/**
 * Rules for one local player's phase, skill adjustment and combat state.
 */
public final class PlayerSession {
    private static final Phase[] CYCLE = {Phase.UPKEEP, Phase.MOVEMENT, Phase.ARKHAM_ENCOUNTER,
            Phase.OTHER_WORLD_ENCOUNTER, Phase.MYTHOS};
    private final int focus;
    private int[] committed;
    private int[] draft;
    private boolean setup = true;
    private Phase phase = Phase.UPKEEP;
    private boolean skillsConfirmed;
    private boolean combat;

    public PlayerSession(int focus, int[] positions) {
        if (focus < 0 || positions == null || positions.length != 3
                || Arrays.stream(positions).anyMatch(p -> p < 0 || p > 3))
            throw new IllegalArgumentException("Invalid skill state");
        this.focus = focus;
        committed = positions.clone();
        draft = positions.clone();
    }

    public static PlayerSession restore(int focus, int[] positions, boolean setup, Phase phase,
                                        boolean confirmed, boolean combat) {
        if (phase == null || phase == Phase.ANY || (combat && (setup || phase == Phase.UPKEEP))
                || (setup && (phase != Phase.UPKEEP || confirmed)))
            throw new IllegalArgumentException("Invalid session state");
        var session = new PlayerSession(focus, positions);
        session.setup = setup;
        session.phase = phase;
        session.skillsConfirmed = confirmed;
        session.combat = combat;
        return session;
    }

    public boolean canEditSkills() {
        return setup || (phase == Phase.UPKEEP && !skillsConfirmed && !combat);
    }

    public boolean canSetPosition(int skill, int position) {
        if (skill < 0 || skill >= 3 || position < 0 || position > 3 || !canEditSkills())
            return false;
        int proposedCost = cost() - Math.abs(draft[skill] - committed[skill])
                + Math.abs(position - committed[skill]);
        return setup || proposedCost <= focus;
    }

    public boolean setPosition(int skill, int position) {
        if (!canSetPosition(skill, position)) return false;
        draft[skill] = position;
        return true;
    }

    private int cost() {
        int sum = 0;
        for (int i = 0; i < 3; i++) sum += Math.abs(draft[i] - committed[i]);
        return sum;
    }

    public int getRemainingFocus() {
        return setup ? focus : focus - cost();
    }

    public boolean isDirty() {
        return !Arrays.equals(committed, draft);
    }

    public void resetDraft() {
        draft = committed.clone();
    }

    public void startGame() {
        if (!setup) throw new IllegalStateException("Game already started");
        committed = draft.clone();
        setup = false;
    }

    public void confirmSkills() {
        if (setup || !canEditSkills())
            throw new IllegalStateException("Skills cannot be confirmed");
        committed = draft.clone();
        skillsConfirmed = true;
    }

    public void nextPhase() {
        if (setup || combat || isDirty()) throw new IllegalStateException("Cannot advance phase");
        phase = CYCLE[(phase.ordinal() + 1) % CYCLE.length];
        if (phase == Phase.UPKEEP) skillsConfirmed = false;
    }

    public void startCombat() {
        if (setup || phase == Phase.UPKEEP || combat)
            throw new IllegalStateException("Cannot start combat");
        combat = true;
    }

    public void endCombat() {
        combat = false;
    }

    public int[] getPositions() {
        return draft.clone();
    }

    public int[] getCommittedPositions() {
        return committed.clone();
    }

    public boolean isSetup() {
        return setup;
    }

    public Phase getPhase() {
        return phase;
    }

    public boolean isSkillsConfirmed() {
        return skillsConfirmed;
    }

    public boolean isCombat() {
        return combat;
    }
}
