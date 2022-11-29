/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lichantools.gestores;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import lichantools.modelos.Especialidad;
import lichantools.modelos.HerramientaPañol;
import lichantools.modelos.Pañol;
import lichantools.servicios.DBConnection;

/**
 *
 * @author mainuser
 */
public class GestorPañoles {
    private static GestorPañoles gestor;
    private ObservableMap<Integer, Pañol> pañoles;
    private ObservableMap<String, Especialidad> especialidades;
    
    private GestorPañoles(){
        pañoles = FXCollections.observableHashMap();
        especialidades = FXCollections.observableHashMap();

    }
    
    public static GestorPañoles get(){
        if(gestor==null){
            gestor = new GestorPañoles();
        }
        
        return gestor;
    }
    
    public ObservableList<Pañol> obtenerPañoles(){
        if(pañoles.isEmpty()){
            try{
                Connection db = DBConnection.get();
                Statement stmt = db.createStatement();
                String query = "SELECT * "+
                               "FROM Pañol, Especialidad "+
                               "WHERE Pan_esp=Esp_id";

                ResultSet rs = stmt.executeQuery(query);

                while(rs.next()){
                    Integer id = rs.getInt("Pan_id");
                    String nombre = rs.getString("Pan_nom");
                    String esp_id = rs.getString("Esp_id");
                    String especialidad = rs.getString("Esp_nom");
                    String ubicacion = rs.getString("Pan_ubi");

                    Pañol p = new Pañol(id, nombre, esp_id, especialidad, ubicacion);
                    pañoles.put(id, p);
                }

            }
            catch(Exception ex){
                System.err.println(ex.getMessage());
            }
        }
              
        return FXCollections.observableArrayList(pañoles.values());
    }
    
    public ObservableList<Especialidad> obtenerEspecialidades(){
        if(especialidades.isEmpty()){
            try{
                Connection db = DBConnection.get();
                Statement stmt = db.createStatement();
                String query = "SELECT * FROM Especialidad";

                ResultSet rs = stmt.executeQuery(query);

                while(rs.next()){
                    String id = rs.getString("Esp_id");
                    String nombre = rs.getString("Esp_nom");

                    Especialidad e = new Especialidad(nombre, id);
                    especialidades.put(id, e);
                }

            }
            catch(Exception ex){
                System.err.println(ex.getMessage());
            }
        }
        
        return FXCollections.observableArrayList(especialidades.values());
    }
    
    public Especialidad obtenerEspecialidadPorId(String id){
        if(especialidades.isEmpty()){
            obtenerEspecialidades();
        }
        
        return especialidades.get(id);
    }
    
    public Pañol obtenerPorId(Integer id){
        if(pañoles.isEmpty()){
            obtenerPañoles();
        }
        return pañoles.get(id);
    }

    public void actualizarPañol(Pañol anterior, Pañol nuevo){
        try{
            Connection db = DBConnection.get();
            String query = "UPDATE Pañol "+
                           "SET Pan_nom=?, Pan_ubi=?, Pan_esp=? "+
                           "WHERE Pan_id=?";
            
            PreparedStatement ps = db.prepareStatement(query);
            ps.setString(1, nuevo.getNombre());
            ps.setString(2, nuevo.getUbicacion());
            ps.setString(3, nuevo.getEspecialidadId());
            ps.setInt(4, anterior.getId());
            
            ps.executeUpdate();
            
            pañoles.replace(anterior.getId(), nuevo);
        }
        catch(Exception ex){
            System.err.println(ex.getMessage());
        }
    }
    
    public void eliminarPañol(Pañol pañol){
        if(pañoles.containsKey(pañol.getId())){
            try{
                Connection db = DBConnection.get();
                String query = "DELETE FROM Pañol "+
                               "WHERE Pan_id=?";

                PreparedStatement ps = db.prepareStatement(query);
                ps.setInt(1, pañol.getId());
                ps.execute();
                pañoles.remove(pañol.getId());
            }
            catch(Exception ex){
                System.err.println(ex.getMessage());
            }
        }
    }
    
    public int ingresarPañol(Pañol pañol){     
        try {
            Connection db = DBConnection.get();
            String query = "INSERT INTO Pañol(Pan_nom, Pan_ubi, Pan_esp) "+
                           "VALUES(?, ?, ?)";
            
            PreparedStatement ps = db.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            
            System.out.println(pañol.getNombre());
            System.out.println(pañol.getEspecialidadId());
            
            ps.setString(1, pañol.getNombre());
            ps.setString(2, pañol.getUbicacion());
            ps.setString(3, pañol.getEspecialidadId());
            
            ps.execute();
            
            ResultSet claves = ps.getGeneratedKeys();
            
            if(claves.next()){
                pañol.setId(claves.getInt(1));
            }
            
            pañoles.put(pañol.getId(), pañol);
            
            return pañol.getId();
        }
        catch(Exception ex) {
            System.err.println(ex.getMessage());
        }      
        
        return -1;
    }
    
    public void actualizarHerramientaPañol(Pañol pañol, ObservableList<HerramientaPañol> anterior, ObservableList<HerramientaPañol> nuevo) {
        try {
            Connection db = DBConnection.get();
            PreparedStatement ps;
            String query = "DELETE FROM `Herr-Pañol` "+
                           "WHERE Herrp_her=? AND Herrp_pan=?";
                    
            String query1 = "INSERT INTO `Herr-Pañol` (Herrp_pan, Herrp_her, Herrp_stockT, Herrp_stockR) "+
                           "VALUES (?, ?, ?, ?)";
            
            
            for(HerramientaPañol hp: anterior) {
                
                ps = db.prepareStatement(query);
                
                ps.setInt(1, hp.getHerr().getId());
                ps.setInt(2, pañol.getId());
                
                ps.execute();
            }
            
            for(HerramientaPañol hp: nuevo) {
                
                ps = db.prepareStatement(query1);
                
                ps.setInt(1, pañol.getId());
                ps.setInt(2, hp.getHerr().getId());
                ps.setInt(3, hp.getStockTotal());
                ps.setInt(4, hp.getStockReal());
                
                ps.executeUpdate();
            }
                    
        }
        catch(Exception ex) {
            System.err.println(ex.getMessage());
        }
    }
}
