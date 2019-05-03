/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sudoku_solver.Logic;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 *
 * @author cross
 */
public class Coordinate {
    Row row;
    Column column;
    Square square;
    Number number;
    
    protected Coordinate(Row row, Column column, Square square) {
        this.row = row;
        this.column = column;
        this.square = square;
        
        // Assign this coordinate to the row, column and square.
        this.row.coords.add(this);
        this.column.coords.add(this);
        this.square.coords.add(this);
    }
    
    /**
     * Performs a number of checks to see if this number belongs in this
     * coordinate. If so, the number is assigned to this coordinate and true
     * is returned. Otherwise, false is returned. 
     * @param number The number to check.
     * @return Returns true if the number belongs in this coordinate. Otherwise
     * returns false. 
     */
    protected boolean isNumber(Number number, HashSet<Number> numbers) {
        // Declare a result
        boolean result = false;
        // Is it possible for this coordinate to contain this number?
        Boolean possible = possible(number);
        // If so, do either the row, column or square have exactly
        // one available coordinate?
        int rowBlanks = row.countBlank();
        int colBlanks = column.countBlank();
        int squareBlanks = square.countBlank();
        Boolean lastAvailable = (
                rowBlanks == 1
                || colBlanks == 1
                || squareBlanks == 1);
        
        // If it is possible for this number to go in this coordinate,
        if(possible) {
            // If it is the last number for any row, column or square,
            if(lastAvailable) {
                // Return true.
                result = true;
            }
            // Otherwise,
            else {
                // Are there any other possible numbers in the row,
                // column or square?
                Boolean singlePossible = (
                        singlePossible(row.getRemainingNumbers(numbers))
                        && singlePossible(column.getRemainingNumbers(numbers))
                        && singlePossible(square.getRemainingNumbers(numbers))
                        );
                if (singlePossible) {
                    // If this is the only possible number, return true.
                    return true;
                } else if (square.trySolve(this, number)) {
                    return true;
                }
                else {
                    // Try simulation
                    boolean simSuccess = false;
                    // Check simsuccess individually so individual containers
                    // only attempt a simulation if the number has not been found
                    // through an earlier simulation.
                    if (!simSuccess) {
                        simSuccess = row.solveThroughSimulation(numbers, this, number);
                    }
                    if (!simSuccess) {
                        simSuccess = column.solveThroughSimulation(numbers, this, number);
                    }
                    if (!simSuccess) {
                        simSuccess = square.solveThroughSimulation(numbers, this, number);
                    }
                    // Otherwise return false. 
                    if (simSuccess) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }
        return result;
    }
    
    /**
     * Checks the given number to see if it is possible that this number
     * belongs in this coordinate. This method will inspect the row, column
     * and square linked to this coordinate for the given number. If the
     * given number is found in either of those containers, returns false.
     * Otherwise, returns true.
     * @param number The number to check. 
     * @return Returns true if it is possible for this number to belong in this 
     * coordinate. Otherwise returns false. 
     */
    protected boolean possible(Number number) {
        return (!row.hasNumber(number) && 
                !column.hasNumber(number) && 
                !square.hasNumber(number));
    }
    
    /**
     * Given a list of numbers, checks to see how many are possible. 
     * If there is more than one possible, returns false. Otherwise returns true.
     * @param numbers The collection of numbers to check. 
     * @return False if multiple possibilities, otherwise true. 
     */
    protected boolean singlePossible(HashSet<Number> numbers) {
        // Declare a result
        int counter = 0;
        // Iterate over the Number collection, 
        Iterator<Number> i = numbers.iterator();
        while(i.hasNext()) {
            // See if the current number is possible.
            boolean possible = possible(i.next());
            if (possible) {
                // If so, increment the counter.
                counter ++;
            }
        }
        // Return true if the counter is 1 or less.
        return (counter <= 1);
    }
    
    protected void putNumber(Number number) {
        this.number = number;
    }
    
    /**
     *
     * @param rows
     * @param columns
     * @return
     */
    protected static HashMap<Integer, Coordinate> getCoords(
        HashMap<Integer, Row> rows,
        HashMap<Integer, Column> columns,
        HashMap<Integer, Square> squares) {
                
        HashMap<Integer, Coordinate> coords = new HashMap<>();
        // Create coordinates
        for (Integer i = 1; i <= 81; i++) {
            // Assign variables for the ID
            // of the row, column and square.
            Integer row;
            Integer column;
            Integer square;

            // Get the ID of the column.
            column = i%9;
            
            // Get the ID of the row.
            if(column == 9) {
                row = (int)(i/9);
            } else {
                row = (int)(i/9)+1;
            }
            // Get the ID of the square.
            if(column >= 1 && column <= 3) {
                if(row >= 1 && row <=3) {square = 1;}
                else if(row <= 6) {square = 4;}
                else if(row <=9) {square = 7;}
                else {square = -1;}
            } else if (column <= 6) {
                if(row >= 1 && row <=3) {square = 2;}
                else if(row <= 6) {square = 5;}
                else if(row <=9) {square = 8;}
                else {square = -1;}
            } else if (column <= 9) {
                if(row >= 1 && row <=3) {square = 3;}
                else if(row <= 6) {square = 6;}
                else if(row <=9) {square = 9;}
                else {square = -1;}
            } else {square = -1;}
            
            // create the coordinate
            coords.put(i, 
                    new Coordinate(
                            rows.get(row),
                            columns.get(column),
                            squares.get(square)
                    )
            );
        }
        return coords;
    }
    
    public Number getNumber() {
        return number;
    }
    
    /**
     * Resets the coordinate so that it is no longer linked to a Number.
     * @return 
     */
    protected Boolean reset() {
        number = null;
        return number == null;
    }
}
