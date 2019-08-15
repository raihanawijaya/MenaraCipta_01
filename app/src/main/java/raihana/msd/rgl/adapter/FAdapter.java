package raihana.msd.rgl.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import raihana.msd.rgl.BDetailActivity;
import raihana.msd.rgl.R;
import raihana.msd.rgl.model.EightColumnClass;

//1. RV Adapter //3.Implement method
public class FAdapter extends RecyclerView.Adapter<FAdapter.MyViewHolder> {

    //4. Declaration
    Context mcontext;
    List<EightColumnClass> mData;

    //5.Constructor
    public FAdapter(Context mcontext) {
        this.mcontext = mcontext;
        mData = new ArrayList<>();
    }

    public void setmData(List<EightColumnClass> mData) {
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //6. Make return MyViewHoler(view = layout inflatter)
        View v;
        v = LayoutInflater.from(mcontext).inflate(R.layout.items_f,viewGroup,false);
        MyViewHolder vHolder = new MyViewHolder(v);
        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int i) {
        //9. get data position to viewHolder
        final EightColumnClass EightColumnClass = mData.get(i);
        int TrxStatus;
        viewHolder.tvColumn_1.setText(EightColumnClass.getColumn_1());
        viewHolder.tvColumn_2.setText(EightColumnClass.getColumn_2());
        viewHolder.tvColumn_3.setText(EightColumnClass.getColumn_3());
        viewHolder.tvColumn_4.setText(EightColumnClass.getColumn_4());
        viewHolder.tvColumn_5.setText(EightColumnClass.getColumn_5());
        viewHolder.tvColumn_6.setText(EightColumnClass.getColumn_6());
        viewHolder.tvColumn_8.setText(EightColumnClass.getColumn_8());

        /*
        TrxStatus =  Integer.parseInt(mData.get(i).getColumn_7());
        if (TrxStatus == 1) {
            viewHolder.lay_item_e.setBackgroundColor(Color.parseColor("#f7e2a6"));
        };
        if (TrxStatus == 3) {
            viewHolder.lay_item_e.setBackgroundColor(Color.parseColor("#abf4d2"));
        };
        if (TrxStatus == 2) {
             viewHolder.lay_item_e.setBackgroundColor(Color.parseColor("#fadc89"));
        };
        if (TrxStatus == 4) {
            viewHolder.lay_item_e.setBackgroundColor(Color.parseColor("#52cec6"));
        };*/


        viewHolder.lay_item_f.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent go_to_b_detail = new Intent(mcontext, BDetailActivity.class);
                go_to_b_detail.putExtra("onDate",EightColumnClass.getColumn_1());
                go_to_b_detail.putExtra("noTrx",EightColumnClass.getColumn_5());
                go_to_b_detail.putExtra("storeName",EightColumnClass.getColumn_2());
                go_to_b_detail.putExtra("storeCode",EightColumnClass.getColumn_6());
                mcontext.startActivity(go_to_b_detail);
            }
        });
    }

    @Override
    public int getItemCount() {
        //7. size of data List<>
        return mData.size();
    }

    //2. ViewHolder -> Implement method
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        //8. fill in itemView
        private LinearLayout lay_item_f;
        private TextView tvColumn_1,tvColumn_2,tvColumn_3,tvColumn_4,tvColumn_5,tvColumn_6,tvColumn_8;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            lay_item_f = itemView.findViewById(R.id.lay_item_f);
            tvColumn_1 = itemView.findViewById(R.id.tv_column_1);
            tvColumn_2 = itemView.findViewById(R.id.tv_column_2);
            tvColumn_3 = itemView.findViewById(R.id.tv_column_3);
            tvColumn_4 = itemView.findViewById(R.id.tv_column_4);
            tvColumn_5 = itemView.findViewById(R.id.tv_column_5);
            tvColumn_6 = itemView.findViewById(R.id.tv_column_6);
            tvColumn_8 = itemView.findViewById(R.id.tv_column_8);
        }
    }

}
