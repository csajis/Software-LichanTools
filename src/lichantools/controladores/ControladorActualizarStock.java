/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Herramientas | Templates
 * and open the template in the editor.
 */
package lichantools.controladores;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import lichantools.Main;
import lichantools.gestores.GestorHerramientas;
import lichantools.gestores.GestorSesion;
import lichantools.modelos.HerramientaPañol;

/**
 *
 * @author mainuser
 */
public class ControladorActualizarStock implements Initializable{
    private ObservableList<HerramientaPañol> herramientas;
    private HerramientaPañol herr_sel;
    private Integer cant_sel;
    
    @FXML private HBox caja_mensaje;
    
    @FXML private TextField tools_tab_filter;
    @FXML private TableView<HerramientaPañol> tools_tab;
    @FXML private TableColumn<HerramientaPañol, String> tools_tab_name;
    
    @FXML private TextField campo_nuevo_stock;
    @FXML private Text mensaje_error;
    
    @FXML private TableView<HerramientaPañol> tool_detail_tab;
    @FXML private TableColumn<HerramientaPañol, String> tool_detail_name;
    @FXML private TableColumn<HerramientaPañol, String> tool_detail_type;
    @FXML private TableColumn<HerramientaPañol, String> tool_detail_char;
    @FXML private TableColumn<HerramientaPañol, Integer> tool_detail_stock;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //configuración de columnas para tabla de herramientas
        tools_tab_name.setCellValueFactory(cell -> cell.getValue().getHerr().getPropiedadNombre());
        
        //configuración de columnas para tabla con detalle de herramienta seleccionada
        tool_detail_name.setCellValueFactory(cell -> cell.getValue().getHerr().getPropiedadNombre());
        tool_detail_stock.setCellValueFactory(cell -> cell.getValue().getPropiedadStock());
        tool_detail_char.setCellValueFactory(cell -> cell.getValue().getHerr().getPropiedadCaracteristicas());
        tool_detail_type.setCellValueFactory(cell -> cell.getValue().getHerr().getPropiedadTipo());
        
        herramientas = GestorSesion.get().obtenerHerramientasSesion();
        
        tools_tab.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            herr_sel = tools_tab.getSelectionModel().getSelectedItem();
            tool_detail_tab.setDisable(false);
            tool_detail_tab.getItems().clear();
            tool_detail_tab.getItems().add(herr_sel);
        });
        
        configurarFiltroHerramientas();
    }
    
   @FXML public void modificarAction(ActionEvent event) throws Exception{
        mensaje_error.setText("");
    
        if(campo_nuevo_stock.getText().matches("^\\d+$")){
            cant_sel = Integer.parseInt(campo_nuevo_stock.getText());
        }
        else{
            cant_sel = 0;
        }
        
        if(herr_sel != null && cant_sel > 0){
            GestorHerramientas.get().actualizarStock(herr_sel, cant_sel);
                    Timeline timeline = new Timeline( new KeyFrame(Duration.seconds(1.5), new EventHandler<ActionEvent>(){
                @Override
                public void handle(ActionEvent event){
                   caja_mensaje.setVisible(false);
                }
            }) 
        );
        timeline.setCycleCount(1);
        
        caja_mensaje.setVisible(true);
        timeline.play();
        }
        else if(herr_sel == null){
            mensaje_error.setText("No ha seleccionado una herramienta");
        }
        else if(cant_sel <= 0){
            mensaje_error.setText("Ingrese una cantidad mayor a cero");
        }
    }    
        
    @FXML public void cancelarAction(ActionEvent event) throws Exception{
        Main.configurarScene("menu");
    }
    
    private void configurarFiltroHerramientas(){
        FilteredList<HerramientaPañol> filtroHerramientas = new FilteredList<>(herramientas, p->true);
        
        tools_tab_filter.textProperty().addListener((observable, oldValue, newValue)->{
            filtroHerramientas.setPredicate( herr -> {
                //Si el campo es nulo o está vacío muestra todas las filas
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                
                //nuevo valor del campo en minúsculas
                String lowercaseValue = newValue.toLowerCase();
                
                
                //selecciona sólo aquellas filas que contienen la cadena ingresada
                if(herr.getHerr().getNombre().toLowerCase().contains(lowercaseValue)){
                    return true;
                }
                
                //No coincide ninguno
                return false;
            });
        });
        
        tools_tab.setItems(filtroHerramientas);
    }
    
}
