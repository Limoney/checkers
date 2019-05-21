package com.infa;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    static int defaultWindowWidth= 800;
    static int defaultWindowHight= 600;

    @Override
    public void start(Stage primaryStage) throws Exception{
        //Parent root = FXMLLoader.load(getClass().getResource("MainMenu/mainMenu.fxml"));
        Parent root = FXMLLoader.load(getClass().getResource("MainMenu/mainMenu.fxml"));
        primaryStage.setTitle("Hello World");
        Scene s =new Scene(root, defaultWindowWidth, defaultWindowHight);
        s.getStylesheets().add(getClass().getResource("css/main.css").toExternalForm());
        primaryStage.setScene(s);
        primaryStage.setMinHeight(480);
        primaryStage.setMinWidth(480);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
