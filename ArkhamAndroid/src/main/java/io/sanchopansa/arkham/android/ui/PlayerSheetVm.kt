package io.sanchopansa.arkham.android.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.sanchopansa.arkham.api.JsonSource
import io.sanchopansa.arkham.core.Phase
import io.sanchopansa.arkham.core.PlayerSession
import io.sanchopansa.arkham.core.investigators.Investigator
import io.sanchopansa.arkham.json.JsonExtractor
import org.json.JSONArray
import org.json.JSONObject

class PlayerSheetVm : ViewModel() {
    var uiState by mutableStateOf<PlayerSheetUiState>(PlayerSheetUiState.Loading)
        private set
    var session by mutableStateOf<PlayerSession?>(null)
        private set
    var error by mutableStateOf<String?>(null)
        private set
    var pendingTransition by mutableStateOf(false)
        private set
    private lateinit var investigator: Investigator
    private var store: PlayerSessionStore? = null
    private var loaded = false
    private var pendingSave = false

    fun initialize(storage: PlayerSessionStore) {
        if (uiState != PlayerSheetUiState.Loading) return
        store = storage
        uiState = PlayerSheetUiState.ChoosingMode
    }

    fun showPlayerMenu() {
        error = null
        uiState = PlayerSheetUiState.ChoosingPlayerAction(store?.hasSnapshot() == true)
    }

    fun showModeMenu() {
        uiState = PlayerSheetUiState.ChoosingMode
    }

    fun restoreSession(source: JsonSource) {
        if ((uiState as? PlayerSheetUiState.ChoosingPlayerAction)?.canRestore != true &&
            uiState != PlayerSheetUiState.SessionLoadFailed
        ) return

        try {
            val snapshot = checkNotNull(store?.read()) { "Сохранение не найдено" }
            val name = snapshot.getString("investigator")
            val player = readInvestigators(source).single { it.name == name }
            openSession(player, snapshot)
        } catch (e: Exception) {
            reportSessionLoadError(e)
            uiState = PlayerSheetUiState.SessionLoadFailed
        }
    }

    fun loadInvestigatorChoices(source: JsonSource) {
        if (uiState !is PlayerSheetUiState.ChoosingPlayerAction &&
            uiState != PlayerSheetUiState.InvestigatorLoadFailed
        ) return

        uiState = try {
            PlayerSheetUiState.SelectingInvestigator(readInvestigators(source).sortedBy { it.name })
        } catch (e: Exception) {
            Log.e("PlayerSheetVm", "Failed to load investigators", e)
            PlayerSheetUiState.InvestigatorLoadFailed
        }
    }

    private fun readInvestigators(source: JsonSource): Set<Investigator> =
        JsonExtractor(source).extractInvestigatorsFromJson("Investigators.json").also {
            check(it.isNotEmpty()) { "Investigators.json is empty" }
        }

    fun selectInvestigator(player: Investigator) {
        if (uiState is PlayerSheetUiState.SelectingInvestigator) openSession(player, null)
    }

    fun loadInvestigator(player: Investigator, storage: PlayerSessionStore? = null) {
        if (loaded) return
        store = storage
        try {
            openSession(player, storage?.read())
        } catch (e: Exception) {
            reportSessionLoadError(e)
        }
    }

    private fun reportSessionLoadError(error: Exception) {
        this.error = "Не удалось загрузить сохранение: ${error.message}"
    }

    private fun openSession(player: Investigator, json: JSONObject?) {
        val state = if (json == null) {
            PlayerSession(
                player.focus.value,
                intArrayOf(player.ss.sliderIndex, player.fw.sliderIndex, player.ll.sliderIndex)
            )
        } else {
            require(json.getString("investigator") == player.name) { "Сохранение другого персонажа" }
            val positions = json.getJSONArray("positions")
            require(positions.length() == 3)
            val focus = json.getInt("focus")
            require(focus in 0..player.focus.currentMaximum)
            val restored = PlayerSession.restore(
                focus,
                IntArray(3) { positions.getInt(it) },
                json.getBoolean("setup"),
                Phase.valueOf(json.getString("phase")),
                json.getBoolean("confirmed"),
                json.getBoolean("combat")
            )
            val health = json.getInt("health")
            val sanity = json.getInt("sanity")
            require(health in 0..player.stamina.currentMaximum && sanity in 0..player.sanity.currentMaximum)
            val money = json.getInt("money")
            val clues = json.getInt("clues")
            player.stamina.add(health - player.stamina.value)
            player.sanity.add(sanity - player.sanity.value)
            player.focus.add(focus - player.focus.value)
            player.money = money
            player.clueTokens = clues
            restored
        }
        investigator = player
        session = state
        loaded = true
        applySkills(state)
        error = null
        publish()
    }

    private var revision = 0L
    private fun publish() {
        uiState = PlayerSheetUiState.Ready(investigator.toUiModel(), ++revision)
    }

    private fun save() {
        val storage = store ?: return
        val s = session ?: return
        val json = JSONObject().put("version", 1).put("investigator", investigator.name)
            .put("health", investigator.stamina.value).put("sanity", investigator.sanity.value)
            .put("money", investigator.money).put("clues", investigator.clueTokens)
            .put("focus", investigator.focus.value)
            .put("positions", JSONArray(s.committedPositions.toList())).put("setup", s.isSetup)
            .put("phase", s.phase.name).put("confirmed", s.isSkillsConfirmed)
            .put("combat", s.isCombat)
        try {
            storage.write(json); pendingSave = false; error = null
        } catch (e: Exception) {
            pendingSave = true; error = "Не удалось сохранить: ${e.message}"
        }
    }

    fun retry(player: Investigator, storage: PlayerSessionStore) {
        if (!loaded) loadInvestigator(player, storage) else retrySave()
    }

    fun retrySave() {
        if (pendingSave) save()
    }

    private fun act(action: (PlayerSession) -> Unit) {
        if (error != null) return
        val s = session ?: return
        action(s); publish(); save()
    }

    fun changeHealth(delta: Int) = act { investigator.stamina.add(delta) }
    fun changeSanity(delta: Int) = act { investigator.sanity.add(delta) }
    fun changeMoney(delta: Int) = act { investigator.money += delta }
    fun changeClueTokens(delta: Int) = act { investigator.clueTokens += delta }
    fun moveSkill(index: Int, position: Int) {
        if (error != null) return
        session?.setPosition(index, position); publish()
    }

    fun resetSkills() {
        session?.resetDraft(); publish()
    }

    fun confirmSkills() = act { s ->
        s.confirmSkills(); applySkills(s)
    }

    private fun applySkills(s: PlayerSession) {
        val p = s.committedPositions
        investigator.ss.setSkillPosition(p[0]); investigator.fw.setSkillPosition(p[1]); investigator.ll.setSkillPosition(
            p[2]
        )
    }

    fun startGame() = act { it.startGame(); applySkills(it) }
    fun nextPhase() {
        val s = session ?: return
        if (s.isDirty) pendingTransition = true else act { it.nextPhase() }
    }

    fun resolveTransition(saveSkills: Boolean) {
        pendingTransition = false
        act {
            if (saveSkills) {
                it.confirmSkills(); applySkills(it)
            } else it.resetDraft(); it.nextPhase()
        }
    }

    fun cancelTransition() {
        pendingTransition = false
    }

    fun startCombat() = act { it.startCombat() }
    fun endCombat() = act { it.endCombat() }
}
