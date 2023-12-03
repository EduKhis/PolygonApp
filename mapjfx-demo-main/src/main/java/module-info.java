module com.example.map.mapjfxdemo {
    requires com.sothawo.mapjfx;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens com.example.map.mapjfxdemo to javafx.fxml, javafx.graphics;
}
