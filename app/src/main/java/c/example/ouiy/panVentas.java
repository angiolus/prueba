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
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class panVentas extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private Spinner pymeSpinner;
    private EditText productQuantityEditText, saleDescriptionEditText, saleAmountEditText;
    private Button addSaleButton;
    private RecyclerView saleRecyclerView;
    private SaleAdapter saleAdapter;

    private List<Map<String, Object>> saleList = new ArrayList<>();
    private List<String> pymeNames = new ArrayList<>();
    private List<String> pymeIds = new ArrayList<>();
    private String selectedPymeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pan_ventas);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        pymeSpinner = findViewById(R.id.pymeSpinner);
        productQuantityEditText = findViewById(R.id.saleQuantityEditText);
        saleDescriptionEditText = findViewById(R.id.saleDescriptionEditText);
        saleAmountEditText = findViewById(R.id.saleAmountEditText);
        addSaleButton = findViewById(R.id.addSaleButton);
        saleRecyclerView = findViewById(R.id.saleRecyclerView);
        saleRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (auth.getCurrentUser() != null) {
            loadPymes();
        } else {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
        }

        pymeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPymeId = pymeIds.get(position);
                loadSales();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        addSaleButton.setOnClickListener(v -> addSaleToPyme());
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
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(panVentas.this, android.R.layout.simple_spinner_item, pymeNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    pymeSpinner.setAdapter(adapter);
                } else {
                    Toast.makeText(panVentas.this, "No se encontraron PYMEs", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(panVentas.this, "Error al cargar PYMEs", Toast.LENGTH_SHORT).show();
                Log.e("loadPymes", "Error al obtener PYMEs: ", task.getException());
            }
        });
    }

    private void loadSales() {
        if (selectedPymeId == null) return;

        CollectionReference salesRef = firestore.collection("Admins").document(auth.getCurrentUser().getUid())
                .collection("Pymes").document(selectedPymeId).collection("Sales");

        salesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                saleList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Map<String, Object> sale = document.getData();
                    sale.put("id", document.getId());
                    saleList.add(sale);
                }

                // Actualiza el adaptador
                saleAdapter = new SaleAdapter(saleList, panVentas.this);
                saleRecyclerView.setAdapter(saleAdapter);
            } else {
                Toast.makeText(panVentas.this, "Error al cargar ventas", Toast.LENGTH_SHORT).show();
                Log.e("loadSales", "Error al obtener ventas: ", task.getException());
            }
        });
    }

    private void addSaleToPyme() {
        String saleQuantity = productQuantityEditText.getText().toString().trim();
        String saleDescription = saleDescriptionEditText.getText().toString().trim();
        String saleAmount = saleAmountEditText.getText().toString().trim();

        if (saleQuantity.isEmpty() || saleDescription.isEmpty() || saleAmount.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> saleData = new HashMap<>();
        saleData.put("quantity", Integer.parseInt(saleQuantity));
        saleData.put("description", saleDescription);
        saleData.put("amount", Double.parseDouble(saleAmount));

        DocumentReference pymeRef = firestore.collection("Admins").document(auth.getCurrentUser().getUid())
                .collection("Pymes").document(selectedPymeId);

        pymeRef.collection("Sales").add(saleData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(panVentas.this, "Venta añadida a la PYME", Toast.LENGTH_SHORT).show();
                productQuantityEditText.setText("");
                saleDescriptionEditText.setText("");
                saleAmountEditText.setText("");
                loadSales(); // Cargar las ventas nuevamente para reflejar la nueva venta
            } else {
                Toast.makeText(panVentas.this, "Error al añadir venta", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deleteSale(int position) {
        String saleId = saleList.get(position).get("id").toString();

        DocumentReference saleRef = firestore.collection("Admins").document(auth.getCurrentUser().getUid())
                .collection("Pymes").document(selectedPymeId)
                .collection("Sales").document(saleId);

        saleRef.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                saleList.remove(position); // Eliminar la venta de la lista local
                saleAdapter.notifyItemRemoved(position); // Notificar al adaptador para actualizar la vista
                Toast.makeText(panVentas.this, "Venta eliminada", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(panVentas.this, "Error al eliminar venta", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
