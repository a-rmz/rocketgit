package com.rocketgit.controller;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainController {

    @FXML
    TreeView<com.rocketgit.objects.Repository> treeViewRepoList;
   
    
    @FXML
    StackPane stack_pane;
    
    @FXML
    BorderPane root;

    @FXML
    public void initialize() {
        initRepoList();
    }

    public void exit(ActionEvent actionEvent) {
        Platform.exit();
    }
    
    

    // TODO Remove dummy data
    private void initRepoList() {
    	// inicializamos la lista de repositorios
    	// por ahora la información es dummy, se cambiará este método
        treeViewRepoList.setEditable(false);
        treeViewRepoList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        TreeItem<com.rocketgit.objects.Repository> root = new TreeItem<>();
        root.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.GITHUB));
        root.setValue(new com.rocketgit.objects.Repository("GitHub", ""));

        root.getChildren()
            .add(new TreeItem<>(
                new com.rocketgit.objects.Repository("Rocket.Git", ".git"),
                new FontAwesomeIconView(FontAwesomeIcon.BOOK)
            ));

        root.setExpanded(true);

        // Agregamos el evento de la lista para que al ser presionados abra el nuevo fxml
        treeViewRepoList.setOnMouseClicked(mouseEvent -> {
            TreeItem<com.rocketgit.objects.Repository> item = treeViewRepoList.getSelectionModel().getSelectedItem();

            if (item != null && item.isLeaf()) {
                openTreeView(item.getValue().getPath());
            }
        });
        treeViewRepoList.setRoot(root);
    }
            
    // Los cambios de pantalla solo se irán haciendo cambiando el stack_pane
    public void setView(Node node) {
    	stack_pane.getChildren().setAll(node);
    }
    
    // Para cargar una nueva vista
    public FXMLLoader loadView(String fxml) throws IOException {
    	FXMLLoader loader = new FXMLLoader();
   	 
    	ResourceBundle rb = ResourceBundle.getBundle(
    				"i18n.main",
    				new Locale.Builder().setLanguage("en").build()
    				);
    	loader.setResources(rb);
    	loader.setCharset(Charset.forName("UTF-8"));
    	Node root = (loader.load(getClass().getClassLoader().getResource(fxml).openStream()));
    	root.setStyle("-fx-font-family: 'Comfortaa';");
    	setView(root);

    	return loader;
    }
    
    // Evento para abrir la configuracion 
    public void openConfig(ActionEvent actionEvent)  {    	
    	try {
			loadView("config.fxml");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}   	
    }
    
    // Evento para abrir el dialogo de new repo
    public void openNew(ActionEvent actionEvent) {
    	
    	try {

    		FXMLLoader loader = new FXMLLoader();
    	   	 
        	ResourceBundle rb = ResourceBundle.getBundle(
        				"i18n.main",
        				new Locale.Builder().setLanguage("en").build()
        				);
        	loader.setResources(rb);
        	loader.setCharset(Charset.forName("UTF-8"));
        	VBox custom = (loader.load(getClass().getClassLoader().getResource("init.fxml").openStream()));
        	// Se crea una alerta
        	Alert alert = new Alert(Alert.AlertType.NONE);
            alert.setTitle("New Git Repository");
            
            // Se hace un dialog pane
        	DialogPane dialog = alert.getDialogPane();
        	dialog.setContent(custom);
        	
        	// Se agrega a la ventana
        	Window window = alert.getDialogPane().getScene().getWindow();
            window.setOnCloseRequest(event -> window.hide());
            alert.showAndWait();    
    		
    	} catch(IOException e) {
    		e.printStackTrace();
    	}
    }
    
    // Evento para abrir el Tree
    public void openTreeView(String path) {
    	try {
			TreeController treeController = loadView("tree.fxml").getController();
			treeController.setRepo("Rocket.Git", path);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
    }
}
