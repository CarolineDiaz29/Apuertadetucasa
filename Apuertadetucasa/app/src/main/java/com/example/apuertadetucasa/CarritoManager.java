package com.example.apuertadetucasa;

import java.util.ArrayList;
import java.util.List;

public class CarritoManager {
    private static CarritoManager instance; // Instancia única del carrito (Singleton)
    private List<Producto> productosEnCarrito; // Lista de productos en el carrito

    // Constructor privado para implementar el patrón Singleton
    private CarritoManager() {
        productosEnCarrito = new ArrayList<>(); // Inicializa la lista de productos
    }

    // Método para obtener la instancia única del carrito
    public static synchronized CarritoManager getInstance() {
        if (instance == null) {
            instance = new CarritoManager(); // Crea la instancia si no existe
        }
        return instance; // Devuelve la instancia
    }

    // Método para agregar un producto al carrito
    public void agregarProducto(Producto producto) {
        // Verifica si el producto ya está en el carrito
        for (Producto p : productosEnCarrito) {
            if (p.getNombre().equals(producto.getNombre())) { // Si el producto ya está
                p.incrementarCantidad(); // Incrementa su cantidad
                return; // Sale del método
            }
        }

        // Si el producto no está en el carrito, lo agrega con cantidad 1
        producto.setCantidad(1);
        productosEnCarrito.add(producto);
    }

    // Método para obtener la lista de productos en el carrito
    public List<Producto> getProductosEnCarrito() {
        return productosEnCarrito;
    }

    // Método para calcular el total del carrito
    public double calcularTotal() {
        double total = 0;
        for (Producto p : productosEnCarrito) {
            total += p.getPrecioNumerico() * p.getCantidad(); // Suma precio * cantidad
        }
        return total; // Devuelve el total
    }

    // Método para vaciar el carrito
    public void limpiarCarrito() {
        productosEnCarrito.clear(); // Limpia la lista de productos
    }
}
