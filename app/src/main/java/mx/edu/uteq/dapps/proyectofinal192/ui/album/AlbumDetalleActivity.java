package mx.edu.uteq.dapps.proyectofinal192.ui.album;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONObject;

import mx.edu.uteq.dapps.proyectofinal192.R;

public class AlbumDetalleActivity extends AppCompatActivity {

    private ImageView ivAlbum;
    private TextView tvAlbum;
    private TextView tvInter;
    private TextView tvFecha;

    private TextView tvListaCanciones;
    private ProgressDialog pdCanciones;

    private RequestQueue conexionServ;
    private StringRequest peticionServ;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detalle);

        ActionBar ab = getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);

        String albumId = getIntent().getStringExtra("album_id");
        String imgAlbum = getIntent().getStringExtra("img_album");
        String nombreAlbum = getIntent().getStringExtra("nombre_album");
        String nombreInter = getIntent().getStringExtra("nombre_inter");
        String fecha = getIntent().getStringExtra("fecha");

        ivAlbum = findViewById(R.id.iv_album);
        tvAlbum = findViewById(R.id.tv_album);
        tvInter = findViewById(R.id.tv_inter);
        tvFecha = findViewById(R.id.tv_fecha);

        tvListaCanciones = findViewById(R.id.tv_lista_canciones);
        pdCanciones = new ProgressDialog(this);

        conexionServ = Volley.newRequestQueue(this);

        pdCanciones.setTitle("Cargando info");
        pdCanciones.setMessage("Por favor espera...");
        pdCanciones.setIndeterminate(true);
        pdCanciones.setCancelable(false);
        pdCanciones.show();

        peticionServ = new StringRequest(
                Request.Method.GET,
                "https://wikiclod.mx/dapps/api-192/album/canciones/"+albumId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject objRespuesta = new JSONObject(response);

                            if (objRespuesta.getInt("response_code") == 200) {

                                Picasso.with(AlbumDetalleActivity.this)
                                        .load(imgAlbum)
                                        .placeholder(R.drawable.placeholder)
                                        .into(ivAlbum);

                                tvAlbum.setText(nombreAlbum);
                                tvFecha.setText(fecha);
                                tvInter.setText(nombreInter);

                                JSONArray albums = objRespuesta.getJSONArray("canciones");
                                String contenidoCanciones = "<ul>";
                                for(int i = 0; i < albums.length(); i++) {
                                    contenidoCanciones += "<li>"+albums.getJSONObject(i).getString("titulo")+"</li>";
                                }
                                contenidoCanciones += "</ul>";
                                tvListaCanciones.setText(Html.fromHtml(contenidoCanciones));
                            }
                        }
                        catch(Exception e) {
                            Toast.makeText(AlbumDetalleActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        pdCanciones.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AlbumDetalleActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                        pdCanciones.dismiss();
                    }
                }
        );
        conexionServ.add(peticionServ);
    }
}