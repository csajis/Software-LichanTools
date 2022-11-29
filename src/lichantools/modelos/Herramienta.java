/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lichantools.modelos;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author mainuser
 */
public class Herramienta {
    private Integer id;
    private String nombre;
    private ObservableList<Tipo> tipos;
    private Integer stock_critico;
    private ObservableList<Medida> medidas;
    
    private StringProperty prop_nombre;
    private StringProperty prop_tipo;
    private StringProperty prop_caracteristicas;
    
    public Herramienta(String nombre, Integer stock_critico, ObservableList<Tipo> tipos, ObservableList<Medida> medidas) {
        this.tipos = tipos;
        this.nombre = nombre;
        this.stock_critico = stock_critico;
        this.medidas = medidas;
        
        this.prop_nombre = new SimpleStringProperty();
        this.prop_caracteristicas = new SimpleStringProperty();
        this.prop_tipo = new SimpleStringProperty();
    }
    
    public Herramienta(Integer id, String nombre, Integer stock_critico, ObservableList<Tipo> tipos, ObservableList<Medida> medidas) {
        this.id = id;
        this.tipos = tipos;
        this.nombre = nombre;
        this.medidas = medidas;
        this.stock_critico = stock_critico;
        
        this.prop_nombre = new SimpleStringProperty();
        this.prop_caracteristicas = new SimpleStringProperty();
        this.prop_tipo = new SimpleStringProperty();
    }
 
    //getters
    
    public Integer getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public ObservableList<Tipo> getTipos() {
        return tipos;
    }
    
    public ObservableList<Medida> getMedidas() {
        return medidas;
    }

    public Integer getStockCritico() {
        return stock_critico;
    }
    
    public String getTipoString(){
        List<String> t_str = new ArrayList();
        tipos.forEach(tipo -> {
            t_str.add(tipo.getNombre());
        });
        return String.join("\n", t_str);
    }
    
    public String getCaracteristicas(){
        List<String> car = new ArrayList();
        medidas.forEach(medida->{
            car.add(medida.toString());
        });
        
        return String.join("\n", car);        
    }
    
    //setters
    
    public void setId(Integer id){
        this.id = id;
    }
    
    //tableview
    public StringProperty getPropiedadNombre() {
        prop_nombre.set(nombre);
        return prop_nombre;
    }
    
    public StringProperty getPropiedadTipo(){
        prop_tipo.set(getTipoString());
        return prop_tipo;
    }

    public StringProperty getPropiedadCaracteristicas() {
        prop_caracteristicas.set(getCaracteristicas());
        return prop_caracteristicas;
    }

    public void setId(int id) {
        this.id = id;
    }
}
