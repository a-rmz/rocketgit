package com.rocketgit.controller;

import java.util.Collection;

import org.eclipse.jgit.transport.RemoteConfig;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

public class UpdateRemoteController {
	
	@FXML
	TextField url;
	
	@FXML
	ChoiceBox<RemoteConfig> select;
		
	Collection<RemoteConfig> remotes;


	public void setRemotes(Collection<RemoteConfig> remotes) {
		this.remotes = remotes;
		select.getItems().addAll(remotes);
		select.setConverter(new StringConverter<RemoteConfig>() {

			@Override
			public String toString(RemoteConfig object) {
				// TODO Auto-generated method stub
				return object.getName();
			}

			@Override
			public RemoteConfig fromString(String string) {
				// TODO Auto-generated method stub
				return null;
			}
			
			});	
		}

}
