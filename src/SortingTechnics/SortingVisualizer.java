package SortingTechnics;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class SortingVisualizer extends JPanel {

    private int[] arr;
    private int highlightedIndex1 = -1;
    private int highlightedIndex2 = -1;

    private volatile boolean isSorting = false; // 🔴 control flag

    public SortingVisualizer() {
        generateArray();
    }

    // Generate random array
    public void generateArray() {
        isSorting = false;
        Random rand = new Random();
        arr = new int[80];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = rand.nextInt(400) + 20;
        }
        repaint();
    }

    // Draw bars
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int width = getWidth() / arr.length;

        for (int i = 0; i < arr.length; i++) {
            if (i == highlightedIndex1 || i == highlightedIndex2)
                g.setColor(Color.RED);
            else
                g.setColor(Color.BLUE);

            g.fillRect(i * width, getHeight() - arr[i], width - 2, arr[i]);
        }
    }

    // Swap helper
    private void swap(int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    // Delay
    private void sleep() {
        try {
            Thread.sleep(30);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Stop sorting
    public void stopSorting() {
        isSorting = false;
        highlightedIndex1 = highlightedIndex2 = -1;
        repaint();
    }

    // ================= BUBBLE SORT =================
    public void bubbleSort() {
        isSorting = true;
        new Thread(() -> {
            for (int i = 0; i < arr.length - 1 && isSorting; i++) {
                for (int j = 0; j < arr.length - i - 1 && isSorting; j++) {
                    highlightedIndex1 = j;
                    highlightedIndex2 = j + 1;

                    if (arr[j] > arr[j + 1])
                        swap(j, j + 1);

                    repaint();
                    sleep();
                }
            }
            stopSorting();
        }).start();
    }

    // ================= SELECTION SORT =================
    public void selectionSort() {
        isSorting = true;
        new Thread(() -> {
            for (int i = 0; i < arr.length && isSorting; i++) {
                int min = i;
                for (int j = i + 1; j < arr.length && isSorting; j++) {
                    highlightedIndex1 = min;
                    highlightedIndex2 = j;

                    if (arr[j] < arr[min])
                        min = j;

                    repaint();
                    sleep();
                }
                swap(i, min);
            }
            stopSorting();
        }).start();
    }

    // ================= INSERTION SORT =================
    public void insertionSort() {
        isSorting = true;
        new Thread(() -> {
            for (int i = 1; i < arr.length && isSorting; i++) {
                int key = arr[i];
                int j = i - 1;

                while (j >= 0 && arr[j] > key && isSorting) {
                    highlightedIndex1 = j;
                    highlightedIndex2 = j + 1;

                    arr[j + 1] = arr[j];
                    j--;

                    repaint();
                    sleep();
                }
                arr[j + 1] = key;
            }
            stopSorting();
        }).start();
    }

    // ================= MERGE SORT =================
    private void merge(int l, int m, int r) {
        if (!isSorting) return;

        int n1 = m - l + 1;
        int n2 = r - m;

        int[] L = new int[n1];
        int[] R = new int[n2];

        System.arraycopy(arr, l, L, 0, n1);
        System.arraycopy(arr, m + 1, R, 0, n2);

        int i = 0, j = 0, k = l;

        while (i < n1 && j < n2 && isSorting) {
            highlightedIndex1 = l + i;
            highlightedIndex2 = m + 1 + j;

            arr[k++] = (L[i] <= R[j]) ? L[i++] : R[j++];
            repaint();
            sleep();
        }

        while (i < n1 && isSorting) {
            arr[k++] = L[i++];
            repaint();
            sleep();
        }

        while (j < n2 && isSorting) {
            arr[k++] = R[j++];
            repaint();
            sleep();
        }
    }

    private void mergeSortUtil(int l, int r) {
        if (l < r && isSorting) {
            int m = (l + r) / 2;
            mergeSortUtil(l, m);
            mergeSortUtil(m + 1, r);
            merge(l, m, r);
        }
    }

    public void mergeSort() {
        isSorting = true;
        new Thread(() -> {
            mergeSortUtil(0, arr.length - 1);
            stopSorting();
        }).start();
    }

    // ================= QUICK SORT =================
    private int partition(int low, int high) {
        int pivot = arr[high];
        int i = low - 1;

        for (int j = low; j < high && isSorting; j++) {
            highlightedIndex1 = j;
            highlightedIndex2 = high;

            if (arr[j] < pivot) {
                i++;
                swap(i, j);
            }
            repaint();
            sleep();
        }
        swap(i + 1, high);
        return i + 1;
    }

    private void quickSortUtil(int low, int high) {
        if (low < high && isSorting) {
            int pi = partition(low, high);
            quickSortUtil(low, pi - 1);
            quickSortUtil(pi + 1, high);
        }
    }

    public void quickSort() {
        isSorting = true;
        new Thread(() -> {
            quickSortUtil(0, arr.length - 1);
            stopSorting();
        }).start();
    }

    // ================= MAIN =================
    public static void main(String[] args) {

        SortingVisualizer panel = new SortingVisualizer();

        JFrame frame = new JFrame("Sorting Visualizer - Java Swing");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 500);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        JPanel controls = new JPanel();

        JButton generateBtn = new JButton("Generate");
        JButton bubbleBtn = new JButton("Bubble");
        JButton selectionBtn = new JButton("Selection");
        JButton insertionBtn = new JButton("Insertion");
        JButton mergeBtn = new JButton("Merge");
        JButton quickBtn = new JButton("Quick");
        JButton stopBtn = new JButton("STOP");

        generateBtn.addActionListener(e -> panel.generateArray());
        bubbleBtn.addActionListener(e -> panel.bubbleSort());
        selectionBtn.addActionListener(e -> panel.selectionSort());
        insertionBtn.addActionListener(e -> panel.insertionSort());
        mergeBtn.addActionListener(e -> panel.mergeSort());
        quickBtn.addActionListener(e -> panel.quickSort());
        stopBtn.addActionListener(e -> panel.stopSorting());

        controls.add(generateBtn);
        controls.add(bubbleBtn);
        controls.add(selectionBtn);
        controls.add(insertionBtn);
        controls.add(mergeBtn);
        controls.add(quickBtn);
        controls.add(stopBtn);

        frame.add(panel, BorderLayout.CENTER);
        frame.add(controls, BorderLayout.SOUTH);
        frame.setVisible(true);
    }
}
