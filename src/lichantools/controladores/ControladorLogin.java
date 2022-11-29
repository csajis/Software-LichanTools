/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lichantools.controladores;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import lichantools.Main;
import lichantools.gestores.GestorSesion;

/**
 *
 * @author mainuser
 */
public class ControladorLogin implements Initializable {
    @FXML private TextField usr_input;
    @FXML private TextField pwd_input;
    @FXML private Text login_message;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        usr_input.textProperty().addListener((observable, oldValue, newValue)->{
            if(newValue.isEmpty()){
                login_message.setText("");
            }
        });
    }    
    
    @FXML protected void loginAction(ActionEvent event) throws Exception {
        if(usr_input.getText().matches("^\\d{7,8}-[\\dk]$")){
            String tipo_usuario = GestorSesion.get().iniciarSesion(usr_input.getText(), pwd_input.getText());

            switch (tipo_usuario) {
                case "administrador":
                    login_message.setText("");
                    Main.configurarSceneAdmin("menu_admin");
                    break;
                case "encargado":
                    login_message.setText("");
                    Main.configurarScene("menu");
                    break;
                default:
                    login_message.setText("Usuario o contraseña incorrecta");
                    break;
            }
        }
        else{
            login_message.setText("Rut inválido");
        }
    }
}
