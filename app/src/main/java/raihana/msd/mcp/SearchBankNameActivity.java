package raihana.msd.mcp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
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

import raihana.msd.mcp.adapter.SearchBankNameAdapter;
import raihana.msd.mcp.connection.ConnectionClass;
import raihana.msd.mcp.model.OneColumnClass;
import raihana.msd.mcp.utils.SharedPreference;

public class SearchBankNameActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private SwipeRefreshLayout swipeRefreshLayout;
    private EditText etSearch;
    private ImageView ivSearch;
    private RecyclerView myRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<OneColumnClass> MyRecordset = new ArrayList<>();
    private boolean success = false;
    private ConnectionClass connectionClass;
    private SearchBankNameAdapter MyAdapter;
    private SharedPreference sharedPreference;
    private String onDate, storeCode, username;
    private String s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_bank);
        sharedPreference = new SharedPreference(this);
        etSearch = findViewById(R.id.et_search);
        myRecyclerView = (RecyclerView) findViewById(R.id.rv_search_bank);
        myRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        myRecyclerView.setLayoutManager(mLayoutManager);
        MyRecordset = new ArrayList<OneColumnClass>();
        connectionClass = new ConnectionClass();
        username = sharedPreference.getObjectData("username", String.class);
        onDate = (String) getIntent().getSerializableExtra("onDate");
        ivSearch = findViewById(R.id.iv_search);

        swipeRefreshLayout = findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(this);

        new SyncData().execute();
        //pop up activity
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*.7),(int)(height*.3));

    }

    @Override
    public void onRefresh() {
        SyncData syncData = new SyncData();// this is the Asynctask, which is used to process in background to reduce load on app process
        syncData.execute("");
    }

    private class SyncData extends AsyncTask<String, String, String> {
        String msg = "Internet/DB_Credentials/Windows_FireWall_TurnOn Error, See Android Monitor in the bottom For details";
        ProgressDialog progress;


        @Override
        protected void onPreExecute() {
       //  progress = ProgressDialog.show(SearchBankNameActivity.this,"Search Data", "Searching ! Please wait...", true);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection conn = connectionClass.CONN();
                if (conn == null) {
                    success = false;
                } else {
                    String query = "EXEC DB_A4A292_SOUP.dbo.SP_GADGET_LIST_BANK_NAME ";
                    Log.d("QUERY", query);
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    if (rs != null) {
                        MyRecordset.clear();
                        while (rs.next()) {
                            try {
                                MyRecordset.add(new OneColumnClass(rs.getString("COLUMN_1")));
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                        msg = "Found";
                        success = true;
                        Log.d("msg", msg);

                    } else {
                        msg = "No Data Found";
                        success = false;
                        Log.d("msg", msg);
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
       //     progress.dismiss();
            swipeRefreshLayout.setRefreshing(false);


            if (success == false) {
            } else {
                try {
                    MyAdapter = new SearchBankNameAdapter(MyRecordset, SearchBankNameActivity.this, SearchBankNameActivity.this);
                    myRecyclerView.setAdapter(MyAdapter);
                } catch (Exception ex) {
                }
            }
        }
    }


    public void SendData(String a) {
        this.s = a;
        Intent intent = getIntent();
        intent.putExtra("SearchBankName",s);
        setResult(Activity.RESULT_OK,intent);
        finish();
    }


}
