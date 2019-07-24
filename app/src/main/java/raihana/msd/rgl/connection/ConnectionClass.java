package raihana.msd.rgl.connection;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionClass {
    @SuppressLint("NewApi")
    public Connection CONN()
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String ConnURL = null;
        try
        {
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
           ConnURL = "jdbc:jtds:sqlserver://sql5042.site4now.net;database=DB_A4A292_RGL;user=DB_A4A292_RGL_admin;password=rgl12345;Network Protocol=NamedPipes" ;
           // ConnURL = "jdbc:jtds:sqlserver://161.202.180.154;database=ANDROIDXP_ENJI;instance=SQLEXPRESS;user=menara;password=menara;Network Protocol=NamedPipes" ;
            //ConnectionURL = "jdbc:jtds:sqlserver://161.202.180.154;database=ANDRO;user=menara;password=menara";
            conn = DriverManager.getConnection(ConnURL);
        }
        catch (SQLException se)
        {
            Log.e("error here 1 : ", se.getMessage());
        }
        catch (ClassNotFoundException e)
        {
            Log.e("error here 2 : ", e.getMessage());
        }
        catch (Exception e)
        {
            Log.e("error here 3 : ", e.getMessage());
        }
        return conn;
    }
}
