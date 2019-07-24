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
import raihana.msd.rgl.model.BClass;

//1. RV Adapter //3.Implement method
public class BAdapter extends RecyclerView.Adapter<BAdapter.MyViewHolder> {

    //4. Declaration
    Context mcontext;
    List<BClass> mData;

    //5.Constructor
    public BAdapter(Context mcontext) {
        this.mcontext = mcontext;
        mData = new ArrayList<>();
    }

    public void setmData(List<BClass> mData) {
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //6. Make return MyViewHoler(view = layout inflatter)
        View v;
        v = LayoutInflater.from(mcontext).inflate(R.layout.items_b,viewGroup,false);
        MyViewHolder vHolder = new MyViewHolder(v);
        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int i) {
        //9. get data position to viewHolder
        final BClass bClass = mData.get(i);
        viewHolder.tvTrxDate.setText(mData.get(i).getTrxDate());
        viewHolder.tvQty.setText(mData.get(i).getQty());
        viewHolder.tvGross.setText(mData.get(i).getGross());
        viewHolder.lay_item_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent go_to_b_detail = new Intent(mcontext, BDetailActivity.class);
                go_to_b_detail.putExtra("onDate",bClass.getTrxDate());
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
        private LinearLayout lay_item_b;
        private TextView tvTrxDate, tvQty, tvGross;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            lay_item_b = itemView.findViewById(R.id.lay_item_b);
            tvTrxDate = itemView.findViewById(R.id.tv_trx_date);
            tvQty = itemView.findViewById(R.id.tv_qty);
            tvGross = itemView.findViewById(R.id.tv_gross);
        }
    }

}
