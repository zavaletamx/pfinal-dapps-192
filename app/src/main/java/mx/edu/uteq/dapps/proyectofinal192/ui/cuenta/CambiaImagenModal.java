package mx.edu.uteq.dapps.proyectofinal192.ui.cuenta;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import mx.edu.uteq.dapps.proyectofinal192.R;

/*Las ventanas modales de android, por defecto heredan de la clase BottomSheetDialogFragment*/
public class CambiaImagenModal extends BottomSheetDialogFragment {

    private SharedPreferences prefs;

    /*Atributos de la UI*/
    private ImageView ivUserpic;
    private TextInputEditText tietUripic;
    private ProgressBar pbCim;
    private LinearLayout llCargando;
    private LinearLayout llContenido;
    private Button btnGuardarUserpic;

    private RequestQueue conexionServ;
    private StringRequest peticionServ;

    /*Las ventanas modales utilizan un metodo llamado
    * onCreateView para inicializar sus componentes*/

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        /*Sacamos el id del usuario del archivo de sesion d epreferencias*/
        prefs = getActivity().getSharedPreferences("musicapp", Context.MODE_PRIVATE);
        final String usuarioId = prefs.getString("usuario_id", null);

        /*Invocamo y retornamos una vista (Layout)*/
        View rootView = inflater.inflate(
                R.layout.cambia_imagen_modal,
                container,
                false
        );

        /*Vinculamos los componentes del layout con el controlador*/
        tietUripic = rootView.findViewById(R.id.tiet_uripic);
        pbCim = rootView.findViewById(R.id.pb_cim);
        llCargando = rootView.findViewById(R.id.ll_cargando);
        llContenido = rootView.findViewById(R.id.ll_contenido);
        btnGuardarUserpic = rootView.findViewById(R.id.btn_guardaruserpic);

        conexionServ = Volley.newRequestQueue(getActivity());

        /*Click boton guardar imagen*/
        btnGuardarUserpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Mostramos el loader y ocultamos contenido*/
                llContenido.setVisibility(View.GONE);
                llCargando.setVisibility(View.VISIBLE);

                peticionServ = new StringRequest(
                        Request.Method.POST,
                        "https://wikiclod.mx/dapps/api-192/cuenta/editar/" + usuarioId,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                /*Generar un objeto json con la respuesta*/
                                try {
                                    JSONObject objRespuesta = new JSONObject(response);

                                    /*Si la respuesta es correcta*/
                                    if (objRespuesta.getInt("response_code") == 200) {

                                        Toast.makeText(
                                                getActivity(),
                                                "Imagen actualizada",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        /*Actualizamos la imagen de perfíl en el Drawer*/
                                        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);

                                        Toast.makeText(getActivity(), tietUripic.getText().toString(), Toast.LENGTH_SHORT).show();

                                        ivUserpic = navigationView.
                                                getHeaderView(0).
                                                findViewById(R.id.image_view_avatar);

                                        Picasso
                                                .with(getActivity())
                                                .load(tietUripic.getText().toString())
                                                .into(ivUserpic);

                                        /*Recargar las actividades dle proyecto con sus fragmentos*/
                                        getActivity().finish();
                                        getActivity().startActivity(getActivity().getIntent());

                                    }

                                    else if (objRespuesta.getInt("response_code") == 400) {
                                        Toast.makeText(getActivity(),
                                                objRespuesta.getString("errors"),
                                                Toast.LENGTH_SHORT
                                        ).show();
                                    }
                                }
                                catch(Exception e) {
                                    Toast.makeText(
                                            getActivity(),
                                            e.getMessage(),
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }

                                llCargando.setVisibility(View.GONE);
                                llContenido.setVisibility(View.VISIBLE);

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        }
                ){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("usuario_id", usuarioId);
                        params.put("userpic", tietUripic.getText().toString());

                        return params;
                    }
                };
                conexionServ.add(peticionServ);
            }
        });

        return rootView;
    }
}
