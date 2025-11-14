package com.ThePod.Admirals;

import com.ThePod.Admirals.board.AttackResult;
import com.ThePod.Admirals.board.CellState;
import com.ThePod.Admirals.board.Coordinates;
import com.ThePod.Admirals.board.EnemyBoard;
import com.ThePod.Admirals.board.MyBoard;
import com.ThePod.Admirals.network.Connection;
import com.ThePod.Admirals.network.callback.TurnCallback;
import com.ThePod.Admirals.util.CodeGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import lombok.Getter;

import java.util.Random;

public class GameManager {
    private static GameManager instance = null;

    private boolean isMyTurn = false;
    private final Connection connection;
    private TurnCallback callback;
    private MyBoard myBoard;
    private EnemyBoard enemyBoard;
    private boolean enemySetupDone = false;

    private GameManager(Connection connection) {
        this.connection = connection;
        this.connection.setOnDataReceive(this::dataReceived);
    }

    public static void newInstance(Connection connection) {
        if (instance != null) instance.connection.stop();
        instance = new GameManager(connection);
    }

    public static int[][] getMyBoard(){
        return instance.myBoard.getBoard();
    }

    public static int[][] getEnemyBoard(){
        return instance.enemyBoard.getBoard();
    }

    public static String getCode(){
        return CodeGenerator.encode(Connection.getCurrentIP());
    }

    public static void start(TurnCallback callback) {
        instance.callback = callback;
        callback.setUp();
    }

    public static void setupFinished(int[][] board) {
        instance.myBoard = new MyBoard(board);
        instance.enemyBoard = new EnemyBoard();

        if (instance.enemySetupDone) instance.connection.sendData("START?");
        else instance.connection.sendData("READY");
    }

    public static void attack(Coordinates coordinates) {
        instance.connection.sendData("ATTACK " +  coordinates.toString());
    }

    private void dataReceived(String data) {
        System.out.println("Received data: \"" + data + "\"");

        if (data.equals("READY")){
            enemySetupDone = true;
            callback.onEnemyReady();
        }

        else if (data.equals("START?")) {
            isMyTurn = new Random().nextBoolean();
            connection.sendData("START! " + !isMyTurn);

            if (isMyTurn) callback.onMyTurn();
            else callback.onEnemyTurn();
        }

        else if (data.startsWith("START!")) {
            isMyTurn = Boolean.getBoolean(data.split(" ")[1]);

            if (isMyTurn) callback.onMyTurn();
            else callback.onEnemyTurn();
        }

        else if (data.startsWith("ATTACK")){
            String crd = data.substring(7);
            Coordinates coordinates = new Coordinates(crd);

            AttackResult result = myBoard.attacked(coordinates);
            if (result.toString().startsWith("SINK")) {
                int boat = -myBoard.getBoard()[coordinates.getRow()][coordinates.getColumn()];
                String s = myBoard.getShip(result.getCellState());
                connection.sendData("RESULT " + coordinates + " " + result + ";" + s);
            } else {
                connection.sendData("RESULT " + coordinates + " " + result);
                callback.enemyAttack(coordinates, result, "IDK YET");
            }
        }

        else if (data.startsWith("RESULT")) {
            String position = data.substring(7, 9);
            String substring = data.substring(10);
            if (substring.startsWith("HIT")) {
                enemyBoard.attacked(new Coordinates(position), AttackResult.HIT);
            } else if (substring.startsWith("SINK")) {

            } else if (substring.equals("MISS")) {
                enemyBoard.attacked(new Coordinates(position), AttackResult.MISS);
            } else if (substring.equals("GAME_OVER")) {
                //TODO end game logic
                callback.onGameOver("TODO");
            }
        }

    }
}
