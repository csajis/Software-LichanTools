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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import lichantools.modelos.Forma;
import lichantools.modelos.Herramienta;
import lichantools.modelos.HerramientaPañol;
import lichantools.modelos.Medida;
import lichantools.modelos.Pañol;
import lichantools.modelos.Tipo;
import lichantools.modelos.Unidad;
import lichantools.servicios.DBConnection;

/**
 *
 * @author mainuser
 */
public class GestorHerramientas{
    private static GestorHerramientas gestor;
    private final ObservableMap<Integer, Herramienta> herramientas;
    private final ObservableMap<Integer, Tipo> tipos;
    private final ObservableMap<Integer, Forma> formas;
    private final ObservableMap<Integer, Unidad> unidades;
    
    private GestorHerramientas(){
        herramientas = FXCollections.observableHashMap();
        tipos = FXCollections.observableHashMap();
        formas = FXCollections.observableHashMap();
        unidades = FXCollections.observableHashMap();
    }
    
    public static GestorHerramientas get(){
        if(gestor == null){
            gestor = new GestorHerramientas();
        }
        
        return gestor;
    }

    public ObservableMap<Integer, HerramientaPañol> obtenerPorPañol(Pañol pañol){
        ObservableMap<Integer, HerramientaPañol> herr = FXCollections.observableHashMap();
 
        try{
            Connection db = DBConnection.get();
                
            String query = "SELECT * "+
                           "FROM `Herr-Pañol` "+
                           "WHERE `Herr-Pañol`.Herrp_pan=?";
                
            PreparedStatement ps = db.prepareStatement(query);
            Integer pañol_id = pañol.getId();
                
            ps.setInt(1, pañol_id);
                
            ResultSet rs = ps.executeQuery();
                
            while(rs.next()){
                Integer herr_id = rs.getInt("Herrp_her");
                Integer stock_real = rs.getInt("Herrp_stockR");
                Integer stock_total = rs.getInt("Herrp_stockT");
                   
                System.out.println("id: "+ herr_id);
                
                Herramienta h = obtenerPorId(herr_id);    
                HerramientaPañol hp = new HerramientaPañol(h, stock_total, stock_real);
                
                herr.put(herr_id, hp);
            }
        }
        catch(Exception ex){
            System.err.println(ex.getMessage());
        }
    
        return herr;
    }
    
    public ObservableList<Herramienta> obtenerHerramientas(){
        if(herramientas.isEmpty()){
            try{
                Connection db = DBConnection.get();

                String query = "SELECT * FROM Herramienta";

                Statement stmt = db.createStatement();
                ResultSet rs = stmt.executeQuery(query);

                while(rs.next()){
                    Integer id = rs.getInt("Herr_id");
                    String nombre = rs.getString("Herr_nombre");
                    Integer stock_critico = rs.getInt("Herr_stock_crit");

                    //obtener tipos                    
                    ObservableList<Tipo> lista_tipos = FXCollections.observableArrayList();
                    
                    String query1 = "SELECT * "+
                                    "FROM `Herr-Tipo`, TipoH "+
                                    "WHERE `Herr-Tipo`.HerrT_herr=? "+
                                    "AND `Herr-Tipo`.HerrT_tipo=TipoH.Tipoh_id";

                    PreparedStatement ps= db.prepareStatement(query1);
                    ps.setInt(1, id);
                    
                    ResultSet rs2 = ps.executeQuery();                    

                    while(rs2.next()){
                        Integer t_id = rs2.getInt("Tipoh_id");
                        String t_nom = rs2.getString("Tipoh_nom");  
                        Tipo tipo = new Tipo(t_id, t_nom); 
                        lista_tipos.add(tipo);
                    }
                    
                    //obtener caracteristicas
                    ObservableList<Medida> lista_medidas = FXCollections.observableArrayList();
                    
                    String query2 = "SELECT * "+
                                    "FROM `Herr-Form-Uni` "+
                                    "WHERE `Herr-Form-Uni`.HFU_herr=?";

                    ps = db.prepareStatement(query2);
                    ps.setInt(1, id);

                    ResultSet rs3 = ps.executeQuery();

                    while(rs3.next()){
                        Integer med_id = rs3.getInt("HFU_form_med");
                        Integer u_id= rs3.getInt("HFU_uni");
                        Float valor = rs3.getFloat("HFU_valor");
                        
                        Forma f = obtenerFormaPorId(med_id);
                        Unidad u = obtenerUnidadPorId(u_id);
                        
                        Medida m = new Medida(f, valor, u);
                        lista_medidas.add(m);
                    }

                    Herramienta h = new Herramienta(id, nombre, stock_critico, lista_tipos, lista_medidas);
                    herramientas.put(id, h);
                }
            }
            catch(Exception ex){
                System.err.println(ex.getMessage());
            }
        }
        
        return FXCollections.observableArrayList(herramientas.values());
    }
    
    
    public Herramienta obtenerPorId(Integer id) throws SQLException, ClassNotFoundException{
        if(herramientas.isEmpty()){
            obtenerHerramientas();
        }
        return herramientas.get(id);
    }
    
    public int ingresarHerramienta(Herramienta herr) {
        try {
            Connection db = DBConnection.get();
            String query = "INSERT INTO Herramienta ( Herr_nombre, Herr_stock_crit )" + 
                           "VALUES ( ?, ?) ";
            
            PreparedStatement ps = db.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            
            ps.setString(1, herr.getNombre());
            ps.setInt(2, herr.getStockCritico());
            
            ps.execute();
            
            ResultSet claves = ps.getGeneratedKeys();
            
            if (claves.next()){
                herr.setId(claves.getInt(1));
            }
            
            herramientas.put(herr.getId(), herr);
            
            ObservableList<Medida> medidas = herr.getMedidas();
            ObservableList<Tipo> tipos = herr.getTipos();
            
            for(Medida m: medidas) {
                
                query = "INSERT INTO `Herr-Form-Uni` (HFU_herr, HFU_uni, HFU_form_med, HFU_valor) "+
                        "VALUES (?, ?, ?, ?) ";
                
                ps = db.prepareStatement(query);
                
                ps.setInt(1, herr.getId());
                ps.setInt(2, m.getUnidad().getId());
                ps.setInt(3, m.getForma().getId());
                ps.setFloat(4, m.getValor());
                
                ps.execute();
            }
            
            for(Tipo t: tipos) {
                
                query = "INSERT INTO `Herr-Tipo` (HerrT_tipo, HerrT_herr) "+
                        "VALUES (?, ?) ";
                
                ps = db.prepareStatement(query);
                
                ps.setInt(1, t.getId());
                ps.setInt(2, herr.getId());
                
                ps.execute();
            }
            
        }
        catch(Exception ex){
            System.err.println(ex.getMessage());
        }
        
        return herr.getId();
    }

    public void eliminarHerramienta(Herramienta herr) {
        if (herramientas.containsKey(herr.getId())) {
            try{
                Connection db = DBConnection.get();
                String query = "DELETE FROM Herramienta "+
                               "WHERE Herr_id=? ";
                
                PreparedStatement ps = db.prepareStatement(query);
                ps.setInt(1, herr.getId());
                
                ps.execute();
                
                herramientas.remove(herr.getId());              
            }
            catch(Exception ex){
                System.err.println(ex.getMessage());
            }
        }
    }
    
    public void actualizarHerramienta(Herramienta anterior, Herramienta nuevo) {
        try {
            Connection db = DBConnection.get();
            String query = "UPDATE Herramienta "+
                           "SET Herr_nombre=?, Herr_stock_crit=? "+
                           "WHERE Herr_id=?";
                 
            PreparedStatement ps = db.prepareStatement(query);
            
            ps.setString(1, nuevo.getNombre());
            ps.setInt(2, nuevo.getStockCritico());
            ps.setInt(3, anterior.getId());
            
            ps.executeUpdate();
            
            herramientas.replace(anterior.getId(), nuevo);
            
            ObservableList<Medida> medidas = anterior.getMedidas();
            ObservableList<Tipo> tiposh = anterior.getTipos();
            
            for(Medida m: medidas) {
                
                query = "DELETE FROM `Herr-Form-Uni` "+
                        "WHERE HFU_herr=? AND HFU_uni=? AND HFU_form_med=?";
                
                ps = db.prepareStatement(query);
                
                ps.setInt(1, nuevo.getId());
                ps.setInt(2, m.getUnidad().getId());
                ps.setInt(3, m.getForma().getId());
                
                ps.execute();
            }
            
            for(Tipo t: tiposh) {
                
                query = "DELETE FROM `Herr-Tipo` "+
                        "WHERE HerrT_tipo=? AND HerrT_herr=?";
                
                ps = db.prepareStatement(query);
                
                ps.setInt(1, t.getId());
                ps.setInt(2, nuevo.getId());
                
                ps.execute();
            }
            
            medidas = nuevo.getMedidas();
            tiposh = nuevo.getTipos();
            
            for(Medida m: medidas) {
                
                query = "INSERT INTO `Herr-Form-Uni` (HFU_herr, HFU_uni, HFU_form_med, HFU_valor) "+
                        "VALUES (?, ?, ?, ?) ";
                
                ps = db.prepareStatement(query);
                
                ps.setInt(1, anterior.getId());
                ps.setInt(2, m.getUnidad().getId());
                ps.setInt(3, m.getForma().getId());
                ps.setFloat(4, m.getValor());
                
                ps.execute();
            }
            
            for(Tipo t: tiposh) {
                
                query = "INSERT INTO `Herr-Tipo` (HerrT_tipo, HerrT_herr) "+
                        "VALUES (?, ?) ";
                
                ps = db.prepareStatement(query);
                
                ps.setInt(1, t.getId());
                ps.setInt(2, anterior.getId());
                
                ps.execute();
            }
            
        }
        catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }
    
    public void actualizarStock(HerramientaPañol hp, Integer n){
        try{
            Connection db = DBConnection.get();
            String query = "UPDATE `Herr-Pañol` "+
                           "SET Herrp_StockT=?, Herrp_StockR=? "+
                           "WHERE Herrp_her=? AND Herrp_pan=?";
            
            PreparedStatement ps = db.prepareStatement(query);
            
            ps.setInt(1, hp.getStockTotal() + n - hp.getStockReal());
            ps.setInt(2, n);
            ps.setInt(3, hp.getHerr().getId());
            ps.setInt(4, GestorSesion.get().obtenerPañol().getId());
            
            ps.executeUpdate();
            
            hp.setStockReal(n);
            hp.setStockTotal(hp.getStockTotal() + n - hp.getStockReal());
        }
        catch(Exception ex){
            System.err.println(ex.getMessage());
        }
    }
    
        public void aumentarStock(HerramientaPañol hp, Integer n){
        try{
            Connection db = DBConnection.get();
            String query = "UPDATE `Herr-Pañol` "+
                           "SET Herrp_StockR=? "+
                           "WHERE Herrp_her=? AND Herrp_pan=?";
            
            PreparedStatement ps = db.prepareStatement(query);
            
            ps.setInt(1, hp.getStockReal() + n);
            ps.setInt(2, hp.getHerr().getId());
            ps.setInt(3, GestorSesion.get().obtenerPañol().getId());
            
            ps.executeUpdate();
            
            hp.setStockReal(hp.getStockReal() + n);
        }
        catch(Exception ex){
            System.err.println(ex.getMessage());
        }
    }
    
    public ObservableList<Tipo> obtenerListaTipos(){
        if(tipos.isEmpty()){
            try{
                Connection db = DBConnection.get();
                
                String query = "SELECT * FROM TipoH";

                Statement stmt = db.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                
                while(rs.next()){
                    Integer id = rs.getInt("Tipoh_id");
                    String nombre = rs.getString("Tipoh_nom");
                    Tipo tipo = new Tipo(id, nombre);
                    tipos.put(id, tipo);
                }
            }
            catch(Exception ex){
                System.err.println(ex.getMessage());
            }
        }
        return FXCollections.observableArrayList(tipos.values());
    }

    public ObservableList<Unidad> obtenerListaUnidades() {
        if(unidades.isEmpty()) {
            try {
                Connection db = DBConnection.get();
                
                String query = "SELECT * FROM Unidad";
                
                Statement stmt = db.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                
                while(rs.next()) {
                    Integer id = rs.getInt("Uni_id");
                    String nombre = rs.getString("Uni_nom");
                    Unidad unidad = new Unidad(id, nombre);
                    unidades.put(id, unidad);
                }
            }
            catch(Exception ex) {
                System.err.println(ex.getMessage());
            }
        }
        
        return FXCollections.observableArrayList(unidades.values());
    }
    
    public ObservableList<Forma> obtenerListaFormas() {
        if(unidades.isEmpty()) {
            try {
                Connection db = DBConnection.get();
                
                String query = "SELECT * FROM `Forma Medicion`";
                
                Statement stmt = db.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                
                while(rs.next()) {
                    Integer id = rs.getInt("Form-med_id");
                    String nombre = rs.getString("Form-med_nom");
                    Forma forma = new Forma(id, nombre);
                    formas.put(id, forma);
                }
            }
            catch(Exception ex) {
                System.err.println(ex.getMessage());
            }
        }
        
        return FXCollections.observableArrayList(formas.values());
    }
    
    public Forma obtenerFormaPorId(Integer id) throws SQLException, ClassNotFoundException{
        if(formas.isEmpty()){
            obtenerListaFormas();
        }
        return formas.get(id);
    }
        
    public Unidad obtenerUnidadPorId(Integer id) throws SQLException, ClassNotFoundException{
        if(unidades.isEmpty()){
            obtenerListaUnidades();
        }
        
        return unidades.get(id);
    }
}
