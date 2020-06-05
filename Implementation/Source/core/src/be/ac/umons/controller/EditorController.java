package be.ac.umons.controller;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

import be.ac.umons.game.Editor;
import be.ac.umons.model.EditorContainer;
import be.ac.umons.model.EditorMap;
import be.ac.umons.model.hexagon.Hexagon;
import be.ac.umons.model.hexagon.Modifier;
import be.ac.umons.util.Pos;
import be.ac.umons.util.Tile;
import be.ac.umons.view.BotBarRenderer;
import be.ac.umons.view.HexagonDragActor;
import be.ac.umons.view.MapRenderer;

/**
 * Controller used in the Editor
 * @param <T>			Editor instance
 */
public class EditorController<T extends Editor> extends Controller<T> {

	private BotBarRenderer editorRenderer;
	private EditorContainer editorContainer;

	/**
	 * Constructor
	 * @param game				An Editor that will contain the controller
	 */
	public EditorController(T game) {
		super(game);
		this.editorRenderer = game.getBotEditorRenderer();
		this.editorContainer = game.getEditorContainer();
		addDragAndDrop();
		game.getMap().sendLaser();
	}

	/**
	 * Prepares the needed elements and launches the drag and drop move
	 * @param payload			Payload containing the src cell data
	 * @param realMousePos		Target cell
	 * @param isBot				True if target belongs to a Container
	 * @param toEditor			True if the target belongs to an EditorContainer
	 */
	protected void prepareMove(DragAndDrop.Payload payload, Vector3 realMousePos, boolean isBot, boolean toEditor) {
		EditorTravellerObjectContainer cont = (EditorTravellerObjectContainer) payload.getObject();
		Pos from = cont.pos;
		Pos to;
		if (isBot)
			to = bot.getClickedCell(realMousePos);
		else
			to = renderer.getClickedCell(realMousePos);
		switchHexagon(from, to, cont.bot, isBot, cont.editor, toEditor);
		cleanPayload(payload);
	}

	/**
	 * Switches the content of two hexagons
	 *
	 * @param from 				Cell of the first hexagon
	 * @param to   				Cell of the second hexagon
	 * @param fromBot			True if the first hexagon belongs to a container
	 * @param toBot				True if the second hexagon belongs to a container
	 * @param fromEditor		True if the first hexagon belongs to an EditorContainer
	 * @param toEditor			True if the second hexagon belongs to an EditorContainer
	 */
	protected void switchHexagon(Pos from, Pos to, boolean fromBot, boolean toBot, boolean fromEditor, boolean toEditor) {
		if (!fromEditor && !toEditor)
			super.switchHexagon(from, to, fromBot, toBot);
		else {
			if (toEditor && !fromEditor) {
				if (fromBot)
					game.getContainer().removeHexagon(Math.abs(from.getY() - 1) * 10 + from.getX());
				else
					game.getMap().moveToContainer(from, editorContainer);
			} else if (!toEditor && toBot)
				cont.addHexagon(editorContainer.getHexagon(Math.abs(from.getY() - 1) * 10 + from.getX()));
			else if (!toEditor) {
				int containerIndex = (1 - from.getY()) * 10 + from.getX();
				game.getMap().moveFromContainer(editorContainer, containerIndex, to);
			}
		}
		game.getMap().sendLaser();


	}

	/**
	 * Prepares the Payload with the source Cell data
	 * @param payload			Payload to be prepared
	 * @param rend				MapRenderer corresponding to the object containing the source cell
	 * @param mouseCell			Source Cell
	 * @param isBot				True if the source cell belongs to a Container
	 * @param isEditor			True if the source cell belongs to an EditorContainer
	 */
	protected void preparePayload(DragAndDrop.Payload payload, MapRenderer rend, Pos mouseCell, boolean isBot, boolean isEditor) {
		EditorTravellerObjectContainer cont = new EditorTravellerObjectContainer(rend, mouseCell, isBot, isEditor);
		payload.setObject(cont);
		Tile[] tiles;
		Hexagon hex;
		if(isEditor)
			hex = game.getEditorContainer().getHexagon(game.getBotEditorRenderer().getIndexFromPos(mouseCell));
		else if(isBot)
			hex = game.getContainer().getHexagon(game.getBotRenderer().getIndexFromPos(mouseCell));
		else
			hex = game.getMap().getHexagon(mouseCell);
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

	@Override
	protected void addDragAndDrop() {
		super.addDragAndDrop();
		dnd.addSource(new DragAndDrop.Source(editorRenderer) {
			final DragAndDrop.Payload payload = new DragAndDrop.Payload();

			@Override
			public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
				Vector3 realMousePos = getAbsPos(x, y, bot);
				Pos mouseCell = bot.getClickedCell(realMousePos);
				if (mouseCell != null && editorContainer.getHexagons().size() > Math.abs(mouseCell.getY() - 1) * 10 + mouseCell.getX()) {
					preparePayload(payload, bot, mouseCell, true, true);
				}
				return payload;
			}
		});
		dnd.addTarget(new DragAndDrop.Target(editorRenderer) {
			@Override
			public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
				return payload.getObject() != null;
			}

			@Override
			public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
				Vector3 realMousePos = getAbsPos(x, y, bot);
				prepareMove(payload, realMousePos, true, true);
			}
		});
	}

	@Override
	protected boolean isGoodSource(Pos mouseCell, boolean bot) {
		if (bot)
			return mouseCell != null && cont.getHexagons().size() > Math.abs(mouseCell.getY() - 1) * 10 + mouseCell.getX();
		else
			return (mouseCell != null) && (game.getMap().getHexagon(mouseCell) != null) && (game.getMap().getOnTop(mouseCell).isMovable());
	}

	@Override
	protected boolean isGoodTarget(Pos mouseCell, DragAndDrop.Payload payload, boolean bot) {
		if((mouseCell==null) || (payload.getObject())==null){
			return false;
		}
		EditorTravellerObjectContainer trav = (EditorTravellerObjectContainer) payload.getObject();
		Hexagon src;
		if(trav.editor)
			src = game.getEditorContainer().getHexagon(game.getBotRenderer().getIndexFromPos(((TravellerObjectContainer)payload.getObject()).pos));
		else if(trav.bot)
			src = game.getContainer().getHexagon(game.getBotRenderer().getIndexFromPos(((TravellerObjectContainer)payload.getObject()).pos));
		else
			src = game.getMap().getOnTop(((TravellerObjectContainer)payload.getObject()).pos);
		if(bot)
			return true;
		else{
			Hexagon target = game.getMap().getHexagon(mouseCell);
			return (target==null || target.isEmpty() ||target.isMovable() || src instanceof Modifier);
		}
	}

	@Override
	protected void prepareMove(DragAndDrop.Payload payload, Vector3 realMousePos, boolean isBot) {
		prepareMove(payload, realMousePos, isBot, false);
	}

	@Override
	protected void preparePayload(DragAndDrop.Payload payload, MapRenderer rend, Pos mouseCell, boolean isBot) {
		preparePayload(payload, rend, mouseCell, isBot, false);

	}

	@Override
	public void processInput(float x, float y, MapRenderer rend, boolean isBot) {
		Vector3 mousePos = getAbsPos(x, y, rend);
		Pos clickedCell = rend.getClickedCell(new Vector3(mousePos.x, mousePos.y, 0));
		boolean checkBot = (clickedCell != null) && (!isBot || Math.abs(clickedCell.getY() - 1) * 10 + clickedCell.getX() < game.getContainer().getHexagons().size());
		boolean checkRend = (clickedCell != null) && (isBot || game.getMap().getHexagon(clickedCell) != null && !game.getMap().getHexagon(clickedCell).isEmpty());
		if (checkBot && checkRend) {
			super.processInput(x, y, rend, isBot);
		} else {
			renderer.hideMenu();
			bot.hideMenu();
		}
		game.getMap().sendLaser();

	}

	/**
	 * TravellerObjectContainer that takes also into account if the source belongs to an EditorContainer
	 */
	private class EditorTravellerObjectContainer extends TravellerObjectContainer {

		private boolean editor;

		public EditorTravellerObjectContainer(MapRenderer src, Pos pos, boolean bot, boolean editor) {
			super(src, pos, bot);
			this.editor = editor;

		}
	}




}