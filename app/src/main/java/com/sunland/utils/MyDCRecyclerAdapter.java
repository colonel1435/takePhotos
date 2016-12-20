package com.sunland.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.sunland.signalinspect.DCInfo;
import com.sunland.signalinspect.DepotActivity;
import com.sunland.signalinspect.DepotInfo;
import com.sunland.signalinspect.R;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.sunland.signalinspect.DepotActivity.DC_ITEM_KEY;
import static com.sunland.signalinspect.DepotActivity.DC_ITEM_SEP;

/**
 * Created by Zero on 2016/10/04.
 */
public class MyDCRecyclerAdapter extends RecyclerView.Adapter<MyDCRecyclerAdapter.ViewHolder> {

    private static final String TAG = "wumin";
    private ArrayMap<String, List<DCInfo>> mListDatas = null;
    private OnItemClickListener mListener;
    public  Context mContext;
    private String mDepot;
    private MyDCTurnoutRecyclerAdapter myDCTurnoutRecyclerAdapter;
    private ItemTouchHelper mDcItemTouchHelper;
    private PopupWindow popupWindow;
    public interface OnItemClickListener {
        void OnItemClick(View view, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }
    public MyDCRecyclerAdapter(ArrayMap<String, List<DCInfo>> list, Context context, String depot) {
        this.mDepot = depot;
        this.mContext = context;
        this.mListDatas = list;
    }
    @Override
    public MyDCRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.dc_item_turnout, parent, false);
            return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyDCRecyclerAdapter.ViewHolder holder, int position) {
        String dc = mListDatas.keyAt(position);
        holder.bindData(dc);
        holder.itemView.setTag(dc);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        holder.recyclerView.setItemAnimator(new DefaultItemAnimator());
        holder.recyclerView.setHasFixedSize(true);
        holder.recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(mContext, holder.recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
//                        Toast.makeText(mContext, "Single CLICK", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
//                        Toast.makeText(mContext, "LONG CLICK", Toast.LENGTH_LONG).show();
//                        View popupView = LayoutInflater.from(mContext).inflate(R.layout.dialog_dc_turnout, null);
//                        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT,
//                                                                    ViewGroup.LayoutParams.WRAP_CONTENT);
//                        popupWindow.setFocusable(true);
//                        popupWindow.setOutsideTouchable(true);
//                        popupWindow.setBackgroundDrawable(new ColorDrawable(1));
//                        popupWindow.showAtLocation(view, Gravity.CENTER, 0 ,0);
                    }
                }));
        myDCTurnoutRecyclerAdapter = new MyDCTurnoutRecyclerAdapter(mContext, mListDatas.get(dc), mDepot);
        holder.recyclerView.setAdapter(myDCTurnoutRecyclerAdapter);

        mDcItemTouchHelper = new ItemTouchHelper(onDCItemTouchCallback);
        mDcItemTouchHelper.attachToRecyclerView(holder.recyclerView);
        if(mListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mListener.OnItemClick(holder.itemView, pos);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mListDatas  == null ? 0:mListDatas.size();
    }

//    public void addData(int position, String dc) {
//        mListDatas.add(position, dc);
//        notifyItemInserted(position);
//    }

    public void removeData(int position) {
        mListDatas.remove(mListDatas.keyAt(position));
        notifyItemRemoved(position);
    }

    public void addChildView(int position, String parent, String item) {
        String dcItem = mListDatas.get(parent).get(position).getItem();
        Log.i(TAG, "ADD DC -> " + parent + " Item -> " + item);

        SharedPreferences sp = mContext.getSharedPreferences(mDepot, MODE_PRIVATE);
        String content = sp.getString(DepotActivity.DC_KEY,"");
//        Log.i(TAG, "Content -> " + content);
        if (content.length() > 0) {
            content = CustomUtils.addStrFromBegin(content, DepotActivity.DC_THUMB_SEP + item, dcItem, parent);
//            Log.i(TAG, "Content -> " + content);
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt(item, 1);
            editor.putString(DepotActivity.DC_KEY, content).commit();
        }
    }

    public void delChildView(int position, String parent) {
        String dcItem = mListDatas.get(parent).get(position).getItem();
        Log.i(TAG, "DEL DC -> " + parent + " Position -> " + position + " Item -> " + dcItem);

        SharedPreferences sp = mContext.getSharedPreferences(mDepot, MODE_PRIVATE);
        String content = sp.getString(DepotActivity.DC_KEY,"");
//        Log.i(TAG, "Content -> " + content);
        if (content.length() > 0) {
            content = CustomUtils.delStrFromBegin(content, DepotActivity.DC_THUMB_SEP + dcItem, parent);
//            Log.i(TAG, "Content -> " + content);
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt(dcItem, 0);
            editor.putString(DepotActivity.DC_KEY, content).commit();
        }

    }

    public void modifyChildView(int position, String parent, String newVal) {
        String dcItem = mListDatas.get(parent).get(position).getItem();
        Log.i(TAG, "Modify DC -> " + parent + " old -> " + dcItem + " to new -> " + newVal);
        SharedPreferences sp = mContext.getSharedPreferences(mDepot, MODE_PRIVATE);
        String content = sp.getString(DepotActivity.DC_KEY,"");
//        Log.i(TAG, "Content -> " + content);
        if (content.length() > 0) {
            content = CustomUtils.replaceStrFromBegin(content, newVal, dcItem, parent);
//            Log.i(TAG, "Content -> " + content);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(DepotActivity.DC_KEY, content).commit();
        }

    }
    public void refreshChildView() {
        myDCTurnoutRecyclerAdapter.notifyDataSetChanged();
    }
    private ItemTouchHelper.Callback onDCItemTouchCallback = new ItemTouchHelper.Callback() {
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlags;
            int swipeFlags;
            if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN
                        | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                swipeFlags = 0;
            } else {
                dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            }
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            if (viewHolder.getItemViewType() != target.getItemViewType()) {
                return false;
            }
            int old = viewHolder.getLayoutPosition();
            int newPos = target.getLayoutPosition();
            myDCTurnoutRecyclerAdapter.notifyItemMoved(old, newPos);
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            myDCTurnoutRecyclerAdapter.notifyItemRemoved(position);
            String delStr = mListDatas.valueAt(position).get(position) + DC_ITEM_SEP;
            Log.i(TAG, "Remove ITEM -> " + position + " " + delStr);
            mListDatas.valueAt(position).remove(position);

            SharedPreferences sp = mContext.getSharedPreferences(mDepot, mContext.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            String item_name = sp.getString(DC_ITEM_KEY, "");
            item_name = CustomUtils.delStr(item_name, delStr);
            editor.putString(DC_ITEM_KEY, item_name).commit();
        }
    };

    private View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            Toast.makeText(mContext, "LONG Click", Toast.LENGTH_LONG).show();
            return true;
        }
    };
    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mDC;
        public RecyclerView recyclerView;

        public ViewHolder(View itemView) {
            super(itemView);
            mDC = (TextView) itemView.findViewById(R.id.tv_dc_name);
            recyclerView = (RecyclerView)itemView.findViewById(R.id.recyclerview_dc_item_turnout);


        }
        public void bindData(String item) {
            if (item != null) {
                mDC.setText(item);
            }
        }
    }

}
