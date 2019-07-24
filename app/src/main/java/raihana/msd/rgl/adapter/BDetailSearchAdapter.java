package raihana.msd.rgl.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import raihana.msd.rgl.BDetailSearchActivity;
import raihana.msd.rgl.R;
import raihana.msd.rgl.model.BDetailSearchClass;

public class BDetailSearchAdapter extends RecyclerView.Adapter<BDetailSearchAdapter.MyViewHolder> {

    Context mcontext;
    List<BDetailSearchClass> mData;

    public BDetailSearchAdapter(List<BDetailSearchClass> list_detail_search_B, Context mcontext) {
        this.mcontext = mcontext;
        mData = list_detail_search_B;
    }

    public void setmData(List<BDetailSearchClass> mData) {
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.items_b_search, parent, false);
        BDetailSearchAdapter.MyViewHolder vh = new BDetailSearchAdapter.MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        final BDetailSearchClass bDetailSearchClass = mData.get(i);
        holder.tvArticle.setText(bDetailSearchClass.getArticle());
        holder.tvUniqueId.setText(bDetailSearchClass.getUniqueID());
        holder.tvTrxNo.setText(bDetailSearchClass.getTrxNo());
        holder.tvPrice.setText(bDetailSearchClass.getPrice());
        holder.lay_item_b_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BDetailSearchActivity)mcontext).onChildItemClicked(bDetailSearchClass);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        //8. fill in itemView
        private TextView tvArticle, tvUniqueId,tvTrxNo, tvPrice;
        private LinearLayout lay_item_b_search;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            lay_item_b_search = itemView.findViewById(R.id.lay_item_b_search);
            tvArticle = itemView.findViewById(R.id.tv_article);
            tvUniqueId = itemView.findViewById(R.id.tv_unique_id);
            tvTrxNo = itemView.findViewById(R.id.tv_trx_no);
            tvPrice = itemView.findViewById(R.id.tv_price);
        }
    }
}
