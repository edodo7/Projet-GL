package be.ac.umons.util;

import be.ac.umons.model.hexagon.Hexagon;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class allow to encapsulate the current state of a playing level
 */
public class State implements Serializable {
    public Hexagon[][]  hexagons;
    public ArrayList<Hexagon> container;
    public Long seconds;
    public Integer moves;

    /**
     * @param hexagons The hexagons in the map of the playing level
     * @param container The hexagons in container of the playing level
     * @param seconds The seconds left in timer
     * @param moves The number of moves made in the playing level
     */
    public State(Hexagon[][] hexagons,ArrayList<Hexagon> container, Long seconds, Integer moves){
        this.hexagons = hexagons;
        this.container = container;
        this.seconds = seconds;
        this.moves = moves;
    }
}
