package ViewModel;

import Model.IModel;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;

import java.util.Observable;
import java.util.Observer;

public class MyViewModel extends Observable implements Observer {
    private IModel model;
    public Solution currentSol=null;

    public void setCharacterPositionRow(int characterPositionRow) {
        this.characterPositionRow = characterPositionRow;
    }

    public void setCharacterPositionCol(int characterPositionCol) {
        this.characterPositionCol = characterPositionCol;
    }

    private int characterPositionRow;
    private int characterPositionCol;

    public StringProperty characterPositionRowStr = new SimpleStringProperty("1"); //For Binding
    public StringProperty characterPositionColumnStr = new SimpleStringProperty("1"); //For Binding

    public int getCharacterPositionRow() {
        return characterPositionRow;
    }

    public int getCharacterPositionCol() {
        return characterPositionCol;
    }

    public MyViewModel(IModel model) {
        this.model = model;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o == model) {
            switch ((String) arg) {
                case "generateMaze":
                    characterPositionRow = model.getCharacterPositionRow();
                    characterPositionRowStr.set(characterPositionRow + "");
                    characterPositionCol = model.getCharacterPositionColumn();
                    characterPositionColumnStr.set(characterPositionCol + "");
                    setChanged();
                    notifyObservers("generateMaze");
                    break;

                case "setCharacterPosition":
                    characterPositionRow = model.getCharacterPositionRow();
                    characterPositionRowStr.set(characterPositionRow + "");
                    characterPositionCol = model.getCharacterPositionColumn();
                    characterPositionColumnStr.set(characterPositionCol + "");
                    setChanged();
                    notifyObservers("setCharacterPosition");
                    break;

                case "solveMaze":
                    currentSol=model.getCurrentSol();
                    setChanged();
                    notifyObservers("solveMaze");
                    break;

                case "win":
                    setChanged();
                    notifyObservers("win");
                    break;

            }
        }
    }

    public void generateMaze(int height, int width){
        model.generateMaze(height, width);
    }

    public void moveCharacter(KeyCode movement){
        model.moveCharacter(movement);
    }

    public Maze getMaze() {
        return model.getMaze();
    }


    public void solveMaze() {
        Maze maze= getMaze();
        int x= characterPositionRow;
        int y=characterPositionCol;
        Position currStart=new Position(x,y);
        maze.setStart(currStart);
        model.solveSolution(maze);
    }

    public void sendLoadedMaze(byte[] byteMaze,int rowPos, int colPos) {
        model.getLoadedMaze(byteMaze,rowPos,colPos);
    }

    public void moveByDrag(MouseEvent mouseEvent,double canvasSizeX,double canvasSizeY) {
        double cellSizeX=canvasSizeX/getMaze().getNumOfRows();
        double cellSizeY=canvasSizeY/getMaze().getNumOfColumns();
        model.dragMouse(mouseEvent,cellSizeX,cellSizeY);
    }
}
