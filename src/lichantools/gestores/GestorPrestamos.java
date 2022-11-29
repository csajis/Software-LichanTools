/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lichantools.gestores;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import lichantools.modelos.Herramienta;
import lichantools.modelos.Persona;
import lichantools.modelos.PresHerr;
import lichantools.modelos.Prestamo;
import lichantools.servicios.DBConnection;

/**
 *
 * @author mainuser
 */
public class GestorPrestamos{
    private static GestorPrestamos gestor;
    private final ObservableMap<Integer, Prestamo> prestamos;
    
    private GestorPrestamos(){
        prestamos = FXCollections.observableHashMap();
    }
    
    public static GestorPrestamos get(){
        if(gestor == null){
            gestor = new GestorPrestamos();
        }
        
        return gestor;
    }

    public ObservableList<Prestamo> obtenerPrestamos() throws SQLException, ClassNotFoundException{
        if(prestamos.isEmpty()){
            try{
                Connection db = DBConnection.get();
                Statement stmt = db.createStatement();
                
                ResultSet rs = stmt.executeQuery("SELECT * FROM Prestamo");
                
                while(rs.next()){
                    Integer id = rs.getInt("Pres_id");
                    String estado = rs.getString("Pres_estado");
                    Date fecha = rs.getDate("Pres_fecha");
                    String tipo = rs.getString("Pres_tipo");
                    String rut = rs.getString("Pres_per");
                    
                    if(estado.equals("Devuelto")){
                        continue;
                    }
                   
                    Prestamo p = new Prestamo(id, estado, fecha, tipo, rut);
                    
                    String query = "SELECT * "+
                                   "FROM `Pres-Herr` "+
                                   "WHERE `Pres-Herr`.PresH_pres=?";
                    
                    PreparedStatement ps = db.prepareStatement(query);
                    ps.setInt(1, id);
                    
                    ResultSet rs2 = ps.executeQuery();
                    
                    while(rs2.next()){
                        Integer herr_id = rs2.getInt("PresH_herr");
                        Herramienta herr = GestorHerramientas.get().obtenerPorId(herr_id);
                        Integer canti = rs2.getInt("PresH_herr_canti");
                        Integer cantf = rs2.getInt("PresH_herr_cantf");
                        
                        PresHerr ph = new PresHerr(herr, canti, cantf);
                        p.agregarHerramienta(ph);
                    }
                    
                    prestamos.put(id, p);
                }
            }
            catch(SQLException ex){
                    throw new SQLException(ex);
            }
        }
        
        return FXCollections.observableArrayList(prestamos.values());
    }

    
    //retorna id de préstamo creado
    public int crearPrestamo(Prestamo prestamo) {
        Integer id = null;
        
        try{
            if(prestamos.isEmpty()){
                obtenerPrestamos();
            }
            
            Connection db = DBConnection.get();            
            String query = "INSERT INTO Prestamo (Pres_estado, Pres_fecha, Pres_pan, Pres_tipo, Pres_per) "+
                           "VALUES (?, ?, ?, ?, ?)";
            
            System.out.println("Verificando prestamo");
            
            PreparedStatement ps = db.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            
            String estado = "Nodevuelto";
            Timestamp fecha = new Timestamp(prestamo.obtenerFecha().getTime());
            Integer pañol_id = GestorSesion.get().obtenerPañol().getId();
            String tipo = prestamo.obtenerTipo();
            String rut = prestamo.obtenerRut();
            
            System.out.println("Verificando prestamo");
            
            ps.setString(1, estado);
            ps.setTimestamp(2, fecha);
            ps.setInt(3, pañol_id);
            ps.setString(4, tipo);
            ps.setString(5, rut);
            
            ps.execute();
            
            ResultSet claves = ps.getGeneratedKeys();
            
            if (claves.next()){
                prestamo.setId(claves.getInt(1));
            }
            
            System.out.println("Insert Realizado");
            
            query = "INSERT INTO `Pres-Herr` (PresH_pres, PresH_herr, PresH_herr_canti, PresH_herr_cantf) "+
                    "VALUES (?, ?, ?, ?)";
            
            ps = db.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            
            ps.setInt(1, prestamo.getId());
            
            for(PresHerr p: prestamo.obtenerHerramientas()){
                Integer herr_id = p.obtenerHerramienta().getId();
                Integer canti = p.obtenerCantidadInicial();
                Integer cantf = p.obtenerCantidadFinal();
                
                ps.setInt(2, herr_id);
                ps.setInt(3, canti);
                ps.setInt(4, cantf);
                
                ps.execute();
                
                query = "UPDATE `Herr-Pañol` "+
                        "SET Herrp_StockR=? "+
                        "WHERE Herrp_her=? AND Herrp_pan=?";
                
                PreparedStatement ps2 = db.prepareStatement(query);
                
                ps2.setInt(1, cantf);
                ps2.setInt(2, herr_id);
                ps2.setInt(3, pañol_id);
                
                ps2.executeUpdate();
            }

            prestamos.put(id, prestamo);
        }
        catch(Exception ex){
            System.err.println(ex.getMessage());
        }
        
        return prestamo.getId();
    }
    
    public ObservableList<Prestamo> obtenerPorPersona(Persona persona){
        ObservableList<Prestamo> pres_persona = FXCollections.observableArrayList();
        
        if(prestamos.isEmpty()){
            try{
                Connection db = DBConnection.get();
                String query = "SELECT * FROM Prestamo WHERE Pres_per=? AND Pres_pan=?";
                
                PreparedStatement ps = db.prepareStatement(query);
                
                ps.setString(1, persona.getRut());
                ps.setInt(2, GestorSesion.get().obtenerPañol().getId());
                
                ResultSet rs = ps.executeQuery();
                
                while(rs.next()){
                    Integer id = rs.getInt("Pres_id");
                    String estado = rs.getString("Pres_estado");
                    Date fecha = rs.getDate("Pres_fecha");
                    String tipo = rs.getString("Pres_tipo");
                    String rut = rs.getString("Pres_per");
                   
                    Prestamo p = new Prestamo(id, estado, fecha, tipo, rut);
                    
                    query = "SELECT * "+
                            "FROM `Pres-Herr` "+
                            "WHERE `Pres-Herr`.PresH_pres=?";
                    
                    ps = db.prepareStatement(query);
                    ps.setInt(1, id);
                    
                    ResultSet rs2 = ps.executeQuery();
                    
                    while(rs2.next()){
                        Integer herr_id = rs2.getInt("PresH_herr");
                        Herramienta herr = GestorHerramientas.get().obtenerPorId(herr_id);
                        Integer canti = rs2.getInt("PresH_herr_canti");
                        Integer cantf = rs2.getInt("PresH_herr_cantf");
                        
                        PresHerr ph = new PresHerr(herr, canti, cantf);
                        p.agregarHerramienta(ph);
                    }
                    
                    pres_persona.add(p);
                }
            }
            catch(Exception ex){
                  System.err.println(ex.getMessage());
            }
            
            return pres_persona;
        }
        
        return FXCollections.observableArrayList(prestamos.values());
    }
    
     public void devolverPrestamo(Prestamo prestamo) {
        try {
            ObservableList<PresHerr> presh = prestamo.obtenerHerramientas();
            
            Connection db = DBConnection.get();
            
            String query = "UPDATE `Pres-Herr` "+
                           "SET PresH_herr_cantf=? "+
                           "WHERE PresH_pres=? AND PresH_herr=?";
            
            PreparedStatement ps = db.prepareStatement(query);
            
            Boolean estado = true;
            
            for(PresHerr ph : presh) {
                ps.setInt(1, ph.obtenerCantidadFinal());
                ps.setInt(2, prestamo.getId());
                ps.setInt(3, ph.obtenerHerramienta().getId());
                
                ps.executeUpdate();
                
                if(!ph.estaCompleto()){
                    estado = false;
                }
            }
            
            if(estado == true) {
                query = "UPDATE Prestamo "+
                        "SET Pres_estado=? "+
                        "WHERE Pres_id=?";
                
                ps = db.prepareStatement(query);
                
                ps.setString(1, "Devuelto");
                ps.setInt(2, prestamo.getId());
                
                ps.executeUpdate();
            }

        }
        catch(Exception ex) {
            System.err.println(ex.getMessage());
        }
    }
}
