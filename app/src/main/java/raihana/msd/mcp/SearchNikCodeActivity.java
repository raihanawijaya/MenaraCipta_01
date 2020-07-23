package raihana.msd.mcp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import raihana.msd.mcp.adapter.SearchNikCodeAdapter;
import raihana.msd.mcp.connection.ConnectionClass;
import raihana.msd.mcp.model.EightColumnClass;
import raihana.msd.mcp.utils.SharedPreference;

public class SearchNikCodeActivity extends AppCompatActivity {

    private EditText etSearch;
    private ImageView ivSearch;
    private RecyclerView myRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<EightColumnClass> MyRecordset = new ArrayList<>();
    private boolean success = false;
    private ConnectionClass connectionClass;
    private SearchNikCodeAdapter MyAdapter;
    private SharedPreference sharedPreference;
    private String onDate, storeCode, username;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_nik);
        sharedPreference = new SharedPreference(this);
        etSearch = findViewById(R.id.et_search);
        myRecyclerView = (RecyclerView) findViewById(R.id.rv_search_nik_code);
        myRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        myRecyclerView.setLayoutManager(mLayoutManager);
        MyRecordset = new ArrayList<EightColumnClass>();
        connectionClass = new ConnectionClass();
        username = sharedPreference.getObjectData("username", String.class);
        onDate = (String) getIntent().getSerializableExtra("onDate");
        ivSearch = findViewById(R.id.iv_search);

        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SyncData syncData = new SyncData();// this is the Asynctask, which is used to process in background to reduce load on app process
                syncData.execute("");
            }
        });

        etSearch.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
            // If the event is a key-down event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && keyCode == (KeyEvent.KEYCODE_ENTER)) {
                // Perform action on key press
                SyncData syncData = new SyncData();// this is the Asynctask, which is used to process in background to reduce load on app process
                syncData.execute("");
                return true;
            }

            return false;
            }
        });


    }

    private class SyncData extends AsyncTask<String, String, String> {
        String msg = "Internet/DB_Credentials/Windows_FireWall_TurnOn Error, See Android Monitor in the bottom For details";
        ProgressDialog progress;

        String articleSearch = etSearch.getText().toString();

        @Override
        protected void onPreExecute() {
         progress = ProgressDialog.show(SearchNikCodeActivity.this,"Search Data", "Searching ! Please wait...", true);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection conn = connectionClass.CONN();
                if (conn == null) {
                    success = false;
                } else {
                    String query = "EXEC DB_A4A292_SOUP.dbo.SP_GADGET_LIST_SEARCH_NIK '" + articleSearch +"'";
                    Log.d("QUERY", query);
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    if (rs != null) {
                        MyRecordset.clear();
                        while (rs.next()) {
                            try {
                               MyRecordset.add(new EightColumnClass(rs.getString("COLUMN_1"),rs.getString("COLUMN_2"),rs.getString("COLUMN_3"),rs.getString("COLUMN_4"),rs.getString("COLUMN_5"),rs.getString("COLUMN_6"),rs.getString("COLUMN_7"),rs.getString("COLUMN_8")));
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                        msg = "Found";
                        success = true;
                    } else {
                        msg = "No Data Found";
                        success = false;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Writer writer = new StringWriter();
                e.printStackTrace(new PrintWriter(writer));
                msg = writer.toString();
                success = false;

            }
            return msg;
        }

        @Override
        protected void onPostExecute(String msg) {
            progress.dismiss();

            if (success == false) {
            } else {
                try {
                    MyAdapter = new SearchNikCodeAdapter(MyRecordset, SearchNikCodeActivity.this);
                    myRecyclerView.setAdapter(MyAdapter);
                } catch (Exception ex) {
                }
            }
        }
    }


    public void onChildItemClicked(EightColumnClass data){
        Intent intent = getIntent();
        intent.putExtra("SearchNikCode",data.getColumn_1());
        intent.putExtra("SearchNikName",data.getColumn_2());
        setResult(Activity.RESULT_OK,intent);
        finish();
    }
}
