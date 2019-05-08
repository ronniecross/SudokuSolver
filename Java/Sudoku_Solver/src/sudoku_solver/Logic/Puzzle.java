/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sudoku_solver.Logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;

/**
 *
 * @author cross
 */
public class Puzzle {

    HashMap<Integer, Row> rows;
    HashMap<Integer, Column> columns;
    HashMap<Integer, Square> squares;
    HashMap<Integer, Number> numbers;
    HashMap<Integer, Coordinate> coords;
    protected static boolean debug = false;

    public Puzzle(HashMap<Integer, Integer> mappings) {
        initialize(mappings);
    }
    
    /**
     * Initialise a new puzzle with an empty set of mappings.
     */
    public Puzzle() {
        HashMap<Integer, Integer> mappings = new HashMap<>();
        initialize(mappings);
    }

    private void initialize(HashMap<Integer, Integer> mappings) {
        rows = new HashMap<>();
        columns = new HashMap<>();
        squares = new HashMap<>();
        numbers = new HashMap<>();
        coords = new HashMap<>();

        for (Integer i = 1; i <= 9; i++) {
            rows.put(i, new Row(i));
            columns.put(i, new Column(i));
            numbers.put(i, new Number(i));
        }
        
        squares = Square.initCollection(columns, rows);

        // Create coordinates
        for (Integer i = 1; i <= 81; i++) {
            // Assign variables for the ID
            // of the row, column and square.
            Integer row;
            Integer column;
            Integer square;

            // Get the ID of the column.
            column = i % 9;
            if (column == 0) {
                column = 9;
            }
            //System.out.println(i + " % " + " 9 = " + column);

            // Get the ID of the row.
            if (column == 9) {
                row = (int) (i / 9);
            } else {
                row = (int) (i / 9) + 1;
            }
            // Get the ID of the square.
            if (column >= 1 && column <= 3) {
                if (row >= 1 && row <= 3) {
                    square = 1;
                } else if (row <= 6) {
                    square = 4;
                } else if (row <= 9) {
                    square = 7;
                } else {
                    square = -1;
                }
            } else if (column <= 6) {
                if (row >= 1 && row <= 3) {
                    square = 2;
                } else if (row <= 6) {
                    square = 5;
                } else if (row <= 9) {
                    square = 8;
                } else {
                    square = -1;
                }
            } else if (column <= 9) {
                if (row >= 1 && row <= 3) {
                    square = 3;
                } else if (row <= 6) {
                    square = 6;
                } else if (row <= 9) {
                    square = 9;
                } else {
                    square = -1;
                }
            } else {
                square = -1;
            }

            // create the coordinate
            try {
                coords.put(i,
                        new Coordinate(
                                rows.get(row),
                                columns.get(column),
                                squares.get(square)
                        )
                );
            } catch (Exception ex) {
                if (debug) {
                    System.out.println("Row: " + row + ", Column: " + column
                        + ", Square: " + square + " failed.");
                }
            }
        }

        // Get a set of keys from Mappings
        Set<Integer> mappingKeys = mappings.keySet();
        // Iterate the keys in mappingKeys
        for (int coordInt : mappingKeys) {
            Coordinate coord = coords.get(coordInt);
            Number number = numbers.get(mappings.get(coordInt));
            coord.putNumber(number);
        }
    }
    
    /**
     * Returns a collection of unassigned coordinates.
     */
    public HashMap<Integer, Coordinate> getUnassignedCoords() {
        HashMap<Integer, Coordinate> result = new HashMap<>();
        Set<Integer> keys = coords.keySet();
        for(Integer k : keys) {
            if(coords.get(k).number == null) {
                result.put(k, coords.get(k));
            }
        }
        return result;
    }

    /**
     * Attempts to resolve the given coordinate, and returns a true or false
     * response indicating whether the operation was successful.
     *
     * @param coord The coordinate to resolve.
     * @return Returns true if the coordinate was resolved, otherwise returns
     * false.
     */
    protected boolean resolve(Coordinate coord, boolean trySim) {
        // Boolean to determine if coordinate has been resolved. 
        Boolean resolved = false;
        // Declare an integer to represent numbers through iteration.
        int numInt = 1;
        // Convert numbers into a hashSet
        HashSet<Number> numbersHS = new HashSet<>();
        for (Number number : numbers.values()) {
            numbersHS.add(number);
        }
        // Iterate each number, until the co-ordinate has been resolved or
        // all numbers have been iterated.
        while (numInt <= 9 && !resolved) {
            Number number = numbers.get(numInt);
            // Attempt to resolve the co-ordinate, and assign the success to
            // resolved. 
            resolved = coord.isNumber(number, numbersHS, trySim);
            // If not resolved, try to resolve using square solve
            // If the coordinate is resolved to this number,
            if (resolved) {
                // Assign this number to this coordinate
                coord.putNumber(number);
            }
            //increment numInt
            numInt++;
        }
        return resolved;
    }
    
    /**
     * Attempts to solve the puzzle, and returns a collection containing all
     * resolved coordinates and the number corresponding to the coordinates.
     *
     * @return A HashMap containing the coordinates and numbers.
     */
    public HashMap<Integer, Integer> solve() {
        // HashMap to store the results.
        HashMap<Integer, Integer> results = new HashMap<>();
        // Variable identifying iteration with no solved co-ordinates.
        Boolean failedIteration = false;
        // Variable identifying whether simulation will be trued.
        Boolean trySim = false;
        // Number of unassigned coordinates
        int unassigned = countUnassignedCoordinates();
        // Iterate co-ordinates until failed iteration, or all resolved.
        while (!failedIteration && results.size() != unassigned) {
            // Tracker to see if any changes have been made
            Boolean changeTracker = false;
            // Iterate over coordinates
            for (int i = 1; i <= coords.size(); i++) {
                // Get the current coordinate
                Coordinate coord = coords.get(i);
                // Check to see if coordinate is already assigned.
                boolean assigned = (coord.number != null);
                // Variable storing success of resolve attempts
                Boolean resolveResult = false;
                // If the coordinate is not already assigned, 
                if (!assigned) {
                    // Assign attempt to resolve coordinate to resolveResult
                    resolveResult = resolve(coord, trySim);
                }
                // If the coordinate was resolved, add it to the results.
                if (resolveResult) {
                    results.put(i, coord.number.number);
                    // If no change had previously been observed, update change tracker.
                    if (!changeTracker) {
                        changeTracker = true;
                    }}}
            // If no changes were observed, update failedIteration and, if
            // applicable, trySim.
            if (!changeTracker) {
                if (!trySim) {
                    trySim = true;
                    // If simulation was attempted and there were still no
                    // changes, consider this a failed iteration. 
                } else {
                    failedIteration = true;
                }
            }
            else {
                // If changes were observed, make sure trySim is false.
                trySim = false;
            }
        }
        // When iterating stops, return results set.
        return results;
    }
    
    /**
     * Returns the number of coordinates not assigned a number.
     * @return The number of unassigned coordinates.
     */
    public int countUnassignedCoordinates() {
        // Declare a counter variable, and initialize to 0.
        int counter = 0;
        // Get a list of keys for the coordinates map
        Set<Integer> coordsKeys = coords.keySet();
        // For each key,
        for(Integer key : coordsKeys) {
            // Get the coordinate
            Coordinate coord = coords.get(key);
            // If the coord is not linked to a number,
            if(coord.number == null) {
                // Increment counter
                counter++;
            }
        }
        // return the counter
        return counter;
    }
    
    /**
     * Returns the number of coordinates assigned a number.
     * @return The number of assigned coordinates.
     */
    public int countAssignedCoordinates() {
        // Declare a counter variable, and initialize to 0.
        int counter = 0;
        // Get a list of keys for the coordinates map
        Set<Integer> coordsKeys = coords.keySet();
        // For each key,
        for(Integer key : coordsKeys) {
            // Get the coordinate
            Coordinate coord = coords.get(key);
            // If the coord is linked to a number,
            if(coord.number != null) {
                // Increment counter
                counter++;
            }
        }
        // return the counter
        return counter;
    }


    
    public void testSimulation() {
        List<Number> numList = new ArrayList<>();
        int numbersToAdd = 9;
        for(int i = 1; i <= numbersToAdd; i++) {
            numList.add(numbers.get(i));
        }
        TreeMap<Integer,TreeMap<Integer, Number>> results = new TreeMap<>();
        TreeMap<Integer,Number> resultSet = new TreeMap<>();
            GroupContainer.getPossibleSequences(numList, results);
            
            // Iterate each result in results
            SortedSet<Integer> resultKeys = (SortedSet<Integer>) results.keySet();
            for (int rKey : resultKeys) {
                TreeMap<Integer, Number> resSet = results.get(rKey);
                if (debug) {
                    System.out.println();
                System.out.print("Collection: ");
                }
                // Iterate each result set
                SortedSet<Integer> rsKeys = (SortedSet<Integer>) resSet.keySet();
                for (int rsKey : rsKeys) {
                    Number num = resSet.get(rsKey);
                    System.out.print(num.number + ",");
                }
            }
    }
    
    public static void toggleDebug() {
        if (debug) {
            debug = false;
        } else {
            debug = true;
        }
    }
    
    /**
     * Resets each Coordinate in the puzzle so that it is no longer linked to
     * a Number object.
     */
    public void clearPuzzle() {
        Set<Integer> coordKeys = coords.keySet();
        for(int key : coordKeys) {
            coords.get(key).reset();
        }
    }
    
    public HashMap<Integer, Coordinate> getCoords() {
        return new HashMap<Integer, Coordinate>(coords);
    }
    
    /**
     * Initialise the puzzle with sample data for a difficult puzzle.
     * Useful for testing algorithm.
     */
    public void initHardSamplePuzzle() {
        // Clear the number associated with each coordinate in the coords collection.
        clearPuzzle();
        // Add sample data.
        coords.get(7).number = numbers.get(7);
        coords.get(15).number = numbers.get(2);
        coords.get(17).number = numbers.get(4);
        coords.get(19).number = numbers.get(4);
        coords.get(20).number = numbers.get(2);
        coords.get(22).number = numbers.get(7);
        coords.get(23).number = numbers.get(1);
        coords.get(26).number = numbers.get(6);
        coords.get(29).number = numbers.get(6);
        coords.get(30).number = numbers.get(1);
        coords.get(33).number = numbers.get(9);
        coords.get(36).number = numbers.get(4);
        coords.get(38).number = numbers.get(5);
        coords.get(39).number = numbers.get(4);
        coords.get(43).number = numbers.get(3);
        coords.get(44).number = numbers.get(1);
        coords.get(46).number = numbers.get(7);
        coords.get(49).number = numbers.get(5);
        coords.get(52).number = numbers.get(6);
        coords.get(53).number = numbers.get(8);
        coords.get(56).number = numbers.get(4);
        coords.get(59).number = numbers.get(6);
        coords.get(60).number = numbers.get(5);
        coords.get(62).number = numbers.get(7);
        coords.get(63).number = numbers.get(3);
        coords.get(65).number = numbers.get(9);
        coords.get(67).number = numbers.get(4);
        coords.get(75).number = numbers.get(5);
    }
}
