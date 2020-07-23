package raihana.msd.mcp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

import raihana.msd.mcp.adapter.WorkOrderForMachineCodeAdapter;
import raihana.msd.mcp.adapter.WorkOrderProcessAdapter;
import raihana.msd.mcp.connection.ConnectionClass;
import raihana.msd.mcp.model.EightColumnClass;
import raihana.msd.mcp.model.TwentyColumnClass;
import raihana.msd.mcp.utils.SharedPreference;

public class WorkOrderForMachineCodeActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView ivPrint;
    private TextView tvMachineCode;
    private TextView tvUserCode, tvCompanyName,tvUserName;
    private RecyclerView myRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<EightColumnClass> MyRecordset = new ArrayList<>();
    private boolean success = false;
    private ConnectionClass connectionClass;
    private WorkOrderForMachineCodeAdapter MyAdapter;
    private SharedPreference sharedPreference;
    private String MachineCode;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_order_for_machine_code);
        sharedPreference = new SharedPreference(this);
        tvMachineCode = findViewById(R.id.tv_machine_code);
        myRecyclerView = (RecyclerView) findViewById(R.id.rv_detail);
        myRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        myRecyclerView.setLayoutManager(mLayoutManager);
        MyRecordset = new ArrayList<>();
        connectionClass = new ConnectionClass();
        tvUserCode = findViewById(R.id.tv_user_code);
        tvUserName = findViewById(R.id.tv_user_name);

        tvCompanyName = findViewById(R.id.tv_company_name);

        ivPrint = findViewById(R.id.iv_print);

        //  username = sharedPreference.getObjectData("username", String.class);
        MachineCode = (String) getIntent().getSerializableExtra("MACHINE_CODE");
        tvUserCode.setText(sharedPreference.getObjectData("USER_CODE", String.class));
        tvUserName.setText(sharedPreference.getObjectData("USER_NAME", String.class));
        tvCompanyName.setText(sharedPreference.getObjectData("COMPANY_NAME", String.class));

        swipeRefreshLayout = findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(this);
        tvMachineCode.setText(MachineCode);
//        tvQtyOrder.setText(QtyOrder);

        new SyncData().execute();
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
         progress = ProgressDialog.show(WorkOrderForMachineCodeActivity.this,"Refresh Detail Order", "Mohon Tunggu Sebentar ...!", true);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection conn = connectionClass.CONN();
                if (conn == null) {
                    success = false;
                } else {
                    String query = "EXEC WIPXP_MNR.dbo.SP_ANDROID_LIST_WO_NO_FOR_MACHINE_CODE '" + MachineCode + "'";
                    Log.d("QUERY", query);
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    MyRecordset.clear();
                    if (rs != null) {
                        while (rs.next()) {
                            try {
                                MyRecordset.add(new EightColumnClass(rs.getString("COLUMN_1"),rs.getString("COLUMN_2"),rs.getString("COLUMN_3"),rs.getString("COLUMN_4"),rs.getString("COLUMN_5"),rs.getString("COLUMN_6"),rs.getString("COLUMN_7"),rs.getString("COLUMN_8")));
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
                    MyAdapter = new WorkOrderForMachineCodeAdapter(MyRecordset, WorkOrderForMachineCodeActivity.this);
                    myRecyclerView.setAdapter(MyAdapter);
                } catch (Exception ex) {
                }
            }
        }
    }


    public void onChildItemClicked(final EightColumnClass model){
        new AlertDialog.Builder(WorkOrderForMachineCodeActivity.this)
                .setTitle("WO# " +  model.getColumn_1()+ "...")
                .setMessage("Anda Ingin Buka Work Order   " +  model.getColumn_1()+ " ?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent go_to_order = new Intent(WorkOrderForMachineCodeActivity.this,  WorkOrderProcessActivity.class);

                        go_to_order.putExtra("MACHINE_CODE",MachineCode);
                        go_to_order.putExtra("MY_IMAGE",model.getColumn_6());
                        go_to_order.putExtra("WO_NO",model.getColumn_1());
                        go_to_order.putExtra("QTY_ORDER",model.getColumn_4());
                        //     go_to_order.putExtra("ARTWORK",model.getColumn_5());

                        sharedPreference.storeData("PRODUK_NAME",model.getColumn_2());
                        sharedPreference.storeData("ARTWORK",model.getColumn_5());
                        sharedPreference.storeData("ARAH_GULUNGAN",model.getColumn_6());
                        //go_to_order.putExtra("WO_NO",model.getColumn_1());

                        startActivity(go_to_order);
                    }
                })
                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .show();
        //Intent go_to_lotid = new Intent(getApplicationContext(),  WorkOrderProcessLotIdActivity.class);
        //go_to_lotid.putExtra("PROC_NO",data.getColumn_1());
        //go_to_lotid .putExtra("WO_NO",data.getColumn_18());
       // startActivity(go_to_lotid);
    }

    }


