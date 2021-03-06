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
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Window;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.RemoteAddCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

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
    	// por ahora la informaci�n es dummy, se cambiar� este m�todo
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
            
    // Los cambios de pantalla solo se ir�n haciendo cambiando el stack_pane
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

    public void commitStaged() {
        if (treeController != null) {
            int size;
            try {
                size = treeController.git.status().call().getChanged().size();
            } catch (GitAPIException e) {
                e.printStackTrace();
                return;
            }

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(bundle.getString("commit_staged/error/title"));
            alert.setHeaderText(bundle.getString("commit_staged/error/header"));

            if (size > 0) {
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
                        alert.setContentText(bundle.getString("commit_staged/error/empty_message"));
                        alert.showAndWait();
                    }
                });
            } else {
                alert.setContentText(bundle.getString("commit_staged/error/no_added"));
                alert.showAndWait();
            }
        }
    }

    public void push() {
        if (treeController != null) try {
            String branch = treeController.git.getRepository().getBranch();
            try {
                Iterable<PushResult> results = treeController.git
                    .push()
                    // TODO: Remove hardcoded origin
                    .setRemote("origin")
                    .setRefSpecs(new RefSpec(branch + ":" + branch))
                    .call();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(bundle.getString("push/title"));

                if (results.iterator().next()
                    .getRemoteUpdates().iterator().next()
                    .getStatus() == RemoteRefUpdate.Status.UP_TO_DATE) {
                    alert.setHeaderText(null);
                    alert.setContentText(bundle.getString("push/up_to_date/message"));
                } else {
                    alert.setHeaderText(bundle.getString("push/success/header"));
                    alert.setContentText(bundle.getString("push/success/message"));
                }
                alert.showAndWait();
            } catch (GitAPIException g) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(bundle.getString("push/title"));
                alert.setHeaderText(bundle.getString("push/error/header"));
                alert.setContentText(bundle.getString("push/error/message"));
                alert.showAndWait();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pull() {
        if (treeController != null) {
            String branch;
            try {
                branch = treeController.git.getRepository().getBranch();
            } catch (IOException e) {
                branch = "master";
            }

            try {
                PullResult result = treeController.git
                    .pull()
                    .setRemoteBranchName(branch)
                    .setRemote("origin")
                    .call();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(bundle.getString("pull/title"));

                if (result.getMergeResult().getMergeStatus().toString().equals("Already-up-to-date")) {
                    alert.setHeaderText(null);
                    alert.setContentText(bundle.getString("pull/up_to_date/message"));
                } else if (result.isSuccessful()) {
                    alert.setHeaderText(bundle.getString("pull/success/header"));
                    alert.setContentText(bundle.getString("pull/success/message") + result.getFetchedFrom());
                } else {
                   alert.setAlertType(Alert.AlertType.ERROR);
                   alert.setHeaderText(bundle.getString("pull/other/header"));
                   alert.setContentText(result.toString());
                }

                alert.showAndWait();
            } catch (GitAPIException e) {
                e.printStackTrace();
            }
        }
    }

    public void branchCreate() {
        if (treeController != null) {
            TextInputDialog dialog = new TextInputDialog("");
            dialog.setTitle(bundle.getString("branch/title"));
            dialog.setHeaderText(bundle.getString("branch/create/header"));

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(branch -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(bundle.getString("branch/title"));

                if (branch.length() > 0) {
                    try {
                        Ref ref = treeController.git
                            .branchCreate()
                            .setName(branch)
                            .call();
                        alert.setHeaderText(bundle.getString("branch/success/header"));
                        alert.setContentText(
                            String.format("%s %s (%s)",
                                bundle.getString("branch/create/success/message"),
                                branch,
                                ref.getObjectId().abbreviate(6).name()
                            )
                        );
                    } catch (RefAlreadyExistsException raee) {
                        alert.setAlertType(Alert.AlertType.ERROR);
                        alert.setHeaderText(bundle.getString("branch/error/header"));
                        alert.setContentText(bundle.getString("branch/error/existing"));
                    } catch (GitAPIException e) {
                        alert.setHeaderText(bundle.getString("branch/error/header"));
                        alert.setContentText(e.toString());
                    }
                } else {
                    alert.setContentText(bundle.getString("branch/error/empty_name"));
                }
                alert.showAndWait();
            });
        }
    }

    public void branchDelete() {
        if (treeController != null) {
            TextInputDialog dialog = new TextInputDialog("");
            dialog.setTitle(bundle.getString("branch/title"));
            dialog.setHeaderText(bundle.getString("branch/delete/header"));

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(branch -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(bundle.getString("branch/title"));

                if (branch.length() > 0) {
                    try {
                        treeController.git
                            .branchDelete()
                            .setBranchNames(branch)
                            .setForce(true)
                            .call();

                        alert.setHeaderText(bundle.getString("branch/success/header"));
                        alert.setContentText(
                            String.format("%s %s",
                                bundle.getString("branch/delete/success/message"),
                                branch
                            )
                        );
                    } catch (GitAPIException e) {
                        alert.setHeaderText(bundle.getString("branch/error/header"));
                        alert.setContentText(e.toString());
                    }
                } else {
                    alert.setContentText(bundle.getString("branch/error/empty_name"));
                }
                alert.showAndWait();
            });
        }
    }

    public void branchCheckout() {
        if (treeController != null) {
            TextInputDialog dialog = new TextInputDialog("");
            dialog.setTitle(bundle.getString("branch/title"));
            dialog.setHeaderText(bundle.getString("branch/checkout/header"));

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(branch -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(bundle.getString("branch/title"));

                if (branch.length() > 0) {
                    try {
                        treeController.git
                            .checkout()
                            .setName(branch)
                            .call();

                        alert.setHeaderText(bundle.getString("branch/success/header"));
                        alert.setContentText(
                            String.format("%s '%s'",
                                bundle.getString("branch/checkout/success/message"),
                                branch
                            )
                        );
                    } catch (CheckoutConflictException e) {
                        alert.setHeaderText(bundle.getString("branch/error/header"));
                        alert.setContentText(bundle.getString("branch/error/checkout_conflict"));
                    } catch (GitAPIException e) {
                        alert.setHeaderText(bundle.getString("branch/error/header"));
                        alert.setContentText(e.toString());
                    }
                } else {
                    alert.setContentText(bundle.getString("branch/error/empty_name"));
                }
                alert.showAndWait();
            });
        }
    }

    public void listRemotes() {
        if (treeController != null) {
            try {
                Collection<RemoteConfig> remotes = treeController.git.remoteList().call();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(bundle.getString("remote_list/title"));

                if (remotes.size() > 0) {
                    alert.setHeaderText(bundle.getString("remote_list/header"));

                    VBox customContent = new VBox();
                    customContent.setSpacing(5);

                    remotes.forEach(config -> {
                        Text remoteName = new Text(String.format("%s", config.getName()));
                        remoteName.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

                        Text remoteFetch = new Text(String.format("Fetch:\t%s", config.getURIs().get(0)));
                        Text remotePush = new Text(String.format("Push:\t%s", config.getURIs().get(0)));

                        customContent.getChildren().addAll(remoteName, remoteFetch, remotePush, new Text(""));
                        alert.getDialogPane().setGraphic(null);
                        alert.getDialogPane().setContent(customContent);
                    });
                } else {
                    alert.setHeaderText(null);
                    alert.setContentText(bundle.getString("remote_list/no_remotes"));
                }

                alert.showAndWait();
            } catch (GitAPIException e) {
                e.printStackTrace();
            }
        }
    }

    public void addRemote() {
        if (treeController != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(bundle.getString("status/title"));
            alert.setHeaderText(bundle.getString("status/header"));

            VBox customContent = new VBox();
            customContent.setSpacing(10);

            Label nameLabel, urlLabel;
            nameLabel = new Label("Name:");
            urlLabel = new Label("URL:");

            HBox nameContent, urlContent;
            nameContent = new HBox();
            urlContent = new HBox();
            nameContent.setSpacing(5);
            urlContent.setSpacing(5);

            TextField nameTextField, urlTextField;
            nameTextField = new TextField();
            urlTextField = new TextField();

            nameContent.getChildren().addAll(nameLabel, nameTextField);
            urlContent.getChildren().addAll(urlLabel, urlTextField);

            customContent.getChildren().addAll(nameContent, urlContent);

            alert.getDialogPane().setContent(customContent);
            alert.getDialogPane().setGraphic(null);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                String name = nameTextField.getText();
                String url = urlTextField.getText();
                RemoteAddCommand command = treeController.git.remoteAdd();

                if (name.length() > 0) {
                    URIish urIish;
                    try {
                        urIish = new URIish(url);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        return;
                    }

                    command.setName(name);
                    command.setUri(urIish);
                    try {
                        command.call();
                    } catch (GitAPIException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
