package be.ac.umons.model.xml;

import be.ac.umons.HexaMaze;
import be.ac.umons.game.Difficulty;
import be.ac.umons.game.Extension;
import be.ac.umons.game.GameLevel;
import be.ac.umons.model.Container;
import be.ac.umons.model.Laser;
import be.ac.umons.model.Map;
import be.ac.umons.model.hexagon.*;
import be.ac.umons.util.Color;
import be.ac.umons.util.Edge;
import be.ac.umons.util.Pos;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Class that loads the level's xml file
 */
public class XMLGameLoader {

	private final File xmlFile;
	private final File xsdFile = new File("levels/level_schema.xsd");
	private GameData gameData;

	/**
	 * Check the level XML File and load it
	 * @param xmlFile	an XML file
	 * @throws IllegalArgumentException     if the xml file is not valid
	 */
	public XMLGameLoader(File xmlFile) throws IllegalArgumentException {
		this.xmlFile = xmlFile;
		if (!validateXSD())
			throw new IllegalArgumentException("Bad XML file : " + xmlFile.getAbsolutePath());
		gameData = loadGameData();
		if (!gameData.isValid())
			throw new IllegalArgumentException("Logical error in the XML file");
	}

	/**
	 * Checks if the xml file is valid using a xsd file
	 * @return							True if the XML is valid
	 */
	private boolean validateXSD() {
		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		try {
			Schema schema = factory.newSchema(xsdFile);
			Validator validator = schema.newValidator();
			validator.validate(new StreamSource(xmlFile));
		} catch (SAXException | IOException e) {
			System.out.println(e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Creates the level with the read data
	 * @param element					Level Element
	 * @return							A GameLevel
	 */
	private GameLevel loadLevel(Element element) {
		String name = element.getAttribute("name");
		String tmx = element.getAttribute("map");
		Difficulty difficulty = Difficulty.valueOf(element.getAttribute("difficulty").toUpperCase());
		GameLevel level = new GameLevel(tmx, xmlFile.getName(), name, difficulty);
		level.setTimer(Integer.valueOf(element.getAttribute("timer")));
		for (Extension extension : Extension.values()) {
			String ext_str = element.getAttribute(extension.toString().toLowerCase());
			if (ext_str.isEmpty())
				ext_str = "false";

			level.setExtension(extension, Boolean.valueOf(ext_str));
		}
		return level;
	}

	/**
	 * Creates an Hexagon with the read data
	 * @param element					Hexagon Element
	 * @param level                     The level
	 * @return							An Hexagon
	 */
	private Hexagon loadHexagon(Element element, GameLevel level) {
		Hexagon hex = null;
		switch (element.getTagName()) {
			case "empty":
				hex = new Empty();
				break;
			case "source": {
				ArrayList<Color> colors = new ArrayList<>();
				for (Color color : Color.values()) {
					String booleanValue = element.getAttribute(color.toString().toLowerCase());
					if (booleanValue.isEmpty() || Boolean.valueOf(booleanValue)) {
						colors.add(color);
					}
				}
				String floatValue = element.getAttribute("intensity");
				float intensity;
				if (floatValue.isEmpty()) {
					intensity = 1.0f;
				} else {
					intensity = Float.valueOf(floatValue);
				}
				Color[] colorsArray = new Color[colors.size()];
				hex = new Source(new Laser(colors.toArray(colorsArray), intensity,
						level.isExtensionActive(Extension.INTENSITY) && HexaMaze.settings.isExtensionActive(Extension.INTENSITY),
						level.isExtensionActive(Extension.COLOR) && HexaMaze.settings.isExtensionActive(Extension.COLOR)));
				break;
			}
			case "target":
				hex = new Target();
				break;
			case "wall":
				hex = new Wall();
				break;
			case "mirror":
				switch (element.getAttribute("kind")) {
					case "1-way":
						hex = new OneWayMirror();
						break;
					case "2-way":
						hex = new TwoWayMirror();
						break;
					case "3-way":
						hex = new ThreeWayMirror();
						break;
				}
				break;
			case "splitter":
				hex = new Splitter(Integer.valueOf(element.getAttribute("way")));
				break;
			case "filter": {
				ArrayList<Color> colors = new ArrayList<>();
				for (Color color : Color.values()) {
					if (Boolean.valueOf(element.getAttribute(color.toString().toLowerCase())))
						colors.add(color);
				}
				Color[] colorsArray = new Color[colors.size()];
				hex = new Filter(colors.toArray(colorsArray));
				break;
			}
			case "converter":
				hex = new Converter(Color.valueOf(element.getAttribute("color").toUpperCase()));
				break;
			case "gate":
				hex = new Gate();
				break;
			case "genericbridge": {
				boolean[] canPass = new boolean[]{Boolean.valueOf(element.getAttribute("northgate")),
						Boolean.valueOf(element.getAttribute("northeastgate")),
						Boolean.valueOf(element.getAttribute("southeastgate"))};
				hex = new GenericBridge(canPass);
				break;
			}
			case "specificbridge": {
				Color[] canPass = new Color[]{Color.valueOf(element.getAttribute("northgate").toUpperCase()),
						Color.valueOf(element.getAttribute("northeastgate").toUpperCase()),
						Color.valueOf(element.getAttribute("southeastgate").toUpperCase())};
				hex = new SpecificBridge(canPass);
				break;
			}
			case "amplifier":
				hex = new Amplifier();
				break;
			case "reducer":
				hex = new Reducer();
				break;
			default:
				throw new IllegalStateException("Bad hexagon name");
		}
		String fixedPosition = element.getAttribute("fixedposition");
		if (fixedPosition.isEmpty())
			fixedPosition = "false";
		assert hex != null;
		hex.setMovable(!Boolean.valueOf(fixedPosition));
		String orientation = element.getAttribute("orientation");
		if (orientation.isEmpty())
			orientation = "north";
		hex.rotate(Edge.valueOf(orientation.toUpperCase()));
		String fixedOrientation = element.getAttribute("fixedorientation");
		if (fixedOrientation.isEmpty())
			fixedOrientation = "false";
		hex.setRotatable(!Boolean.valueOf(fixedOrientation));
		return hex;
	}

	/**
	 * Creates a Map with the read data
	 * @param element					Map Element
	 * @return							A Map
	 */
	private Map loadMap(Element element) {
		return new Map(Integer.parseInt(element.getAttribute("width")), Integer.parseInt(element.getAttribute("height")),
				Boolean.valueOf(element.getAttribute("fill")));
	}

	/**
	 * Fills the Map with the read data
	 * @param element					Blocks Element
	 * @param map						Map to be filled
	 * @param level						The GameLevel
	 */
	private void loadBlocks(Element element, Map map, GameLevel level) {
		final NodeList positions = element.getElementsByTagName("position");
		for (int i = 0; i < positions.getLength(); ++i) {
			if (positions.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element position = (Element) positions.item(i);
				Pos pos = new Pos(Integer.valueOf(position.getAttribute("x")), Integer.valueOf(position.getAttribute("y")));
				Hexagon hex = null;
				NodeList hexagons = position.getChildNodes();
				for (int j = 0; j < hexagons.getLength(); ++j) {
					if (hexagons.item(j).getNodeType() == Node.ELEMENT_NODE) {
						if (hex == null) {
							hex = loadHexagon((Element) hexagons.item(j), level);
						} else {
							hex.setModifier((Modifier) loadHexagon((Element) hexagons.item(j), level));
						}
					}
				}
				map.setHexagon(hex, pos);
			}
		}
	}

	/**
	 * Loads the constraints
	 * @param element					Constraints Element
	 * @param map						Map to be filled
	 * @param level						The GameLevel
	 */
	private void loadConstraints(Element element, Map map, GameLevel level) {
		final NodeList positions = element.getElementsByTagName("position");
		for (int i = 0; i < positions.getLength(); ++i) {
			if (positions.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element position = (Element) positions.item(i);
				Pos pos = new Pos(Integer.valueOf(position.getAttribute("x")), Integer.valueOf(position.getAttribute("y")));
				NodeList constraints = position.getChildNodes();
				for (int j = 0; j < constraints.getLength(); ++j) {
					if (constraints.item(j).getNodeType() == Node.ELEMENT_NODE) {
						map.setConstraint(loadHexagon((Element) constraints.item(j), level), pos);
					}
				}
			}
		}
	}

	/**
	 * Loads the Container
	 * @param element					Container Element
	 * @param level						The GameLevel
	 *@return    						A Container
	 */
	private Container loadContainer(Element element, GameLevel level) {
		Container container = new Container();
		final NodeList containerObjects = element.getChildNodes();
		for (int i = 0; i < containerObjects.getLength(); ++i) {
			if (containerObjects.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element hexagon = (Element) containerObjects.item(i);
				Hexagon hex = loadHexagon(hexagon, level);
				container.addHexagon(hex);
			}
		}
		return container;
	}

	private GameData loadGameData() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document document = null;
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(xmlFile);
		} catch (ParserConfigurationException | IOException | SAXException e) {
			e.printStackTrace();
		}
		assert document != null;
		Element levelEl = document.getDocumentElement();
		GameLevel level = loadLevel(levelEl);
		Element gridEl = (Element) levelEl.getElementsByTagName("grid").item(0);
		Map map = loadMap(gridEl);
		Element blocks = (Element) levelEl.getElementsByTagName("blocks").item(0);
		loadBlocks(blocks, map, level);
		Element constraints = (Element) levelEl.getElementsByTagName("constraints").item(0);
		loadConstraints(constraints, map, level);
		Element containerEl = (Element) levelEl.getElementsByTagName("container").item(0);
		Container container = loadContainer(containerEl, level);

		return new GameData(level, map, container);
	}

	/**
	 * Creates a GameData Object
	 * @return							A GameData
	 */
	public GameData getGameData() {
		return gameData;
	}
}
