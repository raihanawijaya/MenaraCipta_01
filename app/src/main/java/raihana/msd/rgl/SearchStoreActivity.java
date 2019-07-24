package raihana.msd.rgl;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import raihana.msd.rgl.adapter.BDetailSearchAdapter;
import raihana.msd.rgl.adapter.SearchStoreAdapter;
import raihana.msd.rgl.connection.ConnectionClass;
import raihana.msd.rgl.model.BDetailSearchClass;
import raihana.msd.rgl.model.SearchStoreClass;
import raihana.msd.rgl.utils.SharedPreference;

public class SearchStoreActivity extends AppCompatActivity {

    private EditText etSearch;
    private ImageView ivSearch;
    private RecyclerView myRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<SearchStoreClass> list_detail_search_B = new ArrayList<>();
    private boolean success = false;
    private ConnectionClass connectionClass;
    private SearchStoreAdapter b_detail_search_adapter;
    private SharedPreference sharedPreference;
    private String onDate, storeCode, username;

    public void sync() {
        try{
            new SyncData().execute();
        }catch (Exception e){
            Log.e(SearchStoreActivity.class.getSimpleName(), "sync: ",e );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_store);
        sharedPreference = new SharedPreference(this);
        etSearch = findViewById(R.id.et_search);
        myRecyclerView = (RecyclerView) findViewById(R.id.rv_search_store);
        myRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        myRecyclerView.setLayoutManager(mLayoutManager);
        list_detail_search_B = new ArrayList<SearchStoreClass>();
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

    }

    private class SyncData extends AsyncTask<String, String, String> {
        String msg = "Internet/DB_Credentials/Windows_FireWall_TurnOn Error, See Android Monitor in the bottom For details";
        ProgressDialog progress;

        String articleSearch = etSearch.getText().toString();

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection conn = connectionClass.CONN();
                if (conn == null) {
                    success = false;
                } else {
                    String query = "EXEC DB_A4A292_RGL.dbo.SP_LIST_SEARCH_STORE '" + articleSearch + "','" + username +"'";
                    Log.d("QUERY", query);
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    if (rs != null) {
                        list_detail_search_B.clear(); // kalau datanya ada listnya di clear dulu
                        while (rs.next()) {
                            try {
                                // kalau ada data masukkan ke list
                                list_detail_search_B.add(new SearchStoreClass(rs.getString("STORE_CODE"),rs.getString("STORE_NAME"),rs.getString("ALAMAT_STORE"),rs.getString("WILAYAH")));
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                        /*swipeRefreshLayout.setRefreshing(false);*/
                        msg = "Found";
                        success = true;
                    } else {
                        /*swipeRefreshLayout.setRefreshing(false);*/
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
            if (success == false) {
            } else {
                try {
                    b_detail_search_adapter = new SearchStoreAdapter(list_detail_search_B, SearchStoreActivity.this);
                    myRecyclerView.setAdapter(b_detail_search_adapter);
                } catch (Exception ex) {
                }
            }
        }
    }


    public void onChildItemClicked(SearchStoreClass data){
        Intent intent = getIntent();
        intent.putExtra("searchStoreCode",data.getStoreCode());
        intent.putExtra("searchStoreName",data.getStoreName());
        setResult(Activity.RESULT_OK,intent);
        finish();
    }
}
