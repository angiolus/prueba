package c.example.ouiy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class SaleAdapter extends RecyclerView.Adapter<SaleAdapter.SaleViewHolder> {

    private final List<Map<String, Object>> saleList;
    private final panVentas context;

    public SaleAdapter(List<Map<String, Object>> saleList, panVentas context) {
        this.saleList = saleList;
        this.context = context;
    }

    @NonNull
    @Override
    public SaleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_venta, parent, false);
        return new SaleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SaleViewHolder holder, int position) {
        Map<String, Object> sale = saleList.get(position);
        holder.saleQuantity.setText("Cantidad: " + sale.get("quantity").toString());
        holder.saleDescription.setText("Descripción: " + sale.get("description").toString());
        holder.saleAmount.setText("Monto: $" + sale.get("amount").toString());

        holder.deleteButton.setOnClickListener(v -> context.deleteSale(position));
    }

    @Override
    public int getItemCount() {
        return saleList.size();
    }

    static class SaleViewHolder extends RecyclerView.ViewHolder {
        TextView saleQuantity, saleDescription, saleAmount;
        Button deleteButton;

        SaleViewHolder(@NonNull View itemView) {
            super(itemView);
            saleQuantity = itemView.findViewById(R.id.saleQuantityTextView);
            saleDescription = itemView.findViewById(R.id.saleDescriptionTextView);
            saleAmount = itemView.findViewById(R.id.saleAmountTextView);
            deleteButton = itemView.findViewById(R.id.deleteSaleButton);
        }
    }
}
