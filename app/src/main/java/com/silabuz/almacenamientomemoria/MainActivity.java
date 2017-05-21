package com.silabuz.almacenamientomemoria;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {


    //Variables en pantalla
    private EditText mTextoEditText;
    private TextView mResultadosEditText;
    private Button mInternaButton;
    private Button mExternaButton;


    //Variables del menu
    private static final int ID_MENU_1= 1;
    private static final int ID_MENU_2 = 2;
    private static final int ID_MENU_3 = 3;

    private static final int PERMISOS_ESCRITURA=100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Declaramos las variables
        mTextoEditText =(EditText)findViewById(R.id.texto_edit_text);
        mResultadosEditText =(TextView)findViewById(R.id.resultados_text_view);
        mInternaButton =(Button)findViewById(R.id.interno_button);
        mExternaButton =(Button)findViewById(R.id.externo_button);

        mInternaButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                //obtenemos el mTextoEditText
                String mostrar= mTextoEditText.getText().toString();
                try
                {
                    //Definimos la variable fout, como un archivo que sólo se puede acceder desde la app
                    OutputStreamWriter fout=
                            new OutputStreamWriter(
                                    openFileOutput("prueba_curso_android.txt", Context.MODE_PRIVATE));
                    //**** Mostrar como APPEND

                    //Guardamos lo que hemos incluido en el editText
                    fout.write(mostrar);
                    fout.close();
                    //Las notificaciones serán explicadas más adelante
                    Toast.makeText(getApplicationContext(), "Texto guardado",Toast.LENGTH_SHORT).show();
                }
                catch (Exception ex)
                {
                    //Si no logró guardar en memoria, mostrar un mensaje de error en el Log.
                    Log.e("Ficheros", "Error al escribir fichero a memoria mInternaButton");
                }
            }
        });

        mExternaButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {

                String estado = Environment.getExternalStorageState();
                boolean lista=false;

                if (estado.equals(Environment.MEDIA_MOUNTED))
                {
                    lista=true;
                    Toast.makeText(getApplicationContext(), "Memoria externa lista!", Toast.LENGTH_SHORT).show();
                }
                else if (estado.equals(Environment.MEDIA_MOUNTED_READ_ONLY))
                {
                    Toast.makeText(getApplicationContext(), "Memoria externa sin permisos de escritura", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Memoria externa no disponible", Toast.LENGTH_SHORT).show();
                }

                //Comprueba si la memora SD está lista con permisos de escritura.
                if(lista)
                {
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        //Solicitamos permiso de escritura en memoria externa al usuario
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                PERMISOS_ESCRITURA);
                    } else {
                        //Si ya tiene el permiso guardamos en memoria
                        guardarEnMemoriaExterna();
                    }
                }
            }
        });
    }

    /**
     * Guardamos en memoria externa despues de solicitar permiso al usuario
     */
    private void guardarEnMemoriaExterna(){
        //Guardamos en mostrar lo que escribimos
        String mostrar= mTextoEditText.getText().toString();

        try
        {
            //Busca la ruta de la SD
            File ruta_sd = Environment.getExternalStorageDirectory();
            //Obtiene la dirección raiz de la SD
            File f = new File(ruta_sd.getAbsolutePath(), "prueba_curso_sd.txt");

            //Define que el archivo que será modificado se encuentra en la variable f
            OutputStreamWriter fout =
                    new OutputStreamWriter(
                            new FileOutputStream(f));

            //Guardamos lo que hemos escrito en el archivo.
            fout.write(mostrar);
            fout.close();
            Toast.makeText(getApplicationContext(), "Texto guardado", Toast.LENGTH_SHORT).show();
        }
        catch (Exception ex)
        {
            //Si no logró guardar en memoria SD, mostrar un mensaje de error en el Log.
            Log.e("Ficheros", "Error al escribir fichero a tarjeta SD");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, ID_MENU_1, Menu.NONE, "Interna")
                .setIcon(android.R.drawable.ic_menu_add);
        menu.add(Menu.NONE, ID_MENU_2, Menu.NONE, "Externa")
                .setIcon(android.R.drawable.stat_notify_sdcard_prepare);
        menu.add(Menu.NONE, ID_MENU_3, Menu.NONE, "Recursos")
                .setIcon(android.R.drawable.ic_dialog_info);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case ID_MENU_1:
                try
                {
                    //Abriremos el contenido guardado en la memoria Interna
                    BufferedReader archivo =
                            new BufferedReader(
                                    new InputStreamReader(
                                            openFileInput("prueba_curso_android.txt")));

                    //Guardamos el contenido del archivo en mTextoEditText.
                    String texto = archivo.readLine();
                    archivo.close();

                    //Mostramos el contenido en el TextView de la parte inferior llamados mResultadosEditText
                    mResultadosEditText.setText(texto);
                }
                catch (Exception ex)
                {
                    Log.e("Ficheros", "Error al leer desde memoria mInternaButton");
                }
                return true;
            case ID_MENU_2:
                try
                {
                    //Obtiene la ruta de la tarjeta SD
                    File ruta_sd = Environment.getExternalStorageDirectory();
                    //En la variable f llamamos al archivo creado
                    File f = new File(ruta_sd.getAbsolutePath(), "prueba_curso_sd.txt");

                    BufferedReader fin =
                            new BufferedReader(
                                    new InputStreamReader(
                                            new FileInputStream(f)));
                    //Guardamos el contenido en la variable Texto
                    String texto = fin.readLine();
                    fin.close();

                    //Mostramos el contenido en el TextView de la parte inferior llamados mResultadosEditText
                    mResultadosEditText.setText(texto);
                }
                catch (Exception ex)
                {
                    Log.e("Ficheros", "Error al leer desde tarjeta SD");
                }
                return true;

            case ID_MENU_3:
                try
                {
                    //Obtenemos la ruta desde Res/raw
                    InputStream ruta =
                            getResources().openRawResource(R.raw.prueba_csv);

                    //Lo almacenamos en el buffer de lectura
                    BufferedReader archivo =
                            new BufferedReader(new InputStreamReader(ruta));

                    //Vaciamos mResultadosEditText
                    mResultadosEditText.setText("");

                    //Abrimos el archivo
                    String texto="";
                    while((texto=archivo.readLine())!=null)
                    {
                        //Hacemos desaparecer las comas.
                        String texto_sin_comas=texto.replace(",","  ");
                        //Mostramos los mResultadosEditText
                        mResultadosEditText.append(texto_sin_comas+"\n");
                    }
                    archivo.close();
                }
                catch (Exception ex)
                {
                    Log.e("Ficheros", "Error al leer fichero desde recurso raw");
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISOS_ESCRITURA: {
                //Si el permiso no fue concedido el array de grantResults estara vacio
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //Una vez que el permiso fue concedido guaramos en memoria
                    guardarEnMemoriaExterna();
                } else {
                    Toast.makeText(getApplicationContext(),"Permiso denagado",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
