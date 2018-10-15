package com.rocketgit.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

// Controller de la tabla 
public class TreeController {


    @FXML
    public void initialize() {
    }

    public void exit(ActionEvent actionEvent) {
        Platform.exit();
    }


}