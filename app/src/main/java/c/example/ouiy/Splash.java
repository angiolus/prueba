package c.example.ouiy;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(hayConexion(Splash.this)){
                    startActivity(new Intent(Splash.this, Registro.class));
                    finish();
                }else{
                    //Toast.makeText(Splash.this, "No hay conexión", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder miAlert = new AlertDialog.Builder(Splash.this);
                    miAlert.setTitle("No hay acceso a internet");
                    miAlert.setMessage("Por favor, activa tus datos o la wifi para usar la app!");
                    miAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
                    AlertDialog alertPro = miAlert.show();
                    alertPro.setCancelable(false);
                    alertPro.setCanceledOnTouchOutside(false);



                }

            }
        },2000);


    }

    public static boolean hayConexion(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if(netinfo != null && netinfo.isConnectedOrConnecting()){
            NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if((wifi != null && wifi.isConnectedOrConnecting()) ||
                    (mobile != null && mobile.isConnectedOrConnecting())){
                return true;
            }else{
                //Hay conexion pero no hay trafico
                return false;
            }









        }else{
            return false;
        }


    }




}