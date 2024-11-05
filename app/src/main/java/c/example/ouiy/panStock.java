package c.example.ouiy;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class panStock extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private Spinner pymeSpinner;
    private EditText productNameEditText, productQuantityEditText, productDescriptionEditText;
    private Button addProductButton;
    private RecyclerView productRecyclerView;
    private ProductAdapter productAdapter;

    private List<Map<String, Object>> productList = new ArrayList<>();
    private List<String> pymeNames = new ArrayList<>();
    private List<String> pymeIds = new ArrayList<>();
    private String selectedPymeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pan_stock);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        pymeSpinner = findViewById(R.id.pympeSpinner);
        productNameEditText = findViewById(R.id.productNameEditText);
        productQuantityEditText = findViewById(R.id.productQuantityEditText);
        productDescriptionEditText = findViewById(R.id.productDescriptionEditText);
        addProductButton = findViewById(R.id.addProductButton);
        productRecyclerView = findViewById(R.id.productRecyclerView);
        productRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (auth.getCurrentUser() != null) {
            loadPymes();  // Cargar PYMEs solo si el usuario está autenticado
        } else {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
        }

        pymeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPymeId = pymeIds.get(position);
                loadProducts(); // Cargar productos de la PYME seleccionada
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        addProductButton.setOnClickListener(v -> addProductToPyme());
    }

    private void loadPymes() {
        String adminId = auth.getCurrentUser().getUid();
        Log.d("UID", "Usuario autenticado UID: " + adminId);

        CollectionReference pymesRef = firestore.collection("Admins").document(adminId).collection("Pymes");

        pymesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                pymeNames.clear();
                pymeIds.clear();

                for (QueryDocumentSnapshot document : task.getResult()) {
                    String pymeName = document.getString("name");
                    if (pymeName != null) {
                        pymeNames.add(pymeName);
                        pymeIds.add(document.getId());
                        Log.d("loadPymes", "PYME encontrada: " + pymeName);
                    } else {
                        Log.d("loadPymes", "Documento sin campo 'name': " + document.getId());
                    }
                }

                if (!pymeNames.isEmpty()) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(panStock.this, android.R.layout.simple_spinner_item, pymeNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    pymeSpinner.setAdapter(adapter);
                } else {
                    Toast.makeText(panStock.this, "No se encontraron PYMEs", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(panStock.this, "Error al cargar PYMEs", Toast.LENGTH_SHORT).show();
                Log.e("loadPymes", "Error al obtener PYMEs: ", task.getException());
            }
        });
    }

    private void loadProducts() {
        if (selectedPymeId == null) return;

        CollectionReference productsRef = firestore.collection("Admins").document(auth.getCurrentUser().getUid())
                .collection("Pymes").document(selectedPymeId).collection("Products");

        productsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                productList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Map<String, Object> product = document.getData();
                    product.put("id", document.getId()); // Guardar el ID del producto
                    productList.add(product);
                }

                // Actualiza el adaptador
                productAdapter = new ProductAdapter(productList, panStock.this);
                productRecyclerView.setAdapter(productAdapter);
            } else {
                Toast.makeText(panStock.this, "Error al cargar productos", Toast.LENGTH_SHORT).show();
                Log.e("loadProducts", "Error al obtener productos: ", task.getException());
            }
        });
    }

    private void addProductToPyme() {
        String productName = productNameEditText.getText().toString().trim();
        String productQuantity = productQuantityEditText.getText().toString().trim();
        String productDescription = productDescriptionEditText.getText().toString().trim();

        if (productName.isEmpty() || productQuantity.isEmpty() || productDescription.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> productData = new HashMap<>();
        productData.put("name", productName);
        productData.put("quantity", Integer.parseInt(productQuantity));
        productData.put("description", productDescription);

        DocumentReference pymeRef = firestore.collection("Admins").document(auth.getCurrentUser().getUid())
                .collection("Pymes").document(selectedPymeId);

        pymeRef.collection("Products").add(productData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(panStock.this, "Producto añadido a la PYME", Toast.LENGTH_SHORT).show();
                productNameEditText.setText("");
                productQuantityEditText.setText("");
                productDescriptionEditText.setText("");
                loadProducts(); // Cargar los productos nuevamente para reflejar el nuevo producto
            } else {
                Toast.makeText(panStock.this, "Error al añadir producto", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void showEditProductDialog(Map<String, Object> product, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_product, null);
        builder.setView(dialogView);

        EditText nameEditText = dialogView.findViewById(R.id.editProductNameEditText);
        EditText descriptionEditText = dialogView.findViewById(R.id.editProductDescriptionEditText);
        EditText quantityEditText = dialogView.findViewById(R.id.editProductQuantityEditText);
        Button updateButton = dialogView.findViewById(R.id.updateProductButton);

        nameEditText.setText((String) product.get("name"));
        descriptionEditText.setText((String) product.get("description"));
        quantityEditText.setText(String.valueOf(product.get("quantity")));
        String productId = product.get("id").toString();

        AlertDialog dialog = builder.create();

        updateButton.setOnClickListener(v -> {
            String updatedName = nameEditText.getText().toString().trim();
            String updatedDescription = descriptionEditText.getText().toString().trim();
            String updatedQuantityStr = quantityEditText.getText().toString().trim();

            if (updatedName.isEmpty() || updatedDescription.isEmpty() || updatedQuantityStr.isEmpty()) {
                Toast.makeText(panStock.this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            int updatedQuantity = Integer.parseInt(updatedQuantityStr);

            // Actualizar el producto en Firestore
            DocumentReference productRef = firestore.collection("Admins").document(auth.getCurrentUser().getUid())
                    .collection("Pymes").document(selectedPymeId)
                    .collection("Products").document(productId);

            Map<String, Object> updatedProductData = new HashMap<>();
            updatedProductData.put("name", updatedName);
            updatedProductData.put("description", updatedDescription);
            updatedProductData.put("quantity", updatedQuantity);

            productRef.update(updatedProductData).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    productList.set(position, updatedProductData); // Actualiza la lista local
                    productAdapter.notifyItemChanged(position); // Notifica al adaptador para actualizar la vista
                    dialog.dismiss();
                    Toast.makeText(panStock.this, "Producto actualizado", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(panStock.this, "Error al actualizar producto", Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    public void deleteProduct(int position) {
        String productId = productList.get(position).get("id").toString();

        DocumentReference productRef = firestore.collection("Admins").document(auth.getCurrentUser().getUid())
                .collection("Pymes").document(selectedPymeId)
                .collection("Products").document(productId);

        productRef.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                productList.remove(position); // Eliminar el producto de la lista local
                productAdapter.notifyItemRemoved(position); // Notificar al adaptador para actualizar la vista
                Toast.makeText(panStock.this, "Producto eliminado", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(panStock.this, "Error al eliminar producto", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
