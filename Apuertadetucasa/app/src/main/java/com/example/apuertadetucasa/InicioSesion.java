package com.example.apuertadetucasa;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class InicioSesion extends AppCompatActivity {

    private EditText txtEmail, txtContraseña;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inicio_sesion);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.btnConfirmar), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtEmail = findViewById(R.id.txtEmail);
        txtContraseña = findViewById(R.id.txtPassword);
    }

    //Método para el boton de iniciar sesion
    public void btnIncioSesion (View v){
        try {
            GestionSQLiteOpenHelper gestion = new GestionSQLiteOpenHelper(this,"Usuarios",null,1);
            //Abrir la BD en modo Lectura/Escritura
            SQLiteDatabase BaseDeDatos = gestion.getWritableDatabase();
            //Buscaremos los datos de acuerdo al numero de email del artículo
            String email = txtEmail.getText().toString();
            String contraseña = txtContraseña.getText().toString();
            //Verificamos que el campo tenga información
            if (!email.isEmpty() && !contraseña.isEmpty()) {
                //Crear una variable tipo Cursor, que nos ayuda a seleccionar el registro por email del usuario
                //Usamos el metodo RawQuery que ayuda a ejecutar un SELECT
                Cursor fila = BaseDeDatos.rawQuery("select password, tipoUsuario, customerId from T_USUARIOS where email = ?",new String[]{email});
                //Verificar si se retornaron registros
                if (fila.moveToFirst()) {
                    //Verificamos que la contraseña sea la misma que la que esta en la base de datos
                    if (contraseña.equals(fila.getString(0))) {
                        if ("Administrador".equals(fila.getString(1))) {
                            Intent i = new Intent(this, Pedidos.class);
                            startActivity(i);
                            BaseDeDatos.close();
                        } else {
                            Cursor fila2 = BaseDeDatos.rawQuery("select address from T_CLIENTES where customerId = ?",new String[]{fila.getString(2)});
                            if (fila2.moveToFirst()) {
                                Intent i = new Intent(this, MenuCliente.class);
                                i.putExtra("Id", fila.getString(2));
                                i.putExtra("direccion", fila2.getString(0));
                                startActivity(i);
                                BaseDeDatos.close();
                            }
                        }
                    } else {
                        Toast.makeText(this,"Email o Contraseña incorrecta!!!",Toast.LENGTH_SHORT).show();
                        BaseDeDatos.close();
                    }
                } else {
                    Toast.makeText(this,"Usuario no encontrado",Toast.LENGTH_SHORT).show();
                    BaseDeDatos.close();
                }

            } else {
                Toast.makeText(this,"Error!!! Los Campos deben estar completados",Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
        }

    }
}