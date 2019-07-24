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
import raihana.msd.rgl.model.AClass;

//1. RV Adapter //3.Implement method
public class AAdapter extends RecyclerView.Adapter<AAdapter.MyViewHolder> {

    //4. Declaration
    Context mcontext;
    List<AClass> mData;

    //5.Constructor
    public AAdapter(Context mcontext) {
        this.mcontext = mcontext;
        mData = new ArrayList<>();
    }

    public void setmData(List<AClass> mData) {
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
        final AClass aClass = mData.get(i);
        viewHolder.tvTrxNo.setText(mData.get(i).getTrxNo());
        viewHolder.tvTrxDate.setText(mData.get(i).getTrxDate());
        viewHolder.tvStoreName.setText(mData.get(i).getStoreName());
        viewHolder.tvStoreCode.setText(mData.get(i).getStoreCode());
        viewHolder.tvQty.setText(mData.get(i).getQty());
        viewHolder.tvGross.setText(mData.get(i).getGross());
        viewHolder.lay_item_a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent go_to_b_detail = new Intent(mcontext, BDetailActivity.class);
                go_to_b_detail.putExtra("onDate",aClass.getTrxDate());
                go_to_b_detail.putExtra("noTrx",aClass.getTrxNo());
                go_to_b_detail.putExtra("storeName",aClass.getStoreName());
                go_to_b_detail.putExtra("storeCode",aClass.getStoreCode());
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
        private LinearLayout lay_item_a;
        private TextView tvTrxNo, tvTrxDate, tvStoreCode, tvStoreName, tvQty, tvGross;;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            lay_item_a = itemView.findViewById(R.id.lay_item_a);
            tvTrxDate = itemView.findViewById(R.id.tv_trx_date);
            tvTrxNo = itemView.findViewById(R.id.tv_trx_no);
            tvStoreCode = itemView.findViewById(R.id.tv_store_code);
            tvStoreName = itemView.findViewById(R.id.tv_store_name);
            tvQty = itemView.findViewById(R.id.tv_qty);
            tvGross = itemView.findViewById(R.id.tv_gross);
        }
    }

}
