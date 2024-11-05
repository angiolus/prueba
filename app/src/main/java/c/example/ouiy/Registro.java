package c.example.ouiy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Registro extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText registroCorreo, registroClave;
    private Spinner spinnerRole;  // Añadimos el Spinner para seleccionar el rol
    private Button botonRegistro;
    private TextView loginRedirect;
    private FirebaseFirestore firestore;  // Instancia de Firestore

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();  // Inicializamos Firestore
        registroCorreo = (EditText) findViewById(R.id.signEmail);
        registroClave = (EditText) findViewById(R.id.signContraseña);
        spinnerRole = (Spinner) findViewById(R.id.spinnerRole);  // Inicializamos el Spinner
        botonRegistro = (Button) findViewById(R.id.botonRegistrar);
        loginRedirect = (TextView) findViewById(R.id.redirectLogin);

        botonRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usuario = registroCorreo.getText().toString().trim();
                String clave = registroClave.getText().toString().trim();
                String rol = spinnerRole.getSelectedItem().toString();  // Obtenemos el rol seleccionado

                if (usuario.isEmpty()) {
                    registroCorreo.setError("El correo no puede estar vacío");
                }
                if (clave.isEmpty()) {
                    registroClave.setError("La clave no puede estar vacía");
                } else {
                    auth.createUserWithEmailAndPassword(usuario, clave).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Obtener UID del usuario registrado
                                String userId = auth.getCurrentUser().getUid();

                                // Crear referencia a Firestore y preparar los datos del usuario
                                DocumentReference userRef = firestore.collection("Users").document(userId);

                                Map<String, Object> userData = new HashMap<>();
                                userData.put("email", usuario);
                                userData.put("role", rol);  // Guardamos el rol seleccionado

                                // Guardar los datos en Firestore
                                userRef.set(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // Mensaje de éxito
                                            Toast.makeText(Registro.this, "Registro completado correctamente", Toast.LENGTH_SHORT).show();
                                            // Redirigir a la pantalla principal
                                            startActivity(new Intent(Registro.this, MainActivity.class));
                                        } else {
                                            Toast.makeText(Registro.this, "Error al guardar en Firestore: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                // Mensaje de error si el registro falla
                                Toast.makeText(Registro.this, "El registro falló: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });

        loginRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Registro.this, MainActivity.class));
            }
        });
    }
}
