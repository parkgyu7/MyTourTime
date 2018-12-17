package parkgyu7.mytourtime;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import parkgyu7.mytourtime.db.PlanDatabase;

/**
 * Created by Administrator on 2018-02-27.
 */

public class TourListAdapter extends BaseAdapter {

    public static final String TAG = "TourListAdapter";
    private Context mContext;
    private List<TourListItem> mItems = new ArrayList<TourListItem>();

    Button deleteTourBtn;

    public TourListAdapter(Context mContext) {
        this.mContext = mContext;
    }



    public void clear() {
        mItems.clear();
    }
    public void addItem(TourListItem it) {
        mItems.add(it);
    }

    public void setListItems(List<TourListItem> lit) {
        mItems = lit;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    public boolean areAllItemsSelectable() {
        return false;
    }

    public boolean isSelectable(int position) {
        try {
            return mItems.get(position).isSelectable();
        } catch (IndexOutOfBoundsException ex) {
            return false;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        TourListItemView itemView;

        if (convertView == null) {
            itemView = new TourListItemView(mContext);
        } else {
            itemView = (TourListItemView) convertView;
        }

        // set current item data
        itemView.setContents(0, ((String) mItems.get(position).getData(0)));
        itemView.setContents(1, ((String) mItems.get(position).getData(1)));
        itemView.setContents(2, ((String) mItems.get(position).getData(2)));


        deleteTourBtn = (Button) itemView.findViewById(R.id.tourItemOptBtn);
        deleteTourBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTour(position);
            }
        });
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewTourPlanList(position);
            }
        });

        return itemView;

    }

    /**
     * Tour plan보기
     */

    public void viewTourPlanList(int position){
        ((MainActivity)mContext).viewTourPlanList(position);
    }

    /**
     * 메모 삭제
     */
    public void deleteTour(int position) {

        String mTourId = mItems.get(position).getId();

        // delete PLAN record
        Log.d(TAG, "deleting previous PLAN ");
        String SQL = "delete from " + PlanDatabase.TABLE_PLAN +
                " where TOUR_ID = '" + mTourId + "'";
        Log.d(TAG, "SQL : " + SQL);
        if (MainActivity.mDatabase != null) {
            MainActivity.mDatabase.execSQL(SQL);
        }

        // delete TOUR record
        Log.d(TAG, "deleting previous TOUR record : " + mTourId);
        SQL = "delete from " + PlanDatabase.TABLE_TOUR +
                " where _id = '" + mTourId + "'";
        Log.d(TAG, "SQL : " + SQL);
        if (MainActivity.mDatabase != null) {
            MainActivity.mDatabase.execSQL(SQL);
        }

        ((MainActivity)mContext).loadTourListData();
    }


}
