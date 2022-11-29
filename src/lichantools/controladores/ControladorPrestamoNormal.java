/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lichantools.controladores;

import com.sun.prism.impl.Disposer.Record;
import java.net.URL;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.util.Duration;
import lichantools.Main;
import lichantools.gestores.GestorPersonas;
import lichantools.gestores.GestorPrestamos;
import lichantools.gestores.GestorSesion;
import lichantools.modelos.HerramientaPañol;
import lichantools.modelos.Persona;
import lichantools.modelos.PresHerr;
import lichantools.modelos.Prestamo;

/**
 *
 * @author mainuser
 */
public class ControladorPrestamoNormal implements Initializable{
    private ObservableList<Persona> personas;
    private ObservableList<HerramientaPañol> herramientas;
    
    private Persona per_sel;
    private HerramientaPañol herr_sel;
    
    @FXML private HBox caja_mensaje;
    
    @FXML private TextField people_tab_filter;
    @FXML private TableView<Persona> people_tab;
    @FXML private TableColumn<Persona, String> people_tab_name;
    @FXML private TableColumn<Persona, String> people_tab_rut;
    
    @FXML private TextField tools_tab_filter;
    @FXML private TableView<HerramientaPañol> tools_tab;
    @FXML private TableColumn<HerramientaPañol, String> tools_tab_name;    
    @FXML private TableColumn<HerramientaPañol, String> tools_tab_car;
    @FXML private TableColumn<HerramientaPañol, Integer> tools_tab_stock;
    
    @FXML private ComboBox<Integer> amount_selector;
    private final ObservableList<Integer> options = FXCollections.observableArrayList();
    
    @FXML private TableView<PresHerr> selected_tools_table;
    @FXML private TableColumn<PresHerr, String> selected_tools_name;
    @FXML private TableColumn<PresHerr, Integer> selected_tools_amount;
    @FXML private TableColumn selected_tools_delete;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb){
        //configuración de columnas para tabla de personas
        people_tab_name.setCellValueFactory(cell -> cell.getValue().getPropiedadNombre());
        people_tab_rut.setCellValueFactory(cell -> cell.getValue().getPropiedadRut());
        
        //configuración de columnas para tabla de herramientas disponibles
        tools_tab_name.setCellValueFactory(cell -> cell.getValue().getHerr().getPropiedadNombre());
        tools_tab_car.setCellValueFactory(cell -> cell.getValue().getHerr().getPropiedadCaracteristicas());
        tools_tab_stock.setCellValueFactory(cell -> cell.getValue().getPropiedadStock());
        
        //configuración de columnas para tabla de herramientas a prestar
        selected_tools_name.setCellValueFactory(cell -> cell.getValue().obtenerPropiedadNombre());
        selected_tools_amount.setCellValueFactory(cell -> cell.getValue().obtenerPropiedadCantidad());
        
        
        //obtener nómina de personas y herramientas
        personas = GestorPersonas.get().obtenerPersonas();
        herramientas = GestorSesion.get().obtenerHerramientasSesion();

        
        configurarFiltroPersonas();
        configurarFiltroHerramientas();
        
        people_tab.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            per_sel = people_tab.getSelectionModel().getSelectedItem();
        });
        
        tools_tab.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            herr_sel = tools_tab.getSelectionModel().getSelectedItem();
            actualizarSelectorCantidad();
        });
        
        //configura columna eliminar
        selected_tools_delete.setCellFactory( 
            new Callback<TableColumn<Record, Boolean>, TableCell<Record, Boolean>>() {
                @Override  
                public TableCell<Record, Boolean> call(TableColumn<Record, Boolean> p) {
                    return new ButtonCell();
                }
        });
        
        amount_selector.setItems(options);
    }
    
    @FXML public void prestarAction(ActionEvent event) throws Exception{
        if(per_sel != null && !selected_tools_table.getItems().isEmpty()){
            ObservableList<PresHerr> herr = FXCollections.observableArrayList(selected_tools_table.getItems());
            Prestamo pres =  new Prestamo("Norm", per_sel.getRut(), herr);
            GestorPrestamos.get().crearPrestamo(pres);
            selected_tools_table.getItems().clear();
            
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
    }    
    
    @FXML public void borrarAction(ActionEvent event) throws Exception{
        selected_tools_table.getItems().forEach( herr ->{
            HerramientaPañol h = GestorSesion.get().obtenerHerrPorId(herr.obtenerHerramienta().getId());
            h.aumentarStockReal(herr.obtenerCantidadPrestada());
        });
        
        selected_tools_table.getItems().clear();
        
        actualizarSelectorCantidad();
    }
        
    @FXML public void cancelarAction(ActionEvent event) throws Exception{
        borrarAction(event);
        Main.configurarScene("menu");
    }
    
    @FXML public void filterPeopleTableAction(ActionEvent event){    
    }
    
    @FXML public void agregarHerramientaAction(ActionEvent event){
        int cant_sel = amount_selector.getValue();
        
        Boolean in_tab = false;
        
        for(PresHerr p: selected_tools_table.getItems()){
            if(p.obtenerHerramienta().getId().equals(herr_sel.getHerr().getId())){
                in_tab = true;
                p.agregarCantidad(cant_sel);
                break;
            }
        }
        
        if(in_tab == false){
            int stock = herr_sel.getStockReal();
            PresHerr item = new PresHerr(herr_sel.getHerr(), stock, stock - cant_sel);
            selected_tools_table.getItems().add(item);
        }
        
        
        herr_sel.reducirStockReal(cant_sel);
        actualizarSelectorCantidad();
    }
    
    private void actualizarSelectorCantidad(){
        options.clear();
        
        if(herr_sel != null){
            for(int i = herr_sel.getStockReal(); i>0; i--){
                options.add(i);
            }
            amount_selector.setValue(herr_sel.getStockReal());
        }
    } 
    
    //definición de botón eliminar
    private class ButtonCell extends TableCell<Record, Boolean> {
        final Button cellButton = new Button("Eliminar");
        
        ButtonCell(){
        	//Action when the button is pressed
            cellButton.setOnAction((ActionEvent t) -> {
                // obtener elemento seleccionado
                PresHerr item = (PresHerr) ButtonCell.this.getTableView().getItems().get(ButtonCell.this.getIndex());
                
                //remover elemento seleccionado
                HerramientaPañol h = GestorSesion.get().obtenerHerrPorId(item.obtenerHerramienta().getId());
                h.aumentarStockReal(item.obtenerCantidadPrestada());
                selected_tools_table.getItems().remove(item);
                actualizarSelectorCantidad();
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
    
    private void configurarFiltroPersonas(){
        FilteredList<Persona> peopleFiltered = new FilteredList<>(personas, p->true);
        
        people_tab_filter.textProperty().addListener((observable, oldValue, newValue)->{
            peopleFiltered.setPredicate( person -> {
                //Si el campo es nulo o está vacío muestra todas las filas
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                
                //nuevo valor del campo en minúsculas
                String lowercaseValue = newValue.toLowerCase();
                
                
                //selecciona sólo aquellas filas que contienen la cadena ingresada
                if(person.getNombre().toLowerCase().contains(lowercaseValue)){
                    return true;
                }
                else if(person.getRut().toLowerCase().contains(lowercaseValue)){
                    return true;
                }
                
                //No coincide ninguno
                return false;
            });
        });
        
        people_tab.setItems(peopleFiltered);
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
