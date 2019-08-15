package raihana.msd.rgl.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import raihana.msd.rgl.BDetailActivity;
import raihana.msd.rgl.MainActivity;
import raihana.msd.rgl.R;
import raihana.msd.rgl.adapter.BAdapter;
import raihana.msd.rgl.connection.ConnectionClass;
import raihana.msd.rgl.model.EightColumnClass;
import raihana.msd.rgl.utils.SharedPreference;


public class BFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    //1. Declaration: View, RecyclerView, List<>,
    View v;
    private RecyclerView myRecyclerView;
    private List<EightColumnClass> listB = new ArrayList<>();
    private boolean success = false;
    private ConnectionClass connectionClass;
    private BAdapter BAdapter;
    private SyncData orderData = new SyncData();
    private String onDate;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SharedPreference sharedPreference;
    private String startDate, endDate, storeCode;
    private LinearLayout lay_b_add;

    public void sync() {
        try{
            new SyncData().execute();
        }catch (Exception e){
            Log.e(BFragment.class.getSimpleName(), "sync: ",e );
        }
    }

    public BFragment() {
        onRefresh();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //2. Return view
        v = inflater.inflate(R.layout.fragment_b,container,false);
        sharedPreference = new SharedPreference(getContext());
        connectionClass = new ConnectionClass();
        myRecyclerView = v.findViewById(R.id.rv_fragment_b);
        BAdapter = new BAdapter(getContext());
        myRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        myRecyclerView.setAdapter(BAdapter);
        swipeRefreshLayout = v.findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(this);
        storeCode = sharedPreference.getObjectData("username", String.class);
        lay_b_add = v.findViewById(R.id.lay_b_add);

        lay_b_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), BDetailActivity.class);
                startActivity(intent);
              }
        });

        return v;
        //5. Make the RV


    }

    private class SyncData extends AsyncTask<String, String, String> {
        String msg = "Internet/DB_Credentials/Windows_FireWall_TurnOn Error, See Android Monitor in the bottom For details";
        ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            startDate = ((MainActivity)getActivity()).getDate_out1();
            endDate = ((MainActivity)getActivity()).getDate_out2();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection conn = connectionClass.CONN();
                if (conn == null) {
                    success = false;
                } else {
                    String query = "EXEC DB_A4A292_RGL.dbo.SP_DAILY_SALES_USER_ID_STORE '" + storeCode + "','" + startDate +"','" + endDate + "'";//nah ini tglnya masih dr query blm dari varnya
                    Log.d("QUERY", query);
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    if (rs != null) {
                        listB.clear(); // kalau datanya ada listnya di clear dulu
                        while (rs.next()) {
                            try {
                                // kalau ada data masukkan ke list
                                listB.add(new EightColumnClass(rs.getString("COLUMN_1"),rs.getString("COLUMN_2"),rs.getString("COLUMN_3"),rs.getString("COLUMN_4"),rs.getString("COLUMN_5"),rs.getString("COLUMN_6"),rs.getString("COLUMN_7"),rs.getString("COLUMN_8")));
                                onDate = rs.getString("TRX_DATE");
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                        BAdapter.setmData(listB);
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
            BAdapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);// setelah datanya masuk jangan lupa kasih tau adapternya
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        swipeRefreshLayout.setRefreshing(false);
        BAdapter.setmData(listB);
    }

    @Override
    public void onRefresh() {
        try {
            new SyncData().execute();
        }catch (Exception e){
            Log.e(BFragment.class.getSimpleName()
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
            Log.e(BFragment.class.getSimpleName(), "onStop: Stop Service Failed, because : "+e.getMessage() );
        }
        super.onStop();
    }
}

