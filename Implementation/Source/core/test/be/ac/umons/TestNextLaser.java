package be.ac.umons;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.EnumMap;

import be.ac.umons.model.Laser;
import be.ac.umons.model.hexagon.Amplifier;
import be.ac.umons.model.hexagon.Converter;
import be.ac.umons.model.hexagon.Empty;
import be.ac.umons.model.hexagon.Filter;
import be.ac.umons.model.hexagon.GenericBridge;
import be.ac.umons.model.hexagon.Hexagon;
import be.ac.umons.model.hexagon.OneWayMirror;
import be.ac.umons.model.hexagon.Reducer;
import be.ac.umons.model.hexagon.SpecificBridge;
import be.ac.umons.model.hexagon.Splitter;
import be.ac.umons.model.hexagon.ThreeWayMirror;
import be.ac.umons.model.hexagon.TwoWayMirror;
import be.ac.umons.model.hexagon.Wall;
import be.ac.umons.util.Color;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(GdxTestRunner.class)
public class TestNextLaser {

	private static Laser redLaser;
	private static Laser redGreenLaser;
	private static Laser whiteLaser;
	private static EnumMap<Color, Boolean> emptyMap;

	@BeforeClass
	public static void init() {
		redLaser = new Laser(new Color[]{Color.RED}, 0.5f, true, true);
		redGreenLaser = new Laser(new Color[]{Color.RED, Color.GREEN}, 0.5f, true, true);
		whiteLaser = new Laser(new Color[]{Color.RED, Color.GREEN, Color.BLUE}, 0.5f, true, true);
		emptyMap = new EnumMap<>(Color.class);
		for (Color color : Color.values()) {
			emptyMap.put(color, false);
		}
	}

	public void assertSameColor(Hexagon hex) {
		for (Laser laser : new Laser[]{redLaser, redGreenLaser, whiteLaser}) {
			assertEquals(laser.getColors(), hex.getNextLaser(null, laser).getColors());
		}
	}

	public void assertReducedIntensity(Hexagon hex) {
		for (Laser laser : new Laser[]{redLaser, redGreenLaser, whiteLaser}) {
			assertTrue(hex.getNextLaser(null, laser).getIntensity() < laser.getIntensity());
		}
	}

	public void assertIncreasedIntensity(Hexagon hex) {
		for (Laser laser : new Laser[]{redLaser, redGreenLaser, whiteLaser}) {
			assertTrue(hex.getNextLaser(null, laser).getIntensity() > laser.getIntensity());
		}
	}

	@Test
	public void testDefaultBehaviour() {
		Hexagon[] hexs = new Hexagon[]{new Empty(), new GenericBridge(new boolean[]{true, true, false}),
				new SpecificBridge(new Color[]{Color.RED, Color.GREEN, null}), new OneWayMirror(), new TwoWayMirror(),
				new ThreeWayMirror(), new Wall()};
		for (Hexagon hex : hexs) {
			assertReducedIntensity(hex);
			assertSameColor(hex);
		}
	}

	@Test
	public void testFilter() {
		Filter filter = new Filter(new Color[]{Color.RED, Color.BLUE});
		EnumMap<Color, Boolean> redMap = emptyMap.clone();
		redMap.put(Color.RED, true);
		assertEquals(redMap, filter.getNextLaser(null, redLaser).getColors());
		assertEquals(redMap, filter.getNextLaser(null, redGreenLaser).getColors());
		EnumMap<Color, Boolean> redBlueMap = redMap.clone();
		redBlueMap.put(Color.BLUE, true);
		assertEquals(redBlueMap, filter.getNextLaser(null, whiteLaser).getColors());

		assertReducedIntensity(filter);
	}

	@Test
	public void testConverter() {
		Converter converter = new Converter(Color.GREEN);
		EnumMap<Color, Boolean> greenMap = emptyMap.clone();
		greenMap.put(Color.GREEN, true);
		assertEquals(greenMap, converter.getNextLaser(null, redLaser).getColors());
		assertEquals(greenMap, converter.getNextLaser(null, redGreenLaser).getColors());
		assertEquals(greenMap, converter.getNextLaser(null, whiteLaser).getColors());

		assertReducedIntensity(converter);
	}

	@Test
	public void testSplitter() {
		float prevIntensity = 0.0f;
		for (int way = 5; way >= 2; way--) {
			Splitter splitter = new Splitter(way);
			float intensity = splitter.getNextLaser(null, redLaser).getIntensity();
			assertTrue(prevIntensity < intensity);
			assertSameColor(splitter);
			prevIntensity = intensity;
		}
	}

	@Test
	public void testAmplifier() {
		Amplifier amplifier = new Amplifier();
		assertIncreasedIntensity(amplifier);
		assertSameColor(amplifier);
		// Assert that the amplifier amplifies more than the reduction of a normal hexagon
		assertTrue(amplifier.getNextLaser(null, redLaser).getIntensity() + new Empty().getNextLaser(null, redLaser).getIntensity() > 0);
	}

	@Test
	public void testReducer() {
		Reducer reducer = new Reducer();
		assertReducedIntensity(reducer);
		assertSameColor(reducer);
		// Assert that the reducer reduces more than a normal hexagon
		assertTrue(reducer.getNextLaser(null, redLaser).getIntensity() < new Empty().getNextLaser(null, redLaser).getIntensity());
	}
}
