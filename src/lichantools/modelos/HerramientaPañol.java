/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lichantools.modelos;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author mainuser
 */
public class HerramientaPañol{
    private Herramienta herr;
    private Integer stock_total;
    private Integer stock_real;
    
    private IntegerProperty prop_stock;
    private IntegerProperty prop_stockt;
 
    public HerramientaPañol(Herramienta herr, Integer stock_total, Integer stock_real) {
        this.herr = herr;
        this.stock_total = stock_total;
        this.stock_real = stock_real;
    }
    
    //getters
    public Integer getStockTotal() {
        return stock_total;
    }

    public Integer getStockReal() {
        return stock_real;
    }
    
    public Herramienta getHerr(){
        return herr;
    }
    
    //setters
    public void setStockTotal(Integer stock_total) {
        this.stock_total = stock_total;
    }

    public void setStockReal(Integer stock_real) {
        this.stock_real = stock_real;
        prop_stock.setValue(stock_real);
    }
   
    //utilidades
    public void reducirStockReal(Integer n){
        stock_real = stock_real - n;
        prop_stock.setValue(stock_real);
    }
    
    public void aumentarStockReal(Integer n){
        stock_real = stock_real + n;
        prop_stock.setValue(stock_real);
    }
    
    //tableview
    public ObservableValue<Integer> getPropiedadStock() {
        if(prop_stock == null){
            prop_stock = new SimpleIntegerProperty(getStockReal());
        }
        
        return prop_stock.asObject();
    }
    
    public ObservableValue<Integer> getPropiedadStockTotal() {
        if(prop_stockt == null){
            prop_stockt = new SimpleIntegerProperty(getStockTotal());
        }
        
        return prop_stockt.asObject();
    }
}
