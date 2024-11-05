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

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private final List<Map<String, Object>> productList;
    private final panStock context; // Cambiar Context por panStock

    public ProductAdapter(List<Map<String, Object>> productList, panStock context) {
        this.productList = productList;
        this.context = context; // Asegúrate de que el contexto sea de tipo panStock
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Map<String, Object> product = productList.get(position);
        holder.productName.setText((String) product.get("name"));
        holder.productDescription.setText((String) product.get("description"));
        holder.productQuantity.setText("Cantidad: " + product.get("quantity").toString());

        // Manejar el clic del botón de editar
        holder.editButton.setOnClickListener(v -> context.showEditProductDialog(product, position));

        // Manejar el clic del botón de eliminar
        holder.deleteButton.setOnClickListener(v -> context.deleteProduct(position));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productDescription, productQuantity;
        Button editButton, deleteButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productDescription = itemView.findViewById(R.id.productDescription);
            productQuantity = itemView.findViewById(R.id.productQuantity);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
