package io.sanchopansa.arkham.android.ui.models;

import io.sanchopansa.arkham.core.investigators.Investigator

data class InvestigatorUi(
    val name: String,
    val title: String,
    val stamina: Int,
    val maxStamina: Int,
    val sanity: Int,
    val maxSanity: Int,
    val money: Int,
    val clueTokens: Int,
    val speedSneak: SkillUi,
    val fightWill: SkillUi,
    val loreLuck: SkillUi,
    val abilities: List<Investigator.Ability>
)

data class SkillUi(
    val topLabel: String,
    val bottomLabel: String,
    val topValue: Int,
    val bottomValue: Int,
    val currentIndex: Int
)
