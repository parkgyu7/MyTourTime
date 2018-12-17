package parkgyu7.mytourtime;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import parkgyu7.mytourtime.db.PlanDatabase;


public class PlanListAdapter extends BaseAdapter {

    public static final String TAG = "PlanListAdapter";

    private Context mContext;
    private List<PlanListItem> mItems = new ArrayList<PlanListItem>();
    Button optBtn;

    public PlanListAdapter(Context mContext) {
        this.mContext = mContext;
    }
    public void clear() {
        mItems.clear();
    }
    public void addItem(PlanListItem it) {
        mItems.add(it);
    }
    public void setListItems(List<PlanListItem> lit) {
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

        PlanListItemView itemView;

        if (convertView == null) {
            itemView = new PlanListItemView(mContext);
        } else {
            itemView = (PlanListItemView) convertView;
        }

            // plan DateTime 값을 받아옴.

        itemView.setContents(0, ((String) mItems.get(position).getData(1)));      // plan time
        itemView.setContents(1, ((String) mItems.get(position).getData(2))+" / mItems Id : " +mItems.get(position).getId() );      // Plan Title

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPlanList(position);
            }
        });

        optBtn = (Button) itemView.findViewById(R.id.optBtn);
        optBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delPlanItem(position);
            }
        });

        return itemView;
    }

    private void delPlanItem(int position){

        String mPlanId = mItems.get(position).getId();
        Toast.makeText(mContext, "delete Btn clicked. Plan id : "+ mPlanId, Toast.LENGTH_LONG ).show();

        // delete PLAN record

        Log.d(TAG, "deleting previous PLAN record : " + mPlanId);
        String SQL = "delete from " + PlanDatabase.TABLE_PLAN +
                " where _id = '" + mPlanId + "'";
        Log.d(TAG, "SQL : "+ SQL);

        if (MainActivity.mDatabase != null) {
            MainActivity.mDatabase.execSQL(SQL);
        }
        ((PlanListActivity)mContext).loadPlanListData();
    }

    public void viewPlanList(int position){
        ((PlanListActivity)mContext).viewPlanList(position);
    }
}
