package com.example.apuertadetucasa;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Pedidos extends AppCompatActivity {
    private ImageButton imageButton2;
    private boolean isMenuVisible = false;
    private RecyclerView rvPedidos;
    private List<Pedido> listaPedidos;
    private PedidosAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pedidos);

        imageButton2 = findViewById(R.id.imageButton2);
        rvPedidos = findViewById(R.id.rvPedidos);
        rvPedidos.setLayoutManager(new LinearLayoutManager(this));

        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMenu();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Cargar pedidos
        cargarPedidos();
    }

    private void cargarPedidos() {
        listaPedidos = new ArrayList<>();
        try {
            GestionSQLiteOpenHelper gestion = new GestionSQLiteOpenHelper(this, "Usuarios", null, 1);
            SQLiteDatabase BaseDeDatos = gestion.getReadableDatabase();

            // Verificar si la tabla existe
            Cursor tableCursor = BaseDeDatos.rawQuery(
                    "SELECT name FROM sqlite_master WHERE type='table' AND name='T_PEDIDOS'", null);

            if (tableCursor != null && tableCursor.getCount() > 0) {
                tableCursor.close();

                // Imprimir columnas de la tabla
                Cursor columnCursor = BaseDeDatos.rawQuery("PRAGMA table_info(T_PEDIDOS)", null);
                Log.d("DATABASE_DEBUG", "Columnas en T_PEDIDOS:");
                while (columnCursor.moveToNext()) {
                    Log.d("DATABASE_DEBUG", columnCursor.getString(1));
                }
                columnCursor.close();

                // Consulta para traer todos los pedidos
                Cursor cursor = BaseDeDatos.query("T_PEDIDOS", null, null, null, null, null, null);

                Log.d("DATABASE_DEBUG", "Número de pedidos: " + cursor.getCount());

                while (cursor.moveToNext()) {
                    // Imprimir todos los valores de las columnas
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        Log.d("DATABASE_DEBUG",
                                "Columna " + cursor.getColumnName(i) +
                                        ": " + cursor.getString(i));
                    }

                    // Verificar la existencia de cada columna antes de acceder
                    int facturaIndex = cursor.getColumnIndex("factura");
                    int fechaIndex = cursor.getColumnIndex("fecha");
                    int customerNameIndex = cursor.getColumnIndex("customerName");
                    int productosIndex = cursor.getColumnIndex("productos");
                    int totalIndex = cursor.getColumnIndex("total");
                    int direccionIndex = cursor.getColumnIndex("direccion");

                    // Verificar que todos los índices sean válidos
                    if (facturaIndex != -1 && fechaIndex != -1 && customerNameIndex != -1 &&
                            productosIndex != -1 && totalIndex != -1 &&
                            direccionIndex != -1) {

                        Pedido pedido = new Pedido(
                                cursor.getString(facturaIndex),
                                cursor.getString(fechaIndex),
                                cursor.getString(customerNameIndex),
                                cursor.getString(productosIndex),
                                cursor.getString(direccionIndex),
                                cursor.getDouble(totalIndex)
                        );
                        listaPedidos.add(pedido);
                    } else {
                        Log.e("DATABASE_DEBUG", "Falta alguna columna en la tabla");
                    }
                }

                cursor.close();
            } else {
                Log.e("DATABASE_DEBUG", "La tabla T_PEDIDOS no existe");
                if (tableCursor != null) tableCursor.close();
            }

            BaseDeDatos.close();

            // Configurar adaptador
            adapter = new PedidosAdapter(listaPedidos, this);
            rvPedidos.setAdapter(adapter);

        } catch (Exception e) {
            Log.e("DATABASE_ERROR", "Error al cargar pedidos", e);
        }
    }

    private void toggleMenu() {
        if (!isMenuVisible) {
            showMenuFragment();
        } else {
            hideMenuFragment();
        }
        isMenuVisible = !isMenuVisible;
    }

    private void showMenuFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Animación de entrada desde la derecha
        fragmentTransaction.setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_right,
                R.anim.slide_in_right,
                R.anim.slide_out_right
        );

        MenuFragment menuFragment = new MenuFragment();
        fragmentTransaction.add(R.id.main, menuFragment, "menu_fragment");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void hideMenuFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        MenuFragment menuFragment = (MenuFragment) fragmentManager.findFragmentByTag("menu_fragment");
        if (menuFragment != null) {
            fragmentManager.beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_right,
                            R.anim.slide_in_right,
                            R.anim.slide_out_right
                    )
                    .remove(menuFragment)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (isMenuVisible) {
            hideMenuFragment();
            isMenuVisible = false;
        } else {
            super.onBackPressed();
        }
    }
}