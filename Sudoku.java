/*
 * Group project  :- The program that can display soduku solution.
 * Group Members:-
 *             1.Bharat Kumar(1501CH10)
 *             2.Saurabh Singh(1501CH14)
 */


import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.border.Border;
import java.util.Objects;

public class Sudoku {
	 
	public static void main(String[] args) {
	    	new SudokuFrame();
	        }
   
    private final int[][] matrix = new int[9][9];

    public Sudoku(){
    }
    
    public Sudoku(Sudoku sudoku) {

        for (int y = 0; y < 9; ++y) {
            this.matrix[y] = sudoku.matrix[y].clone();
        }
    }

    public int get(int x, int y) {
        return matrix[y][x];
    }

    public void set(int x, int y, int value) {
        matrix[y][x] = value;
    }
}


final class IntSet {

    private final boolean[] table;

    IntSet(int sudokuDimension) {
        this.table = new boolean[sudokuDimension];
    }

    void add(int index) {
        table[index] = true;
    }

    boolean contains(int index) {
        return table[index];
    }

    void remove(int index) {
        table[index] = false;
    }

    void clear() {
        for (int i = 0; i < table.length; ++i) {
            table[i] = false;
        }
    }
}



 class SudokuFrame {

    private final JFrame frame = new JFrame("Sudoku solver");
    private SudokuGrid grid;

    public SudokuFrame() {
        frame.getContentPane().add(grid = new SudokuGrid());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(false);
        centerView();
        frame.setVisible(true);
    }
	private void centerView() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = frame.getSize();

        frame.setLocation((screen.width - frameSize.width) /2,
                          (screen.height - frameSize.height) /2);
    }
}
 

 
 
 final class SudokuGrid extends JPanel {

     private static final Font FONT = new Font("Verdana", Font.CENTER_BASELINE, 20);
     private final JTextField[][] grid;
     
     
     private final int dimension=9;
     private final JPanel gridPanel;
     private final JPanel buttonPanel;
     private final JButton solveButton;
     private final JButton clearButton;
     private final JPanel[][] minisquarePanels;

     SudokuGrid() {
         this.grid = new JTextField[9][9];
         for (int y = 0; y < 9; ++y) {
             for (int x = 0; x < 9; ++x) {
                 JTextField field = new JTextField();
                 grid[y][x] = field;
             }
         }

         this.gridPanel   = new JPanel();
         this.buttonPanel = new JPanel();

         Border border = BorderFactory.createLineBorder(Color.BLACK, 1);
         Dimension fieldDimension = new Dimension(30, 30);
          for (int y = 0; y < dimension; ++y) {
             for (int x = 0; x < dimension; ++x) {
                 JTextField field = grid[y][x];
                 field.setBorder(border);
                 field.setFont(FONT);
                 field.setHorizontalAlignment(JTextField.CENTER);
                 field.setPreferredSize(fieldDimension);
             }
         }

         int minisquareDimension = (int) Math.sqrt(dimension);
         this.gridPanel.setLayout(new GridLayout(minisquareDimension,
                                                 minisquareDimension));

         this.minisquarePanels = new JPanel[minisquareDimension]
                                           [minisquareDimension];

         Border minisquareBorder = BorderFactory.createLineBorder(Color.BLACK, 1);

         for (int y = 0; y < minisquareDimension; ++y) {
             for (int x = 0; x < minisquareDimension; ++x) {
                 JPanel panel = new JPanel();
                 panel.setLayout(new GridLayout(minisquareDimension,
                                                minisquareDimension));
                 panel.setBorder(minisquareBorder);
                 minisquarePanels[y][x] = panel;
                 gridPanel.add(panel);
             }
         }

         for (int y = 0; y < dimension; ++y) {
             for (int x = 0; x < dimension; ++x) {
                 int minisquareX = x / minisquareDimension;
                 int minisquareY = y / minisquareDimension;

                 minisquarePanels[minisquareY][minisquareX].add(grid[y][x]);
             }
         }

         this.gridPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 
                                                                 2));
         this.clearButton = new JButton("Clear");
         this.solveButton = new JButton("Solve");

         this.buttonPanel.setLayout(new BorderLayout());
         this.buttonPanel.add(clearButton, BorderLayout.WEST);
         this.buttonPanel.add(solveButton, BorderLayout.EAST);

         this.setLayout(new BorderLayout());
         this.add(gridPanel,   BorderLayout.NORTH);
         this.add(buttonPanel, BorderLayout.SOUTH);  

         clearButton.addActionListener((ActionEvent e) -> {
             clearAll();
         });

         solveButton.addActionListener((ActionEvent e) -> {
             solve();
         });
     }
   void clearAll() {
         for (JTextField[] row : grid) {
             for (JTextField field : row) {
                 field.setText("");
             }
         }
     }

     void solve() {
         Sudoku sudoku = new Sudoku();

         for (int y = 0; y < 9; ++y) {
             for (int x = 0; x < 9; ++x) {
                 String text = grid[y][x].getText();

                 int number = -1;

                 if(text.matches("[1-9]")){
                     number = Integer.parseInt(text.trim());
                 }else if(text.equals("")){
                 	
                 }
                 else {
                 	JOptionPane.showMessageDialog(null , "Wrong Input" , "ERROR" , JOptionPane.ERROR_MESSAGE);
                 }

                 sudoku.set(x, y, number);
             }
         }

         try {
             Sudoku solution = new SudokuSolver().solve(sudoku);
             String skip = " ";

             for (int y = 0; y < 9; ++y) {
                 for (int x = 0; x < 9; ++x) {
                     grid[y][x].setText(skip + solution.get(x, y));
                 }
             }  
         }catch (Exception ex) {
             JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
         }
     }
 }



 class SudokuSolver {

     private static final int UNUSED = 0;
     private final int dimension=9;
     private final int minisquareDimension=3;
     private Sudoku input;
     private final IntSet[] rowSetArray;
     private final IntSet[] columnSetArray;
     private final IntSet[][] minisquareSetMatrix;
     private final Point point = new Point();
     private Sudoku solution;

     public SudokuSolver() {

         rowSetArray    = new IntSet[dimension];
         columnSetArray = new IntSet[dimension];

         minisquareSetMatrix = new IntSet[minisquareDimension]
                                         [minisquareDimension];

         for (int i = 0; i < dimension; ++i) {
             rowSetArray   [i] = new IntSet(dimension + 1);
             columnSetArray[i] = new IntSet(dimension + 1);
         }

        for (int y = 0; y < minisquareDimension; ++y) {
             for (int x = 0; x < minisquareDimension; ++x) {
                 minisquareSetMatrix[y][x] = new IntSet(dimension + 1);
             }
         }
     }

     public Sudoku solve(Sudoku input) {
         Objects.requireNonNull(input, "The input sudoku is null.");
         this.input = new Sudoku(input);
         fixInputSudoku();
         clearSets();
         tryInitializeSets();
         solution = new Sudoku();
         solve();
         return solution;
     }

     private void fixInputSudoku() {

         for (int y = 0; y < 9; ++y) {
             for (int x = 0; x < 9; ++x) {
                 int currentValue = input.get(x, y);

                 if (currentValue < 1 || currentValue > 9) {
                     input.set(x, y, UNUSED);
                 }
             }
         }
     }

     private void clearSets() {
         for (int i = 0; i < 9; ++i) {
             rowSetArray   [i].clear();
             columnSetArray[i].clear();
         }

         for (int y = 0; y < minisquareDimension; ++y) {
             for (int x = 0; x < minisquareDimension; ++x) {
                 minisquareSetMatrix[y][x].clear();
             }
         }
     }

     private void tryInitializeSets() {
         for (int y = 0; y < dimension; ++y) {
             for (int x = 0; x < dimension; ++x) {
                 int currentValue = input.get(x, y);

                 if (rowSetArray[y].contains(currentValue)) {
                     throw new IllegalArgumentException(
                         "The cell (x = " + x + ", y = " + y + ") with " +
                         "value " + currentValue + 
                         " is a duplicate in its row.");
                 }

                 if (columnSetArray[x].contains(currentValue)) {
                     throw new IllegalArgumentException(
                         "The cell (x = " + x + ", y = " + y + ") with " +
                         "value " + currentValue + 
                         " is a duplicate in its column.");
                 }

                 loadMinisquareCoordinates(x, y);

                 if (minisquareSetMatrix[point.y][point.x].contains(currentValue)) {
                     throw new IllegalArgumentException(
                         "The cell (x = " + x + ", y = " + y + ") with " +
                         "value " + currentValue + 
                         " is a duplicate in its minisquare.");
                 }

                 if (isValidCellValue(currentValue)) {
                     rowSetArray   [y].add(currentValue);
                     columnSetArray[x].add(currentValue);
                     // This call saves the result in the field 'point'.
                     minisquareSetMatrix[point.y][point.x].add(currentValue);
                 }
             }
         }
     }

     private boolean isValidCellValue(int value) {
         return 0 < value && value <= dimension;
     }

     private void loadMinisquareCoordinates(int x, int y) {
         point.x = x / minisquareDimension;
         point.y = y / minisquareDimension;
     }

     private void solve() {
         solve(0, 0);
     }

     private boolean solve(int x, int y) {
         if (x == dimension) {
             // "Carriage return": we are done with row 'y', so move to the row
             // 'y + 1' and set 'x' to zero.
             x = 0;
             ++y;
         }

         if (y == dimension) {
             // We have found a solution, signal success by return 'true'.
             return true;
         }

         if (input.get(x, y) != UNUSED) {
             // Just load a predefined value from the input matrix to solution,
             // and proceed further.
             solution.set(x, y, input.get(x, y));
             return solve(x + 1, y);
         } 

             // Find least number fitting in the current cell (x, y).
         for (int i = 1; i <= dimension; ++i) {
             if (!columnSetArray[x].contains(i)
                     && !rowSetArray[y].contains(i)) {
                 loadMinisquareCoordinates(x, y);

                 if (!minisquareSetMatrix[point.y][point.x].contains(i)) {
                     solution.set(x, y, i);
                     rowSetArray   [y].add(i);
                     columnSetArray[x].add(i);
                     minisquareSetMatrix[point.y][point.x].add(i);

                     if (solve(x + 1, y)) {
                         // A solution found; stop backtracking by returning
                         // at each recursion level.
                         return true;
                     }

                     // Setting 'i' at current cell (x, y) did not lead towards
                     // solution; remove from the sets and try larger value 
                     // for 'i' in the next iteration.
                     rowSetArray   [y].remove(i);
                     columnSetArray[x].remove(i);

                     // Reload the minisquare coordinates as they are likely to
                     // be invalid due to recursion.
                     loadMinisquareCoordinates(x, y);
                     minisquareSetMatrix[point.y][point.x].remove(i);
                 }
             }
         }

         // No number fits at this (x, y), backtrack a little.
         return false;
     }
 }