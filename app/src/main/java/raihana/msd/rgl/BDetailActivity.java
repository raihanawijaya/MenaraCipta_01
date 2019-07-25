package raihana.msd.rgl;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import raihana.msd.rgl.adapter.BDetailAdapter;
import raihana.msd.rgl.connection.ConnectionClass;
import raihana.msd.rgl.model.BDetailClass;
import raihana.msd.rgl.utils.SharedPreference;

public class BDetailActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private EditText etTrxDate, etDiscCode, etUniqueID,etPrice, etQty;
    private ImageView ivDelete, ivSearchArticle, ivSearchStore, ivAdd;
    private TextView tvStoreCode, tvStoreName, tvTrxNo, tvPrice, tvArticle, tvUsername;
    private String storeCode,storeName, onDate, trxNo, username, price;
    private Button btn_save;
    private boolean success = false;
    //FILE ETC
    private RecyclerView myRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<BDetailClass> list_detail_B = new ArrayList<>();
    private ConnectionClass connectionClass;
    private BDetailAdapter b_detail_adapter;
    private SharedPreference sharedPreference;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Context context;

    public void sync() {
        try{
            new SyncData().execute();
        }catch (Exception e){
            Log.e(BDetailActivity.class.getSimpleName(), "sync: ",e );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b_detail);
        connectionClass = new ConnectionClass();
        this.context = context;
        //RECYCLER VIEW
        myRecyclerView = (RecyclerView) findViewById(R.id.rv_b_detail);
        myRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        myRecyclerView.setLayoutManager(mLayoutManager);
        list_detail_B = new ArrayList<BDetailClass>();
        //SHARED PREFERENCE / SERIALIZABLE EXTRA
        Context mcontext;
        sharedPreference = new SharedPreference(this);
        username = sharedPreference.getObjectData("username", String.class);
        price = (String) getIntent().getSerializableExtra("getPrice");
        onDate = (String) getIntent().getSerializableExtra("onDate");
        storeCode = (String) getIntent().getSerializableExtra("storeCode");
        storeName = (String) getIntent().getSerializableExtra("storeName");
        trxNo = (String) getIntent().getSerializableExtra("noTrx");
        //EDITTEXT
        etTrxDate = findViewById(R.id.et_trx_date);
        etQty = findViewById(R.id.et_qty);
        etTrxDate.setText(onDate);
        /*etUniqueID.setText(uniqueID);
        etPrice.setText(price);*/
        //TEXTVIEW
        tvUsername = findViewById(R.id.tv_username);
        tvStoreCode = findViewById(R.id.tv_store_code);
        tvStoreName = findViewById(R.id.tv_store_name);
        tvTrxNo = findViewById(R.id.tv_trx_no);
        tvPrice = findViewById(R.id.tv_price);
        tvArticle = findViewById(R.id.tv_article);
        tvStoreCode.setText(storeCode);
        tvStoreName.setText(storeName);
        tvTrxNo.setText(trxNo);
        tvUsername.setText(username);

        //IMAGEVIEW
        ivAdd = findViewById(R.id.iv_add);
        ivSearchStore = findViewById(R.id.iv_search_store);
        ivSearchArticle = findViewById(R.id.iv_search_article);
        ivDelete = findViewById(R.id.iv_delete);
        btn_save = findViewById(R.id.btn_save);
        //SWIPE
        swipeRefreshLayout = findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(this);
        final SyncData orderData = new SyncData();
        orderData.execute();

       ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(BDetailActivity.this)
                        .setTitle("Confirm?")
                        .setMessage("Apakah anda ingin membuat order baru?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                InputNewSales inputNewSales = new InputNewSales();// this is the Asynctask, which is used to process in background to reduce load on app process
                                inputNewSales.execute("");
                                etTrxDate.setText(sharedPreference.getObjectData("today", String.class));
                                setDataEmpty();
                            }
                        })
                        .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputData inputData = new InputData();// this is the Asynctask, which is used to process in background to reduce load on app process
                inputData.execute("");
            }
        });


       /*
        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteData deleteData = new DeleteData();// this is the Asynctask, which is used to process in background to reduce load on app process
                deleteData.execute("");
                onRefresh();
            }
        });*/

        ivSearchArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gotosearch = new Intent(BDetailActivity.this,SearchArticleActivity.class);
                startActivityForResult(gotosearch,124);
            }
        });

        ivSearchStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gotosearch = new Intent(BDetailActivity.this,SearchStoreActivity.class);
                gotosearch.putExtra("username", username);
                startActivityForResult(gotosearch,123);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == 123){
                String searchStoreCode = data.getStringExtra("searchStoreCode");
                String searchStoreName = data.getStringExtra("searchStoreName");
                tvStoreCode.setText(searchStoreCode);
                tvStoreName.setText(searchStoreName);
            } else if(requestCode == 124){
                String searchArticle = data.getStringExtra("searchArticle");
                String searchPrice = data.getStringExtra("searchPrice");
                tvArticle.setText(searchArticle);
                tvPrice.setText(searchPrice);
            }
        }
    }

    @Override
    public void onRefresh() {
        try {
            new SyncData().execute();
            swipeRefreshLayout.setRefreshing(false);
        }catch (Exception e){
            Log.e(BDetailActivity.class.getSimpleName()
                    , "onResume: Start Service Failed, because : " + e.getMessage() );
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private class InputNewSales extends AsyncTask<String, String, String> {
        String msg = "Internet/DB_Credentials/Windows_FireWall_TurnOn Error, See Android Monitor in the bottom For details";
        ProgressDialog progress;

        String trxNoNew;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(BDetailActivity.this,
                    "Adding New Trx Number", "Loading! Please wait...", true);
        }

        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection conn = connectionClass.CONN();
                if (conn == null) {
                    success = false;
                } else {
                    String query = "EXEC DB_A4A292_RGL.dbo.SP_GET_NEW_SALES_ORDER '" + storeCode + "'";Log.d("QUERY", query);
                    Log.d("QUERY", query);
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    if (rs != null) {
                        while (rs.next()) {
                            try {
                                // kalau ada data masukkan ke list
                                trxNoNew = rs.getString("TRX_NO");
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
            progress.dismiss();
            if (success == false) {
            } else {
                try {
                    trxNo = trxNoNew;
                    tvTrxNo.setText(trxNo);
                    onRefresh();
                } catch (Exception ex) {
                }
            }

        }
    }

    private class SyncData extends AsyncTask<String, String, String> {
        String msg = "Internet/DB_Credentials/Windows_FireWall_TurnOn Error, See Android Monitor in the bottom For details";
        ProgressDialog progress;
        String trxDate = etTrxDate.getText().toString();
        String trxCode = tvTrxNo.getText().toString();

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
                    String query = "EXEC DB_A4A292_RGL.dbo.SP_TRX_USER_ID_STORE '" + username + "','" + trxCode +"'";
                    Log.d("QUERY", query);
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    if (rs != null) {
                        list_detail_B.clear(); // kalau datanya ada listnya di clear dulu
                        while (rs.next()) {
                            try {
                                // kalau ada data masukkan ke list
                                list_detail_B.add(new BDetailClass(rs.getString("ARTICLE"),rs.getString("QTY"),rs.getString("PRICE"),rs.getString("GROSS")));
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
            if (success == false) {
            } else {
                try {
                    b_detail_adapter = new BDetailAdapter(list_detail_B, BDetailActivity.this);
                    myRecyclerView.setAdapter(b_detail_adapter);
                } catch (Exception ex) {
                }
            }
        }
    }

    private class InputData extends AsyncTask<String, String, String> {
        String msg = "Internet/DB_Credentials/Windows_FireWall_TurnOn Error, See Android Monitor in the bottom For details";
        ProgressDialog progress;
        String trxNo = tvTrxNo.getText().toString();
        String trxDate = etTrxDate.getText().toString();
        String article = tvArticle.getText().toString();
        String priceString = tvPrice.getText().toString().replace(",","");;
        String qtyString = etQty.getText().toString();
        int qty = Integer.parseInt(qtyString); //DONE
        float price = Float.parseFloat(priceString); //DONE

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(BDetailActivity.this,
                    "Input Data", "Loading! Please wait...", true);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection conn = connectionClass.CONN();
                if (conn == null) {
                    success = false;
                } else {
                    String query = "EXEC DB_A4A292_RGL.dbo.SP_INSERT_SALES '" + trxNo   + "','" + trxDate + "','" + storeCode + "','" + username + "','" + article + "','" + qty + "','" + price +"'";
                    Log.d("QUERY", query);
                    Statement stmt = conn.createStatement();
                    int status = stmt.executeUpdate(query);
                    if (status != 0) {
                        msg = "Input Success";
                        success = true;
                    } else {
                        msg = "Input Failed";
                        success = false;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Writer writer = new StringWriter();
                e.printStackTrace(new PrintWriter(writer));
                msg = "Gagal : Check Uniqueid & Disc !" ;//writer.toString();
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
                    b_detail_adapter = new BDetailAdapter(list_detail_B, BDetailActivity.this);
                    myRecyclerView.setAdapter(b_detail_adapter);
                } catch (Exception ex) {
                }
            }

        }
    }




/*    private class DeleteData extends AsyncTask<String, String, String> {
        String msg = "Internet/DB_Credentials/Windows_FireWall_TurnOn Error, See Android Monitor in the bottom For details";
        ProgressDialog progress;
        String trxDate = onDate;
        String nikCode = storeCode;
        String uniqueID = etUniqueID.getText().toString();
        String discCode = etDiscCode.getText().toString();
        String priceString = etPrice.getText().toString().replace(",","");
        int price = Integer.parseInt(priceString); //DONE

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(BDetailActivity.this,
                    "Input Data", "Loading! Please wait...", true);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection conn = connectionClass.CONN();
                if (conn == null) {
                    success = false;
                } else {
                    String query = "EXEC ANDROIDXP_ENJI.[dbo].[SP_DELETE_SALES] '" + trxDate + "','" + storeCode + "','" + nikCode + "','" + uniqueID + "','" + discCode + "','" + price +"'";
                    Log.d("QUERY", query);
                    Statement stmt = conn.createStatement();
                    int status = stmt.executeUpdate(query);
                    if (status != 0) {
                        msg = "Delete Success";
                        success = true;
                    } else {
                        msg = "Delete Failed";
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
            Toast.makeText(BDetailActivity.this, msg + "", Toast.LENGTH_SHORT).show();
            if (success == false) {
            } else {
                try {
                    b_detail_adapter = new BDetailAdapter(list_detail_B, BDetailActivity.this);
                    myRecyclerView.setAdapter(b_detail_adapter);
                } catch (Exception ex) {
                }
            }

        }
    }

    private class UpdateData extends AsyncTask<String, String, String> {
        String msg = "Internet/DB_Credentials/Windows_FireWall_TurnOn Error, See Android Monitor in the bottom For details";
        ProgressDialog progress;
        String trxDate = onDate;
        String nikCode = storeCode;
        String uniqueID = etUniqueID.getText().toString();
        String discCode = etDiscCode.getText().toString();
        String priceString = etPrice.getText().toString().replace(",","");
        int price = Integer.parseInt(priceString); //DONE

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(BDetailActivity.this,
                    "Input Data", "Loading! Please wait...", true);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection conn = connectionClass.CONN();
                if (conn == null) {
                    success = false;
                } else {
                    String query = "EXEC ANDROIDXP_ENJI.[dbo].[SP_UPDATE_SALES] '" + trxDate + "','" + storeCode + "','" + nikCode + "','" + uniqueID + "','" + discCode + "','" + price +"'";
                    Log.d("QUERY", query);
                    Statement stmt = conn.createStatement();
                    int status = stmt.executeUpdate(query);
                    if (status != 0) {
                        msg = "Update Success";
                        success = true;
                    } else {
                        msg = "Update Failed";
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
            Toast.makeText(BDetailActivity.this, msg + "", Toast.LENGTH_SHORT).show();
            if (success == false) {
            } else {
                try {
                    b_detail_adapter = new BDetailAdapter(list_detail_B, BDetailActivity.this);
                    myRecyclerView.setAdapter(b_detail_adapter);
                } catch (Exception ex) {
                }
            }

        }
    }*/


    public void setData(BDetailClass model){
        tvArticle.setText(model.getArticle());
        tvPrice.setText(model.getPrice());
        etQty.setText(model.getQty());
        supportInvalidateOptionsMenu();
    }

    public void setDataEmpty(){
        tvArticle.setText("");
        tvPrice.setText("");
        etQty.setText("");
        supportInvalidateOptionsMenu();
    }

}
