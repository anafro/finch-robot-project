package ru.anafro.finch.finchrobotproject;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Line;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import static ru.anafro.finch.finchrobotproject.FinchStats.*;

public class WindowController implements Initializable {
    @FXML
    public ImageView Arrow;
    @FXML
    public Line distanceLine;
    @FXML
    public Arc arc;
    @FXML
    public ProgressBar progressBar;
    @FXML
    private Label labelLightLeft;
    @FXML
    private Label labelLightRight;
    @FXML
    ColorPicker BeakColorPicker;
    @FXML
    ColorPicker TailColorPicker;
    @FXML
    ColorPicker AllColorPicker;
    @FXML
    NumberAxis yAxis;
    @FXML
    CheckBox safeModeCheckBox;
    @FXML
    CheckBox switchLightCheckBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Timer timer = new Timer();
        timer.schedule(UpdateStatsUI, 0, 400);
        HookLightUI();
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(250);
        yAxis.setTickUnit(25);
        WindowApplication.windowController = this;
    }

    private final TimerTask UpdateStatsUI = new TimerTask() {
        @Override
        public void run() {
            double startLineY = 15;
            double distanceY = startLineY + 210 * (1 - ((double) finchDistance / 255));
            if(distanceY < startLineY)
                distanceY = startLineY;

            double finalDistanceY = distanceY;
            Platform.runLater(() -> {
                if (finchLightLeft >= 0)
                    labelLightLeft.setText("Light Left: " + finchLightLeft + "%");
                if (finchLightRight >= 0)
                    labelLightRight.setText("Light Right: " + finchLightRight + "%");

                if (finchAngle >= 0)
                    Arrow.setRotate((163 + finchAngle) % 360);
                if (finchDistance >= 0) {
                    distanceLine.setStartY(finalDistanceY);
                    distanceLine.setEndY(finalDistanceY);
                }
            });
        }
    };

    public void UpdateGearbox() {
        Platform.runLater(() -> {
            progressBar.setProgress(FinchController.speed / 100.0); //[0-1]
        });
    }

    public void HookLightUI() {
        safeModeCheckBox.selectedProperty().addListener(
                (ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> FinchController.safeMode = new_val);

        BeakColorPicker.setOnAction(actionEvent -> ColorController.SetFrontLight(BeakColorPicker.getValue()));

        TailColorPicker.setOnAction(actionEvent -> ColorController.SetBackLight(TailColorPicker.getValue()));

        AllColorPicker.setOnAction(actionEvent -> {
            Color CurrentColor = AllColorPicker.getValue();
            ColorController.SetBackLight(CurrentColor);
            ColorController.SetFrontLight(CurrentColor);
        });

        switchLightCheckBox.selectedProperty().addListener(
            (ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> ColorController.SwitchLight());
    }
}