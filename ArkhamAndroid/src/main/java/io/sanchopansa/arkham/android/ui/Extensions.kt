package io.sanchopansa.arkham.android.ui

import io.sanchopansa.arkham.android.ui.models.InvestigatorUi
import io.sanchopansa.arkham.android.ui.models.SkillUi
import io.sanchopansa.arkham.core.investigators.Investigator

fun Investigator.toUiModel() = InvestigatorUi(
    name = name,
    title = title,
    stamina = stamina.value,
    maxStamina = stamina.currentMaximum,
    sanity = sanity.value,
    maxSanity = sanity.currentMaximum,
    money = money,
    clueTokens = clueTokens,
    speedSneak = SkillUi("Скорость", "Скрытность", ss.leftBlue, ss.leftRed, ss.sliderIndex),
    fightWill = SkillUi("Сила", "Воля", fw.leftBlue, fw.leftRed, fw.sliderIndex),
    loreLuck = SkillUi("Знания", "Удача", ll.leftBlue, ll.leftRed, ll.sliderIndex),
    abilities = abilities
)
