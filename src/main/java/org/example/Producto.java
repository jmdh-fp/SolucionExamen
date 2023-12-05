package org.example;

import java.awt.*;

public class Producto {
    private String nombre;
    private String familia;
    private double precio;

    public Producto(String nombre, String familia, double precio) {
        this.nombre = nombre;
        this.familia = familia;
        this.precio = precio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFamilia() {
        return familia;
    }

    public void setFamilia(String familia) {
        this.familia = familia;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    @Override
    public String toString() {
        return new StringBuilder().append(nombre).append(";").append(familia).append(";").append(precio).toString();
    }
}
