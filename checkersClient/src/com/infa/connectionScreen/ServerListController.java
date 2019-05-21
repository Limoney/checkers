package com.infa.connectionScreen;

import com.infa.Network.Connection;
import com.infa.Network.Packet;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {

    }

    public void showServerSelectionScene(ActionEvent e) throws IOException
    {
        Parent p = FXMLLoader.load(getClass().getResource("../MainMenu/mainMenu.fxml"));
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

        Connection c = new Connection();
        c.connect(ip,port);
        Platform.runLater(new Thread()
        {
            @Override
            public void run()
            {
                if(!c.isConnected())
                {
                    makeNotification("Cannot connecto to the server");
                    return;
                }
                c.send(new Packet(Packet.HEADER_REQUEST_ROOM_LIST,null));
                while(true)
                {
                    if(c.isDataReady())
                    {
                        showRoomList((ArrayList<Integer>)c.getRecivedData().getData());
                        break;
                    }
                }
            }
        });
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
        TableView tab = (TableView)((AnchorPane)notificationPanel.lookup("#roomSelectionWrapper")).lookup("#roomTable");
        for(int i : roomIDs)
        {
            tab.getColumns().add(i,"a");
        }
        System.out.println("done");
    }
}
