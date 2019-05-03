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
        // Get all remaining numbers and format them as a List.
        List<Number> nums = new ArrayList<>(getRemainingNumbers(numbers));
        // Remove the provided number from nums (that has already been checked!)
        nums.remove(number);
        // Get a list of blank coordinates into a sorted list.
        List<Coordinate> coordList = new ArrayList<>(getBlankCoords());
        // Remove the provided coordinate from coordList (that has already been checked!)
        coordList.remove(coord);
        // Get a list of all possible number sequences
        TreeMap<Integer,TreeMap<Integer,Number>> sequences = new TreeMap<>();
        getPossibleSequences(nums,sequences);
        // Count the number of 'perfect' sequences
        int perfectSequences = 0;
        // Iterate over each sequence
        Set<Integer> seqsKS = sequences.keySet();
        for(int i : seqsKS) {
            TreeMap<Integer, Number> sequence = sequences.get(i);
            // Count the number of possible sequences
            int numPossible = 0;
            // Iverate over the sequence
            Set<Integer> seqKS = sequence.keySet();
            for(int k : seqKS) {
                // Get the current number
                Number n = sequence.get(k);
                // Get the current coordinate
                Coordinate c = null;
                try {
                    c = coordList.get((k-1));
                } catch (Exception ex) {
                    System.out.println(ex);
                }
                
                // If this is possible, increment numPossible
                if(c.possible(n)) {
                    numPossible++;
                }
            }
            // If numPossible is equal to the size of nums, increment perfectSequences
            if (numPossible == nums.size()) {
                perfectSequences++;
            }
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
}
