package com.example.map.mapjfxdemo;

import com.sothawo.mapjfx.*;
import com.sothawo.mapjfx.event.MapLabelEvent;
import com.sothawo.mapjfx.event.MapViewEvent;
import com.sothawo.mapjfx.event.MarkerEvent;
import com.sothawo.mapjfx.offline.OfflineCache;
import javafx.animation.AnimationTimer;
import javafx.animation.Transition;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Controller {
    private ObservableList<Coordinate> observableList = FXCollections.observableArrayList();
    private static final Coordinate coordMoscow = new Coordinate(55.7514, 37.6171);
    private static final int ZOOM_DEFAULT = 10;
    private final Marker markerClick;

    @FXML
    private Button buttonZoom;

    @FXML
    private MapView mapView;

    @FXML
    private HBox topControls;

    @FXML
    private Slider sliderZoom;
    /**
     * the first CoordinateLine
     */
    private CoordinateLine trackMagenta;
    /**
     * the second CoordinateLine
     */
    private CoordinateLine trackCyan;
    /**
     * Coordinateline for polygon drawing.
     */
    private CoordinateLine polygonLine;
    /**
     * Check Button for polygon drawing mode.
     */
    @FXML
    private CheckBox checkDrawPolygon;

    @FXML
    private TableView<Coordinate> coordinateTable;

    @FXML
    private TableColumn<Coordinate, Double> latitudeColumn;

    @FXML
    private TableColumn<Coordinate, Double> longitudeColumn;
    @FXML
    private Button clearPolygon;

    public Controller() {
        markerClick = Marker.createProvided(Marker.Provided.ORANGE).setVisible(false);

    }

    public void initMapAndControls(Projection projection) {

        final OfflineCache offlineCache = mapView.getOfflineCache();

        final String cacheDir = System.getProperty("java.io.tmpdir") + "/mapjfx-cache";

        setControlsDisable(true);

        buttonZoom.setOnAction(event -> mapView.setZoom(ZOOM_DEFAULT));
        sliderZoom.valueProperty().bindBidirectional(mapView.zoomProperty());

        afterMapIsInitialized();

        MapType mapType = MapType.OSM;
        mapType = MapType.OSM;
        setupEventHandlers();

        ChangeListener<Boolean> polygonListener =
                (observable, oldValue, newValue) -> {
                    if (!newValue && polygonLine != null) {
                        mapView.removeCoordinateLine(polygonLine);
                        polygonLine = null;
                    }
                };
        checkDrawPolygon.selectedProperty().addListener(polygonListener);

        mapView.initialize(Configuration.builder()
                .projection(projection)
                .showZoomControls(false)
                .build());
    }

    public void fillCoordinateTable() {
        latitudeColumn.setCellValueFactory(new PropertyValueFactory<Coordinate, Double>("latitude"));
        longitudeColumn.setCellValueFactory(new PropertyValueFactory<Coordinate, Double>("longitude"));
        coordinateTable.setItems(observableList);
    }

    private void setupEventHandlers() {
        clearPolygon.setOnAction(x -> {
            observableList.clear();
            fillCoordinateTable();
            checkDrawPolygon.setSelected(false);
        });


        mapView.addEventHandler(MapViewEvent.MAP_CLICKED, event -> {
            event.consume();
            final Coordinate newPosition = event.getCoordinate().normalize();
            observableList.add(newPosition);
            fillCoordinateTable();

            Boolean bool = checkDrawPolygon.isSelected();
            if (bool) {
                handlePolygonClick(event);
            } else {
                observableList.clear();
                fillCoordinateTable();
            }
        });
    }

    private void handlePolygonClick(MapViewEvent event) {
        final List<Coordinate> coordinates = new ArrayList<>();
        if (polygonLine != null) {
            polygonLine.getCoordinateStream().forEach(coordinates::add);
            mapView.removeCoordinateLine(polygonLine);
            polygonLine = null;
        }
        coordinates.add(event.getCoordinate());
        polygonLine = new CoordinateLine(coordinates)
                .setColor(Color.DODGERBLUE)
                .setFillColor(Color.web("lawngreen", 0.4))
                .setClosed(true);
        mapView.addCoordinateLine(polygonLine);
        polygonLine.setVisible(true);
    }

    private void setControlsDisable(boolean flag) {
        topControls.setDisable(flag);
    }

    private void afterMapIsInitialized() {
        mapView.setZoom(ZOOM_DEFAULT);
        mapView.setCenter(coordMoscow);
        setControlsDisable(false);
    }
}
