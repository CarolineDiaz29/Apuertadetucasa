package com.example.apuertadetucasa;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MenuCliente extends AppCompatActivity {
    private List<Producto> productos = new ArrayList<>();
    private TextView tvCerrarP, nombreCliente;
    private String nombre, direccion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_cliente);

        Intent intent = getIntent();
        String id = intent.getStringExtra("Id");
        direccion = intent.getStringExtra("direccion");
        nombreCliente = findViewById(R.id.tvNomCliente);

        GestionSQLiteOpenHelper gestion = new GestionSQLiteOpenHelper(this,"Usuarios",null,1);
        //Abrir la BD en modo Lectura/Escritura
        SQLiteDatabase BaseDeDatos = gestion.getWritableDatabase();
        Cursor fila = BaseDeDatos.rawQuery("select customerName from T_CLIENTES where customerId = ?",new String[]{id});
        //Verificar si se retornaron registros
        if (fila.moveToFirst()) {
            nombre = fila.getString(0);
            nombreCliente.setText("Hola, " + nombre);
        }

        tvCerrarP = findViewById(R.id.tvCerrarP);

        // Cargar productos desde la base de datos
        productos = obtenerProductos();

        // Configurar RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewProductos);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 columnas
        recyclerView.setAdapter(new ProductosAdapter(productos)); // Adaptador con la lista de productos

        //Configura el tv para cerrar la sesion del usuario
        tvCerrarP.setOnClickListener(v -> {
            // Mostrar mensaje de cierre de sesión
            Toast.makeText(this, "Cerrando sesión...", Toast.LENGTH_SHORT).show();

            // Navegar al LoginActivity y limpiar el stack de actividades
            Intent i = new Intent(this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);

            // Finalizar la actividad actual
            finish();
        });
    }

    //Metodo para llamar todod los prodcutos de las base de datos y ponerlo en un formato en especial que se encuentra en la clase producto
    private List<Producto> obtenerProductos() {
        List<Producto> productos = new ArrayList<>();
        GestionSQLiteOpenHelper gestion = new GestionSQLiteOpenHelper(this, "Usuarios", null, 1);
        Cursor cursor = gestion.getReadableDatabase().rawQuery("SELECT * FROM T_PRODUCTOS", null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String nombre = cursor.getString(cursor.getColumnIndex("nombre"));
                @SuppressLint("Range") String precio = cursor.getString(cursor.getColumnIndex("precio"));
                @SuppressLint("Range") byte[] imagen = cursor.getBlob(cursor.getColumnIndex("imagen"));

                productos.add(new Producto(nombre, precio, imagen)); // Crear objetos Producto
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            Toast.makeText(this, "No se encontraron productos", Toast.LENGTH_SHORT).show();
        }
        return productos;
    }

    //Metodo para enviar a el activity Carrito y envia el nombre y la direccion del cliente
    public void btnCarrito(View v) {
        Intent i = new Intent(this, carrito.class);
        i.putExtra("nombre", nombre);
        i.putExtra("direccion", direccion);
        startActivity(i);
    }
}