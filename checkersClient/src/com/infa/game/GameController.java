package com.infa.game;

import com.infa.connectionScreen.ServerListController;
import com.infa.network.Connection;
import com.infa.network.Packet;
import com.sun.security.ntlm.Server;
import game.Board;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;

public class GameController implements Initializable
{
    @FXML private Canvas canvas;
    @FXML private TextField chatInput;
    @FXML private TextArea chatMessages;
    @FXML private AnchorPane chatWrapper;
    @FXML private AnchorPane windowFrame;
    @FXML private AnchorPane nav;
    @FXML private StackPane notificationPanel;

    private int boardSize = 8;
    private ArrayList<Tile> tiles;
    private ArrayList<Pawn> pawns;
    private Board board;
    private GameState gameState;
    private Connection connectionRef;

    //network stuff
    //Connection connection;

    AnimationTimer animation;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        chatMessages.setWrapText(true);
        ChangeListener<Number> resize =(observable, oldValue, newValue) -> resizeBoard();
        windowFrame.widthProperty().addListener(resize);
        windowFrame.heightProperty().addListener(resize);

    }

    protected void finalize() throws Throwable
    {
        System.out.println("Game Controller destructed");
    }

//    public void newGame(ActionEvent e)
//    {
//        leaveGame(e);
//        chatMessages.setText("");
//        chatInput.setText("");
//        Parent p = null;
//        try
//        {
//            p = FXMLLoader.load(getClass().getResource("../connectionScreen/serverList.fxml"));
//            Stage s = (Stage)((Node)e.getSource()).getScene().getWindow();
//            s.setScene(new Scene(p));
//        }
//        catch (IOException exc)
//        {
//            exc.printStackTrace();
//        }
//    }

    public void startNewGame()
    {
        board.show();
        chatInput.setText("");
        chatMessages.setText("");
        gameState = GameState.HAS_NOT_STARTED_YET;
        makeNotification("waiting for player");
        generateBoard();
        final long startNanoTime = System.nanoTime();
        //main game loop
        animation =new AnimationTimer()
        {
            public void handle(long currentNanoTime)
            {
                double t = (currentNanoTime - startNanoTime) / 1000000000.0;
                updateNetwork();
                updateGraphics();
            }
        };
        animation.start();
    }

    private void resizeUI()
    {
        //System.out.println(windowFrame.getWidth()+" "+windowFrame.getPrefHeight());
        double canvasw = windowFrame.getWidth() - chatWrapper.getPrefWidth();
        double canvash = windowFrame.getHeight() - nav.getPrefHeight();
        canvas.setWidth(canvasw);
        canvas.setHeight(canvash);

        double chmh = windowFrame.getHeight() - nav.getHeight() - chatInput.getHeight();
        chatWrapper.setPrefHeight(chmh);
        //System.out.println("pos "+ canvas.getLayoutX() + "  "+canvas.getLayoutY());
    }

    private void generateBoard()
    {
        //resizeUI();
        tiles = new ArrayList<Tile>();
        pawns = new ArrayList<Pawn>();
        //pane.getChildren().removeAll();
        //System.out.println("pos g"+ canvas.getLayoutX() + "  "+canvas.getLayoutY());
        double tileWidth = canvas.getWidth() / boardSize;
        double tileHight = canvas.getHeight() / boardSize;
        {
            double tmp = Math.min(tileWidth,tileHight);
            tileHight = tmp;
            tileWidth = tmp;
        }

        Pawn.setTileWidth(tileWidth);
        Pawn.setTileHeight(tileHight);

        double emptyX = canvas.getWidth() - 8*tileWidth;
        emptyX /=2;
        double emptyY = canvas.getHeight() - 8*tileHight;
        emptyY /=2;

        for(int y=0;y<boardSize;y++)
        {
            for(int x=0;x<boardSize;x++)
            {
                //generate Tiles
                Tile tile = new Tile(x*tileWidth + emptyX ,y*tileHight+emptyY,tileWidth,tileHight,((x+y*boardSize+y) %2==0) ? Color.WHITE : Color.BLACK,x+y*boardSize);
                tiles.add(tile);
                System.out.print(x+y*boardSize+" ");

                //generate Pawns
                if(board.getBoard().get(x+y*board.getSize())==1)
                {
                    double margin = (tileWidth - tileWidth*0.8f)/2 ;
                    Pawn p = new Pawn(x*(float)tileWidth + (float)emptyX + (float)margin,y * (float)tileHight + (float)emptyY + (float)margin,Color.GAINSBORO);
                    p.setRadius(tileWidth*0.8f);
                    pawns.add(p);
                    tile.setOwner(p);
                }
                else if(board.getBoard().get(x+y*board.getSize())==2)
                {
                    double margin = (tileWidth - tileWidth*0.8f)/2 ;
                    Pawn p = new Pawn(x*(float)tileWidth + (float)emptyX + (float)margin,y * (float)tileHight + (float)emptyY + (float)margin,Color.DARKGRAY);
                    p.setRadius(tileWidth*0.8f);
                    pawns.add(p);
                    tile.setOwner(p);
                }
            }
            System.out.print("\n");
        }
    }


    private void resizeBoard()
    {
        resizeUI();
        if(board==null) return;
        double tileWidth = canvas.getWidth() / boardSize;
        double tileHight = canvas.getHeight() / boardSize;
        {
            double tmp = Math.min(tileWidth,tileHight);
            tileHight = tmp;
            tileWidth = tmp;
        }

        Pawn.setTileWidth(tileWidth);
        Pawn.setTileHeight(tileHight);


        //System.out.println(windowFrame.getWidth()+" "+windowFrame.getHeight() + "   "+chatWrapper.getWidth());
        double emptyX = canvas.getWidth() - 8*tileWidth;
        emptyX /=2;
        double emptyY = canvas.getHeight() - 8*tileHight;
        emptyY /=2;


        for(int y=0;y<boardSize;y++)
        {
            for(int x=0;x<boardSize;x++)
            {
                //generate Tiles
                Tile tmp = tiles.get(x + y * 8);
                tmp.setX(x * tileWidth + emptyX);
                tmp.setY(y * tileHight + emptyY);
                tmp.setWidth(tileWidth);
                tmp.setHeight(tileHight);
                if(tmp.hasOwner())
                {
                    Pawn owner = tmp.getOwner();
                    owner.setRadius(tileWidth *0.8);
                    double margin = (tileWidth - owner.getRadius())/2 ;
                    owner.setX(x * (float) tileWidth+ emptyX + margin);
                    owner.setY(y * (float) tileHight + emptyY + margin);
                    owner.resize();
                }
            }
        }
    }

    public void checkClick(MouseEvent mouseEvent)
    {
        double mouseX = mouseEvent.getX();
        double mouseY = mouseEvent.getY();
        Iterator<Pawn> pawnIterator = pawns.iterator();
        while ( pawnIterator.hasNext() )
        {
            Pawn pawn = pawnIterator.next();
            pawn.isSelected(mouseX,mouseY);
        }
    }


    public void sendMessage(ActionEvent actionEvent)
    {
        String m = chatInput.getText();
        chatMessages.appendText(m+"\n");
        Packet p = new Packet(Packet.HEADER_CHAT_MESSAGE,m);
        connectionRef.send(p);
        chatInput.setText("");
    }

    public void leaveGame(ActionEvent actionEvent)
    {
        Packet pt = new Packet(Packet.HEADER_REQUEST_LEAVE_ROOM,null);
        connectionRef.send(pt);
        try
        {
            gameState = GameState.HAS_ENDED;
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../connectionScreen/serverList.fxml"));
            Parent p = loader.load();
            ServerListController g = loader.getController();
            connectionRef=null;
            Stage s = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
            s.setScene(new Scene(p));
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void makeNotification(String message)
    {
        chatInput.setDisable(true);
        canvas.setDisable(true);
        notificationPanel.setVisible(true);
        ((Label)notificationPanel.getChildren().get(0)).setText(message);
    }

    private void hideNotifications()
    {
        chatInput.setDisable(false);
        canvas.setDisable(false);
        notificationPanel.setVisible(false);
    }

    private void updateNetwork()
    {
        //fixme z kontrolera poprzedniego musze wiedzieć czy hostuje czy nie i zacząć też grę u gracza 2  :c
        switch (gameState)
        {
            case HAS_NOT_STARTED_YET:
            {
                try
                {
                    if(connectionRef.isDataReady() && connectionRef.getRecivedData().checkHeader(Packet.HEADER_RESPONSE_USER_HAS_JOINED) && gameState!=GameState.PLAYING)
                    {
                        hideNotifications();
                        //startNewGame();
                        //here start game. i need to come up with better names
                        gameState = GameState.PLAYING;
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                break;
            }
            case PLAYING:
            {
                try
                {
                    if(connectionRef.isDataReady())
                    {
                        if(connectionRef.getRecivedData().checkHeader(Packet.HEADER_RESPONSE_USER_HAS_LEFT))
                        {
                            makeNotification("Player has left the game. You win!");
                            gameState = GameState.HAS_ENDED;
                            System.out.println("he left");
                        }
                        else if(connectionRef.getRecivedData().checkHeader(Packet.HEADER_CHAT_MESSAGE))
                        {
                            chatMessages.appendText((connectionRef.getRecivedData().getData()+"\n"));
                            connectionRef.awaitNewData();
                        }
                    }

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            break;
        }
    }

    private void updateGraphics()
    {
        Iterator<Tile> tileIterator = tiles.iterator();
        canvas.getGraphicsContext2D().clearRect(0,0, canvas.getWidth(),canvas.getHeight());
        while ( tileIterator.hasNext() )
        {
            Tile tile = tileIterator.next();
            tile.update();
            tile.draw(canvas.getGraphicsContext2D());
        }
        Iterator<Pawn> pawnIterator = pawns.iterator();
        while ( pawnIterator.hasNext() )
        {
            Pawn pawn = pawnIterator.next();
            pawn.update();
            pawn.draw(canvas.getGraphicsContext2D());
        }
    }

    public void highlightTile(Pawn pawn)
    {
        //if(pawn.getX()>0 && pawn.getX() + sth < canvas.getWidth())
    }

    public void setConnection(Connection c)
    {
        this.connectionRef = c;
    }

    public void setBoard(Board b){this.board = b;}
}
