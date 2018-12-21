package parkgyu7.mytourtime;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018-02-27.
 */


public class TourListSelectableAdapter extends BaseAdapter {

    public static final String TAG = "TourListAdapter";

    private Context mContext;
    private List<TourListItem> mItems = new ArrayList<TourListItem>();


    TextView DdayTxtView;

    private String tourTitle;
    private String tourFirstDay;
    private String tourLastDay;
    private String favoriteChecker;
    private String doneChecker;

    public TourListSelectableAdapter(Context mContext) {
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

        TourListItemViewCheckable itemView;

        if (convertView == null) {
            itemView = new TourListItemViewCheckable(mContext);
        } else {
            itemView = (TourListItemViewCheckable) convertView;
        }


        // get item data
        tourTitle = (String) mItems.get(position).getData(1);
        tourFirstDay = (String) mItems.get(position).getData(2);
        tourLastDay = (String) mItems.get(position).getData(3);
        favoriteChecker = (String) mItems.get(position).getData(4);
        doneChecker = (String) mItems.get(position).getData(5);

        // set current item data
        itemView.setContents(1, "title : "+tourTitle+" / id :"+ mItems.get(position).getId());         // title
        itemView.setContents(2, tourFirstDay);      // first day
        itemView.setContents(3, tourLastDay);       // last day


        // TOUR_ITEM FAVORITE BUTTON
        Button tourFavoriteBtn = (Button)itemView.findViewById(R.id.tourFavoriteBtn);

        if (favoriteChecker.equals("off")){
            tourFavoriteBtn.setBackground(ContextCompat.getDrawable(mContext,R.drawable.staroff));
        }else {tourFavoriteBtn.setBackground(ContextCompat.getDrawable(mContext,R.drawable.staron));}

        // TOUR_ITEM DONE process
        TextView tourItemTourTitle = (TextView) itemView.findViewById(R.id.tourItemTourTitle);
        if (doneChecker.equals("off")){
            tourItemTourTitle.setPaintFlags(0);
            tourItemTourTitle.setTypeface(null, Typeface.BOLD);
        }else {
            tourItemTourTitle.setPaintFlags(tourItemTourTitle.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
        }

        // TOUR D-day set
        DdayTxtView = (TextView) itemView.findViewById(R.id.DdayTxt);
        setTourDday(tourFirstDay,tourLastDay);

        return itemView;

    }


    public void setTourDday(String fday, String lday){

        Calendar todayCal = Calendar.getInstance();

        Date firstDate = new Date();
        Date lastDate = new Date();

        try {
            firstDate = BasicInfo.datetimeFormat_Date.parse(fday);
            lastDate = BasicInfo.datetimeFormat_Date.parse(lday);
        } catch (Exception ex) {
            Log.d(TAG, "Exception in parsing date : " + fday);
        }

        long today = todayCal.getTimeInMillis();
        long firstDay = firstDate.getTime();
        long lastDay = lastDate.getTime();

        int dDay = 0;
        int src = (24 * 60 * 60 * 1000);

        if (today<firstDay){
            dDay = (int)(firstDay-today)/src;
            DdayTxtView.setText("D-"+dDay);
            DdayTxtView.setTextColor(ContextCompat.getColor(mContext,R.color.colorAccent));

        }else if (today>lastDay){
            dDay = (int)(today-lastDay)/src;
            DdayTxtView.setText("D+"+dDay);
            DdayTxtView.setTextColor(ContextCompat.getColor(mContext,R.color.colorPrimary));

        }else {
            DdayTxtView.setText("D-Day");
            DdayTxtView.setTextColor(Color.RED);
        }
    }
}
