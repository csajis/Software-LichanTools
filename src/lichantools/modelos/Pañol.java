/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lichantools.modelos;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author mainuser
 */
public class Pañol {
    Integer id;
    String nombre;
    String ubicacion;
    String especialidad;
    String especialidad_id;
    StringProperty prop_nombre;
    StringProperty prop_esp;

    public Pañol(Integer id, String nombre, String especialidad_id, String especialidad, String ubicacion) {
        this.id = id;
        this.nombre = nombre;
        this.especialidad = especialidad;
        this.especialidad_id = especialidad_id;
        this.ubicacion = ubicacion;
    }

    public Integer getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEspecialidad() {
        return especialidad;
    }    

    public String getUbicacion() {
        return ubicacion;
    }

    public String getEspecialidadId() {
        return especialidad_id;
    }

    public void setId(Integer id) {
        this.id = id;
    }    

    public StringProperty getPropiedadNombre() {
        if(prop_nombre == null){
            prop_nombre = new SimpleStringProperty(nombre);
        }
        
        return prop_nombre;
    }

    public StringProperty getPropiedadEspecialidad() {
        if(prop_esp == null){
            prop_esp = new SimpleStringProperty(especialidad);
        }
        
        return prop_esp;    
    }
}
