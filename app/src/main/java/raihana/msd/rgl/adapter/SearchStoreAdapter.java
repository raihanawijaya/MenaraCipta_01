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
import raihana.msd.rgl.SearchArticleActivity;
import raihana.msd.rgl.SearchStoreActivity;
import raihana.msd.rgl.model.BDetailSearchClass;
import raihana.msd.rgl.model.SearchArticleClass;
import raihana.msd.rgl.model.SearchStoreClass;

public class SearchStoreAdapter extends RecyclerView.Adapter<SearchStoreAdapter.MyViewHolder> {

    Context mcontext;
    List<SearchStoreClass> mData;

    public SearchStoreAdapter(List<SearchStoreClass> list_detail_search_B, Context mcontext) {
        this.mcontext = mcontext;
        mData = list_detail_search_B;
    }

    public void setmData(List<SearchStoreClass> mData) {
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.items_search_store, parent, false);
        SearchStoreAdapter.MyViewHolder vh = new SearchStoreAdapter.MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        final SearchStoreClass model = mData.get(i);
        holder.storeCode.setText(model.getStoreCode());
        holder.storeName.setText(model.getStoreName());
        holder.address.setText(model.getAddress());
        holder.wilayah.setText(model.getWilayah());
        holder.lay_item_search_store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SearchStoreActivity)mcontext).onChildItemClicked(model);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        //8. fill in itemView
        private TextView storeCode, storeName, address, wilayah;
        private LinearLayout lay_item_search_store;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            lay_item_search_store = itemView.findViewById(R.id.lay_item_search_store);
            storeCode = itemView.findViewById(R.id.tv_store_code);
            storeName = itemView.findViewById(R.id.tv_store_name);
            address = itemView.findViewById(R.id.tv_address);
            wilayah = itemView.findViewById(R.id.tv_wilayah);
        }
    }
}
