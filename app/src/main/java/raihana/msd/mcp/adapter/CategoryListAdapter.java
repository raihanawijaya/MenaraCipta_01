package raihana.msd.mcp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import raihana.msd.mcp.OrderActivity;
import raihana.msd.mcp.R;
import raihana.msd.mcp.model.EightColumnClass;

//1. RV Adapter //3.Implement method
public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.MyViewHolder> {
    //4. Declaration
    Context mcontext;
    List<EightColumnClass> mData;


    //5.Constructor
    public CategoryListAdapter(List<EightColumnClass> MyRecordset, Context mcontext) {
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
        v = LayoutInflater.from(mcontext).inflate(R.layout.items_category_list,viewGroup,false);
        MyViewHolder vHolder = new MyViewHolder(v);
        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int i) {
        //9. get data position to viewHolder
        final EightColumnClass EightColumnClass = mData.get(i);
        String TrxStatus;

        viewHolder.tv_column_1.setText(EightColumnClass.getColumn_1());

        viewHolder.lay_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mcontext instanceof OrderActivity)
                    ((OrderActivity) mcontext).setDataToCtgrId(EightColumnClass);
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
        private TextView tv_column_1,tv_column_2,tv_column_3,tv_column_4,tv_column_5,tv_column_6,tv_column_8;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            lay_item = itemView.findViewById(R.id.lay_item);
            tv_column_1 = itemView.findViewById(R.id.tv_column_1);
            tv_column_2 = itemView.findViewById(R.id.tv_column_2);
            tv_column_3 = itemView.findViewById(R.id.tv_column_3);
            tv_column_4 = itemView.findViewById(R.id.tv_column_4);
            tv_column_5 = itemView.findViewById(R.id.tv_column_5);
            tv_column_6 = itemView.findViewById(R.id.tv_column_6);
            tv_column_8 = itemView.findViewById(R.id.tv_column_8);
        }
    }

}
