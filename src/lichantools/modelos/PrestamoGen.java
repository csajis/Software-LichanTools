/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lichantools.modelos;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

/**
 *
 * @author mainuser
 */
public class PrestamoGen {
    Integer id;
    private String nombre;
    private final ObservableMap<Integer, PresGenHerr> herramientas;
    
 
    public PrestamoGen(Integer id, String nombre){
        this.id = id;
        this.nombre = nombre;
        herramientas = FXCollections.observableHashMap();
    }

    /**
     * @return the id
     */
    public Integer obtenerId() {
        return id;
    }

    /**
     * @return the name
     */
    public String obtenerNombre(){
        return nombre;
    }

    /**
     * @return the tools
     */
    public ObservableList<PresGenHerr> obtenerHerramientas() {
        return FXCollections.observableArrayList(herramientas.values());
    }
    
    public void agregarHerramienta(Integer id, PresGenHerr p){
        herramientas.put(id, p);
    }
}
