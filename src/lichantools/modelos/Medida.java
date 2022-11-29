/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lichantools.modelos;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author mainuser
 */
public class Medida {
    private Forma forma;
    private Float valor;
    private Unidad unidad;
    
    private StringProperty prop_nom;
    private FloatProperty prop_valor;
    private StringProperty prop_uni;

    public Medida(Forma forma, Float valor, Unidad unidad) {
        this.forma = forma;
        this.valor = valor;
        this.unidad = unidad;
    }

    public Forma getForma() {
        return forma;
    }

    public Float getValor() {
        return valor;
    }

    public Unidad getUnidad() {
        return unidad;
    }

    public StringProperty getPropiedadNombre() {
        if(prop_nom == null){
            prop_nom = new SimpleStringProperty(forma.getNombre());
        }
        return prop_nom;
    }

    public ObservableValue<Float> getPropiedadValor() {
        if(prop_valor == null){
            prop_valor = new SimpleFloatProperty(valor);
        }
        
        return prop_valor.asObject();
    }

    public StringProperty getPropiedadUnidad() {
        if(prop_uni == null){
            prop_uni = new SimpleStringProperty(unidad.getStr());
        }
        
        return prop_uni; 
    }
    
    @Override
    public String toString(){
        return forma.getNombre() + ": " + Float.toString(valor) + unidad.getStr();
    }
    
}
