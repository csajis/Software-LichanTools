/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lichantools.controladores.admin;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import lichantools.Main;
import lichantools.gestores.GestorHerramientas;
import lichantools.gestores.GestorPersonas;
import lichantools.gestores.GestorSesion;
import lichantools.servicios.DBConnection;

/**
 *
 * @author mainuser
 */
public class ControladorMenuAdmin implements Initializable{
    @FXML Text texto_nombre_usuario;
    @FXML Text texto_tipo_usuario;
    @FXML Text texto_institucion;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        texto_nombre_usuario.setText(GestorSesion.get().obtenerUsuario().getNombre());
        texto_tipo_usuario.setText(GestorSesion.get().obtenerTipo().toUpperCase());
        texto_institucion.setText(GestorSesion.get().obtenerInstitucion());
        
        GestorPersonas.get().obtenerPersonas();
        GestorHerramientas.get().obtenerHerramientas();
        DBConnection.close();
    }
    
    @FXML protected void logoutAction(ActionEvent event) throws Exception{
        GestorSesion.get().cerrarSesion();
        Main.configurarSceneAdmin("login");
    }
    
    @FXML protected void accionAdministrarHerr(ActionEvent event) throws Exception{
        Main.configurarSceneAdmin("admin_herr");
    }     
    
    @FXML protected void accionAdministrarPresGen(ActionEvent event) throws Exception{
        Main.configurarSceneAdmin("admin_presgen");
    }     
   
    @FXML protected void accionAdministrarPañoles(ActionEvent event) throws Exception{
        Main.configurarSceneAdmin("admin_pañol");
    }
}
