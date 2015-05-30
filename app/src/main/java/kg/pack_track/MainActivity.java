package kg.pack_track;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.File;
import java.io.IOException;

public class MainActivity extends ActionBarActivity {

    private ArrayAdapter<String> packsAdapter;
    private ArrayList<String> packs;
    private ListView lvPacks;

    private ArrayList<Pattern> fedex;
    private ArrayList<Pattern> ups;
    private ArrayList<Pattern> usps;
    private HashMap<String, String> urls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createRegex();

        lvPacks = (ListView) findViewById(R.id.lvPacks);
        packs = new ArrayList<String>();
        //add file storing functionality
        packsAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, packs);
        lvPacks.setAdapter(packsAdapter);
        setupListViewListener();
        setupListViewClick();
        loadDB();

    }

    private void setupListViewListener() {
        lvPacks.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent,
                                                   View view, int position, long id) {
                        deleteTrack(position);
                        packs.remove(position);
                        packsAdapter.notifyDataSetChanged();
                        //add write to file function call

                        return true;
                    }
                }
        );

    }

    private void loadDB() {
        DatabaseHandler db = new DatabaseHandler(this);
        List<String> nums = db.getAllTrackingNums();
        for (int i = 0; i < nums.size(); i++) {
            String urlText = formUrl(nums.get(i));
            packsAdapter.add(urlText);
        }

    }

    private void deleteTrack(int position) {
        DatabaseHandler db = new DatabaseHandler(this);
        db.deleteTrackingNumber(position);
    }

    private void setupListViewClick() {
        lvPacks.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String url = packs.get(position);
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(browserIntent);
                    }
                }
        );
    }


    public void onAddPack(View v) {
        DatabaseHandler db = new DatabaseHandler(this);

        EditText etNewPack = (EditText) findViewById(R.id.etNewPack);
        String packText = etNewPack.getText().toString();
        packText = packText.replaceAll("\\s+", "");
        String urlText = formUrl(packText);
        if (urlText.equals("")) {
            createAlertError();
            return;
        }
        packsAdapter.add(urlText);
        etNewPack.setText("");
        db.addTrackingNumber(packText);
        //add write item function call

    }

    private String formUrl(String input) {
        String url = "";

        for (int i = 0; i < fedex.size(); i++) {
            Matcher m = fedex.get(i).matcher(input);
            if (m.find()) {
                url = "" + urls.get("fedex")+input;
                return url;
            }
        }

        for (int i = 0; i < ups.size(); i++) {
            Matcher m = ups.get(i).matcher(input);
            if (m.find()) {
                url = "" + urls.get("ups")+input;
                return url;
            }
        }

        for (int i = 0; i < usps.size(); i++) {
            Matcher m = usps.get(i).matcher(input);
            if (m.find()) {
                url = "" + urls.get("usps")+input;
                return url;
            }
        }

        return url;
    }

    private void createRegex () {
        fedex = new ArrayList<Pattern>();
        ups = new ArrayList<Pattern>();
        usps = new ArrayList<Pattern>();

        Pattern p = Pattern.compile("\\b(1Z ?[0-9A-Z]{3} ?[0-9A-Z]{3} ?[0-9A-Z]{2} ?[0-9A-Z]{4} ?[0-9A-Z]{3} ?[0-9A-Z]|[\\dT]\\d\\d\\d ?\\d\\d\\d\\d ?\\d\\d\\d)\\b");
        ups.add(p);

        Pattern fed1 = Pattern.compile("(\\b96\\d{20}\\b)|(\\b\\d{15}\\b)|(\\b\\d{12}\\b)");
        Pattern fed2 = Pattern.compile("\\b((98\\d\\d\\d\\d\\d?\\d\\d\\d\\d|98\\d\\d) ?\\d\\d\\d\\d ?\\d\\d\\d\\d( ?\\d\\d\\d)?)\\b");
        Pattern fed3 = Pattern.compile("^[0-9]{15}$");
        fedex.add(fed1);
        fedex.add(fed2);
        fedex.add(fed3);

        Pattern usps1 = Pattern.compile("(\\b\\d{30}\\b)|(\\b91\\d+\\b)|(\\b\\d{20}\\b | (\\b\\d{26}\\b))");
        Pattern usps2 = Pattern.compile("^E\\D{1}\\d{9}\\D{2}$|^9\\d{15,21}$");
        Pattern usps3 = Pattern.compile("^91[0-9]+$");
        Pattern usps4 = Pattern.compile("^[A-Za-z]{2}[0-9]+US$");
        usps.add(usps1);
        usps.add(usps2);
        usps.add(usps3);
        usps.add(usps4);

        urls = new HashMap<String, String>();
        urls.put("fedex", "http://www.fedex.com/Tracking?action=track&tracknumbers=");
        urls.put("ups", "http://wwwapps.ups.com/WebTracking/track?track=yes&trackNums=");
        urls.put("usps", "https://tools.usps.com/go/TrackConfirmAction_input?qtc_tLabels1=");
    }

    private void createAlertError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage("Please enter a valid UPS, USPS, or Fedex package number.");
        builder.setCancelable(false);
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

}
