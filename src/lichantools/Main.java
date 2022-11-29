/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lichantools;

import java.awt.Desktop;
import java.io.File;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
/**
 *
 * @author mainuser
 */
public class Main extends Application {
    private static Stage mainStage;
    private static HostServices host;
    
    @Override
    public void start(Stage stage) throws Exception {
        //host = getHostServices();
        
        configurarStage(stage);
        configurarScene("login");
       
        stage.getIcons().add(new Image("file:icono-herr.png"));
        stage.show();
    }

    /**
     * @param args the command line arguments
     * @throws java.sql.SQLException
     * @throws java.lang.ClassNotFoundException
     */
    
    public static void main(String[] args) throws SQLException, ClassNotFoundException {   
        launch(args);
    }
    
    static public Stage getMainStage(){
        return mainStage;
    }
    
    static private void configurarStage(Stage stage){
        mainStage  = stage;
    }
    
    static public void configurarScene(String scene_name) throws Exception{
        Parent gui;
        String vista;
        String titulo;
        Stage stage = getMainStage();
        Boolean resizable = true;        
        
                
        switch (scene_name) {
            case "login":
                resizable = false;
                vista = "VistaLogin";
                titulo = "LichanTools 1.0";
                break;
            case "menu":
                vista = "VistaMenuEncargado";
                titulo = "Menú";
                break;
            case "normal_rent":
                vista = "VistaPrestamoNormal";
                titulo = "Préstamo normal";
                break;
            case "generic_rent":
                vista = "VistaPrestamoGen";
                titulo = "Préstamo genérico";
                break;
            case "tool_return":
                vista = "VistaRetornoHerr";
                titulo = "Devolver herramienta";
                break;
            case "update_stock":
                vista = "VistaActualizarStock";
                titulo = "Modificar stock";
                break;
            default:
                vista = "";
                titulo = "";
                break;
        }
        
        gui = FXMLLoader.load(Main.class.getResource("vistas/" + vista + ".fxml"));
        
        Scene scene = new Scene(gui);

        stage.setTitle(titulo);
        stage.setScene(scene);
        stage.setResizable(resizable);
        stage.centerOnScreen();
    }
    
    
    static public void configurarSceneAdmin(String scene_name) throws Exception{
        Parent gui;
        String vista;
        String titulo;
        Stage stage = getMainStage();
        Boolean resizable = true;        
        
        switch (scene_name) {
            case "login":
                resizable = false;
                vista = "VistaLogin";
                titulo = "LichanTools 1.0";
                break;
            case "menu_admin":
                vista = "VistaMenuAdmin";
                titulo = "Menú de administración";
                break;
            case "admin_herr":
                vista = "VistaAdminHerr";
                titulo = "Administración de herramientas";
                break;
            case "admin_presgen":
                vista = "VistaAdminPrestamosGen";
                titulo = "Administración de préstamos genéricos";
                break;
            case "admin_pañol":
                vista = "VistaAdminPañoles";
                titulo = "Administración de pañoles";
                break;
            default:
                vista = "";
                titulo = "";
                break;
        }
        
        gui = FXMLLoader.load(Main.class.getResource("vistas/" + vista + ".fxml"));
        
        Scene scene = new Scene(gui);

        stage.setTitle(titulo);
        stage.setScene(scene);
        stage.setResizable(resizable);
        stage.centerOnScreen();
    }
    
    static public void abrirArchivo(File archivo){  
        System.out.println("ABRIENDO NUEVO ARCHIVO");
        
        if(Desktop.isDesktopSupported()){
            new Thread(() -> {
                try {
                    Desktop.getDesktop().open(archivo);
                    System.out.println("REALIZADO");
                } 
                catch(Exception e){
                    System.err.println(e.getMessage());
                }
            }).start();
        }
            
    }
}
    
