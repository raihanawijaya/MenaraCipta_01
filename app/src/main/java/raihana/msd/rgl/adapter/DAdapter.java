package raihana.msd.rgl.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import raihana.msd.rgl.R;
import raihana.msd.rgl.model.EightColumnClass;

//1. RV Adapter //3.Implement method
public class DAdapter extends RecyclerView.Adapter<DAdapter.MyViewHolder> {

    //4. Declaration
    Context mcontext;
    List<EightColumnClass> mData;

    //5.Constructor
    public DAdapter(Context mcontext) {
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
        v = LayoutInflater.from(mcontext).inflate(R.layout.items_d  ,viewGroup,false);
        MyViewHolder vHolder = new MyViewHolder(v);
        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int i) {
        //9. get data position to viewHolder
        final EightColumnClass EightColumnClass = mData.get(i);
        int Flag;

        viewHolder.tvColumn_1.setText(mData.get(i).getColumn_1());
        viewHolder.tvColumn_2.setText(mData.get(i).getColumn_2());
        viewHolder.tvColumn_3.setText(mData.get(i).getColumn_3());
        viewHolder.tvColumn_4.setText(mData.get(i).getColumn_4());

        viewHolder.tvColumn_1.setTextColor(Color.BLACK);
        viewHolder.tvColumn_1.setTypeface(null, Typeface.NORMAL);
        viewHolder.tvColumn_2.setTextColor(Color.BLACK);
        viewHolder.tvColumn_2.setTypeface(null, Typeface.NORMAL);
        viewHolder.tvColumn_3.setTextColor(Color.BLACK);
        viewHolder.tvColumn_3.setTypeface(null, Typeface.NORMAL);
        viewHolder.tvColumn_4.setTextColor(Color.BLACK);
        viewHolder.tvColumn_4.setTypeface(null, Typeface.NORMAL);
        viewHolder.lay_item_d.setBackgroundColor(Color.parseColor("#ffb5b0"));
        /*
        viewHolder.lay_item_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent go_to_b_detail = new Intent(mcontext, BDetailActivity.class);
                go_to_b_detail.putExtra("onDate",bClass.getTrxDate());
                mcontext.startActivity(go_to_b_detail);
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
        private LinearLayout lay_item_d;
        private TextView tvColumn_1, tvColumn_2, tvColumn_3, tvColumn_4;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            lay_item_d = itemView.findViewById(R.id.lay_item_d);
            tvColumn_1 = itemView.findViewById(R.id.tv_column_1);
            tvColumn_2 = itemView.findViewById(R.id.tv_column_2);
            tvColumn_3= itemView.findViewById(R.id.tv_column_3);
            tvColumn_4= itemView.findViewById(R.id.tv_column_4);
        }
    }

}
