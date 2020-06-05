package be.ac.umons.model;


import be.ac.umons.HexaMaze;
import be.ac.umons.game.Extension;
import be.ac.umons.model.hexagon.Hexagon;
import be.ac.umons.model.hexagon.Modifier;
import be.ac.umons.model.levelChecker.LevelChecker;
import be.ac.umons.util.Pos;

import java.util.List;

/**
 * Map used by an Editor
 */
public class EditorMap extends Map {

	/**
	 * Constructor
	 * @param width					Width of the map
	 * @param height				Height of the map
	 */
	public EditorMap(int width, int height) {
		super(width, height);
	}

	/**
	 * Returns true if the position is within the bounds of the map
	 * @param pos					Position
	 * @param width					Width of the map
	 * @param height				Height of the map
	 * @return						True if the position is within the bounds of the map
	 */
	public boolean inBounds(Pos pos, int width, int height) {
		return (pos.getX() < width) && (pos.getY() < height);
	}

	@Override
	public boolean canMoveTo(Pos from, Pos to) {
		boolean notNull = inBounds(from, getWidth(), getHeight()) && inBounds(to, getWidth(), getHeight());
		if(notNull){
			Hexagon src = getOnTop(from);
			Hexagon target = getOnTop(to);
			boolean isOK;
			if(src instanceof Modifier){
				isOK = target==null ||target.isEmpty() || target.canHaveModifier();
			}
			else{
				isOK = respectConstraint(src, to);
			}
			return isOK;
		}
		else
			return false;
	}

	@Override
	public boolean canInsertTo(Hexagon hex, Pos to) {
		boolean notNull = inBounds(to, getWidth(), getHeight());
		if(notNull) {
			Hexagon target = getHexagon(to);
			boolean isOK;
			if(hex instanceof Modifier){
				isOK = target==null || target.isEmpty() || target.canHaveModifier();
			}
			else{
				isOK = respectConstraint(hex,to);
			}
			return isOK;
		}
		else
			return false;
	}

	@Override
	public boolean canInsertTo(Container container, Pos to, int index) {
		boolean notNull = inBounds(to, getWidth(), getHeight()) && index < container.size();
		if(notNull) {
			Hexagon src = container.getHexagon(index);
			Hexagon target = getHexagon(to);
			boolean isOK;
			if(src instanceof Modifier){
				isOK = target==null || target.canHaveModifier() || target.isEmpty();
			}
			else{
				isOK = respectConstraint(src, to);
			}
			return isOK;
		}
		else
			return false;
	}

	@Override
	public void fillHole(Pos pos) {
		if (getConstraint(pos) != null)
			super.fillHole(pos);
		else
			setHexagon(null, pos);
	}

	/**
	 * Updates the size of the map
	 * @param _width				New width
	 * @param _height				New height
	 */
	public void updateSize(int _width, int _height) {
		Hexagon[][] newList = new Hexagon[_height][_width];
		Hexagon[][] newContrList = new Hexagon[_height][_width];
		int minWidth = Math.min(_width, getWidth());
		int minHeight = Math.min(_height, getHeight());
		for (int i = 0; i < minHeight; i++) {
			for (int j = 0; j < minWidth; j++) {
				newList[i][j] = hexagons[i][j];
				newContrList[i][j] = constraints[i][j];
			}
		}
		hexagons = newList;
		constraints = newContrList;
		width = _width;
		height = _height;
	}

	/**
	 * Replaces the content of the map
	 * @param newHexs				New Hexagons
	 * @param _sources				New Sources
	 * @param _targets				New Targets
	 * @param _constraints          New constraints
	 */
	public void replaceMap(Hexagon[][] newHexs, List<Pos> _sources, List<Pos> _targets, Hexagon[][] _constraints) {
		hexagons = newHexs;
		sources = _sources;
		targets = _targets;
		constraints = _constraints;
	}

	/**
	 * Checks if the created level is valid
	 * @param container         The container of the level
	 * @return					True if the created level is valid
	 */
	public boolean checkLevel(Container container) {
		for (int i = 0; i < getHeight(); i++) {
			for (int j = 0; j < getWidth(); j++) {
				Hexagon hex = getHexagon(new Pos(j, i));
				if (hex != null && !hex.isEmpty() && !respectConstraint(hex, new Pos(j, i)))
					return false;

			}
		}
		if (getSourcesPos().size() == 0 || getTargetsPos().size() == 0)
			return false;
		if (HexaMaze.settings.isExtensionActive(Extension.LEVEL_CHECKER)) {
			LevelChecker checker = new LevelChecker(this, container);
			return checker.getSolution() != null;
		}
		return true;
	}
}
