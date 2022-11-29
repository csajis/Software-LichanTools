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
public class PresGenHerr{
    HerramientaPañol herramienta;
    Integer cantidad;

    public PresGenHerr(HerramientaPañol herramienta, Integer cantidad) {
        this.herramienta = herramienta;
        this.cantidad = cantidad;
    }

    public HerramientaPañol getHerramienta() {
        return herramienta;
    }

    public Integer getCantidad() {
        return cantidad;
    }
    
    
}
