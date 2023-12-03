package com.example.map.mapjfxdemo;

import com.sothawo.mapjfx.Projection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DemoApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        String fxmlFile = "/fxml/DemoApp.fxml";
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent rootNode = fxmlLoader.load(getClass().getResourceAsStream(fxmlFile));

        final Controller controller = fxmlLoader.getController();
        final Projection projection = getParameters().getUnnamed().contains("wgs84")
                ? Projection.WGS_84 : Projection.WEB_MERCATOR;
        controller.initMapAndControls(projection);

        Scene scene = new Scene(rootNode);
        primaryStage.setTitle("Stupor app");
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
