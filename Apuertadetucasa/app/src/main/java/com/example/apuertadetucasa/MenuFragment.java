package com.example.apuertadetucasa;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class MenuFragment extends Fragment {

    private TextView tvAgregarProductos;
    private TextView tvModificarProductos;
    private TextView tvEliminarProductos;
    private TextView tvCerrarSesion;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        // Añadir click listener al fondo semi-transparente
        view.setOnClickListener(v -> {
            if (getActivity() instanceof Pedidos) {
                ((Pedidos) getActivity()).onBackPressed();
            }
        });

        // Evitar que los clics en el menú cierren el fragmento
        view.findViewById(R.id.menuLinearLayout).setOnClickListener(v -> {
            // No hacer nada, esto evita que el clic se propague al fondo
        });
        initViews(view);
        setupListeners();

        return view;
    }


    private void initViews(View view) {
        tvAgregarProductos = view.findViewById(R.id.tvAgregarProductos);
        tvModificarProductos = view.findViewById(R.id.tvModificarProductos);
        tvEliminarProductos = view.findViewById(R.id.tvEliminarProductos);
        tvCerrarSesion = view.findViewById(R.id.tvCerrarSesion);
    }

    private void setupListeners() {
        tvAgregarProductos.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), RegistrarProductos.class);
            startActivity(intent);
        });

        tvModificarProductos.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ModificarProductos.class);
            startActivity(intent);
        });

        tvEliminarProductos.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EliminarProducto.class);
            startActivity(intent);
        });

        tvCerrarSesion.setOnClickListener(v -> {
            // Aquí agregamos la lógica para cerrar sesión
            Toast.makeText(requireContext(), "Cerrando sesión...", Toast.LENGTH_SHORT).show();

            // Navegar al LoginActivity y limpiar el stack de actividades
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            // Cerrar la actividad actual si es necesario
            if (getActivity() != null) {
                getActivity().finish();
            }
        });
    }
}