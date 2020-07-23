package raihana.msd.mcp;

import android.app.Activity;
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
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
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

import raihana.msd.mcp.adapter.WorkOrderProcessLotIdAdapter;
import raihana.msd.mcp.adapter.WorkOrderProcessLotIdPrintJobAdapter;
import raihana.msd.mcp.connection.ConnectionClass;
import raihana.msd.mcp.model.EightColumnClass;
import raihana.msd.mcp.model.TwentyColumnClass;
import raihana.msd.mcp.utils.SharedPreference;

public class WorkOrderProcessLotIdPrintJobActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView ivPrint;
    private Button btnMulaiPersiapan,btnCancelPersiapan,btnTroublePersiapan,btnSelesaiPersiapan;
    private Button btnMulaiSetting,btnCancelSetting,btnTroubleSetting,btnSelesaiSetting;
    private Button btnMulaiCetak,btnCancelCetak,btnTroubleCetak,btnSelesaiCetak,btnApprovalSetting;
    private TextView tvMachineCode,tvWoNo,tvProdukName,tvProcNotes,tvLotId,tvLotIdMtr,tvProcNo;
    private TextView tvUserCode, tvCompanyName,tvUserName;
    private RecyclerView myRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<EightColumnClass> MyRecordset = new ArrayList<>();
    private boolean success = false;
    private ConnectionClass connectionClass;
    private WorkOrderProcessLotIdPrintJobAdapter MyAdapter;
    private SharedPreference sharedPreference;
    private String WoNo,JobCode,s,s1,s2,ProcNo,ProcNotes,LotId,LotIdMtr,usercode,MachineCode;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_order_process_lotid_print_job);
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
        tvMachineCode = findViewById(R.id.tv_machine_code);
        tvWoNo = findViewById(R.id.tv_wo_no);
        tvProdukName = findViewById(R.id.tv_produk_name);
        tvProcNotes = findViewById(R.id.tv_proc_notes);
        tvProcNo = findViewById(R.id.tv_proc_no);
        tvLotId = findViewById(R.id.tv_lot_id);
        tvLotIdMtr = findViewById(R.id.tv_lot_id_mtr);

        ivPrint = findViewById(R.id.iv_print);

        btnMulaiPersiapan = findViewById(R.id.btn_mulai_persiapan);
        btnMulaiSetting = findViewById(R.id.btn_mulai_setting);
        btnMulaiCetak = findViewById(R.id.btn_mulai_cetak);

        btnCancelPersiapan = findViewById(R.id.btn_cancel_persiapan);
        btnCancelSetting = findViewById(R.id.btn_cancel_setting);
        btnCancelCetak = findViewById(R.id.btn_cancel_cetak);
        btnApprovalSetting = findViewById(R.id.btn_approved_setting);

        btnTroublePersiapan = findViewById(R.id.btn_trouble_persiapan);
        btnTroubleSetting = findViewById(R.id.btn_trouble_setting);
        btnTroubleCetak = findViewById(R.id.btn_trouble_cetak);

        btnSelesaiPersiapan = findViewById(R.id.btn_selesai_persiapan);
        btnSelesaiSetting = findViewById(R.id.btn_selesai_setting);
        btnSelesaiCetak = findViewById(R.id.btn_selesai_cetak);


        //  username = sharedPreference.getObjectData("username", String.class);
        MachineCode = (String) getIntent().getSerializableExtra("MACHINE_CODE");
        WoNo = (String) getIntent().getSerializableExtra("WO_NO");
        ProcNo = (String) getIntent().getSerializableExtra("PROC_NO");
        ProcNotes = (String) getIntent().getSerializableExtra("PROC_NOTES");
        LotId = (String) getIntent().getSerializableExtra("LOT_ID");
        LotIdMtr = (String) getIntent().getSerializableExtra("LOT_ID_MTR");

        tvUserCode.setText(sharedPreference.getObjectData("USER_CODE", String.class));
 //       tvUserName.setText(sharedPreference.getObjectData("USER_NAME", String.class));
 //       tvCompanyName.setText(sharedPreference.getObjectData("COMPANY_NAME", String.class));

//        usercode =  tvUserCode.getText().toString();
        tvWoNo.setText(WoNo);
        tvMachineCode.setText(MachineCode);

  //    tvProdukName.setText(sharedPreference.getObjectData("PRODUK_NAME", String.class));
        tvProcNotes.setText(ProcNotes);
        tvProcNo.setText(ProcNo);
        tvLotId.setText(LotId);
        tvLotIdMtr.setText(LotIdMtr);


        swipeRefreshLayout = findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(this);


        new CheckLastJobNo().execute();
        new SyncData().execute();

        btnMulaiPersiapan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(WorkOrderProcessLotIdPrintJobActivity.this)
                        .setTitle("Konfirmasi ?")
                        .setMessage("Mulai Proses Persiapan Kerja  ?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                JobCode = "1";
                                new InsertPrintJob().execute();

                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });

        btnCancelPersiapan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(WorkOrderProcessLotIdPrintJobActivity.this)
                        .setTitle("Konfirmasi ?")
                        .setMessage("Cancel Proses Persiapan Kerja  ?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                JobCode = "2";
                                new InsertPrintJob().execute();

                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });

        btnTroublePersiapan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(WorkOrderProcessLotIdPrintJobActivity.this)
                        .setTitle("Konfirmasi ?")
                        .setMessage("Ada Hambatan Persiapan Cetak ?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                JobCode = "3";
                             Intent go_to_intent = new Intent(getApplicationContext(),  WorkOrderProcessLotIdPrintTroubleActivity.class);
                                    go_to_intent.putExtra("MACHINE_CODE",tvMachineCode.getText().toString());
                                    go_to_intent.putExtra("WO_NO",tvWoNo.getText().toString());
                                    go_to_intent.putExtra("PROC_NO",tvProcNo.getText().toString());
                                    go_to_intent.putExtra("PROC_NOTES",tvProcNotes.getText().toString());
                                    go_to_intent.putExtra("LOT_ID",tvLotId.getText().toString());
                                    go_to_intent.putExtra("JOB_CODE",JobCode);
                                    go_to_intent.putExtra("JOB_NAME","Trouble Persiapan");
                                    startActivity(go_to_intent);
                             }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });
        btnSelesaiPersiapan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(WorkOrderProcessLotIdPrintJobActivity.this)
                        .setTitle("Konfirmasi ?")
                        .setMessage("Proses Persiapan Kerja SELESAI ?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                JobCode = "4";
                                new InsertPrintJob().execute();

                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });


        // ---------------------------------- Button Setting ----------------------
        btnMulaiSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(WorkOrderProcessLotIdPrintJobActivity.this)
                        .setTitle("Konfirmasi ?")
                        .setMessage("Proses Mulai Setting ?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                JobCode = "5";
                                new InsertPrintJob().execute();

                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });

        btnCancelSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(WorkOrderProcessLotIdPrintJobActivity.this)
                        .setTitle("Konfirmasi ?")
                        .setMessage("Cancel Proses Setting ?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                JobCode = "6";
                                new InsertPrintJob().execute();
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });

        btnTroubleSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(WorkOrderProcessLotIdPrintJobActivity.this)
                        .setTitle("Konfirmasi ?")
                        .setMessage("Ada Hambatan Proses Setting ?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                JobCode = "7";
                                Intent go_to_intent = new Intent(getApplicationContext(),  WorkOrderProcessLotIdPrintTroubleActivity.class);
                                go_to_intent.putExtra("WO_NO",tvWoNo.getText().toString());
                                go_to_intent.putExtra("PROC_NO",tvProcNo.getText().toString());
                                go_to_intent.putExtra("PROC_NOTES",tvProcNotes.getText().toString());
                                go_to_intent.putExtra("LOT_ID",tvLotId.getText().toString());
                                go_to_intent.putExtra("JOB_CODE",JobCode);
                                startActivity(go_to_intent);

                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });

        btnApprovalSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(WorkOrderProcessLotIdPrintJobActivity.this)
                        .setTitle("Konfirmasi ?")
                        .setMessage("Approval QC Setting ?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                JobCode = "8";
                                new InsertPrintJob().execute();

                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });
        btnSelesaiSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(WorkOrderProcessLotIdPrintJobActivity.this)
                        .setTitle("Konfirmasi ?")
                        .setMessage("Proses Setting Selesai ?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                JobCode = "9";
                                new InsertPrintJob().execute();

                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });

        // --------------------------- Button Cetak -------------
        btnMulaiCetak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(WorkOrderProcessLotIdPrintJobActivity.this)
                        .setTitle("Konfirmasi ?")
                        .setMessage("Proses Cetak Dimulai ?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                JobCode = "10";
                                new InsertPrintJob().execute();

                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });

        btnCancelCetak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(WorkOrderProcessLotIdPrintJobActivity.this)
                        .setTitle("Konfirmasi ?")
                        .setMessage("Cancel Proses Cetak ?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                JobCode = "11";
                                new InsertPrintJob().execute();

                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });

        btnTroubleCetak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(WorkOrderProcessLotIdPrintJobActivity.this)
                        .setTitle("Konfirmasi ?")
                        .setMessage("Ada Hambatan Proses Cetak ?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                JobCode = "12";
                                Intent go_to_intent = new Intent(getApplicationContext(),  WorkOrderProcessLotIdPrintTroubleActivity.class);
                                go_to_intent.putExtra("WO_NO",tvWoNo.getText().toString());
                                go_to_intent.putExtra("PROC_NO",tvProcNo.getText().toString());
                                go_to_intent.putExtra("PROC_NOTES",tvProcNotes.getText().toString());
                                go_to_intent.putExtra("LOT_ID",tvLotId.getText().toString());
                                go_to_intent.putExtra("JOB_CODE",JobCode);
                                startActivity(go_to_intent);
                            }
                        })
                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });

        btnSelesaiCetak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(WorkOrderProcessLotIdPrintJobActivity.this)
                        .setTitle("Konfirmasi ?")
                        .setMessage("Proses Cetak Selesai ?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                JobCode = "13";
                                new InsertPrintJob().execute();
                                Intent go_to_intent = new Intent(getApplicationContext(),  WorkOrderProcessLotIdRollIdActivity.class);
                                go_to_intent.putExtra("WO_NO",tvWoNo.getText().toString());
                                go_to_intent.putExtra("PROC_NO",tvProcNo.getText().toString());
                                go_to_intent.putExtra("LOT_ID",tvLotId.getText().toString());
                                go_to_intent.putExtra("LOT_ID_MTR",tvLotIdMtr.getText().toString());

                                go_to_intent.putExtra("JOB_CODE",JobCode);
                                startActivity(go_to_intent);
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


    @Override
    public void onResume() {
        super.onResume();
        SyncData syncData = new SyncData();// this is the Asynctask, which is used to process in background to reduce load on app process
        syncData.execute("");
    }
    private class SyncData extends AsyncTask<String, String, String> {
        String msg = "Internet/DB_Credentials/Windows_FireWall_TurnOn Error, See Android Monitor in the bottom For details";
        ProgressDialog progress;

        @Override
        protected void onPreExecute() {
         progress = ProgressDialog.show(WorkOrderProcessLotIdPrintJobActivity.this,"Querying", "Please Wait...!", true);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection conn = connectionClass.CONN();
                if (conn == null) {
                    success = false;
                } else {
                    String query = "EXEC WIPXP_MNR.dbo.SP_ANDROID_LIST_WO_PROC_NO_LOT_ID_PRINT_JOB '" + WoNo +"','" + ProcNo + "','" + LotId + "'";
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
                    MyAdapter = new WorkOrderProcessLotIdPrintJobAdapter(MyRecordset, WorkOrderProcessLotIdPrintJobActivity.this);
                    myRecyclerView.setAdapter(MyAdapter);
                } catch (Exception ex) {
                }
            }
        }
    }

    private class InsertPrintJob extends AsyncTask<String, String, String> {
        String msg = "Internet/DB_Credentials/Windows_FireWall_TurnOn Error, See Android Monitor in the bottom For details";
        ProgressDialog progress;

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
                    String query = "EXEC WIPXP_MNR.dbo.[SP_ANDROID_INSERT_LOT_ID_PROCESS_PRINT_JOB] '" + WoNo +"','" + ProcNo + "','" + LotId + "','" + JobCode + "','" + usercode + "'";
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
                    new SyncData().execute();

                    if (JobCode.equals("1")) {
                        btnMulaiPersiapan.setEnabled(false);
                        btnCancelPersiapan.setEnabled(true);
                        btnTroublePersiapan.setEnabled(true);
                        btnSelesaiPersiapan.setEnabled(true);
                    }
                    if (JobCode.equals("4")) {
                        btnMulaiPersiapan.setEnabled(false);
                        btnCancelPersiapan.setEnabled(false);
                        btnTroublePersiapan.setEnabled(false);
                        btnSelesaiPersiapan.setEnabled(false);

                        btnCancelSetting.setEnabled(true);
                        btnTroubleSetting.setEnabled(true);
                        btnApprovalSetting.setEnabled(true);
                        btnSelesaiSetting.setEnabled(true);

                    }

                    if (JobCode.equals("5")) {
                        btnMulaiPersiapan.setEnabled(false);
                        btnCancelPersiapan.setEnabled(false);
                        btnTroublePersiapan.setEnabled(false);
                        btnSelesaiPersiapan.setEnabled(false);

                        btnCancelSetting.setEnabled(true);
                        btnTroubleSetting.setEnabled(true);
                        btnApprovalSetting.setEnabled(true);
                        btnSelesaiSetting.setEnabled(true);

                    }

                    if (JobCode.equals("9")) {
                        btnMulaiCetak.setEnabled(false);
                        btnCancelCetak.setEnabled(true);
                        btnTroubleCetak.setEnabled(true);
                        btnSelesaiCetak.setEnabled(true);
                   }



                    if (JobCode.equals("12")) {
                        btnMulaiCetak.setEnabled(false);
                        btnCancelCetak.setEnabled(true);
                        btnTroubleCetak.setEnabled(true);
                        btnSelesaiCetak.setEnabled(false);

                        btnCancelSetting.setEnabled(false);
                        btnTroubleSetting.setEnabled(false);
                        btnApprovalSetting.setEnabled(false);
                        btnSelesaiSetting.setEnabled(false);

                    }

                    if (JobCode.equals("23")) {
                        btnTroubleCetak.setEnabled(true);
                        btnSelesaiCetak.setEnabled(true);
                    }

                } catch (Exception ex) {

               }
            }

        }
    }

    private class CheckLastJobNo extends AsyncTask<String, String, String> {
        String msg = "Internet/DB_Credentials/Windows_FireWall_TurnOn Error, See Android Monitor in the bottom For details";
        ProgressDialog progress;
        String LastJobNo;
        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(WorkOrderProcessLotIdPrintJobActivity.this,"Querying", "Please Wait...!", true);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection conn = connectionClass.CONN();
                if (conn == null) {
                    success = false;
                } else {
                    String query = "EXEC WIPXP_MNR.dbo.SP_ANDROID_GET_LAST_PROCESS_PRINT_JOB_NO '" + WoNo +"','" + ProcNo + "','" + LotId + "'";
                    Log.d("QUERY", query);
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    MyRecordset.clear();
                    if (rs != null) {
                        while (rs.next()) {
                            try {
                                LastJobNo = rs.getString("JOB_NO");
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
                   if (LastJobNo.equals("1")||(LastJobNo.equals("2"))||(LastJobNo.equals("3"))||(LastJobNo.equals("21"))) {
                       btnMulaiPersiapan.setEnabled(false);
                       btnCancelPersiapan.setEnabled(true);
                       btnTroublePersiapan.setEnabled(true);
                       btnSelesaiPersiapan.setEnabled(true);
                   }

                    if (LastJobNo.equals("7")||(LastJobNo.equals("22"))) {
                        btnMulaiCetak.setEnabled(false);
                        btnTroubleSetting.setEnabled(true);
                        btnApprovalSetting.setEnabled(true);
                        btnSelesaiSetting.setEnabled(true);
                    }

                      if (LastJobNo.equals("5")){
                        btnMulaiPersiapan.setEnabled(false);
                        btnMulaiSetting.setEnabled(false);
                        btnCancelSetting.setEnabled(true);
                        btnApprovalSetting.setEnabled(true);
                        btnTroubleSetting.setEnabled(true);
                        btnSelesaiSetting.setEnabled(true);
                    }

                    if (LastJobNo.equals("10")){
                        btnCancelSetting.setEnabled(true);
                        btnApprovalSetting.setEnabled(true);
                        btnTroubleSetting.setEnabled(true);
                        btnSelesaiSetting.setEnabled(true);

                        btnMulaiPersiapan.setEnabled(false);
                        btnMulaiCetak.setEnabled(false);
                        btnCancelCetak.setEnabled(true);
                        btnTroubleCetak.setEnabled(true);
                        btnSelesaiCetak.setEnabled(true);
                    }

                    if (LastJobNo.equals("13")){
                        btnMulaiPersiapan.setEnabled(false);
                    }

                    if (LastJobNo.equals("12")){
                        btnMulaiPersiapan.setEnabled(false);
                        btnTroubleCetak.setEnabled(true);
                        btnSelesaiCetak.setEnabled(false);

                        btnCancelSetting.setEnabled(false);
                        btnTroubleSetting.setEnabled(false);
                        btnApprovalSetting.setEnabled(false);
                        btnSelesaiSetting.setEnabled(false);
                  }

                    if (LastJobNo.equals("23")) {
                        btnTroubleCetak.setEnabled(true);
                        btnSelesaiCetak.setEnabled(true);
                    }


                } catch (Exception ex) {
                }
            }
        }
    }

    public void onChildItemClicked(EightColumnClass data){

        if (data.getColumn_1().equals("13")){
            Intent go_to_intent = new Intent(getApplicationContext(),  WorkOrderProcessLotIdRollIdActivity.class);
            go_to_intent.putExtra("WO_NO",tvWoNo.getText().toString());
            go_to_intent.putExtra("PROC_NO",tvProcNo.getText().toString());
            go_to_intent.putExtra("LOT_ID",tvLotId.getText().toString());
            go_to_intent.putExtra("LOT_ID_MTR",tvLotIdMtr.getText().toString());

            go_to_intent.putExtra("JOB_CODE","13");
            startActivity(go_to_intent);
        }

        if (data.getColumn_1().equals("3")||data.getColumn_1().equals("7")||data.getColumn_1().equals("12")){
            Intent go_to_intent = new Intent(getApplicationContext(),  WorkOrderProcessLotIdPrintSolvedTroubleActivity.class);

            String Job_Code = null;

            if (data.getColumn_1().equals("3")) {
              Job_Code = "21";
            }


            if (data.getColumn_1().equals("7")) {
                Job_Code = "22";
            }

            if (data.getColumn_1().equals("12")) {
                Job_Code = "23";
            }
            go_to_intent.putExtra("WO_NO",tvWoNo.getText().toString());
            go_to_intent.putExtra("PROC_NO",tvProcNo.getText().toString());
            go_to_intent.putExtra("LOT_ID",tvLotId.getText().toString());
            go_to_intent.putExtra("LOT_ID_MTR",tvLotIdMtr.getText().toString());
            go_to_intent.putExtra("JOB_CODE",Job_Code);  //-->
            go_to_intent.putExtra("PROBLEM_ID",data.getColumn_4());
            go_to_intent.putExtra("PROBLEM_NOTES",data.getColumn_5());
            go_to_intent.putExtra("PROBLEM_DATE",data.getColumn_3());
            go_to_intent.putExtra("PROBLEM_ROWID",data.getColumn_8());
            go_to_intent.putExtra("JOB_NAME",data.getColumn_2());
            startActivity(go_to_intent);
        }


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
