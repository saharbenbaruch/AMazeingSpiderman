package View;

import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.canvas.Canvas;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class PlayerDisplayer extends Canvas {
    private int characterPositionRow = 1;
    private int characterPositionColumn = 1;
    private double rows;
    private double cols;
    private int blackSpotX=1;
    private int blackSpotY=1;

    public StringProperty imageFileNameCharacterProperty() {
        return ImageFileNameCharacter;
    }

    public void setCharacterPositionRow(int characterPositionRow) {
        this.characterPositionRow = characterPositionRow;
    }

    public void setCharacterPositionColumn(int characterPositionColumn) {
        this.characterPositionColumn = characterPositionColumn;
    }

    public void setImageFileNameCharacter(String imageFileNameCharacter) {
        this.ImageFileNameCharacter.set(imageFileNameCharacter);
    }

    private StringProperty ImageFileNameCharacter = new SimpleStringProperty();
    private StringProperty ImageFileNameSpiderNet = new SimpleStringProperty();
    private StringProperty ImageFileNameBlackSpot = new SimpleStringProperty();

    public void setImageFileNameSpiderNet(String imageFileNameSpiderNet) {
        this.ImageFileNameSpiderNet.set(imageFileNameSpiderNet);
    }

    public String getImageFileNameBlackSpot() {
        return ImageFileNameBlackSpot.get();
    }

    public StringProperty imageFileNameBlackSpotProperty() {
        return ImageFileNameBlackSpot;
    }

    public void setImageFileNameBlackSpot(String imageFileNameBlackSpot) {
        this.ImageFileNameBlackSpot.set(imageFileNameBlackSpot);
    }

    public String getImageFileNameSpiderNet() {
        return ImageFileNameSpiderNet.get();
    }

    public StringProperty imageFileNameSpiderNetProperty() {
        return ImageFileNameSpiderNet;
    }

    public void setCharacterPosition(int row, int column) {
        double canvasHeight = getHeight();
        double canvasWidth = getWidth();
        double cellHeight = canvasHeight / rows;
        double cellWidth = canvasWidth / cols;
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(characterPositionColumn * cellWidth, characterPositionRow * cellHeight, cellWidth, cellHeight);
        characterPositionRow = row;
        characterPositionColumn = column;
        redraw();
    }

    public void setBlackSpot(int x,int y){
        blackSpotX=x;
        blackSpotY=y;
        double cellHeight = getHeight() / rows;
        double cellWidth = getWidth() / cols;
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(characterPositionColumn * cellWidth, characterPositionRow * cellHeight, cellWidth, cellHeight);
        Image blackSpotImage = null;
        try {
       blackSpotImage = new Image(new FileInputStream(ImageFileNameBlackSpot.get()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //draw black spot
       gc.drawImage(blackSpotImage,cellWidth*blackSpotX,cellHeight*blackSpotY);

    }

    public void redraw() {
        double canvasHeight = getHeight();
        double canvasWidth = getWidth();
        double cellHeight = canvasHeight / rows;
        double cellWidth = canvasWidth / cols;

        try {
            Image characterImage = new Image(new FileInputStream(ImageFileNameCharacter.get()));
            GraphicsContext gc = getGraphicsContext2D();
            gc.clearRect(characterPositionColumn * cellWidth, characterPositionRow * cellHeight, cellWidth, cellHeight);
            gc.drawImage(characterImage, characterPositionColumn * cellWidth, characterPositionRow * cellHeight, cellWidth, cellHeight);
            //setBlackSpot(blackSpotX,blackSpotY);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getImageFileNameCharacter() {
        return ImageFileNameCharacter.get();
    }

    public void setMazeSize(int numOfRows, int numOfColumns) {
        rows = numOfRows;
        cols = numOfColumns;
    }

    public void clearAll() {
        double canvasHeight = getHeight();
        double canvasWidth = getWidth();
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0,0,canvasWidth,canvasHeight);
    }


    public void drawSolution(Solution currentSol) {
        double canvasHeight = getHeight();
        double canvasWidth = getWidth();
        double cellHeight = canvasHeight / rows;
        double cellWidth = canvasWidth / cols;

        try {
            Image characterImage = new Image(new FileInputStream(ImageFileNameSpiderNet.get()));
            GraphicsContext gc = getGraphicsContext2D();
            for (int i=1;i<currentSol.getSolutionPath().size();i++){
                String tempStr=currentSol.getSolutionPath().get(i).toString();
                Scanner in = new Scanner(tempStr).useDelimiter("[^0-9]+");
                int x = in.nextInt();
                int y=in.nextInt();
                gc.drawImage(characterImage, y * cellWidth, x * cellHeight, cellWidth, cellHeight);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}


