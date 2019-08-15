package raihana.msd.rgl.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import raihana.msd.rgl.BDetailActivity;
import raihana.msd.rgl.R;
import raihana.msd.rgl.model.EightColumnClass;

//1. RV Adapter //3.Implement method
public class BDetailAdapter extends RecyclerView.Adapter<BDetailAdapter.MyViewHolder> {
    //4. Declaration
    Context mcontext;
    List<EightColumnClass> mData;

    public BDetailAdapter(List<EightColumnClass> list_detail_B, Context mcontext) {
        this.mcontext = mcontext;
        mData = list_detail_B;
    }

    public void setmData(List<EightColumnClass> mData) {
        this.mData = mData;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View v = inflater.inflate(R.layout.items_b_detail, viewGroup, false);
        MyViewHolder vh = new BDetailAdapter.MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int i) {
        final EightColumnClass EightColumnClass = mData.get(i);
        int TrxStatus;

        viewHolder.tvColumn1.setText(EightColumnClass.getColumn_1());
        viewHolder.tvColumn2.setText(EightColumnClass.getColumn_2());
        viewHolder.tvColumn3.setText(EightColumnClass.getColumn_3());
        viewHolder.tvColumn4.setText(EightColumnClass.getColumn_4());
        viewHolder.tvColumn5.setText(EightColumnClass.getColumn_5());
        viewHolder.tvColumn6.setText(EightColumnClass.getColumn_6());
        viewHolder.tvColumn7.setText(EightColumnClass.getColumn_7());
        viewHolder.tvColumn8.setText(EightColumnClass.getColumn_8());
      //  viewHolder.lay_item_b_detail.setBackgroundColor(Color.parseColor("#fff7e0"));

        TrxStatus =  Integer.parseInt(mData.get(i).getColumn_7());
        if (TrxStatus == 1) {
            viewHolder.tvColumn8.setTextColor(Color.BLUE);
            viewHolder.lay_item_b_detail.setBackgroundColor(Color.parseColor("#fff7e0"));
        };
        if (TrxStatus == 2) {
            viewHolder.tvColumn8.setTextColor(Color.RED);
            viewHolder.lay_item_b_detail.setBackgroundColor(Color.parseColor("#D81B60"));
        };

        viewHolder.lay_item_b_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mcontext instanceof BDetailActivity)
                    ((BDetailActivity) mcontext).setData(EightColumnClass);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        //8. fill in itemView
        private TextView tvColumn1,tvColumn2,tvColumn3,tvColumn4,tvColumn5,tvColumn6,tvColumn7,tvColumn8;
        private LinearLayout lay_item_b_detail;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            lay_item_b_detail = itemView.findViewById(R.id.lay_item_b_detail);
            tvColumn1 = itemView.findViewById(R.id.tv_column_1);
            tvColumn2= itemView.findViewById(R.id.tv_column_2);
            tvColumn3 = itemView.findViewById(R.id.tv_column_3);
            tvColumn4 = itemView.findViewById(R.id.tv_column_4);
            tvColumn5 = itemView.findViewById(R.id.tv_column_5);
            tvColumn6 = itemView.findViewById(R.id.tv_column_6);
            tvColumn7 = itemView.findViewById(R.id.tv_column_7);
            tvColumn8 = itemView.findViewById(R.id.tv_column_8);

        }
    }
}
