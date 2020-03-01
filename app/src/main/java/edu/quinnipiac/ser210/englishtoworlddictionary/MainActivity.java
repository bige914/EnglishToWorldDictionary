package edu.quinnipiac.ser210.englishtoworlddictionary;
/**
 * MainActivity class, accepts inputs from TextEdit and Spinner
 * to perform a function related to translation to other languages from English
 *
 * @authors Ellsworth Evarts IV, Ania Lightly
 * @date 2/29/2020
 */
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    //View view;
    //View root = view.getRootView();
    TargetLangHandler tLangHandler = new TargetLangHandler();
    ShareActionProvider provider;
    boolean userSelect = false;
    boolean wordToTran = false;
    private String[] itemStr = new String[2];


    private String url1 = "https://systran-systran-platform-for-language-processing-v1";
    private String url2 = ".p.rapidapi.com/translation/text/translate?";
    private String source = "source=en&";
    private String baseUrl = url1 + url2 + source;

    private String inpt = "";

    EditText word;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //view=(View)findViewById(R.id.myview);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Spinner spinner = (Spinner)findViewById(R.id.spinner);

        word = findViewById(R.id.editText);
        /*
        word.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText editText = (EditText) v;
                if(!hasFocus && !word.getText().toString().equals("")){
                    //Toast.makeText(getApplicationContext(), word.getText().toString(), Toast.LENGTH_LONG).show();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(),0);

                    inpt = editText.getText().toString();
                    Log.d("input =", inpt);
                    itemStr[1]=inpt;
                }
            }
        });
        */

        word.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent keyEvent) {
                Log.d("onkey", "onkey");
                if((keyCode == keyEvent.KEYCODE_ENTER)){
                    EditText editText = (EditText) v;
                    inpt=editText.getText().toString();
                    Log.d("input =", inpt);
                    itemStr[1]=inpt;
                    return true;
                }
                return false;
            }
        });




        if (word == null)
            Log.d("word is null =", "null");
        //inpt=word.getText().toString();
        Log.d("input =", inpt);
        //itemStr[1]=inpt;

        if(inpt != ""){

            wordToTran = true;
        }
        else wordToTran = false;

        ArrayAdapter<String> langAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,tLangHandler.language_codes);
        langAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(langAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (userSelect) { //userSelect&&wordToTran

                    final String item = (String) parent.getItemAtPosition(position);
                    Log.i("onItemSelected :trgtLng", item);
                    System.out.println("onWordSelect :word " + itemStr[1]);
                    //Log.i("onWordSelect :word", itemStr[1]);
                    itemStr[0]=item;

                    //TODO : call of async subclass goes here
                    new FetchTranslation().execute(itemStr);//possible cause of issue? had 'item' in it prior

                    userSelect = false;
                    wordToTran = false;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        userSelect = true;

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem shareItem = menu.findItem(R.id.action_share);
        provider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        if (provider == null)
            Log.d("MainActivity", "noshare provider");

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            /*
            switch (id){
                //case R.id.submenu1: root.setBackgroundColor(ContextCompat.getColor(getActivity)); break;
                //case R.id.submenu2: root.setBackgroundColor(getResources().getColor(android.R.color.background_dark)); break; //Color.parseColor("#222222") Color.BLACK
                default:
                    //return super.onOptionsItemSelected(item);
            }
            */
            return true;
        }

        if(id == R.id.action_help){
            Intent intent = new Intent(MainActivity.this, Help.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_share) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, "Hi there");
            if (provider != null) {
                provider.setShareIntent(intent);
            } else
                Toast.makeText(this, "no provider", Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    //AsyncTask<Params, Progress, Result>
    private class FetchTranslation extends AsyncTask<String,Void,String> {


        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConnection =null;
            String translation = "";


            try{
                String target = "target="+params[0]+"&";
                String input = "input="+params[1];

                URL url = new URL(baseUrl + target + input);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("X-RapidAPI-Key","8db1d44a5emsh082121746e4b546p16eb75jsn4e81403eff20");

                urlConnection.connect();


                if (urlConnection.getInputStream() == null) {
                    Log.e("no connection", "no connection");
                    return null;
                }
                translation = getStringFromBuffer(
                        new BufferedReader(new InputStreamReader(urlConnection.getInputStream())));
                Log.d("translation", translation);
            }catch (Exception e){
                Log.e("MainActivity","Error" + e.getMessage());
                return null;
            }finally {
                if(urlConnection !=null)
                    urlConnection.disconnect();
            }

            return translation;
        }

        private String getStringFromBuffer(BufferedReader bufferedReader) throws Exception{
            StringBuffer buffer = new StringBuffer();
            String line;

            while((line = bufferedReader.readLine()) != null){
                buffer.append(line + '\n');

            }
            if (bufferedReader!=null){
                try{
                    bufferedReader.close();
                }catch (IOException e){
                    Log.e("MainActivity","Error" + e.getMessage());
                    return null;
                }
            }
            Log.d("translation", buffer.toString());
            return  tLangHandler.getTranslation(buffer.toString());
        }

        @Override
        protected void onPostExecute(String result) {

            if(result != null){
                Log.d("MainActivity",result);
                Intent intent = new Intent(MainActivity.this,ResultActivity.class);
                intent.putExtra("translation",result);
                startActivity(intent);
            }




        }





    }
}
