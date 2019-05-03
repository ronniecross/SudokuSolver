/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sudoku_solver.Logic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 *
 * @author cross
 */
public class Square extends GroupContainer {

    HashSet<Column> columns;
    HashSet<Row> rows;

    protected Square(int position) {
        super(position);
    }

    protected Square(int position, HashSet<Column> columns, HashSet<Row> rows) {
        super(position);
        this.columns = columns;
        this.rows = rows;
    }

    /**
     * Runs through several checks to try and determine whether this coordinate
     * belongs in this number.
     *
     * @param coord
     * @param number
     * @return
     */
    protected boolean trySolve(Coordinate coord, Number number) {
        // FIRST CHECK: Cross reference the other rows and columns occupying
        // the same square as the given coordinate. If both return '2', meaning
        // for both rows and columns, the rows and/or columns in this square
        // that are not the row/and or column which this coordinate occupies
        // contains the given number, then it can be concluded that this number
        // belongs in this coordinate. 
        int rowCount = rowCheck(rows, coord, number);
        int colCount = columnCheck(columns, coord, number);
        if (rowCount == 2 && colCount == 2) {
            return true;
        }
        return false;
    }

    /**
     * Given a coordinate, identifies the current row or column (depending on
     * containerSet type) and checks the remaining row/columns in the square for
     * the specified number. Returns the number of containers found to contain
     * the number Number. NOTE: This should only be used when it has already
     * been confirmed (i.e. through Coordinate.possible() that Number is not
     * already assigned to another Coordinate in the coordinate's current group
     * container.
     *
     * @param containerSet
     * @param coord
     * @param number
     * @param containerType "Row" or "Column"
     * @return
     */
//    private int crossCheck(HashSet<GroupContainer> containerSet,
//            Coordinate coord, Number number, String containerType) {
//        // Create a new set which contains the remaining two containers. 
//        HashSet<GroupContainer> modContainerSet = new HashSet<>();
//        for(GroupContainer c : containerSet) {
//            if (containerType.equals("Row") && coord.row != c) {
//                modContainerSet.add(c);
//            } else if (containerType.equals("Column") && coord.column != c) {
//                modContainerSet.add(c);
//            } else {
//                return -1;
//            }
//        }
//        // Declare a counter
//        int counter = 0;
//        // Determine whether 0, 1 or 2 of the other containers contins
//        for(GroupContainer c : modContainerSet) {
//            for(Coordinate crd : c.coords) {
//                if (crd.number == number) {
//                    counter ++;
//                }
//            }
//        }
//        // the given Number.
//        return counter;
//    }
    private int rowCheck(HashSet<Row> rowSet,
            Coordinate coord, Number number) {
        // Create a new set which contains the remaining two containers. 
        HashSet<Row> modRowSet = new HashSet<>();
        for (Row r : rowSet) {
            if (coord.row != r) {
                modRowSet.add(r);
            }
        }
        // Declare a counter
        int counter = 0;
        // Determine whether 0, 1 or 2 of the other containers contins
        for (Row r : modRowSet) {
            for (Coordinate crd : r.coords) {
                if (crd.number == number) {
                    counter++;
                }
            }
        }
        // the given Number.
        return counter;
    }

    private int columnCheck(HashSet<Column> colSet,
            Coordinate coord, Number number) {
        // Create a new set which contains the remaining two containers. 
        HashSet<Column> modColSet = new HashSet<>();
        for (Column c : colSet) {
            if (coord.column != c) {
                modColSet.add(c);
            }
        }
        // Declare a counter
        int counter = 0;
        // Determine whether 0, 1 or 2 of the other containers contins
        for (Column c : modColSet) {
            for (Coordinate crd : c.coords) {
                try {
                    if (crd.number == number) {
                        counter++;
                    }
                } catch (Exception ex) {
                    
                }
            }
        }
        // the given Number.
        return counter;
    }

    protected static HashMap<Integer, Square> initCollection(
            HashMap<Integer, Column> columns, HashMap<Integer, Row> rows) {
        HashMap<Integer, Square> squares = new HashMap<>();
        for (Integer i = 1; i <= 9; i++) {
            //Add squares
            HashSet<Column> squareCols = new HashSet<>();
            HashSet<Row> squareRows = new HashSet<>();
            int modi = i%3;
            if (modi == 0) {
                modi = 3;
            }
            squareCols.add(columns.get((modi*3) - 2));
            squareCols.add(columns.get((modi*3) - 1));
            squareCols.add(columns.get(modi*3));

            if (i == 1 || i == 2 || i == 3) {
                squareRows.add(rows.get(1));
                squareRows.add(rows.get(2));
                squareRows.add(rows.get(3));
            }
            if (i == 4 || i == 5 || i == 6) {
                squareRows.add(rows.get(4));
                squareRows.add(rows.get(5));
                squareRows.add(rows.get(6));
            }
            if (i == 7 || i == 8 || i == 9) {
                squareRows.add(rows.get(7));
                squareRows.add(rows.get(8));
                squareRows.add(rows.get(9));
            }
            squares.put(i, new Square(i, squareCols, squareRows));
        }
        return squares;
    }
}
