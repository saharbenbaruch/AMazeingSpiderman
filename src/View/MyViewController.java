package View;

import Server.Server;
import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.*;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import java.util.Random;

public class MyViewController implements Observer, IView {

    @FXML
    private MyViewModel viewModel;
    public MazeDisplayer mazeDisplayer;
    public PlayerDisplayer playerDisplayer;
    public javafx.scene.control.TextField txtfld_rowsNum;
    public javafx.scene.control.TextField txtfld_columnsNum;
    public javafx.scene.control.Label lbl_rowsNum;
    public javafx.scene.control.Label lbl_columnsNum;
    public javafx.scene.control.Button btn_generateMaze;
    public javafx.scene.control.Button btn_solveMaze;
    public javafx.scene.control.Button btn_mute;
    public javafx.scene.layout.BorderPane boarderPane;
    public javafx.scene.layout.Pane pane;
    public javafx.scene.layout.Pane bigPane;
    public javafx.scene.layout.VBox vBox;
    private MediaPlayer player;
    public javafx.scene.image.ImageView cover;
    public javafx.scene.image.ImageView instructionsPic;
    public javafx.scene.control.TextArea textArea_prop;
    public javafx.scene.control.RadioButton spiderman_radioBut;
    public javafx.scene.control.RadioButton venom_radiobut;
    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
        bindProperties(viewModel);
        btn_solveMaze.setDisable(true);
    }

    private void bindProperties(MyViewModel viewModel) {
        //mach position of character as label
        lbl_rowsNum.textProperty().bind(viewModel.characterPositionRowStr);
        lbl_columnsNum.textProperty().bind(viewModel.characterPositionColumnStr);
        //set back picture same size as window
        cover.fitHeightProperty().bind(bigPane.heightProperty());
        cover.fitWidthProperty().bind(bigPane.widthProperty());

    }

    @Override
    public void update(Observable o, Object arg) {
        if (o == viewModel) {
            switch ((String) arg) {
                case "generateMaze":
                    displayMaze(viewModel.getMaze());
                    btn_generateMaze.setDisable(false);
                    btn_solveMaze.setDisable(false);
                    playerDisplayer.clearAll();
                          playerDisplayer.setCharacterPosition(viewModel.getMaze().getStartPosition().getRowIndex(),viewModel.getMaze().getStartPosition().getColumnIndex());
                    movePlayer();
                    break;
                case "setCharacterPosition":
                    movePlayer();
                    break;

                    case "solveMaze":
                        drawSolution(viewModel.currentSol);
                        break;

                case "win":
                    stopMusic();
                    playMusic("winning.mp3",2);
                    ActionEvent actionEvent=new ActionEvent();
                    openWinWindow(actionEvent);
                    break;

            }
        }
    }

    public void openWinWindow(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("Winner");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("winWindow.fxml").openStream());
            Scene scene = new Scene(root, 600, 400);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
        } catch (Exception e) {

        }
    }

    private void drawSolution(Solution currentSol) {
        playerDisplayer.drawSolution(currentSol);
        btn_solveMaze.setDisable(false);
    }

    public void saveMaze(ActionEvent actionEvent) throws IOException {
        FileChooser chooser=new FileChooser();
        chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("spiderMaze","*.spiderMaze"));
        File f=chooser.showSaveDialog(null);
        if(f!=null){
            File saveFile=new File(f.getPath());
            ObjectOutputStream maze=new ObjectOutputStream(new FileOutputStream(saveFile));
            Object[] mazeAndInfo=new Object[3];
            byte[] mazeByte=viewModel.getMaze().toByteArray();;
            mazeAndInfo[0]=mazeByte;
            mazeAndInfo[1]=characterPositionRow.toString();
            mazeAndInfo[2]=characterPositionColumn.toString();
            maze.writeObject(mazeAndInfo);
            maze.flush();
            maze.close();
        }
        else{
            Alert a= new Alert(Alert.AlertType.ERROR,"Not found file.");
            Optional<ButtonType> result = a.showAndWait();
            if (result.get() == ButtonType.OK){
                 a.close();
            }
        }

        }

    public void loadMaze(ActionEvent actionEvent) throws IOException, ClassNotFoundException {
        FileChooser chooser=new FileChooser();
        chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("spiderMaze","*.spiderMaze"));
        File f=chooser.showOpenDialog(null);
        if(f!=null){
            File saveFile=new File(f.getPath());
            ObjectInputStream maze=new ObjectInputStream(new FileInputStream(saveFile));
            Object[] mazeAndInfo= (Object[]) maze.readObject();
            byte[] byteMaze=(byte[]) mazeAndInfo[0];
            String characterRow=(String)mazeAndInfo[1];
            String characterCol=(String)mazeAndInfo[2];
            maze.close();
            characterPositionRow.setValue(characterRow);
            characterPositionColumn.setValue(characterCol);
            characterRow=characterRow.replaceAll("[^0-9]", "");
            characterCol=characterCol.replaceAll("[^0-9]", "");
            viewModel.sendLoadedMaze(byteMaze,Integer.parseInt(characterRow),Integer.parseInt(characterCol));

        }
        else{
            Alert a= new Alert(Alert.AlertType.ERROR,"Not found file.");
            Optional<ButtonType> result = a.showAndWait();
            if (result.get() == ButtonType.OK){
                a.close();
            }
        }

    }

    @Override
    public void displayMaze(Maze maze) {
        mazeDisplayer.setMaze(maze);
        playerDisplayer.setMazeSize(maze.getNumOfRows(),maze.getNumOfColumns());

      //  int characterPositionRow = viewModel.getCharacterPositionRow();
       // int characterPositionColumn = viewModel.getCharacterPositionCol();
       // mazeDisplayer.setCharacterPosition(characterPositionRow, characterPositionColumn);
       // this.characterPositionRow.set(characterPositionRow + "");
      //  this.characterPositionColumn.set(characterPositionColumn + "");
    }

    public void movePlayer(){
        int characterPositionRow = viewModel.getCharacterPositionRow();
        int characterPositionColumn = viewModel.getCharacterPositionCol();
        playerDisplayer.setCharacterPosition(characterPositionRow,characterPositionColumn);
        this.characterPositionRow.set(characterPositionRow + "");
        this.characterPositionColumn.set(characterPositionColumn + "");
    }

    public void generateMaze() {
        int heigth = Integer.valueOf(txtfld_rowsNum.getText());
        int width = Integer.valueOf(txtfld_columnsNum.getText());
        btn_generateMaze.setDisable(true);
        if (heigth>0 && width>0) {
            viewModel.generateMaze(heigth, width);
            if (player!=null)
                player.stop();
            playMusic("music.mp3", 200);
        }
//incase of illegal input
        else {
            Alert alert=new Alert(Alert.AlertType.ERROR,"Illegal input",ButtonType.OK);
            alert.showAndWait();
            ButtonType result = alert.getResult();
            //if press OK give another chance
            if (result==ButtonType.OK){
                btn_generateMaze.setDisable(false);
            }

        }
    }

    public void Instructions(){
        Stage stage=new Stage();
        FXMLLoader fxmlLoader=new FXMLLoader();
        try {
            Parent root=fxmlLoader.load(getClass().getResource("Instructions.fxml"));
            Scene scene=new Scene(root,600,380);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void playMusic(String music, int time) {
        Media m = new Media(MyViewController.class.getClassLoader().getResource(music).toExternalForm());
        player = new MediaPlayer(m);
        player.setAutoPlay(true);
        player.setStartTime(Duration.seconds(0));
        player.setStopTime(Duration.seconds(time));
        switch (music){
            case "winning.mp3":
                player.setCycleCount(1);
                break;
            case "music.mp3":
                player.setCycleCount(MediaPlayer.INDEFINITE);
                break;
        }
        player.play();
    }

    public void stopMusic (){
        if (btn_mute.getText().equals("Mute")) {
            player.stop();
            btn_mute.setText("Play");
        }
        else if (btn_mute.getText().equals("Play")) {
            player.play();
            btn_mute.setText("Mute");
        }
    }

    public void solveMaze(ActionEvent actionEvent) {
        btn_solveMaze.setDisable(true);
        viewModel.solveMaze();
    }

    private void showAlert(String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(alertMessage);
        alert.show();
    }

    public void KeyPressed(KeyEvent keyEvent) {
        viewModel.moveCharacter(keyEvent.getCode());
        keyEvent.consume();
    }

    //region String Property for Binding
    public StringProperty characterPositionRow = new SimpleStringProperty();

    public StringProperty characterPositionColumn = new SimpleStringProperty();

    public String getCharacterPositionRow() {
        return characterPositionRow.get();
    }

    public StringProperty characterPositionRowProperty() {
        return characterPositionRow;
    }

    public String getCharacterPositionColumn() {
        return characterPositionColumn.get();
    }

    public StringProperty characterPositionColumnProperty() {
        return characterPositionColumn;
    }

    public void setResizeEvent(Scene scene) {
        long width = 0;
        long height = 0;
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                System.out.println("Width: " + newSceneWidth);
                mazeDisplayer.setWidth(mazeDisplayer.getWidth() + (newSceneWidth.doubleValue() - oldSceneWidth.doubleValue()));
                playerDisplayer.setWidth(playerDisplayer.getWidth() + (newSceneWidth.doubleValue() - oldSceneWidth.doubleValue()));

                mazeDisplayer.redraw();
                playerDisplayer.clearAll();
                playerDisplayer.redraw();
            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                System.out.println("Height: " + newSceneHeight);
                mazeDisplayer.setHeight(mazeDisplayer.getHeight() + (newSceneHeight.doubleValue() - oldSceneHeight.doubleValue()));

                playerDisplayer.setHeight(playerDisplayer.getHeight() + (newSceneHeight.doubleValue() - oldSceneHeight.doubleValue()));
                mazeDisplayer.redraw();
                playerDisplayer.clearAll();
                playerDisplayer.redraw();
            }
        });
    }

    public void Info(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("Info");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("Info.fxml").openStream());
            Scene scene = new Scene(root, 600, 600);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
        } catch (Exception e) {

        }
    }

    public void Properties(ActionEvent actionEvent) {
        try {
            String content= "The Maze properties are:\n\n";
            Server.Configuration confi=new Server.Configuration();
            try {
                String searchingAlgo=confi.getSerchingAlgorithem();
                String mazeGenerator=confi.getMazeGenerator();
                int SizeOfThreadPool=confi.getSizeOfThreadPool();
                content=content+"Searching Algorithem is: "+ searchingAlgo
                        +"\n"+ "Maze Generator is: "+mazeGenerator+"\n"+ "Size of Thread Pool is: "+ String.valueOf(SizeOfThreadPool)+"\n";

                textArea_prop=new TextArea(content);
                textArea_prop.cancelEdit();
                //textArea_prop.setText(content);



            } catch (IOException e) {
                Alert a=new Alert(Alert.AlertType.ERROR);
                a.setContentText("file not founded.");
            }
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Properties");
            stage.setWidth(350);
            stage.setHeight(200);
            stage.setResizable(false);
            Pane pane = new Pane();
            pane.setPrefHeight(350);
            pane.setPrefWidth(350);
            pane.getChildren().addAll(textArea_prop);
            Scene scene = new Scene(pane, 350, 350);
            stage.setScene(scene);
            stage.showAndWait();
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.showAndWait();
        } catch (Exception e) {

        }

    }

        public void exit(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Are you sure you want to exit?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            System.exit(0);
            // ... user chose OK
            // Close program
        }
    }


    public void changeCharacterToVenom(){

            playerDisplayer.setImageFileNameCharacter("resources/images/venom.png");
            playerDisplayer.clearAll();
            playerDisplayer.redraw();
        spiderman_radioBut.setSelected(false);
        venom_radiobut.setSelected(true);

        }
    public void changeCharacterToSpider(){


            playerDisplayer.setImageFileNameCharacter("resources/images/char3.jpg");
            playerDisplayer.clearAll();
            playerDisplayer.redraw();
             venom_radiobut.setSelected(false);
             spiderman_radioBut.setSelected(true);

        }

    public void moveByDrag(MouseEvent mouseEvent) {
        viewModel.moveByDrag(mouseEvent,mazeDisplayer.getWidth(),mazeDisplayer.getHeight());
    }

    public void onMouseClick(MouseEvent mouseEvent) {
        this.mazeDisplayer.requestFocus();
    }


}


