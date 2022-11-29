/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lichantools.servicios;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author mainuser
 */
public class DBConnection {
    private static Connection db = null;
    private static final String URL = "jdbc:mysql://servicevpn.ddns.net:3306/LichanTools";
    private static final String USER = "admin";
    private static final String PASS = "lichantools";
   
    
    public static Connection get(){
        try{
            if(db == null || db.isClosed()){
                Class.forName("com.mysql.jdbc.Driver");
                
                System.out.println("intentando conectar");
                db = DriverManager.getConnection(URL, USER, PASS);
                System.out.println("conectado");
            } 
        }
        catch (Exception ex) {
            System.err.println(ex.getMessage());
        }

        return db;
    }
    
    public static void close(){
      if (db != null) {
         try{
            db.close();
         }
         catch(Exception ex){
             System.err.println(ex.getMessage());
         }
      }
   }
}
