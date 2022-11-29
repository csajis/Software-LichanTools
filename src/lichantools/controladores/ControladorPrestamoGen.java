/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lichantools.controladores;

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
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.Duration;
import lichantools.Main;
import lichantools.gestores.GestorPersonas;
import lichantools.gestores.GestorPrestamosGen;
import lichantools.modelos.HerramientaPañol;
import lichantools.modelos.Persona;
import lichantools.modelos.PresGenHerr;
import lichantools.modelos.PresHerr;
import lichantools.modelos.PrestamoGen;

/**
 *
 * @author mainuser
 */
public class ControladorPrestamoGen implements Initializable{
    private ObservableList<Persona> personas;
    private ObservableList<PrestamoGen> pres_gen;
    private Persona per_sel;
    private PrestamoGen sel_pres;
    
    @FXML private HBox caja_mensaje;
    
    @FXML private TextField people_tab_filter;
    @FXML private TableView<Persona> people_tab;
    @FXML private TableColumn<Persona, String> people_tab_name;
    @FXML private TableColumn<Persona, String> people_tab_rut;    
    
    @FXML private ComboBox<PrestamoGen> rental_selector;
    @FXML private Text mensaje_error;
    
    @FXML private TableView<PresHerr> sel_tools_tab;
    @FXML private TableColumn<PresHerr, String> sel_tools_name;
    @FXML private TableColumn<PresHerr, String> sel_tools_car;
    @FXML private TableColumn<PresHerr, Integer> sel_tools_amount;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //configuración de columnas para tabla de personas
        people_tab_name.setCellValueFactory(cell -> cell.getValue().getPropiedadNombre());
        people_tab_rut.setCellValueFactory(cell -> cell.getValue().getPropiedadRut());
        
        //configuración de columnas para tabla de herramientas a prestar
        sel_tools_name.setCellValueFactory(cell -> cell.getValue().obtenerPropiedadNombre());
        sel_tools_car.setCellValueFactory(cell -> cell.getValue().obtenerHerramienta().getPropiedadCaracteristicas());
        sel_tools_amount.setCellValueFactory(cell -> cell.getValue().obtenerPropiedadCantidad());
        
        
        //configura celdas para combobox 
        rental_selector.setCellFactory(
            new Callback<ListView<PrestamoGen>, ListCell<PrestamoGen>>() { 
                    @Override
                    public ListCell<PrestamoGen> call(ListView<PrestamoGen> p) {
                        ListCell<PrestamoGen> cell = new ListCell<PrestamoGen>(){
                            @Override
                            protected void updateItem(PrestamoGen item, boolean empty) {
                                super.updateItem(item, empty);
                                setText(empty ? "" : item.obtenerNombre());
                            }   
                        };
                                
                        return cell;
                    }                    
            }
        );
        
        //configura celda valor visible para combobox
        rental_selector.setButtonCell(new ListCell<PrestamoGen>(){
            @Override
            protected void updateItem(PrestamoGen item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.obtenerNombre());
            }
        });
        
        //obtener nómina de personas y de préstamos genéricos
        personas = GestorPersonas.get().obtenerPersonas();
        pres_gen = GestorPrestamosGen.get().obtenerPrestamosGenericos();

      
        configurarFiltroPersonas();
        
        people_tab.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            per_sel = people_tab.getSelectionModel().getSelectedItem();
        });
        
        rental_selector.setItems(pres_gen);
    }
    
    @FXML protected void agregarHerramientasAction(ActionEvent event) throws Exception{
        sel_pres = rental_selector.getValue();
        mensaje_error.setText("");
        
        if(sel_pres != null){
            ObservableList<PresHerr> herr_pres = FXCollections.observableArrayList();
            
            for(PresGenHerr g: sel_pres.obtenerHerramientas()){
                HerramientaPañol h = g.getHerramienta();
                
                if(h != null && h.getStockReal() > 0){
                    Integer stock = h.getStockReal();
                    herr_pres.add(new PresHerr(h.getHerr(), stock, stock - g.getCantidad()));
                }
                else{
                    herr_pres.clear();
                    mensaje_error.setText("Este préstamo no puede realizarse debido \n"+
                                           "a falta de stock.");
                    return;
                }
            }
            
            borrarAction(event);
            sel_tools_tab.setItems(herr_pres);
        }
    }
    
    @FXML protected void accionSeleccionarPresGen(ActionEvent event){
        mensaje_error.setText("");
    }
    
    @FXML protected void prestarAction(ActionEvent event) throws Exception{
        if(per_sel != null && !sel_tools_tab.getItems().isEmpty()){
            GestorPrestamosGen.get().crearPrestamoGen(sel_pres, per_sel);
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
        
        borrarAction(event);
    }    
    
    @FXML public void borrarAction(ActionEvent event) throws Exception{
        sel_tools_tab.getItems().clear();
    }
        
    @FXML public void cancelarAction(ActionEvent event) throws Exception{
        borrarAction(event);
        Main.configurarScene("menu");
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
}
