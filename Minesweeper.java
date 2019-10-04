import java.util.ArrayList;

import javalib.impworld.*;

import java.util.Random;

import java.awt.Color;

import javalib.worldimages.*;

// Represents a game of Minesweeper
class Minesweeper extends World {

  // Number of horizontal cells
  public static final int COLUMNS = 8;
  // Number of vertical cells
  public static final int ROWS = 8;
  // Size of the cells when drawn in pixels
  public static final int CELL_SIZE = 40;
  // Pixel width of the game
  public static final int GAME_WIDTH = COLUMNS * CELL_SIZE;
  // Pixel height of the game
  public static final int GAME_HEIGHT = ROWS * CELL_SIZE;
  // Number of mines in the game. Increase to add difficulty
  public static final int NUM_MINES = 10;
  // Random object to used in the randomization of mine placement
  public Random rand = new Random();
  // Board of the game
  ArrayList<ArrayList<Cell>> cells;

  // Default constructor with no parameters
  // for creating the game with a random board of cells
  public Minesweeper(ArrayList<ArrayList<Cell>> board) {

    // Initiate this game's cells with an empty array of arrays of cells
    this.cells = board;
  }

  // Convenience constructor
  public Minesweeper() {
    this(new ArrayList<ArrayList<Cell>>());

    if (COLUMNS * ROWS < NUM_MINES) {
      throw new IllegalArgumentException("There are more mines than cells :(");
    }
    else {

      // Generate the board of cells and random mines
      generateBoard();
      // Updates the neighbors of all cells and updates the neighboring mine count
      updateNeighbors();
    }
  }

  // EFFECT: Generates the board of cells and random mines
  public void generateBoard() {

    // calculates the total number of possible cells with the given horizontal and
    // vertical cells
    int totalCells = COLUMNS * ROWS;

    // Creates an empty array of cells that serves as a starting array
    // for creating all possible cells
    ArrayList<Cell> oneLineArray = new ArrayList<>();
    // initializes a counter for the mines that have been created so far
    int minesRemaining = 0;

    // for every possible cell, add a new cell to the array
    // the first n cells in the array will be created as mines,
    // n is the number of mines in NUM_MINES
    for (int i = 0; i < totalCells; i++) {
      oneLineArray.add(new Cell(minesRemaining < NUM_MINES));
      minesRemaining++;
    }

    // For each column of cells, defined by H_Cells, add the ROWS number of cells to
    // each
    // column and then add these columns to the game's board of cells.
    for (int x = 0; x < COLUMNS; x++) {
      ArrayList<Cell> columnTemp = new ArrayList<>();
      for (int y = 0; y < ROWS; y++) {
        int randomInt = rand.nextInt(oneLineArray.size());
        columnTemp.add(oneLineArray.get(randomInt));
        oneLineArray.remove(randomInt);
      }
      this.cells.add(columnTemp);
    }
  }

  // EFFECT: Updates every cell in the game to count the number of neighboring
  // mines
  public void updateNeighbors() {
    for (int x = 0; x < COLUMNS; x++) {
      for (int y = 0; y < ROWS; y++) {
        this.cells.get(x).get(y).updateNeighbors(this.cells, x, y);
        this.cells.get(x).get(y).updateNeighborMines();
      }
    }
  }

  // Overwritten method for creating a WorldScene of the Minesweeper game
  public WorldScene makeScene() {

    // Empty scene to draw all the cells on
    WorldScene scene = getEmptyScene();

    int x = 0;
    for (ArrayList<Cell> column : this.cells) {
      int y = 0;
      for (Cell cell : column) {
        scene.placeImageXY(cell.draw(), x * CELL_SIZE, y * CELL_SIZE);
        y++;
      }
      x++;
    }

    // Returns the scene with all the cells placed
    return scene;
  }

  // EFFECT: Updates the fields of Cell depending on whether the left or right
  // mouse button was clicked
  public void onMouseClicked(Posn mouse, String buttonName) {
    Cell clickedCell = this.cells.get((int) Math.floor(mouse.x / (CELL_SIZE * 1.0)))
        .get((int) Math.floor(mouse.y / (CELL_SIZE * 1.0)));

    if (buttonName.equals("LeftButton")) {
      if (!clickedCell.flagged) {
        if (clickedCell.mine) {
          this.endOfWorld("YOU LOST!");
        }
        else {
          clickedCell.uncover();
        }
      }
    }
    else if (buttonName.equals("RightButton")) {
      clickedCell.flag();
    }
    checkWin();
  }

  // EFFECT: Determines if all the cells that are not mines if they have been
  // uncovered, and calls endOfWorld method if it holds true
  void checkWin() {
    boolean hasWon = true;
    for (ArrayList<Cell> column : this.cells) {
      for (Cell cell : column) {
        if (cell.flagged && !cell.mine) {
          hasWon = false;
        }
        if (!cell.mine && cell.covered) {
          hasWon = false;
        }
      }
    }
    if (hasWon) {
      this.endOfWorld("You Won!");
    }
  }

  // Draws a WorldScene of the last scene when the game is over
  public WorldScene lastScene(String string) {

    WorldScene scene = makeScene();

    scene.placeImageXY(new TextImage(string, 50, Color.black), GAME_WIDTH / 2, GAME_HEIGHT / 2);
    return scene;
  }
}
