package be.ac.umons.controller;

import be.ac.umons.HexaMaze;
import be.ac.umons.game.Game;
import be.ac.umons.model.Container;
import be.ac.umons.model.Laser;
import be.ac.umons.model.hexagon.*;
import be.ac.umons.util.Color;
import be.ac.umons.util.Pos;
import be.ac.umons.util.Tile;
import be.ac.umons.view.BotBarRenderer;
import be.ac.umons.view.HexagonDragActor;
import be.ac.umons.view.MapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;


/**
 * Class used to handle interaction with the user.
 * @param <T>		Game instance
 */
public abstract class Controller<T extends Game> {

	protected T game;
	protected MapRenderer renderer;
	protected BotBarRenderer bot;
	protected DragAndDrop dnd;
	protected Container cont;
	TiledMapTileSet tileset;
	private boolean listenClick = true;

	/**
	 * Constructor
	 * @param _game				A Game that will contain the controller
	 */
	public Controller(T _game) {
		game = _game;
		renderer = game.getRenderer();
		bot = game.getBotRenderer();
		tileset = renderer.getMap().getTileSets().getTileSet(0);
		cont = game.getContainer();
	}

	/**
	 * Initializes the Drag and Drop system
	 */
	protected void addDragAndDrop() {
		dnd = new DragAndDrop();
		dnd.setDragTime(0);
		dnd.addSource(new DragAndDrop.Source(renderer) {
			final DragAndDrop.Payload payload = new DragAndDrop.Payload();

			@Override
			public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
				Vector3 realMousePos = getAbsPos(x, y, renderer);
				Pos mouseCell = renderer.getClickedCell(realMousePos);
				if (isGoodSource(mouseCell, false)) {
					preparePayload(payload, renderer, mouseCell, false);
				} else {
					cleanPayload(payload);
				}
				return payload;
			}
		});
		dnd.addSource(new DragAndDrop.Source(bot) {
			final DragAndDrop.Payload payload = new DragAndDrop.Payload();

			@Override
			public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
				Vector3 realMousePos = getAbsPos(x, y, bot);
				Pos mouseCell = bot.getClickedCell(realMousePos);
				if (isGoodSource(mouseCell, true)) {
					preparePayload(payload, bot, mouseCell, true);
				}
				return payload;
			}
		});
		dnd.addTarget(new DragAndDrop.Target(renderer) {
			@Override
			public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
				Vector3 realMousePos = getAbsPos(x, y, renderer);
				Pos mouseCell = renderer.getClickedCell(realMousePos);
				return isGoodTarget(mouseCell, payload, false);
			}

			@Override
			public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
				Vector3 realMousePos = getAbsPos(x, y, renderer);
				prepareMove(payload, realMousePos, false);
			}
		});
		dnd.addTarget(new DragAndDrop.Target(bot) {
			@Override
			public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
				Vector3 realMousePos = getAbsPos(x, y, bot);
				Pos mouseCell = bot.getClickedCell(realMousePos);
				return isGoodTarget(mouseCell, payload, true);
			}

			@Override
			public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
				Vector3 realMousePos = getAbsPos(x, y, bot);
				prepareMove(payload, realMousePos, true);
			}
		});
	}

	/**
	 * Checks if the element at the mouseCell position cn be used as source of a Drag and Drop move
	 * @param mouseCell			The Cell
	 * @param bot				True if the Cell belongs to a Container
	 * @return                  whether the element at the mouseCell position cn be used as source of a Drag and Drop move
	 */
	protected boolean isGoodSource(Pos mouseCell, boolean bot) {
		if (bot)
			return mouseCell != null && cont.getHexagons().size() > this.bot.getIndexFromPos(mouseCell);
		else
			return (mouseCell != null) && (game.getMap().getHexagon(mouseCell) != null) && (!game.getMap().getHexagon(mouseCell).isEmpty()) && (game.getMap().getOnTop(mouseCell).isMovable());
	}

	/**
	 * Prepares the Payload with the source Cell data
	 * @param payload			Payload to be prepared
	 * @param rend				MapRenderer corresponding to the object containing the source cell
	 * @param mouseCell			Source Cell
	 * @param isBot				True if the source cell belongs to a Container
	 */
	protected void preparePayload(DragAndDrop.Payload payload, MapRenderer rend, Pos mouseCell, boolean isBot) {
		Hexagon hex;
		if(isBot)
			hex = cont.getHexagon(game.getBotRenderer().getIndexFromPos(mouseCell));
		else
			hex = game.getMap().getHexagon(mouseCell);
		TravellerObjectContainer cont = new TravellerObjectContainer(rend, mouseCell, isBot);
		payload.setObject(cont);
		Tile[] tiles;
		if (hex.getModifier() == null)
			tiles = hex.getTiles();
		else
			tiles = hex.getModifier().getTiles();
		HexagonDragActor actor = new HexagonDragActor(
				tiles,
				rend.getRatioX(),
				rend.getRatioY(),
				tileset, rend.getCamera());
		payload.setDragActor(actor);
		renderer.hideMenu();
		bot.hideMenu();
	}

	/**
	 * Checks if the element at the mouseCell position cn be used as target of a Drag and Drop move
	 * @param mouseCell			The Cell
	 * @param payload			Payload containing the source cell
	 * @param bot				True if the Cell belongs to a Container
	 * @return                  whether the element at the mouseCell position cn be used as target of a Drag and Drop move
	 */
	protected  boolean isGoodTarget(Pos mouseCell, DragAndDrop.Payload payload, boolean bot){
		if((mouseCell==null) || (payload.getObject())==null){
			return false;
		}
		TravellerObjectContainer trav = (TravellerObjectContainer) payload.getObject();
		Hexagon src;
		if(trav.bot)
			src = game.getContainer().getHexagon(game.getBotRenderer().getIndexFromPos(((TravellerObjectContainer)payload.getObject()).pos));
		else
			src = game.getMap().getOnTop(((TravellerObjectContainer)payload.getObject()).pos);
		if(bot)
			return true;
		else{
			Hexagon target = game.getMap().getHexagon(mouseCell);
			return target != null && (target.isMovable() || src instanceof Modifier);
		}
	}

	/**
	 * Prepares the needed elements and launches the drag and drop move
	 * @param payload			Payload containing the src cell data
	 * @param realMousePos		Target cell
	 * @param isBot				True if target belongs to a Container
	 */
	protected void prepareMove(DragAndDrop.Payload payload, Vector3 realMousePos, boolean isBot) {
		TravellerObjectContainer cont = (TravellerObjectContainer) payload.getObject();
		Pos from = cont.pos;
		Pos to;
		if (isBot)
			to = bot.getClickedCell(realMousePos);
		else
			to = renderer.getClickedCell(realMousePos);
		switchHexagon(from, to, cont.bot, isBot);
		cleanPayload(payload);
	}

	/**
	 * Cleans the Payload object
	 * @param pay		Payload to be cleaned
	 */
	protected void cleanPayload(DragAndDrop.Payload pay) {
		pay.setObject(null);
		pay.setDragActor(null);
	}

	/**
	 * Switches the content of two hexagons
	 * @param from 				Cell of the first hexagon
	 * @param to   				Cell of the second hexagon
	 * @param fromBot			True if the first hexagon belongs to a container
	 * @param toBot				True if the second hexagon belongs to a container
	 */
	protected void switchHexagon(Pos from, Pos to, boolean fromBot, boolean toBot) {
		if (!fromBot || !toBot) {
			if (fromBot) {
				int containerIndex = (1 - from.getY()) * 10 + from.getX();
				game.getMap().moveFromContainer(game.getContainer(), containerIndex, to);
			} else if (toBot) {
				game.getMap().moveToContainer(from, game.getContainer());
			} else {
				game.getMap().moveOnMap(from, to);
			}
		}
	}

	/**
	 * Process when the TiledMap is clicked
	 * @param x 				X coordinate of the clicked position
	 * @param y 				Y coordinate of the clicked position
	 * @param rend				Clicked MapRenderer
	 * @param isBot				True if the clicked cell belongs to a Container
	 */
	public void processInput(float x, float y, MapRenderer rend, boolean isBot) {
		Vector3 mousePos = getAbsPos(x, y, rend);
		Pos clickedCell = rend.getClickedCell(new Vector3(mousePos.x, mousePos.y, 0));
		if (isBot)
			renderer.hideMenu();
		else
			bot.hideMenu();
		boolean isOK;
		if (clickedCell != null) {
			if (!isBot)
				isOK = game.getMap().getHexagon(clickedCell) != null && !game.getMap().getHexagon(clickedCell).isEmpty() && listenClick;
			else
				isOK = bot.getIndexFromPos(clickedCell) < cont.getHexagons().size() && listenClick;
		} else {
			isOK = false;
		}
		if (isOK) {
			if (rend.isMenuShown()) {
				int res = rend.checkMenuIcons(mousePos.x, mousePos.y);
				switch (res) {
					case -1:
						showMenu(clickedCell, rend);
						break;
					case 0:
						if (!game.getMap().getHexagon(clickedCell).isEmpty() && game.getMap().getHexagon(clickedCell).isMovable()) {
							switchHexagon(clickedCell, null, false, true);
							renderer.hideMenu();
						}
						break;
					case 1:
						rotateHexagon(clickedCell, false, isBot);
						break;
					case 2:
						rotateHexagon(clickedCell, true, isBot);
						break;
					case 3:
						game.getMap().getHexagon(clickedCell).setMovable(!game.getMap().getHexagon(clickedCell).isMovable());
						break;
					case 4:
						game.getMap().getHexagon(clickedCell).setRotatable(!game.getMap().getHexagon(clickedCell).isRotatable());
						break;
					case 5:
						if (isBot)
							chooseColor(cont.getHexagon(bot.getIndexFromPos(clickedCell)), Color.RED);
						else
							chooseColor(game.getMap().getHexagon(clickedCell), Color.RED);
						break;
					case 6:
						if (isBot)
							chooseColor(cont.getHexagon(bot.getIndexFromPos(clickedCell)), Color.GREEN);
						else
							chooseColor(game.getMap().getHexagon(clickedCell), Color.GREEN);
						break;
					case 7:
						if (isBot)
							chooseColor(cont.getHexagon(bot.getIndexFromPos(clickedCell)), Color.BLUE);
						else
							chooseColor(game.getMap().getHexagon(clickedCell), Color.BLUE);
						break;
					case 8:
						if (isBot)
							turnColors((SpecificBridge) cont.getHexagon(bot.getIndexFromPos(clickedCell)));
						else
							turnColors((SpecificBridge) game.getMap().getHexagon(clickedCell));
						break;
					case 9:
						updateConstraints(clickedCell);
						break;
					case 10:
						if (isBot)
							updateBridgeWay((GenericBridge) cont.getHexagon(bot.getIndexFromPos(clickedCell)));
						else
							updateBridgeWay((GenericBridge) game.getMap().getHexagon(clickedCell));
						break;
					default:
						showMenu(clickedCell, rend);
						break;
				}
			} else {
				showMenu(clickedCell, rend);
			}
		} else {
			renderer.hideMenu();
			bot.hideMenu();
		}

	}

	/**
	 * Shows the actions menu in a Cell
	 * @param clickedCell		Cell where the actions menu will be show
	 * @param rend				MapRenderer corresponding to the object containing the cell
	 */
	protected void showMenu(Pos clickedCell, MapRenderer rend) {
		rend.setMenu(clickedCell);
	}

	/**
	 * Changes the orientation of an hexagon
	 * @param position      	Position of the hexagon
	 * @param anticlockwise 	True if the rotation is anticlockwise
	 * @param botBar		 	True if the hexagon belongs to a Container
	 */
	protected void rotateHexagon(Pos position, boolean anticlockwise, boolean botBar) {
		Hexagon hex;
		if (botBar)
			hex = cont.getHexagons().get(bot.getIndexFromPos(position));
		else
			hex = game.getMap().getHexagon(position);
		if (anticlockwise)
			hex.rotate(hex.getRotation().getNext());
		else
			hex.rotate(hex.getRotation().getPrev());
	}


	/**
	 * Changes the color of an Hexagon (If it already contains that color, it is removed)
	 * @param hex			The Hexagon
	 * @param color			The new Color
	 */
	public void chooseColor(Hexagon hex, Color color) {
		switch (hex.getClass().getSimpleName()) {
			case "Source":
				Laser laser = ((Source) hex).getLaser();
				laser.setColor(color,!laser.getColors().get(color));
				break;
			case "Filter":
				((Filter) hex).getFilterColors().put(color, !((Filter) hex).getFilterColors().get(color));
				break;
			case "Converter":
				((Converter) hex).setConvertColor(color);
				break;
			case "SpecificBridge":
				int index = (hex.getRotation().toInt() * 2) % 3;
				if (((SpecificBridge) hex).getCrossColors()[index] != null && ((SpecificBridge) hex).getCrossColors()[index].equals(color))
					((SpecificBridge) hex).modifyColor(null, index);
				else
					((SpecificBridge) hex).modifyColor(color, index);
		}
	}


	/**
	 * Applies a rotation to the colors of an SpecificBridge
	 * @param hex		The SpecificBridge
	 */
	public void turnColors(SpecificBridge hex) {
		Color tmp = hex.getCrossColors()[0];
		hex.getCrossColors()[0] = hex.getCrossColors()[2];
		hex.getCrossColors()[2] = hex.getCrossColors()[1];
		hex.getCrossColors()[1] = tmp;
	}

	/**
	 * Applies or removes a constraint to a cell (Uses the current Hexagon in that cell as constraint)
	 * @param pos		Cell where the constraint will be applied/removed
	 */
	public void updateConstraints(Pos pos) {
		Hexagon hex = game.getMap().getHexagon(pos);
		Hexagon constraint = game.getMap().getConstraint(pos);
		if (constraint == null || !constraint.getClass().getSimpleName().equals(hex.getClass().getSimpleName()))
			try {
				game.getMap().setConstraint(game.getMap().getHexagon(pos).clone(), pos);
			} catch (Exception e) {
				e.printStackTrace();
			}
		else {
			game.getMap().setConstraint(null, pos);
		}
	}

	/**
	 * Enables/disables a way of a GenericBridge
	 * @param hex		The GenericBridge
	 */
	public void updateBridgeWay(GenericBridge hex) {
		int index = (hex.getRotation().toInt() * 2) % 3;
		hex.getCanPass()[index] = !hex.getCanPass()[index];
	}

	/**
	 * Returns an absolute position from a relative position within a MapRenderer
	 * @param origX 			X component of the relative position
	 * @param origY 			Y component of the relative position
	 * @param rend				The MapRenderer
	 * @return Absolute position
	 */
	protected Vector3 getAbsPos(float origX, float origY, MapRenderer rend) {
		return new Vector3(origX + rend.getActorX(), HexaMaze.HEIGHT - (origY + rend.getActorY()), 0);
	}

	/**
	 * Updates this controller with the GameMapRenderer from his Game instance
	 */
	public void update() {
		renderer = game.getRenderer();
		dnd.clear();
		addDragAndDrop();
	}

	/**
	 * Makes the controller stop listening events
	 */
	public void cleanController() {
		dnd.clear();
		listenClick = false;
	}

	/**
	 * Class used to send data from a DragAndDrop.Source to an DragAndDrop.Target
	 */
	protected class TravellerObjectContainer {
		public MapRenderer src;
		public Pos pos;
		public boolean bot;

		public TravellerObjectContainer(MapRenderer src, Pos pos, boolean bot) {
			this.src = src;
			this.pos = pos;
			this.bot = bot;
		}
	}
}