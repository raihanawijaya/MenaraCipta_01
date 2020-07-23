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

import raihana.msd.mcp.R;
import raihana.msd.mcp.SearchBankNameActivity;
import raihana.msd.mcp.model.OneColumnClass;

//1. RV Adapter //3.Implement method
public class SearchBankNameAdapter extends RecyclerView.Adapter<SearchBankNameAdapter.MyViewHolder> {

    //4. Declaration
    Context mcontext;
    List<OneColumnClass> mData;
    SearchBankNameActivity MyActivity;

    //5.Constructor
    public SearchBankNameAdapter(List<OneColumnClass> myRecordset, Context mcontext, SearchBankNameActivity myActivity) {
        this.mcontext = mcontext;
        this.MyActivity = myActivity;
        mData = myRecordset;
    }

    public void setmData(List<OneColumnClass> mData) {
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //6. Make return MyViewHoler(view = layout inflatter)
        View v;
        v = LayoutInflater.from(mcontext).inflate(R.layout.items_search_bank, viewGroup, false);
        MyViewHolder vHolder = new MyViewHolder(v);
        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, final int i) {
        //9. get data position to viewHolder
        final OneColumnClass Column = mData.get(i);

        viewHolder.tv_column_1.setText(Column.getColumn_1());

        viewHolder.lay_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyActivity.SendData(Column.getColumn_1());
            }
        });
    }


    @Override
    public int getItemCount() {
        //7. size of data List<>
        return mData.size();
    }

    //2. ViewHolder -> Implement method
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        //8. fill in itemView
        private LinearLayout lay_item;
        private TextView tv_column_1;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            lay_item = itemView.findViewById(R.id.lay_item_a);
            tv_column_1 = itemView.findViewById(R.id.tv_column_1);
        }
    }

}
