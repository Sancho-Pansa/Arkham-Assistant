package io.sanchopansa.arkham.android.ui

import io.sanchopansa.arkham.android.ui.models.InvestigatorUi
import io.sanchopansa.arkham.android.ui.models.SkillUi
import io.sanchopansa.arkham.core.investigators.Investigator

fun Investigator.toUiModel() = InvestigatorUi(
    name,
    stamina.value,
    stamina.currentMaximum,
    sanity.value,
    sanity.currentMaximum,
    money,
    clueTokens,
    SkillUi("Скорость", "Скрытность", ss.leftBlue, ss.leftRed, ss.sliderIndex),
    SkillUi("Бой", "Воля", fw.leftBlue, fw.leftRed, fw.sliderIndex),
    SkillUi("Знания", "Удача", ll.leftBlue, ll.leftRed, ll.sliderIndex)
)
