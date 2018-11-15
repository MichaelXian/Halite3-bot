package hlt.GNN;

import java.util.ArrayList;

import static java.util.Collections.swap;

public class InsertionSort {

    public static void sortKeyValue(ArrayList<String> keys, ArrayList<Double> outputValues) {
        for (int i = 0; i < outputValues.size(); i++) { // Insertion sort but we swap both the values and keys
            double val = outputValues.get(i);
            int j = i-1;
            while (j >= 0 && outputValues.get(j) < val) {
                swap(outputValues, i, j);
                swap(keys, i, j);
                j--;
            }
        }
    }
}
