/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lichantools.modelos;

import java.util.Date;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author mainuser
 */
public class Persona {
    private String rut;
    private String nombre;
    private String apellidom;
    private String apellidop;
    private Date fecha_nacimiento;
    private String num_telefono;
    private String direccion;
    private String correo;
    private String genero;
    private String tipo;
    private StringProperty nombre_completo;
    private StringProperty prop_rut;

    public Persona(String rut, String nombre, String apellidom, String apellidop, Date fecha_nacimiento, String num_telefono, String direccion, String correo, String genero, String tipo) {
        this.rut = rut;
        this.nombre = nombre;
        this.apellidom = apellidom;
        this.apellidop = apellidop;
        this.fecha_nacimiento = fecha_nacimiento;
        this.num_telefono = num_telefono;
        this.direccion = direccion;
        this.correo = correo;
        this.genero = genero;
        this.tipo = tipo;
    }
    
    public String getRut(){
        return rut;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @return the apellidom
     */
    public String getApellidom() {
        return apellidom;
    }

    /**
     * @return the apellidop
     */
    public String getApellidop() {
        return apellidop;
    }

    /**
     * @return the fecha_nacimiento
     */
    public Date getFecha_nacimiento() {
        return fecha_nacimiento;
    }

    /**
     * @return the num_telefono
     */
    public String getNum_telefono() {
        return num_telefono;
    }

    /**
     * @return the direccion
     */
    public String getDireccion() {
        return direccion;
    }

    /**
     * @return the correo
     */
    public String getCorreo() {
        return correo;
    }

    /**
     * @return the genero
     */
    public String getGenero() {
        return genero;
    }

    /**
     * @return the tipo
     */
    public String getTipo() {
        return tipo;
    }
    
    public StringProperty getPropiedadNombre(){
        if(nombre_completo == null){
            nombre_completo = new SimpleStringProperty(nombre + ' ' + apellidop + ' ' + apellidom);
        }
        
        return nombre_completo;
    }
    
    public StringProperty getPropiedadRut(){
        if(prop_rut == null){
            prop_rut = new SimpleStringProperty(rut);
        }
        
        return prop_rut;
    }
    
    public String getNombreCompleto(){
        return nombre_completo.get();
    }
}
