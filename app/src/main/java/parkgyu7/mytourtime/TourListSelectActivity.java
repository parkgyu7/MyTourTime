package parkgyu7.mytourtime;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Date;

import parkgyu7.mytourtime.db.PlanDatabase;

public class TourListSelectActivity extends AppCompatActivity {

    public static final String TAG = "** TourListSelectAct ";

    ListView selectableTourList;
    TourListSelectableAdapter tourListSelectableAdapter;
    TextView selectItemCount;
    TextView allItemCount;
    ToggleButton checkallBtn;
    Button selectDelBtn;

    int selectCount;

    public static PlanDatabase mDatabase = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_list_select);

        selectableTourList = (ListView) findViewById(R.id.selectable_tourlist);
        tourListSelectableAdapter = new TourListSelectableAdapter(this);
        selectableTourList.setAdapter(tourListSelectableAdapter);

        selectItemCount = (TextView) findViewById(R.id.selectItemCount);
        allItemCount = (TextView)findViewById(R.id.allItemCount);
        checkallBtn = (ToggleButton)findViewById(R.id.checkallBtn);
        selectDelBtn = (Button)findViewById(R.id.selectDelBtn);
        setCount();

        selectableTourList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setCount();
                int Allcount = tourListSelectableAdapter.getCount();
                if (selectCount == Allcount)
                    checkallBtn.setChecked(true);
                else
                    checkallBtn.setChecked(false);

            }
        });



        checkallBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int count =0;
                count = tourListSelectableAdapter.getCount();
                int chkCount = selectableTourList.getCheckedItemCount();

                if (isChecked){
                    Toast.makeText(getApplicationContext(),"select All!",Toast.LENGTH_SHORT).show();

                    for(int i=0; i<count;i++){
                        selectableTourList.setItemChecked(i,true);
                    }


                }else {
                    if (count == chkCount ){
                        Toast.makeText(getApplicationContext(),"select All Cancle!",Toast.LENGTH_SHORT).show();
                        for(int i=0; i<count;i++){
                            selectableTourList.setItemChecked(i,false);
                        }
                    }
                }
                setCount();
            }
        });




        selectDelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "  * delSelctItemBtn clicked");
                SparseBooleanArray checkedItems = selectableTourList.getCheckedItemPositions();

                int count = tourListSelectableAdapter.getCount() ;


                Log.d(TAG, " * list count : "+count);

                for (int i = count-1; i >= 0; i--) {
                    if (checkedItems.get(i)) {
                        Log.d(TAG, "  * checkedItems :"+ i);
                        deleteSelectedTourItem(i);
                    }
                }
                Toast.makeText(getApplicationContext()," Del Btn Clicked", Toast.LENGTH_LONG).show();
                // 모든 선택 상태 초기화.
                selectableTourList.clearChoices();
                loadSelectableTourListData();



            }
        });


    }
    //-------------------------------------- onCreate() ----------------------------------------------------//




    protected void onStart() {
        openDatabase();         // open DB
        loadSelectableTourListData();
        super.onStart();
    }



    /**
     * 데이터베이스 열기 ( if null -> create)
     */
    public void openDatabase() {
        if (mDatabase != null) {
            mDatabase.close();
            mDatabase = null;
        }
        mDatabase = PlanDatabase.getInstance(this);
        boolean isOpen = mDatabase.open();
        if (isOpen) {
            Log.d(TAG, "Plan database is open.");
        } else {
            Log.d(TAG, "Plan database is not open.");
        }
    }


    public void setCount(){

         selectCount = 0;

        SparseBooleanArray checkedItems = selectableTourList.getCheckedItemPositions();
        final int length =tourListSelectableAdapter.getCount();

        for (int i =0; i<length;i++){
            if(checkedItems.valueAt(i)){
                Log.d(TAG, "  * checkedItems :"+ i);
                selectCount ++;
            }
        }
        Log.d(TAG, "  * checkedItemsCount :"+ selectCount);

        selectItemCount.setText(selectCount+"");                //
        selectItemCount.invalidate();

    }



    public void loadSelectableTourListData() {

        String SQL = "select _id, TOUR_TITLE, TOUR_firstDATE, TOUR_lastDATE, TOUR_FAVORITE, TOUR_DONE from TOUR order by CREATE_DATE desc";

        int recordCount = -1;

        if (MainActivity.mDatabase != null) {
            Cursor outCursor = MainActivity.mDatabase.rawQuery(SQL);

            recordCount = outCursor.getCount();
            Log.d(TAG, "cursor count : " + recordCount + "\n");

            tourListSelectableAdapter.clear();

            for (int i = 0; i < recordCount; i++) {
                outCursor.moveToNext();

                String tourId = outCursor.getString(0);
                String tourTitle = outCursor.getString(1);

                String tourFirstDateStr = outCursor.getString(2);

                if (tourFirstDateStr != null) {
                    try {
                        Date inDate = BasicInfo.dateTimeFormat.parse(tourFirstDateStr);
                        tourFirstDateStr = BasicInfo.datetimeFormat_Date.format(inDate);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    tourFirstDateStr = "";
                }

                String tourLastDateStr = outCursor.getString(3);

                if (tourFirstDateStr != null) {
                    try {
                        Date inDate = BasicInfo.dateTimeFormat.parse(tourLastDateStr);
                        tourLastDateStr = BasicInfo.datetimeFormat_Date.format(inDate);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    tourLastDateStr = "";
                }

                String tourFavorite = outCursor.getString(4);
                String tourDone = outCursor.getString(5);

                tourListSelectableAdapter.addItem(new TourListItem(tourId, tourTitle, tourFirstDateStr, tourLastDateStr, tourFavorite,tourDone));
            }

            outCursor.close();

            tourListSelectableAdapter.notifyDataSetChanged();
            if (recordCount==0) finish();
            allItemCount.setText(recordCount +"");
            allItemCount.invalidate();
        }
    }

    /**
     * 메모 삭제
     */
    public void deleteSelectedTourItem(int position) {

        TourListItem item = (TourListItem) tourListSelectableAdapter.getItem(position);
        String tourId = item.getId();

        // delete PLAN record
        Log.d(TAG, "deleting previous PLAN ");

        String SQL = "delete from " + PlanDatabase.TABLE_PLAN +
                " where TOUR_ID = '" + tourId + "'";
        Log.d(TAG, "SQL : " + SQL);
        if (MainActivity.mDatabase != null) {
            MainActivity.mDatabase.execSQL(SQL);
        }

        // delete TOUR record
        Log.d(TAG, "deleting previous TOUR record : " + tourId);
        SQL = "delete from " + PlanDatabase.TABLE_TOUR +
                " where _id = '" + tourId + "'";
        Log.d(TAG, "SQL : " + SQL);
        if (MainActivity.mDatabase != null) {
            MainActivity.mDatabase.execSQL(SQL);
        }
    }

}
