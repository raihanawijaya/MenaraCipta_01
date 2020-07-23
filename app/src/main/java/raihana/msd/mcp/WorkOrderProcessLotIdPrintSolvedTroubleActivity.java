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
import java.sql.RowId;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import raihana.msd.mcp.adapter.TroubleListAdapter;
import raihana.msd.mcp.connection.ConnectionClass;
import raihana.msd.mcp.model.EightColumnClass;
import raihana.msd.mcp.utils.SharedPreference;

public class WorkOrderProcessLotIdPrintSolvedTroubleActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView ivPrint,ivArahGulungan;
    private EditText etProblemSolveNotes,etProblemSolvedBy,etProblemSolvedTime,etProblemNotes;
    private Button btnSolvedHambatan;
    private TextView tvWoNo,tvProcNo,tvLotId,tvlotIdMtr,tvUserCode,tvCompanyName;
    private TextView tvProblemId,tvProblemNotes,tvProblemDate,tvUserName,tvJobName,tvProblemRowid;
    private RecyclerView myRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<EightColumnClass> MyRecordset = new ArrayList<>();
    private boolean success = false;
    private ConnectionClass connectionClass;
    private TroubleListAdapter MyAdapter;
    private SharedPreference sharedPreference;
    private String WoNo,ProcNo,usercode,LotId,JobCode,LotIdMtr;
    private String ProblemId,ProblemNotes,ProblemDate,ProblemRowid,JobName;
    private String SolveNotes,SolvedBy,SolvedTime;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_order_process_lotid_print_solved_trouble);
        sharedPreference = new SharedPreference(this);
        tvWoNo = findViewById(R.id.tv_wo_no);
        myRecyclerView = (RecyclerView) findViewById(R.id.rv_detail);
//        myRecyclerView.setHasFixedSize(true);
//        mLayoutManager = new LinearLayoutManager(this);
//        myRecyclerView.setLayoutManager(mLayoutManager);
        MyRecordset = new ArrayList<>();
        connectionClass = new ConnectionClass();
        tvUserCode = findViewById(R.id.tv_user_code);
        tvUserName = findViewById(R.id.tv_user_name);
        tvCompanyName = findViewById(R.id.tv_company_name);
        tvProcNo = findViewById(R.id.tv_proc_no);
        tvLotId = findViewById(R.id.tv_lot_id);
        tvlotIdMtr = findViewById(R.id.tv_lot_id_mtr);
        tvJobName = findViewById(R.id.tv_job_name);;

        tvProblemRowid = findViewById(R.id.tv_problem_rowid);
        tvProblemNotes = findViewById(R.id.tv_problem_notes);
        tvProblemDate = findViewById(R.id.tv_problem_date);
        tvProblemId = findViewById(R.id.tv_problem_id);

        etProblemSolveNotes = findViewById(R.id.et_problem_solve_notes);
        etProblemSolvedBy = findViewById(R.id.tv_problem_solved_by);
        etProblemSolvedTime = findViewById(R.id.et_problem_solved_time);

        btnSolvedHambatan = findViewById(R.id.btn_solved_hambatan);
        ivArahGulungan = findViewById(R.id.iv_arah_gulungan);

        // ------> Get Data Intent...
        WoNo = (String) getIntent().getSerializableExtra("WO_NO");
        ProcNo = (String) getIntent().getSerializableExtra("PROC_NO");
        LotId =  (String) getIntent().getSerializableExtra("LOT_ID");
        LotIdMtr = (String) getIntent().getSerializableExtra("LOT_ID_MTR");
        JobCode = (String) getIntent().getSerializableExtra("JOB_CODE");
        ProblemId = (String) getIntent().getSerializableExtra("PROBLEM_ID");
        ProblemNotes = (String) getIntent().getSerializableExtra("PROBLEM_NOTES");
        ProblemDate = (String) getIntent().getSerializableExtra("PROBLEM_DATE");
        ProblemRowid = (String) getIntent().getSerializableExtra("PROBLEM_ROWID");
        JobName = (String) getIntent().getSerializableExtra("JOB_NAME");

        tvUserCode.setText(sharedPreference.getObjectData("USER_CODE", String.class));
        tvUserName.setText(sharedPreference.getObjectData("USER_NAME", String.class));
        tvCompanyName.setText(sharedPreference.getObjectData("COMPANY_NAME", String.class));
        usercode =  tvUserCode.getText().toString();

        tvWoNo.setText(WoNo);
        tvProcNo.setText(ProcNo);
        tvLotId.setText(LotId);
        tvlotIdMtr.setText(LotIdMtr);

        tvJobName.setText(JobName);
        tvProblemRowid.setText(ProblemRowid);
        tvProblemId.setText(ProblemId);
        tvProblemNotes.setText(ProblemNotes);
        tvProblemDate.setText(ProblemDate);

//        swipeRefreshLayout = findViewById(R.id.swipe);
//        swipeRefreshLayout.setOnRefreshListener(this);

        new SyncData().execute();
        new GetSolvedData().execute();

        btnSolvedHambatan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvLotId.getText().toString().equals("")) {
                    return;
                }

                new AlertDialog.Builder(WorkOrderProcessLotIdPrintSolvedTroubleActivity.this)
                        .setTitle("Beres ?")
                        .setMessage("Hambatan Tersebut Sudah Diselesaikan ?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                new InsertPrintJobSolvedHambatan().execute();
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
         progress = ProgressDialog.show(WorkOrderProcessLotIdPrintSolvedTroubleActivity.this,"Refresh...", "Mohon Tunggu Sebentar ...!", true);
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
//            swipeRefreshLayout.setRefreshing(false);

            if (success == false) {

            } else {
                try {
               //     MyAdapter = new TroubleListAdapter(MyRecordset, WorkOrderProcessLotIdPrintSolvedTroubleActivity.this);
               //     myRecyclerView.setAdapter(MyAdapter);
                } catch (Exception ex) {
                }
            }
        }
    }

    private class GetSolvedData extends AsyncTask<String, String, String> {
        String msg = "Internet/DB_Credentials/Windows_FireWall_TurnOn Error, See Android Monitor in the bottom For details";
        ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(WorkOrderProcessLotIdPrintSolvedTroubleActivity.this,"Refresh...", "Mohon Tunggu Sebentar ...!", true);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection conn = connectionClass.CONN();
                if (conn == null) {
                    success = false;
                } else {
                    String query = "SELECT KETERANGAN,SOLVED_BY,FORMAT(SOLVED_TIME,'dd-MMM  hh:mm') SOLVED_TIME FROM  WIPXP_MNR.dbo.[TRX_WIP_PROSES_CETAK_LOT_ID_JOB] \n" +
                            "WHERE SOLVED_ROWID = '" + ProblemRowid + "'";
                    Log.d("QUERY", query);
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    MyRecordset.clear();
                    if (rs != null) {
                        while (rs.next()) {
                            try {
                                SolveNotes = rs.getString("KETERANGAN");
                                SolvedBy = rs.getString("SOLVED_BY");
                                SolvedTime = rs.getString("SOLVED_TIME");

                //                MyRecordset.add(new EightColumnClass(rs.getString("COLUMN_1"),rs.getString("COLUMN_2"),rs.getString("COLUMN_3"),rs.getString("COLUMN_4"),rs.getString("COLUMN_5"),rs.getString("COLUMN_6"),rs.getString("COLUMN_7"),rs.getString("COLUMN_8")));
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
//            swipeRefreshLayout.setRefreshing(false);

            if (success == false) {

            } else {
                try {
                   etProblemSolveNotes.setText(SolveNotes);
                   etProblemSolvedBy.setText(SolvedBy);
                   etProblemSolvedTime.setText(SolvedTime);

                } catch (Exception ex) {
                }
            }
        }
    }

    private class InsertPrintJobSolvedHambatan extends AsyncTask<String, String, String> {
        String msg = "Internet/DB_Credentials/Windows_FireWall_TurnOn Error, See Android Monitor in the bottom For details";
        ProgressDialog progress;
        String SolveNotes = etProblemSolveNotes.getText().toString();
        String SolvedBy = etProblemSolvedBy.getText().toString();

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
                    String query = "EXEC WIPXP_MNR.dbo.[SP_ANDROID_INSERT_LOT_ID_PROCESS_PRINT_JOB_TROUBLE_SOLVED] '" + WoNo +"','" + ProcNo + "','" + LotId + "','" + JobCode + "','" + ProblemId + "','" + SolveNotes + "','" + ProblemRowid + "','1','" + usercode + "','" + SolvedBy + "'";
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
                 //   finish();

                } catch (Exception ex) {

                }
            }

        }
    }

    public void onChildItemClicked(EightColumnClass Data){
    ProblemId =  Data.getColumn_2();
    etProblemNotes.setText(Data.getColumn_1());
     }





}
