package raihana.msd.rgl.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import raihana.msd.rgl.BDetailActivity;
import raihana.msd.rgl.R;
import raihana.msd.rgl.model.BDetailClass;

//1. RV Adapter //3.Implement method
public class BDetailAdapter extends RecyclerView.Adapter<BDetailAdapter.MyViewHolder> {
    //4. Declaration
    Context mcontext;
    List<BDetailClass> mData;

    public BDetailAdapter(List<BDetailClass> list_detail_B, Context mcontext) {
        this.mcontext = mcontext;
        mData = list_detail_B;
    }

    public void setmData(List<BDetailClass> mData) {
        this.mData = mData;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View v = inflater.inflate(R.layout.items_b_detail, viewGroup, false);
        MyViewHolder vh = new BDetailAdapter.MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int i) {
        final BDetailClass bDetailClass = mData.get(i);
        viewHolder.article.setText(bDetailClass.getArticle());
        viewHolder.qty.setText(bDetailClass.getQty());
        viewHolder.price.setText(bDetailClass.getPrice());
        viewHolder.gross.setText(bDetailClass.getGross());
        viewHolder.lay_item_b_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mcontext instanceof BDetailActivity)
                    ((BDetailActivity) mcontext).setData(bDetailClass);
            }
        });
        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(mcontext)
                        .setTitle("Confirm?")
                        .setMessage("Apakah anda ingin menghapus no order ini?")
                        .setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                               /* BDetailActivity.InputNewSales inputNewSales = new BDetailActivity.InputNewSales();// this is the Asynctask, which is used to process in background to reduce load on app process
                                inputNewSales.execute("");
                                etTrxDate.setText(sharedPreference.getObjectData("today", String.class));
                                setDataEmpty();*/
                            }
                        })
                        .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        //8. fill in itemView
        private TextView article, qty, price, gross, delete;
        private LinearLayout lay_item_b_detail;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            lay_item_b_detail = itemView.findViewById(R.id.lay_item_b_detail);
            article = itemView.findViewById(R.id.tv_article);
            qty= itemView.findViewById(R.id.tv_qty);
            price = itemView.findViewById(R.id.tv_price);
            gross = itemView.findViewById(R.id.tv_gross);
            delete = itemView.findViewById(R.id.tv_delete_item_b_detail);
        }
    }
}
