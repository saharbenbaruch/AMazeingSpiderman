package Model;

import Client.Client;
import Client.IClientStrategy;
import IO.MyDecompressorInputStream;
import Server.Server;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import algorithms.mazeGenerators.IMazeGenerator;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MyMazeGenerator;
import algorithms.search.AState;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyModel extends Observable implements IModel {
    private Maze maze;
    private int characterPositionRow;
    private int characterPositionColumn;
    private Server mazeGeneratingServer;
    private Server solveSearchProblemServer;
    private Solution currentSol = null;


    public Maze generateRandomMaze(int height, int width) {
        IMazeGenerator generator = new MyMazeGenerator();
        maze = generator.generate(height, width);
        maze.print();
        System.out.println("-------------------");
        characterPositionRow = maze.getStartPosition().getRowIndex();
        characterPositionColumn = maze.getStartPosition().getColumnIndex();
        return maze;
    }

    @Override
    public void generateMaze(int height, int width) {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        int[] mazeDimensions = new int[]{height, width};
                        toServer.writeObject(mazeDimensions);
                        toServer.flush();
                        byte[] compressedMaze = (byte[]) ((byte[]) fromServer.readObject());
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[height * width + 12];
                        is.read(decompressedMaze);
                        maze = new Maze(decompressedMaze);
                        maze.print();
                        characterPositionRow = maze.getStartPosition().getRowIndex();
                        characterPositionColumn = maze.getStartPosition().getColumnIndex();


                    } catch (Exception var12) {
                        var12.printStackTrace();
                    }

                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException var1) {
            var1.printStackTrace();
        }

//        generateRandomMaze(height, width);
        setChanged();
        notifyObservers("generateMaze");

    }

    public void solveMaze() {

    }

    //check if charcter want to move to legal position
    public boolean isAvailable(int row, int col) {
        if ((row >= 0) && (row <= maze.getNumOfRows() - 1) && (col >= 0) && (col <= maze.getNumOfColumns() - 1))
            return ((maze.getValueInCell(row, col) == (0) ||
                    maze.getValueInCell(row, col) == (3) || maze.getValueInCell(row, col) == (4)));

        else
            return false;
    }

    @Override
    public void moveCharacter(KeyCode movement) {
        switch (movement) {
            case UP:
                if (isAvailable(characterPositionRow - 1, characterPositionColumn))
                    characterPositionRow--;
                break;
            case DOWN:
                if (isAvailable(characterPositionRow + 1, characterPositionColumn))
                    characterPositionRow++;
                break;
            case RIGHT:
                if (isAvailable(characterPositionRow, characterPositionColumn + 1))
                    characterPositionColumn++;
                break;
            case LEFT:
                if (isAvailable(characterPositionRow, characterPositionColumn - 1))
                    characterPositionColumn--;
                break;
            case NUMPAD8:
                if (isAvailable(characterPositionRow - 1, characterPositionColumn))
                    characterPositionRow--;
                break;
            case NUMPAD2:
                if (isAvailable(characterPositionRow + 1, characterPositionColumn))
                    characterPositionRow++;
                break;
            case NUMPAD6:
                if (isAvailable(characterPositionRow, characterPositionColumn + 1))
                    characterPositionColumn++;
                break;
            case NUMPAD4:
                if (isAvailable(characterPositionRow, characterPositionColumn - 1))
                    characterPositionColumn--;
                break;
            case NUMPAD9:
                if (isAvailable(characterPositionRow - 1, characterPositionColumn + 1)) {
                    characterPositionRow--;
                    characterPositionColumn++;
                }
                break;
            case NUMPAD7:
                if (isAvailable(characterPositionRow - 1, characterPositionColumn - 1)) {
                    characterPositionRow--;
                    characterPositionColumn--;
                }
                break;
            case NUMPAD1:
                if (isAvailable(characterPositionRow + 1, characterPositionColumn - 1)) {
                    characterPositionRow++;
                    characterPositionColumn--;
                }
                break;
            case NUMPAD3:
                if (isAvailable(characterPositionRow + 1, characterPositionColumn + 1)) {
                    characterPositionRow++;
                    characterPositionColumn++;
                }

                break;


        }
        setChanged();
        notifyObservers("setCharacterPosition");

        if (characterPositionRow == maze.getGoalPosition().getRowIndex() && characterPositionColumn == maze.getGoalPosition().getColumnIndex()) {
            setChanged();
            notifyObservers("win");
        }


    }

    @Override
    public Maze getMaze() {
        return maze;
    }

    @Override
    public int getCharacterPositionRow() {
        return characterPositionRow;
    }

    @Override
    public int getCharacterPositionColumn() {
        return characterPositionColumn;
    }

    public void startServers() {
        mazeGeneratingServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        solveSearchProblemServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
        mazeGeneratingServer.start();
        solveSearchProblemServer.start();
    }

    public void stopServers() {
        mazeGeneratingServer.stop();
        solveSearchProblemServer.stop();
    }

    public Solution getCurrentSol() {
        return currentSol;
    }

    public void solveSolution(Maze maze) {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        maze.print();
                        toServer.writeObject(maze);
                        toServer.flush();
                        Solution mazeSolution = (Solution) fromServer.readObject();
                        System.out.println(String.format("Solution steps: %s", mazeSolution));
                        ArrayList<AState> mazeSolutionSteps = mazeSolution.getSolutionPath();

                        for (int i = 0; i < mazeSolutionSteps.size(); ++i) {
                            System.out.println(String.format("%s. %s", i, ((AState) mazeSolutionSteps.get(i)).toString()));
                            currentSol = mazeSolution;
                            setChanged();
                            notifyObservers("solveMaze");
                        }
                    } catch (Exception var10) {
                        var10.printStackTrace();
                    }

                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException var1) {
            ;
        }

    }

    @Override
    public void getLoadedMaze(byte[] byteMaze, int rowPos, int colPos) {
        Maze m = new Maze(byteMaze);
        maze = m;
        characterPositionRow = rowPos;
        characterPositionColumn = colPos;
        setChanged();
        notifyObservers("generateMaze");
    }

    public void dragMouse(MouseEvent e, double cellWight, double cellHight) {
        double upBorder = characterPositionRow * cellHight;
        double leftBorder = characterPositionColumn * cellWight;

        double bottomBorder = (characterPositionRow + 1) * cellHight;
        double rightBorder = (characterPositionColumn + 1) * cellWight;

        // Move Up
        if((e.getY() < upBorder)
                && ((e.getX() < rightBorder) && (e.getX() > leftBorder)))
        {
            moveCharacter(KeyCode.UP);
        }
        // Move Down
        else if((e.getY() > bottomBorder)
                && ((e.getX() < rightBorder) && (e.getX() > leftBorder)))
        {
            moveCharacter(KeyCode.DOWN);
        }
        // Move Right
        else if((e.getX() > rightBorder)
                && ((e.getY() < bottomBorder) && (e.getY() > upBorder)))
        {
            moveCharacter(KeyCode.RIGHT);
        }

        // Move UP-Right
        else if((e.getY() < upBorder) && (e.getX() > rightBorder))
        {
            moveCharacter(KeyCode.NUMPAD9);
        }
        // Move Down-Left
        else if((e.getY() > bottomBorder) && (e.getX() < upBorder))
        {
            moveCharacter(KeyCode.NUMPAD1);
        }
        // Move Up-Left
        else if((e.getY() < upBorder) && (e.getX() < leftBorder))
        {
            moveCharacter(KeyCode.NUMPAD7);
        }
        // Move Down-Right
        else if((e.getY() > bottomBorder) && (e.getX() > rightBorder))
        {
            moveCharacter(KeyCode.NUMPAD3);
        }
        // Move Down-Left
        else if((e.getY() > bottomBorder) && (e.getX() < upBorder))
        {
            moveCharacter(KeyCode.NUMPAD1);
        }
        // Move Left

        else if((e.getX() < leftBorder)
                && ((e.getY() < bottomBorder) && (e.getY() > upBorder)))
        {
            moveCharacter(KeyCode.LEFT);
        }
    }
}



