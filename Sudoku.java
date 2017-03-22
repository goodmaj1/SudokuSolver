import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Sudoku {
	private static int boardSize = 0;
	private static int partitionSize = 0;
	private static ArrayList[][] constraints;

	public static void main(String[] args) {
		String filename = args[0];
		File inputFile = new File(filename);
		Scanner input = null;
		int[][] vals = null;
		int temp = 0;
		int count = 0;

		try {
			input = new Scanner(inputFile);
			temp = input.nextInt();
			boardSize = temp;
			partitionSize = (int) Math.sqrt(boardSize);
			System.out.println("Boardsize: " + temp + "x" + temp);
			vals = new int[boardSize][boardSize];
			constraints =  new ArrayList[boardSize][boardSize];
			for (int i = 0; i < boardSize; i++) {
				for (int j = 0; j < boardSize; j++) {
					constraints[i][j] = getConstraints(vals, i, j);
				}
			}

			System.out.println("Input:");
			int i = 0;
			int j = 0;
			while (input.hasNext()) {
				temp = input.nextInt();
				count++;
				System.out.print(temp);
				vals[i][j] = temp;
				j++;
				if (j == boardSize) {
					j = 0;
					i++;
					System.out.println();
				}
				if (j == boardSize) {
					break;
				}
			}
			input.close();
		} catch (FileNotFoundException exception) {
			System.out.println("Input file not found: " + filename);
		} catch (IOException e) {
			System.out.println(e);
		}
		if (count != boardSize * boardSize)
			throw new RuntimeException("Incorrect number of inputs.");

		if (solvePuzzle(vals, 0, 0)) {
			// Output
			String[] splitName = args[0].split("\\.");
			String outfileName = splitName[0] + "Solution.txt";
			File outfile = new File(outfileName);
			try {
				FileWriter w = new FileWriter(outfile);
				System.out.println();
				System.out.println("Output");
				System.out.println();
				for (int i = 0; i < boardSize; i++) {
					for (int j = 0; j < boardSize; j++) {
						w.write(vals[i][j] + " ");
						System.out.printf("%3d", vals[i][j]);
					}
					System.out.println();
					w.write("\n");
				}
				w.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("\nSolver found no solution to the puzzle!");
		}

	}

	public static ArrayList<Integer> getConstraints(int[][] vals, int row, int col) {
		ArrayList<Integer> constraints = new ArrayList<>();
		for (int i = 0; i < boardSize; i++) { // check columns
			if (!constraints.contains(vals[row][i])) {
				constraints.add(vals[row][i]);
			}
		}
		for (int i = 0; i < boardSize; i++) { // check columns
			if (!constraints.contains(vals[i][col])) {
				constraints.add(vals[i][col]);
			}
		}
		int boxRow;
		int boxCol;
		for (int i = 0; i < partitionSize; i++) { // check block
			boxRow = (row / partitionSize) * partitionSize + i;
			for (int j = 0; j < partitionSize; j++) {
				boxCol = (col / partitionSize) * partitionSize + j;
				if (!constraints.contains(vals[boxRow][boxCol]))
					constraints.add(vals[boxRow][boxCol]);
			}
		}
		return constraints;
	}

	public static boolean solvePuzzle(int[][] vals, int row, int col) {
		if (col == boardSize) { // see if the solving is complete
			col = 0;
			row++;
			if (row == boardSize) {
				return true;
			}
		}
		if (vals[row][col] != 0) { // no need to check this val
			solvePuzzle(vals, row, col + 1);
		}
		for (int num = 1; num <= boardSize; num++) { // need to check this value
			if (validMove(num, vals, row, col)) { // check row/column/block for
													// issue
				vals[row][col] = num;
				if (solvePuzzle(vals, row, col + 1)) // check next column
					return true;
			}
		}
		vals[row][col] = 0; // value did not work: need to go back
		return false;
	}

	private static boolean validMove(int num, int[][] vals, int row, int col) {
		if (constraints[row][col].contains(num)){ // see if the num is in the constraints ArrayList for that position
			return false;
		}
		for (int i = 0; i < boardSize; i++) { // check columns
			if (vals[row][i] == num)
				return false;
		}
		for (int i = 0; i < boardSize; i++) { // check rows
			if (vals[i][col] == num)
				return false;
		}
		int boxRow;
		int boxCol;
		for (int i = 0; i < partitionSize; i++) { // check block
			boxRow = (row / partitionSize) * partitionSize + i;
			for (int j = 0; j < partitionSize; j++) {
				boxCol = (col / partitionSize) * partitionSize + j;
				if (vals[boxRow][boxCol] == num)
					return false;
			}
		}
		return true;
	}

}