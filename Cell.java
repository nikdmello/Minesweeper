import java.util.ArrayList;

import java.awt.Color;

import java.util.Arrays;

import javalib.worldimages.*;

// Represents a cell in the minesweeper game that can be a mine or a number or an empty cell
class Cell {
  boolean mine; // determines if this cell has a mine or not
  boolean covered; // determines if this cell has been uncovered
  boolean flagged; // determines if this cell has been flagged
  int neighborMines; // number of neighboring mines
  ArrayList<Cell> neighbors; // keeps track of who is this cell's neighboring cells
  // List of the colors to be used when drawing the number of mines around
  ArrayList<Color> textColors = new ArrayList<Color>(Arrays.asList(Color.blue, Color.green,
      Color.red, Color.magenta, Color.black, new Color(101, 67, 33), Color.gray, Color.cyan));

  // Default constructor
  public Cell(boolean mine, boolean covered, boolean flagged, int neighborMines,
      ArrayList<Cell> neighbors) {
    this.mine = mine;
    this.covered = covered;
    this.flagged = flagged;
    this.neighborMines = neighborMines;
    this.neighbors = neighbors;
  }

  // Convenience constructor that only takes in a boolean value that determines if
  // it is a mine
  public Cell(boolean mine) {
    this(mine, true, false, 0, new ArrayList<>());
  }

  // EFFECT: Updates the ArrayList<Cell> of neighboring cells
  public void updateNeighbors(ArrayList<ArrayList<Cell>> board, int x, int y) {

    // Add Top Left
    if (x > 0 && y > 0) {
      this.neighbors.add(board.get(x - 1).get(y - 1));
    }
    // Add Top Center
    if (y > 0) {
      this.neighbors.add(board.get(x).get(y - 1));
    }
    // Add Top Right
    if (x < Minesweeper.COLUMNS - 1 && y > 0) {
      this.neighbors.add(board.get(x + 1).get(y - 1));
    }
    // Add Left
    if (x > 0) {
      this.neighbors.add(board.get(x - 1).get(y));
    }
    // Add Right
    if (x < Minesweeper.COLUMNS - 1) {
      this.neighbors.add(board.get(x + 1).get(y));
    }
    // Add Bottom Left
    if (x > 0 && y < Minesweeper.ROWS - 1) {
      this.neighbors.add(board.get(x - 1).get(y + 1));
    }
    // Add Bottom Center
    if (y < Minesweeper.ROWS - 1) {
      this.neighbors.add(board.get(x).get(y + 1));
    }
    // Add Bottom Right
    if (x < Minesweeper.COLUMNS - 1 && y < Minesweeper.ROWS - 1) {
      this.neighbors.add(board.get(x + 1).get(y + 1));
    }
  }

  // EFFECT: Updates the number of neighboring mines for each neighbor in the
  // ArrayList<Cell> of neighbors
  public void updateNeighborMines() {
    for (Cell neighbor : this.neighbors) {
      if (!this.mine) {
        if (neighbor.mine) {
          this.neighborMines++;
        }
      }
      else {
        this.neighborMines++;
      }
    }
  }

  // EFFECT: Updates the flagged boolean value to the opposite of the current
  // state
  public void flag() {
    this.flagged = !this.flagged;
  }

  // EFFECT: Updates the covered boolean value to false if currently true, or it
  // uncovers all neighboring cells that were clicked that do not contain mines
  public void uncover() {
    if (this.covered && this.neighborMines == 0) {
      this.floodUncover();
    }
    if (this.covered) {
      this.covered = false;
    }
  }

  // EFFECT: Updates the covered boolean value of the neighbors
  public void floodUncover() {
    for (Cell neighbor : this.neighbors) {
      if (neighbor.covered && neighbor.neighborMines == 0) {
        this.covered = false;
        neighbor.floodUncover();
      }
      if (neighbor.covered) {
        neighbor.covered = false;
      }
    }
  }

  // Draws a WorldImage of cells
  public WorldImage draw() {

    WorldImage blankSquare = new RectangleImage(Minesweeper.CELL_SIZE, Minesweeper.CELL_SIZE,
        OutlineMode.OUTLINE, Color.black);
    WorldImage coveredSquare = new OverlayImage(blankSquare, new RectangleImage(
        Minesweeper.CELL_SIZE, Minesweeper.CELL_SIZE, OutlineMode.SOLID, Color.lightGray));
    WorldImage uncoveredSquare = new OverlayImage(blankSquare, new RectangleImage(
        Minesweeper.CELL_SIZE, Minesweeper.CELL_SIZE, OutlineMode.SOLID, new Color(176, 176, 176)));

    if (this.covered) {
      if (this.flagged) {
        return new OverlayImage(new EquilateralTriangleImage((Minesweeper.CELL_SIZE * 0.8) * 0.5,
            OutlineMode.SOLID, Color.yellow), coveredSquare).movePinhole(-Minesweeper.CELL_SIZE / 2,
                -Minesweeper.CELL_SIZE / 2);
      }
      else {
        return coveredSquare.movePinhole(-Minesweeper.CELL_SIZE / 2, -Minesweeper.CELL_SIZE / 2);
      }
    }
    else {
      if (this.mine) {
        return new OverlayImage(new CircleImage((int) ((Minesweeper.CELL_SIZE * 0.8) / 2),
            OutlineMode.SOLID, Color.red), uncoveredSquare).movePinhole(-Minesweeper.CELL_SIZE / 2,
                -Minesweeper.CELL_SIZE / 2);
      }
      else if (this.neighborMines == 0) {
        return uncoveredSquare.movePinhole(-Minesweeper.CELL_SIZE / 2, -Minesweeper.CELL_SIZE / 2);
      }
      else {
        return new OverlayImage(new TextImage("" + this.neighborMines, Minesweeper.CELL_SIZE * 0.8,
            textColors.get(this.neighborMines - 1)), uncoveredSquare)
                .movePinhole(-Minesweeper.CELL_SIZE / 2, -Minesweeper.CELL_SIZE / 2);
      }
    }
  }
}
