package c.example.ouiy;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class verPymes extends AppCompatActivity {

    private RecyclerView recyclerViewPymes;
    private PymeAdapter pymeAdapter;
    private List<Pyme> pymeList;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private static final String TAG = "VerPymes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_pymes);

        recyclerViewPymes = findViewById(R.id.recyclerViewPymes);
        recyclerViewPymes.setLayoutManager(new LinearLayoutManager(this));

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        pymeList = new ArrayList<>();
        pymeAdapter = new PymeAdapter(pymeList);
        recyclerViewPymes.setAdapter(pymeAdapter);

        loadPymes();
    }

    private void loadPymes() {
        String adminId = auth.getCurrentUser().getUid();

        firestore.collection("Admins").document(adminId).collection("Pymes")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        pymeList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            String email = document.getString("email");
                            pymeList.add(new Pyme(name, email));
                        }
                        pymeAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(verPymes.this, "Error al cargar las PYMES", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error getting documents: ", task.getException());
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(verPymes.this, "Error al conectar con Firestore", Toast.LENGTH_SHORT).show());
    }
}

