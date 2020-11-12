package mx.edu.uteq.dapps.proyectofinal192;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity {

    /*
    Elementos de la vista (Layout)
     */
    private EditText etTel;
    private EditText etPin;
    private ProgressBar pbRegistro;

    /*
    Elementos para consumir un servicio
    1.- atributo de tipo RequestQueue (conexion al servidor)
    2.- atributo de tipo StringRequest (petición al servidor)
     */
    private RequestQueue conexionServ;
    private StringRequest peticionServ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        /*
        Inicializamos los elementos
         */
        etTel = findViewById(R.id.et_tel);
        etPin = findViewById(R.id.et_pin);
        pbRegistro = findViewById(R.id.pb_registro);

        conexionServ = Volley.newRequestQueue(RegistroActivity.this);
    }

    public void registroUsuarioBD(View v) {
        /*Mostramos el progressbar*/
        pbRegistro.setVisibility(View.VISIBLE);

        /*
        Una petición de volley necesita los siguientes parámetros
        1.- Método de envío (POST/GET)
        2.- url del servicio (endpoint)
        3.- Metodo para las acciones cuando el servidor responde
        4.- Metodo para las acciones cuando el servidor no responde o tira un error
        5.- *OPCIONAL* indicar los parámetrosque enviaremos como variables al servidor
         */

        peticionServ = new StringRequest(
                // Param 1
                Request.Method.POST,

                // Param 2
                "http://cidtai.uteq.edu.mx/dapps/api-192/auth/registro",

                // Param 3
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(
                                RegistroActivity.this,
                                response,
                                Toast.LENGTH_SHORT
                        )
                        .show();
                        pbRegistro.setVisibility(View.GONE);
                    }
                },

                // Param 4
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(
                                RegistroActivity.this,
                                error.toString(),
                                Toast.LENGTH_SHORT
                        ).show();

                        pbRegistro.setVisibility(View.GONE);
                    }
                }

        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                /*Creamos un mapa para enviar los nombres y valores de las variables*/
                Map<String, String> params = new HashMap<>();

                params.put("tel", etTel.getText().toString());
                /*Mandamos la contraseña ENCRIPTADA*/
                params.put("pin", MD5_Hash(etPin.getText().toString()));

                return params;
            }
        };

        conexionServ.add(peticionServ);
    } // /oncreate

    public String MD5_Hash(String s) {
        MessageDigest m = null;

        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        m.update(s.getBytes(),0,s.length());
        String hash = new BigInteger(1, m.digest()).toString(16);
        return hash;
    }


} // /clase