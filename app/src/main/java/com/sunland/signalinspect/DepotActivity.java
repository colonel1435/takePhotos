package com.sunland.signalinspect;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DecorContentParent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.sunland.utils.BitmapUtils;
import com.sunland.utils.CustomUtils;
import com.sunland.utils.MyDCRecyclerAdapter;
import com.sunland.utils.MyDCTurnoutRecyclerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class DepotActivity extends AppCompatActivity {

    public static final String WORK_DIR = "/signalInspect/";
    private static final String DATE_FORMAT_STR = "yyMMddHHmm";
    public static final String DEPOT_KEY = "depot";
    public static final String DC_KEY   = "DC";
    public static final String DC_ITEM_KEY   = "DC_ITEM";
    public static final String DC_TURNOUT_KEY   = "DC_TURNOUT";
    public static final String DC_THUMB_KEY = "DC_THUMB";
    public static final String DC_ITEM_SEP  = ":";
    public static final String DC_THUMB_SEP  = "-";
    public static final String DC_STATUS_SEP  = "|";
    public static final String FILE_NAME_SEP = "_";
    public static final String DC_ITEM_MAX_KEY = "max";
    private static final int TAKE_PHOTOS = 0;
    private static final int SAVE_PHOTOS = 1;
    private static final String TAG = "wumin";
    public static final String ZOOM_PHOTO_KEY = "ZOOM";
    public static final String DC_LIST = "dc_item";
    private static final int DC_PHOTO_TYPE = 0;
    private static final int OTHER_PHOTO_TYPE = 1;
    private static final String FILE_TYPE = "image/*";
    private static String SCAN_PATH = "";
    public static final int SHOW_THUMB = 1;

    public static int currentButtonPosition = -1;
    public static TextView currentView = null;
    public static String imgName = "";
    public static String imgDir = "";
    private static String fileName = "";
    private static Context mContext = null;
    private static String depot = null;
    private int dc_num;
    private int dc_item_max;
    private TextView mTitle;
    private ImageButton btSetPosition;
    private ImageButton btBackPosition;
    private ScrollView mScrollView;
    private RecyclerView mRecyclerView;
    private MyDCRecyclerAdapter myDCRecyclerAdapter;
    private ItemTouchHelper mItemTouchHelper;
    private PopupWindow popupWindow;
    private PopupWindow dcPopupWindow;

    ArrayMap<String, List<DCInfo>> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_depot);

        mContext = this;
        Bundle bundle = getIntent().getExtras();
        depot = bundle.getString(DEPOT_KEY);
        dc_num = Integer.parseInt(bundle.getString(DC_KEY).split(":")[1]);
        dc_item_max = bundle.getInt(DC_ITEM_MAX_KEY);

        initView();
    }

    private void initView() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_depot_bar);
//        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imgDir = Environment.getExternalStorageDirectory().toString() + WORK_DIR;
        File dir = new File(imgDir);
        if (!dir.exists()) {
            dir.mkdir();
        }

        mTitle = (TextView)findViewById(R.id.tv_dc_title);
        mTitle.setText(depot);

        mScrollView = (ScrollView)findViewById(R.id.scrollView_dc);
        mScrollView.setSmoothScrollingEnabled(true);
        mScrollView.smoothScrollTo(0,0);

        mRecyclerView = (RecyclerView)this.findViewById(R.id.recyclerview_dc_item);
//        mRecyclerView.setLayoutManager(new MyLinearLayoutManager(mContext));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);

        mData = new ArrayMap<>();
        getData(mData);

        myDCRecyclerAdapter = new MyDCRecyclerAdapter(mData, mContext, depot);
        mRecyclerView.setAdapter(myDCRecyclerAdapter);

        mItemTouchHelper = new ItemTouchHelper(onItemTouchCallback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        View itemView = LayoutInflater.from(mContext).inflate(R.layout.dc_item, null);
        btSetPosition = (ImageButton) itemView.findViewById(R.id.dc_set_position);
        btBackPosition = (ImageButton) itemView.findViewById(R.id.dc_back_position);

    }


    private ItemTouchHelper.Callback onItemTouchCallback = new ItemTouchHelper.Callback() {
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlags;
            int swipeFlags;
            if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN
                        | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                swipeFlags = 0;
            } else {
//                dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
//                swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                dragFlags = 0;
                swipeFlags = 0;
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
            myDCRecyclerAdapter.notifyItemMoved(old, newPos);
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            myDCRecyclerAdapter.notifyItemRemoved(position);

            String dc = mData.keyAt(position);
            mData.remove(dc);

            SharedPreferences sp = getSharedPreferences(depot, MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            String item_name = sp.getString(DC_KEY, "");
            item_name = CustomUtils.delStr2End(item_name, dc, DC_ITEM_SEP);
            editor.putString(DC_KEY, item_name).commit();
            int itemNum  = sp.getInt(MainActivity.DC_NUM, -1);
            if (itemNum != -1) {
                itemNum -= 1;
                editor.putInt(MainActivity.DC_NUM, itemNum).commit();
                SharedPreferences spMain = getSharedPreferences(MainActivity.DEPOT_LIST, MODE_PRIVATE);
                SharedPreferences.Editor mainEditor = spMain.edit();
                mainEditor.putInt(depot, itemNum).commit();
            }


        }
    };

    public void getData(ArrayMap<String, List<DCInfo>> Items) {
        File file = new File(Environment.getDataDirectory() + getPackageName().toString()
                + "/shared_prefs", depot + "_" + DC_LIST + ".xml");
        if (file.exists()) {
            file.delete();
        }
        String dc_name = "";
        String item_name = "";
        SharedPreferences sp = getSharedPreferences(depot, MODE_PRIVATE);
        item_name = sp.getString(DC_KEY, "");
        if (item_name != "") {
            String[] turnouts = item_name.split(DC_ITEM_SEP);
            Items.clear();
            for (String str : turnouts) {
                Log.i(TAG, "ITEM -> " + str);
                String[] strs = str.split(DC_THUMB_SEP);
                String dc = strs[0];
                List<DCInfo> itemList = new ArrayList<>();
                for (int i = 1; i < strs.length; i++) {
                    DCInfo dcInfo = new DCInfo();
                    dcInfo.setDC(dc);
                    String dcItem = strs[i];
                    dcInfo.setItem(dcItem);
                    String setStatus = sp.getString(dc+dcItem+getString(R.string.dc_set_position), "");
                    dcInfo.setSetThumb(setStatus);
                    String backStatus = sp.getString(dc+dcItem+getString(R.string.dc_back_position), "");
                    dcInfo.setBackThumb(backStatus);

                    itemList.add(dcInfo);
                }
                Items.put(dc, itemList);
            }
        } else {
            String tmp = "";
            SharedPreferences.Editor editor = sp.edit();
            for (int i = 1; i < dc_num + 1; i++) {
                tmp = i + getString(R.string.dc_id_postfix);
                dc_name += tmp;
                List<DCInfo> dcList = new ArrayList<>();
                for (int j = 1; j < dc_item_max + 1; j++) {
                    String dcItem = getString(R.string.dc_item_name_postfix) + j;
                    dc_name += DC_THUMB_SEP + dcItem;
                    DCInfo dcInfo = new DCInfo(tmp, dcItem, "", "");
                    Log.i(TAG, "Add DC ITEM -> " + dcItem);
                    dcList.add(dcInfo);
                    editor.putInt(dcItem, 1);

                }
                dc_name += DC_ITEM_SEP;
                Items.put(tmp, dcList);
            }
            editor.putString(DC_KEY, dc_name);
            editor.commit();
        }
    }

    public String createPhotoName(int type, String val, String status) {
        String now = CustomUtils.getCurTime(DATE_FORMAT_STR);
        String jpgName = "";
        switch (type) {
            case DC_PHOTO_TYPE:
                jpgName = depot + "_" + DC_KEY + "_" + val + "_"  + status + "_" + now + ".jpg";
                break;
            case OTHER_PHOTO_TYPE:
                jpgName = depot + "_"  + val + "_"  + status + "_" + now + ".jpg";
                break;
            default:
                break;
        }

        return jpgName;
    }
    public void onBtSetPositionClick(View view) {
        String dc = view.getTag(R.id.btSetPositionContent).toString();
        currentButtonPosition = (int)view.getTag(R.id.btSetPositionIndex);
        fileName = createPhotoName(DC_PHOTO_TYPE, dc, getString(R.string.dc_set_position));
        imgName = imgDir + fileName;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(imgName)));
        startActivityForResult(intent, TAKE_PHOTOS);
    }

    public void onBtBackPositionClick(View view) {
        String dc = view.getTag(R.id.btBackPositionContent).toString();
        currentButtonPosition = (int)view.getTag(R.id.btBackPositionIndex);
        fileName = createPhotoName(DC_PHOTO_TYPE, dc, getString(R.string.dc_back_position));
        imgName = imgDir + fileName;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(imgName)));
        startActivityForResult(intent, TAKE_PHOTOS);
    }

    public void onBtDefaultClick(View view) {
        final View popupView =  LayoutInflater.from(mContext).inflate(R.layout.dialog_other_photo, null);
        final EditText etPhoto = (EditText)popupView.findViewById(R.id.et_other_photo);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.notification_msg_title)
                .setView(popupView)
                .setNegativeButton(getString(R.string.depot_cancle), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(getString(R.string.msg_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String val = etPhoto.getText().toString();
                        if (val.length() == 0) {
                            Toast.makeText(mContext, getString(R.string.msg_photo_null), Toast.LENGTH_LONG).show();
                            return;
                        }
                        fileName = createPhotoName(OTHER_PHOTO_TYPE, val, getString(R.string.dc_misc));
                        imgName = imgDir + fileName;
                        CustomUtils.hideKeyboard(popupView);
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(imgName)));
                        startActivityForResult(intent, TAKE_PHOTOS);
                    }
                })
                .create().show();
    }

    public void onSetThumbClick(View view) {
        String path = (String)view.getTag(R.id.ivSetPositionContent);
        if (path.equals("")) {
            Toast.makeText(mContext, getString(R.string.show_thumb_null), Toast.LENGTH_LONG).show();
            return;
        }

        String file = CustomUtils.delStr2End(path, ActionSearchActivity.THUMBNAIL_LABEL, "/");
        Uri uri = Uri.fromFile(new File(file));
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
        /*
        Intent intent = new Intent(DepotActivity.this, DispalyImageActivity.class);
        int[] location = new int[2];
        view.getLocationOnScreen(location);

        intent.putExtra("path", path);
        intent.putExtra("locationX", location[0]);
        intent.putExtra("locationY", location[1]);

        intent.putExtra("width", view.getWidth());
        intent.putExtra("height", view.getHeight());
        startActivity(intent);
        overridePendingTransition(0, 0);
        */
    }

    public void onBackThumbClick(View view) {

        String path = (String)view.getTag(R.id.ivBackPositionContent);
        if (path.equals("")) {
            Toast.makeText(mContext, getString(R.string.show_thumb_null), Toast.LENGTH_LONG).show();
            return;
        }
        String file = CustomUtils.delStr2End(path, ActionSearchActivity.THUMBNAIL_LABEL, "/");
        Uri uri = Uri.fromFile(new File(file));
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);

         /*
        Intent intent = new Intent(DepotActivity.this, DispalyImageActivity.class);
        int[] location = new int[2];
        view.getLocationOnScreen(location);

        intent.putExtra("path", path);
        intent.putExtra("locationX", location[0]);
        intent.putExtra("locationY", location[1]);

        intent.putExtra("width", view.getWidth());
        intent.putExtra("height", view.getHeight());
        startActivity(intent);
        overridePendingTransition(0, 0);
        */
    }

    private void showFileBrowser() {
//        Uri uri = Uri.fromFile(new File(imgDir));
//        Intent intent = new Intent();
//        intent.setAction(android.content.Intent.ACTION_VIEW);
//        intent.setDataAndType(uri, "image/*");
//        startActivity(intent);
        List<String> datas = new ArrayList<>();
        BitmapUtils.getBmpUrl(datas, BrowseImageActivity.IMAGES_FOLDER);
        if (datas.size() == 0 ) {
            Toast.makeText(mContext, getString(R.string.photo_null), Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(DepotActivity.this, BrowseImageActivity.class);
            startActivity(intent);
        }

    }

    private void showFileSearch() {
//        Toast.makeText(mContext, "搜索", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(DepotActivity.this, ActionSearchActivity.class);
        intent.putExtra(ActionSearchActivity.WORK_DIR_KEY, imgDir);
        startActivity(intent);
    }

    public void onDcClick(View view) {
        currentView = (TextView) view;
        View popupView = LayoutInflater.from(mContext).inflate(R.layout.dialog_dc_popup, null);
        dcPopupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        dcPopupWindow.setFocusable(true);
        dcPopupWindow.setOutsideTouchable(true);
        dcPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        dcPopupWindow.setAnimationStyle(R.style.popwindow_dc_anim);

        int popupWidth = dcPopupWindow.getWidth();
        int popupHeight = dcPopupWindow.getHeight();
        int width = currentView.getWidth();
        int height = currentView.getHeight();
        dcPopupWindow.showAsDropDown(currentView, (width - popupWidth) / 2, 0);
//        dcPopupWindow.showAtLocation(view, Gravity.BOTTOM, 0 ,0);
    }

    public void onDcErase(View view) {
        dcPopupWindow.dismiss();
        int position = (int)currentView.getTag(R.id.tvDCPositionIndex);
        String dc = mData.keyAt(position);
        mData.remove(dc);
        myDCRecyclerAdapter.notifyItemRemoved(position);

        SharedPreferences sp = getSharedPreferences(depot, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        String item_name = sp.getString(DC_KEY, "");
        item_name = CustomUtils.delStr2End(item_name, dc, DC_ITEM_SEP);
        editor.putString(DC_KEY, item_name).commit();
        int itemNum  = sp.getInt(MainActivity.DC_NUM, -1);
        if (itemNum != -1) {
            itemNum -= 1;
            editor.putInt(MainActivity.DC_NUM, itemNum).commit();
            SharedPreferences spMain = getSharedPreferences(MainActivity.DEPOT_LIST, MODE_PRIVATE);
            SharedPreferences.Editor mainEditor = spMain.edit();
            mainEditor.putInt(depot, itemNum).commit();
        }
    }
    private void addDC() {
        final View popupView =  LayoutInflater.from(mContext).inflate(R.layout.dialog_dc_item, null);
        final EditText etDC = (EditText)popupView.findViewById(R.id.et_dc_item);
        final EditText etTurnout = (EditText)popupView.findViewById(R.id.et_dc_turnout_item);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.notification_msg_title)
                .setView(popupView)
                .setNegativeButton(getString(R.string.depot_cancle), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(getString(R.string.depot_new), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CustomUtils.hideKeyboard(popupView);
                        String val = etDC.getText().toString();
                        if (val.length() == 0) {
                            Toast.makeText(mContext, getString(R.string.dc_item_null), Toast.LENGTH_LONG).show();
                            return;
                        }
                        String turnout = etTurnout.getText().toString();
                        if (turnout.length() == 0) {
                            Toast.makeText(mContext, getString(R.string.dc_turnout_item_null), Toast.LENGTH_LONG).show();
                            return;
                        }

                        String addStr = val + getString(R.string.dc_id_postfix);
                        SharedPreferences sp = getSharedPreferences(depot, MODE_PRIVATE);
                        String item_name = sp.getString(DC_KEY, "");
                        if (item_name.indexOf(addStr) != -1) {
                            Toast.makeText(mContext, getString(R.string.msg_dc_again), Toast.LENGTH_LONG).show();
                            return;
                        }

                        List<DCInfo> list = new ArrayList<>();
                        int itemSize = Integer.parseInt(turnout);
                        for (int i = 1; i < itemSize+1; i++) {
                            String itemStr = getString(R.string.dc_item_name_postfix)+i;
                            DCInfo info = new DCInfo();
                            info.setDC(val+getString(R.string.dc_id_postfix));
                            info.setItem(itemStr);
                            list.add(info);
                            addStr += DC_THUMB_SEP + itemStr;
                        }
                        mData.put(val+getString(R.string.dc_id_postfix), list);
                        myDCRecyclerAdapter.notifyDataSetChanged();

                        SharedPreferences.Editor editor = sp.edit();
                        item_name += addStr;
                        item_name += DC_ITEM_SEP;
                        editor.putString(DC_KEY, item_name).commit();

                        int itemNum = sp.getInt(MainActivity.DC_NUM, -1);
                        if (itemNum != -1) {
                            itemNum ++;
                            editor.putInt(MainActivity.DC_NUM, itemNum).commit();
                            SharedPreferences spMain = getSharedPreferences(MainActivity.DEPOT_LIST, MODE_PRIVATE);
                            SharedPreferences.Editor mainEditor = spMain.edit();
                            mainEditor.putInt(depot, itemNum).commit();
                        }
                    }
                })
                .create().show();

    }
    public static void onSaveFinished() {
        Toast.makeText(mContext, mContext.getString(R.string.save_finished), Toast.LENGTH_LONG).show();
    }

    public void onRefreshThumb(String path) {
        List<String> strItems = CustomUtils.splitString(path, FILE_NAME_SEP);
        String strItem = strItems.get(2);
        String strStatus = strItems.get(3);
        String dcNum = strItem.substring(0, strItem.indexOf(getString(R.string.dc_id_postfix))+1);
        Log.i(TAG, "ALL -> " + strItems.toString() + "\nDC -> " + strItem + "\nStatus -> " + strStatus + " DC -> " + dcNum + " Path ->" + path);
        List<DCInfo> dcItems = mData.get(dcNum);
        SharedPreferences sp = getSharedPreferences(depot, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        String thumb;
        if (strStatus.equals(getString(R.string.dc_set_position))) {
            dcItems.get(currentButtonPosition).setSetThumb(path);
            thumb = strItem + getString(R.string.dc_set_position);
            Log.i(TAG, "Update setposition -> " + thumb);
        } else {
            dcItems.get(currentButtonPosition).setBackThumb(path);
            thumb = strItem + getString(R.string.dc_back_position);
            Log.i(TAG, "Update Backposition -> " + thumb);
        }
        editor.putString(thumb, path).commit();

        myDCRecyclerAdapter.notifyDataSetChanged();
        myDCRecyclerAdapter.refreshChildView();
    }

    public void onTurnoutClick(View view) {
        currentView = (TextView) view;
        View popupView = LayoutInflater.from(mContext).inflate(R.layout.dialog_dc_turnout, null);
        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(1));
        popupWindow.setAnimationStyle(R.style.popwindow_anim_style);
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0 ,0);
    }
    public void onDcAdd(View view) {
        popupWindow.dismiss();
        final View popupView =  LayoutInflater.from(mContext).inflate(R.layout.dialog_dc_turnout_add, null);
        final EditText etItem = (EditText)popupView.findViewById(R.id.et_dc_add_item);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.notification_msg_title)
                .setView(popupView)
                .setNegativeButton(getString(R.string.depot_cancle), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(getString(R.string.depot_new), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CustomUtils.hideKeyboard(popupView);
                        String val = etItem.getText().toString();
                        if (val.length() == 0) {
                            Toast.makeText(mContext, getString(R.string.dc_item_null), Toast.LENGTH_LONG).show();
                            return;
                        }
                        SharedPreferences sp = getSharedPreferences(depot, MODE_PRIVATE);
                        int ret = sp.getInt(val, 0);
                        if (ret != 0) {
                            Toast.makeText(mContext, getString(R.string.msg_dc_item), Toast.LENGTH_LONG).show();
                            return;
                        }
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt(val, 1);
                        int position = (int)currentView.getTag(R.id.tvTurnoutPositionIndex);
                        String parent = currentView.getTag(R.id.tvTurnoutParentContent).toString();
                        myDCRecyclerAdapter.addChildView(position, parent, val);
                        getData(mData);
                        myDCRecyclerAdapter.notifyDataSetChanged();
                        Toast.makeText(mContext, getString(R.string.dc_item_add_msg), Toast.LENGTH_LONG).show();
                    }
                })
                .create().show();
    }
    public void onDcDel(View view) {
        popupWindow.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.notification_msg_title)
                .setMessage(getString(R.string.dc_item_del_msg_ok))
                .setNegativeButton(getString(R.string.depot_cancle), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(getString(R.string.msg_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int position = (int)currentView.getTag(R.id.tvTurnoutPositionIndex);
                        String parent = currentView.getTag(R.id.tvTurnoutParentContent).toString();

                        myDCRecyclerAdapter.delChildView(position, parent);
                        getData(mData);
                        myDCRecyclerAdapter.notifyDataSetChanged();
                        Toast.makeText(mContext, getString(R.string.dc_item_del_msg) , Toast.LENGTH_LONG).show();
                    }
                })
                .create().show();
    }
    public void onDcModify(View view) {
        popupWindow.dismiss();
        final String oldVal = currentView.getText().toString();
        final View popupView =  LayoutInflater.from(mContext).inflate(R.layout.dialog_dc_turnout_modify, null);
        final TextView tvItemOld = (TextView)popupView.findViewById(R.id.tv_dc_item_mod_old);
        tvItemOld.setText(oldVal);
        final EditText etItem = (EditText)popupView.findViewById(R.id.et_dc_item_mod_new);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.notification_msg_title)
                .setView(popupView)
                .setNegativeButton(getString(R.string.depot_cancle), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(getString(R.string.msg_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String val = etItem.getText().toString();
                        if (val.length() == 0) {
                            Toast.makeText(mContext, getString(R.string.dc_item_null), Toast.LENGTH_LONG).show();
                            return;
                        }
                        CustomUtils.hideKeyboard(popupView);
                        int position = (int)currentView.getTag(R.id.tvTurnoutPositionIndex);
                        String parent = currentView.getTag(R.id.tvTurnoutParentContent).toString();
                        myDCRecyclerAdapter.modifyChildView(position, parent, val);
                        getData(mData);
                        myDCRecyclerAdapter.notifyDataSetChanged();
                        Toast.makeText(mContext, getString(R.string.dc_item_modify_msg), Toast.LENGTH_LONG).show();
                    }
                })
                .create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_search:
                showFileSearch();
                return true;
            case R.id.action_browse:
                showFileBrowser();
                return true;
            case R.id.action_add:
                addDC();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == TAKE_PHOTOS) {
                Log.i(TAG, "Take photo finished...");
                Intent intent = new Intent(DepotActivity.this, ClipImageActivity.class);
                intent.putExtra(ClipImageActivity.PHOTONAME_KEY, fileName);
                intent.putExtra(ClipImageActivity.PHOTOFILE_KEY, imgName);
                intent.putExtra(ClipImageActivity.DEPOT_KEY, depot);
                startActivityForResult(intent, SAVE_PHOTOS);
            }
            if (requestCode == SAVE_PHOTOS) {
                Log.i(TAG, "Save photo finished...");
                String thumb = data.getStringExtra(ZOOM_PHOTO_KEY);
                onRefreshThumb(thumb);
            }
        }
        imgName = "";
        super.onActivityResult(requestCode, resultCode, data);
    }
}
