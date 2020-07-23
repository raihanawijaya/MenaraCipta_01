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

import raihana.msd.mcp.adapter.SearchArticleAdapter;
import raihana.msd.mcp.connection.ConnectionClass;
import raihana.msd.mcp.model.TwentyColumnClass;
import raihana.msd.mcp.utils.SharedPreference;

public class SearchArticleActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private SwipeRefreshLayout swipeRefreshLayout;
    private EditText etSearch;
    private ImageView ivSearch;
    private RecyclerView myRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<TwentyColumnClass> MyRecordset = new ArrayList<>();
    private boolean success = false;
    private ConnectionClass connectionClass;
    private SearchArticleAdapter MyAdapter;
    private SharedPreference sharedPreference;
    private String onDate, storeCode, username;
    private String s,s1,s2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_article);
        sharedPreference = new SharedPreference(this);
        etSearch = findViewById(R.id.et_search);
        myRecyclerView = (RecyclerView) findViewById(R.id.rv_search_article);
        myRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        myRecyclerView.setLayoutManager(mLayoutManager);
        MyRecordset = new ArrayList<TwentyColumnClass>();
        connectionClass = new ConnectionClass();
        username = sharedPreference.getObjectData("username", String.class);
        onDate = (String) getIntent().getSerializableExtra("onDate");
        ivSearch = findViewById(R.id.iv_search);

        swipeRefreshLayout = findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(this);


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

    @Override
    public void onRefresh() {
        SyncData syncData = new SyncData();// this is the Asynctask, which is used to process in background to reduce load on app process
        syncData.execute("");
    }

    private class SyncData extends AsyncTask<String, String, String> {
        String msg = "Internet/DB_Credentials/Windows_FireWall_TurnOn Error, See Android Monitor in the bottom For details";
        ProgressDialog progress;

        String articleSearch = etSearch.getText().toString();

        @Override
        protected void onPreExecute() {
         progress = ProgressDialog.show(SearchArticleActivity.this,"Search Data", "Searching ! Please wait...", true);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection conn = connectionClass.CONN();
                if (conn == null) {
                    success = false;
                } else {
                    String query = "EXEC DB_A4A292_SOUP.dbo.SP_GADGET_LIST_CATALOGUE_WALLET_SEARCH '" + articleSearch +"'";
                    Log.d("QUERY", query);
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    if (rs != null) {
                        MyRecordset.clear();
                        while (rs.next()) {
                            try {
                                MyRecordset.add(new TwentyColumnClass(rs.getString("COLUMN_1"),rs.getString("COLUMN_2"),rs.getString("COLUMN_3"),rs.getString("COLUMN_4"),rs.getString("COLUMN_5"),rs.getString("COLUMN_6"),rs.getString("COLUMN_7"),rs.getString("COLUMN_8"),rs.getString("COLUMN_9"),rs.getString("COLUMN_10"),rs.getString("COLUMN_11"),rs.getString("COLUMN_12"),rs.getString("COLUMN_13"),rs.getString("COLUMN_14"),rs.getString("COLUMN_15"),rs.getString("COLUMN_16"),rs.getString("COLUMN_17"),rs.getString("COLUMN_18"),rs.getString("COLUMN_19"),rs.getString("COLUMN_20")));
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
            progress.dismiss();
            swipeRefreshLayout.setRefreshing(false);


            if (success == false) {
            } else {
                try {
                    MyAdapter = new SearchArticleAdapter(MyRecordset, SearchArticleActivity.this, SearchArticleActivity.this);
                    myRecyclerView.setAdapter(MyAdapter);
                } catch (Exception ex) {
                }
            }
        }
    }

    public void onChildItemClicked(TwentyColumnClass data){
        Intent intent = getIntent();
        intent.putExtra("SearchProductName",data.getColumn_1());
        intent.putExtra("SearchProductColour",data.getColumn_2());
        intent.putExtra("SearchProductPrice",data.getColumn_3());
        Log.d("SearchProductName : ",data.getColumn_1());
        setResult(Activity.RESULT_OK,intent);
        finish();
    }


    public void SendData(String a, String b, String c) {
        this.s = a;
        this.s1 = b;
        this.s2 = c;
        Intent intent = getIntent();
        intent.putExtra("SearchProductName",s);
        intent.putExtra("SearchProductColour",s1);
        intent.putExtra("SearchProductPrice",s2);
        Log.d("SearchProductName : ",s);
        setResult(Activity.RESULT_OK,intent);
        finish();
    }


}
