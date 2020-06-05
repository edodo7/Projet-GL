package be.ac.umons;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import be.ac.umons.game.Difficulty;
import be.ac.umons.game.GameLevel;
import be.ac.umons.model.Container;
import be.ac.umons.model.Laser;
import be.ac.umons.model.Map;
import be.ac.umons.model.hexagon.Empty;
import be.ac.umons.model.hexagon.Source;
import be.ac.umons.model.hexagon.Target;
import be.ac.umons.model.hexagon.TwoWayMirror;
import be.ac.umons.model.hexagon.Wall;
import be.ac.umons.model.xml.XMLGameLoader;
import be.ac.umons.util.Edge;
import be.ac.umons.util.Pos;

import static org.junit.Assert.assertEquals;

@RunWith(GdxTestRunner.class)
public class TestXMLGameLoader {
	private XMLGameLoader xmlGameLoader;

	@Before
	public void init() {
		xmlGameLoader = new XMLGameLoader(new File("levels/test_level.xml"));
	}

	@Test
	public void testLevel() {
		GameLevel loadedLevel = xmlGameLoader.getGameData().getLevel();
		assertEquals("test_level.xml", loadedLevel.getXml());
		assertEquals("test.tmx", loadedLevel.getTmx());
		assertEquals("Laser reflection", loadedLevel.getName());
		assertEquals(Difficulty.EASY, loadedLevel.getDifficulty());
	}

	@Test
	public void testMap() {
		Map loadedMap = xmlGameLoader.getGameData().getMap();
		Map map = new Map(3, 4);
		for (int y = 0; y < map.getHeight(); ++y) {
			for (int x = 0; x < map.getWidth(); ++x) {
				map.setHexagon(new Empty(), new Pos(x, y));
			}
		}
		map.setHexagon(new Source(new Laser(true, true), Edge.NORTH, true, false, null), new Pos(0, 0));
		map.setHexagon(new Target(Edge.NORTH, true, false, null), new Pos(2, 0));
		for (int y = 0; y <= 2; ++y) {
			map.setHexagon(new Wall(Edge.NORTH, false, false, null), new Pos(1, y));
		}
		assertEquals(map.getHexagon(new Pos(0, 0)), loadedMap.getHexagon(new Pos(0, 0)));
		assertEquals(map, loadedMap);
	}

	@Test
	public void testContainer() {
		Container loadedContainer = xmlGameLoader.getGameData().getContainer();
		Container container = new Container();
		container.addHexagon(new TwoWayMirror());
		container.addHexagon(new TwoWayMirror());
		container.addHexagon(new TwoWayMirror());
		assertEquals(container, loadedContainer);
	}
}
