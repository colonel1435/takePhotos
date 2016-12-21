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
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sunland.signalinspect.BrowseImageActivity;
import com.sunland.signalinspect.BrowseInfo;
import com.sunland.signalinspect.DCInfo;
import com.sunland.signalinspect.DepotActivity;
import com.sunland.signalinspect.DepotInfo;
import com.sunland.signalinspect.R;
import com.sunland.view.SquareCenterImageView;

import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

import java.io.File;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static com.sunland.signalinspect.DepotActivity.DC_ITEM_KEY;
import static com.sunland.signalinspect.DepotActivity.DC_ITEM_SEP;

/**
 * Created by Zero on 2016/10/04.
 */
public class MyBrowseRecyclerAdapter extends RecyclerView.Adapter<MyBrowseRecyclerAdapter.ViewHolder> {

    private static final String TAG = "wumin";
    private List<BrowseInfo> mListDatas = null;
    public  static Context mContext;
    public MyBrowseRecyclerAdapter(List<BrowseInfo> list, Context context) {
        this.mContext = context;
        this.mListDatas = list;
    }
    @Override
    public MyBrowseRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.action_browse_item, parent, false);
            return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyBrowseRecyclerAdapter.ViewHolder holder, int position) {
        holder.bindData(mListDatas.get(position));
        holder.mImage.setTag(R.id.ivBrowsePositionIndex,position);
    }

    @Override
    public int getItemCount() {
        return mListDatas  == null ? 0:mListDatas.size();
    }


    public void removeData(int position) {
        mListDatas.remove(mListDatas.get(position));
        notifyItemRemoved(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private SquareCenterImageView mImage;
        private CheckBox mCheck;

        public ViewHolder(View itemView) {
            super(itemView);
            mImage = (SquareCenterImageView) itemView.findViewById(R.id.iv_action_browse);
            mCheck = (CheckBox) itemView.findViewById(R.id.action_browse_check_box);

        }
        public void bindData(BrowseInfo item) {
            if (item != null) {
                File photoFile = new File(item.getTitle());
                Glide.with(MyBrowseRecyclerAdapter.mContext).load(photoFile).crossFade().thumbnail(0.3f).into(mImage);
                boolean checked = item.getChecked();
                mCheck.setChecked(checked ? true : false);
                if(BrowseImageActivity.mode == BrowseImageActivity.MULTIPLE_CHOICE) {
                    mCheck.setVisibility(View.VISIBLE);
                } else {
                    mCheck.setVisibility(GONE);
                }
            }
        }
    }

}
