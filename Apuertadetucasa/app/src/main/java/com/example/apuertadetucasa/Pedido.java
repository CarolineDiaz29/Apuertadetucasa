package com.example.apuertadetucasa;

public class Pedido {
    private String factura;
    private String fecha;
    private String customerName;
    private String productos;
    private double total;
    private String direccion;

    // Constructor con valores por defecto
    public Pedido(String factura, String fecha, String customerName,
                  String productos, String direccion, double total) {
        this.factura = factura != null ? factura : "Sin factura";
        this.fecha = fecha != null ? fecha : "Sin fecha";
        this.customerName = customerName != null ? customerName : "Sin nombre";
        this.productos = productos != null ? productos : "Sin productos";
        this.total = total;
        this.direccion = direccion != null ? direccion : "Sin direccion";
    }
    // Getters
    public String getFactura() { return factura; }
    public String getFecha() { return fecha; }
    public String getCustomerName() { return customerName; }
    public String getProductos() { return productos; }
    public double getTotal() { return total; }
    public String getDireccion() { return direccion; }
}