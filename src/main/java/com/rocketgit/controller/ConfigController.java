package com.rocketgit.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import org.controlsfx.control.ToggleSwitch;
import javafx.application.Platform;
import javafx.event.ActionEvent;

public class ConfigController {
	
	@FXML
    BorderPane border_pane;
	
	@FXML
	private Button config_branch_open;
	@FXML
	private Button config_diff_open;
	@FXML
	private Button config_decorate_open;
	@FXML
	private Button config_interactive_open;
	@FXML
	private Button config_status_open;
	
	@FXML
	private GridPane config_branch_grid;
	@FXML
	private GridPane config_diff_grid;
	@FXML
	private GridPane config_decorate_grid;
	@FXML
	private GridPane config_interactive_grid;
	@FXML
	private GridPane config_status_grid;
	
	@FXML
	private ChoiceBox<String> config_scope;
	
	@FXML
	private ToggleSwitch config_ui;
	
	@FXML
	TreeView<String> treeViewRepoList;

	@FXML
	public void initialize() {
		initRepoList();
	}

	public void exit(ActionEvent actionEvent) {
		Platform.exit();
	}

		// TODO Remove dummy data
	private void initRepoList() {
		// Configuraciones iniciales
	    config_scope.getItems().addAll("Local", "Global", "System");
	    initGrid();
	}
	
	
	public void initGrid() {
		// Para que todos los grid estén ocultos
		config_branch_grid.setVisible(false);
		config_branch_grid.managedProperty().bind(config_branch_grid.visibleProperty());
		
		config_diff_grid.setVisible(false);
		config_diff_grid.managedProperty().bind(config_diff_grid.visibleProperty());
		
		config_decorate_grid.setVisible(false);
		config_decorate_grid.managedProperty().bind(config_decorate_grid.visibleProperty());
		
		config_interactive_grid.setVisible(false);
		config_interactive_grid.managedProperty().bind(config_interactive_grid.visibleProperty());
		
		config_status_grid.setVisible(false);
		config_status_grid.managedProperty().bind(config_status_grid.visibleProperty());
	}

	@FXML
	private void collapseBranch(ActionEvent event) {
		collapseOrExpand(config_branch_open, config_branch_grid);	
	}
	
	@FXML
	private void collapseDecorate(ActionEvent event) {
		collapseOrExpand(config_decorate_open, config_decorate_grid);
	}
	
	@FXML
	private void collapseDiff(ActionEvent event) {
		collapseOrExpand(config_diff_open, config_diff_grid);
	}
	
	@FXML
	private void collapseInteractive(ActionEvent event) {
		collapseOrExpand(config_interactive_open, config_interactive_grid);
	}
	
	@FXML
	private void collapseStatus(ActionEvent event) {
		collapseOrExpand(config_status_open, config_status_grid);
	}

	// Metodo para collapsar y expandir los colores 
	private static void collapseOrExpand(Button btn, GridPane grid) {
		if(btn.getStyleClass().contains("show")) {
			btn.getStyleClass().remove("show");
			grid.setVisible(false);
			grid.managedProperty().bind(grid.visibleProperty());
			}
		else {
			btn.getStyleClass().add("show");
			grid.setVisible(true);
		}
	
	}
	
}
