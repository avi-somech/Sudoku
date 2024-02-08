package finalproject;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;


public class ChessSudoku {
    /* SIZE is the size parameter of the Sudoku puzzle, and N is the square of the size.  For
     * a standard Sudoku puzzle, SIZE is 3 and N is 9.
     */
    public int SIZE, N;

    /* The grid contains all the numbers in the Sudoku puzzle.  Numbers which have
     * not yet been revealed are stored as 0.
     */
    public int grid[][];


    // Field that stores the same Sudoku puzzle solved in all possible ways
    public HashSet<ChessSudoku> solutions = new HashSet<ChessSudoku>();

    //other private fields
    private Map<Integer, HashSet<Integer>> rows = new HashMap<>();
    private Map<Integer, HashSet<Integer>> cols = new HashMap<Integer, HashSet<Integer>>();

    


    /* The solve() method should remove all the unknown characters ('x') in the grid
     * and replace them with the numbers in the correct range that satisfy the constraints
     * of the Sudoku puzzle. If true is provided as input, the method should find finds ALL
     * possible solutions and store them in the field named solutions. */
    public void solve(boolean allSolutions) {
        solve1(allSolutions);

    }

    private boolean solve1(boolean moreSolution) {
        initializeMaps();
        findDefiniteColumnsAndRowsAndBoxesAndOneOption(grid);
        if (solveSudoku(moreSolution)) {
            return true;
        }
        return false;
    }

    private boolean solveSudoku(boolean moreSolution) {
        int[][] board = this.grid;
        int n = this.N;

        int row = -1;
        int col = -1;

        //gets lowest options to use in backtrack
        int[] nextCell = findNextCellTOUse();
        row = nextCell[0];
        col = nextCell[1];

        //this means that there is no next cell i.e. board is complete
        if (row == -1 && col == -1) {
            return true;
        }

        ArrayList options = new ArrayList();
        //you only want to use the options that work in the backtracking algorithm
        for (int i = 1; i <= N; i++) {
            if (isNoConflict(grid, row, col, i)) {
                options.add(i);
            }
        }

        for (int k = 0; k < options.size(); k++) {
            addToBoard(row, col, (int) options.get(k));
            if (this.solveSudoku(moreSolution)) {   //Note: this will be true when there is no more empty cells (function returns)

                //return true;
                if (!moreSolution) { //If we only need one solution, then we are done
                    return true;
                }
                if (moreSolution) { //If we want the multiple solutions
                    ChessSudoku other = new ChessSudoku(SIZE);
                    //copy the current solution into the "other" object
                    for (int i = 0; i < grid.length; i++) {
                        for (int j = 0; j < grid.length; j++) {
                            other.grid[i][j] = this.grid[i][j];
                        }
                    }
                    solutions.add(other); //add this object to the solution set
                    int rowToRemoveFrom = row;
                    int colToRemoveFrom = col;

                    int valuebeingRemoved = board[row][col];
                    removeFromBoard(row, col, valuebeingRemoved); //incorporates back track
                }
            } else {
                // replace it
                int rowToRemoveFrom = row;
                int colToRemoveFrom = col;
                int valuebeingRemoved = board[row][col]; //get the value to take it out of the row and column sets
                removeFromBoard(row, col, valuebeingRemoved);
                //remove from board will make the value of that cell zero so it will be viewed as an empty cell again
            }
        }
        return false;
    }

    private int findNumberOfOptions(int row, int column) {
        int number = 0;

        for (int i = 1; i <= N; i++) {
            if (isNoConflict(grid, row, column, i)) {
                number++;
            }
        }
        return number;
    }

    private void initializeMaps() {
        for (int col = 0; col < grid.length; col++) {
            cols.put(col, new HashSet<Integer>());
        }
        //rows
        for (int i = 0; i < grid.length; i++) {
            rows.put(i, new HashSet<Integer>());
            for (int j = 0; j < grid.length; j++) {
                if (grid[i][j] != 0)
                    rows.get(i).add(grid[i][j]); //add to the rows map
                    cols.get(j).add(grid[i][j]); //add to the columns map
            }
        }
    }

    private int[] findNextCellTOUse() {
        int lowestRow = -1;
        int lowestColumn = -1;
        int lowestNumberOfoptions = -1;
        int currentNumberOfOptions = -1;

        //finds first empty cell
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                //for first empty cell, initialize everything
                if (grid[i][j] == 0 && lowestRow == -1) {
                    lowestRow = i;
                    lowestColumn = j;
                    currentNumberOfOptions = findNumberOfOptions(i, j);
                    lowestNumberOfoptions = currentNumberOfOptions;
                    if (lowestNumberOfoptions == 1) { //once you find anyhting with one option, use that since theres no better case
                                                        //considering that we already dealt with definites: wait but one option means definite...
                                                        //should we not use 2? No. because this is done in the backtracking. So there will be cases of one option
                                                        //so this will help catch issue faster
                                                        //now, technically, could be good to check if size of options is 2 after checking 1 and then 3, etc
                        int[] lowest = {lowestRow, lowestColumn};
                        return lowest;
                    }
                }
                if (grid[i][j] == 0) {
                    currentNumberOfOptions = findNumberOfOptions(i, j);
                    if (currentNumberOfOptions < lowestNumberOfoptions) {
                        lowestNumberOfoptions = currentNumberOfOptions;
                        lowestRow = i;
                        lowestColumn = j;
                        if (lowestNumberOfoptions == 1) {
                            int[] lowest = {lowestRow, lowestColumn};
                            return lowest;
                        }
                    }
                }
            }

        }

        int[] lowest = {lowestRow, lowestColumn};

        return lowest;

    }

    //helper method to check if the assignment we want to assign to a given spot in the board conflicts
    // with numbers in its row, column or box
    private boolean isNoConflict(int[][] board, int row, int col, int num) {
        // Check if the number works in terms of not conflicting with a row
        if (cols.get(col).contains(num)) {
            return false;
        }

        if (rows.get(row).contains(num)) {
            return false;
        }

        // Check if the number works in terms of not conflicting with its box
        //int sqrt = (int) Math.sqrt(board.length);
        int boxRowStart = row - row % SIZE;
        int boxColStart = col - col % SIZE;

        for (int r = boxRowStart; r < boxRowStart + SIZE; r++) {
            for (int d = boxColStart; d < boxColStart + SIZE; d++) {
                if (board[r][d] == num) {
                    return false;
                }
            }
        }
        // if there is no conflict with any of the above 3, then we can return true
        return true;
    }

    private boolean isOnBoard(int row, int col) {
        if (row >= 0 && row < N && col >= 0 && col < N) {
            return true;
        }
        return false;
    }

    private void addToBoard(int row, int col, int num) {
        grid[row][col] = num;
        rows.get(row).add(num);
        cols.get(col).add(num);
    }

    private void removeFromBoard(int row, int col, int num) {
        grid[row][col] = 0;
        rows.get(row).remove(num);
        cols.get(col).remove(num);
    }


    private int onlyOneOption() {
        int counter = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                int currentValue = grid[i][j];
                if (currentValue == 0) {
                    ArrayList check = new ArrayList();
                    for (int k = 1; k <= N; k++) {
                        if (isNoConflict(grid, i, j, k)) {
                            check.add(k);
                            if (check.size() > 1) { //size greater than 1 means more than one option
                                break;
                            }
                        }
                    }
                    if (check.size() == 1) {
                        addToBoard(i, j, (int) check.get(0));
                        counter++;
                    }
                }
            }
        }
        if (counter != 0) {
            onlyOneOption();
        }
        return counter;
    }

    private int findDefinitesRows(int[][] grid) {
        int counter = 0;
        //loop from row 1 til the end
        for (int i = 0; i < N; i++) {
            ArrayList emptyCells = findindexOfEmptyCellsInAGivenRow(i);
            if (!(emptyCells.size() == 0)) { //if row is already full, nothing to do
                for (int j = 1; j <= N; j++) { //loops through the numbers
                    if (!(isInRow(i, j))) { //if the number is already in row, its not an option for that cell
                        ArrayList optionsForNumber = new ArrayList();
                        int k = 0;
                        while (optionsForNumber.size() <= 1) {

                            int indexOfColumnToCheck = (int) emptyCells.get(k);
                            if (isNoConflict(grid, i, indexOfColumnToCheck, j)) {
                                optionsForNumber.add(indexOfColumnToCheck);
                            }
                            if (k == emptyCells.size() - 1) { //k checks each index in empty cells
                                //once k is the size -1 that means it already checked all indices so we break
                                break;
                            }
                            k++;
                        }
                        //k loop is done
                        if (optionsForNumber.size() == 1) {
                            addToBoard(i, (int) optionsForNumber.get(0), j);
                            counter++; //counter increases everytime we put something in the board
                            for (int a = 0; a < emptyCells.size(); a++) {
                                if ((int) (emptyCells.get(a)) == (int) optionsForNumber.get(0)) {
                                    emptyCells.remove(a); //Since this index was filled, it need to be removed from empty list
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        return counter;
    }


    private int findDefinitesColumns(int[][] grid) {
        int counter = 0;

        //loop from column 1 til the end
        for (int i = 0; i < N; i++) {

            ArrayList emptyCells = findindexOfEmptyCellsInAGivenColumn(i);
            if (!(emptyCells.size() == 0)) {


                for (int j = 1; j <= N; j++) {

                    if (!(isInColumn(i, j))) { //ex: j =1; 1 is not in the row
                        ArrayList optionsForNumber = new ArrayList();

                        int k = 0;
                        while (optionsForNumber.size() <= 1) {

                            int indexOfRowToCheck = (int) emptyCells.get(k);
                            if (isNoConflict(grid, indexOfRowToCheck, i, j)) {
                                optionsForNumber.add(indexOfRowToCheck);


                            }
                            if (k == emptyCells.size() - 1) {
                                break;
                            }
                            k++;
                        }
                        //k loop is done
                        if (optionsForNumber.size() == 1) {
                            addToBoard((int) optionsForNumber.get(0), i, j);
                            counter++;
                            for (int a = 0; a < emptyCells.size(); a++) {
                                if ((int) (emptyCells.get(a)) == (int) optionsForNumber.get(0)) {
                                    emptyCells.remove(a);
                                    break;
                                }
                            }
                        }


                    }


                }
            }
        }
        return counter;
    }

    private int findDefinitesBoxes(int[][] grid) {
        int counter = 0;
        for (int i = 0; i < SIZE * 2 + 1; i = i + SIZE) {
            for (int j = 0; j < SIZE * 2 + 1; j = j + SIZE) {

                ArrayList box = new ArrayList();
                ArrayList rowPos = new ArrayList();
                ArrayList colPos = new ArrayList();


                //i =0, j=0
                for (int k = 0; k < SIZE; k++) {
                    for (int a = 0; a < SIZE; a++) {
                        box.add(grid[i + k][j + a]); //the number at this spot of the box
                        rowPos.add(i + k);           //the index of the row
                        colPos.add(j + a);           //the index of the column

                        if (box.size() == N) {
                            counter = counter + findDefiniteBoxpart2(box, rowPos, colPos, grid);
                        }
                    }
                }
            }
        }
        if (counter != 0) {
            findDefinitesBoxes(grid);
        }
        return counter;
    }

    private int findDefiniteBoxpart2(ArrayList box, ArrayList row, ArrayList col, int[][] grid) {
        int counter = 0;
        ArrayList emptyCells = new ArrayList();
        for (int i = 0; i < N; i++) {   //this will add the times where box has an empty cell
            if ((int) (box.get(i)) == 0) {
                emptyCells.add(i);      //i refers to the position in the array
                //note: the 3 arrays box, row and col are aligned. so a given number has the same index
                //in all of these arrays
            }
        }

        if (!(emptyCells.size() == 0)) {
            for (int j = 1; j <= N; j++) { //loop through all numbers to see how many options each has

                if (!(isInBox(box, j))) { //only go through numbers not already in the box
                    ArrayList optionsForNumber = new ArrayList();

                    int k = 0;
                    while (optionsForNumber.size() <= 1) { //if size is greater than 1, then you know there is more than one option

                        int indexOfPos = (int) emptyCells.get(k);
                        if (isNoConflict(grid, (int) (row.get(indexOfPos)), (int) (col.get(indexOfPos)), j)) {
                            optionsForNumber.add(indexOfPos);
                        }
                        if (k == emptyCells.size() - 1) { //when there are equal, that means you checked all empty indices to see if number could fit there
                            break;
                        }
                        k++;
                    }
                    //k loop is done
                    if (optionsForNumber.size() == 1) {
                        addToBoard((int) (row.get((int) optionsForNumber.get(0))), (int) (col.get((int) optionsForNumber.get(0))), j);
                        counter++;
                        for (int a = 0; a < emptyCells.size(); a++) {
                            if ((int) (emptyCells.get(a)) == (int) optionsForNumber.get(0)) {
                                emptyCells.remove(a);
                                break;
                            }
                        }
                    }
                }
            }
        }
        return counter;
    }

    private void findDefiniteColumnsAndRowsAndBoxesAndOneOption(int[][] grid) {

        int counterOfRows = findDefinitesRows(grid);
        int counterOfColumns = findDefinitesColumns(grid);
        int counterofBoxes = findDefinitesBoxes(grid);
        int counterOfOneOption = onlyOneOption();

        int sumOfCounters = counterOfRows + counterOfColumns + counterofBoxes + counterOfOneOption;

        if (sumOfCounters != 0) {
            findDefiniteColumnsAndRowsAndBoxesAndOneOption(grid);
        }

    }

    private ArrayList findindexOfEmptyCellsInAGivenRow(int row) {
        ArrayList emptyCellsInRow = new ArrayList();

        for (int j = 0; j < N; j++) {
            if (grid[row][j] == 0) {
                emptyCellsInRow.add(j);
            }
        }
        return emptyCellsInRow;
    }

    private ArrayList findindexOfEmptyCellsInAGivenColumn(int column) {
        ArrayList emptyCells = new ArrayList();

        for (int j = 0; j < N; j++) {
            if (grid[j][column] == 0) {
                emptyCells.add(j);
            }
        }
        return emptyCells;
    }

    private boolean isInRow(int row, int valueToCheckFor) {
        if (rows.get(row).contains(valueToCheckFor)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isInColumn(int column, int valueToCheckFor) {
        if (cols.get(column).contains(valueToCheckFor)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isInBox(ArrayList box, int valueToCheckFor) {
        for (int i = 0; i < N; i++) {
            if ((int) (box.get(i)) == valueToCheckFor) {
                return true;
            }
        }
        return false;
    }

    /* Default constructor.  This will initialize all positions to the default 0
     * value.  Use the read() function to load the Sudoku puzzle from a file or
     * the standard input. */
    //the grid is filled with zeros initially
    public ChessSudoku(int size) {
        SIZE = size;
        N = size * size;

        grid = new int[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                grid[i][j] = 0;
    }


    /* readInteger is a helper function for the reading of the input file.  It reads
     * words until it finds one that represents an integer. For convenience, it will also
     * recognize the string "x" as equivalent to "0". */
    static int readInteger(InputStream in) throws Exception {
        int result = 0;
        boolean success = false;

        while (!success) {
            String word = readWord(in);

            try {
                result = Integer.parseInt(word);
                success = true;
            } catch (Exception e) {
                // Convert 'x' words into 0's
                if (word.compareTo("x") == 0) {
                    result = 0;
                    success = true;
                }
                // Ignore all other words that are not integers
            }
        }

        return result;
    }


    /* readWord is a helper function that reads a word separated by white space. */
    static String readWord(InputStream in) throws Exception {
        StringBuffer result = new StringBuffer();
        int currentChar = in.read();
        String whiteSpace = " \t\r\n";
        // Ignore any leading white space
        while (whiteSpace.indexOf(currentChar) > -1) {
            currentChar = in.read();
        }

        // Read all characters until you reach white space
        while (whiteSpace.indexOf(currentChar) == -1) {
            result.append((char) currentChar);
            currentChar = in.read();
        }
        return result.toString();
    }


    /* This function reads a Sudoku puzzle from the input stream in.  The Sudoku
     * grid is filled in one row at at time, from left to right.  All non-valid
     * characters are ignored by this function and may be used in the Sudoku file
     * to increase its legibility. */
    public void read(InputStream in) throws Exception {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                grid[i][j] = readInteger(in);
            }
        }
    }


    /* Helper function for the printing of Sudoku puzzle.  This function will print
     * out text, preceded by enough ' ' characters to make sure that the printint out
     * takes at least width characters.  */
    void printFixedWidth(String text, int width) {
        for (int i = 0; i < width - text.length(); i++)
            System.out.print(" ");
        System.out.print(text);
    }


    /* The print() function outputs the Sudoku grid to the standard output, using
     * a bit of extra formatting to make the result clearly readable. */
    public void print() {
        // Compute the number of digits necessary to print out each number in the Sudoku puzzle
        int digits = (int) Math.floor(Math.log(N) / Math.log(10)) + 1;

        // Create a dashed line to separate the boxes
        int lineLength = (digits + 1) * N + 2 * SIZE - 3;
        StringBuffer line = new StringBuffer();
        for (int lineInit = 0; lineInit < lineLength; lineInit++)
            line.append('-');

        // Go through the grid, printing out its values separated by spaces
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                printFixedWidth(String.valueOf(grid[i][j]), digits);
                // Print the vertical lines between boxes
                if ((j < N - 1) && ((j + 1) % SIZE == 0))
                    System.out.print(" |");
                System.out.print(" ");
            }
            System.out.println();

            // Print the horizontal line between boxes
            if ((i < N - 1) && ((i + 1) % SIZE == 0))
                System.out.println(line.toString());
        }
    }


    /* The main function reads in a Sudoku puzzle from the standard input,
     * unless a file name is provided as a run-time argument, in which case the
     * Sudoku puzzle is loaded from that file.  It then solves the puzzle, and
     * outputs the completed puzzle to the standard output. */
    public static void main(String args[]) throws Exception {
        InputStream in = new FileInputStream("../../sudoku_boards/hard3x3.txt");

        // The first number in all Sudoku files must represent the size of the puzzle.  See
        // the example files for the file format.
        int puzzleSize = readInteger(in);
        if (puzzleSize > 100 || puzzleSize < 1) {
            System.out.println("Error: The Sudoku puzzle size must be between 1 and 100.");
            System.exit(-1);
        }

        ChessSudoku s = new ChessSudoku(puzzleSize);

        // read the rest of the Sudoku puzzle
        s.read(in);

        System.out.println("Before the solve:");
        s.print();
        System.out.println();

        // Solve the puzzle by finding one solution.

        Date date1 = new Date();
        long timeMilli1 = date1.getTime();


        s.solve(true);

        // Print out the (hopefully completed!) puzzle
        System.out.println("After the solve:");
        s.print();

        if ( s.solutions.size() > 1){
            System.out.println("number of solutions is " + s.solutions.size());
        }

        Date date2 = new Date();
        long timeMilliDif = date2.getTime() - timeMilli1;
        System.out.println("seconds taken: " + timeMilliDif/1000.0);

    }
}
