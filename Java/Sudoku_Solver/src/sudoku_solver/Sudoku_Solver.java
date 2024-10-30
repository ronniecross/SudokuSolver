/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sudoku_solver;

import java.util.HashMap;
import java.util.HashSet;
import sudoku_solver.Logic.Puzzle;

/**
 *
 * @author cross
 */
public class Sudoku_Solver {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
            HashMap<Integer, Integer> mappings = new HashMap<>();
            mappings.put(2, 8);
            mappings.put(3,5);
            mappings.put(4,9);
            mappings.put(6,4);
            mappings.put(7,7);
            mappings.put(8,1);
            mappings.put(10,4);
            mappings.put(14,5);
            mappings.put(17,2);
            mappings.put(19,3);
            mappings.put(20,9);
            mappings.put(25,8);
            mappings.put(31,4);
            mappings.put(32,6);
            mappings.put(35,7);
            mappings.put(36,1);
            mappings.put(38,4);
            mappings.put(41,8);
            mappings.put(44,3);
            mappings.put(46,1);
            mappings.put(47,2);
            mappings.put(50,3);
            mappings.put(51,5);
            mappings.put(57,1);
            mappings.put(62,9);
            mappings.put(63,2);
            mappings.put(65,7);
            mappings.put(68,9);
            mappings.put(72,4);
            mappings.put(74,3);
            mappings.put(75,4);
            mappings.put(76,5);
            mappings.put(78,2);
            mappings.put(79,6);
            mappings.put(80,8);
            Puzzle puzzle = new Puzzle(mappings);
        try {
            System.out.println(puzzle.solve());
        } catch (Exception ex) {
            System.out.println(ex);
        }
        
    }
    
}
