package com.example.apuertadetucasa;

import android.os.Parcel;
import android.os.Parcelable;

public class Producto implements Parcelable {
    private String nombre;
    private String precio;
    private int cantidad;
    private byte[] imagen;  //Controla la cantidad del prodcuto en el carro

    // Constructor existente
    public Producto(String nombre, String precio, byte[] imagen) {
        this.nombre = nombre;
        this.precio = precio;
        this.imagen = imagen;
        this.cantidad = 0;
    }

    public String getNombre() {
        return nombre;
    }

    public String getPrecio() {
        return "$" + precio;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioNumerico() {
        return Double.parseDouble(precio);
    }
    public void incrementarCantidad() {
        cantidad++;
    }

    public void decrementarCantidad() {
        if (cantidad > 0) {
            cantidad--;
        }
    }
    // Métodos Parcelable
    protected Producto(Parcel in) {
        nombre = in.readString();
        precio = in.readString();
        cantidad = in.readInt();

        // Leer imagen
        int imagenLength = in.readInt();
        if (imagenLength > 0) {
            imagen = new byte[imagenLength];
            in.readByteArray(imagen);
        }
    }

    public static final Parcelable.Creator<Producto> CREATOR = new Parcelable.Creator<Producto>() {
        @Override
        public Producto createFromParcel(Parcel in) {
            return new Producto(in);
        }

        @Override
        public Producto[] newArray(int size) {
            return new Producto[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nombre);
        dest.writeString(precio);
        dest.writeInt(cantidad);

        // Escribir imagen
        if (imagen != null) {
            dest.writeInt(imagen.length);
            dest.writeByteArray(imagen);
        } else {
            dest.writeInt(0);
        }
    }
}
