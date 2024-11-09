package c.example.ouiy;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

public class AdminP extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_p);

        // Configuración del DrawerLayout y NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);


        // Configuración del ActionBarDrawerToggle
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Configuración de botones en el layout original
        Button botonMeStock = findViewById(R.id.botonStock);
        Button botonMeVenta = findViewById(R.id.botonVenta);
        Button botonMeHistorial = findViewById(R.id.botonHistorial);
        Button botonAñadirP = findViewById(R.id.añadirPyme);
        Button botonVerP = findViewById(R.id.verPyme);
        NavigationView navigationView = findViewById(R.id.nav_view);


        // Configuración de listeners de los botones
        botonMeStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminP.this, panStock.class));
            }
        });

        botonMeVenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminP.this, panVentas.class));
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
                startActivity(new Intent(AdminP.this, verPymes.class));
            }
        });

        // Configuración del listener para el Navigation Drawer



            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    if (item.getItemId() == R.id.nav_stock) {
                        startActivity(new Intent(AdminP.this, panStock.class));
                    } else if (item.getItemId() == R.id.venta) {
                        startActivity(new Intent(AdminP.this, panVentas.class));
                    } else if (item.getItemId() == R.id.nav_historial) {
                        startActivity(new Intent(AdminP.this, PymeP.class));
                    } else if (item.getItemId() == R.id.nav_add_pyme) {
                        startActivity(new Intent(AdminP.this, AddPyme.class));
                    } else if (item.getItemId() == R.id.nav_view_pyme) {
                        startActivity(new Intent(AdminP.this, verPymes.class));
                    } else {
                        throw new IllegalStateException("Unexpected value: " + item.getItemId());
                    }
                    drawerLayout.closeDrawers();
                    return true;
                }
            });


    }

    // Manejo del botón de menú del ActionBar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Cerrar el Navigation Drawer al presionar el botón de retroceso
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
