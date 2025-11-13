package com.ThePod.Admirals;

import com.ThePod.Admirals.board.AttackResult;
import com.ThePod.Admirals.board.Coordinates;
import com.ThePod.Admirals.board.EnemyBoard;
import com.ThePod.Admirals.board.MyBoard;
import com.ThePod.Admirals.network.Connection;
import com.ThePod.Admirals.network.callback.TurnCallback;
import com.ThePod.Admirals.util.CodeGenerator;
import lombok.Getter;

import java.util.Random;

public class GameManager {
    @Getter private static GameManager instance;

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
        instance = new GameManager(connection);
    }

    public static String getCode(){
        return CodeGenerator.encode(Connection.getCurrentIP());
    }

    public void start(TurnCallback callback) {
        this.callback = callback;
        connection.sendData("SETUP");
        callback.setUp();
    }

    public void setupFinished(int[][] board) {
        myBoard = new MyBoard(board);
        enemyBoard = new EnemyBoard();

        if (enemySetupDone) connection.sendData("START?");
        else connection.sendData("READY");
    }

    public void attack(Coordinates coordinates) {
        connection.sendData("ATTACK " +  coordinates.toString());
    }

    private void dataReceived(String data) {
        if (data.equals("SETUP")){
            callback.setUp();
        }

        else if (data.equals("READY")){
            enemySetupDone = true;
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
            // TODO WHEN ATTACKED
        }

        else if (data.startsWith("RESULT")) {
            String position = data.substring(7, 9);
            String substring = data.substring(10);
            if (substring.startsWith("HIT")) {
                enemyBoard.attacked(new Coordinates(position), AttackResult.HIT);
            } else if (substring.startsWith("SINK")) {

            } else if (substring.startsWith("MISS")) {

            } else if (substring.startsWith("GAME_OVER")) {
                //TODO end game logic
            }
        }

        else if (data.startsWith("ATTACK")) {

        }
    }
}
