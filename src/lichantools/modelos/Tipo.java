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
public class Tipo {
    Integer id;
    String nombre;
    StringProperty prop_nombre;
        
    public Tipo(Integer id, String nombre){
        this.id = id;
        this.nombre = nombre;
    }

    //getters
    public String getNombre() {
       return nombre;
    }

    public Integer getId() {
        return id;
    }
            

    public StringProperty getPropiedadNombre() {
        if(prop_nombre == null){
            prop_nombre = new SimpleStringProperty(nombre);
        }
            
        return prop_nombre;
    }
}

