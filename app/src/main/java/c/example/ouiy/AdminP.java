package c.example.ouiy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AdminP extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_p);

        Button botonMeStock = (Button) findViewById(R.id.botonStock);
        Button botonMeVenta = (Button) findViewById(R.id.botonVenta);
        Button botonMeHistorial = (Button) findViewById(R.id.botonHistorial);
        Button botonAñadirP = (Button) findViewById(R.id.añadirPyme);
        Button botonVerP = (Button) findViewById(R.id.verPyme);



        botonMeStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminP.this, panStock.class));
            }
        });

        botonMeVenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminP.this, PymeP.class));
            }
        });


        botonMeHistorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminP.this, PymeP.class));
            }
        });

        botonAñadirP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminP.this, AddPyme.class));
            }
        });

        botonVerP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminP.this, verPymes.class)); // Nueva actividad para mostrar la lista de pymes
            }
        });

    }
}