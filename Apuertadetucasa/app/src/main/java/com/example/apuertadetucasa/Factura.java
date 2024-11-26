package com.example.apuertadetucasa;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Factura extends AppCompatActivity {
    private static final int DOMICILIO = 10000; // Costo fijo del domicilio
    private TextView tvNomClienteF, tvNFactura, tvDireccionF;
    private TextView tvProductoF, tvPrecioF, tvRecargoF, tvValorF;
    private String nombre, numeroFactura, fecha, direccion;
    private double total;
    private ArrayList<String> productosLista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Configuración de borde a borde para mejorar la UI
        setContentView(R.layout.activity_factura);

        Intent intent = getIntent(); // Obtener datos del Intent
        nombre = intent.getStringExtra("nombre");
        direccion = intent.getStringExtra("direccion");
        productosLista = new ArrayList<>();

        // Inicializar las vistas
        initializeViews();

        // Mostrar el nombre del cliente y la dirección
        tvNomClienteF.setText("Hola, " + nombre);
        tvDireccionF.setText(direccion);

        // Obtener los productos enviados desde el Intent
        ArrayList<Producto> productos = getIntent().getParcelableArrayListExtra("productos_carrito");
        for (Producto producto : productos) {
            productosLista.add(producto.toString());
        }

        // Generar y mostrar los detalles de la factura
        generarDetallesFactura(productos);

        // Ajustar márgenes para botones con bordes dinámicos
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.btnConfirmar), (v, insets) -> {
            ViewCompat.setPaddingRelative(v, insets.getSystemWindowInsetLeft(),
                    insets.getSystemWindowInsetTop(),
                    insets.getSystemWindowInsetRight(),
                    insets.getSystemWindowInsetBottom());
            return insets;
        });
    }

    // Inicializar las referencias de las vistas
    private void initializeViews() {
        tvNomClienteF = findViewById(R.id.tvNomClienteF);
        tvNFactura = findViewById(R.id.tvNFactura);
        tvDireccionF = findViewById(R.id.txtDireccionF);
        tvProductoF = findViewById(R.id.tvProductoF);
        tvPrecioF = findViewById(R.id.tvPrecioF);
        tvRecargoF = findViewById(R.id.tvRecargoF);
        tvValorF = findViewById(R.id.tvValorF);
    }

    // Generar los detalles de la factura
    private void generarDetallesFactura(ArrayList<Producto> productos) {
        // Generar un número único para la factura
        numeroFactura = generarNumeroFactura();
        tvNFactura.setText(numeroFactura);

        // Calcular el subtotal de los productos
        double subtotal = calcularSubtotal(productos);

        // Construir el detalle de los productos
        StringBuilder productosDetalle = new StringBuilder();
        for (Producto producto : productos) {
            productosDetalle.append(producto.getNombre())
                    .append(" x")
                    .append(producto.getCantidad())
                    .append("\n");
        }
        tvProductoF.setText(productosDetalle.toString().trim());

        // Mostrar subtotal
        tvPrecioF.setText(String.format("Subtotal: $%.2f", subtotal));

        // Mostrar recargo por domicilio
        tvRecargoF.setText(String.format("Recargo por Domicilio: $%d", DOMICILIO));

        // Calcular el total (subtotal + domicilio)
        total = subtotal + DOMICILIO;
        tvValorF.setText(String.format("Total: $%.2f", total));
    }

    // Calcular el subtotal de los productos
    private double calcularSubtotal(ArrayList<Producto> productos) {
        double subtotal = 0;
        for (Producto producto : productos) {
            subtotal += producto.getPrecioNumerico() * producto.getCantidad();
        }
        return subtotal;
    }

    // Generar un número único de factura basado en la fecha y un número aleatorio
    private String generarNumeroFactura() {
        SecureRandom random = new SecureRandom(); // Generador de números aleatorios
        int numeroAleatorio = random.nextInt(900000) + 100000; // Genera un número de 6 dígitos

        // Obtener la fecha actual en formato "yyyyMMdd"
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        fecha = sdf.format(new Date());

        return fecha + "-" + numeroAleatorio; // Combinar fecha con número aleatorio
    }

    // Confirmar el pedido y guardarlo en la base de datos
    public void btnConfirmarPedido(View view) {
        // Verificar si la dirección no está vacía
        if (!tvDireccionF.getText().toString().trim().isEmpty()) {
            String direccion = tvDireccionF.getText().toString().trim();

            // Obtener productos del Intent y generar una lista con nombres y cantidades
            ArrayList<Producto> productos = getIntent().getParcelableArrayListExtra("productos_carrito");
            ArrayList<String> nombresProductos = new ArrayList<>();

            for (Producto producto : productos) {
                nombresProductos.add(producto.getNombre() + " (x" + producto.getCantidad() + ")");
            }

            // Unir los nombres de los productos con comas
            String productosTexto = String.join(", ", nombresProductos);

            // Crear una conexión a la base de datos SQLite
            GestionSQLiteOpenHelper gestion = new GestionSQLiteOpenHelper(this, "Usuarios", null, 1);
            SQLiteDatabase BaseDeDatos = gestion.getWritableDatabase();

            // Preparar los valores a insertar
            ContentValues registro = new ContentValues();
            registro.put("factura", numeroFactura);
            registro.put("fecha", fecha);
            registro.put("customerName", nombre);
            registro.put("productos", productosTexto);
            registro.put("total", total);
            registro.put("direccion", direccion);

            // Insertar el registro en la tabla T_PEDIDOS
            BaseDeDatos.insert("T_PEDIDOS", null, registro);
            BaseDeDatos.close(); // Cerrar la conexión

            // Mostrar un mensaje de confirmación
            Toast.makeText(this, "Pedido Realizado", Toast.LENGTH_SHORT).show();
        } else {
            // Mostrar un mensaje de error si la dirección está vacía
            Toast.makeText(this, "No se puede registrar el pedido. \n Ingrese la dirección", Toast.LENGTH_SHORT).show();
        }
    }
}