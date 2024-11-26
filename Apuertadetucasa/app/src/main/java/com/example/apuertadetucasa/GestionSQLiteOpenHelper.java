package com.example.apuertadetucasa;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class GestionSQLiteOpenHelper extends SQLiteOpenHelper {
    public GestionSQLiteOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //Tablas de administrador y clientes
        sqLiteDatabase.execSQL("CREATE TABLE T_ADMINISTRADOR (adminId INTEGER PRIMARY KEY, adminName TEXT)");
        sqLiteDatabase.execSQL("CREATE TABLE T_CLIENTES (customerId INTEGER PRIMARY KEY, customerName TEXT, address TEXT, phone REAL)");
        //Tabla de Usuarios para conectar con tabla administrador y clientes
        sqLiteDatabase.execSQL("CREATE TABLE T_USUARIOS " +
                "(Id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "tipoUsuario TEXT, " +
                "email TEXT, " +
                "password TEXT, " +
                "adminId INTEGER, " +
                "customerId INTEGER, " +
                "FOREIGN KEY (adminId) REFERENCES T_ADMINISTRADOR(adminId), " +
                "FOREIGN KEY (customerId) REFERENCES T_CLIENTES(customerId))");
        //Tabla de Productos
        sqLiteDatabase.execSQL("CREATE TABLE T_PRODUCTOS (productId INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT, precio REAL, stock INTEGER, imagen BLOB)");
        //Tabla de Pedidos
        sqLiteDatabase.execSQL("CREATE TABLE T_PEDIDOS (factura TEXT PRIMARY KEY, fecha TEXT, customerName TEXT, productos TEXT, total REAL, direccion TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}