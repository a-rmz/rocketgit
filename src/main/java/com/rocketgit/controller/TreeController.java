package com.rocketgit.controller;

import com.rocketgit.objects.Commit;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.lib.BranchConfig;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revplot.PlotCommitList;
import org.eclipse.jgit.revplot.PlotLane;
import org.eclipse.jgit.revplot.PlotWalk;
import org.eclipse.jgit.revwalk.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

// Controller de la tabla 
public class TreeController {

    @FXML
    Label treeRepoName;

    @FXML
    TableView<Commit> commitTableView;

    public void setRepo(String name, String path) {
        try (Git git = Git.open(new File(path))) {
            Iterable<RevCommit> logs = git.log().call();
            treeRepoName.setText(name);

            ArrayList<Commit> commits = new ArrayList<>();

            Repository repository = git.getRepository();
            ObjectId rootId = repository.resolve(Constants.HEAD);
            PlotWalk walk = new PlotWalk(git.getRepository());
            walk.sort(RevSort.BOUNDARY, true);
            RevCommit root = walk.parseCommit(rootId);
            walk.markStart(root);

            PlotCommitList<PlotLane> plotCommitList = new PlotCommitList<>();
            plotCommitList.source(walk);
            plotCommitList.fillTo(Integer.MAX_VALUE);


            plotCommitList.forEach(plotLaneCommit -> {
                PlotLane plotLane = plotLaneCommit.getLane();
                int level = plotLane.getPosition();
                commits.add(
                    new Commit(
                        plotLaneCommit.getId().abbreviate(6).name(),
                        plotLaneCommit.getShortMessage(),
                        plotLaneCommit.getAuthorIdent().getName(),
                        plotLaneCommit.getAuthorIdent().getEmailAddress(),
                        plotLaneCommit.getAuthorIdent().getWhen().toString(),
                        level
                    )
                );
            });


            ObservableList<Commit> log = FXCollections.observableArrayList(commits);
            commitTableView.setItems(log);
        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
        }
    }

    public void exit(ActionEvent actionEvent) {
        Platform.exit();
    }


}