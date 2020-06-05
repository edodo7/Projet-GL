package be.ac.umons;

import org.junit.Test;
import org.junit.runner.RunWith;

import be.ac.umons.model.Laser;
import be.ac.umons.model.hexagon.Amplifier;
import be.ac.umons.model.hexagon.Empty;
import be.ac.umons.model.hexagon.Filter;
import be.ac.umons.model.hexagon.OneWayMirror;
import be.ac.umons.model.hexagon.Reducer;
import be.ac.umons.model.hexagon.Source;
import be.ac.umons.util.Color;

import static org.junit.Assert.assertEquals;

@RunWith(GdxTestRunner.class)
public class TestModifiers {

	/**
	 * Test that the getNextLaser method is executed on the modifier
	 */
	@Test
	public void testNextLaserModifier() {
		// Without modifier
		OneWayMirror mirror = new OneWayMirror();
		Laser laser = new Laser(true, true);
		Laser reducedLaser = laser.clone();
		reducedLaser.reduceIntensity();
		Laser modifiedLaser = mirror.getNextLaserModifier(laser);
		assertEquals(reducedLaser, mirror.getNextLaser(null, modifiedLaser));

		// Check if the modifier is traversed
		mirror.setModifier(new Filter(new Color[]{Color.RED}));
		Laser reducedRedLaser = reducedLaser.clone();
		reducedRedLaser.reduceIntensity();
		reducedRedLaser.setColor(Color.GREEN, false);
		reducedRedLaser.setColor(Color.BLUE, false);
		modifiedLaser = mirror.getNextLaserModifier(laser);
		assertEquals(reducedRedLaser, mirror.getNextLaser(null, modifiedLaser));

		// Check when the modifier is removed
		mirror.setModifier(null);
		modifiedLaser = mirror.getNextLaserModifier(laser);
		assertEquals(reducedLaser, mirror.getNextLaser(null, modifiedLaser));
	}

	/**
	 * Test that an empty hexagon can't have a modifier
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testEmpty() {
		new Empty().setModifier(new Amplifier());
	}

	/**
	 * Test that a source can't have a modifier
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSource() {
		new Source(new Laser(true, true)).setModifier(new Amplifier());
	}

	/**
	 * Test that an hexagon with a modifier can't be set as modifier
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testStackModifier() {
		Amplifier modifier = new Amplifier();
		modifier.setModifier(new Reducer());
		new OneWayMirror().setModifier(modifier);
	}
}
