package com.example.apuertadetucasa;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Registrar extends AppCompatActivity {

    private EditText txtNombre, txtTelefono, txtEmail, txtContraseña, txtIdentificacion, txtDireccion;
    private RadioGroup radioGroup;
    private RadioButton radioButtonAdmin, radioButtonCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.btnConfirmar), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        radioGroup = findViewById(R.id.radioGroup2);
        radioButtonAdmin = findViewById(R.id.btnAdministrador);
        radioButtonCliente = findViewById(R.id.btnCliente);
        txtNombre = findViewById(R.id.txtNombre);
        txtIdentificacion = findViewById(R.id.txtIdentificacion);
        txtTelefono = findViewById(R.id.txtTelefono);
        txtEmail = findViewById(R.id.txtEmailR);
        txtContraseña = findViewById(R.id.txtPasswordR);
        txtDireccion = findViewById(R.id.txtDireccion);

        bloquearRadioButton();
    }

    //Metodo para el boton Registrar
    public void btnRegistrar (View v){
        GestionSQLiteOpenHelper gestion = new GestionSQLiteOpenHelper(this, "Usuarios", null, 1);
        //Abrir la BD en modo Lectura/Escritura
        SQLiteDatabase BaseDeDatos = gestion.getWritableDatabase();
        //Capturamos los datos digitados por el usuario
        String nombre = txtNombre.getText().toString();
        String identificacion = txtIdentificacion.getText().toString();
        String telefono = txtTelefono.getText().toString();
        String email = txtEmail.getText().toString();
        String contraseña = txtContraseña.getText().toString();
        String direccion = txtDireccion.getText().toString();
        String tipoUsuario = "";
        //Se verifica el radioButton cual fue seleccionado
        if (radioButtonAdmin.isChecked()) {
            tipoUsuario = "Administrador";  // El usuario es Administrador
        } else if (radioButtonCliente.isChecked()) {
            tipoUsuario = "Cliente";  // El usuario es Cliente
        }

        //Verificar datos en los campos
        if (!nombre.isEmpty() && !identificacion.isEmpty() && !telefono.isEmpty() && !email.isEmpty() && !contraseña.isEmpty() && !tipoUsuario.isEmpty()) {
            //Armar un registro para llevarlo a la base de datos
            ContentValues registro = new ContentValues();
            ContentValues registro2 = new ContentValues();
            registro.put("tipoUsuario", tipoUsuario);
            //registro.put("nombre", nombre);
            registro.put("email", email);
            registro.put("password", contraseña);
            if (tipoUsuario.equals("Administrador")) {
                registro.put("tipoUsuario", tipoUsuario);
                registro.put("adminId", identificacion);
                registro2.put("adminId", identificacion);
                registro2.put("adminName", nombre);
                BaseDeDatos.insert("T_ADMINISTRADOR",null,registro2);
                Intent i = new Intent(this, Pedidos.class);
                startActivity(i);
            } else {
                registro.put("tipoUsuario", tipoUsuario);
                registro.put("customerId", identificacion);
                registro2.put("customerId", identificacion);
                registro2.put("customerName", nombre);
                registro2.put("address", direccion);
                registro2.put("phone", telefono);
                BaseDeDatos.insert("T_CLIENTES",null,registro2);
                Intent i = new Intent(this, MenuCliente.class);
                i.putExtra("Id", identificacion);
                i.putExtra("direccion", direccion);
                startActivity(i);
            }
            //Llevar el registro a la tabla
            BaseDeDatos.insert("T_USUARIOS",null,registro);
            //Cerrar la BD
            BaseDeDatos.close();
            //Limpiar las cajas de texto
            txtNombre.setText("");
            txtIdentificacion.setText("");
            txtTelefono.setText("");
            txtEmail.setText("");
            txtContraseña.setText("");
            txtDireccion.setText("");
            radioGroup.clearCheck();
            Toast.makeText(this,"Usuario guardado exitosamente",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,"Error!!! Todos los campos deben tener información",Toast.LENGTH_SHORT).show();
        }
    }
    //Bloqueo del Radio button De administrador en caso de que ya exista un administrador
    private void bloquearRadioButton() {
        GestionSQLiteOpenHelper gestion = new GestionSQLiteOpenHelper(this,"Usuarios",null,1);
        //Abrir la BD en modo Lectura/Escritura
        SQLiteDatabase BaseDeDatos = gestion.getWritableDatabase();
        Cursor fila = BaseDeDatos.rawQuery("select * from T_ADMINISTRADOR",null);
        //Verificar si se retornaron registros
        if (fila.moveToFirst()) {
            radioButtonAdmin.setEnabled(false);
        }
    }
}