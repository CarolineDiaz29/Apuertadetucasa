package com.example.apuertadetucasa;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CarritoAdapter extends RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder> {
    private List<Producto> productosEnCarrito; // Lista de productos en el carrito
    private OnTotalChangedListener totalChangedListener; // Listener para actualizar el total

    // Interfaz para notificar cambios en el total al padre
    public interface OnTotalChangedListener {
        void onTotalChanged(); // Método llamado cuando cambia el total
    }

    // Constructor que inicializa los productos y el listener
    public CarritoAdapter(List<Producto> productosEnCarrito, OnTotalChangedListener listener) {
        this.productosEnCarrito = productosEnCarrito;
        this.totalChangedListener = listener;
    }

    @NonNull
    @Override
    public CarritoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla el diseño de un elemento del carrito
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.car, parent, false);
        return new CarritoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarritoViewHolder holder, int position) {
        Producto producto = productosEnCarrito.get(position); // Obtiene el producto actual

        // Establece los detalles del producto
        holder.tvNombreProducto.setText(producto.getNombre());
        holder.tvPrecioProducto.setText(String.format("$%.2f", producto.getPrecioNumerico()));
        holder.tvCantidadProducto.setText(String.valueOf(producto.getCantidad()));

        // Convierte un array de bytes en un Bitmap y lo establece en la imagen
        if (producto.getImagen() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(producto.getImagen(), 0, producto.getImagen().length);
            holder.ivProducto.setImageBitmap(bitmap);
        }

        // Botón para incrementar la cantidad
        holder.btnIncrementar.setOnClickListener(v -> {
            producto.incrementarCantidad(); // Incrementa la cantidad del producto
            notifyItemChanged(position); // Notifica el cambio al adaptador
            if (totalChangedListener != null) {
                totalChangedListener.onTotalChanged(); // Actualiza el total
            }
        });

        // Botón para decrementar la cantidad
        holder.btnDecrementar.setOnClickListener(v -> {
            if (producto.getCantidad() > 1) { // Si la cantidad es mayor a 1
                producto.decrementarCantidad(); // Decrementa la cantidad
                notifyItemChanged(position); // Notifica el cambio al adaptador
                if (totalChangedListener != null) {
                    totalChangedListener.onTotalChanged(); // Actualiza el total
                }
            } else {
                // Si la cantidad llega a 0, elimina el producto del carrito
                productosEnCarrito.remove(position);
                notifyItemRemoved(position); // Notifica la eliminación
                notifyItemRangeChanged(position, productosEnCarrito.size()); // Actualiza las posiciones
                if (totalChangedListener != null) {
                    totalChangedListener.onTotalChanged(); // Actualiza el total
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return productosEnCarrito.size(); // Devuelve el número de productos en el carrito
    }

    // Clase interna para gestionar cada elemento del carrito
    static class CarritoViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProducto; // Imagen del producto
        TextView tvNombreProducto; // Nombre del producto
        TextView tvPrecioProducto; // Precio del producto
        TextView tvCantidadProducto; // Cantidad del producto
        ImageButton btnIncrementar; // Botón para incrementar la cantidad
        ImageButton btnDecrementar; // Botón para decrementar la cantidad

        public CarritoViewHolder(@NonNull View itemView) {
            super(itemView);
            // Inicializa las vistas
            ivProducto = itemView.findViewById(R.id.ivProductoCarrito);
            tvNombreProducto = itemView.findViewById(R.id.tvNombreProductoCarrito);
            tvPrecioProducto = itemView.findViewById(R.id.tvPrecioProductoCarrito);
            tvCantidadProducto = itemView.findViewById(R.id.tvCantidadProductoCarrito);
            btnIncrementar = itemView.findViewById(R.id.btnIncrementar);
            btnDecrementar = itemView.findViewById(R.id.btnDecrementar);
        }
    }
}