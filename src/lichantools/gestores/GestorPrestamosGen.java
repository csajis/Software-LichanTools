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
import lichantools.modelos.HerramientaPañol;
import lichantools.modelos.Persona;
import lichantools.modelos.PresGenHerr;
import lichantools.modelos.PresHerr;
import lichantools.modelos.Prestamo;
import lichantools.modelos.PrestamoGen;
import lichantools.servicios.DBConnection;

/**
 *
 * @author mainuser
 */
public class GestorPrestamosGen{
    private static GestorPrestamosGen gestor;
    private final ObservableMap<Integer, PrestamoGen> prestamos_gen;
    
    private GestorPrestamosGen(){
        prestamos_gen = FXCollections.observableHashMap();
    }
    
    static public GestorPrestamosGen get(){
        if(gestor == null){
            gestor = new GestorPrestamosGen();
        }
        
        return gestor;
    }
    
    public ObservableList<PrestamoGen> obtenerPrestamosGenericos() {
        if(prestamos_gen.isEmpty()){
            try{
                Connection db = DBConnection.get();
                Statement stmt = db.createStatement(); 
                String query = "SELECT * "+
                               "FROM `Prestamo-Gen`, `Presgen-Herr` "+
                               "WHERE `Prestamo-Gen`.Pgen_id=`Presgen-Herr`.PGH_pgen";
                
                ResultSet rs = stmt.executeQuery(query);
                
                while(rs.next()){
                    Integer id = rs.getInt("Pgen_id");
                                        
                    String nombre = rs.getString("Pgen_nom");
                    Integer herr_id = rs.getInt("PGH_herr");
                    Integer cant = rs.getInt("PGH_cant");
                    
                    PrestamoGen g = obtenerPorId(id);
                    
                    if(g == null){
                        g = new PrestamoGen(id, nombre);
                        prestamos_gen.put(id, g);
                    }
                    
                    HerramientaPañol h = GestorSesion.get().obtenerHerrPorId(herr_id);
                    PresGenHerr p = new PresGenHerr(h, cant);                    
                    g.agregarHerramienta(herr_id, p);
                }
                
            }
            catch(Exception ex){
                System.err.println(ex.getMessage());
            }
        }
        
        return FXCollections.observableArrayList(prestamos_gen.values());
    }
    
    public void crearPrestamoGen(PrestamoGen pres_gen, Persona persona){
        try{
            ObservableList<PresHerr> herr = FXCollections.observableArrayList();
            
            for(PresGenHerr p: pres_gen.obtenerHerramientas()){
                Integer stock = p.getHerramienta().getStockReal();
                PresHerr h = new PresHerr(p.getHerramienta().getHerr(), stock, stock - p.getCantidad());
                herr.add(h);
            }
            
            Prestamo pres =  new Prestamo("Gen", persona.getRut(), herr);
            Integer id = GestorPrestamos.get().crearPrestamo(pres);
            
            if(id != 0){
                Connection db = DBConnection.get();
                String query = "INSERT INTO `Pres-Presgen` (PPG_pres, PPG_pgen) "+
                               "VALUES (?, ?)";
                
                PreparedStatement ps = db.prepareStatement(query);
                
                ps.setInt(1, id);
                ps.setInt(2, pres_gen.obtenerId());
                
                ps.execute();
            }
            
        }
        catch(Exception ex){
            
        }
    }
    
    public PrestamoGen obtenerPorId(Integer id){
        return prestamos_gen.get(id);
    }
}
