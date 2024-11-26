package com.example.apuertadetucasa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class carrito extends AppCompatActivity implements CarritoAdapter.OnTotalChangedListener {
    private RecyclerView recyclerViewCarrito;
    private TextView tvTotalCarrito, tvNombreCliente;
    private CarritoAdapter carritoAdapter;
    private String nombre, direccion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_carrito);

        Intent intent = getIntent();

        nombre = intent.getStringExtra("nombre");
        direccion = intent.getStringExtra("direccion");
        tvNombreCliente = findViewById(R.id.tvNomClienteC);
        tvNombreCliente.setText("Hola, " + nombre);

        //Configurar RecyclerView para los artículos del carrito
        recyclerViewCarrito = findViewById(R.id.recyclerViewCarrito);
        recyclerViewCarrito.setLayoutManager(new LinearLayoutManager(this));

        //Obtiene los artículos del carrito y configura el adaptador con el listador de cambios totales
        carritoAdapter = new CarritoAdapter(
                CarritoManager.getInstance().getProductosEnCarrito(),
                this // OnTotalChangedListener
        );
        recyclerViewCarrito.setAdapter(carritoAdapter);

        //Configurar la vista de texto del precio total
        tvTotalCarrito = findViewById(R.id.tvTotalCarrito);
        actualizarTotal();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.btnConfirmar), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onTotalChanged() {
        actualizarTotal();
    }

    private void actualizarTotal() {
        double total = CarritoManager.getInstance().calcularTotal();
        tvTotalCarrito.setText(String.format("Total: $%.2f", total));
    }

    public void btnRealizarPedido(View v) {
        // Obtiene la lista de productos en el carrito
        ArrayList<Producto> productosEnCarrito = new ArrayList<>(CarritoManager.getInstance().getProductosEnCarrito());

        // Verifica si el carrito está vacío
        if (productosEnCarrito.isEmpty()) {
            // Muestra un mensaje (Toast) si el carrito está vacío
            Toast.makeText(this, "No puede generar una factura sin productos en el carrito", Toast.LENGTH_SHORT).show();
        } else {
            // Si el carrito no está vacío, procede a la actividad Factura
            Intent i = new Intent(this, Factura.class);
            i.putExtra("nombre", nombre);
            i.putExtra("direccion", direccion);
            // Pasa los productos del carrito a la actividad Factura
            i.putParcelableArrayListExtra("productos_carrito", productosEnCarrito);
            startActivity(i);
        }
    }
}