package com.epam.jmp.tasks;

import java.util.Arrays;
import java.util.concurrent.RecursiveAction;

public class MergeSortTask extends RecursiveAction {

    // Threshold for switching to sequential sorting
    private static final int THRESHOLD = 1000;

    private final int[] array;
    private final int start;
    private final int end;

    public MergeSortTask(int[] array) {
        this(array, 0, array.length);
    }

    public MergeSortTask(int[] array, int start, int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }

    @Override
    protected void compute() {
        if (end - start <= THRESHOLD) {
            Arrays.sort(array, start, end); // Fallback to sequential sort
            return;
        }

        int mid = (start + end) / 2;

        MergeSortTask leftTask = new MergeSortTask(array, start, mid);
        MergeSortTask rightTask = new MergeSortTask(array, mid, end);

        invokeAll(leftTask, rightTask);

        merge(array, start, mid, end);
    }

    private void merge(int[] array, int start, int mid, int end) {
        int[] temp = new int[end - start];
        int i = start, j = mid, k = 0;

        while (i < mid && j < end) {
            if (array[i] <= array[j]) {
                temp[k++] = array[i++];
            } else {
                temp[k++] = array[j++];
            }
        }

        while (i < mid) {
            temp[k++] = array[i++];
        }

        while (j < end) {
            temp[k++] = array[j++];
        }

        System.arraycopy(temp, 0, array, start, temp.length);
    }
}
