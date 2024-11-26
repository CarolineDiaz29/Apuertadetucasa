package com.example.apuertadetucasa;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class PedidosAdapter extends RecyclerView.Adapter<PedidosAdapter.PedidoViewHolder> {
    private List<Pedido> pedidosList;
    private Context context;

    public PedidosAdapter(List<Pedido> pedidosList, Context context) {
        this.pedidosList = pedidosList;
        this.context = context;
    }

    @NonNull
    @Override
    public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_pedido, parent, false);
        return new PedidoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoViewHolder holder, int position) {
        Pedido pedido = pedidosList.get(position);

        holder.tvNumeroFactura.setText("Factura: " + pedido.getFactura());
        holder.tvFecha.setText("Fecha: " + pedido.getFecha());
        holder.tvNombreCliente.setText("Cliente: " + pedido.getCustomerName());
        holder.tvProductos.setText("Productos: " + pedido.getProductos());
        holder.tvTotal.setText(String.format("Total: $%.2f", pedido.getTotal()));
        holder.tvDireccion.setText("Dirección: " + pedido.getDireccion());

        // Añadir OnClickListener para abrir Google Maps con la dirección
        holder.itemView.setOnClickListener(v -> {
            String direccion = pedido.getDireccion();

            // Verificar que la dirección no esté vacía
            if (direccion != null && !direccion.trim().isEmpty()) {
                // Crear un URI de Google Maps con la dirección
                String uri = String.format("geo:0,0?q=%s", Uri.encode(direccion));

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");

                // Verificar si Google Maps está instalada
                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(intent);
                } else {
                    // Alternativa si Google Maps no está instalada
                    Uri fallbackUri = Uri.parse(
                            String.format("https://www.google.com/maps/search/?api=1&query=%s",
                                    Uri.encode(direccion))
                    );
                    Intent fallbackIntent = new Intent(Intent.ACTION_VIEW, fallbackUri);
                    context.startActivity(fallbackIntent);
                }
            } else {
                // Mostrar un mensaje si no hay dirección disponible
                Toast.makeText(context, "Dirección no disponible", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return pedidosList.size();
    }

    class PedidoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumeroFactura, tvFecha, tvNombreCliente,
                tvProductos, tvTotal, tvDireccion;

        PedidoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumeroFactura = itemView.findViewById(R.id.tvNumeroFactura);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvNombreCliente = itemView.findViewById(R.id.tvNombreCliente);
            tvProductos = itemView.findViewById(R.id.tvProductos);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            tvDireccion = itemView.findViewById(R.id.tvDireccion);
        }
    }
}