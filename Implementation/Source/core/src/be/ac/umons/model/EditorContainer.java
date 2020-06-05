package be.ac.umons.model;

import be.ac.umons.HexaMaze;
import be.ac.umons.game.Extension;
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
import be.ac.umons.util.Color;

/**
 * Class containing all the hexagons that can be used to create levels in the editor
 */
public class EditorContainer extends Container {

	/**
	 * Constructor
	 */
	public EditorContainer() {
		super();
		fillContainer();
	}

	/**
	 * Fills the container with all kinds of hexagons
	 */
	public void fillContainer() {
		hexagons.add(new Empty());
		hexagons.add(new Source(new Laser(false, true))); // TODO set activated extensions
		hexagons.add(new Target());
		hexagons.add(new Wall());
		hexagons.add(new Gate());
		hexagons.add(new OneWayMirror());
		hexagons.add(new TwoWayMirror());
		hexagons.add(new ThreeWayMirror());
		hexagons.add(new Splitter(2));
		hexagons.add(new Splitter(3));
		hexagons.add(new Splitter(4));
		hexagons.add(new Splitter(5));

		if (HexaMaze.settings.isExtensionActive(Extension.COLOR)) {
			hexagons.add(new Filter(new Color[]{Color.RED}));
			hexagons.add(new Converter(Color.RED));
			hexagons.add(new GenericBridge(new boolean[]{true, true, true}));
			hexagons.add(new SpecificBridge(new Color[]{Color.RED, Color.GREEN, Color.BLUE}));
		}

		if (HexaMaze.settings.isExtensionActive(Extension.COLOR)) {
			hexagons.add(new Amplifier());
			hexagons.add(new Reducer());
		}
	}

	@Override
	public Hexagon getHexagon(int index) {

		try {
			return hexagons.get(index).clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
		//return null;
	}

	@Override
	public void addHexagon(Hexagon hex) {
	}

	@Override
	public Hexagon removeHexagon(int index) {
		return getHexagon(index);
	}

}