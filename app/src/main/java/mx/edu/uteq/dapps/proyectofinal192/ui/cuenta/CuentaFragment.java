package mx.edu.uteq.dapps.proyectofinal192.ui.cuenta;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import mx.edu.uteq.dapps.proyectofinal192.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CuentaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CuentaFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /*INICIALIZAMOS LOS ELEMENTOS DEL LAYOUT*/
    private ImageView ivUserpic;
    private TextInputEditText tietTel;
    private Button btnCambioImagen;
    private ProgressDialog pdCuenta;

    private RequestQueue conexionServ;
    private StringRequest peticionServ;

    private SharedPreferences prefs;

    public CuentaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CuentaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CuentaFragment newInstance(String param1, String param2) {
        CuentaFragment fragment = new CuentaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        prefs = getActivity().getSharedPreferences("musicapp", Context.MODE_PRIVATE);
        final String usuarioId = prefs.getString("usuario_id", null);

        // Guardamos la vista principal del fragmento de una variable
        final View rootView = inflater.inflate(R.layout.fragment_cuenta, container, false);

        /*Enlazamos los elementos del Layout*/
        btnCambioImagen = rootView.findViewById(R.id.btn_cambio_imagen);

        ivUserpic = rootView.findViewById(R.id.iv_userpic);
        tietTel = rootView.findViewById(R.id.tiet_tel);
        pdCuenta = new ProgressDialog(getActivity());

        conexionServ = Volley.newRequestQueue(getActivity());

        /*Agregar Progress Cargando*/
        pdCuenta.setTitle("Cargando información");
        pdCuenta.setMessage("Por favor espera...");
        pdCuenta.setIndeterminate(true);
        pdCuenta.setCancelable(false);
        pdCuenta.show();

        peticionServ = new StringRequest(
                Request.Method.POST,
                "https://wikiclod.mx/dapps/api-192/cuenta/datos/" + usuarioId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject objRespuesta = new JSONObject(response);

                            if (objRespuesta.getInt("response_code") == 200) {
                                /*Tomamos el objeto de los datos del usuario*/
                                JSONObject datosUsuario = objRespuesta.getJSONObject("datos_usuario");

                                /*Mostramos el telefono del usuario*/
                                tietTel.setText(datosUsuario.getString("tel"));

                                /*Si hay una imagen de usuario*/
                                if (datosUsuario.getString("userpic") != null) {
                                    Picasso
                                            .with(getActivity())
                                            .load(datosUsuario.getString("userpic"))
                                            .into(ivUserpic);
                                }
                            }
                        }

                        catch(Exception e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        /*Quitamos el Progress*/
                        pdCuenta.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("usuario_id", usuarioId);

                return params;
            }
        };
        conexionServ.add(peticionServ);

        /*Agregamos el evento click*/
        btnCambioImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Cargar la ventana modal*/
                CambiaImagenModal cim = new CambiaImagenModal();
                /*Show usa 2 parametros
                * 1.- La instancia del contexto
                * 2.- Tipo de visualizacion de la ventana modal*/
                cim.show(
                        getActivity().getSupportFragmentManager(),
                        "ModalBottomSheet"
                );
            }
        });

        /*TODO: ESTA SIEMPRE DEBE SER LA ÚLTIMA LÍNEA DE UN FRAGMENTO*/
        return rootView;
    }
}