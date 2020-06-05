package be.ac.umons;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.EnumMap;
import java.util.List;

import be.ac.umons.model.Laser;
import be.ac.umons.model.Map;
import be.ac.umons.model.hexagon.Amplifier;
import be.ac.umons.model.hexagon.Converter;
import be.ac.umons.model.hexagon.Empty;
import be.ac.umons.model.hexagon.GenericBridge;
import be.ac.umons.model.hexagon.Hexagon;
import be.ac.umons.model.hexagon.Source;
import be.ac.umons.model.hexagon.Splitter;
import be.ac.umons.model.hexagon.Target;
import be.ac.umons.model.hexagon.ThreeWayMirror;
import be.ac.umons.model.hexagon.TwoWayMirror;
import be.ac.umons.util.Color;
import be.ac.umons.util.Edge;
import be.ac.umons.util.Pos;
import be.ac.umons.util.Segment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(GdxTestRunner.class)
public class TestMap {

	private int width = 3;
	private int height = 3;
	private Map map;

	@Before
	public void initMap() {

		Hexagon hexagons[][] = new Hexagon[][]{
				{new TwoWayMirror(Edge.NORTHEAST), new ThreeWayMirror(), new Target(Edge.NORTHWEST)},
				{new ThreeWayMirror(Edge.NORTHEAST), new GenericBridge(new boolean[]{true, false, true}), new Empty()},
				{new Source(new Laser(true, true), Edge.SOUTHEAST), new Splitter(2, Edge.NORTHWEST), new Empty()}
		};
		hexagons[1][1].setModifier(new Amplifier());
		hexagons[2][1].setModifier(new Converter(Color.RED));
		map = new Map(width, height);
		map.setHexagons(hexagons);
	}

	@Test
	public void testNextPos() {
		Pos pos = new Pos(0, 2);
		pos = map.getNextPos(pos, Edge.SOUTHEAST);
		assertEquals(new Pos(1, 2), pos);
		pos = map.getNextPos(pos, Edge.SOUTH);
		assertEquals(new Pos(1, 1), pos);
		pos = map.getNextPos(pos, Edge.SOUTH);
		assertEquals(new Pos(1, 0), pos);
		pos = map.getNextPos(pos, Edge.NORTHWEST);
		assertEquals(new Pos(0, 0), pos);
		pos = map.getNextPos(pos, Edge.NORTH);
		assertEquals(new Pos(0, 1), pos);
		pos = map.getNextPos(pos, Edge.SOUTHEAST);
		assertEquals(new Pos(1, 1), pos);
		pos = map.getNextPos(pos, Edge.SOUTHEAST);
		assertEquals(new Pos(2, 0), pos);
		assertEquals(new Pos(-1, 0), map.getNextPos(new Pos(0, 0), Edge.SOUTHWEST));
	}

	public boolean winWithoutHexagon(Pos pos) {
		Hexagon hex = map.getHexagon(pos);
		map.removeHexagon(pos);
		map.sendLaser();
		boolean win = map.hasWon();
		map.setHexagon(hex, pos);
		return win;
	}

	@Test
	public void sendLaser() {
		map.sendLaser();
		assertTrue(map.hasWon());
	}

	@Test
	public void sendLaserWin() {
		assertFalse(winWithoutHexagon(new Pos(0, 0)));
		assertFalse(winWithoutHexagon(new Pos(1, 0)));
		assertTrue(winWithoutHexagon(new Pos(2, 0)));
		assertFalse(winWithoutHexagon(new Pos(0, 1)));
		assertFalse(winWithoutHexagon(new Pos(1, 1)));
		assertTrue(winWithoutHexagon(new Pos(2, 1)));
		assertFalse(winWithoutHexagon(new Pos(0, 2)));
		assertFalse(winWithoutHexagon(new Pos(1, 2)));
		assertTrue(winWithoutHexagon(new Pos(2, 2)));
	}

	@Test
	public void sendLaserLose() {
		map.setHexagon(new Empty(), new Pos(1, 1));
		map.getHexagon(new Pos(1, 2)).setModifier(new Amplifier());
		map.sendLaser();
		assertTrue(map.hasLost());
	}

	@Test
	public void testSegments() {
		map.sendLaser();
		EnumMap<Color, Boolean> defaultMap = new Laser(true, true).getColors();
		EnumMap<Color, Boolean> redMap = defaultMap.clone();
		redMap.put(Color.GREEN, false);
		redMap.put(Color.BLUE, false);
		EnumMap<Color, Boolean> emptyMap = redMap.clone();
		emptyMap.put(Color.RED, false);
		List<Segment> segments = map.getSegments();
		assertEquals(9, segments.size());
		assertEquals(new Segment(new Pos(0, 2), new Pos(1, 2), 1.0f, (1.0f - Laser.getReduction()) / (float) Math.sqrt(2), defaultMap, redMap), segments.get(0));
		assertEquals(new Segment(new Pos(1, 2), new Pos(1, 1), segments.get(0).getToIntensity(), 1.0f - Laser.getReduction(), redMap, redMap), segments.get(1));
		assertEquals(new Segment(new Pos(1, 1), new Pos(1, 0), 0.9f, 0.8f, redMap, redMap), segments.get(2));
		assertEquals(new Segment(new Pos(1, 0), new Pos(0, 0), 0.8f, 0.7f, redMap, redMap), segments.get(3));
		assertEquals(new Segment(new Pos(0, 0), new Pos(0, 1), 0.7f, 0.6f, redMap, redMap), segments.get(4));
		assertEquals(new Segment(new Pos(0, 1), new Pos(1, 1), 0.6f, 0.9f, redMap, redMap), segments.get(5));
		assertEquals(new Segment(new Pos(1, 1), new Pos(2, 0), 0.9f, 0.8f, redMap, redMap), segments.get(6));
		assertEquals(new Segment(new Pos(1, 2), new Pos(2, 2), segments.get(0).getToIntensity(), segments.get(0).getToIntensity() - Laser.getReduction(), redMap, redMap), segments.get(7));
		assertEquals(new Segment(new Pos(2, 2), new Pos(3, 3), segments.get(7).getToIntensity(), 0.0f, redMap, emptyMap), segments.get(8));
	}
}
