package raihana.msd.mcp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import raihana.msd.mcp.adapter.CategoryListAdapter;
import raihana.msd.mcp.connection.ConnectionClass;
import raihana.msd.mcp.model.EightColumnClass;
import raihana.msd.mcp.model.TwentyColumnClass;
import raihana.msd.mcp.utils.SharedPreference;

public class CategeoryListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private SwipeRefreshLayout swipeRefreshLayout;
    private EditText etSearch;
    private ImageView ivPrint,ivSave;
    private TextView tvPayType,tvFakturNo,tvJumlahTagihan,tvStatusOrder;
    private RecyclerView myRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<EightColumnClass> MyRecordset = new ArrayList<>();
    private boolean success = false;
    private ConnectionClass connectionClass;
    private CategoryListAdapter MyAdapter;
    private SharedPreference sharedPreference;
    private String s,s1,s2;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);
        sharedPreference = new SharedPreference(this);
        etSearch = findViewById(R.id.et_search);
        myRecyclerView = (RecyclerView) findViewById(R.id.rv_list);
        myRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        myRecyclerView.setLayoutManager(mLayoutManager);
        MyRecordset = new ArrayList<>();

        connectionClass = new ConnectionClass();
        ivPrint = findViewById(R.id.iv_print);
        ivSave = findViewById(R.id.iv_save);
        tvJumlahTagihan = findViewById(R.id.tv_jumlah_tagihan);
        tvPayType = findViewById(R.id.tv_pay_type);
        tvFakturNo = findViewById(R.id.tv_faktur_no);
        tvStatusOrder = findViewById(R.id.tv_status_order);
        swipeRefreshLayout = findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(this);

        if (MyRecordset.isEmpty() == true) {
            new GetCategoryList ().execute();
            Log.d("MyRecordset : ","Kosong");
        }


    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        if (MyRecordset.isEmpty() == true) {
            new GetCategoryList ().execute();
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private class SyncData extends AsyncTask<String, String, String> {
        String msg = "Internet/DB_Credentials/Windows_FireWall_TurnOn Error, See Android Monitor in the bottom For details";
        ProgressDialog progress;


        @Override
        protected void onPreExecute() {
        //  progress = ProgressDialog.show(PaymentActivity.this,"Search Data", "Searching ! Please wait...", true);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection conn = connectionClass.CONN();
                if (conn == null) {
                    success = false;
                } else {
                    String query = "EXEC DB_A4A292_SOUP.dbo.SP_ANDROID_LIST_CATEGORY ";
                    Log.d("QUERY", query);
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    MyRecordset.clear();
                    if (rs != null) {
                        while (rs.next()) {
                            try {
                                MyRecordset.add(new EightColumnClass(rs.getString("COLUMN_1"),rs.getString("COLUMN_2"),"","","","","",""));
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
        //    progress.dismiss();
            swipeRefreshLayout.setRefreshing(false);

            if (success == false) {

            } else {
                try {
                    MyAdapter = new CategoryListAdapter(MyRecordset , CategeoryListActivity.this);
                    myRecyclerView.setAdapter(MyAdapter);
                } catch (Exception ex) {
                }
            }
        }
    }

    private class GetCategoryList extends AsyncTask<String, String, String> {
        String msg = "Internet/DB_Credentials/Windows_FireWall_TurnOn Error, See Android Monitor in the bottom For details";
        ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            //  progress = ProgressDialog.show(PaymentActivity.this,"Search Data", "Searching ! Please wait...", true);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
               // MyRecordset.clear();
                MyRecordset.add(new EightColumnClass("NASI-MIE","1","","","","","",""));
                MyRecordset.add(new EightColumnClass("SOUP IKAN","2","","","","","",""));
                MyRecordset.add(new EightColumnClass("WOKU","3","","","","","",""));
                MyRecordset.add(new EightColumnClass("GURAME","4","","","","","",""));
                MyRecordset.add(new EightColumnClass("KEPITING","5","","","","","",""));
                MyRecordset.add(new EightColumnClass("CUMI","6","","","","","",""));
                MyRecordset.add(new EightColumnClass("UDANG","7","","","","","",""));
                MyRecordset.add(new EightColumnClass("SATE-AYAM","8","","","","","",""));
                MyRecordset.add(new EightColumnClass("CAH-TUMIS","9","","","","","",""));
                MyRecordset.add(new EightColumnClass("TAHU TEMPE","10","","","","","",""));
                MyRecordset.add(new EightColumnClass("SOFT DRINK","11","","","","","",""));
                MyRecordset.add(new EightColumnClass("JUICE","12","","","","","",""));
                MyRecordset.add(new EightColumnClass("MINUMAN","13","","","","","",""));
                MyRecordset.add(new EightColumnClass("MINUMAN LAIN","14","","","","","",""));
                MyRecordset.add(new EightColumnClass("CHARGE","15","","","","","",""));
                MyRecordset.add(new EightColumnClass("DISCOUNT","16","","","","","",""));
                MyRecordset.add(new EightColumnClass("MAKANAN LAIN","17","","","","","",""));
                MyRecordset.add(new EightColumnClass("KAKAP","18","","","","","",""));

            //    System.out.println("String Added in ArrayList= "+ MyRecordset);

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
            //    progress.dismiss();

            MyAdapter = new CategoryListAdapter(MyRecordset , CategeoryListActivity.this);
            myRecyclerView.setAdapter(MyAdapter);
            swipeRefreshLayout.setRefreshing(false);

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
