package com.example.apuertadetucasa;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class EliminarProducto extends AppCompatActivity {

    ImageView ivFoto;
    AutoCompleteTextView autoCompleteTextView;
    ArrayList<String> options = new ArrayList<>();
    String nombre = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_eliminar_producto);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ivFoto = findViewById(R.id.imageView11EliminarProducto2);
        autoCompleteTextView = findViewById(R.id.txtNombreE);

        GestionSQLiteOpenHelper gestion = new GestionSQLiteOpenHelper(this,"Usuarios",null,1);
        //Abrir la BD en modo Lectura
        SQLiteDatabase BaseDeDatos = gestion.getReadableDatabase();
        //Buscaremos los datos de los productos
        Cursor fila = BaseDeDatos.rawQuery("select * from T_PRODUCTOS",null);
        if (fila.moveToFirst()) {
            do {
                String nombre = fila.getString(1);
                //Agregar el nombre al ArrayList
                options.add(nombre);
            } while(fila.moveToNext()); //Se mueve a la siguiente fila
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, options);
        autoCompleteTextView.setAdapter(adapter);

        //Se utiliza seOnItemClickListener para tomar el nombre del producto que selecciono
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Obtener el nombre del producto seleccionado
                String nombreProductoSeleccionado = (String) adapterView.getItemAtPosition(i);
                nombre = nombreProductoSeleccionado;
            }
        });
    }

    public void btnEliminar(View view) {
        if (!nombre.isEmpty()) {
            GestionSQLiteOpenHelper gestion = new GestionSQLiteOpenHelper(this,"Usuarios",null,1);
            //Abrir la BD en modo Lectura/Escritura
            SQLiteDatabase BaseDeDatos = gestion.getWritableDatabase();
            int cantidad = BaseDeDatos.delete("T_PRODUCTOS","nombre=?",new String[]{nombre});
            //Cerrar base de datos y limpiar cajas de texto
            BaseDeDatos.close();
            if (cantidad == 1) {
                Toast.makeText(this,"Producto Eliminado satisfactoriamente",Toast.LENGTH_SHORT).show();
                new android.os.Handler().postDelayed(() -> {
                    Intent i = new Intent(this, Pedidos.class);
                    startActivity(i);
                    finish();
                }, 1000);
            }
        } else {
            Toast.makeText(this,"Error!!! Elija algún producto para eliminar",Toast.LENGTH_SHORT).show();
        }
    }
}