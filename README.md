# Chess Sudoku Solver

## Introduction
Chess Sudoku is a variation of traditional Sudoku that incorporates chess-related constraints into the puzzle. In addition to the standard Sudoku rules, where each row, column, and block must contain all the numbers from 1 to 9 (or 1 to N in N x N puzzles), Chess Sudoku introduces additional rules based on how chess pieces move. For example, a number might represent a knight, and it must be placed in the grid so that no other identical number is within a knight's move away from it, mimicking the L-shaped movement of a knight in chess. This adds an extra layer of complexity and challenge to the puzzle.

## Features
- **Comprehensive Output**: Upon completion, the solver prints the initial puzzle, the solved board, and the total time taken to solve.
- **Multiple Solutions Detection**: The solver identifies if multiple solutions exist for a given puzzle and provides all correct solutions when there are multiple ones. 
- **Flexible Puzzle Sizes**: This solver is not limited to the traditional 9x9 Sudoku grid. It is designed to handle puzzles of arbitrary sizes, including larger 16x16 and 25x25 grids.
- **Optimized Performance**: Employing a backtracking algorithm, the solver is further optimized for speed and efficiency by first determining definite numbers using classical Sudoku rules, thus reducing the solution space for the backtracking process.

## Getting Started
To start, navigate to the src-chess_sudoku directory and run ChessSudoku.java. For different puzzles, select from the sudoku_boards folder or add your own, ensuring the format matches. To change the puzzle, adjust the input stream in the ChessSudoku class's main method to the desired file.