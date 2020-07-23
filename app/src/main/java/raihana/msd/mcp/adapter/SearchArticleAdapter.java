package raihana.msd.mcp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
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
import raihana.msd.mcp.SearchArticleActivity;
import raihana.msd.mcp.model.TwentyColumnClass;

//1. RV Adapter //3.Implement method
public class SearchArticleAdapter extends RecyclerView.Adapter<SearchArticleAdapter.MyViewHolder> {

    //4. Declaration
    Context mcontext;
    List<TwentyColumnClass> mData;
    SearchArticleActivity MyActivity;

    //5.Constructor
    public SearchArticleAdapter(List<TwentyColumnClass> myRecordset, Context mcontext, SearchArticleActivity myActivity) {
        this.mcontext = mcontext;
        this.MyActivity = myActivity;
        mData = myRecordset;
    }

    public void setmData(List<TwentyColumnClass> mData) {
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //6. Make return MyViewHoler(view = layout inflatter)
        View v;
        v = LayoutInflater.from(mcontext).inflate(R.layout.items_search_article,viewGroup,false);
        MyViewHolder vHolder = new MyViewHolder(v);
        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, final int i) {
        //9. get data position to viewHolder
        final TwentyColumnClass Column = mData.get(i);
        String image = mData.get(i).getColumn_11();
        String new_arrival =  mData.get(i).getColumn_13();

        viewHolder.tv_column_1.setText(Column.getColumn_1());
        viewHolder.tv_column_2.setText(Column.getColumn_2());
        viewHolder.tv_column_2.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        viewHolder.tv_column_3.setText(Column.getColumn_3());
        viewHolder.tv_column_4.setText(Column.getColumn_4());
        viewHolder.tv_column_5.setText(Column.getColumn_5());
        viewHolder.tv_column_6.setText(Column.getColumn_6());
        viewHolder.tv_column_7.setText(Column.getColumn_7());
        viewHolder.tv_column_8.setText(Column.getColumn_8());
        viewHolder.tv_column_9.setText(Column.getColumn_9());
        viewHolder.tv_column_10.setText(Column.getColumn_10());
        viewHolder.iv_new_arrival.setVisibility(View.INVISIBLE);

        if(new_arrival.equals("1")){
            viewHolder.iv_new_arrival.setVisibility(View.VISIBLE);
        };

//        viewHolder.lay_item.setBackgroundColor(Color.parseColor(Column.getColumn_12()));

      //  viewHolder.tv_column_6.setBackgroundColor(Color.parseColor(EightColumnClass.getColumn_6()));
      //  viewHolder.tv_column_7.setBackgroundColor(Color.parseColor(EightColumnClass.getColumn_7()));


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
            viewHolder.iv_image.setImageBitmap(decodebitmap);
        }

        catch(Exception e){
            System.out.println(e.getMessage());
        }


        viewHolder.tv_column_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              MyActivity.SendData(Column.getColumn_1(),Column.getColumn_3(),Column.getColumn_4());

               //((SearchArticleActivity) mcontext).onChildItemClicked(Column);

           }
        });
        viewHolder.tv_column_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyActivity.SendData(Column.getColumn_1(),Column.getColumn_5(),Column.getColumn_6());


            }
        });

        viewHolder.tv_column_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyActivity.SendData(Column.getColumn_1(),Column.getColumn_7(),Column.getColumn_8());

            }
        });

        viewHolder.tv_column_9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyActivity.SendData(Column.getColumn_1(),Column.getColumn_9(),Column.getColumn_10());

            }
        });
/*
        if (TrxStatus.equals("1")) {
            viewHolder.lay_item.setBackgroundResource(R.drawable.bg_for_item_3);
        };

        if (TrxStatus.equals("2")) {
            viewHolder.lay_item.setBackgroundResource(R.drawable.bg_for_item_4);
        };

        if (TrxStatus.equals("3")) {
            viewHolder.lay_item.setBackgroundResource(R.drawable.bg_for_item_7);
        }; */
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
        private ImageView iv_image,iv_new_arrival;
        private TextView tv_column_1,tv_column_2,tv_column_3,tv_column_4,tv_column_5,tv_column_6,tv_column_7,tv_column_8,tv_column_9,tv_column_10;
        private TextView tv_column_11,tv_column_12,tv_column_13,tv_column_14,tv_column_15,tv_column_16,tv_column_17,tv_column_18,tv_column_19,tv_column_20;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_image = itemView.findViewById(R.id.iv_bag_image);
            iv_new_arrival = itemView.findViewById(R.id.iv_new_arrival);
            lay_item = itemView.findViewById(R.id.lay_item_a);
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
        }
    }

}
