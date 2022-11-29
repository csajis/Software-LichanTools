/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lichantools.controladores;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import lichantools.Main;
import lichantools.gestores.GestorPersonas;
import lichantools.gestores.GestorSesion;
import lichantools.servicios.DBConnection;
import lichantools.servicios.ExcelWriter;

/**
 *
 * @author mainuser
 */
public class ControladorMenu implements Initializable{
    @FXML Text texto_nombre_usuario;
    @FXML Text texto_tipo_usuario;
    @FXML Text texto_nombre_pan;
    @FXML Text texto_esp_pan;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        texto_nombre_usuario.setText(GestorSesion.get().obtenerUsuario().getNombre());
        texto_tipo_usuario.setText(GestorSesion.get().obtenerTipo().toUpperCase());
        texto_nombre_pan.setText(GestorSesion.get().obtenerPañol().getNombre());
        texto_esp_pan.setText(GestorSesion.get().obtenerPañol().getEspecialidad().toUpperCase());
        
        GestorPersonas.get().obtenerPersonas();
        DBConnection.close();
    }
    
    @FXML protected void logoutAction(ActionEvent event) throws Exception{
        GestorSesion.get().cerrarSesion();
        Main.configurarScene("login");
    }
    
    @FXML protected void genericRentAction(ActionEvent event) throws Exception{
        Main.configurarScene("generic_rent");
    }     
    
    @FXML protected void normalRentAction(ActionEvent event) throws Exception{
        Main.configurarScene("normal_rent");
    }     
        
    @FXML protected void toolReturnAction(ActionEvent event) throws Exception{
        Main.configurarScene("tool_return");
    }     
            
    @FXML protected void updateStockAction(ActionEvent event) throws Exception{
        Main.configurarScene("update_stock");
    }     
    
    @FXML protected void accionCrearNominaHerr(ActionEvent event) throws Exception{
        File excelFile = ExcelWriter.crearNominaHerramientas();
        Main.abrirArchivo(excelFile);
    }     
    
}
