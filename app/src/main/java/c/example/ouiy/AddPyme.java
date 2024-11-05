package c.example.ouiy;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddPyme extends AppCompatActivity {

    private EditText editTextEmailPyme, editTextNamePyme;
    private Button buttonAddPyme;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    private static final String TAG = "AddPyme";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pyme);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Asegúrate de que los IDs aquí coincidan con los del XML (activity_add_pyme.xml)
        editTextEmailPyme = findViewById(R.id.emailPymeEditText); // Este es el campo de email
        editTextNamePyme = findViewById(R.id.namePymeEditText);   // Este es el campo de nombre
        buttonAddPyme = findViewById(R.id.addPymeButton);

        buttonAddPyme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPyme();
            }
        });
    }

    private void addPyme() {
        String namePyme = editTextNamePyme.getText().toString().trim();
        String pymeEmail = editTextEmailPyme.getText().toString().trim();

        if (namePyme.isEmpty()) {
            Toast.makeText(AddPyme.this, "Por favor ingresa el nombre de la Pyme", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pymeEmail.isEmpty()) {
            Toast.makeText(AddPyme.this, "Por favor ingresa un correo de Pyme", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificar si el correo está registrado como Pyme
        firestore.collection("Users")
                .whereEqualTo("email", pymeEmail)
                .whereEqualTo("role", "pyme")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // El correo está registrado como pyme
                        String adminId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        Map<String, Object> pymeData = new HashMap<>();
                        pymeData.put("name", namePyme);
                        pymeData.put("email", pymeEmail);

                        firestore.collection("Admins")
                                .document(adminId)
                                .collection("Pymes")
                                .add(pymeData)
                                .addOnSuccessListener(documentReference -> {
                                    Toast.makeText(AddPyme.this, "Pyme añadida con éxito", Toast.LENGTH_SHORT).show();
                                    editTextNamePyme.setText("");
                                    editTextEmailPyme.setText("");
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Error al añadir la Pyme", e);
                                    Toast.makeText(AddPyme.this, "Error al añadir la Pyme", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        // No se encontró un usuario con el rol de pyme
                        Toast.makeText(AddPyme.this, "El correo ingresado no está registrado como PYME", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al buscar el correo en Firestore", e);
                    Toast.makeText(AddPyme.this, "Error al buscar el correo en Firestore", Toast.LENGTH_SHORT).show();
                });
    }
}
