/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sudoku_solver.Logic;

import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author cross
 */
public class Number {
    int number;
    HashSet<Coordinate> coords;
    protected Number(int number) {
        this.number = number;
        coords = new HashSet<>();
    }
    
    /**
     * Adds a new Coordinate object to the collection of linked Coordinate objects.
     * @param coord The Coordinate object to add.
     */
    protected void putCoord(Coordinate coord) {
        coords.add(coord);
    }
    
    /**
     * Returns the number of linked Coordinate objects
     * @return The number of linked Coordinate objects.
     */
    protected int countCoords() {
        return coords.size();
    }
    
    public int getNumber() {
        return number;
    }
}
