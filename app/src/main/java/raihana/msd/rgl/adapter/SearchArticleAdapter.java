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
import raihana.msd.rgl.model.BDetailSearchClass;
import raihana.msd.rgl.model.SearchArticleClass;

public class SearchArticleAdapter extends RecyclerView.Adapter<SearchArticleAdapter.MyViewHolder> {

    Context mcontext;
    List<SearchArticleClass> mData;

    public SearchArticleAdapter(List<SearchArticleClass> list_detail_search_B, Context mcontext) {
        this.mcontext = mcontext;
        mData = list_detail_search_B;
    }

    public void setmData(List<SearchArticleClass> mData) {
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.items_search_article, parent, false);
        SearchArticleAdapter.MyViewHolder vh = new SearchArticleAdapter.MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        final SearchArticleClass searchArticleClass = mData.get(i);
        holder.article.setText(searchArticleClass.getArticle());
        holder.description.setText(searchArticleClass.getDescription());
        holder.brand.setText(searchArticleClass.getBrand());
        holder.price.setText(searchArticleClass.getPrice());
        holder.stock.setText(searchArticleClass.getStock());
        holder.lay_item_search_article.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SearchArticleActivity)mcontext).onChildItemClicked(searchArticleClass);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        //8. fill in itemView
        private TextView article, description, brand, price, stock;
        private LinearLayout lay_item_search_article;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            lay_item_search_article = itemView.findViewById(R.id.lay_item_search_article);
            article = itemView.findViewById(R.id.tv_article);
            description = itemView.findViewById(R.id.tv_desc);
            brand = itemView.findViewById(R.id.tv_brand);
            price = itemView.findViewById(R.id.tv_price);
            stock = itemView.findViewById(R.id.tv_stock);
        }
    }
}
