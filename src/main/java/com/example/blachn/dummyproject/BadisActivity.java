package com.example.blachn.dummyproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

public class BadisActivity extends AppCompatActivity {

    ArrayAdapter badiliste;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badi_typen);
        EditText badisearch = (EditText) findViewById(R.id.badisearch);
        badisearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                badiliste.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        addBadisToList();
    }

    private void addBadisToList() {
        ListView badis = (ListView) findViewById(R.id.badiliste);
        badiliste = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        final ArrayList<ArrayList<String>> allBadis = BadiData.allBadis(getApplicationContext());
        Set<String> badityp = new TreeSet<>();
        for (ArrayList<String> b : allBadis) {
            if(badityp.add(b.get(1))) {
                badityp.add(b.get(1));
            } else if(b.get(3) != "") {
                badityp.add(b.get(1)+" - "+b.get(3));
            } else {
                badityp.add(b.get(1)+" - "+b.get(4)+" "+b.get(5));
            }
        }
        badiliste.addAll(badityp);
        badis.setAdapter(badiliste);

        AdapterView.OnItemClickListener mListClickedHandler = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), BadiDetailsAcitivity.class);
                String selected = parent.getItemAtPosition(position).toString();
                //kleine Infobox anzeigne
                Toast.makeText(BadisActivity.this, selected, Toast.LENGTH_SHORT).show();
                //Intent mit Zusatzinformationen - hier die Badi Nummer
                intent.putExtra("badi", allBadis.get(position).get(0));
                intent.putExtra("name", selected);
                startActivity(intent);
            }
        };
        badis.setOnItemClickListener(mListClickedHandler);
    }
}
