package be.ac.umons;

import org.junit.Test;
import org.junit.runner.RunWith;

import be.ac.umons.model.Laser;
import be.ac.umons.model.hexagon.Converter;
import be.ac.umons.model.hexagon.Empty;
import be.ac.umons.model.hexagon.Filter;
import be.ac.umons.model.hexagon.Gate;
import be.ac.umons.model.hexagon.GenericBridge;
import be.ac.umons.model.hexagon.OneWayMirror;
import be.ac.umons.model.hexagon.Reducer;
import be.ac.umons.model.hexagon.Source;
import be.ac.umons.model.hexagon.SpecificBridge;
import be.ac.umons.model.hexagon.Splitter;
import be.ac.umons.model.hexagon.Target;
import be.ac.umons.model.hexagon.ThreeWayMirror;
import be.ac.umons.model.hexagon.TwoWayMirror;
import be.ac.umons.model.hexagon.Wall;
import be.ac.umons.util.Color;
import be.ac.umons.util.Edge;

import static be.ac.umons.TestUtil.assertSetEquals;

@RunWith(GdxTestRunner.class)
public class TestNextEdges {

	@Test
	public void testEmpty() {
		Empty hex = new Empty();
		assertSetEquals(hex.getNextEdges(Edge.NORTH, null), Edge.SOUTH);
		assertSetEquals(hex.getNextEdges(Edge.NORTHEAST, null), Edge.SOUTHWEST);
	}

	@Test
	public void testWall() {
		Wall hex = new Wall();
		assertSetEquals(hex.getNextEdges(Edge.NORTH, null));
		assertSetEquals(hex.getNextEdges(Edge.NORTHEAST, null));
	}

	@Test
	public void testOneWayMirror() {
		// The orientation of the OneWayMirror means that the mirror is parallel to that orientation
		// The side of the mirror that reflects the laser is the side on the part of the orientation  
		OneWayMirror hex = new OneWayMirror();
		assertSetEquals(hex.getNextEdges(Edge.NORTH, null), Edge.NORTH);
		assertSetEquals(hex.getNextEdges(Edge.NORTHEAST, null), Edge.NORTHWEST);
		assertSetEquals(hex.getNextEdges(Edge.SOUTHEAST, null));
	}

	@Test
	public void testTwoWayMirror() {
		// The orientation of the TwoWayMirror means that the mirror is parallel to that orientation
		// The two sides of the flat mirror reflect the laser
		TwoWayMirror hex = new TwoWayMirror();
		assertSetEquals(hex.getNextEdges(Edge.NORTH, null), Edge.NORTH);
		assertSetEquals(hex.getNextEdges(Edge.NORTHEAST, null), Edge.NORTHWEST);
		assertSetEquals(hex.getNextEdges(Edge.SOUTHEAST, null), Edge.SOUTHWEST);
	}

	@Test
	public void testThreeWayMirror() {
		// The orientation of the ThreeWayMirror means that the triangular mirror is pointing
		// towards the next corner of the hexagon after the edge in that orientation clockwise
		ThreeWayMirror hex = new ThreeWayMirror();
		assertSetEquals(hex.getNextEdges(Edge.NORTH, null), Edge.NORTHWEST);
		assertSetEquals(hex.getNextEdges(Edge.NORTHEAST, null), Edge.SOUTHEAST);
		assertSetEquals(hex.getNextEdges(Edge.SOUTHEAST, null), Edge.NORTHEAST);
	}


	@Test
	public void testSource() {
		Source hex = new Source(new Laser(true, true));
		assertSetEquals(hex.getNextEdges(Edge.NORTH, null));
		assertSetEquals(hex.getNextEdges(Edge.NORTHEAST, null));
	}

	@Test
	public void testTarget() {
		Target hex = new Target();
		assertSetEquals(hex.getNextEdges(Edge.NORTH, null));
		assertSetEquals(hex.getNextEdges(Edge.NORTHEAST, null));
	}

	@Test
	public void testSplitter() {
		Splitter splitter2 = new Splitter(2);
		assertSetEquals(splitter2.getNextEdges(Edge.NORTHEAST, null));
		assertSetEquals(splitter2.getNextEdges(Edge.NORTH, null), Edge.SOUTHWEST, Edge.SOUTHEAST);

		Splitter splitter3 = new Splitter(3);
		assertSetEquals(splitter3.getNextEdges(Edge.NORTH, null), Edge.SOUTH, Edge.SOUTHWEST, Edge.SOUTHEAST);

		Splitter splitter4 = new Splitter(4);
		assertSetEquals(splitter4.getNextEdges(Edge.NORTH, null), Edge.NORTHWEST, Edge.NORTHEAST, Edge.SOUTHWEST, Edge.SOUTHEAST);

		Splitter splitter5 = new Splitter(5);
		assertSetEquals(splitter5.getNextEdges(Edge.NORTH, null), Edge.SOUTH, Edge.NORTHWEST, Edge.NORTHEAST, Edge.SOUTHWEST, Edge.SOUTHEAST);
	}

	@Test
	public void testGate() {
		Gate hex = new Gate();
		assertSetEquals(hex.getNextEdges(Edge.NORTH, null), Edge.SOUTH);
	}

	@Test
	public void testGenericBridge() {
		GenericBridge hex = new GenericBridge(new boolean[]{true, true, false});
		assertSetEquals(hex.getNextEdges(Edge.NORTH, null), Edge.SOUTH);
		assertSetEquals(hex.getNextEdges(Edge.NORTHEAST, null), Edge.SOUTHWEST);
		assertSetEquals(hex.getNextEdges(Edge.SOUTHEAST, null));
	}

	@Test
	public void testSpecificBridge() {
		SpecificBridge hex = new SpecificBridge(new Color[]{Color.RED, Color.GREEN, null});
		assertSetEquals(hex.getNextEdges(Edge.NORTH, new Laser(new Color[]{Color.RED}, 1.0f, true, true)), Edge.SOUTH);
		assertSetEquals(hex.getNextEdges(Edge.NORTH, new Laser(new Color[]{Color.RED, Color.GREEN}, 1.0f, true, true)));
		assertSetEquals(hex.getNextEdges(Edge.NORTH, new Laser(new Color[]{}, 1.0f, true, true)));
		assertSetEquals(hex.getNextEdges(Edge.NORTHEAST, new Laser(new Color[]{Color.GREEN}, 1.0f, true, true)), Edge.SOUTHWEST);
		assertSetEquals(hex.getNextEdges(Edge.SOUTHEAST, new Laser(new Color[]{Color.RED}, 1.0f, true, true)));
	}

	@Test
	public void testConverter() {
		Converter hex = new Converter(Color.RED);
		assertSetEquals(hex.getNextEdges(Edge.NORTH, null), Edge.SOUTH);
		assertSetEquals(hex.getNextEdges(Edge.NORTHEAST, null));
	}

	@Test
	public void testFilter() {
		Filter hex = new Filter(new Color[]{Color.RED});
		assertSetEquals(hex.getNextEdges(Edge.NORTH, null), Edge.SOUTH);
		assertSetEquals(hex.getNextEdges(Edge.NORTHEAST, null));
	}

	@Test
	public void testReducer() {
		Reducer hex = new Reducer();
		assertSetEquals(hex.getNextEdges(Edge.NORTH, null), Edge.SOUTH);
		assertSetEquals(hex.getNextEdges(Edge.NORTHEAST, null));
	}

	@Test
	public void testRotation() {
		// Test if getNextEdges outputs the correct nextEdges for a certain orientation
		TwoWayMirror hex = new TwoWayMirror();
		assertSetEquals(hex.getNextEdges(Edge.NORTH, null), Edge.NORTH);
		hex.rotate(Edge.NORTHEAST);
		assertSetEquals(hex.getNextEdges(Edge.NORTH, null), Edge.SOUTHEAST);
		hex.rotate(Edge.SOUTHEAST);
		assertSetEquals(hex.getNextEdges(Edge.NORTH, null), Edge.SOUTHWEST);
		hex.rotate(Edge.SOUTH);
		assertSetEquals(hex.getNextEdges(Edge.NORTH, null), Edge.NORTH);
		hex.rotate(Edge.SOUTHWEST);
		assertSetEquals(hex.getNextEdges(Edge.NORTH, null), Edge.SOUTHEAST);
		hex.rotate(Edge.NORTHWEST);
		assertSetEquals(hex.getNextEdges(Edge.NORTH, null), Edge.SOUTHWEST);
	}
}
