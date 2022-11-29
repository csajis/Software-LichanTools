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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import lichantools.modelos.HerramientaPañol;
import lichantools.modelos.Pañol;
import lichantools.modelos.Usuario;
import lichantools.servicios.DBConnection;

/**
 *
 * @author mainuser
 */
public class GestorSesion {
    private static GestorSesion sesion;
    private ObservableMap<Integer, HerramientaPañol> herr_sesion;
    private Usuario usuario;
    private Pañol pañol;
    private String tipo;
    private String institucion;
    
    private GestorSesion(){
        usuario = new Usuario("mr", "robot", "tester");
        pañol = null;
        tipo = "";
    }
    
    static public GestorSesion get(){
        if(sesion == null){
            sesion = new GestorSesion();
        }
        
        return sesion;
    }
    public String obtenerInstitucion(){
        return institucion;
    }
    
    public Usuario obtenerUsuario(){
        return usuario;
    }
    
    public String obtenerTipo(){
        return tipo;
    }
    
    public ObservableList<HerramientaPañol> obtenerHerramientasSesion(){
        return FXCollections.observableArrayList(herr_sesion.values());
    }
    
    public Pañol obtenerPañol() {
        return pañol;
    }
    
    public String iniciarSesion(String nombre_usuario, String contraseña){
        try { 
            Connection db = DBConnection.get();
            
            //Verifica si es un encargado
            PreparedStatement ps = db.prepareStatement("SELECT * FROM Encargado WHERE Enc_rut=? AND Enc_contra=?");
            
            ps.setString(1, nombre_usuario);
            ps.setString(2, contraseña);
            
            ResultSet rs = ps.executeQuery();
            
            if(rs.next())
            {
                String nombre = rs.getString("Enc_nom");
                String apellidop = rs.getString("Enc_apellidop");
                String apellidom = rs.getString("Enc_apellidom");
                Integer pañol_id = rs.getInt("Enc_pan");
                
                usuario = new Usuario(nombre, apellidop, apellidom);
                pañol = GestorPañoles.get().obtenerPorId(pañol_id);
                tipo =  "encargado";
                System.out.println("ENCARGADO");
                herr_sesion = GestorHerramientas.get().obtenerPorPañol(pañol);
                return tipo;
            }
            
            //Verifica si es una cuenta administrador
            ps = db.prepareStatement("SELECT * FROM Administrador, Institucion WHERE Adm_rut=? AND Adm_contra=? AND Adm_inst=Ins_id");

            ps.setString(1, nombre_usuario);
            ps.setString(2, contraseña);

            rs = ps.executeQuery();

            if(rs.next())
            {
                String nombre = rs.getString("Adm_nom");
                String apellidop = rs.getString("Adm_apellidop");
                String apellidom = rs.getString("Adm_apellidom");
                String ins = rs.getString("Ins_nombre");
                
                usuario = new Usuario(nombre, apellidop, apellidom);
                institucion = ins;
                tipo = "administrador";
                
                return tipo;
            }
        } 
        catch(Exception ex) {
            System.err.println(ex.getMessage());
        }
        
        return "";
    }
    
    public void cerrarSesion() throws SQLException, ClassNotFoundException{
        DBConnection.get().close();
    }

    public HerramientaPañol obtenerHerrPorId(Integer herr_id) {
        if(herr_sesion.isEmpty()){
            obtenerHerramientasSesion();
        }
        
        return herr_sesion.get(herr_id);
    }
}
