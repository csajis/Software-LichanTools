/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lichantools.gestores;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import lichantools.modelos.Persona;
import lichantools.servicios.DBConnection;

/**
 *
 * @author mainuser
 */
public class GestorPersonas{
    private static GestorPersonas gestor;
    private final ObservableMap<String, Persona> personas;
    
    private GestorPersonas(){
        personas = FXCollections.observableHashMap();
    }
    
    public static GestorPersonas get(){
        if(gestor == null){
            gestor = new GestorPersonas();
        }
        
        return gestor;
    }

    
    public ObservableList<Persona> obtenerPersonas() {
        if(personas.isEmpty()){
            try{
                Connection db = DBConnection.get();
                Statement stmt = db.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM Persona");
                
                while(rs.next()){
                    String rut = rs.getString("Per_rut");
                    String nombre = rs.getString("Per_nombre");
                    String apellidop = rs.getString("Per_apellidop");
                    String apellidom = rs.getString("Per_apellidom");
                    Date fnac = rs.getDate("Per_fnac");
                    String tel = rs.getString("Per__tel");
                    String dir = rs.getString("Per__dir");
                    String correo = rs.getString("Per__email");
                    
                    System.out.println(nombre);

                    Persona p = new Persona(rut, nombre, apellidop, apellidom, fnac, tel, dir, correo, null, null);
                    personas.put(rut, p);
                }
            }
            catch(Exception ex){
                System.err.println(ex.getMessage()); 
            }
        }
        
        return FXCollections.observableArrayList(personas.values());
    }

    public Persona obtenerPersonaPorRut(String rut) throws SQLException, ClassNotFoundException{
        if(personas.isEmpty()){
            obtenerPersonas();
        }
        return personas.get(rut);
    }    
}
