package mx.edu.uteq.dapps.proyectofinal192.ui.album;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mx.edu.uteq.dapps.proyectofinal192.R;


public class AlbumAdapter extends BaseAdapter {
    private Context contexto;
    private JSONArray datos;
    private LayoutInflater inflater;

    private ImageView ivAlbum;
    private TextView tvAlbum;
    private TextView tvInter;
    private TextView tvFecha;
    private Switch swLista;
    private ImageButton btnInfo;

    private SharedPreferences prefs;

    public AlbumAdapter(Context contexto, JSONArray datos) {
        this.contexto = contexto;
        this.datos = datos;
        inflater = LayoutInflater.from(contexto);
        prefs = contexto.getSharedPreferences("musicapp", Context.MODE_PRIVATE);
    }

    @Override
    public int getCount() {
        return datos.length();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(
                    R.layout.album_item,
                    null
            );

            ivAlbum = convertView.findViewById(R.id.iv_album);
            tvAlbum = convertView.findViewById(R.id.tv_album);
            tvInter = convertView.findViewById(R.id.tv_inter);
            tvFecha = convertView.findViewById(R.id.tv_fecha);
            swLista = convertView.findViewById(R.id.sw_lista);
            btnInfo = convertView.findViewById(R.id.btn_info);

            try {

                final String urlImagen = datos.getJSONObject(position).getString("img_album");
                final String albumId = datos.getJSONObject(position).getString("album_id");
                final String nombreAlbum = datos.getJSONObject(position).getString("nombre_album");
                final String nombreInter = datos.getJSONObject(position).getString("nombre_inter");
                final String fecha = datos.getJSONObject(position).getString("fecha_lanzamiento");

                Picasso.with(contexto).load(urlImagen).placeholder(R.drawable.placeholder).into(ivAlbum);
                tvAlbum.setText(nombreAlbum);
                tvInter.setText(nombreInter);
                tvFecha.setText(fecha);

                btnInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contexto.startActivity(
                                new Intent(
                                        contexto,
                                        AlbumDetalleActivity.class
                                )
                                        .putExtra("album_id", albumId)
                                        .putExtra("img_album", urlImagen)
                                        .putExtra("nombre_album", nombreAlbum)
                                        .putExtra("nombre_inter", nombreInter)
                                        .putExtra("fecha", fecha)
                        );
                    }
                });
            }

            catch(Exception e) {
                Toast.makeText(contexto, e.getMessage(), Toast.LENGTH_SHORT).show();
            }


        }
        return convertView;
    }
}
