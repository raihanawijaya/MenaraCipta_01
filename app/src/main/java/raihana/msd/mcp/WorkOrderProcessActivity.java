package raihana.msd.mcp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import raihana.msd.mcp.adapter.GDetailAdapter;
import raihana.msd.mcp.adapter.WorkOrderProcessAdapter;
import raihana.msd.mcp.connection.ConnectionClass;
import raihana.msd.mcp.model.EightColumnClass;
import raihana.msd.mcp.model.TwentyColumnClass;
import raihana.msd.mcp.utils.SharedPreference;

public class WorkOrderProcessActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView ivPrint,ivArahGulungan;
    private TextView tvMachineCode,tvWoNo,tvProdukName,tvArtwork,tvQtyOrder;
    private TextView tvUserCode, tvCompanyName,tvUserName;
    private RecyclerView myRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<TwentyColumnClass> MyRecordset = new ArrayList<>();
    private boolean success = false;
    private ConnectionClass connectionClass;
    private WorkOrderProcessAdapter MyAdapter;
    private SharedPreference sharedPreference;
    private String MachineCode,WoNo,s,s1,s2,QtyOrder,MyImage;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_order_process);
        sharedPreference = new SharedPreference(this);
        tvWoNo = findViewById(R.id.tv_wo_no);
        myRecyclerView = (RecyclerView) findViewById(R.id.rv_detail);
        myRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        myRecyclerView.setLayoutManager(mLayoutManager);
        MyRecordset = new ArrayList<>();
        connectionClass = new ConnectionClass();
        tvUserCode = findViewById(R.id.tv_user_code);
        tvUserName = findViewById(R.id.tv_user_name);
        tvQtyOrder = findViewById(R.id.tv_qty_order);
        tvMachineCode = findViewById(R.id.tv_machine_code);

        tvCompanyName = findViewById(R.id.tv_company_name);
        tvProdukName = findViewById(R.id.tv_produk_name);
        tvArtwork = findViewById(R.id.tv_Artwork);
        ivPrint = findViewById(R.id.iv_print);
        ivArahGulungan = findViewById(R.id.iv_arah_gulungan);

        //  username = sharedPreference.getObjectData("username", String.class);
        WoNo = (String) getIntent().getSerializableExtra("WO_NO");
        MachineCode = (String) getIntent().getSerializableExtra("MACHINE_CODE");
        QtyOrder = (String) getIntent().getSerializableExtra("QTY_ORDER");
        MyImage = (String) getIntent().getSerializableExtra("MY_IMAGE");

        tvUserCode.setText(sharedPreference.getObjectData("USER_CODE", String.class));
        tvUserName.setText(sharedPreference.getObjectData("USER_NAME", String.class));
        tvCompanyName.setText(sharedPreference.getObjectData("COMPANY_NAME", String.class));
        tvProdukName.setText(sharedPreference.getObjectData("PRODUK_NAME", String.class));
        tvArtwork.setText(sharedPreference.getObjectData("ARTWORK", String.class));

        swipeRefreshLayout = findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(this);
        tvMachineCode.setText(MachineCode);
        tvWoNo.setText(WoNo);
        tvQtyOrder.setText(QtyOrder);

        new SyncData().execute();

        try {
            String image = MyImage;
            byte[] b = new byte[image.length() / 2];
            //image diambil dari database dengan tipe data IMAGE
            for (int x = 0; x < b.length; x++) {
                int index = x * 2;
                int v = Integer.parseInt(image.substring(index, index + 2), 16);
                b[x] = (byte) v;
            }
            Log.d("decode", String.valueOf(b));
            //  BitmapFactory.Options options = new BitmapFactory.Options();
            Bitmap decodebitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            ivArahGulungan.setImageBitmap(decodebitmap);

        } catch (Exception ex) {

        }


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
         progress = ProgressDialog.show(WorkOrderProcessActivity.this,"Refresh Detail Order", "Mohon Tunggu Sebentar ...!", true);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection conn = connectionClass.CONN();
                if (conn == null) {
                    success = false;
                } else {
                    String query = "EXEC WIPXP_MNR.dbo.SP_ANDROID_LIST_PROC_NO_OF_WORK_ORDER '" + WoNo +"'";
                    Log.d("QUERY", query);
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    MyRecordset.clear();
                    if (rs != null) {
                        while (rs.next()) {
                            try {
                                MyRecordset.add(new TwentyColumnClass(rs.getString("COLUMN_1"),rs.getString("COLUMN_2"),rs.getString("COLUMN_3"),rs.getString("COLUMN_4"),rs.getString("COLUMN_5"),rs.getString("COLUMN_6"),rs.getString("COLUMN_7"),rs.getString("COLUMN_8"),rs.getString("COLUMN_9"),rs.getString("COLUMN_10"),rs.getString("COLUMN_11"),rs.getString("COLUMN_12"),rs.getString("COLUMN_13"),rs.getString("COLUMN_14"),rs.getString("COLUMN_15"),rs.getString("COLUMN_16"),rs.getString("COLUMN_17"),rs.getString("COLUMN_18"),"",""));
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
                    MyAdapter = new WorkOrderProcessAdapter(MyRecordset, WorkOrderProcessActivity.this);
                    myRecyclerView.setAdapter(MyAdapter);
                } catch (Exception ex) {
                }
            }
        }
    }


    public void onChildItemClicked(TwentyColumnClass data){


        Intent go_to_lotid = new Intent(getApplicationContext(),  WorkOrderProcessLotIdActivity.class);
        go_to_lotid.putExtra("MACHINE_CODE",MachineCode);
        go_to_lotid.putExtra("PROC_NO",data.getColumn_1());
        go_to_lotid.putExtra("PROC_NOTES",data.getColumn_2());
        go_to_lotid .putExtra("WO_NO",data.getColumn_18());
        startActivity(go_to_lotid);
    }

    }


