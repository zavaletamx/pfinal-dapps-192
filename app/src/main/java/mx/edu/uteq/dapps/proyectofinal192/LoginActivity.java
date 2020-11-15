package mx.edu.uteq.dapps.proyectofinal192;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    /*
     * Componentes gráficos (xml)
     * */
    private LinearLayout llFormulario;
    private LinearLayout llCargando;

    private TextInputEditText tietTel;
    private TextInputEditText tietPin;

    private Button btnLogin;
    private Button btnRegistro;

    private AlertDialog.Builder alerta;

    /*Elementos de conexión remota*/
    private RequestQueue conexionServ;
    private StringRequest peticionServ;

    /*Variable de preferencias locales*/
    private SharedPreferences prefs;

    /*Editar las preferencias locales*/
    private SharedPreferences.Editor prefsEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /*Vinculamos componentes*/
        llFormulario = findViewById(R.id.ll_formulario);
        llCargando = findViewById(R.id.ll_cargando);

        tietTel = findViewById(R.id.tiet_tel);
        tietPin = findViewById(R.id.tiet_pin);

        btnLogin = findViewById(R.id.btn_login);
        btnRegistro = findViewById(R.id.btn_registro);

        alerta = new AlertDialog.Builder(LoginActivity.this);

        conexionServ = Volley.newRequestQueue(LoginActivity.this);

        /*Siya existe algun valor enlas preferencias locales lo enviamos al home*/
        prefs = getSharedPreferences("musicapp", MODE_PRIVATE);

        /*Si no tenemos el valor de las preferencias, debemos indicar cual será el valor defecto*/
        String usuarioId = prefs.getString("usuario_id", null);
        if(usuarioId != null) {
            startActivity(new Intent(
                    LoginActivity.this,
                    MainActivity.class
            ));
        }
    }

    /*
    Metodo de navegación a registro
     */
    public void irAregistro(View v) {
        startActivity(new Intent(
                LoginActivity.this,
                RegistroActivity.class
        ));
    }

    /*
    Método para la evaluación de login remoto
     */
    public void loginBD(View v) {

        /*TODO validar ambos campos*/

        /*Ocultamos y mostramos linear*/
        llFormulario.setVisibility(View.GONE);
        llCargando.setVisibility(View.VISIBLE);

        btnLogin.setEnabled(false);
        btnRegistro.setEnabled(false);

        /*Inicializamos la peticion
         * 1.- Tipo de envio
         * 2.- URl dle servicio
         * 3.- Metodo de respuesta
         * 4.- Metodo de error
         * 5.- (OPCIONAL) parametros de envio POST*/
        peticionServ = new StringRequest(
                //P1
                Request.Method.POST,

                //P2
                "http://cidtai.uteq.edu.mx/dapps/api-192/auth/login",

                //P3
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        /*A partir de la cadena de respuesta, intentamos generar un
                         * archivo JSON*/
                        try {
                            /*Creamos un objeto JSON*/
                            JSONObject respuesta = new JSONObject(response);

                            /*Revisamos si el codigo de respuesta es satisfactorio*/
                            if (respuesta.getInt("response_code") == 200) {

                                /*Generamos un objeto JSON de los datos
                                 * del usuario*/
                                JSONObject datosUsuario = respuesta.getJSONObject("datos_usuario");

                                /*Guardamos las preferencias de manera local
                                Las preferencias se guardan en un espacio de trabajo*/
                                /*
                                 * 1.- Nombre del espacio de trabajo
                                 * 2.- Modo de uso (provado/publico/compartido)*/
                                prefs = getSharedPreferences("musicapp", MODE_PRIVATE);

                                /*Editamos las preferencias dentro de mi espcaio*/
                                prefsEditor = prefs.edit();

                                /*Agregamos el id del usuario en nuestro espacio de variables*/
                                prefsEditor.putString("usuario_id", datosUsuario.getString("id"));
                                prefsEditor.putString("telefono", datosUsuario.getString("tel"));

                                /*Escribimos los cambios en el archivo de configuracion*/
                                prefsEditor.commit();

                                /*Datos correctos*/
                                alerta.setTitle("Bienvenido")
                                        .setMessage("Hola de nuevo")
                                        .setIcon(R.drawable.music)
                                        .setCancelable(false)
                                        .setNeutralButton("Continuar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                startActivity(new Intent(
                                                        LoginActivity.this,
                                                        MainActivity.class
                                                ));
                                            }
                                        })
                                        .show();
                            }

                            /*Si los datos no existen (usuario/contraseña)*/
                            if (respuesta.getInt("response_code") == 404) {
                                /*Usuario / contraseña incorrectos*/
                                alerta.setTitle("ERROR")
                                        .setMessage("Usuario / Contraseña incorrectos")
                                        .setCancelable(false)
                                        .setNeutralButton("Aceptar", null)
                                        .setIcon(R.drawable.music)
                                        .show();

                                /*Limpiar los campos*/
                                tietTel.setText("");
                                tietPin.setText("");
                            }

                            /*Si existe un error de validación*/
                            if (respuesta.getInt("response_code") == 400) {
                                /*Error de validación*/
                                alerta.setTitle("ERROR")
                                        .setMessage(respuesta.getString("errors"))
                                        .setCancelable(false)
                                        .setNeutralButton("Aceptar", null)
                                        .setIcon(R.drawable.music)
                                        .show();
                            }
                        }
                        catch (Exception e) {
                            alerta.setTitle("ERROR")
                                    .setMessage(e.getMessage())
                                    .setCancelable(false)
                                    .setNeutralButton("Aceptar", null)
                                    .setIcon(R.drawable.music)
                                    .show();
                        }

                        /*Invertimos los elementos visuales*/
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
                        alerta.setTitle("ERROR")
                                .setMessage(error.toString())
                                .setCancelable(false)
                                .setNeutralButton("Aceptar", null)
                                .setIcon(R.drawable.music)
                                .show();
                    }
                }
        )
                //P5
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("tel", tietTel.getText().toString());
                params.put("pin", Helper.MD5_Hash(tietPin.getText().toString()));
                return params;
            }
        };

        /*Agregamos a las peticiones del servidor*/
        conexionServ.add(peticionServ);

    }
}