package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;

public interface IModel {
    void generateMaze(int height, int width);
    void moveCharacter(KeyCode movement);
    Maze getMaze();
    int getCharacterPositionRow();
    int getCharacterPositionColumn();
    Solution getCurrentSol();

    void solveSolution(Maze maze);

    void getLoadedMaze(byte[] byteMaze,int rowPos,int colPos);

    void dragMouse(MouseEvent mouseEvent, double cellSizeX, double cellSizeY);
}
