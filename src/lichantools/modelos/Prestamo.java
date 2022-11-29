/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lichantools.modelos;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author mainuser
 */
public class Prestamo {
    private Integer id;
    private String estado;
    private Date fecha;
    private Integer pañol_id;
    private String tipo;
    private String rut;
    private ObservableList<PresHerr> herramientas;
    private IntegerProperty prop_id;
    private StringProperty prop_fecha;
    
    public Prestamo(String tipo, String rut, ObservableList<PresHerr> herr){
        this.tipo = tipo;
        this.rut = rut;
        this.herramientas = herr;
        this.estado = "Nodevuelto";
        this.fecha = new Date();
    }
    
    public Prestamo(Integer id, String estado, Date fecha, String tipo, String rut){
        this.id = id;
        this.estado = estado;
        this.fecha = fecha;
        this.tipo  = tipo;
        this.rut = rut;
        this.herramientas = FXCollections.observableArrayList();
    }

    //setters 
    public void setId(Integer id) {
        this.id = id;
    }
    
    public void agregarHerramienta(PresHerr tool){
        herramientas.add(tool);
    }
    
    public String obtenerRut(){
        return rut;
    }

    public ObservableList<PresHerr> obtenerHerramientas() {
       return herramientas;
    }

    public String obtenerTipo() {
        return tipo;
    }
    
    public Date obtenerFecha() {
        return fecha;
    }
    
    public ObservableValue<Integer> obtenerPropiedadId(){
        if(prop_id == null){
            prop_id = new SimpleIntegerProperty(id);
        }
        
        return prop_id.asObject();
    }
    
    public StringProperty obtenerPropiedadFecha(){
        if(prop_fecha == null){
            String fec = new SimpleDateFormat("dd-MM-yyyy").format(fecha);
            prop_fecha = new SimpleStringProperty(fec);
        }
        return prop_fecha;
    }

    public Integer getId() {
        return id;
    }
    
    public Boolean estaDevuelto(){
        return "Devuelto".equals(estado);
    }
    
}
