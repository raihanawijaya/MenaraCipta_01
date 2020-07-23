package raihana.msd.mcp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.Gravity;
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

import raihana.msd.mcp.adapter.NDetailAdapter;
import raihana.msd.mcp.connection.ConnectionClass;
import raihana.msd.mcp.model.EightColumnClass;
import raihana.msd.mcp.model.TwentyColumnClass;
import raihana.msd.mcp.utils.SharedPreference;

public class Activity_N_Detail extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private SwipeRefreshLayout swipeRefreshLayout;
    private EditText etSearch;
    private Button btnBatalMenu;
    private ImageView ivPrint;
    private TextView tvMenuName,tvQtyBatal;
    private RecyclerView myRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<EightColumnClass> MyRecordset = new ArrayList<>();
    private boolean success = false;
    private ConnectionClass connectionClass;
    private NDetailAdapter MyAdapter;
    private SharedPreference sharedPreference;
    private String Ord_No, storeCode, username,Struk_Notes,MenuId,MenuName;
    private String s,s1,s2;

    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket socket;
    BluetoothDevice bluetoothDevice;

    OutputStream outputStream;
    InputStream inputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;
    String value = "";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_n_detail);
        sharedPreference = new SharedPreference(this);
        etSearch = findViewById(R.id.et_search);
        myRecyclerView = (RecyclerView) findViewById(R.id.rv_n_detail);
        myRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        myRecyclerView.setLayoutManager(mLayoutManager);
        MyRecordset = new ArrayList<>();
        connectionClass = new ConnectionClass();
        username = sharedPreference.getObjectData("username", String.class);

        Ord_No = (String) getIntent().getSerializableExtra("ORD_NO");

        ivPrint = findViewById(R.id.iv_print);
        tvMenuName = findViewById(R.id.tv_menu_name);
        tvQtyBatal = findViewById(R.id.tv_qty_batal);
        btnBatalMenu = findViewById(R.id.btn_batal_menu);

        swipeRefreshLayout = findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(this);

        etSearch.setText(Ord_No);

       btnBatalMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (tvMenuName.getText().toString().equals("")) {
                    Toast toast = Toast.makeText(Activity_N_Detail.this, "Pilih Dulu Menu Yang Ingin Dibatalkan !", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                } else {
                    new AlertDialog.Builder(Activity_N_Detail.this)
                            .setTitle("Pembatalan Order ?")
                            .setMessage("Menu " + tvMenuName.getText().toString() + " Akan dibatalkan ?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    new BatalMenu().execute();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .show();
                }
            }
        });

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

        String articleSearch = etSearch.getText().toString();

        @Override
        protected void onPreExecute() {
         progress = ProgressDialog.show(Activity_N_Detail.this,"Search Data", "Searching ! Please wait...", true);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection conn = connectionClass.CONN();
                if (conn == null) {
                    success = false;
                } else {
                    String query = "EXEC DB_A4A292_SOUP.dbo.SP_ANDROID_LIST_ORDERED_MENU '" + articleSearch +"'";
                    Log.d("QUERY", query);
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    MyRecordset.clear();
                    if (rs != null) {
                        while (rs.next()) {
                            try {
                                System.out.println(rs.getString("COLUMN_2"));;
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
                    MyAdapter = new NDetailAdapter(MyRecordset, Activity_N_Detail.this);
                    myRecyclerView.setAdapter(MyAdapter);
                } catch (Exception ex) {
                }
            }
        }
    }

    private class BatalMenu extends AsyncTask<String, String, String> {
        String msg = "Internet/DB_Credentials/Windows_FireWall_TurnOn Error, See Android Monitor in the bottom For details";
        ProgressDialog progress;
        //String priceStringRev = priceString.replace("","0");

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(Activity_N_Detail.this,
                    "Proses Pembatalan ...", "Proses Pembatalan Sedang Berlangsung , Harap Tunggu !", true);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection conn = connectionClass.CONN();
                if (conn == null) {
                    success = false;
                } else {
                    String query = "EXEC DB_A4A292_SOUP.dbo.[SP_ANDROID_CANCEL_MENU_ORDER] '" + Ord_No + "','" + MenuId + "'";
                    Log.d("QUERY", query);
                    Statement stmt = conn.createStatement();
                    int status = stmt.executeUpdate(query);
                    if (status != 0) {
                        msg = "Menu Sudah Dibatalkan !";
                        success = true;
                    } else {
                        msg = "Pembatalan Menu Gagal !";
                        success = false;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Writer writer = new StringWriter();
                e.printStackTrace(new PrintWriter(writer));
                msg= writer.toString().substring(23,44);
                success = false;

            }
            return msg;
        }

        @Override
        protected void onPostExecute(String msg) {
            progress.dismiss();
            if (success == false) {
                Toast toast = Toast.makeText(Activity_N_Detail.this, msg, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
            } else {
                try {
                    new SyncData().execute();
                    new AlertDialog.Builder(Activity_N_Detail.this)
                            .setTitle("Pembatalan Berhasil...")
                            .setMessage("Menu " + tvMenuName.getText().toString() + " Sudah Dibatalkan !")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                            tvMenuName.setText("");
                            tvQtyBatal.setText("");

                               }
                            })
                            // A null listener allows the button to dismiss the dialog and take no further action.
                            //  .setNegativeButton(android.R.string.no, null)
                            .show();


                }
                catch (Exception ex) {
                    Toast toast = Toast.makeText(Activity_N_Detail.this, msg, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();

                }
            }

        }
    }

    private class PrintData extends AsyncTask<String, String, String> {
        String msg = "Internet/DB_Credentials/Windows_FireWall_TurnOn Error, See Android Monitor in the bottom For details";
        ProgressDialog progress;

        String articleSearch = etSearch.getText().toString();

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(Activity_N_Detail.this,"Print Tagihan", "Printing ! Please wait...", true);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection conn = connectionClass.CONN();
                if (conn == null) {
                    success = false;
                } else {
                    String query = "EXEC DB_A4A292_SOUP.dbo.[SP_ANDROID_RPT_TAGIHAN] '" + articleSearch +"'";
                    Log.d("QUERY", query);
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    MyRecordset.clear();
                    if (rs != null) {
                        while (rs.next()) {
                            try {
                                //MyRecordset.add(new EightColumnClass(rs.getString("COLUMN_1"),rs.getString("COLUMN_2"),rs.getString("COLUMN_3"),rs.getString("COLUMN_4"),rs.getString("COLUMN_5"),rs.getString("COLUMN_6"),rs.getString("COLUMN_7"),rs.getString("COLUMN_8")));

                                Struk_Notes = rs.getString("STRUK_NOTES")+ "\n\n";
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

            IntentPrint(Struk_Notes);


        }
    }

    public void setDataToBatalan(EightColumnClass model){

        tvMenuName.setText(model.getColumn_2());
        tvQtyBatal.setText("1");
        MenuId= model.getColumn_7();
        supportInvalidateOptionsMenu();
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

    public static String fixedLengthString(String string, int length) {
        return String.format("%1$"+length+ "s", string);
    }

    private String leftpad(String text, int length) {
        return String.format("%" + length + "." + length + "s", text);
    }

    private String rightpad(String text, int length) {
        return String.format("%-" + length + "." + length + "s", text);
    }

    private void IntentPrint(String txtvalue){
        byte[] buffer = txtvalue.getBytes();
        byte[] PrintHeader = { (byte) 0xAA, 0x55,2,0 };
        PrintHeader[3]=(byte) buffer.length;
        InitPrinter();
        if(PrintHeader.length>128)
        {
            value+="\nValue is more than 128 size\n";
            Toast.makeText(getApplicationContext(), value, Toast.LENGTH_LONG).show();
        }
        else
        {
            try
            {
                outputStream.write(txtvalue.getBytes());
                outputStream.close();
                socket.close();
            }
            catch(Exception ex)
            {
                value+=ex.toString()+ "\n" +"Excep IntentPrint \n";
                Toast.makeText(getApplicationContext(), value, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void InitPrinter(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        try
        {
            if(!bluetoothAdapter.isEnabled())
            {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }

            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

            if(pairedDevices.size() > 0)
            {
                for(BluetoothDevice device : pairedDevices)
                {
                  //  if(device.getName().equals("RPP02N"))
                    if(device.getAddress().equals(sharedPreference.getObjectData("PRINTER_KASIR", String.class)))
                    //Note, you will need to change this to match the name of your device
                    {
                        bluetoothDevice = device;
                        break;
                    }
                }

                UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
                Method m = bluetoothDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                socket = (BluetoothSocket) m.invoke(bluetoothDevice, 1);
                bluetoothAdapter.cancelDiscovery();
                socket.connect();
                outputStream = socket.getOutputStream();
                inputStream = socket.getInputStream();
                beginListenForData();
            }
            else
            {
                value+="No Devices found";
                Toast.makeText(getApplicationContext(), value, Toast.LENGTH_LONG).show();
                return;
            }
        }
        catch(Exception ex)
        {
            value+=ex.toString()+ "\n" +" InitPrinter \n";
            Toast.makeText(getApplicationContext(), value, Toast.LENGTH_LONG).show();
        }
    }

    private void beginListenForData(){
        try {
            final Handler handler = new Handler();

            // this is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            workerThread = new Thread(new Runnable() {
                public void run() {

                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {

                        try {

                            int bytesAvailable = inputStream.available();

                            if (bytesAvailable > 0) {

                                byte[] packetBytes = new byte[bytesAvailable];
                                inputStream.read(packetBytes);

                                for (int i = 0; i < bytesAvailable; i++) {

                                    byte b = packetBytes[i];
                                    if (b == delimiter) {

                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length
                                        );

                                        // specify US-ASCII encoding
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        // tell the user data were sent to bluetooth printer device
                                        handler.post(new Runnable() {
                                            public void run() {
                                                Log.d("e", data);
                                            }
                                        });

                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                        }

                    }
                }
            });

            workerThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
