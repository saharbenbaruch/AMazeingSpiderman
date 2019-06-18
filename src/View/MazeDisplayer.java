package View;

import algorithms.mazeGenerators.Maze;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.canvas.Canvas;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MazeDisplayer extends Canvas {
    private Maze maze;
   // private int characterPositionRow=1;
 //   private int characterPositionColumn=1;

    public void setMaze(Maze maze) {
        this.maze = maze;
        // characterPositionRow=maze.getStartPosition().getRowIndex() ;
       //  characterPositionColumn=maze.getStartPosition().getColumnIndex();
        redraw();
    }

//    public void setCharacterPosition(int row, int column) {
//      //  characterPositionRow = row;
//      //  characterPositionColumn = column;
//        redraw();
//    }

//    public int getCharacterPositionRow() {
////        return characterPositionRow;
////    }
////
////    public int getCharacterPositionColumn() {
////        return characterPositionColumn;
////    }
    private StringProperty ImageFileNameCriminal = new SimpleStringProperty();

        public String getImageFileNameCriminal() {
            return ImageFileNameCriminal.get();
        }

        public StringProperty imageFileNameCriminalProperty() {
            return ImageFileNameCriminal;
        }

        public void setImageFileNameCriminal(String imageFileNameCriminal) {
            this.ImageFileNameCriminal.set(imageFileNameCriminal);
        }

    public void redraw() {
        if (maze != null) {
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            double cellHeight = canvasHeight / maze.getNumOfRows();
            double cellWidth = canvasWidth / maze.getNumOfColumns();

            try {
                Image wallImage = new Image(new FileInputStream(ImageFileNameWall.get()));
                Image criminalImage = new Image(new FileInputStream(ImageFileNameCriminal.get()));
                //Image characterImage = new Image(new FileInputStream(ImageFileNameCharacter.get()));

                GraphicsContext gc = getGraphicsContext2D();
                gc.clearRect(0, 0, getWidth(), getHeight());

                //Draw Maze
                for (int i = 0; i < maze.getNumOfRows(); i++) {
                    for (int j = 0; j < maze.getNumOfColumns(); j++) {
                        if (maze.getValueInCell(i,j) == 1) {
                            //gc.fillRect(i * cellHeight, j * cellWidth, cellHeight, cellWidth);
                            gc.drawImage(wallImage,  j * cellWidth,i * cellHeight,cellWidth,cellHeight);
                        }
                    }
                }
                gc.drawImage(criminalImage,  maze.getGoalPosition().getColumnIndex() * cellWidth,maze.getGoalPosition().getRowIndex() * cellHeight,cellWidth,cellHeight);

                //Draw Character
                //gc.setFill(Color.RED);
                //gc.fillOval(characterPositionColumn * cellHeight, characterPositionRow * cellWidth, cellHeight, cellWidth);
              //  gc.drawImage(characterImage,characterPositionColumn * cellWidth, characterPositionRow * cellHeight,cellWidth,cellHeight);
            } catch (FileNotFoundException e) {
                //e.printStackTrace();
            }
        }
    }

    //region Properties
    private StringProperty ImageFileNameWall = new SimpleStringProperty();


   // private StringProperty ImageFileNameCharacter = new SimpleStringProperty();

    public String getImageFileNameWall() {
        return ImageFileNameWall.get();
    }

    public void setImageFileNameWall(String imageFileNameWall) {
        this.ImageFileNameWall.set(imageFileNameWall);
    }

 /*   public String getImageFileNameCharacter() {
        return ImageFileNameCharacter.get();
    }*/

   /* public void setImageFileNameCharacter(String imageFileNameCharacter) {
        this.ImageFileNameCharacter.set(imageFileNameCharacter);
    }*/
    //endregion
}
