package raihana.msd.mcp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
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
import java.util.List;

import javax.crypto.Mac;

import raihana.msd.mcp.adapter.TroubleListAdapter;
import raihana.msd.mcp.adapter.WorkOrderProcessLotIdAdapter;
import raihana.msd.mcp.connection.ConnectionClass;
import raihana.msd.mcp.model.EightColumnClass;
import raihana.msd.mcp.utils.SharedPreference;

public class WorkOrderProcessLotIdPrintTroubleActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView ivPrint,ivArahGulungan;
    private EditText etProblemNotes,etKeterangan;
    private Button btnCatatHambatan;
    private TextView tvWoNo,tvProcNotes,tvProcNo,tvLotId,tvJobName,tvMachineCode;
    private TextView tvUserCode, tvCompanyName,tvUserName;
    private RecyclerView myRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<EightColumnClass> MyRecordset = new ArrayList<>();
    private boolean success = false;
    private ConnectionClass connectionClass;
    private TroubleListAdapter MyAdapter;
    private SharedPreference sharedPreference;
    private String WoNo,ProcNo,s,s1,s2,usercode,arahgulungan,LotId,ProblemId,JobCode,JobName,ProcNotes,MachineCode;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_order_process_lotid_print_trouble);
        sharedPreference = new SharedPreference(this);

        myRecyclerView = (RecyclerView) findViewById(R.id.rv_detail);
        myRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        myRecyclerView.setLayoutManager(mLayoutManager);
        MyRecordset = new ArrayList<>();
        connectionClass = new ConnectionClass();
        tvUserCode = findViewById(R.id.tv_user_code);
        tvUserName = findViewById(R.id.tv_user_name);
        tvCompanyName = findViewById(R.id.tv_company_name);

        tvWoNo = findViewById(R.id.tv_wo_no);
        tvMachineCode = findViewById(R.id.tv_machine_code);
        tvProcNo = findViewById(R.id.tv_proc_no);
        tvProcNotes = findViewById(R.id.tv_proc_notes);
        tvLotId = findViewById(R.id.tv_lot_id);
        etKeterangan = findViewById(R.id.et_keterangan);
        tvJobName = findViewById(R.id.tv_job_name);
        etProblemNotes = findViewById(R.id.et_problem_notes);

        btnCatatHambatan = findViewById(R.id.btn_catat_hambatan);
        ivArahGulungan = findViewById(R.id.iv_arah_gulungan);

        //  username = sharedPreference.getObjectData("username", String.class);
        MachineCode = (String) getIntent().getSerializableExtra("MACHINE_CODE");
        WoNo = (String) getIntent().getSerializableExtra("WO_NO");
        ProcNo = (String) getIntent().getSerializableExtra("PROC_NO");
        ProcNotes = (String) getIntent().getSerializableExtra("PROC_NOTES");
        LotId =  (String) getIntent().getSerializableExtra("LOT_ID");
        JobCode = (String) getIntent().getSerializableExtra("JOB_CODE");
        JobName = (String) getIntent().getSerializableExtra("JOB_NAME");
        tvUserCode.setText(sharedPreference.getObjectData("USER_CODE", String.class));
        tvUserName.setText(sharedPreference.getObjectData("USER_NAME", String.class));
        tvCompanyName.setText(sharedPreference.getObjectData("COMPANY_NAME", String.class));
        arahgulungan = sharedPreference.getObjectData("ARAH_GULUNGAN", String.class);
        usercode =  tvUserCode.getText().toString();

        tvMachineCode.setText(MachineCode);
        tvWoNo.setText(WoNo);
        tvProcNo.setText(ProcNo);
        tvProcNotes.setText(ProcNotes);
        tvLotId.setText(LotId);
        tvJobName.setText(JobName);
        swipeRefreshLayout = findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(this);

 //       new ShowImageArahGulungan().execute();
        new SyncData().execute();

        btnCatatHambatan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvLotId.getText().toString().equals("")) {
                    return;
                }

                new AlertDialog.Builder(WorkOrderProcessLotIdPrintTroubleActivity.this)
                        .setTitle("Konfirmasi ?")
                        .setMessage("Simpan Catatan Hambatan Tersebut ?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                new InsertPrintJobHambatan().execute();
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .show();
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

        @Override
        protected void onPreExecute() {
         progress = ProgressDialog.show(WorkOrderProcessLotIdPrintTroubleActivity.this,"Refresh...", "Mohon Tunggu Sebentar ...!", true);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection conn = connectionClass.CONN();
                if (conn == null) {
                    success = false;
                } else {
                    String query = "EXEC WIPXP_MNR.dbo.SP_ANDROID_LIST_PRINT_TROUBLE ";
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
                    MyAdapter = new TroubleListAdapter(MyRecordset, WorkOrderProcessLotIdPrintTroubleActivity.this);
                    myRecyclerView.setAdapter(MyAdapter);
                } catch (Exception ex) {
                }
            }
        }
    }

    private class ShowImageArahGulungan extends AsyncTask<String, String, String> {
        String msg = "Internet/DB_Credentials/Windows_FireWall_TurnOn Error, See Android Monitor in the bottom For details";
        ProgressDialog progress;
        String myImage;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(WorkOrderProcessLotIdPrintTroubleActivity.this,"Refresh...", "Mohon Tunggu Sebentar ...!", true);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection conn = connectionClass.CONN();
                if (conn == null) {
                    success = false;
                } else {
                    String query = "SELECT [IMAGE] FROM WIPXP_MNR.dbo.MASTER_ARAH_GULUNGAN WHERE ARAH_GULUNGAN = '"+ arahgulungan + "'";
                    Log.d("QUERY", query);
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    MyRecordset.clear();
                    if (rs != null) {
                        while (rs.next()) {
                            try {
                                myImage = rs.getString("IMAGE");
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
                    String image = myImage;
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
        }
    }

    private class InsertPrintJobHambatan extends AsyncTask<String, String, String> {
        String msg = "Internet/DB_Credentials/Windows_FireWall_TurnOn Error, See Android Monitor in the bottom For details";
        ProgressDialog progress;
        String Keterangan = etKeterangan.getText().toString();

        @Override
        protected void onPreExecute() {
            //  progress = ProgressDialog.show(WorkOrderProcessLotIdPrintJobActivity.this,"Inserting...", "Simpan Data , Harap Tunggu !", true);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection conn = connectionClass.CONN();
                if (conn == null) {
                    success = false;
                } else {
                    String query = "EXEC WIPXP_MNR.dbo.[SP_ANDROID_INSERT_LOT_ID_PROCESS_PRINT_JOB_TROUBLE] '" + WoNo +"','" + ProcNo + "','" + LotId + "','" + JobCode + "','" + ProblemId + "','" + Keterangan + "','" + usercode + "'";
                    Log.d("QUERY", query);
                    Statement stmt = conn.createStatement();
                    int status = stmt.executeUpdate(query);
                    if (status != 0) {
                        msg =  " Input Data Berhasil !";
                        success = true;
                    } else {
                        msg = "Input Data Gagal !";
                        success = false;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Writer writer = new StringWriter();
                e.printStackTrace(new PrintWriter(writer));
                msg= writer.toString();
                success = false;

            }
            return msg;
        }

        @Override
        protected void onPostExecute(String msg) {
            //    progress.dismiss();
            if (success == false) {
                Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
            } else {
                try {

                    Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                    finish();

                } catch (Exception ex) {

                }
            }

        }
    }

    public void onChildItemClicked(EightColumnClass Data){
      //  Intent go_to_intent = new Intent(getApplicationContext(),  WorkOrderProcessLotIdPrintJobActivity.class);
     //   go_to_intent.putExtra("WO_NO",Data.getColumn_1());
     //   go_to_intent.putExtra("PROC_NO",Data.getColumn_2());
     //   go_to_intent.putExtra("LOT_ID",Data.getColumn_3());
     //   go_to_intent.putExtra("LOT_ID_MTR",Data.getColumn_4());
    ProblemId =  Data.getColumn_2();
    etProblemNotes.setText(Data.getColumn_1());
        Log.d("OnClicked : ", ProblemId);
        //go_to_order.putExtra("PRODUK_NAME",model.getColumn_2());
        //     go_to_order.putExtra("ARTWORK",model.getColumn_5());
//    //    sharedPreference.storeData("PRODUK_NAME",model.getColumn_2());
        //    sharedPreference.storeData("ARTWORK",model.getColumn_5());
        //go_to_order.putExtra("WO_NO",model.getColumn_1());
     //   startActivity(go_to_intent);
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
