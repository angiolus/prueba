package c.example.ouiy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;  // Instancia de Firestore
    private EditText loginCorreo, loginClave ;
    private Button botonLogin;
    private TextView redirectRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();  // Inicializamos Firestore

        loginCorreo = (EditText) findViewById(R.id.loginUsuario);
        loginClave = (EditText) findViewById(R.id.loginContraseña);
        botonLogin = (Button) findViewById(R.id.botonIniciar);
        redirectRegistro = (TextView) findViewById(R.id.redirectRegistro);

        botonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = loginCorreo.getText().toString();
                String clave = loginClave.getText().toString();

                if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    if (!clave.isEmpty()) {
                        auth.signInWithEmailAndPassword(email, clave)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        String userId = auth.getCurrentUser().getUid();
                                        // Obtener el documento del usuario desde Firestore
                                        DocumentReference userRef = firestore.collection("Users").document(userId);

                                        // Consultar los datos del usuario en Firestore
                                        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                if (documentSnapshot.exists()) {
                                                    // Obtener el rol del usuario
                                                    String rol = documentSnapshot.getString("role");

                                                    if (rol != null) {
                                                        // Redirigir según el rol
                                                        if (rol.equals("admin")) {
                                                            startActivity(new Intent(MainActivity.this, AdminP.class));  // Pantalla de admin
                                                        } else if (rol.equals("pyme")) {
                                                            startActivity(new Intent(MainActivity.this, PymeP.class));  // Pantalla de Pyme
                                                        } else {
                                                            Toast.makeText(MainActivity.this, "Rol desconocido", Toast.LENGTH_SHORT).show();
                                                        }
                                                    } else {
                                                        Toast.makeText(MainActivity.this, "No se pudo obtener el rol", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    Toast.makeText(MainActivity.this, "Usuario no encontrado en Firestore", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(MainActivity.this, "Error al obtener datos de Firestore", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this, "El login falló", Toast.LENGTH_SHORT).show();
                                    }
                                });

                    } else {
                        loginClave.setError("La clave no puede estar vacía");
                    }
                } else if (email.isEmpty()) {
                    loginCorreo.setError("El correo no puede estar vacío");
                } else {
                    loginCorreo.setError("Ingresa un correo válido");
                }
            }
        });

        redirectRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Registro.class));
            }
        });
    }
}
