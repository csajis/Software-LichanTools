/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lichantools.modelos;

import java.util.Objects;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
/**
 *
 * @author mainuser
 */
public class PresHerr {
    Herramienta herramienta;
    Integer cant_inicial;
    Integer cant_final;
    StringProperty prop_nombre;
    IntegerProperty prop_cantidad;
    
    public PresHerr(Herramienta herr, int canti, int cantf){
        this.herramienta = herr;
        this.cant_inicial = canti;
        this.cant_final = cantf;
        this.prop_cantidad = new SimpleIntegerProperty(-1);
    }

    public void setCantidadInicial(Integer cant_inicial) {
        this.cant_inicial = cant_inicial;
    }

    public void setCantidadFinal(Integer cant_final) {
        this.cant_final = cant_final;
    }
    
    public Herramienta obtenerHerramienta(){
        return herramienta;
    }
    
    public Integer obtenerCantidadPrestada(){
        return (cant_inicial - cant_final);
    }

    public Integer obtenerCantidadInicial() {
        return cant_inicial;
    }

    public Integer obtenerCantidadFinal() {
        return cant_final;
    }
    
    public StringProperty obtenerPropiedadNombre(){
        if(prop_nombre == null){
            prop_nombre = new SimpleStringProperty(herramienta.getNombre());
        }
        
        return prop_nombre;
    }
    
    public void devolver(Integer n){
        if(n <= obtenerCantidadPrestada()){
            cant_final += n;
            prop_cantidad.set(obtenerCantidadPrestada());
        }
    }
    
    public ObservableValue<Integer> obtenerPropiedadCantidad(){
        if(prop_cantidad.get() == -1){
            prop_cantidad.set(obtenerCantidadPrestada());
        }
        
        return prop_cantidad.asObject();
    }
    
    public void agregarCantidad(Integer n){
        prop_cantidad.setValue(obtenerCantidadPrestada() + n);
        cant_final = cant_final - n;
    }
    
    public Boolean estaCompleto(){
        return Objects.equals(cant_inicial, cant_final);
    }
}
