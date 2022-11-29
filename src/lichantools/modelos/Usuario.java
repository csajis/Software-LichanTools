/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lichantools.modelos;

/**
 *
 * @author mainuser
 */
public class Usuario {
    private String nombre;
    private String apellidop;
    private String apellidom;
    
    public Usuario(String nombre, String apellidop, String apellidom){
        this.nombre = nombre;
        this.apellidop = apellidop;
        this.apellidom = apellidom;
    }

    public String getNombre() {
        return nombre + ' ' + apellidop + ' ' + apellidom;
    }
    
    
}
