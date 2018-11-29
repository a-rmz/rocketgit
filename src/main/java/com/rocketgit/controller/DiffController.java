package com.rocketgit.controller;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;

public class DiffController {

	@FXML
	TreeTableView<String> treeTableView;
	
	@FXML
	TreeTableColumn<String, String> treeTableColumn;
	
	@FXML
	TableView<String> treeTableViewOld;
	@FXML
	TableColumn<String, String> treeTableColumnOld;
	
	@FXML
	TableView<String> treeTableViewNew;
	@FXML
	TableColumn<String, String> treeTableColumnNew;

	@FXML
	BorderPane root;

	@FXML
	public void initialize() throws IOException {
		initRepoList();
	}

	public void exit(ActionEvent actionEvent) {
		Platform.exit();
	}

	// TODO Remove dummy data
	private void initRepoList() throws IOException {

		displayTreeView(FileSystems.getDefault().getPath(".").toAbsolutePath().toString());
		
        treeTableViewOld.getColumns().addAll(treeTableColumnOld);
        treeTableViewNew.getColumns().addAll(treeTableColumnNew);

		treeTableView.setOnMouseClicked(mouseEvent -> {
			TreeItem<String> item = treeTableView.getSelectionModel().getSelectedItem();

			if (item != null && item.isLeaf()) {
				Path path = Paths.get(item.getValue());
				try (BufferedReader reader = Files.newBufferedReader(path, Charset.forName("UTF-8"))) {
					String currentLine = null, wholeFile;
					Collection<String> list = new ArrayList<>();

					while ((currentLine = reader.readLine()) != null) {// while there is content on the current line
						list.add(currentLine + "/n"); // print the current line
					}
					ObservableList<String> details = FXCollections.observableArrayList(list);
					
			        treeTableColumnOld.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
			        treeTableViewOld.setItems(details);
			        
			        treeTableColumnNew.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
			        treeTableViewNew.setItems(details);
				} catch (IOException ex) {
					ex.printStackTrace(); // handle an exception here
				}
			}
        });
	}
	
	public void displayTreeView(String inputDirectoryLocation) {
		System.out.println(inputDirectoryLocation);
	    // Creates the root item.
	    TreeItem<String> rootItem = new TreeItem<>(inputDirectoryLocation);

	    // Hides the root item of the tree view.
	    treeTableView.setShowRoot(false);

	    // Creates the cell factory.
	    treeTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue()));
	    // Get a list of files.
	    File fileInputDirectoryLocation = new File(inputDirectoryLocation);
	    File fileList[] = fileInputDirectoryLocation.listFiles();

	    // create tree
	    for (File file : fileList) {
	        createTree(file, rootItem);
	    }

	    treeTableView.setRoot(rootItem);
	}
	
	public static void createTree(File file, TreeItem<String> parent) {
	    if (file.isDirectory()) {
	        TreeItem<String> treeItem = new TreeItem<>(file.getName());
	        parent.getChildren().add(treeItem);
	        for (File f : file.listFiles()) {
	            createTree(f, treeItem);
	        }
	    } else {
	        parent.getChildren().add(new TreeItem<>(file.getName()));
	    }
	}
	
}
