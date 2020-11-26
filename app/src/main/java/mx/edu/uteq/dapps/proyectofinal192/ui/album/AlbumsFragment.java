package mx.edu.uteq.dapps.proyectofinal192.ui.album;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mx.edu.uteq.dapps.proyectofinal192.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AlbumsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlbumsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /*Componentes del Layout*/
    private SwipeRefreshLayout srlAlbums;
    private ListView lvAlbums;

    private RequestQueue conexionServ;
    private StringRequest peticionServ;

    private List<String> listaInters;
    private ArrayAdapter<String> adaptador;

    public AlbumsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AlbumsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AlbumsFragment newInstance(String param1, String param2) {
        AlbumsFragment fragment = new AlbumsFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_albums, container, false);
        listaInters = new ArrayList<>();
        adaptador = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                listaInters
        );

        /*Inicializamos los elementos*/
        srlAlbums = rootView.findViewById(R.id.srl_albums);

        lvAlbums = rootView.findViewById(R.id.lv_albums);
        lvAlbums.setAdapter(adaptador);

        conexionServ = Volley.newRequestQueue(getActivity());

        /*Iniciamos el fragmento en modo loading*/
        srlAlbums.post(new Runnable() {
            @Override
            public void run() {
                srlAlbums.setRefreshing(true);

                peticionServ = new StringRequest(
                        Request.Method.GET,
                        "https://wikiclod.mx/dapps/api-192/interprete/datos_interprete/",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                /*Tomar los datos json del objeto*/
                                try {
                                    JSONObject objRespuesta = new JSONObject(response);
                                    /*Si el servicio responde con 200*/
                                    if (objRespuesta.getInt("response_code") == 200) {
                                        JSONArray arrInters = objRespuesta.getJSONArray("inters");
                                        /*Recorrer el arreglo de objetos JSON para agregarlo a la lista*/
                                        for(int i = 0; i < arrInters.length(); i++) {
                                            /*Creamos un objeto json por cada elemento
                                            * del arreglo*/
                                            JSONObject inter = arrInters.getJSONObject(i);

                                            listaInters.add(
                                                    inter.getString("nombre_album") + " " +
                                                    inter.getString("nombre_inter")
                                            );

                                            /*Actualizamos el contenido del adaptador*/
                                            adaptador.notifyDataSetChanged();
                                        }
                                    }

                                    else {
                                        Toast.makeText(getActivity(), "SIN ALBUMS", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                catch(Exception e) {
                                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                                srlAlbums.setRefreshing(false);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                srlAlbums.setRefreshing(false);
                                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                );
                conexionServ.add(peticionServ);
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }
}