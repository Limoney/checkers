package com.infa.connectionScreen;

import com.infa.game.GameController;
import com.infa.network.Connection;
import com.infa.network.Packet;
import game.Board;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ServerListController implements Initializable
{
    @FXML private  TextField directConnect;
    @FXML private Button dcJoinButton;
    @FXML private StackPane notificationPanel;
    @FXML private StackPane serverTableWrapper;
    @FXML private GridPane directConnectWrapper;

    private Connection connection;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        TableColumn<Integer, Integer> roomid = new TableColumn<Integer, Integer>("Room ID");
        roomid.setCellValueFactory(new PropertyValueFactory<Integer,Integer>("value"));
        ListView tab = (ListView) ((AnchorPane)notificationPanel.lookup("#roomSelectionWrapper")).lookup("#roomList");
        //tab.getColumns().add(roomid);
        Integer a = 1;

    }

    protected void finalize() throws Throwable
    {
        connection.close();
    }

    public void showServerSelectionScene(ActionEvent e) throws IOException
    {
        Parent p = FXMLLoader.load(getClass().getResource("../mainMenu/mainMenu.fxml"));
        Stage s = (Stage)((Node)e.getSource()).getScene().getWindow();
        s.setScene(new Scene(p));
    }

    public void join(ActionEvent e) throws IOException
    {
        String ip = directConnect.getText();
        if(!ip.matches("\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3}:\\d+"))//check for invalid ip
        {
            makeNotification("invalid ip or port");
            return;
        }
        int pos = ip.lastIndexOf(':');
        int port = Integer.parseInt(ip.substring(pos+1));
        ip=ip.substring(0,pos);

//        FXMLLoader loader = new FXMLLoader(getClass().getResource("../game/board.fxml"));
//        Parent p = loader.load();
//
//        GameController g = loader.getController();
//        if(!g.initializeNetwork())
//        {
//            loader=null;
//            g=null;
//            makeNotification("unable to access server.");
//            return;
//        }
        notificationPanel.setVisible(true);
        connection = new Connection();
        connection.connect(ip,port);
        this.refreshRoomList(null);
//        g.startNewGame();
//        Stage s = (Stage)((Node)e.getSource()).getScene().getWindow();
//        s.setScene(new Scene(p));
    }

    private void makeNotification(String message)
    {
        serverTableWrapper.setDisable(true);
        notificationPanel.setVisible(true);
        directConnectWrapper.setDisable(true);
        for (Node n : notificationPanel.getChildren())
        {
            if(n.lookup("#notificationWrapper")==null)
            {
                n.setVisible(false);
            }
            else n.setVisible(true);
        }
        ((Label)notificationPanel.lookup("#notification")).setText(message);
        //(notificationPanel.lookup("#test")).setOpacity(0.5);
    }

    public void hideNotification(ActionEvent actionEvent)
    {
        directConnectWrapper.setDisable(false);
        serverTableWrapper.setDisable(false);
        //((Label)notificationPanel.lookup("#notification")).setVisible(false);
        notificationPanel.setVisible(false);
    }

    public void showRoomList(ArrayList<Integer> roomIDs)
    {
        System.out.println("hello?");
        notificationPanel.lookup("#roomSelectionWrapper").setVisible(true);
        ListView roomlist = (ListView) ((AnchorPane)notificationPanel.lookup("#roomSelectionWrapper")).lookup("#roomList");
        roomlist.getItems().clear();
        for(int i : roomIDs)
        {
//            tab.getItems().add(i);//fixme https://www.youtube.com/watch?v=O4BHfZQlSbs
            roomlist.getItems().add("Room "+i);
        }
        System.out.println("done");
    }

    public void closeRoomListMenu(ActionEvent actionEvent)
    {
        notificationPanel.setVisible(false);
        notificationPanel.lookup("#roomSelectionWrapper").setVisible(false);
        connection.close();
    }

    public void createRoom(ActionEvent actionEvent)
    {
        Packet p = new Packet(Packet.HEADER_REQUEST_NEW_ROOM,null);
        connection.send(p);
        //here create thread and wait for srv response
        Platform.runLater(new Thread()
        {
            @Override
            public void run()
            {
                if(!connection.isConnected())
                {
                    makeNotification("Cannot connect to the server");
                    return;
                }
                boolean run=true;
                while(run)
                {
                    if(connection.isDataReady())
                    {
                        Board board = (Board)connection.getRecivedData().getData();
                        try
                        {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("../game/board.fxml"));
                            Parent p = loader.load();
                            GameController g = loader.getController();
                            g.setConnection(connection);
                            g.setBoard(board);
                            g.startNewGame();
                            connection=null;
                            Stage s = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
                            s.setScene(new Scene(p));
                        }
                        catch(Exception ex)
                        {
                            ex.printStackTrace();
                        }
                        run=false;
                    }
                }
            }
        });
    }

    public void refreshRoomList(ActionEvent actionEvent)
    {
        Platform.runLater(new Thread()
        {
            @Override
            public void run()
            {
                if(!connection.isConnected())
                {
                    makeNotification("Cannot connecto to the server");
                    return;
                }
                connection.send(new Packet(Packet.HEADER_REQUEST_ROOM_LIST,null));
                boolean run=true;
                while(run)
                {
                    if(connection.isDataReady())
                    {
                        showRoomList((ArrayList<Integer>)connection.getRecivedData().getData());
                        run=false;
                    }
                }
            }
        });
    }

    public void joinRoom(ActionEvent actionEvent)
    {
        Platform.runLater(new Thread()
        {
            @Override
            public void run()
            {
                if(!connection.isConnected())
                {
                    makeNotification("Cannot connecto to the server");
                    return;
                }
                ListView roomlist = (ListView) ((AnchorPane)notificationPanel.lookup("#roomSelectionWrapper")).lookup("#roomList");
                String tmp = (String)roomlist.getSelectionModel().getSelectedItem();
                if(tmp.equals("") || tmp==null) return;
                Integer roomid =  Integer.parseInt(tmp.substring(tmp.indexOf(' ')+1));
                connection.send(new Packet(Packet.HEADER_REQUEST_JOIN_ROOM,roomid));
                boolean run=true;
                while(run)
                {
                    if(connection.isDataReady())
                    {
                        Board board = (Board)connection.getRecivedData().getData();
                        try
                        {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("../game/board.fxml"));
                            Parent p = loader.load();
                            GameController g = loader.getController();
                            g.setConnection(connection);
                            g.setBoard(board);
                            g.startNewGame();
                            connection=null;
                            Stage s = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
                            s.setScene(new Scene(p));
                        }
                        catch(Exception ex)
                        {
                            ex.printStackTrace();
                        }
                        run=false;
                    }
                }
            }
        });
    }

    public void setConnection(Connection connection)
    {
        this.connection = connection;
    }
}
