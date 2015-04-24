package kg.pack_track;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.io.File;
import java.io.IOException;

public class MainActivity extends ActionBarActivity {

    private ArrayAdapter<String> packsAdapter;
    private ArrayList<String> packs;
    private ListView lvPacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvPacks = (ListView) findViewById(R.id.lvPacks);
        packs = new ArrayList<String>();
        //add file storing functionality
        packsAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, packs);
        lvPacks.setAdapter(packsAdapter);
        setupListViewListener();

    }

    private void setupListViewListener() {
        lvPacks.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        packs.remove(position);
                        packsAdapter.notifyDataSetChanged();
                        //add write to file function call
                        return true;
                    }
                }
        );

    }


    public void onAddPack(View v) {
        EditText etNewPack = (EditText) findViewById(R.id.etNewPack);
        String packText = etNewPack.getText().toString();
        //Add package regex stuff here - function calls / etc..
        packsAdapter.add(packText);
        etNewPack.setText("");
        //add write item function call

    }


}
