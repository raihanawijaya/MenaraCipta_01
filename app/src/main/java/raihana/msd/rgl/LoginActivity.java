package raihana.msd.rgl;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import raihana.msd.rgl.connection.ConnectionClass;
import raihana.msd.rgl.utils.SharedPreference;

public class LoginActivity extends AppCompatActivity {
    SharedPreference sharedPreference;
    Button btnMasuk;
    EditText etUserCode, etPassword;
    ProgressBar progressBar;
    String un, pass, db, ip;
    String usercode, password, username;
    private ConnectionClass connectionClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreference = new SharedPreference(this);
        etUserCode = findViewById(R.id.et_user_code);
        etPassword = findViewById(R.id.et_password);
 //       progressBar = findViewById(R.id.progress_bar);
//        progressBar.setVisibility(View.GONE);
        connectionClass = new ConnectionClass();

        btnMasuk = findViewById(R.id.btn_masuk);
        btnMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usercode = etUserCode.getText().toString();
                password = etPassword.getText().toString();
                CheckLogin checkLogin = new CheckLogin();// this is the Asynctask, which is used to process in background to reduce load on app process
                checkLogin.execute("");
            }
        });
    }

    //CHECK LOGIN, ONPRE OR ONPOST
    public class CheckLogin extends AsyncTask<String,String,String> {
        String z = "";
        Boolean isSuccess = false;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String r) {
            /*progressBar.setVisibility(View.GONE);*/
            // Toast.makeText(LoginActivity.this, r, Toast.LENGTH_SHORT).show();
            if (isSuccess) {
                //ONPOSTEXECUTE, IF ISSUCCESS, SHOW TOAST MESSAGE
                sharedPreference.storeData("username", usercode);
                /*sharedPreference.storeData("USER_NAME", username);*/
                Toast.makeText(LoginActivity.this, "Login Successfull", Toast.LENGTH_LONG).show();
                //ONPOSTEXECUTE, IF ISSUCCESS, GO TO MAIN ACTIVITY
                Intent gotomain = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(gotomain);
            }
        }


        //DO IN BACKGROUOND, MAKE QUERY, CALL CONNECTION CLASS, RETURN VALUE OF Z AND ISSUCCESS
        @Override
        protected String doInBackground(String... params) {
            if (usercode.trim().equals("") || password.trim().equals(""))
                z = "Please enter Username/StoreCode and Password";
            else {
                try {
                    Connection conn = connectionClass.CONN();
                    if (conn == null) {
                        z = "Check Your Internet Access!";
                    } else {
                        String query = "EXEC DB_A4A292_RGL.dbo.SP_GET_LOGIN '"+usercode+"','"+ password+"'";
                        //String query = "EXEC ANDROIDXP_ENJI.dbo.SP_GET_LOGIN '"+usercode+"','"+ password+"'";
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(query);
                        if (rs.next()) {
                            z = "Login successful";
                            username = rs.getString("USER_ID");
                            isSuccess = true;
                            //con.close();
                        } else {
                            z = "Invalid Credentials!";
                            isSuccess = false;
                        }
                    }
                } catch (Exception ex) {
                    isSuccess = false;
                    z = ex.getMessage();
                }
            }
            return z;
        }
    }

}
