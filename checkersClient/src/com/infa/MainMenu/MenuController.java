package com.infa.mainMenu;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.infa.game.GameController;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MenuController implements Initializable
{
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {

    }

    public  void hostGame(ActionEvent e) throws IOException
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../game/board.fxml"));
        Parent p = loader.load();

        GameController g = loader.getController();
        g.startNewGame();
        Stage s = (Stage)((Node)e.getSource()).getScene().getWindow();
        s.setScene(new Scene(p));
    }

    public void showServerSelectionScene(ActionEvent e) throws IOException
    {
        Parent p = FXMLLoader.load(getClass().getResource("../connectionScreen/serverList.fxml"));
        Stage s = (Stage)((Node)e.getSource()).getScene().getWindow();
        double oldw = s.getWidth();
        double oldh = s.getHeight();
        s.setScene(new Scene(p));
        s.setWidth(oldw);
        s.setHeight(oldh);
    }
}
