/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lichantools.controladores;

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
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.util.Duration;
import lichantools.Main;
import lichantools.gestores.GestorHerramientas;
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
public class ControladorRetornoHerr implements Initializable{
    private ObservableList<Persona> personas;
    private Persona per_sel;
    private Prestamo prestamo_sel;
    
    @FXML private HBox caja_mensaje;
    
    @FXML private TextField people_tab_filter;
    @FXML private TableView<Persona> people_tab;
    @FXML private TableColumn<Persona, String> people_tab_name;
    @FXML private TableColumn<Persona, String> people_tab_rut;  
    
    @FXML private TableView<Prestamo> tab_prestamos;
    @FXML private TableColumn<Prestamo, Integer> tab_prestamos_id;
    @FXML private TableColumn<Prestamo, String> tab_prestamos_fecha; 
    
    @FXML private TableView<PresHerrTab> tab_detalle_pres;
    @FXML private TableColumn<PresHerrTab, String> tab_detalle_herr;
    @FXML private TableColumn<PresHerrTab, String> tab_detalle_car;
    @FXML private TableColumn<PresHerrTab, Integer> tab_detalle_cantnd; 
    @FXML private TableColumn<PresHerrTab, String> tab_detalle_cantad; 
    @FXML private TableColumn tab_detalle_sel; 
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //configuración de columnas para tabla de personas
        people_tab_name.setCellValueFactory(cell -> cell.getValue().getPropiedadNombre());
        people_tab_rut.setCellValueFactory(cell -> cell.getValue().getPropiedadRut());

        //configuración de columnas para tabla de préstamos
        tab_prestamos_id.setCellValueFactory(cell -> cell.getValue().obtenerPropiedadId());
        tab_prestamos_fecha.setCellValueFactory(cell -> cell.getValue().obtenerPropiedadFecha());
        
        //configuración de columnas para tabla de detalle de préstamo
        tab_detalle_herr.setCellValueFactory(cell -> cell.getValue().obtenerPropiedadNombre());
        tab_detalle_car.setCellValueFactory(cell -> cell.getValue().obtenerHerramienta().getPropiedadCaracteristicas());
        tab_detalle_cantnd.setCellValueFactory(cell -> cell.getValue().obtenerPropiedadCantidad());
        tab_detalle_cantad.setCellValueFactory(new PropertyValueFactory<>("tf"));
        
        //obtener nómina de personas y de préstamos genéricos
        personas = GestorPersonas.get().obtenerPersonas();

        initPeopleFilter();
        
        people_tab.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            per_sel = people_tab.getSelectionModel().getSelectedItem();
            
            tab_prestamos.getItems().clear();
            
            ObservableList<Prestamo> prestamos = GestorPrestamos.get().obtenerPorPersona(per_sel);
            prestamos.forEach(prestamo ->{
                if(!prestamo.estaDevuelto()){
                    tab_prestamos.getItems().add(prestamo);
                }
            });
        });
        
        tab_prestamos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            prestamo_sel = tab_prestamos.getSelectionModel().getSelectedItem();
            
            if(prestamo_sel != null){
                ObservableList<PresHerrTab> pres = FXCollections.observableArrayList();

                for(PresHerr ph: prestamo_sel.obtenerHerramientas()){
                    if(!ph.estaCompleto()){
                        pres.add(new PresHerrTab(ph));
                    }
                }

                tab_detalle_pres.setItems(pres);
            }
        });
        
        //configura columna seleccionar todo
        tab_detalle_sel.setCellFactory( 
            new Callback<TableColumn<Disposer.Record, Boolean>, TableCell<Disposer.Record, Boolean>>() {
                @Override  
                public TableCell<Disposer.Record, Boolean> call(TableColumn<Disposer.Record, Boolean> p) {
                    return new ControladorRetornoHerr.CheckBoxCell();
                }
        });
    }
    
   @FXML public void accionConfirmarDev(ActionEvent event) throws Exception{
        ObservableList<PresHerrTab> lista_ph = tab_detalle_pres.getItems();
        
        //comprobación de campos
        for(PresHerrTab p: lista_ph){
            String cantidad = p.getTf().getText();
            if(cantidad.isEmpty()){
                cantidad = "0";
            }
            
            if(cantidad.matches("^\\d+$")){
                if(Integer.parseInt(cantidad) > p.obtenerCantidadPrestada()){
                    return;
                }
            }
            else{
                return;
            }
        }
        
        for(PresHerrTab p: lista_ph){
            String cantidad = p.getTf().getText();
            if(cantidad.isEmpty()){
                cantidad = "0";
            }
            for(PresHerr ph: prestamo_sel.obtenerHerramientas()){
                if(Objects.equals(ph.obtenerHerramienta().getId(), p.obtenerHerramienta().getId())){
                    Integer n = Integer.parseInt(cantidad);
                    HerramientaPañol herrp = GestorSesion.get().obtenerHerrPorId(ph.obtenerHerramienta().getId());                    
                    GestorHerramientas.get().aumentarStock(herrp, n);
                    ph.devolver(n);
                    break;
                }
            }
        }
        
        GestorPrestamos.get().devolverPrestamo(prestamo_sel);
        
        tab_detalle_pres.getItems().clear();
        
        ObservableList<PresHerrTab> pres = FXCollections.observableArrayList();
            
        for(PresHerr ph: prestamo_sel.obtenerHerramientas()){
            if(!ph.estaCompleto()){
                pres.add(new PresHerrTab(ph));
            }
        }
            
        tab_detalle_pres.setItems(pres);
        
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

        
    @FXML public void cancelAction(ActionEvent event) throws Exception{
        Main.configurarScene("menu");
    }
    
    private void initPeopleFilter(){
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
                if(person.getNombreCompleto().toLowerCase().contains(lowercaseValue)){
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
     
    public class PresHerrTab extends PresHerr{
        private final TextField tf;
        
        public PresHerrTab(PresHerr ph) {
            super(ph.obtenerHerramienta(), ph.obtenerCantidadInicial(), ph.obtenerCantidadFinal());
            tf = new TextField("");
        }
        
        public TextField getTf(){
            return tf;
        }
    }
     
    //definición de checkbox seleccionar todo
    private class CheckBoxCell extends TableCell<Disposer.Record, Boolean> {
        final CheckBox cellCheckbox = new CheckBox();
        
        CheckBoxCell(){
            
            //Acción cuando checkbox es seleccionado/deseleccionado
            cellCheckbox.setOnAction((ActionEvent ev) -> {
                //obtener elemento seleccionado
                PresHerrTab herr = (PresHerrTab) CheckBoxCell.this.getTableView().getItems().get(CheckBoxCell.this.getIndex());

                //obtener referencia a campo de texto
                TextField textfield = herr.getTf();
                
                if(cellCheckbox.isSelected()){                   
                    textfield.setText(herr.obtenerCantidadPrestada().toString());
                    textfield.setDisable(true);
                }
                else{
                    textfield.setText("0");
                    textfield.setDisable(false);
                }
            });
        }

        //Muestra el checkbox si la fila no está vacía
        @Override
        protected void updateItem(Boolean t, boolean empty) {
            super.updateItem(t, empty);
            if(!empty){
                setGraphic(cellCheckbox);
            }
            else{
                setGraphic(null);
            }
        }
    } 

    //definición de campo de texto
    private class TextFieldCell extends TableCell<Disposer.Record, Boolean> {
        final TextField cellText;
        
        TextFieldCell(){
            cellText = new TextField();
            cellText.getStyleClass().add("textfield");
            
            //Acción cuando el campo de texto es modificado
            cellText.setOnAction((ActionEvent ev) -> {
                
            });
        }

        //Muestra el campo de texto si la fila no está vacía
        @Override
        protected void updateItem(Boolean t, boolean empty) {
            super.updateItem(t, empty);
            if(!empty){
                setGraphic(cellText);
            }
            else{
                setGraphic(null);
            }
        }
    }  
}
