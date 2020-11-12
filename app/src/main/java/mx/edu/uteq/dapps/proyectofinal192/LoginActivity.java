package mx.edu.uteq.dapps.proyectofinal192;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void irAregistro(View v) {
        startActivity(new Intent(
                LoginActivity.this,
                RegistroActivity.class
        ));
    }
}