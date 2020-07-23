package raihana.msd.mcp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import raihana.msd.mcp.R;
import raihana.msd.mcp.WorkOrderForMachineCodeActivity;
import raihana.msd.mcp.WorkOrderProcessActivity;
import raihana.msd.mcp.model.EightColumnClass;
import raihana.msd.mcp.model.TwentyColumnClass;

//1. RV Adapter //3.Implement method
public class WorkOrderForMachineCodeAdapter extends RecyclerView.Adapter<WorkOrderForMachineCodeAdapter.MyViewHolder> {

    //4. Declaration
    Context mcontext;
    List<EightColumnClass> mData;

    //5.Constructor
    public WorkOrderForMachineCodeAdapter(List<EightColumnClass> MyRecordset, Context mcontext) {
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
        v = LayoutInflater.from(mcontext).inflate(R.layout.items_work_order_for_machine_code,viewGroup,false);
        MyViewHolder vHolder = new MyViewHolder(v);
        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int i) {
        //9. get data position to viewHolder
        final EightColumnClass Column = mData.get(i);
        String TrxStatus,myImage;
        myImage = Column.getColumn_6();
        TrxStatus = Column.getColumn_3();

        viewHolder.tv_column_1.setText(Column.getColumn_1());
        viewHolder.tv_column_2.setText(Column.getColumn_2());
        viewHolder.tv_column_3.setText(Column.getColumn_3());
        viewHolder.tv_column_4.setText(Column.getColumn_4());
        viewHolder.tv_column_5.setText(Column.getColumn_5());
//        viewHolder.tv_column_6.setText(Column.getColumn_6());
        viewHolder.tv_column_7.setText(Column.getColumn_7());
        viewHolder.tv_column_8.setText(Column.getColumn_8());

        viewHolder.lay_item.setBackgroundResource(R.drawable.bg_layout_white);

        if(TrxStatus.equals("BARU")){
            viewHolder.lay_item.setBackgroundResource(R.drawable.bg_layout_print_02);
        }

        try {
            String image = myImage;
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
            viewHolder.ivArahGulungan.setImageBitmap(decodebitmap);

        } catch (Exception ex) {
        }

/*
        viewHolder.lay_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mcontext instanceof WorkOrderForMachineCodeActivity)
                    ((WorkOrderForMachineCodeActivity) mcontext).onChildItemClicked(Column);
            }
        });
*/

        viewHolder.btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mcontext instanceof WorkOrderForMachineCodeActivity)
                    ((WorkOrderForMachineCodeActivity) mcontext).onChildItemClicked(Column);
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
        private Button btn_next;
        private ImageView ivArahGulungan;
        private TextView tv_column_1,tv_column_2,tv_column_3,tv_column_4,tv_column_5,tv_column_6,tv_column_7,tv_column_8;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            lay_item = itemView.findViewById(R.id.lay_item);
            btn_next = itemView.findViewById(R.id.btn_next);
            ivArahGulungan = itemView.findViewById(R.id.iv_arah_gulungan);
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
