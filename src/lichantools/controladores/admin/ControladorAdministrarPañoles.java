/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lichantools.controladores.admin;

import com.sun.prism.impl.Disposer;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.util.Duration;
import lichantools.Main;
import lichantools.gestores.GestorHerramientas;
import lichantools.gestores.GestorPañoles;
import lichantools.modelos.Especialidad;
import lichantools.modelos.Herramienta;
import lichantools.modelos.HerramientaPañol;
import lichantools.modelos.Pañol;

/**
 * FXML Controller class
 *
 * @author mainuser
 */
public class ControladorAdministrarPañoles implements Initializable {
    private ObservableList<Pañol> pañoles;
    private Pañol pañol_sel;
    private ObservableList<Herramienta> herramientas;
    private Herramienta herr_sel;
    
    @FXML private HBox caja_mensaje;
    @FXML private Label mensaje;
    
    @FXML private TableView<Pañol> tab_pañoles;
    @FXML private TableColumn<Pañol, String> tab_pañol_nombre;
    @FXML private TableColumn<Pañol, String> tab_pañol_esp;
            
    @FXML private TextField filtro_pañoles;         
    
    @FXML private TextField campo_nombre; 
    @FXML private TextField campo_ubicacion;
    @FXML private ComboBox<Especialidad> selector_especialidad;
    
    @FXML private Button boton_modificar;
    @FXML private Button boton_eliminar;
 
    @FXML private TextField campo_crear_nombre; 
    @FXML private TextField campo_crear_ubicacion;
    @FXML private ComboBox<Especialidad> selector_crear_especialidad;
    
    @FXML private TableView<Pañol> tab_pañoles_gestion;
    @FXML private TableColumn<Pañol, String> tab_pañol_nombre_gestion;
    @FXML private TableColumn<Pañol, String> tab_pañol_esp_gestion;
            
    @FXML private TextField filtro_pañoles_gestion;  
    
    @FXML private TextField filtro_herr;  
    
    @FXML private TableView<Herramienta> tab_herr;
    @FXML private TableColumn<Herramienta, String> tab_herr_nombre;
    @FXML private TableColumn<Herramienta, String> tab_herr_car;
    @FXML private TextField campo_stockt;
    @FXML private TextField campo_stockr;
    
    @FXML private TableView<HerramientaPañol> tab_herr_pañol;
    @FXML private TableColumn<HerramientaPañol, String> tab_herr_pañol_nombre;
    @FXML private TableColumn<HerramientaPañol, Integer> tab_herr_pañol_stockt;
    @FXML private TableColumn<HerramientaPañol, Integer> tab_herr_pañol_stockr;
    @FXML private TableColumn tab_herr_pañol_eliminar;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       tab_pañol_nombre.setCellValueFactory(cell -> cell.getValue().getPropiedadNombre());
       tab_pañol_esp.setCellValueFactory(cell -> cell.getValue().getPropiedadEspecialidad());
       
       tab_pañol_nombre_gestion.setCellValueFactory(cell -> cell.getValue().getPropiedadNombre());
       tab_pañol_esp_gestion.setCellValueFactory(cell -> cell.getValue().getPropiedadEspecialidad());
       
       tab_herr_nombre.setCellValueFactory(cell -> cell.getValue().getPropiedadNombre());
       tab_herr_car.setCellValueFactory(cell -> cell.getValue().getPropiedadCaracteristicas());
       
       tab_herr_pañol_nombre.setCellValueFactory(cell -> cell.getValue().getHerr().getPropiedadNombre());
       tab_herr_pañol_stockt.setCellValueFactory(cell -> cell.getValue().getPropiedadStockTotal());
       tab_herr_pañol_stockr.setCellValueFactory(cell -> cell.getValue().getPropiedadStock());
       
       pañoles = GestorPañoles.get().obtenerPañoles();
       herramientas = GestorHerramientas.get().obtenerHerramientas(); 
       
       tab_pañoles.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            pañol_sel = tab_pañoles.getSelectionModel().getSelectedItem();
            if(pañol_sel != null){
                campo_nombre.setText(pañol_sel.getNombre());
                campo_ubicacion.setText(pañol_sel.getUbicacion());

                Especialidad esp = GestorPañoles.get().obtenerEspecialidadPorId(pañol_sel.getEspecialidadId());
                selector_especialidad.setValue(esp);
                boton_modificar.setDisable(false);
                boton_eliminar.setDisable(false);
               
            }
        });
       
        tab_pañoles_gestion.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            pañol_sel = tab_pañoles_gestion.getSelectionModel().getSelectedItem();
            if(pañol_sel != null){               
               ObservableList<HerramientaPañol> herrp = FXCollections.observableArrayList(GestorHerramientas.get().obtenerPorPañol(pañol_sel).values());
               herramientas = GestorHerramientas.get().obtenerHerramientas();
               
               for(HerramientaPañol hp: herrp){
                   for(Herramienta h: herramientas){
                       if(Objects.equals(h.getId(), hp.getHerr().getId())){
                           herramientas.remove(h);
                           break;
                       }
                   }
               }
               
               tab_herr.setItems(herramientas);
               tab_herr_pañol.setItems(herrp);
            }
        });
       
        //configura celdas para combobox especialidad
        selector_especialidad.setCellFactory(
            new Callback<ListView<Especialidad>, ListCell<Especialidad>>() { 
                    @Override
                    public ListCell<Especialidad> call(ListView<Especialidad> p) {
                        ListCell<Especialidad> cell = new ListCell<Especialidad>(){
                            @Override
                            protected void updateItem(Especialidad item, boolean empty) {
                                super.updateItem(item, empty);
                                setText(empty ? "" : item.getNombre());
                            }   
                        };
                                
                        return cell;
                    }                    
            }
        );
        
        //configura celda valor visible para combobox especialidad
        selector_especialidad.setButtonCell(new ListCell<Especialidad>(){
            @Override
            protected void updateItem(Especialidad item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getNombre());
            }
        });
        
        //configura celdas para combobox especialidad
        selector_crear_especialidad.setCellFactory(
            new Callback<ListView<Especialidad>, ListCell<Especialidad>>() { 
                    @Override
                    public ListCell<Especialidad> call(ListView<Especialidad> p) {
                        ListCell<Especialidad> cell = new ListCell<Especialidad>(){
                            @Override
                            protected void updateItem(Especialidad item, boolean empty) {
                                super.updateItem(item, empty);                                
                                setText(empty ? "" : item.getNombre());
                            }   
                        };
                                
                        return cell;
                    }                    
            }
        );
        
        //configura celda valor visible para combobox especialidad
        selector_crear_especialidad.setButtonCell(new ListCell<Especialidad>(){
            @Override
            protected void updateItem(Especialidad item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getNombre());
            }
        });
      
       selector_especialidad.setItems(GestorPañoles.get().obtenerEspecialidades());
       selector_crear_especialidad.setItems(GestorPañoles.get().obtenerEspecialidades());
       tab_herr.setItems(herramientas);
       
        //configura columnas eliminar
        tab_herr_pañol_eliminar.setCellFactory( 
            new Callback<TableColumn<Disposer.Record, Boolean>, TableCell<Disposer.Record, Boolean>>() {
                @Override  
                public TableCell<Disposer.Record, Boolean> call(TableColumn<Disposer.Record, Boolean> p) {
                    return new ControladorAdministrarPañoles.ButtonCell();
                }
        });
       
       configurarFiltroPañoles(filtro_pañoles, tab_pañoles);
       configurarFiltroPañoles(filtro_pañoles_gestion, tab_pañoles_gestion); 
       configurarFiltroHerramientas();
    }
    
    
    @FXML public void accionModificarPañol(){
        String nombre = campo_nombre.getText();
        String ubicacion = campo_ubicacion.getText();
        Especialidad esp = selector_especialidad.getValue();
        
        if(!nombre.isEmpty() && !ubicacion.isEmpty()){
            Pañol nuevo = new Pañol(null, nombre, esp.getId(), esp.getNombre(), ubicacion);
            GestorPañoles.get().actualizarPañol(pañol_sel, nuevo);
            
            int i;
            for(i = 0; i < pañoles.size(); i++){
                if(pañoles.get(i) == pañol_sel){
                    break;
                }
            }
            
            pañoles.set(i, nuevo);
            
            Timeline timeline = new Timeline( new KeyFrame(Duration.seconds(1.5), new EventHandler<ActionEvent>(){
                    @Override
                    public void handle(ActionEvent event){
                        caja_mensaje.setVisible(false);
                    }
                }) 
            );
            timeline.setCycleCount(1);
        
            mensaje.setText("Pañol actualizado con éxito");
            caja_mensaje.setVisible(true);
            timeline.play();
        }
    }

    @FXML public void accionEliminarPañol(){
        GestorPañoles.get().eliminarPañol(pañol_sel);
        pañoles.remove(pañol_sel);
        campo_nombre.clear();
        campo_ubicacion.clear();
        selector_especialidad.setValue(null);
        boton_modificar.setDisable(true);
        boton_eliminar.setDisable(true);
        
        Timeline timeline = new Timeline( new KeyFrame(Duration.seconds(1.5), new EventHandler<ActionEvent>(){
                @Override
                public void handle(ActionEvent event){
                    caja_mensaje.setVisible(false);
                }
            }) 
        );
        timeline.setCycleCount(1);
        
        mensaje.setText("Pañol eliminado con éxito");
        caja_mensaje.setVisible(true);
        timeline.play();        
    }
    
    @FXML public void accionCrearPañol(){
        String nombre = campo_crear_nombre.getText();
        String ubicacion = campo_crear_ubicacion.getText();
        Especialidad esp = selector_crear_especialidad.getValue();
        
        if(esp != null && !nombre.isEmpty() && !ubicacion.isEmpty()){
            Pañol nuevo = new Pañol(null, nombre, esp.getId(), esp.getNombre(), ubicacion);
            Integer id = GestorPañoles.get().ingresarPañol(nuevo);  
            campo_crear_nombre.clear();
            campo_crear_ubicacion.clear();
            selector_crear_especialidad.setValue(null);
            
            nuevo.setId(id);
            pañoles.add(nuevo);
            
            Timeline timeline = new Timeline( new KeyFrame(Duration.seconds(1.5), new EventHandler<ActionEvent>(){
                    @Override
                    public void handle(ActionEvent event){
                        caja_mensaje.setVisible(false);
                    }
                }) 
            );
            timeline.setCycleCount(1);
        
            mensaje.setText("Pañol creado con éxito");
            caja_mensaje.setVisible(true);
            timeline.play();
        }
        else if(esp == null){
            System.out.println("Encargado nulo");
        }
    }
    
    @FXML public void accionCancelar() throws Exception{
        Main.configurarSceneAdmin("menu_admin");
    } 
    
    @FXML public void accionAgregarHerr(){
        herr_sel = tab_herr.getSelectionModel().getSelectedItem();
        String stockr = campo_stockr.getText();
        String stockt = campo_stockt.getText();
        
        if(stockr.matches("^\\d+$") && stockt.matches("^\\d+$")){
            HerramientaPañol hp = new HerramientaPañol(herr_sel, Integer.parseInt(stockt), Integer.parseInt(stockr));
            tab_herr_pañol.getItems().add(hp);
            
            campo_stockr.clear();
            campo_stockt.clear();
            tab_herr.getItems().remove(herr_sel);
        }
    }
    
    @FXML public void accionConfirmarMod(){
        ObservableList<HerramientaPañol> anterior = FXCollections.observableArrayList(GestorHerramientas.get().obtenerPorPañol(pañol_sel).values());
        ObservableList<HerramientaPañol> nuevo = FXCollections.observableArrayList(tab_herr_pañol.getItems());
        GestorPañoles.get().actualizarHerramientaPañol(pañol_sel, anterior, nuevo);
        
        Timeline timeline = new Timeline( new KeyFrame(Duration.seconds(1.5), new EventHandler<ActionEvent>(){
                    @Override
                    public void handle(ActionEvent event){
                        caja_mensaje.setVisible(false);
                    }
                }) 
        );
        timeline.setCycleCount(1);
        
        mensaje.setText("Herramientas de pañol modificadas con éxito");
        caja_mensaje.setVisible(true);
        timeline.play();
    }

    private void configurarFiltroPañoles(TextField tf, TableView<Pañol> tab) {
        FilteredList<Pañol> filtroPañoles = new FilteredList<>(pañoles, p->true);
        
        tf.textProperty().addListener((observable, oldValue, newValue)->{
            filtroPañoles.setPredicate( pañol -> {
                
                //Si el campo es nulo o está vacío muestra todas las filas
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                
                //nuevo valor del campo en minúsculas
                String lowercaseValue = newValue.toLowerCase();
                                
                //selecciona sólo aquellas filas que contienen la cadena ingresada
                if(pañol.getNombre().toLowerCase().contains(lowercaseValue)){
                    return true;
                }
                else if(pañol.getEspecialidad().toLowerCase().contains(lowercaseValue)){
                    return true;
                }
                
                //No coincide ninguno
                return false;
            });
        });
        
        tab.setItems(filtroPañoles);
    }
    
    //definición de botón eliminar
    private class ButtonCell extends TableCell<Disposer.Record, Boolean> {
        final Button cellButton = new Button("Eliminar");
        
        ButtonCell(){
            //Action when the button is pressed
            cellButton.setOnAction((ActionEvent t) -> {
                // obtener elemento seleccionado
                HerramientaPañol item = (HerramientaPañol) ButtonCell.this.getTableView().getItems().get(ButtonCell.this.getIndex());
                tab_herr_pañol.getItems().remove(item);
                tab_herr.getItems().add(item.getHerr());
            });
        }

        //Muestra el botón si la fila no está vacía
        @Override
        protected void updateItem(Boolean t, boolean empty) {
            super.updateItem(t, empty);
            if(!empty){
                setGraphic(cellButton);
            }
            else{
                setGraphic(null);
            }
        }
    }
    
        private void configurarFiltroHerramientas(){
        FilteredList<Herramienta> toolsFiltered = new FilteredList<>(herramientas, p->true);
        
        filtro_herr.textProperty().addListener((observable, oldValue, newValue)->{
            toolsFiltered.setPredicate( tool -> {
                //Si el campo es nulo o está vacío muestra todas las filas
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                
                //nuevo valor del campo en minúsculas
                String lowercaseValue = newValue.toLowerCase();
                
                
                //selecciona sólo aquellas filas que contienen la cadena ingresada
                if(tool.getNombre().toLowerCase().contains(lowercaseValue)){
                    return true;
                }
                
                //No coincide ninguno
                return false;
            });
        });
        
        tab_herr.setItems(toolsFiltered);
    }
}
