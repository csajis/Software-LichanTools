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
import lichantools.modelos.Forma;
import lichantools.modelos.Herramienta;
import lichantools.modelos.Medida;
import lichantools.modelos.Tipo;
import lichantools.modelos.Unidad;

/**
 *
 * @author mainuser
 */
public class ControladorAdministrarHerr implements Initializable{
    private ObservableList<Herramienta> herramientas;
    private Herramienta herr_sel;
    
    @FXML private HBox caja_mensaje;
    @FXML private Label mensaje;
    
    @FXML private TextField filtro_herr;
    
    @FXML private TableView<Herramienta> tab_herr;
    @FXML private TableColumn<Herramienta, String> tab_herr_nombre;
    
    @FXML private TextField campo_nombre;
    @FXML private TextField campo_stock;
    @FXML private ComboBox<Tipo> selector_tipo;
    
    @FXML private TableView<Tipo> tab_tipos;
    @FXML private TableColumn<Tipo, String> tab_tipos_nom;
    @FXML private TableColumn tab_tipos_eliminar;
    
    @FXML private ComboBox<Forma> selector_medida;
    @FXML private TextField campo_valor;
    @FXML private ComboBox<Unidad> selector_unidad;
    
    @FXML private TableView<Medida> tab_medidas;
    @FXML private TableColumn<Medida, String> tab_med_nom;
    @FXML private TableColumn<Medida, Float> tab_med_valor;
    @FXML private TableColumn<Medida, String> tab_med_uni;
    @FXML private TableColumn tab_med_eliminar;
    
    @FXML private Button boton_modificar;
    @FXML private Button boton_eliminar;
    
    @FXML private TextField campo_crear_nombre;
    @FXML private TextField campo_crear_stock;
    @FXML private ComboBox<Tipo> selector_crear_tipo;
    
    @FXML private TableView<Tipo> tab_crear_tipos;
    @FXML private TableColumn<Tipo, String> tab_crear_tipos_nom;
    @FXML private TableColumn tab_crear_tipos_eliminar;
    
    @FXML private ComboBox<Forma> selector_crear_medida;
    @FXML private TextField campo_crear_valor;
    @FXML private ComboBox<Unidad> selector_crear_unidad;
    
    @FXML private TableView<Medida> tab_crear_medidas;
    @FXML private TableColumn<Medida, String> tab_crear_med_nom;
    @FXML private TableColumn<Medida, Float> tab_crear_med_valor;
    @FXML private TableColumn<Medida, String> tab_crear_med_uni;
    @FXML private TableColumn tab_crear_med_eliminar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //configuración de columnas para tabla de herramientas disponibles
        tab_herr_nombre.setCellValueFactory(cell -> cell.getValue().getPropiedadNombre());
           
        tab_tipos_nom.setCellValueFactory(cell -> cell.getValue().getPropiedadNombre());        
        tab_crear_tipos_nom.setCellValueFactory(cell -> cell.getValue().getPropiedadNombre());
        
        tab_med_nom.setCellValueFactory(cell -> cell.getValue().getPropiedadNombre());
        tab_med_valor.setCellValueFactory(cell -> cell.getValue().getPropiedadValor());
        tab_med_uni.setCellValueFactory(cell -> cell.getValue().getPropiedadUnidad());
        
        tab_crear_med_nom.setCellValueFactory(cell -> cell.getValue().getPropiedadNombre());
        tab_crear_med_valor.setCellValueFactory(cell -> cell.getValue().getPropiedadValor());
        tab_crear_med_uni.setCellValueFactory(cell -> cell.getValue().getPropiedadUnidad());
        
        herramientas = GestorHerramientas.get().obtenerHerramientas();
        
        //acción al seleccionar una fila
        tab_herr.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            herr_sel = tab_herr.getSelectionModel().getSelectedItem();
            
            if(herr_sel != null){
                campo_nombre.setText(herr_sel.getNombre());
                campo_stock.setText(Integer.toString(herr_sel.getStockCritico()));
                
                tab_tipos.setItems(herr_sel.getTipos());
                tab_medidas.setItems(herr_sel.getMedidas());

                selector_tipo.setItems(GestorHerramientas.get().obtenerListaTipos());
                
                for(Tipo t1: herr_sel.getTipos()){
                    for(Tipo t2 : selector_tipo.getItems()){
                        if(Objects.equals(t1.getId(), t2.getId())){
                            selector_tipo.getItems().remove(t2);
                            break;
                        }
                    }
                }
                
                selector_tipo.setValue(selector_tipo.getItems().get(0));
                
                selector_medida.setItems(GestorHerramientas.get().obtenerListaFormas());
                
                for(Medida t1: herr_sel.getMedidas()){
                    for(Forma t2 : selector_medida.getItems()){
                        if(Objects.equals(t1.getForma().getId(), t2.getId())){
                            selector_medida.getItems().remove(t2);
                            break;
                        }
                    }
                }
                
                selector_medida.setValue(selector_medida.getItems().get(0));
                
                campo_valor.clear();
                
                boton_modificar.setDisable(false);
                boton_eliminar.setDisable(false);
                
            }
        });
        
        //configura celdas para combobox 
        selector_tipo.setCellFactory(
            new Callback<ListView<Tipo>, ListCell<Tipo>>() { 
                    @Override
                    public ListCell<Tipo> call(ListView<Tipo> p) {
                        ListCell<Tipo> cell = new ListCell<Tipo>(){
                            @Override
                            protected void updateItem(Tipo item, boolean empty) {
                                super.updateItem(item, empty);
                                setText(empty ? "" : item.getNombre());
                            }   
                        };
                                
                        return cell;
                    }                    
            }
        );
        
        //configura celda valor visible para combobox
        selector_tipo.setButtonCell(new ListCell<Tipo>(){
            @Override
            protected void updateItem(Tipo item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getNombre());
            }
        });
        
        
        //configura celdas para combobox 
        selector_crear_tipo.setCellFactory(
            new Callback<ListView<Tipo>, ListCell<Tipo>>() { 
                    @Override
                    public ListCell<Tipo> call(ListView<Tipo> p) {
                        ListCell<Tipo> cell = new ListCell<Tipo>(){
                            @Override
                            protected void updateItem(Tipo item, boolean empty) {
                                super.updateItem(item, empty);
                                setText(empty ? "" : item.getNombre());
                            }   
                        };
                                
                        return cell;
                    }                    
            }
        );
        
        //configura celda valor visible para combobox
        selector_crear_tipo.setButtonCell(new ListCell<Tipo>(){
            @Override
            protected void updateItem(Tipo item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getNombre());
            }
        });

        //configura celdas para combobox 
        selector_unidad.setCellFactory(
            new Callback<ListView<Unidad>, ListCell<Unidad>>() { 
                    @Override
                    public ListCell<Unidad> call(ListView<Unidad> p) {
                        ListCell<Unidad> cell = new ListCell<Unidad>(){
                            @Override
                            protected void updateItem(Unidad item, boolean empty) {
                                super.updateItem(item, empty);
                                setText(empty ? "" : item.getStr());
                            }   
                        };
                                
                        return cell;
                    }                    
            }
        );
        
        //configura celda valor visible para combobox
        selector_unidad.setButtonCell(new ListCell<Unidad>(){
            @Override
            protected void updateItem(Unidad item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getStr());
            }
        });        
        
        //configura celdas para combobox 
        selector_crear_unidad.setCellFactory(
            new Callback<ListView<Unidad>, ListCell<Unidad>>() { 
                    @Override
                    public ListCell<Unidad> call(ListView<Unidad> p) {
                        ListCell<Unidad> cell = new ListCell<Unidad>(){
                            @Override
                            protected void updateItem(Unidad item, boolean empty) {
                                super.updateItem(item, empty);
                                setText(empty ? "" : item.getStr());
                            }   
                        };
                                
                        return cell;
                    }                    
            }
        );
        
        //configura celda valor visible para combobox
        selector_crear_unidad.setButtonCell(new ListCell<Unidad>(){
            @Override
            protected void updateItem(Unidad item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getStr());
            }
        });
        
        selector_crear_tipo.setItems(GestorHerramientas.get().obtenerListaTipos());
        selector_crear_medida.setItems(GestorHerramientas.get().obtenerListaFormas());
        
        configurarFiltroHerramientas();
        
        //configura columnas eliminar
        tab_tipos_eliminar.setCellFactory( 
            new Callback<TableColumn<Disposer.Record, Boolean>, TableCell<Disposer.Record, Boolean>>() {
                @Override  
                public TableCell<Disposer.Record, Boolean> call(TableColumn<Disposer.Record, Boolean> p) {
                    return new ControladorAdministrarHerr.ButtonCell();
                }
        });
        
        tab_crear_tipos_eliminar.setCellFactory( 
            new Callback<TableColumn<Disposer.Record, Boolean>, TableCell<Disposer.Record, Boolean>>() {
                @Override  
                public TableCell<Disposer.Record, Boolean> call(TableColumn<Disposer.Record, Boolean> p) {
                    return new ControladorAdministrarHerr.ButtonCellCrear();
                }
        });
        
        //configura columnas eliminar
        tab_crear_med_eliminar.setCellFactory( 
            new Callback<TableColumn<Disposer.Record, Boolean>, TableCell<Disposer.Record, Boolean>>() {
                @Override  
                public TableCell<Disposer.Record, Boolean> call(TableColumn<Disposer.Record, Boolean> p) {
                    return new ControladorAdministrarHerr.ButtonCellMedidaCrear();
                }
        });
        
        //configura columnas eliminar
        tab_med_eliminar.setCellFactory( 
            new Callback<TableColumn<Disposer.Record, Boolean>, TableCell<Disposer.Record, Boolean>>() {
                @Override  
                public TableCell<Disposer.Record, Boolean> call(TableColumn<Disposer.Record, Boolean> p) {
                    return new ControladorAdministrarHerr.ButtonCellMedida();
                }
        });
        
        
        //configura celdas para combobox 
        selector_medida.setCellFactory(
            new Callback<ListView<Forma>, ListCell<Forma>>() { 
                    @Override
                    public ListCell<Forma> call(ListView<Forma> p) {
                        ListCell<Forma> cell = new ListCell<Forma>(){
                            @Override
                            protected void updateItem(Forma item, boolean empty) {
                                super.updateItem(item, empty);
                                setText(empty ? "" : item.getNombre());
                            }   
                        };
                                
                        return cell;
                    }                    
            }
        );
        
        //configura celda valor visible para combobox
        selector_medida.setButtonCell(new ListCell<Forma>(){
            @Override
            protected void updateItem(Forma item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getNombre());
            }
        });
        
        
        //combobox
        selector_crear_medida.setCellFactory(
            new Callback<ListView<Forma>, ListCell<Forma>>() { 
                    @Override
                    public ListCell<Forma> call(ListView<Forma> p) {
                        ListCell<Forma> cell = new ListCell<Forma>(){
                            @Override
                            protected void updateItem(Forma item, boolean empty) {
                                super.updateItem(item, empty);
                                setText(empty ? "" : item.getNombre());
                            }   
                        };
                                
                        return cell;
                    }                    
            }
        );
        
        //configura celda valor visible para combobox
        selector_crear_medida.setButtonCell(new ListCell<Forma>(){
            @Override
            protected void updateItem(Forma item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getNombre());
            }
        });
    }
    
    @FXML public void accionAgregarTipo(){
        Tipo tipo = selector_tipo.getValue();
        selector_tipo.getItems().remove(tipo);
        tab_tipos.getItems().add(tipo);
    }
    
    @FXML public void accionCancelar() throws Exception{
        Main.configurarSceneAdmin("menu_admin");
    }
    
    @FXML public void accionModificarHerr(){
        String nombre = campo_nombre.getText();
        String stock_critico = campo_stock.getText();
        ObservableList<Tipo> lista_tipos = FXCollections.observableArrayList(tab_tipos.getItems());
        ObservableList<Medida> lista_medidas = FXCollections.observableArrayList(tab_medidas.getItems());
         
        if(herr_sel != null && !nombre.isEmpty() && stock_critico.matches("^\\d+$")){
            System.out.println("MODIFICACION");
            Herramienta nuevo = new Herramienta(herr_sel.getId(), nombre, Integer.parseInt(stock_critico), lista_tipos, lista_medidas);
            GestorHerramientas.get().actualizarHerramienta(herr_sel, nuevo);
            
            int i;
            for(i = 0; i < herramientas.size(); i++){
                if(herramientas.get(i) == herr_sel){
                    break;
                }
            }
            
            herramientas.set(i, nuevo);
            
            Timeline timeline = new Timeline( new KeyFrame(Duration.seconds(1.5), new EventHandler<ActionEvent>(){
                    @Override
                    public void handle(ActionEvent event){
                        caja_mensaje.setVisible(false);
                    }
                }) 
            );
            timeline.setCycleCount(1);
        
            mensaje.setText("Herramienta actualizada con éxito");
            caja_mensaje.setVisible(true);
            timeline.play();
        }
    }
    
    @FXML public void accionEliminarHerr(){
        GestorHerramientas.get().eliminarHerramienta(herr_sel);
        
        campo_nombre.clear();
        campo_stock.clear();
        selector_tipo.getItems().clear();
        selector_unidad.getItems().clear();
        tab_tipos.getItems().clear();
        tab_medidas.getItems().clear();
        
        herramientas.remove(herr_sel);
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
        
        mensaje.setText("Herramienta eliminada con éxito");
        caja_mensaje.setVisible(true);
        timeline.play();
    }
    
    @FXML public void accionAgregarMedida(){
        Forma f = selector_medida.getValue();
        String valor = campo_valor.getText();
        Unidad u = selector_unidad.getValue();
        
        if(f != null && u != null && valor.matches("^\\d+.?\\d*$")){
           Medida m = new Medida(f, Float.parseFloat(valor), u);
           tab_medidas.getItems().add(m);
           
           selector_medida.getItems().remove(f);
           campo_valor.clear();
        }
    }
 
    @FXML public void accionAgregarCrearMedida(){
        Forma f = selector_crear_medida.getValue();
        String valor = campo_crear_valor.getText();
        Unidad u = selector_crear_unidad.getValue();
        
        if(f != null && u != null && valor.matches("^\\d+.?\\d*$")){
           Medida m = new Medida(f, Float.parseFloat(valor), u);
           tab_crear_medidas.getItems().add(m);
           
           selector_crear_medida.getItems().remove(f);
           campo_crear_valor.clear();
        }
        
    }
    
    
    @FXML public void accionAgregarCrearTipo(){
        Tipo tipo = selector_crear_tipo.getValue();
        selector_crear_tipo.getItems().remove(tipo);
        tab_crear_tipos.getItems().add(tipo);
        
    }
    
    @FXML public void accionIngresarHerramienta(){
         String nombre = campo_crear_nombre.getText();
         String stock_critico = campo_crear_stock.getText();
         ObservableList<Tipo> lista_tipos = FXCollections.observableArrayList(tab_crear_tipos.getItems());
         ObservableList<Medida> lista_medidas = FXCollections.observableArrayList(tab_crear_medidas.getItems());
         
         if(!nombre.isEmpty() && stock_critico.matches("^\\d+$")){
            Herramienta h = new Herramienta(nombre, Integer.parseInt(stock_critico), lista_tipos, lista_medidas);
            Integer id = GestorHerramientas.get().ingresarHerramienta(h);
            h.setId(id);
            herramientas.add(h);
            
            campo_crear_nombre.clear();
            campo_crear_stock.clear();
            selector_crear_tipo.getItems().addAll(tab_crear_tipos.getItems());
            tab_crear_tipos.getItems().clear();
            campo_crear_valor.clear();
            tab_crear_medidas.getItems().forEach(medida ->{  
                selector_crear_medida.getItems().add(medida.getForma());
            });
            tab_crear_medidas.getItems().clear();
            
            Timeline timeline = new Timeline( new KeyFrame(Duration.seconds(1.5), new EventHandler<ActionEvent>(){
                    @Override
                    public void handle(ActionEvent event){
                        caja_mensaje.setVisible(false);
                    }
                }) 
            );
            timeline.setCycleCount(1);
        
            mensaje.setText("Herramienta creada con éxito");
            caja_mensaje.setVisible(true);
            timeline.play();            
         }
    }
    
    //definición de botón eliminar
    private class ButtonCell extends TableCell<Disposer.Record, Boolean> {
        final Button cellButton = new Button("Eliminar");
        
        ButtonCell(){
            //Action when the button is pressed
            cellButton.setOnAction((ActionEvent t) -> {
                // obtener elemento seleccionado
                Tipo item = (Tipo) ButtonCell.this.getTableView().getItems().get(ButtonCell.this.getIndex());
                selector_tipo.getItems().add(item);
                tab_tipos.getItems().remove(item);
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
    
    //definición de botón eliminar
    private class ButtonCellCrear extends TableCell<Disposer.Record, Boolean> {
        final Button cellButton = new Button("Eliminar");
        
        ButtonCellCrear(){
            //Action when the button is pressed
            cellButton.setOnAction((ActionEvent t) -> {
                // obtener elemento seleccionado
                Tipo item = (Tipo) ButtonCellCrear.this.getTableView().getItems().get(ButtonCellCrear.this.getIndex());
                selector_crear_tipo.getItems().add(item);
                tab_crear_tipos.getItems().remove(item);
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
    
    //definición de botón eliminar
    private class ButtonCellMedida extends TableCell<Disposer.Record, Boolean> {
        final Button cellButton = new Button("Eliminar");
        
        ButtonCellMedida(){
            //Action when the button is pressed
            cellButton.setOnAction((ActionEvent t) -> {
                // obtener elemento seleccionado
                Medida item = (Medida) ButtonCellMedida.this.getTableView().getItems().get(ButtonCellMedida.this.getIndex());
                selector_medida.getItems().add(item.getForma());
                tab_medidas.getItems().remove(item);
                selector_unidad.getItems().clear();
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
    
    @FXML public void accionEstablecerUnidades(ActionEvent event){
        Forma f = selector_medida.getSelectionModel().getSelectedItem();
        
        if(f != null){
            String fn = f.getNombre();
            ObservableList<Unidad> lista_unidades = GestorHerramientas.get().obtenerListaUnidades();
            selector_unidad.getItems().clear();

            for(Unidad u: lista_unidades){
                String un = u.getStr();
                System.out.println(un);

                if(fn.equals("Alto") || fn.equals("Ancho") || fn.equals("Largo") || fn.equals("Diámetro")){           
                    if(un.equals("cm") || un.equals("mt") || un.equals("dm") || un.equals("mm") || un.equals("in")){
                        selector_unidad.getItems().add(u);
                    }
                }

                else if(fn.equals("Peso")){
                    if(un.equals("mg") || un.equals("g") || un.equals("kg")){
                        selector_unidad.getItems().add(u);
                    }
                }

                else if(fn.equals("Volumen")){
                    if(un.equals("cc") || un.equals("lt") || un.equals("ml")){
                        selector_unidad.getItems().add(u);
                    }
                }

                else if(fn.equals("Voltaje")){
                    if(un.equals("volt")){
                        selector_unidad.getItems().add(u);
                    }
                }

                else if(fn.equals("Amperaje")){
                    if(un.equals("amp")){
                        selector_unidad.getItems().add(u);
                    }
                }

                else if(fn.equals("Resistencia")){
                    if(un.equals("ohm")){
                        selector_unidad.getItems().add(u);
                    }
                }

                else if(fn.equals("Potencia")){
                    if(un.equals("watts") || un.equals("joule")){
                        selector_unidad.getItems().add(u);
                    }
                }

                else if(fn.equals("Capacitancia")){
                    if(un.equals("uF") || un.equals("mF") || un.equals("pF") || un.equals("nF")){
                        selector_unidad.getItems().add(u);
                    }
                }

                else if(fn.equals("Frecuencia")){
                    if(un.equals("hz")){
                        selector_unidad.getItems().add(u);
                    }
                }

                else if(fn.equals("Amplitud")){
                    if(un.equals("db")){
                        selector_unidad.getItems().add(u);
                    }
                }

            }

            selector_unidad.setValue(selector_unidad.getItems().get(0));
        }
    }

    @FXML public void accionEstablecerCrearUnidades(ActionEvent event){
        Forma f = selector_crear_medida.getSelectionModel().getSelectedItem();
        String fn = f.getNombre();
        ObservableList<Unidad> lista_unidades = GestorHerramientas.get().obtenerListaUnidades();
        selector_crear_unidad.getItems().clear();
        
        for(Unidad u: lista_unidades){
            String un = u.getStr();
            
            if(fn.equals("Alto") || fn.equals("Ancho") || fn.equals("Largo") || fn.equals("Diámetro")){           
                if(un.equals("cm") || un.equals("mt") || un.equals("dm") || un.equals("mm") || un.equals("in")){
                    selector_crear_unidad.getItems().add(u);
                }
            }
            
            else if(fn.equals("Peso")){
                if(un.equals("mg") || un.equals("g") || un.equals("kg")){
                    selector_crear_unidad.getItems().add(u);
                }
            }
            
            else if(fn.equals("Volumen")){
                if(un.equals("cc") || un.equals("lt") || un.equals("ml")){
                    selector_crear_unidad.getItems().add(u);
                }
            }
            
            else if(fn.equals("Voltaje")){
                if(un.equals("volt")){
                    selector_crear_unidad.getItems().add(u);
                }
            }
            
            else if(fn.equals("Amperaje")){
                if(un.equals("A")){
                    selector_crear_unidad.getItems().add(u);
                }
            }
            
            else if(fn.equals("Resistencia")){
                if(un.equals("ohm")){
                    selector_crear_unidad.getItems().add(u);
                }
            }
            
            else if(fn.equals("Potencia")){
                if(un.equals("w") || un.equals("joule")){
                    selector_crear_unidad.getItems().add(u);
                }
            }

            else if(fn.equals("Capacitancia")){
                if(un.equals("uF") || un.equals("mF") || un.equals("pF") || un.equals("nF")){
                    selector_crear_unidad.getItems().add(u);
                }
            }
            
            else if(fn.equals("Frecuencia")){
                if(un.equals("hz")){
                    selector_crear_unidad.getItems().add(u);
                }
            }
            
            else if(fn.equals("Amplitud")){
                if(un.equals("db")){
                    selector_crear_unidad.getItems().add(u);
                }
            }
            
        }
        
        selector_crear_unidad.setValue(selector_crear_unidad.getItems().get(0));
    }
    
    //definición de botón eliminar
    private class ButtonCellMedidaCrear extends TableCell<Disposer.Record, Boolean> {
        final Button cellButton = new Button("Eliminar");
        
        ButtonCellMedidaCrear(){
            //Action when the button is pressed
            cellButton.setOnAction((ActionEvent t) -> {
                // obtener elemento seleccionado
                Medida item = (Medida) ButtonCellMedidaCrear.this.getTableView().getItems().get(ButtonCellMedidaCrear.this.getIndex());
                selector_crear_medida.getItems().add(item.getForma());
                tab_crear_medidas.getItems().remove(item);
                selector_crear_unidad.getItems().clear();
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
