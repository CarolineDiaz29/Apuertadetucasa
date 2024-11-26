package com.example.apuertadetucasa;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductosAdapter extends RecyclerView.Adapter<ProductosAdapter.ProductoViewHolder> {
    private List<Producto> productos;

    public ProductosAdapter(List<Producto> productos) {
        this.productos = productos;
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.producto, parent, false);
        return new ProductoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        Producto producto = productos.get(position);

        // Establecer nombre y precio del producto
        holder.tvNombreProducto.setText(producto.getNombre());
        holder.tvPrecioProducto.setText(producto.getPrecio());

        //Convertir byte[] a Bitmap y establecer imagen
        if (producto.getImagen() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(producto.getImagen(), 0, producto.getImagen().length);
            holder.ivProducto.setImageBitmap(bitmap);
        }

        //Configurar el botón de añadir al carrito
        holder.btnAgregar.setOnClickListener(v -> {
            // Add product to cart
            CarritoManager.getInstance().agregarProducto(producto);

            // Show toast confirmation
            Toast.makeText(v.getContext(),
                    "Producto agregado al carrito: " + producto.getNombre(),
                    Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }
    //Organiza como se vera el producto en la vista del menu cliente
    static class ProductoViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProducto;
        TextView tvNombreProducto;
        TextView tvPrecioProducto;
        Button btnAgregar;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProducto = itemView.findViewById(R.id.ivProducto);
            tvNombreProducto = itemView.findViewById(R.id.tvNombreProducto);
            tvPrecioProducto = itemView.findViewById(R.id.tvPrecioProducto);
            btnAgregar = itemView.findViewById(R.id.btnAgregar);
        }
    }
}