package mx.edu.uteq.dapps.proyectofinal192;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity {

    /*
    Componentes de la vista (layout/XML)
     */
    private LinearLayout llFormulario;
    private LinearLayout llCargando;

    private TextInputEditText tietTel;
    private TextInputEditText tietCtel;

    private TextInputEditText tietPin;
    private TextInputEditText tietCpin;

    private Button btnRegistro;
    private Button btnLogin;

    private AlertDialog.Builder alerta;

    /*
    Componentes para conectarme remotamente a un servicio
     */
    private RequestQueue conexionServ;
    private StringRequest peticionServ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

         /*
        inicializamos todos los elementos
        (Vinculamos con su elemento del XML
         */
        llFormulario = findViewById(R.id.ll_formulario);
        llCargando = findViewById(R.id.ll_cargando);

        tietTel = findViewById(R.id.tiet_tel);
        tietCtel = findViewById(R.id.tiet_ctel);

        tietPin = findViewById(R.id.tiet_pin);
        tietPin = findViewById(R.id.tiet_cpin);

        btnRegistro = findViewById(R.id.btn_registro);
        btnLogin = findViewById(R.id.btn_login);

        /*
        Estas tres lineas hacen lo mismo
        alerta = new AlertDialog.Builder(this);
        alerta = new AlertDialog.Builder(getApplicationContext());
        */
        alerta = new AlertDialog.Builder(RegistroActivity.this);

        conexionServ = Volley.newRequestQueue(RegistroActivity.this);

    }

    /*Metodo para navegar a login*/
    public void irAlogin(View v) {
        startActivity(new Intent(
                RegistroActivity.this,
                LoginActivity.class
        ));
    }

    /*Metodo para registrarnos de manera remota*/
    public void registroBD(View v) {

        /*TODO VALIDA QUE EL PIN Y SU CONFIRMACION Y EL TEL Y SU CONFIRMACION SEAN IGUALES*/

        /*Ocultamos los datos del formulario*/
        llFormulario.setVisibility(View.GONE);

        /*Mostramos el loader*/
        llCargando.setVisibility(View.VISIBLE);

        /*invalidamos los botones*/
        btnRegistro.setEnabled(false);
        btnLogin.setEnabled(false);

        /*
         * PETICION DE VOLLEY:
         * 1.- Método de envío
         * 2.- URL del servicio
         * 3.- Metodo de acciones cuando el server responde (lo que sea)
         * 4.- Metodo cuando se tira un error del server
         * 5.- (OPCIONAL) parámetros si se uso POST como método de envío
         *
         */
        peticionServ = new StringRequest(
                //P1
                Request.Method.POST,

                //P2
                "http://cidtai.uteq.edu.mx/dapps/api-192/auth/registro",

                //P3
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        /*Si el servicio responde OK*/
                        if (response.equals("OK")) {
                            alerta.setTitle("BIENVENIDO")
                                    .setMessage("Registro completado")
                                    .setNeutralButton("Continuar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            startActivity(new Intent(
                                                    RegistroActivity.this,
                                                    LoginActivity.class
                                            ));
                                        }
                                    })
                                    .setCancelable(false)
                                    .setIcon(R.drawable.music)
                                    .show();
                        }

                        /*Si la respuesta es otra cosa*/
                        else {
                            alerta.setTitle("ERROR")
                                    .setMessage(response)
                                    .setNeutralButton("Aceptar", null)
                                    .setCancelable(false)
                                    .setIcon(R.drawable.music)
                                    .show();
                        }

                        llCargando.setVisibility(View.GONE);
                        llFormulario.setVisibility(View.VISIBLE);

                        btnLogin.setEnabled(true);
                        btnRegistro.setEnabled(true);


                    }
                },

                //P4
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        /*Mostramos cualquier error que existe*/
                        alerta.setTitle("ERROR INESPERADO")
                                .setMessage(error.toString())
                                .setNeutralButton("Aceptar", null)
                                .setCancelable(false)
                                .setIcon(R.drawable.music)
                                .show();

                        llCargando.setVisibility(View.GONE);
                        llFormulario.setVisibility(View.VISIBLE);

                        btnLogin.setEnabled(true);
                        btnRegistro.setEnabled(true);
                    }
                }
        )
                //P5
        {
            /*Enviamos los parámetros a PHP con los nombres y valores que
             * el servicio necesite*/
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                /*Usando el metodo put vamos a indicar las variables del servicio*/
                params.put("tel", tietTel.getText().toString());
                params.put("pin", Helper.MD5_Hash(tietPin.getText().toString()));

                return params;
            }
        };

        /*Ejecutamos la peticion desde el servidor*/
        conexionServ.add(peticionServ);
    }

} // /clase
