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
    private static final String LOG = MainActivity.class.getSimpleName();

    //View variables
    private EditText mTextEditText;
    private TextView mResultsEditText;
    private Button mInternaButton;
    private Button mExternaButton;


    //Menu variables
    private static final int ID_MENU_1= 1;
    private static final int ID_MENU_2 = 2;
    private static final int ID_MENU_3 = 3;

    //Code for get the Callback in Runtime permisson check
    private static final int WRITE_PERMISSIOMN_CODE =100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Init view variables
        mTextEditText =(EditText)findViewById(R.id.texto_edit_text);
        mResultsEditText =(TextView)findViewById(R.id.resultados_text_view);
        mInternaButton =(Button)findViewById(R.id.interno_button);
        mExternaButton =(Button)findViewById(R.id.externo_button);

        mInternaButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                //Get editText value
                String textValue= mTextEditText.getText().toString();
                try
                {
                    //Create a file to save the text
                    OutputStreamWriter fout=
                            new OutputStreamWriter(
                                    openFileOutput("my_file.txt", Context.MODE_PRIVATE));

                    //Save the text into file
                    fout.write(textValue);
                    fout.close();

                    //Show messaage to user
                    Toast.makeText(getApplicationContext(), "Text saved successfully",Toast.LENGTH_SHORT).show();
                }
                catch (Exception ex)
                {
                    Log.e(LOG, "Error saving the text into Internal Storage");
                }
            }
        });

        mExternaButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {

                String externalMemoryStatus = Environment.getExternalStorageState();
                boolean isMemoryReady=false;

                if (externalMemoryStatus.equals(Environment.MEDIA_MOUNTED))
                {
                    isMemoryReady=true;
                    Toast.makeText(getApplicationContext(), "External Storage ready!", Toast.LENGTH_SHORT).show();
                }
                else if (externalMemoryStatus.equals(Environment.MEDIA_MOUNTED_READ_ONLY))
                {
                    Toast.makeText(getApplicationContext(), "External Storage in Read Only Mode", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "External Storage unavailable", Toast.LENGTH_SHORT).show();
                }

                if(isMemoryReady)
                {
                    //Start runtime permission check
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        //If App doesn't have the permission create the RUN TIME request
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                WRITE_PERMISSIOMN_CODE);
                    } else {
                        //If app already had the permission continue saving the data
                        saveInExternalStorage();
                    }
                }
            }
        });
    }

    /**
     * Save the text in External Storage
     */
    private void saveInExternalStorage(){
        //Get the text value
        String textValue= mTextEditText.getText().toString();

        try
        {
            //Look for external storage directory
            File external_path = Environment.getExternalStorageDirectory();
            //Create a file
            File f = new File(external_path.getAbsolutePath(), "my_file_external.txt");

            //Open the file
            OutputStreamWriter fout =
                    new OutputStreamWriter(
                            new FileOutputStream(f));

            //Write the file
            fout.write(textValue);
            fout.close();
            Toast.makeText(getApplicationContext(), "Text saved successfully", Toast.LENGTH_SHORT).show();
        }
        catch (Exception ex)
        {
            Log.e(LOG, "Error saving the text into External Storage");
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
                    //Open the file from internal storage
                    BufferedReader archivo =
                            new BufferedReader(
                                    new InputStreamReader(
                                            openFileInput("my_file.txt")));

                    //get the content into string
                    String savedText = archivo.readLine();
                    archivo.close();

                    //Show the text into results
                    mResultsEditText.setText(savedText);
                }
                catch (Exception ex)
                {
                    Log.e(LOG, "Error reading the text from Internal Storage");
                }
                return true;
            case ID_MENU_2:
                try
                {
                    //Look for external storage directory
                    File external_path = Environment.getExternalStorageDirectory();
                    //Create a file
                    File f = new File(external_path.getAbsolutePath(), "my_file_external.txt");

                    //Open the file from external storage
                    BufferedReader fin =
                            new BufferedReader(
                                    new InputStreamReader(
                                            new FileInputStream(f)));
                    //get the content into string
                    String texto = fin.readLine();
                    fin.close();

                    //Show the text into results
                    mResultsEditText.setText(texto);
                }
                catch (Exception ex)
                {
                    Log.e(LOG, "Error reading the text from External Storage");
                }
                return true;

            case ID_MENU_3:
                try
                {
                    //Get the file from RAW
                    InputStream ruta =
                            getResources().openRawResource(R.raw.prueba_csv);

                    //Open the file from raw
                    BufferedReader archivo =
                            new BufferedReader(new InputStreamReader(ruta));

                    //Clear the results
                    mResultsEditText.setText("");

                    String texf="";
                    while((texf=archivo.readLine())!=null)
                    {
                        //The file contains data in CSV format so, removing the commas.
                        String textWithoutComma=texf.replace(",","  ");
                        //Show the text into results
                        mResultsEditText.append(textWithoutComma+"\n");
                    }
                    archivo.close();
                }
                catch (Exception ex)
                {
                    Log.e(LOG, "Error reading the text from RAW resource");
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Get the RUN TIME permission check result
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            //If the requestCode is the same of the request action
            case WRITE_PERMISSIOMN_CODE: {
                //If the permission is granted the grantResults array is not empty
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //Once permission granted continue saving the data
                    saveInExternalStorage();
                } else {
                    Toast.makeText(getApplicationContext(),"Permission denied",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
