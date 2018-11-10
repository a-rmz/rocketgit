package com.rocketgit.controller;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class DiffController {

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
        treeViewRepoList.setRoot(root);
    }
}
