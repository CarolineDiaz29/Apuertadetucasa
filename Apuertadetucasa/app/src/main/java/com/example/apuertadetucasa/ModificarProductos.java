package com.example.apuertadetucasa;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class ModificarProductos extends AppCompatActivity {

    EditText txtPrecio, txtStock;
    ImageView ivFoto;
    byte[] imagenActualizada;
    AutoCompleteTextView autoCompleteTextView;
    ArrayList<String> options = new ArrayList<>();
    String nombre;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_modificar_productos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtPrecio = findViewById(R.id.txtPrecioM);
        txtStock = findViewById(R.id.txtStockM);
        ivFoto = findViewById(R.id.imageView11);
        autoCompleteTextView = findViewById(R.id.txtNombreM);

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

                // Traer los datos del producto seleccionado desde la base de datos
                obtenerDatosProducto(nombreProductoSeleccionado);
            }
        });

        ivFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirCamara();
            }
        });

    }

    private void abrirCamara() {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(i,1);
    }

    //Metodo para colocar la imagen tomada en el campo ImageView
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imgBitmap = (Bitmap) extras.get("data");
            ivFoto.setImageBitmap(imgBitmap);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            imgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            imagenActualizada = byteArrayOutputStream.toByteArray();
        }
    }

    public void obtenerDatosProducto(String nombreProducto) {
        GestionSQLiteOpenHelper gestion = new GestionSQLiteOpenHelper(this,"Usuarios",null,1);
        //Abrir la BD en modo Lectura
        SQLiteDatabase BaseDeDatos = gestion.getReadableDatabase();
        //Buscaremos los datos del producto seleccionado
        Cursor fila = BaseDeDatos.rawQuery("SELECT precio, stock, imagen FROM T_PRODUCTOS WHERE nombre = ?", new String[]{nombreProducto});
        if (fila.moveToFirst()) {
            String precio = fila.getString(0);
            String stock = fila.getString(1);
            byte[] imagen = fila.getBlob(2);
            bitmap = BitmapFactory.decodeByteArray(imagen, 0, imagen.length);

            //Se colocan los datos obtenidos en los EditText
            txtPrecio.setText(precio);
            txtStock.setText(stock);
            ivFoto.setImageBitmap(bitmap);
        }
    }

    public void btnModificar(View view) {
        if (options.isEmpty()) {
            Toast.makeText(this, "No registra productos", Toast.LENGTH_SHORT).show();
            return;
        } else if(!options.contains(autoCompleteTextView.getText().toString())) {
            Toast.makeText(this, "Por favor, selecciona una opción válida", Toast.LENGTH_SHORT).show();
            return;
        } else {
            if (imagenActualizada == null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                imagenActualizada = byteArrayOutputStream.toByteArray();
            }
            GestionSQLiteOpenHelper gestion = new GestionSQLiteOpenHelper(this,"Usuarios",null,1);
            //Abrir la BD en modo Lectura/Escritura
            SQLiteDatabase BaseDeDatos = gestion.getWritableDatabase();
            //Guardamos en variables si escribieron en EditText para modificar
            String precio = txtPrecio.getText().toString();
            String stock = txtStock.getText().toString();

            //Actualizar en base de datos con consulta UPDATE
            BaseDeDatos.execSQL("UPDATE T_PRODUCTOS SET precio = ?, stock = ?, imagen = ? WHERE nombre = ?", new Object[]{precio, stock, imagenActualizada, nombre});

            // Confirmar que la actualización fue exitosa
            Toast.makeText(this, "Producto actualizado correctamente", Toast.LENGTH_SHORT).show();
            new android.os.Handler().postDelayed(() -> {
                Intent i = new Intent(this, Pedidos.class);
                startActivity(i);
                finish();
            }, 1000);

            // Cerrar la base de datos
            BaseDeDatos.close();
        }
    }
}