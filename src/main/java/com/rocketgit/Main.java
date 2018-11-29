package com.rocketgit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.nio.charset.Charset;
import java.util.Locale;
import java.util.ResourceBundle;

import com.rocketgit.controller.MainController;
import com.rocketgit.database.DBQueryRepository;
import com.rocketgit.database.MyDataSourceFactory;
import com.rocketgit.objects.Repository;


public class Main extends Application {
	
	Parent root;

    @Override
    public void start(Stage stage) throws Exception {
    	// Con este abrimos el fxml que tiene el TreeView de repositorios y el tab
    	
    	// DBQueryRepository query = new DBQueryRepository(MyDataSourceFactory.getDataSource("Commons.MYSQL"));
    	
        FXMLLoader loader = new FXMLLoader();
        
        // Traducciones 
        
        ResourceBundle rb = ResourceBundle.getBundle(
            "i18n.main",
            new Locale.Builder().setLanguage("en").build()
        );
        
        loader.setResources(rb);
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
        
        root.setStyle("-fx-font-family: 'Comfortaa';");
        stage.setTitle(rb.getString("title"));
        stage.setScene(new Scene(root, 1000, 600));
        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
    
    
}
