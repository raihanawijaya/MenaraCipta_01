package raihana.msd.mcp.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import raihana.msd.mcp.OrderActivity;
import raihana.msd.mcp.R;
import raihana.msd.mcp.WorkOrderProcessActivity;
import raihana.msd.mcp.adapter.EAdapter;
import raihana.msd.mcp.connection.ConnectionClass;
import raihana.msd.mcp.model.EightColumnClass;
import raihana.msd.mcp.utils.SharedPreference;

//import raihana.msd.rgl.adapter.EAdapter;


public class EFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    //1. Declaration: View, RecyclerView, List<>,
    View v;
    private RecyclerView myRecyclerView;
    private List<EightColumnClass> MyRecordset = new ArrayList<>();
    private boolean success = false;
    private ConnectionClass connectionClass;
    private EAdapter MyAdapter;
    private SyncData orderData = new SyncData();
    private String usercode;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SharedPreference sharedPreference;
    private String startDate, endDate, storeCode,restoid;
    private LinearLayout lay_e_add;

    public void sync() {
        try{
            new SyncData().execute();
        }catch (Exception e){
            Log.e(EFragment.class.getSimpleName(), "sync: ",e );
        }
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //2. Return view
        v = inflater.inflate(R.layout.fragment_e,container,false);
        sharedPreference = new SharedPreference(getContext());
        connectionClass = new ConnectionClass();
        myRecyclerView = v.findViewById(R.id.rv_fragment_e);
        MyAdapter = new EAdapter(getContext(),EFragment.this);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        myRecyclerView.setAdapter(MyAdapter);
        swipeRefreshLayout = v.findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(this);

        usercode = sharedPreference.getObjectData("USER_CODE", String.class);
        restoid = sharedPreference.getObjectData("RESTO_ID", String.class);


        return v;
        //5. Make the RV


    }

    private class SyncData extends AsyncTask<String, String, String> {
        String msg = "Internet/DB_Credentials/Windows_FireWall_TurnOn Error, See Android Monitor in the bottom For details";
        ProgressDialog progress;

        @Override
        protected void onPreExecute() {
//            startDate = ((MainActivity)getActivity()).getDate_out1();
//            endDate = ((MainActivity)getActivity()).getDate_out2();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection conn = connectionClass.CONN();
                if (conn == null) {
                    success = false;
                } else {
                    String query = "EXEC WIPXP_MNR.[dbo].[SP_ANDROID_LIST_WORK_ORDER_PROCESS_PRINT] ";
                    Log.d("QUERY", query);
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    if (rs != null) {
                        MyRecordset.clear(); // kalau datanya ada listnya di clear dulu
                        while (rs.next()) {
                            try {
                                // kalau ada data masukkan ke list
                                MyRecordset.add(new EightColumnClass(rs.getString("COLUMN_1"),rs.getString("COLUMN_2"),rs.getString("COLUMN_3"),rs.getString("COLUMN_4"),rs.getString("COLUMN_5"),rs.getString("COLUMN_6"),rs.getString("COLUMN_7"),rs.getString("COLUMN_8")));

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                        MyAdapter.setmData(MyRecordset);
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

            if(success = true) {
                MyAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);// setelah datanya masuk jangan lupa kasih tau adapternya
            }else{
                Toast toast = Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, -120);
                toast.show();
            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onRefresh() {
        try {
            new SyncData().execute();
        }catch (Exception e){
            Log.e(EFragment.class.getSimpleName()
                    , "onResume: Start Service Failed, because : " + e.getMessage() );
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStop() {
        try{
            orderData.cancel(true);
        }catch (Exception e){
            Log.e(EFragment.class.getSimpleName(), "onStop: Stop Service Failed, because : "+e.getMessage() );
        }
        super.onStop();
    }

    public void setDataToHeaderFragment(final EightColumnClass model){
            new AlertDialog.Builder(getContext())
                    .setTitle("WO# " +  model.getColumn_1()+ "...")
                    .setMessage("Anda Ingin Buka Work Order   " +  model.getColumn_1()+ " ?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent go_to_order = new Intent(getContext(),  WorkOrderProcessActivity.class);
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

    }

}

