package raihana.msd.mcp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import raihana.msd.mcp.R;
import raihana.msd.mcp.SearchNikCodeActivity;
import raihana.msd.mcp.model.EightColumnClass;

public class SearchNikCodeAdapter extends RecyclerView.Adapter<SearchNikCodeAdapter.MyViewHolder> {

    Context mcontext;
    List<EightColumnClass> mData;

    public SearchNikCodeAdapter(List<EightColumnClass> MyRecodset, Context mcontext) {
        this.mcontext = mcontext;
        mData = MyRecodset;
    }

    public void setmData(List<EightColumnClass> mData) {
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.items_search_nik, parent, false);
        SearchNikCodeAdapter.MyViewHolder vh = new SearchNikCodeAdapter.MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        final EightColumnClass Column = mData.get(i);
        String image = mData.get(i).getColumn_5();

        holder.tv_column_1.setText(Column.getColumn_1());
        holder.tv_column_2.setText(Column.getColumn_2());
        holder.tv_column_3.setText(Column.getColumn_3());
        holder.tv_column_4.setText(Column.getColumn_4());

        holder.lay_item_search_article.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SearchNikCodeActivity) mcontext).onChildItemClicked(Column);
            }
        });

        try{
            //proses perubahan dari hexString menjadi bytearray
            //image diambil dari database dengan tipe data IMAGE

            byte[] b = new byte[image.length() / 2];
            //image diambil dari database dengan tipe data IMAGE
            for (int x = 0; x < b.length; x++) {
                int index = x * 2;
                int v = Integer.parseInt(image.substring(index, index + 2), 16);
                b[x] = (byte) v;
            }
            Log.d("decode", String.valueOf(b));
            //  BitmapFactory.Options options = new BitmapFactory.Options();
            Bitmap decodebitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            holder.iv_image.setImageBitmap(decodebitmap);
        }

        catch(Exception e){
            System.out.println(e.getMessage());
        }
    };

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        //8. fill in itemView
        private TextView tv_column_1,tv_column_2,tv_column_3,tv_column_4,tv_column_5;
        private LinearLayout lay_item_search_article;
        private ImageView iv_image;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            lay_item_search_article = itemView.findViewById(R.id.lay_item_search_article);
            iv_image = itemView.findViewById(R.id.iv_bag_image);
            tv_column_1 = itemView.findViewById(R.id.tv_column_1);
            tv_column_2 = itemView.findViewById(R.id.tv_column_2);
            tv_column_3 = itemView.findViewById(R.id.tv_column_3);
            tv_column_4 = itemView.findViewById(R.id.tv_column_4);
            tv_column_5 = itemView.findViewById(R.id.tv_column_5);
        }
    }
}
