package io.sanchopansa.arkham;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import io.sanchopansa.arkham.api.JsonSource;
import io.sanchopansa.arkham.core.Game;
import io.sanchopansa.arkham.core.GameEngine;
import io.sanchopansa.arkham.core.GameListener;
import io.sanchopansa.arkham.core.GameVault;
import io.sanchopansa.arkham.core.investigators.Investigator;
import io.sanchopansa.arkham.core.locations.Location;
import io.sanchopansa.arkham.core.monsters.Ancient;
import io.sanchopansa.arkham.factories.AbstractGameFactory;
import io.sanchopansa.arkham.factories.JsonGameFactory;
import io.sanchopansa.arkham.impl.ClasspathJsonSource;

class GameEngineTest {

    private static GameVault gameVault;
    private static Graph<Location, DefaultEdge> map;
    private static Multiset<Ancient> ancientsPool;
    private static Multiset<Investigator> investigatorsPool;
    private static Game gameInstance;

    @BeforeAll
    public static void testGameLaunch() throws Exception {
        JsonSource jsonSource = new ClasspathJsonSource(GameEngineTest.class.getClassLoader());
        AbstractGameFactory factory = new JsonGameFactory(jsonSource);
        gameVault = factory.createVault();
        map = factory.createMap();
        ancientsPool = HashMultiset.create(gameVault.getAncientsPool());
        investigatorsPool = HashMultiset.create(gameVault.getInvestigators());
    }

    @Test
    public void testEngineInit() {
        List<String> players = new ArrayList<>(List.of(new String[]{"Аманда Шарп", "Глория Гольдберг", "Декстер Дрейк"}));
        Investigator[] investigators = new Investigator[3];
        investigatorsPool.stream()
                .filter(i -> players.contains(i.getName()))
                .toList()
                .toArray(investigators);
        Ancient a = ancientsPool.stream().toList().get(0);
        gameInstance = new Game(a, investigators);
        GameListener listener = new GameListener() {
            @Override
            public void onPhaseStart(Game gameState, GameVault gameVault) {
                System.out.println("Phase Started");
            }

            @Override
            public void onGameWin() {
                System.out.println("Game Won");
            }

            @Override
            public void onGameOver() {
                System.out.println("Game Over");
            }
        };
        GameEngine engine = new GameEngine(gameInstance, gameVault, map, listener);
        Assertions.assertNotNull(engine);
    }

    @Test
    public void testUpkeepRestoresInvestigatorFocus() {
        Investigator investigator = gameVault.getInvestigators().iterator().next();
        investigator.getFocus().sub(1);
        Game game = new Game(gameVault.getAncientsPool().get(0), investigator);
        GameListener listener = new GameListener() {
            @Override
            public void onPhaseStart(Game gameState, GameVault gameVault) {
            }

            @Override
            public void onGameWin() {
            }

            @Override
            public void onGameOver() {
            }
        };
        GameEngine engine = new GameEngine(game, gameVault, map, listener);

        engine.upkeep();

        Assertions.assertEquals(
                investigator.getFocus().getInitialMaximum(),
                investigator.getFocus().getValue()
        );
    }
}
