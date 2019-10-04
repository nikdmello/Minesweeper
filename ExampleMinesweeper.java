import javalib.worldimages.*;
import tester.Tester;

import java.awt.*;
import java.util.ArrayList;

// Represents tests and examples of Minesweeper
class ExampleMinesweeper {

  // Runs the game
  void testGame(Tester t) {
    initTestArray2();
    Minesweeper g = new Minesweeper();
    g.bigBang(g.GAME_WIDTH, g.GAME_HEIGHT);
  }

  // World images
  WorldImage blankSquare;
  WorldImage coveredSquare;
  WorldImage uncoveredSquare;
  WorldImage mineImage;
  WorldImage coveredImage;
  WorldImage uncoveredImage;
  WorldImage coveredFlaggedImage;

  // Cells
  Cell mine;
  Cell coveredWithMine;
  Cell coveredNoMine;
  Cell uncovered;

  // Initializes the cells
  void initCells() {

    blankSquare = new RectangleImage(Minesweeper.CELL_SIZE, Minesweeper.CELL_SIZE,
        OutlineMode.OUTLINE, Color.black);
    coveredSquare = new OverlayImage(blankSquare, new RectangleImage(Minesweeper.CELL_SIZE,
        Minesweeper.CELL_SIZE, OutlineMode.SOLID, Color.lightGray));
    uncoveredSquare = new OverlayImage(blankSquare, new RectangleImage(Minesweeper.CELL_SIZE,
        Minesweeper.CELL_SIZE, OutlineMode.SOLID, new Color(176, 176, 176)));

    mine = new Cell(true, false, false, 0, new ArrayList<>());
    mineImage = new OverlayImage(
        new CircleImage((int) ((Minesweeper.CELL_SIZE * 0.8) / 2), OutlineMode.SOLID, Color.red),
        uncoveredSquare).movePinhole(-Minesweeper.CELL_SIZE / 2, -Minesweeper.CELL_SIZE / 2);

    coveredWithMine = new Cell(true, true, false, 0, new ArrayList<>());
    coveredNoMine = new Cell(false, true, false, 0, new ArrayList<>());
    coveredImage = coveredSquare.movePinhole(-Minesweeper.CELL_SIZE / 2,
        -Minesweeper.CELL_SIZE / 2);
    coveredFlaggedImage = new OverlayImage(new EquilateralTriangleImage(
        (Minesweeper.CELL_SIZE * 0.8) * 0.5, OutlineMode.SOLID, Color.yellow), coveredSquare)
            .movePinhole(-Minesweeper.CELL_SIZE / 2, -Minesweeper.CELL_SIZE / 2);

    uncovered = new Cell(false, false, false, 0, new ArrayList<>());
    uncoveredImage = uncoveredSquare.movePinhole(-Minesweeper.CELL_SIZE / 2,
        -Minesweeper.CELL_SIZE / 2);
  }

  // Tests for draw method
  void testDraw(Tester t) {
    initCells();

    t.checkExpect(mine.draw(), mineImage);
    t.checkExpect(coveredNoMine.draw(), coveredImage);
    t.checkExpect(coveredWithMine.draw(), coveredImage);
    t.checkExpect(uncovered.draw(), uncoveredImage);

    coveredNoMine.flag();
    t.checkExpect(coveredNoMine.draw(), coveredFlaggedImage);

  }

  // Examples for a test array
  ArrayList<ArrayList<Cell>> testArray;
  Cell cell11;
  Cell cell00;

  // Test array
  void initTestArray() {
    testArray = new ArrayList<>();

    for (int x = 0; x < 3; x++) {
      ArrayList<Cell> columnTemp = new ArrayList<>();
      for (int y = 0; y < 3; y++) {
        columnTemp.add(new Cell(true));
      }
      testArray.add(columnTemp);
    }

    testArray.get(1).set(1, new Cell(false));

    /*
     * Testing array looks like this X are mines C is the mine being tested
     * [X][X][X] [X][C][X] [X][X][X]
     */

    cell11 = testArray.get(1).get(1);

    /*
     * Testing array looks like this X are mines C is the mine being tested
     * [C][X][X] [X][O][X] [X][X][X]
     */

    cell00 = testArray.get(0).get(0);
  }
  
  // Second test array
  void initTestArray2() {
    testArray = new ArrayList<>();

    for (int x = 0; x < 3; x++) {
      ArrayList<Cell> columnTemp = new ArrayList<>();
      for (int y = 0; y < 3; y++) {
        columnTemp.add(new Cell(false));
      }
      testArray.add(columnTemp);
    }

    cell11 = testArray.get(1).get(1);

  }

  // Tests for updateNeighbors method
  void testUpdateNeighbors(Tester t) {
    initCells();
    initTestArray();

    cell11.updateNeighbors(testArray, 1, 1);
    cell00.updateNeighbors(testArray, 0, 0);

    t.checkExpect(cell11.neighbors.size(), 8);
    t.checkExpect(cell00.neighbors.size(), 3);
  }

  // Tests for updateNeighborMines method
  void testUpdateNeighborMines(Tester t) {
    initCells();
    initTestArray();

    cell11.updateNeighbors(testArray, 1, 1);
    cell11.updateNeighborMines();
    cell00.updateNeighbors(testArray, 0, 0);
    cell00.updateNeighborMines();

    t.checkExpect(cell11.neighborMines, 8);
    // Cell 0,0 is a mine so it does not count the mines around itself
    t.checkExpect(cell00.neighborMines, 3);
  }

  // Tests for flag method
  void testFlag(Tester t) {
    initCells();
    t.checkExpect(mine.flagged, false);
    mine.flag();
    t.checkExpect(mine.flagged, true);
  }

  void testUncover(Tester t) {
    initCells();

  }


  // Tests for floodUncover method
  void testFloodUncover(Tester t) {
    initCells();
    initTestArray2();
    cell11.updateNeighbors(testArray, 1, 1);
    cell11.floodUncover();

    Cell cell00 = testArray.get(0).get(0);
    Cell cell02 = testArray.get(2).get(0);

    t.checkExpect(cell00.covered, false);
    t.checkExpect(cell02.covered, false);
  }
  
//  void testCheckWin(Tester t ) {
//    initTestArray();
//    
//    Minesweeper g = new Minesweeper(testArray);
//    g.cells.get(1).get(1).covered = false;
//    
//  }
  
  // Tests for onMouseClicks method
  void testOnMouseClicked(Tester t) {
    initTestArray();
    
    Posn pos = new Posn(Minesweeper.CELL_SIZE + 5, Minesweeper.CELL_SIZE + 5);
    Posn pos2 = new Posn(Minesweeper.CELL_SIZE + 5, (Minesweeper.CELL_SIZE * 2) + 5);
    Minesweeper g = new Minesweeper(testArray);
    
    g.onMouseClicked(pos, "LeftButton");
    Cell cell11 = g.cells.get(1).get(1);
    g.onMouseClicked(pos2, "RightButton");
    Cell cell12 = g.cells.get(1).get(2);

    
    t.checkExpect(cell11.covered, false);
    t.checkExpect(cell12.flagged, true);
  }

}