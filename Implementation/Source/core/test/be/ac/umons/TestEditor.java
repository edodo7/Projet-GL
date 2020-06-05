package be.ac.umons;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import be.ac.umons.game.BasicGame;
import be.ac.umons.game.Editor;
import be.ac.umons.game.EditorLevel;
import be.ac.umons.game.Extension;
import be.ac.umons.game.GameLevel;
import be.ac.umons.game.Mode;
import be.ac.umons.gui.EditorScreen;
import be.ac.umons.model.Container;
import be.ac.umons.model.EditorMap;
import be.ac.umons.model.Laser;
import be.ac.umons.model.Map;
import be.ac.umons.model.hexagon.Amplifier;
import be.ac.umons.model.hexagon.Converter;
import be.ac.umons.model.hexagon.Empty;
import be.ac.umons.model.hexagon.GenericBridge;
import be.ac.umons.model.hexagon.Hexagon;
import be.ac.umons.model.hexagon.OneWayMirror;
import be.ac.umons.model.hexagon.Source;
import be.ac.umons.model.hexagon.Splitter;
import be.ac.umons.model.hexagon.Target;
import be.ac.umons.model.hexagon.ThreeWayMirror;
import be.ac.umons.model.hexagon.TwoWayMirror;
import be.ac.umons.model.xml.XMLGameLoader;
import be.ac.umons.util.Color;
import be.ac.umons.util.Edge;
import be.ac.umons.util.Pos;
import be.ac.umons.util.Segment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(GdxTestRunner.class)
public class TestEditor {
    private EditorMap editor;
    private Container container;


    @Before
    public void initMap() {
        editor = new EditorMap(5,5);
        container = new Container();
        HexaMaze.settings.setExtension(Extension.LEVEL_CHECKER,false);
    }

    @Test
    public void testUpdateSize() {
        editor.updateSize(15,10);
        assertEquals(editor.getWidth(), 15);
        assertEquals(editor.getHeight(), 10);
    }


    @Test
    public void testValidLevel() {
        editor.setHexagon(new Source(new Laser(false,false)),new Pos(0,0));
        assertTrue(!editor.checkLevel(container));
        editor.setHexagon(new Empty(),new Pos(0,1));
        editor.setHexagon(new Target(),new Pos(0,2));
        assertTrue(editor.checkLevel(container));
        HexaMaze.settings.setExtension(Extension.LEVEL_CHECKER,true);
        editor.getHexagon(new Pos(0,0)).rotate(Edge.SOUTH);
        editor.getHexagon(new Pos(0,0)).setRotatable(false);
        editor.getHexagon(new Pos(0,0)).setMovable(false);
        editor.getHexagon(new Pos(0,2)).setMovable(false);
        assertTrue(!editor.checkLevel(container));
        editor.getHexagon(new Pos(0,0)).setRotatable(true);
        assertTrue(editor.checkLevel(container));
    }

    @Test
    public void testLevelCopy(){
        Map testMap = new Map(editor.getWidth(),editor.getHeight(),true);
        testMap.setHexagon(new OneWayMirror(Edge.NORTH),new Pos(1,2));
        testMap.setHexagon(new Target(Edge.NORTHEAST), new Pos(2,2));
        testMap.setHexagon(new Source(new Laser(false,false)),new Pos(0,0));
        editor.updateSize(testMap.getWidth(),testMap.getHeight());
        editor.replaceMap(testMap.getHexagons(),testMap.getSourcesPos(),testMap.getTargetsPos(),testMap.getConstraints());
        for(int y = 0; y < editor.getHeight(); y ++){
            for (int x = 0; x < editor.getWidth(); x++){
                Pos pos = new Pos(x,y);
                assertTrue(editor.getHexagon(pos).equals(testMap.getHexagon(pos)));
            }
        }
    }

}
