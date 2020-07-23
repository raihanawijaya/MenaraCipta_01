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
import android.support.annotation.DrawableRes;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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

import raihana.msd.mcp.adapter.CategoryListAdapter;
import raihana.msd.mcp.adapter.MenuListAdapter;
import raihana.msd.mcp.adapter.OrderedDrinkAdapter;
import raihana.msd.mcp.adapter.OrderedFoodAdapter;
import raihana.msd.mcp.connection.ConnectionClass;
import raihana.msd.mcp.model.EightColumnClass;
import raihana.msd.mcp.model.TwentyColumnClass;
import raihana.msd.mcp.utils.SharedPreference;

public class OrderActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private SwipeRefreshLayout swipeRefreshLayout;
    private CheckBox cbBungkus;
    private Button btnPrintFood,btnPrintDrink;
    private EditText etSearch;
    private ImageView ivMinus,ivPlus,ivPlus5;
    private TextView tvPayType,tvOrdNo,tvJumlahTagihan,tvStatusOrder,tvTableId,tvQtyOrder;
    private RecyclerView Rv_Category_List,Rv_Menu_List,rv_Ordered_Food,rv_Ordered_Drink;
    private RecyclerView.LayoutManager mLayoutManager,MenuLayoutManager,lm_ordered_food,lm_ordered_drink;
    private List<EightColumnClass> MyCtgrRecordset = new ArrayList<>();
    private List<EightColumnClass> MyMenuRecordset = new ArrayList<>();
    private List<EightColumnClass> MyOrderedFoodRecordset = new ArrayList<>();
    private List<EightColumnClass> MyOrderedDrinkRecordset = new ArrayList<>();

    private boolean success = false;
    private ConnectionClass connectionClass;
    private CategoryListAdapter CategoryListAdapter;
    private MenuListAdapter MenuListAdapter;
    private OrderedFoodAdapter OrderedFoodAdapter;
    private OrderedDrinkAdapter OrderedDrinkAdapter;

    private SharedPreference sharedPreference;
    private String Ord_No, storeCode, username,StatusOrder,MenuPrinted,MenuRowid;
    private String s,s1,s2,Struk_Notes_Makanan,Struk_Notes_Minuman;
    private String CategoryId,MenuId,Table_Id,Table_Status,Harga_Menu,MenuName,Ctgr_Type,CategoryName;

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
        setContentView(R.layout.activity_order);
        sharedPreference = new SharedPreference(this);
        cbBungkus = findViewById(R.id.cb_bungkus);

    //    swipeRefreshLayout = findViewById(R.id.swipe_ctgr);
    //    swipeRefreshLayout.setOnRefreshListener(this);
//        swipeRefreshLayout.setRefreshing(false);
        btnPrintDrink = findViewById(R.id.btn_print_drink);
        btnPrintFood  = findViewById(R.id.btn_print_food);

        tvTableId = findViewById(R.id.tv_table_id);
        ivPlus = findViewById(R.id.iv_plus);
        ivPlus5 = findViewById(R.id.iv_plus_lima);
        ivMinus = findViewById(R.id.iv_minus);
        tvOrdNo = findViewById(R.id.tv_ord_no);
        tvQtyOrder =  findViewById(R.id.tv_qty_order);

        Rv_Category_List = (RecyclerView) findViewById(R.id.rv_category_list);
        Rv_Category_List.setHasFixedSize(true);
        Rv_Menu_List = (RecyclerView) findViewById(R.id.rv_menu_list);
        Rv_Menu_List.setHasFixedSize(true);

        rv_Ordered_Food = (RecyclerView) findViewById(R.id.rv_ordered_food_list);
        rv_Ordered_Food.setHasFixedSize(true);
        rv_Ordered_Drink = (RecyclerView) findViewById(R.id.rv_ordered_drink_list);
        rv_Ordered_Drink.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        MenuLayoutManager = new LinearLayoutManager(this);
        lm_ordered_food = new LinearLayoutManager(this);
        lm_ordered_drink = new LinearLayoutManager(this);

        Rv_Category_List.setLayoutManager(mLayoutManager);
        Rv_Menu_List.setLayoutManager(MenuLayoutManager);
        rv_Ordered_Food.setLayoutManager(lm_ordered_food);
        rv_Ordered_Drink.setLayoutManager(lm_ordered_drink);

        MyCtgrRecordset = new ArrayList<>();
        connectionClass = new ConnectionClass();
        username = sharedPreference.getObjectData("username", String.class);
        Ord_No = (String) getIntent().getSerializableExtra("ORD_NO");
        Table_Id = (String) getIntent().getSerializableExtra("TABLE_ID");
        Table_Status = (String) getIntent().getSerializableExtra("TABLE_STATUS");
        tvOrdNo.setText(Ord_No);
        tvTableId.setText(Table_Id);

        new ListCategory ().execute();

        if (Table_Status.equals("OPEN")){
            new BookedTable().execute();        }
        else {
            new OrderedFood().execute();
            new OrderedDrink().execute();}

        ivPlus.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                int int_qty_order;

                int_qty_order = Integer.parseInt(tvQtyOrder.getText().toString());
                int_qty_order = int_qty_order + 1;

                tvQtyOrder.setText(String.valueOf(int_qty_order));
            }
        });

        ivPlus5.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                int int_qty_order;
                String str_qty_order = tvQtyOrder.getText().toString();
                if (str_qty_order.equals("1")) str_qty_order = "0";
                int_qty_order = Integer.parseInt(str_qty_order);
                int_qty_order = int_qty_order + 5;

                tvQtyOrder.setText(String.valueOf(int_qty_order));
            }
        });

        ivMinus.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {

                int int_qty_order;

                if (tvQtyOrder.getText().toString().equals("1")) {
                    tvQtyOrder.setText("1");
                }else {
                    int_qty_order = Integer.parseInt(tvQtyOrder.getText().toString());
                    int_qty_order = int_qty_order - 1;
                    tvQtyOrder.setText(String.valueOf(int_qty_order));
                }
            }
        });

        btnPrintFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              new PrintOrderedFood().execute();
             }
        });

        btnPrintDrink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              new PrintOrderedDrink().execute();
          }
        });

    }

    @Override
    public void onRefresh() {

        ListCategory  syncData = new ListCategory ();
        syncData.execute("");

    }

    private class ListCategory extends AsyncTask<String, String, String> {
        String msg = "Internet/DB_Credentials/Windows_FireWall_TurnOn Error, See Android Monitor in the bottom For details";
        ProgressDialog progress;

        @Override
        protected void onPreExecute() {
        //  progress = ProgressDialog.show(PaymentActivity.this,"Search Data", "Searching ! Please wait...", true);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                MyCtgrRecordset.clear();
                MyCtgrRecordset.add(new EightColumnClass("TERLARIS FOOD","50","","","","","",""));
                MyCtgrRecordset.add(new EightColumnClass("TERLARIS DRINK","51","","","","","",""));
                MyCtgrRecordset.add(new EightColumnClass("NASI-MIE","1","","","","","",""));
                MyCtgrRecordset.add(new EightColumnClass("SOUP IKAN","2","","","","","",""));
                MyCtgrRecordset.add(new EightColumnClass("CUMI","6","","","","","",""));
                MyCtgrRecordset.add(new EightColumnClass("SATE-AYAM","8","","","","","",""));
                MyCtgrRecordset.add(new EightColumnClass("CAH-TUMIS","9","","","","","",""));
                MyCtgrRecordset.add(new EightColumnClass("TAHU TEMPE","10","","","","","",""));
                MyCtgrRecordset.add(new EightColumnClass("MINUMAN","13","","","","","",""));
                MyCtgrRecordset.add(new EightColumnClass("MINUMAN LAIN","14","","","","","",""));
                MyCtgrRecordset.add(new EightColumnClass("SOFT DRINK","11","","","","","",""));
                MyCtgrRecordset.add(new EightColumnClass("JUICE","12","","","","","",""));
                MyCtgrRecordset.add(new EightColumnClass("WOKU","3","","","","","",""));
                MyCtgrRecordset.add(new EightColumnClass("GURAME","4","","","","","",""));
                MyCtgrRecordset.add(new EightColumnClass("KEPITING","5","","","","","",""));
                MyCtgrRecordset.add(new EightColumnClass("UDANG","7","","","","","",""));
                MyCtgrRecordset.add(new EightColumnClass("CHARGE","15","","","","","",""));
                MyCtgrRecordset.add(new EightColumnClass("DISCOUNT","16","","","","","",""));
                MyCtgrRecordset.add(new EightColumnClass("MAKANAN LAIN","17","","","","","",""));
                MyCtgrRecordset.add(new EightColumnClass("KAKAP","18","","","","","",""));

                //    System.out.println("String Added in ArrayList= "+ MyCtgrRecordset);
                success = true;
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
//            swipeRefreshLayout.setRefreshing(false);

            if (success == false) {

            } else {
                try {
                    CategoryListAdapter = new CategoryListAdapter(MyCtgrRecordset, OrderActivity.this);
                    Rv_Category_List.setAdapter(CategoryListAdapter);
                } catch (Exception ex) {
                }
            }
        }
    }

    private class ListMenu extends AsyncTask<String, String, String> {
        String msg = "Internet/DB_Credentials/Windows_FireWall_TurnOn Error, See Android Monitor in the bottom For details";
        ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(OrderActivity.this,CategoryName,
                    "Tunggu Sebentar , Sedang diproses !", true);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection conn = connectionClass.CONN();
                if (conn == null) {
                    success = false;
                } else {
                    String query = "EXEC WIPXP_MNR.[dbo].[SP_ANDROID_LIST_MENU_BY_CTGR] '" + CategoryId + "'";
                    Log.d("QUERY", query);
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    if (rs != null) {
                        MyMenuRecordset.clear(); // kalau datanya ada listnya di clear dulu
                        while (rs.next()) {
                            try {
                                // kalau ada data masukkan ke list
                                MyMenuRecordset.add(new EightColumnClass(rs.getString("COLUMN_1"),rs.getString("COLUMN_2"),rs.getString("COLUMN_3"),rs.getString("COLUMN_4"),"","","",""));

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                        //MenuListAdapter.setmData(MyMenuRecordset);
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
            progress.dismiss();
//            swipeRefreshLayout.setRefreshing(false);

            if (success == false) {

            } else {
                try {
                   MenuListAdapter = new MenuListAdapter(MyMenuRecordset, OrderActivity.this);
                   Rv_Menu_List.setAdapter(MenuListAdapter);
                } catch (Exception ex) {
                }
            }
        }
    }

    private class ListMenuLocal extends AsyncTask<String, String, String> {
        String msg = "Internet/DB_Credentials/Windows_FireWall_TurnOn Error, See Android Monitor in the bottom For details";
        ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(OrderActivity.this,CategoryName,
                    "Tunggu Sebentar , Sedang diproses !", true);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                MyMenuRecordset.clear();
                if (CategoryId.equals("1")) {

                  new ListMenuSoup();
                  //  MyMenuRecordset.add(new EightColumnClass("SOUP IKAN", "50", "", "", "", "", "", ""));
                  //  MyMenuRecordset.add(new EightColumnClass("SOUP IKAN 1/2", "51", "", "", "", "", "", ""));
                  //  MyMenuRecordset.add(new EightColumnClass("WOKU", "52", "", "", "", "", "", ""));
                }
                if (CategoryId.equals("2")) {
                    MyMenuRecordset.add(new EightColumnClass("SOUP IKAN OK", "50", "", "", "", "", "", ""));
                    MyMenuRecordset.add(new EightColumnClass("SOUP IKAN 1/2 YES", "51", "", "", "", "", "", ""));
                    MyMenuRecordset.add(new EightColumnClass("WOKU BELANGA", "52", "", "", "", "", "", ""));
                }

            } catch (Exception e) {
                e.printStackTrace();
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
                    MenuListAdapter = new MenuListAdapter(MyMenuRecordset, OrderActivity.this);
                    Rv_Menu_List.setAdapter(MenuListAdapter);
                } catch (Exception ex) {
                }
            }
        }
    }

    private class AddMenu extends AsyncTask<String, String, String> {
        String msg = "Internet/DB_Credentials/Windows_FireWall_TurnOn Error, See Android Monitor in the bottom For details";
        ProgressDialog progress;
        String QtyOrder = tvQtyOrder.getText().toString();
        String Note_1 = "";
        String Bungkus = "Y";


        @Override
        protected void onPreExecute() {
            if (cbBungkus.isChecked()){
                Bungkus = "Y";
            }else {
                Bungkus = "N";
            }
            progress = ProgressDialog.show(OrderActivity.this,QtyOrder + " " +MenuName, "Sedang Dipesan !", true);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection conn = connectionClass.CONN();
                if (conn == null) {
                    success = false;
                } else {
                    String query = "EXEC WIPXP_MNR.dbo.[SP_ANDROID_INSERT_MENU_ORDER] '" + Ord_No + "','" + MenuId + "','" + QtyOrder + "','" + Harga_Menu + "','" + Note_1 + "','"+ Bungkus + "'";
                    Log.d("QUERY", query);
                    Statement stmt = conn.createStatement();
                    int status = stmt.executeUpdate(query);
                    if (status != 0) {
                        msg =  QtyOrder + " " + MenuName + " OK !";
                        success = true;
                    } else {
                        msg = "Input " + MenuName + " Gagal !";
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
            progress.dismiss();
            if (success == false) {
                    Toast toast = Toast.makeText(OrderActivity.this, msg, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
            } else {
                try {
                  //  Toast toast = Toast.makeText(OrderActivity.this, msg, Toast.LENGTH_SHORT);
                  //  toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                  //  toast.show();
                  //  Log.d("Q ", Ctgr_Type);
                    tvQtyOrder.setText("1");
                    cbBungkus.setChecked(false);
                    if (Ctgr_Type.equals("1.FOOD")) {
                        new OrderedFood().execute();
                    }else{
                        new OrderedDrink().execute();
                    }
                } catch (Exception ex) {
                }
            }

        }
    }

    private class DeleteMenuFood extends AsyncTask<String, String, String> {
        String msg = "Internet/DB_Credentials/Windows_FireWall_TurnOn Error, See Android Monitor in the bottom For details";
        ProgressDialog progress;
        String QtyDelete = "1";

        @Override
        protected void onPreExecute() {

            progress = ProgressDialog.show(OrderActivity.this,QtyDelete + " " +MenuName, "Sedang Dihapus !", true);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection conn = connectionClass.CONN();
                if (conn == null) {
                    success = false;
                } else {
                    String query = "EXEC WIPXP_MNR.dbo.[SP_ANDROID_DELETE_MENU_ORDER] '" + Ord_No + "','" + MenuId + "','" + MenuRowid  + "'";
                    Log.d("QUERY", query);
                    Statement stmt = conn.createStatement();
                    int status = stmt.executeUpdate(query);
                    if (status != 0) {
                        msg =  QtyDelete + " " + MenuName + " Sudah Dihapus !";
                        success = true;
                    } else {
                        msg = "Delete " + MenuName + " Gagal !";
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
            progress.dismiss();
            if (success == false) {
                Toast toast = Toast.makeText(OrderActivity.this, msg, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
            } else {
                try {
                    tvQtyOrder.setText("1");
                    cbBungkus.setChecked(false);
                        new OrderedFood().execute();
                } catch (Exception ex) {
                }
            }

        }
    }

    private class DeleteMenuDrink extends AsyncTask<String, String, String> {
        String msg = "Internet/DB_Credentials/Windows_FireWall_TurnOn Error, See Android Monitor in the bottom For details";
        ProgressDialog progress;
        String QtyDelete = "1";

        @Override
        protected void onPreExecute() {

            progress = ProgressDialog.show(OrderActivity.this,QtyDelete + " " +MenuName, "Sedang Dihapus !", true);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection conn = connectionClass.CONN();
                if (conn == null) {
                    success = false;
                } else {
                    String query = "EXEC WIPXP_MNR.dbo.[SP_ANDROID_DELETE_MENU_ORDER] '" + Ord_No + "','" + MenuId + "','" +  MenuRowid  + "'";
                    Log.d("QUERY", query);
                    Statement stmt = conn.createStatement();
                    int status = stmt.executeUpdate(query);
                    if (status != 0) {
                        msg =  QtyDelete + " " + MenuName + " Sudah Dihapus !";
                        success = true;
                    } else {
                        msg = "Delete " + MenuName + " Gagal !";
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
            progress.dismiss();
            if (success == false) {
                Toast toast = Toast.makeText(OrderActivity.this, msg, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
            } else {
                try {
                    tvQtyOrder.setText("1");
                    cbBungkus.setChecked(false);
                    new OrderedDrink().execute();
                } catch (Exception ex) {
                }
            }

        }
    }

    private class OrderedFood extends AsyncTask<String, String, String> {
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
                    String query = "EXEC WIPXP_MNR.dbo.SP_ANDROID_GET_ORDERED_FOOD '"+ Ord_No +"'";
                    Log.d("QUERY", query);
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    MyOrderedFoodRecordset.clear();
                    if (rs != null) {
                        while (rs.next()) {
                            try {
                                MyOrderedFoodRecordset.add(new EightColumnClass(rs.getString("COLUMN_1"),rs.getString("COLUMN_2"),rs.getString("COLUMN_3"),rs.getString("COLUMN_4"),rs.getString("COLUMN_5"),rs.getString("COLUMN_6"),rs.getString("COLUMN_7"),rs.getString("COLUMN_8")));
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
            //      swipeRefreshLayout.setRefreshing(false);
            if (success == false) {

            } else {
                try {
                    OrderedFoodAdapter = new OrderedFoodAdapter(MyOrderedFoodRecordset, OrderActivity.this);
                    rv_Ordered_Food.setAdapter(OrderedFoodAdapter);
                } catch (Exception ex) {
                }
            }
        }
    }

    private class OrderedDrink extends AsyncTask<String, String, String> {
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
                    String query = "EXEC WIPXP_MNR.dbo.SP_ANDROID_GET_ORDERED_DRINK '"+ Ord_No +"'";
                    Log.d("QUERY", query);
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    MyOrderedDrinkRecordset.clear();
                    if (rs != null) {
                        while (rs.next()) {
                            try {
                                MyOrderedDrinkRecordset.add(new EightColumnClass(rs.getString("COLUMN_1"),rs.getString("COLUMN_2"),rs.getString("COLUMN_3"),rs.getString("COLUMN_4"),rs.getString("COLUMN_5"),rs.getString("COLUMN_6"),rs.getString("COLUMN_7"),rs.getString("COLUMN_8")));
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
            //      swipeRefreshLayout.setRefreshing(false);
            if (success == false) {

            } else {
                try {
                    OrderedDrinkAdapter = new OrderedDrinkAdapter(MyOrderedDrinkRecordset, OrderActivity.this);
                    rv_Ordered_Drink.setAdapter(OrderedDrinkAdapter);
                } catch (Exception ex) {
                }
            }
        }
    }

    private class BookedTable extends AsyncTask<String, String, String> {
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
                    String query = "EXEC WIPXP_MNR.dbo.SP_ANDROID_BOOKED_TABLE '"+ Table_Id +"','','','PLM'";
                    Log.d("QUERY", query);
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    MyCtgrRecordset.clear();
                    if (rs != null) {
                        while (rs.next()) {
                            try {
                                Ord_No = rs.getString("ORD_NO");
                                StatusOrder = rs.getString("TRX_STATUS");
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
            //      swipeRefreshLayout.setRefreshing(false);
            if (success == false) {
                tvOrdNo.setText("");

            } else {
                try {
                    tvOrdNo.setText(Ord_No);
                } catch (Exception ex) {
                }
            }
        }
    }

    private class PrintOrderedFood extends AsyncTask<String, String, String> {
        String msg = "Internet/DB_Credentials/Windows_FireWall_TurnOn Error, See Android Monitor in the bottom For details";
        ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            Struk_Notes_Makanan= null;
         //   progress = ProgressDialog.show(OrderActivity.this,"Print Food", "Printing ! Please wait...", true);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection conn = connectionClass.CONN();
                if (conn == null) {
                    success = false;
                } else {
                    String query = "EXEC WIPXP_MNR.dbo.[SP_ANDROID_PRINT_FOOD] '" + Ord_No +"'";
                    Log.d("QUERY", query);
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    //MyOrderedFoodRecordset.clear();
                    if (rs != null) {
                        while (rs.next()) {
                            try {
                                //MyCtgrRecordset.add(new EightColumnClass(rs.getString("COLUMN_1"),rs.getString("COLUMN_2"),rs.getString("COLUMN_3"),rs.getString("COLUMN_4"),rs.getString("COLUMN_5"),rs.getString("COLUMN_6"),rs.getString("COLUMN_7"),rs.getString("COLUMN_8")));
                                Struk_Notes_Makanan = rs.getString("STRUK_NOTES");
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
           // progress.dismiss();

            if (Struk_Notes_Makanan.equals("null")) {
                msg = "Semua Order Makanan Sudah diprint !";
                new AlertDialog.Builder(OrderActivity.this)
                        .setTitle("Peringatan...!")
                        .setMessage(msg)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                             //   IntentPrintOrderedFood(Struk_Notes);
                            }
                        })
                      //  .setNegativeButton(android.R.string.no, null)
                        .show();
               } else {
                if (Struk_Notes_Makanan.equals("null")) {

                } else {
                    new AlertDialog.Builder(OrderActivity.this)
                            .setTitle("Cetak Pesanan ke Dapur ...")
                            .setMessage("Pesanan Makanan Akan Dicetak ke Printer Dapur ? ")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    IntentPrintOrderedFood(Struk_Notes_Makanan);
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .show();
                  }
            }
        }
    }
    public class SetFoodPrintedToYes extends AsyncTask<String, String, String> {
        String msg = "Internet/DB_Credentials/Windows_FireWall_TurnOn Error, See Android Monitor in the bottom For details";
        ProgressDialog progress;

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
                    String query = "EXEC WIPXP_MNR.dbo.SP_ANDROID_SET_PRINTED_FOOD_TO_YES '" + Ord_No + "'";//nah ini tglnya masih dr query blm dari varnya
                    Log.d("QUERY", query);
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    if (rs != null) {
                        while (rs.next()) {
                            msg = rs.getString("RESULT");
                        }
                        success = true;
                    } else {
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
                Toast toast = Toast.makeText(OrderActivity.this, "Print Makanan Gagal !", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
            } else {
                Toast toast = Toast.makeText(OrderActivity.this, "Print Makanan Berhasil !", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
                new OrderedFood().execute();
            }
        }
    }

    private class PrintOrderedDrink extends AsyncTask<String, String, String> {
        String msg = "Internet/DB_Credentials/Windows_FireWall_TurnOn Error, See Android Monitor in the bottom For details";
        ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            Struk_Notes_Minuman=null;
          //  progress = ProgressDialog.show(OrderActivity.this,"Cetak Pesanan Ke Juice", "Sedang Mencetak , Harap Tunggu !", true);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection conn = connectionClass.CONN();
                if (conn == null) {
                    success = false;
                } else {
                    String query = "EXEC WIPXP_MNR.dbo.[SP_ANDROID_PRINT_DRINK] '" + Ord_No +"'";
                    Log.d("QUERY", query);
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);

                    if (rs != null) {
                        while (rs.next()) {
                            try {
                                //MyCtgrRecordset.add(new EightColumnClass(rs.getString("COLUMN_1"),rs.getString("COLUMN_2"),rs.getString("COLUMN_3"),rs.getString("COLUMN_4"),rs.getString("COLUMN_5"),rs.getString("COLUMN_6"),rs.getString("COLUMN_7"),rs.getString("COLUMN_8")));
                                Struk_Notes_Minuman = rs.getString("STRUK_NOTES");
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
          //  progress.dismiss();
            if (Struk_Notes_Minuman.equals("null")){
                msg = "Semua Order Minuman Sudah diprint !";
                new AlertDialog.Builder(OrderActivity.this)
                        .setTitle("Peringatan...!")
                        .setMessage(msg)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //   IntentPrintOrderedFood(Struk_Notes);
                            }
                        })
                        //  .setNegativeButton(android.R.string.no, null)
                        .show();
            } else {
                if (Struk_Notes_Minuman.equals("null")){

                } else {
                    new AlertDialog.Builder(OrderActivity.this)
                            .setTitle("Cetak Pesanan ke Juice ...")
                            .setMessage("Pesanan Minuman Akan Dicetak ke Printer Juice ? ")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    IntentPrintOrderedDrink(Struk_Notes_Minuman);
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .show();
                }
            }
       }
    }
    public class SetDrinkPrintedToYes extends AsyncTask<String, String, String> {
        String msg = "Internet/DB_Credentials/Windows_FireWall_TurnOn Error, See Android Monitor in the bottom For details";
        ProgressDialog progress;

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
                    String query = "EXEC WIPXP_MNR.dbo.SP_ANDROID_SET_PRINTED_DRINK_TO_YES '" + Ord_No + "'";//nah ini tglnya masih dr query blm dari varnya
                    Log.d("QUERY", query);
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    if (rs != null) {
                        while (rs.next()) {
                            msg = rs.getString("RESULT");
                        }
                        success = true;
                    } else {
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
                Toast toast = Toast.makeText(OrderActivity.this, "Print Minuman Gagal !", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
            } else {
                Toast toast = Toast.makeText(OrderActivity.this, "Print Minuman Berhasil !", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();

                new OrderedDrink().execute();
            }
          }
    }

    public void setDataToCtgrId(EightColumnClass model){
        CategoryName = model.getColumn_1();
        CategoryId = model.getColumn_2();
        new ListMenu().execute();
        // new ListMenuLocal().execute();
    }

    public class ListMenuSoup{} {
        MyMenuRecordset.add(new EightColumnClass("SOUP IKAN", "50", "", "", "", "", "", ""));
        MyMenuRecordset.add(new EightColumnClass("SOUP IKAN 1/2", "51", "", "", "", "", "", ""));
        MyMenuRecordset.add(new EightColumnClass("WOKU", "52", "", "", "", "", "", ""));
    };

    public void setDataToMenuId(EightColumnClass model){
        MenuName = model.getColumn_1();
        MenuId = model.getColumn_2();
        Harga_Menu = model.getColumn_3();
        Ctgr_Type = model.getColumn_4();

        new AddMenu().execute();
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

    public void onOrderedFoodClicked(EightColumnClass data){
        String PesanError;
        MenuPrinted = data.getColumn_4();
        MenuId = data.getColumn_3();
        MenuName = data.getColumn_2();
        Ctgr_Type= data.getColumn_7();
        MenuRowid = data.getColumn_8();

        PesanError ="Menu Sudah Diprint , Tidak Bisa dihapus ! "+"\n"+"Harap Lakukan Pembatalan di kasir !";
        if (MenuPrinted.equals("Y")){
            new AlertDialog.Builder(OrderActivity.this)
                    .setTitle("Peringatan ...")
                    .setMessage(PesanError)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();

        } else {
            new AlertDialog.Builder(OrderActivity.this)
                    .setTitle("Hapus Pesanan ...")
                    .setMessage("Hapus 1 Pesanan " + MenuName + " ?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            new DeleteMenuFood().execute();
                        }
                    })
                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        }

    }

    public void onOrderedDrinkClicked(EightColumnClass data){
        String PesanError;
        MenuPrinted = data.getColumn_4();
        MenuId = data.getColumn_3();
        MenuName = data.getColumn_2();
        Ctgr_Type=data.getColumn_7();
        MenuRowid = data.getColumn_8();

        PesanError ="Menu Sudah Diprint , Tidak Bisa dihapus ! "+"\n"+"Harap Lakukan Pembatalan di kasir !";
        if (MenuPrinted.equals("Y")){
            new AlertDialog.Builder(OrderActivity.this)
                    .setTitle("Peringatan ...")
                    .setMessage(PesanError)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();

        } else {
            new AlertDialog.Builder(OrderActivity.this)
                    .setTitle("Hapus Pesanan... ")
                    .setMessage("Hapus 1 Pesanan " + MenuName + " ?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            new DeleteMenuDrink().execute();
                        }
                    })
                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.no, null)
                    .show();
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

    private void IntentPrintOrderedFood(String txtvalue){
        byte[] buffer = txtvalue.getBytes();
        byte[] PrintHeader = { (byte) 0xAA, 0x55,2,0 };
        PrintHeader[3]=(byte) buffer.length;

        InitPrinterFood();

        if(PrintHeader.length>128)
        {
            value+="\nValue is more than 128 size\n";
            Toast.makeText(getApplicationContext(), value, Toast.LENGTH_LONG).show();
            Toast toast = Toast.makeText(OrderActivity.this, "Print Menu Makanan GAGAL !", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();
        }
        else
        {
            try
            {
                outputStream.write(txtvalue.getBytes());
                outputStream.close();
                socket.close();
                new SetFoodPrintedToYes().execute();
            }
            catch(Exception ex)
            {
                value+=ex.toString()+ "\n" +"Except IntentPrint \n";
                //Toast.makeText(getApplicationContext(), value, Toast.LENGTH_LONG).show();
                Toast toast = Toast.makeText(OrderActivity.this, "Print Menu Makanan GAGAL !", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
            }
        }
    }

    private void IntentPrintOrderedDrink(String txtvalue){
        byte[] buffer = txtvalue.getBytes();
        byte[] PrintHeader = { (byte) 0xAA, 0x55,2,0 };

        PrintHeader[3]=(byte) buffer.length;

        InitPrinterDrink();

        if(PrintHeader.length>128)
        {
            value+="\nValue is more than 128 size\n";
            Toast.makeText(getApplicationContext(), value, Toast.LENGTH_LONG).show();
            Toast toast = Toast.makeText(OrderActivity.this, "Print Menu Minuman Gagal ! ", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();
        }
        else
        {
            try
            {
                outputStream.write(txtvalue.getBytes());
                outputStream.close();
                socket.close();
                new SetDrinkPrintedToYes().execute();
            }
            catch(Exception ex)
            {
                value+=ex.toString()+ "\n" +"Excep IntentPrint \n";
                //Toast.makeText(getApplicationContext(), value, Toast.LENGTH_LONG).show();
                Toast toast = Toast.makeText(OrderActivity.this, "Print Menu Drink Gagal !", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
            }
        }
    }

    private void InitPrinterFood(){
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
                    if(device.getAddress().equals(sharedPreference.getObjectData("PRINTER_DAPUR", String.class)))
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
         //   Toast.makeText(getApplicationContext(), value, Toast.LENGTH_LONG).show();
        }
    }

    private void InitPrinterDrink(){
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
                    if(device.getAddress().equals(sharedPreference.getObjectData("PRINTER_JUICE", String.class)))
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

    @Override
    public void onBackPressed() {
     super.onBackPressed();
// Not calling *super*, disables back button in current screen.
    }


}
