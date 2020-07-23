package raihana.msd.mcp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import raihana.msd.mcp.R;
import raihana.msd.mcp.fragment.AFragment;
import raihana.msd.mcp.fragment.EFragment;
import raihana.msd.mcp.model.EightColumnClass;

//1. RV Adapter //3.Implement method
public class AAdapter extends RecyclerView.Adapter<AAdapter.MyViewHolder> {

    //4. Declaration
    Context mcontext;
    List<EightColumnClass> mData;
    AFragment afragment= new AFragment();
    //5.Constructor
    public AAdapter(Context mcontext, AFragment fragment) {
        this.mcontext = mcontext;
        mData = new ArrayList<>();
        this.afragment = fragment;
    }

    public void setmData(List<EightColumnClass> mData) {
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //6. Make return MyViewHoler(view = layout inflatter)
        View v;
        v = LayoutInflater.from(mcontext).inflate(R.layout.items_a,viewGroup,false);
        MyViewHolder vHolder = new MyViewHolder(v);
        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int i) {
        //9. get data position to viewHolder
        final EightColumnClass EightColumnClass = mData.get(i);
        final String TrxStatus;
        final String myImage;

        myImage = EightColumnClass.getColumn_6();
        TrxStatus = EightColumnClass.getColumn_2();
        viewHolder.tv_column_1.setText(EightColumnClass.getColumn_1()); //WO
        viewHolder.tv_column_2.setText(EightColumnClass.getColumn_2()); //Produk name
/*
        viewHolder.lay_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                afragment.setDataToHeaderFragment(EightColumnClass);
            }
        });
*/

        viewHolder.btn_pilih.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                afragment.setDataToHeaderFragment(EightColumnClass);
            }
        });
        /*
        viewHolder.lay_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TrxStatus.equals("BOOKED")){
                    Intent go_to_order = new Intent(mcontext,  OrderActivity.class);
                    go_to_order.putExtra("ORD_NO",EightColumnClass.getColumn_3());
                    go_to_order.putExtra("TABLE_ID",EightColumnClass.getColumn_1());
                    go_to_order.putExtra("TABLE_STATUS","BOOKED");
                    mcontext.startActivity(go_to_order);
                }

                if (TrxStatus.equals("OPEN")){
                    Intent go_to_order = new Intent(mcontext, OrderActivity.class);
                    go_to_order.putExtra("TABLE_ID",EightColumnClass.getColumn_1());
                    go_to_order.putExtra("TABLE_STATUS","OPEN");
                    mcontext.startActivity(go_to_order);
                }
          }
        });*/
    }

    @Override
    public int getItemCount() {
        //7. size of data List<>
        return mData.size();
    }

    //2. ViewHolder -> Implement method
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        //8. fill in itemView
        private ImageView ivArahGulungan;
        private Button btn_pilih;
        private LinearLayout lay_item;
        private TextView tv_column_1,tv_column_2,tv_column_3,tv_column_4,tv_column_5,tv_column_6,tv_column_7,tv_column_8;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            lay_item = itemView.findViewById(R.id.lay_item);
            btn_pilih = itemView.findViewById(R.id.btn_pilih);
            ivArahGulungan = itemView.findViewById(R.id.iv_arah_gulungan);
            tv_column_1 = itemView.findViewById(R.id.tv_column_1);
            tv_column_2 = itemView.findViewById(R.id.tv_column_2);
            tv_column_3 = itemView.findViewById(R.id.tv_column_3);
            tv_column_4 = itemView.findViewById(R.id.tv_column_4);
            tv_column_5 = itemView.findViewById(R.id.tv_column_5);
            tv_column_6 = itemView.findViewById(R.id.tv_column_6);
            tv_column_7 = itemView.findViewById(R.id.tv_column_7);
            tv_column_8 = itemView.findViewById(R.id.tv_column_8);
        }
    }

}
