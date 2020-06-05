package be.ac.umons;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.ArrayList;

import be.ac.umons.game.Extension;
import be.ac.umons.game.GameLevel;
import be.ac.umons.model.Container;
import be.ac.umons.model.Laser;
import be.ac.umons.model.Map;
import be.ac.umons.model.hexagon.Empty;
import be.ac.umons.model.hexagon.Gate;
import be.ac.umons.model.hexagon.Hexagon;
import be.ac.umons.model.hexagon.OneWayMirror;
import be.ac.umons.model.hexagon.Source;
import be.ac.umons.model.hexagon.Splitter;
import be.ac.umons.model.hexagon.Target;
import be.ac.umons.model.hexagon.ThreeWayMirror;
import be.ac.umons.model.hexagon.TwoWayMirror;
import be.ac.umons.model.levelChecker.Hint;
import be.ac.umons.model.levelChecker.LevelChecker;
import be.ac.umons.model.levelChecker.Solution;
import be.ac.umons.model.xml.GameData;
import be.ac.umons.model.xml.XMLGameLoader;
import be.ac.umons.util.Color;
import be.ac.umons.util.Edge;
import be.ac.umons.util.Pos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(GdxTestRunner.class)
public class TestLevelChecker {

	@BeforeClass
	public static void beforeClass() {
		HexaMaze.settings.setExtension(Extension.INTENSITY, true);
		HexaMaze.settings.setExtension(Extension.COLOR, true);
		Gdx.app.setLogLevel(Application.LOG_NONE);
	}

	@AfterClass
	public static void afterClass() {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
	}

	private Map emptyMap(int width, int height) {
		Map map = new Map(width, height);
		for (int y = 0; y < map.getHeight(); ++y) {
			for (int x = 0; x < map.getWidth(); ++x) {
				map.setHexagon(new Empty(), new Pos(x, y));
			}
		}
		return map;
	}

	private void addSourceTarget(Map map) {
		map.setHexagon(new Source(new Laser(true, true), Edge.SOUTHEAST, false, false, null), new Pos(0, 1));
		map.setHexagon(new Target(Edge.SOUTHEAST, false, false, null), new Pos(0, 0));
	}

	@Test
	public void testGetSolutions() {
		int width = 3;
		int height = 2;
		Map map = emptyMap(width, height);
		addSourceTarget(map);

		Container container = new Container();
		container.addHexagon(new TwoWayMirror());
		container.addHexagon(new ThreeWayMirror());

		LevelChecker levelChecker = new LevelChecker(map, container);
		ArrayList<Solution> solutions = levelChecker.getSolutions();
		assertEquals(2, solutions.size());

		Map sol1 = emptyMap(width, height);
		addSourceTarget(sol1);
		sol1.setHexagon(new TwoWayMirror(Edge.NORTH), new Pos(1, 0));
		sol1.setHexagon(new ThreeWayMirror(Edge.NORTHEAST), new Pos(2, 0));
		assertEquals(sol1, new Map(solutions.get(0)));
		sol1.sendLaser();
		assertTrue(sol1.hasWon());

		Map sol2 = emptyMap(width, height);
		addSourceTarget(sol2);
		sol2.setHexagon(new TwoWayMirror(Edge.NORTHEAST), new Pos(1, 1));
		sol2.setHexagon(new ThreeWayMirror(Edge.NORTH), new Pos(1, 0));
		assertEquals(sol2, new Map(solutions.get(1)));
		sol2.sendLaser();
		assertTrue(sol2.hasWon());
	}

	@Test
	public void testWin() {
		int width = 5;
		int height = 5;
		Map map = new Map(width, height, true);
		map.setHexagon(new Source(new Laser(new Color[]{Color.RED}, 1.0f, false, true), Edge.NORTHEAST, false, false, null), new Pos(0, 0));
		map.setHexagon(new Target(Edge.SOUTHWEST, false, false, null), new Pos(4, 4));
		Container container = new Container();
		for (int i = 0; i < 10; ++i)
			container.addHexagon(new TwoWayMirror());

		LevelChecker levelChecker = new LevelChecker(map, container);
		for (Solution solution : levelChecker.getSolutions()) {
			Map solMap = new Map(solution);
			solMap.sendLaser();
			assertTrue(solMap.hasWon());
		}
	}
	
	@Test
	public void testSplitter() {
		int width = 5;
		int height = 5;
		Map map = new Map(width, height);
		for (int y = 0; y < map.getHeight(); ++y) {
			for (int x = 0; x < map.getWidth(); ++x) {
				map.setHexagon(new Empty(), new Pos(x, y));
			}
		}
		// Set high intensity to the laser so that it does not disappear
		map.setHexagon(new Source(new Laser(new Color[]{Color.RED}, 10.0f, true, true), Edge.NORTHEAST, false, false, null), new Pos(0, 0));
		map.setHexagon(new Target(Edge.SOUTHWEST, false, false, null), new Pos(4, 4));
		map.setHexagon(new Target(Edge.SOUTHEAST, false, false, null), new Pos(0, 4));
		Container container = new Container();
		container.addHexagon(new Splitter(2));
		container.addHexagon(new TwoWayMirror());

		LevelChecker levelChecker = new LevelChecker(map, container);
		for (Solution solution : levelChecker.getSolutions()) {
			Map solMap = new Map(solution);
			solMap.sendLaser();
			assertTrue(solMap.hasWon());
		}
	}

	private int numberOfMovesToWin(Map map, Container container) {
		Hint hint = new Hint(map, container, true);
		for (int i = 0; ; ++i) {
			map.sendLaser();
			if (map.hasWon())
				return i;
			hint.apply();
		}
	}

	private void debugHint(Map map, Container container) {
		LevelChecker levelChecker = new LevelChecker(map, container);
		System.out.println("======== Solutions ========");
		for (Solution solution : levelChecker.getSolutions()) {
			System.out.println(new Map(solution));
		}
		Hint hint = new Hint(map, container, true);
		System.out.println("===========================");
		System.out.println("Initial map : ");
		System.out.println(map);
		for (int i = 0; ; ++i) {
			map.sendLaser();
			if (map.hasWon())
				break;
			System.out.println("====== Hint number " + i + " ======");
			System.out.print("Container : ");
			for (Hexagon hex : container.getHexagons())
				System.out.print(hex + " ");
			System.out.println();
			hint.apply();
			System.out.println("Hint applied : ");
			System.out.println(map);
		}

		System.out.println("Final map : ");
		System.out.println(map);
	}

	@Test
	public void testHint() {
		Map map = new Map(5, 4, true);
		map.setHexagon(new Source(new Laser(new Color[]{Color.RED}, 1.f, false, true), Edge.NORTH, false, false, null), new Pos(0, 0));
		map.setHexagon(new Target(Edge.SOUTH, false, false, null), new Pos(4, 3));
		map.setHexagon(new Gate(Edge.NORTHEAST, true, false, null), new Pos(4, 2));
		map.setHexagon(new Gate(Edge.NORTH, false, false, null), new Pos(4, 1));
		Container container = new Container();

		container.addHexagon(new OneWayMirror());
		container.addHexagon(new Splitter(2));
		container.addHexagon(new ThreeWayMirror());

		assertEquals(4, numberOfMovesToWin(map, container));
	}

	@Test
	public void testAllSolutions() {
		ArrayList<GameLevel> levels = GameLevel.createLevelArray();
		for (GameLevel level : levels) {
			GameData gameData = new XMLGameLoader(new File("levels" + File.separator + level.getXml())).getGameData();
			ArrayList<Solution> solutions = new LevelChecker(gameData.getMap(), gameData.getContainer()).getSolutions();
			assertTrue(solutions.size() >= 1);
			for (Solution solution : solutions) {
				Map solutionMap = new Map(solution);
				solutionMap.sendLaser();
				assertTrue(solutionMap.hasWon());
				assertFalse(solutionMap.hasLost());
			}
		}
	}
}
