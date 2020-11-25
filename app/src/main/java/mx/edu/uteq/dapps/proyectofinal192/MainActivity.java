package mx.edu.uteq.dapps.proyectofinal192;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Barra de navegación*/
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*Boton flotante*/
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        prefs = getSharedPreferences("musicapp", MODE_PRIVATE);
        String userPic = prefs.getString("userpic", null);

        /*Menu lateral (Drawer)*/
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        /*Tomamos el encabezado del menu y ponemos nuestra imagen*/
        ImageView ivAvatar = navigationView.
                getHeaderView(0).
                findViewById(R.id.image_view_avatar);

        Picasso
                .with(this)
                .load(userPic)
                .into(ivAvatar);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_inicio,
                R.id.nav_cuenta,
                R.id.nav_interpretes,
                R.id.nav_albums,
                R.id.nav_canciones,
                R.id.nav_listad
        )
        .setDrawerLayout(drawer)
        .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    /*Menui secundario (parte superior derecha)*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /*Metodo para el touch d elos elementos dle menu secundario*/

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        /*Tomamos el id del elemento del menu*/
        int id = item.getItemId();

        /*Dependiendo del id de cad amenu, agregamos su accion*/
        switch (id) {
            case R.id.salir :
                salir();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        salir();
    }

    private void salir() {
        AlertDialog.Builder alerta = new AlertDialog.Builder(MainActivity.this);
        alerta.setTitle("Cerrar sesión")
                .setMessage("¿Realmente deseas salir?")
                .setPositiveButton("Si, salir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        prefs = getSharedPreferences("musicapp", MODE_PRIVATE);
                        prefsEditor = prefs.edit();

                        /*Limpiamos nuestras variables d epreferencias del archivo local*/
                        //PARA BORRAR UNO POR UNO
                        // prefsEditor.remove("CLAVE")

                        //PARA BORRAR TODOS
                        prefsEditor.clear();
                        prefsEditor.commit();

                        startActivity(new Intent(
                                MainActivity.this,
                                LoginActivity.class
                        ));
                    }
                })
                .setNegativeButton("No, quedarse", null)
                .setIcon(R.drawable.music)
                .setCancelable(false)
                .show();
    }
}