package com.example.apuertadetucasa;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
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

public class RegistrarProductos extends AppCompatActivity {

    EditText txtNombre, txtPrecio, txtStock;
    ImageView ivFoto;
    TextView tvAgregarFoto;
    byte[] imagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrar_productos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ivFoto = findViewById(R.id.imageView11);
        txtNombre = findViewById(R.id.txtNombreR);
        txtPrecio = findViewById(R.id.txtPrecioR);
        txtStock = findViewById(R.id.txtStockR);
        tvAgregarFoto = findViewById(R.id.tvFotoR);
        tvAgregarFoto.setPaintFlags(tvAgregarFoto.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        //Metodo para escuchar cuando de click en textView "AgregarFoto"
        tvAgregarFoto.setOnClickListener(new View.OnClickListener() {
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
            imagen = byteArrayOutputStream.toByteArray();
        }
    }

    //Metodo para agregar producto
    public void btnRegistrarProducto(View view) {
        GestionSQLiteOpenHelper gestion = new GestionSQLiteOpenHelper(this,"Usuarios",null,1);
        //Abrir la BD en modo Lectura/Escritura
        SQLiteDatabase BaseDeDatos = gestion.getWritableDatabase();
        //Capturamos los datos digitados por el usuario
        String nombre = txtNombre.getText().toString();
        String precio = txtPrecio.getText().toString();
        String stock = txtStock.getText().toString();
        //Verificar datos en los campos
        if (!nombre.isEmpty() && !precio.isEmpty() && ivFoto.getDrawable() != null) {
            //Armar un registro para llevarlo a la base de datos
            ContentValues registro = new ContentValues();
            registro.put("nombre", nombre);
            registro.put("precio", precio);
            registro.put("stock",stock);
            registro.put("imagen", imagen);
            //Llevar el registro a la tabla
            BaseDeDatos.insert("T_PRODUCTOS",null,registro);
            //Cerrar la BD
            BaseDeDatos.close();
            //Limpiar las cajas de texto
            txtNombre.setText("");
            txtPrecio.setText("");
            txtStock.setText("");
            ivFoto.setImageDrawable(null);
            Toast.makeText(this,"Producto guardado exitosamente",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,"Error!!! Completar información (Nombre, Precio, Imagen)",Toast.LENGTH_SHORT).show();
        }
    }
}