package com.rocketgit.controller;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.ResourceBundle;
import org.controlsfx.control.NotificationPane;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class MainController {

    @FXML
    TreeView<String> treeViewRepoList;
   
    
    @FXML
    StackPane stack_pane;
    
    @FXML
    BorderPane root;

    @FXML
    public void initialize() {
    	// 
        openTreeView();
        initRepoList();
    }

    public void exit(ActionEvent actionEvent) {
        Platform.exit();
    }
    
    

    // TODO Remove dummy data
    private void initRepoList() {
        treeViewRepoList.setEditable(false);
        TreeItem<String> root = new TreeItem<>();
        FontAwesomeIconView icon = new FontAwesomeIconView();
        icon.setGlyphName("GITHUB");
        root.setGraphic(icon);
        root.setValue("GitHub");
        for (int i = 0; i < 7; i++) {
            FontAwesomeIconView itemIcon = new FontAwesomeIconView();
            itemIcon.setGlyphName("BOOK");
            root.getChildren().add(new TreeItem<>("Repo " + i, itemIcon ));
        }
        root.setExpanded(true);
        
        
        treeViewRepoList.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent mouseEvent)
            {         
            	TreeItem<String> item = treeViewRepoList.getSelectionModel().getSelectedItem();                
                openTreeView();
                
            }
        });
        treeViewRepoList.setRoot(root);
    }
            
    
    public void setView(Node node) {
    	stack_pane.getChildren().setAll(node);
    }
    
    public void loadVista(String fxml) throws IOException {
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
    }
    
    
    public void openConfig(ActionEvent actionEvent)  {    	
    	try {
			loadVista("config.fxml");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}   	
    }
    
    
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
        	Alert alert = new Alert(Alert.AlertType.NONE);
            alert.setTitle("New Git Repository");
            
        	DialogPane dialog = alert.getDialogPane();
        	dialog.setContent(custom);
        	
        	Window window = alert.getDialogPane().getScene().getWindow();
            window.setOnCloseRequest(event -> window.hide());
            alert.showAndWait();    
    		
    	} catch(IOException e) {
    		e.printStackTrace();
    	}
    }
    
    public void openTreeView() {
    	try {
			loadVista("tree.fxml");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
    }
}
