package mx.edu.uteq.dapps.proyectofinal192.ui.album;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import mx.edu.uteq.dapps.proyectofinal192.R;

public class AlbumsFragment extends Fragment {

    /*Componentes del Layout*/
    private SwipeRefreshLayout srlAlbums;
    private ListView lvAlbums;

    private AlbumAdapter adaptador;
    private JSONArray datos;

    private RequestQueue conexionServ;
    private StringRequest peticionServ;

    public AlbumsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_albums, container, false);
        srlAlbums = rootView.findViewById(R.id.srl_albums);
        lvAlbums = rootView.findViewById(R.id.lv_albums);

        conexionServ = Volley.newRequestQueue(getActivity());

        datos = new JSONArray();

        adaptador = new AlbumAdapter(getActivity(), datos);
        lvAlbums.setAdapter(adaptador);

        srlAlbums.post(new Runnable() {
            @Override
            public void run() {
                srlAlbums.setRefreshing(true);
                cargaAlbums();
            }
        });

        srlAlbums.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                srlAlbums.setRefreshing(true);
                cargaAlbums();
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    private void cargaAlbums() {
        peticionServ = new StringRequest(
                Request.Method.GET,
                "https://wikiclod.mx/dapps/api-192/album/info",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject objRespuesta = new JSONObject(response);
                            if (objRespuesta.getInt("response_code") == 200) {
                                adaptador = new AlbumAdapter(
                                        getActivity(),
                                        objRespuesta.getJSONArray("albums")
                                );
                                lvAlbums.setAdapter(adaptador);
                                /*Actualizar el adaptador del ListView*/
                                adaptador.notifyDataSetChanged();
                            }
                        }

                        catch (Exception e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        srlAlbums.setRefreshing(false);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                        srlAlbums.setRefreshing(false);
                    }
                }
        );
        conexionServ.add(peticionServ);
    }
}