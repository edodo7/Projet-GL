package be.ac.umons.model.xml;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.XmlWriter;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.EnumMap;

import be.ac.umons.game.Difficulty;
import be.ac.umons.game.Extension;
import be.ac.umons.model.Container;
import be.ac.umons.model.EditorMap;
import be.ac.umons.model.hexagon.Converter;
import be.ac.umons.model.hexagon.Filter;
import be.ac.umons.model.hexagon.GenericBridge;
import be.ac.umons.model.hexagon.Hexagon;
import be.ac.umons.model.hexagon.Source;
import be.ac.umons.model.hexagon.SpecificBridge;
import be.ac.umons.model.hexagon.Splitter;
import be.ac.umons.util.Color;
import be.ac.umons.util.Pos;


/**
 * Class that handles the creation/save of files used by the editor
 */
public class EditorFileHandler {

	/**
	 * Creates a tmx file
	 * @param width					Width of the map
	 * @param height				Height of the map
	 * @param filename				Filename
	 * @param content				Content of the map
	 */
	public static void createTmx(int width, int height, String filename, String content) {
		FileHandle file = Gdx.files.local("levels" + File.separator + filename);
		Writer writer = file.writer(false);
		XmlWriter xml = new XmlWriter(writer);

		try {
			xml.element("map")
					.attribute("version", "1.0")
					.attribute("tiledversion", "1.0.3")
					.attribute("orientation", "hexagonal")
					.attribute("renderorder", "right-down")
					.attribute("width", width)
					.attribute("height", height)
					.attribute("tilewidth", "148")
					.attribute("tileheight", "128")
					.attribute("hexsidelength", "74")
					.attribute("staggeraxis", "x")
					.attribute("staggerindex", "odd")
					.attribute("nextobjectid", "1")
					.element("tileset")
					.attribute("firstgid", "1")
					.attribute("source", "TileSet.tsx")
					.pop()
					.element("layer")
					.attribute("name", "Layer1")
					.attribute("width", width)
					.attribute("height", height)
					.element("data")
					.attribute("encoding", "csv")
					.text(content)
					.pop()
					.pop()
					.pop();

			writer.close();


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a default tmx file
	 * @param width					Width of the map
	 * @param height				Height of the map
	 * @param filename				Filename
	 */
	public static void createDefault(int width, int height, String filename) {
		StringBuilder tmxContent = new StringBuilder();
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				tmxContent.append("1");
				if (j != width - 1 || i != height - 1)
					tmxContent.append(",");
			}
			tmxContent.append(System.lineSeparator());
		}
		createTmx(width, height, filename, tmxContent.toString());
	}

	/**
	 * Saves a tmx file
	 * @param map					Map to be saved
	 * @param filename				Filename
	 */
	public static void saveTMX(EditorMap map, String filename) {
		StringBuilder tmxContent = new StringBuilder();
		for (int i = map.getHeight() - 1; i > -1; i--) {
			for (int j = 0; j < map.getWidth(); j++) {
				if (map.getHexagon(new Pos(j, i)) != null)
					tmxContent.append("2");
				else
					tmxContent.append("0");
				if (j != map.getWidth() - 1 || i != 0) {
					tmxContent.append(",");
				}
			}

			tmxContent.append(System.lineSeparator());
		}
		createTmx(map.getWidth(), map.getHeight(), filename, tmxContent.toString());
	}

	/**
	 * Saves a xml file
	 * @param map					Map to be saved
	 * @param cont					Container to be saved
	 * @param levelName				Level name
	 * @param filename				Filename
	 * @param difficulty			Difficulty
	 * @param timer					Limit time
	 */
	public static void saveXML(EditorMap map, Container cont, String levelName, String filename, Difficulty difficulty, int timer) {
		FileHandle file = Gdx.files.local("levels" + File.separator + filename);
		Writer writer = file.writer(false);
		ArrayList<Pos> constrPos = new ArrayList<>();
		EnumMap<Extension, Boolean> extensions = EditorFileHandler.getExtensionsNeeded(map, cont);
		XmlWriter xml = new XmlWriter(writer);
		try {
			xml.element("level")
					.attribute("name", levelName)
					.attribute("map", filename.replace(".xml", ".tmx"))
					.attribute("difficulty", difficulty.toString().toLowerCase())
					.attribute("timer", timer)
					.attribute("color", extensions.get(Extension.COLOR))
					.attribute("intensity", extensions.get(Extension.INTENSITY));


			//GRID
			xml.element("grid")
					.attribute("width", map.getWidth())
					.attribute("height", map.getHeight())
					.attribute("fill", "false")
					.pop();

			//BLOCKS
			xml.element("blocks");
			for (int i = 0; i < map.getHeight(); i++) {
				for (int j = 0; j < map.getWidth(); j++) {
					Pos pos = new Pos(j, i);
					Hexagon hex = map.getHexagon(pos);
					if (hex != null) {
						xml.element("position")
								.attribute("x", j)
								.attribute("y", i);
						EditorFileHandler.addXMLElement(xml, hex);
						if (hex.getModifier() != null)
							addXMLElement(xml, hex.getModifier());

						xml.pop();
						if (map.getConstraint(pos) != null) {
							constrPos.add(pos);
						}
					}
				}
			}
			xml.pop();

			//CONSTRAINTS
			xml.element("constraints");
			for (int i = 0; i < constrPos.size(); i++) {
				Pos pos = constrPos.get(i);
				Hexagon constraint = map.getConstraint(pos);
				if (constraint != null) {
					xml.element("position").attribute("x", pos.getX()).attribute("y", pos.getY());
					addXMLElement(xml, map.getConstraint(pos));
					xml.pop();
				}

			}
			xml.pop();

			//CONTAINER
			xml.element("container");
			for (int i = 0; i < cont.getHexagons().size(); i++)
				addXMLElement(xml, cont.getHexagon(i));
			xml.pop();

			xml.pop();
			writer.close();


		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Adds an xml element corresponding to an hexagon
	 * @param xml					XmlWriter
	 * @param hex					Hexagon to be added
	 * @throws IOException          an IOException can occur
	 */
	private static void addXMLElement(XmlWriter xml, Hexagon hex) throws IOException {
		switch (hex.getClass().getSimpleName()) {
			case "Empty":
				xml.element("empty").pop();
				break;
			case "Source":
				xml.element("source")
						.attribute("fixedposition", !hex.isMovable())
						.attribute("fixedorientation", !hex.isRotatable())
						.attribute("orientation", hex.getRotation().toSting())
						.attribute("red",((Source)hex).getLaser().getColors().get(Color.RED))
						.attribute("green",((Source)hex).getLaser().getColors().get(Color.GREEN))
						.attribute("blue",((Source)hex).getLaser().getColors().get(Color.BLUE))
						.pop();
				break;
			case "Target":
				xml.element("target")
						.attribute("fixedposition", !hex.isMovable())
						.attribute("fixedorientation", !hex.isRotatable())
						.attribute("orientation", hex.getRotation().toSting())
						.pop();
				break;
			case "Wall":
				xml.element("wall")
						.attribute("fixedposition", !hex.isMovable())
						.attribute("fixedorientation", !hex.isRotatable())
						.pop();
				break;
			case "OneWayMirror":
				xml.element("mirror")
						.attribute("kind", "1-way")
						.attribute("fixedposition", !hex.isMovable())
						.attribute("fixedorientation", !hex.isRotatable())
						.attribute("orientation", hex.getRotation().toSting())
						.pop();
				break;
			case "TwoWayMirror":
				xml.element("mirror")
						.attribute("kind", "2-way")
						.attribute("fixedposition", !hex.isMovable())
						.attribute("fixedorientation", !hex.isRotatable())
						.attribute("orientation", hex.getRotation().toSting())
						.pop();
				break;
			case "ThreeWayMirror":
				xml.element("mirror")
						.attribute("kind", "3-way")
						.attribute("fixedposition", !hex.isMovable())
						.attribute("fixedorientation", !hex.isRotatable())
						.attribute("orientation", hex.getRotation().toSting())
						.pop();
				break;
			case "Splitter":
				xml.element("splitter")
						.attribute("way", String.valueOf(((Splitter) hex).getWay()))
						.attribute("fixedposition", !hex.isMovable())
						.attribute("fixedorientation", !hex.isRotatable())
						.attribute("orientation", hex.getRotation().toSting())
						.pop();
				break;
			case "Filter":
				xml.element("filter")
						.attribute("red", ((Filter) hex).hasColor(Color.RED))
						.attribute("blue", ((Filter) hex).hasColor(Color.BLUE))
						.attribute("green", ((Filter) hex).hasColor(Color.GREEN))
						.attribute("fixedposition", !hex.isMovable())
						.attribute("fixedorientation", !hex.isRotatable())
						.attribute("orientation", hex.getRotation().toSting())
						.pop();
				break;
			case "Converter":
				xml.element("converter")
						.attribute("color", ((Converter) hex).getConvertColor().toString())
						.attribute("fixedposition", !hex.isMovable())
						.attribute("fixedorientation", !hex.isRotatable())
						.attribute("orientation", hex.getRotation().toSting())
						.pop();
				break;
			case "Gate":
				xml.element("gate")
						.attribute("fixedposition", !hex.isMovable())
						.attribute("fixedorientation", !hex.isRotatable())
						.attribute("orientation", hex.getRotation().toSting())
						.pop();
				break;
			case "GenericBridge":
				xml.element("genericbridge")
						.attribute("fixedposition", !hex.isMovable())
						.attribute("fixedorientation", !hex.isRotatable())
						.attribute("orientation", hex.getRotation().toSting())
						.attribute("northgate", ((GenericBridge) hex).getCanPass()[0])
						.attribute("northeastgate", ((GenericBridge) hex).getCanPass()[1])
						.attribute("southeastgate", ((GenericBridge) hex).getCanPass()[2])
						.pop();
				break;
			case "SpecificBridge":
				xml.element("specificbridge")
						.attribute("fixedposition", !hex.isMovable())
						.attribute("fixedorientation", !hex.isRotatable())
						.attribute("orientation", hex.getRotation().toSting())
						.attribute("northgate", ((SpecificBridge) hex).getCrossColors()[0].toString())
						.attribute("northeastgate", ((SpecificBridge) hex).getCrossColors()[1].toString())
						.attribute("southeastgate", ((SpecificBridge) hex).getCrossColors()[2].toString())
						.pop();
				break;
			case "Amplifier":
				xml.element("amplifier")
						.attribute("fixedposition", !hex.isMovable())
						.attribute("fixedorientation", !hex.isRotatable())
						.attribute("orientation", hex.getRotation().toSting())
						.pop();
				break;
			case "Reducer":
				xml.element("reducer")
						.attribute("fixedposition", !hex.isMovable())
						.attribute("fixedorientation", !hex.isRotatable())
						.attribute("orientation", hex.getRotation().toSting())
						.pop();
				break;
		}

	}

	/**
	 * Gets the need extensions by a level
	 * @param map					The level's Map
	 * @param cont					The level's Container
	 * @return						An HashMap with a boolean value for each extension
	 */
	private static EnumMap<Extension, Boolean> getExtensionsNeeded(EditorMap map, Container cont) {
		EnumMap<Extension, Boolean> res = new EnumMap<>(Extension.class);
		for (Extension extension : Extension.values()) {
			res.put(extension, false);
		}
		for (int y = 0; y < map.getHeight(); y++) {
			for (int x = 0; x < map.getWidth(); x++) {
				Hexagon hex = map.getHexagon(new Pos(x, y));
				if (hex != null && hex.getExtension() != null) {
					res.put(hex.getExtension(), true);
				}
			}
		}
		for (int i = 0; i < cont.size(); i++) {
			Hexagon hex = cont.getHexagon(i);
			if (hex.getExtension() != null) {
				res.put(hex.getExtension(), true);
			}
		}
		return res;
	}
}
