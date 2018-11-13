package com.rocketgit.controller;

import com.rocketgit.components.IconMenuItem;
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
import javafx.scene.text.Text;
import javafx.stage.Window;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;

public class MainController {

    @FXML
    TreeView<com.rocketgit.objects.Repository> treeViewRepoList;

    TreeController treeController;
    ResourceBundle bundle;
    
    @FXML
    StackPane stack_pane;
    
    @FXML
    BorderPane root;

    @FXML
    IconMenuItem addToStageButton;

    @FXML
    public void initialize() {
        initRepoList();
        bundle = ResourceBundle.getBundle(
            "i18n.main",
            new Locale.Builder().setLanguage("en").build()
        );
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
			treeController = loadView("tree.fxml").getController();
			treeController.setRepo("Rocket.Git", path);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
    }


    // Git interaction methods

    public void showStatus() {
        if (treeController != null) {
            try {
                Status status = treeController.git.status().call();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(bundle.getString("status/title"));
                alert.setHeaderText(bundle.getString("status/header"));

                VBox customContent = new VBox();

                Set<String> added = status.getChanged();
                Set<String> uncommittedChanges = status.getModified();
                Set<String> untracked = status.getUntracked();

                Text addedText = new Text(bundle.getString("status/added"));
                addedText.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
                customContent.getChildren().add(addedText);
                added.forEach(line -> customContent.getChildren().add(new Text(line)));
                customContent.getChildren().add(new Text(""));

                Text modifiedText = new Text(bundle.getString("status/modified"));
                modifiedText.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
                customContent.getChildren().add(modifiedText);
                uncommittedChanges.forEach(line -> customContent.getChildren().add(new Text(line)));
                customContent.getChildren().add(new Text(""));

                Text untrackedText = new Text(bundle.getString("status/untracked"));
                untrackedText.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
                customContent.getChildren().add(untrackedText);
                untracked.forEach(line -> customContent.getChildren().add(new Text(line)));
                customContent.getChildren().add(new Text(""));

                alert.getDialogPane().setContent(customContent);
                alert.getDialogPane().setGraphic(null);
                alert.showAndWait();
            } catch (GitAPIException e) {
                e.printStackTrace();
            }
        }
    }

    public void addToStage() {
        if (treeController != null) {
            try {
                Status status = treeController.git.status().call();

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle(bundle.getString("add_to_stage_dialog/title"));
                alert.setHeaderText(bundle.getString("add_to_stage_dialog/header"));
                VBox customContent = new VBox();
                status.getModified().forEach(line -> customContent.getChildren().add(new Label(line)));
                alert.getDialogPane().setContent(customContent);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    treeController.git.add().addFilepattern(".").call();
                }
            } catch (GitAPIException e) {
                e.printStackTrace();
            }
        }
    }

    public void commitStaged(ActionEvent actionEvent) {
        if (treeController != null) {
            TextInputDialog dialog = new TextInputDialog("");
            dialog.setTitle(bundle.getString("commit_staged/title"));
            dialog.setHeaderText(bundle.getString("commit_staged/header"));

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(message -> {
                if (message.length() > 0) {
                    try {
                        treeController.git.commit().setAll(true).setMessage(message).call();
                    } catch (GitAPIException e) {
                        e.printStackTrace();
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle(bundle.getString("commit_staged/error/title"));
                    alert.setHeaderText(bundle.getString("commit_staged/error/header"));
                    alert.setContentText(bundle.getString("commit_staged/error/message"));
                    alert.showAndWait();
                }
            });
        }
    }
}
