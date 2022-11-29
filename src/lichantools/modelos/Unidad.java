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
public class Unidad {
    private Integer id;
    private String str;

    public Unidad(Integer id, String str) {
        this.id = id;
        this.str = str;
    }

    public Integer getId() {
        return id;
    }

    public String getStr() {
        return str;
    }
}
