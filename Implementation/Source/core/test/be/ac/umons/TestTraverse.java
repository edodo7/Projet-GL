package be.ac.umons;

import org.junit.Test;
import org.junit.runner.RunWith;

import be.ac.umons.model.Laser;
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
import be.ac.umons.util.Color;
import be.ac.umons.util.Edge;

import static org.junit.Assert.assertArrayEquals;

@RunWith(GdxTestRunner.class)
public class TestTraverse {

	private static final boolean[] emptyEdges = new boolean[Edge.size()];
	private static final Laser laser = new Laser(new Color[]{Color.RED}, 1.0f, true, true);

	private static boolean[] getEdgesThatContainLaser(Hexagon hex) {
		boolean[] edges = new boolean[Edge.size()];
		for (Edge edge : Edge.values()) {
			edges[edge.toInt()] = hex.containsLaser(edge, laser);
		}
		return edges;
	}

	/**
	 * Test whether the laser will cross another laser for each possible side after traversing it by the north
	 *
	 * @param hex   the hexagon this is tested
	 * @param edges whether the edge with index i will contain the laser
	 */
	private static void testContainsLaser(Hexagon hex, boolean... edges) {
		// No laser must traverse the hexagon before traversing it
		assertArrayEquals(emptyEdges, getEdgesThatContainLaser(hex));
		hex.traverse(Edge.NORTH, laser);

		assertArrayEquals(edges, getEdgesThatContainLaser(hex));

		hex.traverseBack(Edge.NORTH, laser);
		// No laser must traverse the hexagon after traversing it back
		assertArrayEquals(emptyEdges, getEdgesThatContainLaser(hex));
	}

	@Test
	public void testEmpty() {
		testContainsLaser(new Empty(), true, true, true, true, true, true);
	}

	@Test
	public void testWall() {
		testContainsLaser(new Wall(), true, false, false, false, false, false);
	}

	@Test
	public void testOneWayMirror() {
		testContainsLaser(new OneWayMirror(), true, true, false, false, false, true);
	}

	@Test
	public void testTwoWayMirror() {
		testContainsLaser(new TwoWayMirror(), true, true, false, false, false, true);
	}

	@Test
	public void testThreeWayMirror() {
		testContainsLaser(new ThreeWayMirror(), true, false, false, false, false, true);
	}


	@Test
	public void testSource() {
		testContainsLaser(new Source(new Laser(true, true)), true, false, false, false, false, false);
	}

	@Test
	public void testTarget() {
		testContainsLaser(new Target(), true, false, false, false, false, false);
	}

	@Test
	public void testSplitter() {
		testContainsLaser(new Splitter(2), true, false, true, false, true, false);
	}

	@Test
	public void testGate() {
		testContainsLaser(new Gate(), true, false, false, true, false, false);
	}

	@Test
	public void testGenericBridge() {
		testContainsLaser(new GenericBridge(new boolean[]{true, true, false}), true, false, false, true, false, false);
	}

	@Test
	public void testSpecificBridge() {
		testContainsLaser(new SpecificBridge(new Color[]{Color.RED, Color.GREEN, null}), true, false, false, true, false, false);
	}

	@Test
	public void testConverter() {
		testContainsLaser(new Converter(Color.RED), true, false, false, true, false, false);
	}

	@Test
	public void testFilter() {
		testContainsLaser(new Filter(new Color[]{Color.RED}), true, false, false, true, false, false);
	}

	@Test
	public void testReducer() {
		testContainsLaser(new Reducer(), true, false, false, true, false, false);
	}

	@Test
	public void testRotation() {
		TwoWayMirror hex = new TwoWayMirror();

		// Test the NorthEast orientation
		hex.rotate(Edge.NORTHEAST);
		hex.traverse(Edge.NORTH, null);
		assertArrayEquals(new boolean[]{true, true, true, false, false, false}, getEdgesThatContainLaser(hex));
		hex.traverseBack(Edge.NORTH, null);

		// Test the SouthEast orientation
		hex.rotate(Edge.SOUTHEAST);
		hex.traverse(Edge.NORTH, null);
		assertArrayEquals(new boolean[]{true, false, false, false, true, true}, getEdgesThatContainLaser(hex));
		hex.traverseBack(Edge.NORTH, null);
	}
}
