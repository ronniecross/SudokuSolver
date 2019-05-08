/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sudoku_solver.Logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *
 * @author cross
 */
public class GroupContainer {

    int position;
    HashSet<Coordinate> coords;
    HashSet<Number> numbers;

    protected GroupContainer(int position) {
        this.position = position;
        coords = new HashSet<>();
    }

    /**
     * Checks the Coordinates linked to this container for the specified number.
     * If the number is found, returns true. Otherwise returns false.
     *
     * @param number The number to look for.
     * @return True if the number is found, otherwise false.
     */
    protected boolean hasNumber(Number number) {
        // Declare the result as false
        boolean result = false;
        // Iterate over each coordinate in this container.
        Iterator<Coordinate> i = coords.iterator();
        // If a coordinate contains this number,
        // set the result to true.
        while (i.hasNext()) {
            if (i.next().number == number) {
                result = true;
            }
        }
        // Return the result.
        return result;
    }

    /**
     * Returns the number of coordinates linked to this container which have not
     * been assigned a number.
     *
     * @return The number of coordinates not assigned a number.
     */
    protected int countBlank() {
        // Declare a counter.
        int counter = 0;
        // Iterate over each coordinate.
        Iterator<Coordinate> i = coords.iterator();
        // If the corrent coordinate does not have a number assigned,
        // increment the counter by one. 
        while (i.hasNext()) {
            if (i.next().number == null) {
                counter++;
            }
        }
        // return the counter
        return counter;
    }

    /**
     * Returns a collection containing all linked coordinates that do not have a
     * linked number.
     *
     * @return A collection of unassigned coordinates.
     */
    protected HashSet<Coordinate> getBlankCoords() {
        // Declare a result set
        HashSet<Coordinate> results = new HashSet<>();
        // Iterate over each linked coordinate
        Iterator<Coordinate> i = coords.iterator();
        // For each coordinate, if it does not have a linked number,
        // add it to the result set.
        while (i.hasNext()) {
            Coordinate coord = i.next();
            if (coord.number == null) {
                results.add(coord);
            }
        }
        // Return the result set. 
        return results;
    }
    
    protected HashSet<Coordinate> getSatisfiedCoords() {
        HashSet<Coordinate> results = new HashSet<>();
        Iterator<Coordinate> i = coords.iterator();
        while (i.hasNext()) {
            Coordinate coord = i.next();
            if (coord.number != null) {
                results.add(coord);
            }
        }
        return results;
    }

    /**
     * Returns a collection of Number objects that have not been assigned to a
     * coordinate within this container.
     *
     * @return a collection of Number objects.
     */
    protected HashSet<Number> getRemainingNumbers(HashSet<Number> allNumbers) {
        // Declare and initialize an empty collection.
        HashSet<Number> numbers = new HashSet<>();
        // Iterate over each number
        Iterator<Number> iNum = allNumbers.iterator();
        // For each number,
        while (iNum.hasNext()) {
            Number number = iNum.next();
            // Iterate over each co-ordinate in this container.
            Iterator<Coordinate> iCoord = coords.iterator();
            // Declare a boolean 'found' tracker, and initialize it to false.
            Boolean found = false;
            // The iteration should terminate if found = true.
            while (iCoord.hasNext() && !found) {
                Coordinate coord = iCoord.next();
                // If the current number is assigned to the current coordinate,
                if (coord.number == number) {
                    // Set 'found' to true.
                    found = true;
                }
            }
            // If the current number is not found in a co-ordinate,
            if (!found) {
                // Add the number to the collection.
                numbers.add(number);
            }
        }
        // Return the collection.
        return numbers;
    }

    /**
     * Gets all remaining numbers asside from the given number, and all
     * remaining coordinates asside from the given coordinate, and simulates
     * all combinations of those numbers in those coordinates. If exactly
     * one simulation deems it possible for all number/coordinate combinations,
     * returns true. Otherwise returns false. 
     */
    protected boolean solveThroughSimulation(HashSet<Number> numbers, Coordinate coord, Number number) {
        // Will only proceed if the number of remaining numbers is fewer than 5.
        if (!(this.getRemainingNumbers(numbers).size() < 7)) {
            return false;
        }
        // Perform a pre-simulation check to assure that the given number
        // is definitely possible in the given coordinate. 
        if (!coord.possible(number)) {
            return false;
        }
        
        
        // Get remaining numbers and format as a List, then remove current number from the list.
        List<Number> nums = new ArrayList<>(getRemainingNumbers(numbers));
        nums.remove(number);
        
        // Get blank coordinates in a sorted list, then remove current coord from the list.
        List<Coordinate> coordList = new ArrayList<>(getBlankCoords());
        coordList.remove(coord);
        
        // Get a list of all possible number sequences
        TreeMap<Integer,TreeMap<Integer,Number>> sequences = new TreeMap<>();
        getPossibleSequences(nums,sequences);
        
        // Count the number of 'perfect' sequences
        int perfectSequences = 0;
        
        // Iterate over each sequence
        Set<Integer> seqsKS = sequences.keySet();
        
        //////////////////// DEBUGGING ON       
        if (Puzzle.debug) {
                    System.out.println("-----------------------------------------");
        System.out.println("Satisfied coordinates: ");
        Iterator<Coordinate> it = getSatisfiedCoords().iterator();
        while(it.hasNext()) {
            Coordinate c = it.next();
            System.out.println("(" + c.row.position + ", " + c.column.position +
                    " == " + c.number.number + ")");
        }
        }
        //////////////////// DEBUGGING OFF    
        
        for(int i : seqsKS) {
            // Get a map containing each sequence, where the key contains the
            // sequential order, and the value contains the number in that position.
            TreeMap<Integer, Number> sequence = sequences.get(i);
            
            // Count the number of possible sequences
            int numPossible = 0;
            
            // Iterate over the sequence
            Set<Integer> seqKS = sequence.keySet();
            
            //////////////////// DEBUGGING ON
            if (Puzzle.debug) {
                            // Debugging
            System.out.println("");
            System.out.print("Sequence: ");
            for(int k : seqKS) {
                System.out.print(sequence.get(k).number);
            }
            System.out.println();
            System.out.println("------------------");
            }
            //////////////////// DEBUGGING OFF
            
            for(int k : seqKS) {
                // Get the current number
                Number n = sequence.get(k);
                // Get the current coordinate
                Coordinate c = coordList.get((k-1));
//                try {
//                    c = ;
//                } catch (Exception ex) {
//                    System.out.println(ex);
//                }
                boolean possible = c.possible(n);
                // If this is possible, increment numPossible
                if(possible) {
                    numPossible++;
                    //System.out.println(n.number + " possible.");
                } else {
                    //System.out.println(n.number + " not possible.");
                }
            }
            //////////////////// DEBUGGING ON
            if (Puzzle.debug) {
                            String posString = "";
            if(seqKS.size() == numPossible) {
                posString = "POSSIBLE";
            } else {posString = "NOT POSSIBLE";}
            System.out.println("=== Sequence: " + posString);
            }
            //////////////////// DEBUGGING OFF
            
            
            // If numPossible is equal to the size of nums, increment perfectSequences
            if (numPossible == nums.size()) {
                perfectSequences++;
            }
        }
        if (coord.row.position == 1 && coord.row.position == 1) {
            System.out.println("");
        }
        return perfectSequences == 1;
    }



    
    /**
     * Given a list of Number objects and a collection to inject the results,
     * gets a list of all possible sequences of the given numbers where all
     * numbers occur exactly once. 
     * @param numbers The numbers in the list of possible sequences. 
     * @param result The collection to inject the results into. 
     * @return 
     */
    protected static TreeMap<Integer,TreeMap<Integer,Number>> 
        getPossibleSequences(List<Number> numbers, 
            TreeMap<Integer,TreeMap<Integer,Number>> result) {
            return getSeqs(numbers, result, new TreeMap<>(), 1, new Number(-1));
        }
    /**
     * This test method exists to try and figure out a way of embedding X number
     * of iterations within iterations.
     *
     * @return
     */
    private static TreeMap<Integer,TreeMap<Integer,Number>> 
        getSeqs(List<Number> numbers, 
            TreeMap<Integer,TreeMap<Integer,Number>> result, 
            TreeMap<Integer,Number> resultSet, int currentKey, Number lastNumber) {
        // Only iterate if there are still numbers remaining
        if (numbers.size() > 0) {
            // Iterate over every number in the 'numbers' collection
            for (Number number : numbers) {
                // Create a 'numbers' copy without 'number'
                List<Number> numsToParse = new ArrayList<>();
                numbers.stream().filter((n) -> (number != n)).forEachOrdered((n) -> {
                    numsToParse.add(n);});
                // Add 'number' to result set, then increment the key
                resultSet.put(resultSet.size()+1, number);
                currentKey++;
                // Recursively call this method
                getSeqs(numsToParse, result, resultSet, currentKey, number);
                // Remove 'number' from result set.
                Set<Integer> rsKeys = resultSet.keySet();
                rsKeys.forEach((i) -> {
                    Number n = resultSet.get(i);
                    if (n == number) {
                        resultSet.remove(i);
                    }
                });
            }
        } else {
            // Add resultSet to results
            result.put(result.size() +1, new TreeMap<>(resultSet));
        }
        return result;
    }
        
        protected Number trySim(Coordinate coord) {
            // This method will get all possible sequences in this container,
            // and for each sequence, will verify that every number in a given
            // position is possible in the corresponding coordinate in the
            // same position within the container. 
            // If all numbers in a sequence are possible, the sequence is stored
            // in memory, and along with that, the number of sequences where
            // all numbers are possible.
            // If, at the end, there is exactly one possible sequence, the number
            // within that sequence at the position corresponding to this position
            // of the given coordinate is returned. 
            
            // Declarations
            HashSet<Number> numbers = new HashSet<>();
            HashSet<Coordinate> coordsSet = getBlankCoords();
            TreeMap<Integer, Number> perfectSequence = new TreeMap<>();
            int perfectSequenceCount = 0;
            Number result = null;
            
            // Get remaining numbers
            HashSet<Number> allNumbers = new HashSet<>();
            Set<Integer> numKeys = Puzzle.numbers.keySet();
            for(int i : numKeys) {
                allNumbers.add(Puzzle.numbers.get(i));
            }
            numbers = getRemainingNumbers(allNumbers);
            
            // Get possible sequences
            List<Number> numArray = new ArrayList<>();
            for(Number n : numbers) {numArray.add(n);}
            TreeMap<Integer, TreeMap<Integer, Number>> sequences = new TreeMap<>();
            getPossibleSequences(numArray, sequences);
            
            // Convert coords to a HashMap
            HashMap<Integer, Coordinate> coords = new HashMap<>();
            int coordKey = 1;
            for(Coordinate c : coordsSet) {
                coords.put(coordKey, c);
                coordKey++;
            }
            
            // Iterate over each possible sequence
            Set<Integer> sequenceKeys = sequences.keySet();
            for(int key : sequenceKeys) {
                // Get the active sequence
                TreeMap<Integer, Number> sequence = sequences.get(key);
                // Boolean to change to false if no longer perfect
                Boolean isPerfect = true;
                // Iterate over each coordinate
                Set<Integer> coordKeys = coords.keySet();
                for (int i : coordKeys) {
                    // If it is not possible for this number to go into this coord
                    Coordinate c = coords.get(i);
                    Number n = sequence.get(i);
                    int Row = c.row.position;
                    int col = c.column.position;
                    if(!c.possible(n)) {
                        isPerfect = false;
                    }
                }
                // If this is a perfect sequence, increment perfectSequenceCount
                // and assign perfectSequence
                if(isPerfect) {
                    perfectSequenceCount++;
                    perfectSequence = sequence;
                }
            }
            if(perfectSequenceCount >= 1) {
                System.out.println("Position: " + this.position + " "
                        + perfectSequenceCount + " perfect sequences.");
                System.out.println();
            }
            // If there is only one perfect sequence, return the number at the 
            // position of coord within that sequence. Otherwise return null.
            if(perfectSequenceCount == 1) {
                
                Set<Integer> coordKeys = coords.keySet();
                for (int i : coordKeys) {
                    if (coords.get(i) == coord) {
                        result = perfectSequence.get(i);
                    } 
                }
            }
            return result;
        }
}
