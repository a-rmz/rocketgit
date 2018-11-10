package com.rocketgit.objects;

import javafx.beans.property.SimpleStringProperty;

import java.util.Collections;

public class Commit {
    SimpleStringProperty id = new SimpleStringProperty("");
    SimpleStringProperty shortMessage = new SimpleStringProperty("");
    SimpleStringProperty author = new SimpleStringProperty("");
    SimpleStringProperty date = new SimpleStringProperty("");
    SimpleStringProperty graph = new SimpleStringProperty("*");

    public Commit(String id, String shortMessage, String authorName, String authorEmail, String date, int tabSize) {
        setId(id);
        setShortMessage(shortMessage);
        setAuthor(String.format("%s <%s>", authorName, authorEmail));
        setDate(date);
        setGraph(String.format("%s*",
            String.join("", Collections.nCopies(tabSize, "\t"))
        ));
    }

    public String getId() {
        return id.get();
    }

    public SimpleStringProperty idProperty() {
        return id;
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public String getShortMessage() {
        return shortMessage.get();
    }

    public SimpleStringProperty shortMessageProperty() {
        return shortMessage;
    }

    public void setShortMessage(String shortMessage) {
        this.shortMessage.set(shortMessage);
    }

    public String getAuthor() {
        return author.get();
    }

    public SimpleStringProperty authorProperty() {
        return author;
    }

    public void setAuthor(String author) {
        this.author.set(author);
    }

    public String getDate() {
        return date.get();
    }

    public SimpleStringProperty dateProperty() {
        return date;
    }

    public void setDate(String date) {
        this.date.set(date);
    }

    public String getGraph() {
        return graph.get();
    }

    public SimpleStringProperty graphProperty() {
        return graph;
    }

    public void setGraph(String graph) {
        this.graph.set(graph);
    }

}
