package com.rocketgit.controller;

import com.rocketgit.Commons;
import com.rocketgit.components.IconMenuItem;
import com.rocketgit.database.DBQueryRepository;
import com.rocketgit.database.MyDataSourceFactory;
import com.rocketgit.objects.Repository;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.RemoteAddCommand;
import org.eclipse.jgit.api.RemoteRemoveCommand;
import org.eclipse.jgit.api.RemoteSetUrlCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.*;

public class MainController {
	
	private String language; 
	
	private Stage stage;
	
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
    
    
    public void setStage(Stage stage) {
    	this.stage = stage;
    }
    
    public void setLanguague(String lgn) {
    	this.language = lgn;
    	initializeBundle();
    }

    @FXML
    public void initialize() {
    	initializeBundle();
        initRepoList();
        
    }

    
    public void initializeBundle() {
    	bundle = ResourceBundle.getBundle(
                "i18n.main",
                new Locale.Builder().setLanguage(language).build()
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
        root.setValue(new com.rocketgit.objects.Repository("GitHub", "", 0));

        root.getChildren()
            .add(new TreeItem<>(
                new com.rocketgit.objects.Repository("Rocket.Git", ".git", 0),
                new FontAwesomeIconView(FontAwesomeIcon.BOOK)
            ));
        
        DBQueryRepository query = new DBQueryRepository(MyDataSourceFactory.getDataSource(Commons.MYSQL));
		ArrayList<Repository> repositories = query.getRepository();
		repositories.forEach(r -> {
			root.getChildren()
            .add(new TreeItem<>(
                r,
                new FontAwesomeIconView(FontAwesomeIcon.BOOK)
            ));
		});

        root.setExpanded(true);

        // Agregamos el evento de la lista para que al ser presionados abra el nuevo fxml
        treeViewRepoList.setOnMouseClicked(mouseEvent -> {
            TreeItem<com.rocketgit.objects.Repository> item = treeViewRepoList.getSelectionModel().getSelectedItem();

            if (item != null && item.isLeaf()) {
            	System.out.println(item.getValue().getName());
                openTreeView(item.getValue());
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
    	loader.setResources(bundle);
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
    
    // Evento para abrir el Tree
    public void openTreeView(Repository repository) {
    	try {
    		System.out.println(repository.getName());
			treeController = loadView("tree.fxml").getController();
			treeController.setRepo(repository);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
    }
    
    @FXML
    public void changeLanguage() throws IOException {
    	ArrayList<String> dialogData = new ArrayList<String>();
    	dialogData.add(bundle.getString("language/spanish"));
    	dialogData.add(bundle.getString("language/english"));
    	Dialog<String>dialog = new ChoiceDialog<String>(dialogData.get(0), dialogData);
		dialog.setTitle(bundle.getString("language/title"));
		dialog.setHeaderText(bundle.getString("language/header"));

		Optional<String> result = dialog.showAndWait();
		String selected = null;
				
		if (result.isPresent()) {

		    selected = result.get();
		    if(selected != null) {
		    	switch(selected) {
		    		case "Español":
		    		case "Spanish":
		    			language = "es";
			    		break;
		    		case "Ingles":
		    		case "English":
		    			language = "en";
			    		break;
		    		default:
		    			language = "en";
				    	break;
		    	}
		    }
		    this.initializeBundle();
	        FXMLLoader loader = new FXMLLoader();
	        loader.setResources(bundle);
	        loader.setCharset(Charset.forName("UTF-8"));
	        // Se carga la fuente que usaremos
	        try {
	            Font.loadFont(getClass().getClassLoader().getResource("Comfortaa-Regular.ttf").toExternalForm(), 13);
	        } catch (Exception e) {
	            e.printStackTrace();
	            System.exit(0);
	        }
	       
		    Parent root = loader.load(getClass().getClassLoader().getResource("main.fxml").openStream());
		    MainController controller = loader.getController();
	        
	        controller.setStage(stage);
	        controller.setLanguague(language);
	        
	        root.setStyle("-fx-font-family: 'Comfortaa';");
	        stage.setTitle(bundle.getString("title"));
	        stage.setScene(new Scene(root, 1000, 600));
	        stage.show();
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
    
    @FXML
    public void openInit() throws IllegalStateException, GitAPIException {
        Repository repo = openDialog("initrepo.fxml", 
        		bundle.getString("repo/init/header"),
        		bundle.getString("repo/init/title"), false);
        System.out.println(repo);
        if(repo != null) {
             try (Git git = Git.init().setDirectory(new File(repo.getPath())).call()) {
     			System.out.println("Call: Created repository: " + git.getRepository().getDirectory());
     			// Para apuntar a la carpeta .git
     			repo.setPath(repo.getPath() + "/.git");
     			this.saveRepository(repo);
             }	
        }
    }
    
    @FXML
    public void openImport() {
    	Repository repo = openDialog("initrepo.fxml",
    			bundle.getString("repo/import/header"), 
    			bundle.getString("repo/import/title"), false);
        System.out.println(repo);
        if(repo != null) {
        	repo.setPath(repo.getPath() + "/.git");
        	File filegit = new File(repo.getPath());
        	if(filegit.exists() && filegit.isDirectory()) {
     			this.saveRepository(repo);
        	} else this.errorAlert("No Git Repository", "There is not git repository in this root. Please try again with the correct directory.");
        } 
    }
    
    @FXML
    public void openClone() throws InvalidRemoteException, TransportException, GitAPIException {
    	Repository repo = openDialog("clone.fxml",
    			bundle.getString("repo/clone/header"), 
    			bundle.getString("repo/clone/title"), true);
        System.out.println(repo);
        if(repo != null) {
        	Git.cloneRepository().setURI(repo.getUrl()).setDirectory(new File(repo.getPath())).call();
        	System.out.println("Repositorio clonado correctamente");
        	repo.setPath(repo.getPath() + "/.git");
        	this.saveRepository(repo);
        }	
    	
    }
    
    @FXML
    public void updateName() {
    	if (treeController != null) {
    		//Alert alert = new Alert(AlertType.CONFIRMATION);
    		Dialog<String> dialog = new TextInputDialog(" ");
    		dialog.setTitle(bundle.getString("repo/update/title"));
    		dialog.setHeaderText(bundle.getString("repo/update/header"));

    		Optional<String> result = dialog.showAndWait();
    		String entered = null;

    		if (result.isPresent()) {

    		    entered = result.get();
    		    if(entered != null && entered != "") {    		  
            		treeController.repository.setName(entered);
            		DBQueryRepository query = new DBQueryRepository(MyDataSourceFactory.getDataSource(Commons.MYSQL));
    				query.updateNameRepository(treeController.repository);
    				treeViewRepoList.refresh();

    		    }
    		}
    		
    	}
    }
    
    @FXML
    public void deleteRepository() {
    	if (treeController != null) {
    		Alert alert = new Alert(AlertType.CONFIRMATION);
        	alert.setTitle(bundle.getString("repo/delete/title"));
        	alert.setContentText(bundle.getString("repo/delete/header/first") + 
        			treeController.treeRepoName.getText() + 
        			bundle.getString("repo/delete/header/second"));
        	Window window = alert.getDialogPane().getScene().getWindow();
            window.setOnCloseRequest(event -> window.hide());
            
            Optional<ButtonType> result = alert.showAndWait();

            if ((result.isPresent()) && (result.get() == ButtonType.OK)) {
            	DBQueryRepository query = new DBQueryRepository(
            			MyDataSourceFactory.getDataSource(Commons.MYSQL));
        		boolean sucess = query.deleteRepository(treeController.repository);
        		if(sucess) {
        			System.out.println("Repositorio eliminado");
        			removeTreeViewList(treeController.repository);
        		}
            }
            
        	
    	}
    }
    
    public Repository openDialog(String fxml, String header, String title, boolean dialogType) {
    	Dialog<Repository> dialog = null;
    	try {
    		// Carga de fxml
    		FXMLLoader loader = new FXMLLoader();    	   
        	loader.setResources(bundle);
        	loader.setCharset(Charset.forName("UTF-8"));
        	
        	VBox custom = (loader.load(getClass().getClassLoader().getResource(fxml).openStream()));
        	
        	// Se obtiene el controller
        	DialogController controller = loader.getController();
        	controller.setStage(stage);
        	
        	// Se crea el dialogo
        	dialog = new Dialog<Repository>();
        	dialog.setTitle(title);
        	dialog.setHeaderText(header);
        	DialogPane pane = dialog.getDialogPane();
        	pane.setContent(custom);
        	
        	// Se agrega un button the ok
        	ButtonType buttonTypeOk = new ButtonType("Okay", ButtonData.OK_DONE);
        	dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        	
        	
        	// Se agrega a la ventana
        	Window window = dialog.getDialogPane().getScene().getWindow();
            window.setOnCloseRequest(event -> window.hide());
            
            // Funcion para cuando se presione el boton 
        	dialog.setResultConverter(new Callback<ButtonType, Repository>() {
                @Override
                public Repository call(ButtonType b) {
                    if (b == buttonTypeOk) {
                    	// Saber si el directorio es correcto
                    	if(controller.directory.getText() != null && controller.directory.getText() != "" &&
                				controller.name.getText() != null && controller.name.getText() != "") {
                    		File file = new File(controller.directory.getText());
                			if(file.exists() && file.isDirectory()) {
                				Repository repository = new Repository(controller.name.getText(), controller.directory.getText(), -1);
                				// Saber si es mi dialog tiene url
                				if(dialogType) {
                            		if(controller.url.getText() != null && controller.url.getText() != "") {
                            			repository.setUrl(controller.url.getText());
                            		}
                            	}
                				return repository;
                			}
                			else {
                				System.out.println("Error 2: The root is not a directory");
                				return null;
                			}
                    	} else {
                    		System.out.println("Error 1: No directory or name");
                    		return null;
                    	}
                     	
                    }
                    else return null;
                 }
             });
             
        	// Se muestra el dialogo
             Optional<Repository> result = dialog.showAndWait();
             
             // Si esta presente se regresa
             if (result.isPresent()) {
            	 if(result.get() != null) return result.get();
             }
            return null;
            //alert.showAndWait(); 
    		
    	} catch(IOException e) {
    		e.printStackTrace();
    	}
		return null;
    }
    
    /**
     * Para guardar el repositorio en la base de datos
     * @param repository
     * @return
     */
    private boolean saveRepository(Repository repository) {
    	DBQueryRepository query = new DBQueryRepository(MyDataSourceFactory.getDataSource(Commons.MYSQL));
		query.putRepository(repository);
		int id = query.getRepositoryId(repository);
		repository.setId(id);
		System.out.println("The repository is save in the db with id: " + id);
		addTreeViewList(repository);
		return true;
    }
    
    
    private void errorAlert(String title, String description) {
    	Alert alert = new Alert(AlertType.ERROR);
    	alert.setTitle(title);
    	alert.setContentText(description);
    	Window window = alert.getDialogPane().getScene().getWindow();
        window.setOnCloseRequest(event -> window.hide());
    	alert.show();
    }
    
    private void removeTreeViewList(Repository repository) {
    	this.treeViewRepoList.getRoot().getChildren().forEach(r -> {
    		if(r.getValue().getName() == repository.getName() && r.getValue().getPath() == repository.getPath()) {
    			 TreeItem<com.rocketgit.objects.Repository> treeItem = 
    					 (TreeItem<com.rocketgit.objects.Repository>) treeViewRepoList.getSelectionModel().getSelectedItem();	
    			 treeItem.getParent().getChildren().remove(treeItem);
    		}
    	});
    	System.out.println("Repository remove");
    }
    private void addTreeViewList(Repository repository) {
    	this.treeViewRepoList.getRoot().getChildren()
            .add(new TreeItem<>(
                repository,
                new FontAwesomeIconView(FontAwesomeIcon.BOOK)
            ));
    }
    
    
    /**
     * Change the remote default to push :)
     * @throws GitAPIException 
     */
    @FXML
    public void deleteRemote() throws GitAPIException {
    	if(treeController != null) {
            Collection<RemoteConfig> remotes = treeController.git.remoteList().call();
            ArrayList<String> dialogData = new ArrayList<String>();
            remotes.forEach(re -> {
            	dialogData.add(re.getName());
            });
            
    		Dialog<String>dialog = new ChoiceDialog<String>(dialogData.get(0), dialogData);
    		dialog.setTitle(bundle.getString("repo/delete/title"));
    		dialog.setHeaderText(bundle.getString("remote/delete/header"));

    		Optional<String> result = dialog.showAndWait();
    		String selected = null;
    				
    		if (result.isPresent()) {

    		    selected = result.get();
    		    if(selected != null) {
    		    	// Obtenemos el comando
    		    	RemoteRemoveCommand command = treeController.git.remoteRemove();
    		    	// Se agrega el nombre del repositorio a ser eliminado
    		    	command.setName(selected);
    		    	// Se ejecuta
    		    	command.call();
    		    }
    		}
    	}
    }
    
    @FXML
    public void updateRemote() throws GitAPIException {
    	if(treeController != null) {
        	try {
        		// Carga de fxml
        		FXMLLoader loader = new FXMLLoader();        	   	            	
            	loader.setResources(bundle);
            	loader.setCharset(Charset.forName("UTF-8"));
            	
            	VBox custom = (loader.load(getClass().getClassLoader().getResource("remoteupdate.fxml").openStream()));
            	
            	// Se obtiene el controller
            	UpdateRemoteController controller = loader.getController();
            	Collection<RemoteConfig> remotes = treeController.git.remoteList().call();
            	
            	// Se crea el dialogo
            	Alert alert = new Alert(Alert.AlertType.INFORMATION);
            	alert.setTitle(bundle.getString("remote/update/title"));
            	alert.setHeaderText(bundle.getString("remote/update/header"));
            	DialogPane pane = alert.getDialogPane();
            	pane.setContent(custom);            	
            	
            	// Se agrega a la ventana
            	Window window = alert.getDialogPane().getScene().getWindow();
                window.setOnCloseRequest(event -> window.hide());
              
            	controller.setRemotes(remotes);

            	// Se muestra la alerta
                Optional<ButtonType> result = alert.showAndWait();

                 // Si esta presente se regresa
                 if (result.get() == ButtonType.OK) {
                	 // Se valida que los inputs son correctos
                	 if(controller.select.getValue() != null && 
                			 controller.url.getText() != null &&
                			 controller.url.getText().length() > 0) {
                		// Se busca el comando
                		RemoteSetUrlCommand command = treeController.git.remoteSetUrl();
                		// Se agrega el nombre
                		command.setName(controller.select.getValue().getName());
                		// Se agrega la urish
                        try {
                        	URIish urIish = new URIish(controller.url.getText());
                            command.setUri(urIish);
                            // Se llama al comando
                            command.call();
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                            return;
                        }

                	 }
                 }	
        	}catch(IOException e) {
        		e.printStackTrace();
        	}
    	}
    }
   
}
