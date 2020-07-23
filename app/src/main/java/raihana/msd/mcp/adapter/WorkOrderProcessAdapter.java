package raihana.msd.mcp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import raihana.msd.mcp.Activity_N_Detail;
import raihana.msd.mcp.R;
import raihana.msd.mcp.WorkOrderProcessActivity;
import raihana.msd.mcp.model.EightColumnClass;
import raihana.msd.mcp.model.TwentyColumnClass;

//1. RV Adapter //3.Implement method
public class WorkOrderProcessAdapter extends RecyclerView.Adapter<WorkOrderProcessAdapter.MyViewHolder> {

    //4. Declaration
    Context mcontext;
    List<TwentyColumnClass> mData;

    //5.Constructor
    public WorkOrderProcessAdapter(List<TwentyColumnClass> MyRecordset, Context mcontext) {
        this.mcontext = mcontext;
        mData = MyRecordset;
    }

    public void setmData(List<TwentyColumnClass> mData) {
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //6. Make return MyViewHoler(view = layout inflatter)
        View v;
        v = LayoutInflater.from(mcontext).inflate(R.layout.items_work_order_process,viewGroup,false);
        MyViewHolder vHolder = new MyViewHolder(v);
        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int i) {
        //9. get data position to viewHolder
        final TwentyColumnClass Column = mData.get(i);
        String TrxStatus;
        viewHolder.tv_column_1.setText(Column.getColumn_1());
        viewHolder.tv_column_2.setText(Column.getColumn_2());
        viewHolder.tv_column_3.setText(Column.getColumn_3());
        viewHolder.tv_column_4.setText(Column.getColumn_4());
        viewHolder.tv_column_5.setText(Column.getColumn_5());
        viewHolder.tv_column_6.setText(Column.getColumn_6());
        viewHolder.tv_column_7.setText(Column.getColumn_7());
        viewHolder.tv_column_8.setText(Column.getColumn_8());
        viewHolder.tv_column_9.setText(Column.getColumn_9());
        viewHolder.tv_column_10.setText(Column.getColumn_10());
        viewHolder.tv_column_11.setText(Column.getColumn_11());
        viewHolder.tv_column_12.setText(Column.getColumn_12());
        viewHolder.tv_column_13.setText(Column.getColumn_13());
        viewHolder.tv_column_4.setTypeface(Typeface.DEFAULT);
        viewHolder.tv_column_2.setTypeface(Typeface.DEFAULT);

        if (Column.getColumn_1().equals("ALL")) {
            viewHolder.tv_column_4.setTypeface(Typeface.DEFAULT_BOLD);
            viewHolder.tv_column_2.setTypeface(Typeface.DEFAULT_BOLD);
        };

/*
        viewHolder.lay_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mcontext instanceof WorkOrderProcessActivity)
                    ((WorkOrderProcessActivity) mcontext).onChildItemClicked(Column);
            }
        });
*/
        viewHolder.btn_pilih.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mcontext instanceof WorkOrderProcessActivity)
                    ((WorkOrderProcessActivity) mcontext).onChildItemClicked(Column);
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
        private LinearLayout lay_item;
        private Button btn_pilih;
        private TextView tv_column_1,tv_column_2,tv_column_3,tv_column_4,tv_column_5,tv_column_6,tv_column_7,tv_column_8;
        private TextView tv_column_9,tv_column_10,tv_column_11,tv_column_12,tv_column_13;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            lay_item = itemView.findViewById(R.id.lay_item);
            btn_pilih =  itemView.findViewById(R.id.btn_pilih);
            tv_column_1 = itemView.findViewById(R.id.tv_column_1);
            tv_column_2 = itemView.findViewById(R.id.tv_column_2);
            tv_column_3 = itemView.findViewById(R.id.tv_column_3);
            tv_column_4 = itemView.findViewById(R.id.tv_column_4);
            tv_column_5 = itemView.findViewById(R.id.tv_column_5);
            tv_column_6 = itemView.findViewById(R.id.tv_column_6);
            tv_column_7 = itemView.findViewById(R.id.tv_column_7);
            tv_column_8 = itemView.findViewById(R.id.tv_column_8);
            tv_column_9 = itemView.findViewById(R.id.tv_column_9);
            tv_column_10 = itemView.findViewById(R.id.tv_column_10);
            tv_column_11 = itemView.findViewById(R.id.tv_column_11);
            tv_column_12 = itemView.findViewById(R.id.tv_column_12);
            tv_column_13 = itemView.findViewById(R.id.tv_column_13);
        }
    }

}
