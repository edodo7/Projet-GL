package be.ac.umons;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import be.ac.umons.model.Container;
import be.ac.umons.model.Laser;
import be.ac.umons.model.Map;
import be.ac.umons.model.hexagon.Amplifier;
import be.ac.umons.model.hexagon.Converter;
import be.ac.umons.model.hexagon.Empty;
import be.ac.umons.model.hexagon.Filter;
import be.ac.umons.model.hexagon.Gate;
import be.ac.umons.model.hexagon.GenericBridge;
import be.ac.umons.model.hexagon.Hexagon;
import be.ac.umons.model.hexagon.OneWayMirror;
import be.ac.umons.model.hexagon.Reducer;
import be.ac.umons.model.hexagon.Source;
import be.ac.umons.model.hexagon.SpecificBridge;
import be.ac.umons.model.hexagon.Splitter;
import be.ac.umons.model.hexagon.Target;
import be.ac.umons.model.hexagon.ThreeWayMirror;
import be.ac.umons.model.hexagon.TwoWayMirror;
import be.ac.umons.model.hexagon.Wall;
import be.ac.umons.model.xml.XMLGameLoader;
import be.ac.umons.util.Color;
import be.ac.umons.util.Edge;
import be.ac.umons.util.Pos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(GdxTestRunner.class)
public class TestClone {

	@Test
	public void testSource() throws CloneNotSupportedException {
		Source source = new Source(new Laser(new Color[]{Color.RED, Color.BLUE}, 0.8f, true, true),
				Edge.NORTHEAST, true, false, new Amplifier());
		Source copy = source.clone();
		assertEquals(source, copy);
	}

	@Test
	public void testAllHexagons() throws CloneNotSupportedException {
		Hexagon[] hexagons = new Hexagon[]{
				new Amplifier(), new Converter(Color.BLUE), new Empty(), new Filter(new Color[]{Color.RED, Color.GREEN}), new Gate(),
				new GenericBridge(new boolean[]{false, true, true}), new OneWayMirror(), new Reducer(), new Source(new Laser(true, true)),
				new SpecificBridge(new Color[]{Color.GREEN, Color.BLUE, null}), new Splitter(3), new Target(),
				new ThreeWayMirror(), new TwoWayMirror(), new Wall()
		};
		for (Hexagon hex : hexagons) {
			assertEquals(hex, hex.clone());
		}
	}

	@Test
	public void testMap() throws CloneNotSupportedException {
		Map map = new XMLGameLoader(new File("levels/test_level.xml")).getGameData().getMap();
		Map copy = map.clone();
		assertEquals(map, copy);
		map.setHexagon(new Splitter(3), new Pos(0, 0));
		assertNotEquals(map, copy);
	}

	@Test
	public void testContainer() throws CloneNotSupportedException {
		Container container = new Container();
		container.addHexagon(new TwoWayMirror());
		container.addHexagon(new Converter(Color.BLUE));
		Container copy = container.clone();
		assertEquals(container, copy);
		container.removeHexagon(1);
		assertNotEquals(container, copy);
	}
}
