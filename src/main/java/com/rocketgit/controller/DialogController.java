package com.rocketgit.controller;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class DialogController {
	
	Stage stage; 
	
	@FXML
	TextField directory;
	
	@FXML
	TextField name;
	
	@FXML
	TextField url;
	
	@FXML
	Button select;
	
	@FXML
	public void initialize() {
		
	}
	
	@FXML
	private void chooseDirectory() {
		final DirectoryChooser directoryChooser = new DirectoryChooser();
        final File selectedDirectory =  directoryChooser.showDialog(this.getStage());
        if (selectedDirectory != null) {
            directory.setText(selectedDirectory.getPath());
        }
	}

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
}
