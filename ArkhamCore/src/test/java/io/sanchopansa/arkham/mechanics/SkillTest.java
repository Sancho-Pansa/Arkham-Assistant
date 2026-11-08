package io.sanchopansa.arkham.mechanics;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.sanchopansa.arkham.core.investigators.Skill;

public class SkillTest {
    @Test
    public void testInitialValues() {
        Skill skill = new Skill(1, 4);

        Assertions.assertEquals(1, skill.getLeftBlue());
        Assertions.assertEquals(4, skill.getRightBlue());
        Assertions.assertEquals(4, skill.getLeftRed());
        Assertions.assertEquals(1, skill.getRightRed());
        Assertions.assertEquals(1, skill.getBlueValue());
        Assertions.assertEquals(4, skill.getRedValue());
        Assertions.assertEquals(0, skill.getSliderIndex());
    }

    @Test
    public void testSliderMovesAcrossAllPositions() {
        Skill skill = new Skill(1, 4);

        for (int expectedPosition = 1; expectedPosition < 4; expectedPosition++) {
            skill.moveRight();
            Assertions.assertEquals(expectedPosition, skill.getSliderIndex());
        }

        Assertions.assertEquals(4, skill.getBlueValue());
        Assertions.assertEquals(1, skill.getRedValue());

        skill.moveLeft();
        Assertions.assertEquals(2, skill.getSliderIndex());
        Assertions.assertEquals(3, skill.getBlueValue());
        Assertions.assertEquals(2, skill.getRedValue());
    }

    @Test
    public void testSliderDoesNotMovePastEitherEnd() {
        Skill skill = new Skill(1, 4);

        skill.moveLeft();
        Assertions.assertEquals(0, skill.getSliderIndex());

        skill.setSkillPosition(3);
        skill.moveRight();
        Assertions.assertEquals(3, skill.getSliderIndex());
        Assertions.assertEquals(4, skill.getBlueValue());
        Assertions.assertEquals(1, skill.getRedValue());
    }
}
