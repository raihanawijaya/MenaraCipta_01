package raihana.msd.mcp.adapter;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import raihana.msd.mcp.R;
import raihana.msd.mcp.WorkOrderProcessLotIdActivity;
import raihana.msd.mcp.WorkOrderProcessLotIdPrintJobActivity;
import raihana.msd.mcp.model.EightColumnClass;

//1. RV Adapter //3.Implement method
public class WorkOrderProcessLotIdPrintJobAdapter extends RecyclerView.Adapter<WorkOrderProcessLotIdPrintJobAdapter.MyViewHolder> {

    //4. Declaration
    Context mcontext;
    List<EightColumnClass> mData;

    //5.Constructor
    public WorkOrderProcessLotIdPrintJobAdapter(List<EightColumnClass> MyRecordset, Context mcontext) {
        this.mcontext = mcontext;
        mData = MyRecordset;
    }

    public void setmData(List<EightColumnClass> mData) {
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //6. Make return MyViewHoler(view = layout inflatter)
        View v;
        v = LayoutInflater.from(mcontext).inflate(R.layout.items_work_order_process_lotid_print_job,viewGroup,false);
        MyViewHolder vHolder = new MyViewHolder(v);
        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int i) {
        //9. get data position to viewHolder
        final EightColumnClass Column = mData.get(i);
        String TrxStatus,JobCode;
        TrxStatus = Column.getColumn_6();
        JobCode = Column.getColumn_1();
        viewHolder.tv_column_1.setText(Column.getColumn_1());
        viewHolder.tv_column_2.setText(Column.getColumn_2());
        viewHolder.tv_column_3.setText(Column.getColumn_3());
        viewHolder.tv_column_4.setText(Column.getColumn_4());
        viewHolder.tv_column_5.setText(Column.getColumn_5());
        viewHolder.tv_column_7.setText(Column.getColumn_7());
   //     viewHolder.tv_column_8.setText(Column.getColumn_8());

        viewHolder.btn_Solved.setVisibility(View.INVISIBLE);
        if (JobCode.equals("7")||(JobCode.equals("3"))) {
            viewHolder.btn_Solved.setVisibility(View.VISIBLE);
        }

        viewHolder.lay_item.setBackgroundResource(R.drawable.bg_layout_white_01);

        if (TrxStatus.equals("A")){
            viewHolder.lay_item.setBackgroundResource(R.drawable.bg_layout_print_01);
        }

        if (TrxStatus.equals("B")){
            viewHolder.lay_item.setBackgroundResource(R.drawable.bg_layout_print_02);
        }

        if (TrxStatus.equals("C")){
            viewHolder.lay_item.setBackgroundResource(R.drawable.bg_layout_print_03);
        }
/*
        viewHolder.lay_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mcontext instanceof WorkOrderProcessLotIdPrintJobActivity)
                    ((WorkOrderProcessLotIdPrintJobActivity) mcontext).onChildItemClicked(Column);
            }
        });
*/
        viewHolder.btn_Solved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mcontext instanceof WorkOrderProcessLotIdPrintJobActivity)
                    ((WorkOrderProcessLotIdPrintJobActivity) mcontext).onChildItemClicked(Column);
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
        private Button btn_Solved;
        private TextView tv_column_1,tv_column_2,tv_column_3,tv_column_4,tv_column_5,tv_column_6,tv_column_7,tv_column_8;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            lay_item = itemView.findViewById(R.id.lay_item);
            btn_Solved = itemView.findViewById(R.id.btn_solved_hambatan);
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
