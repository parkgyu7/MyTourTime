package parkgyu7.mytourtime;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import parkgyu7.mytourtime.db.PlanDatabase;


/**
 *  plan list activity
 *
 * 1. 제목 / 날짜 수정 가능 -> DB 저장하기 when 이 액티비티를 닫을 때
 * 2. 해당 날짜에 맞는 Plan List 보여주기.
 * 3. pre / next Btn으로 날짜이동 -> list 갱신하여 보여주기
 *
 */

public class PlanListActivity extends AppCompatActivity {
    public static final String TAG = " ** PlanListActivity ";

    Intent intent;

    String mTourId;
    String mTourTitle;
    String mTourFirstDate;
    String mTourLastDate;

    LinearLayout dateLayout;
    TextView tourTitleTxt;
    TextView tourPeriodTxt;
    TextView nDayTxt;
    TextView curDateTxt;
    TextView itemCount;

    Button planListOptBtn;

    Button prevDayBtn;
    Button nextDayBtn;

    FloatingActionButton newPlanfabtb;


    Button insert_dateBtn;
    Button insert_timeBtn;

    ListView mPlanListView;
    PlanListAdapter mPlanListAdapter;



    Calendar mCalendar = Calendar.getInstance();

    Date firstDate;
    Date lastDate;

    Calendar curDate = Calendar.getInstance();
    Calendar today = Calendar.getInstance();

    int tourPeriod;
    int dayIndex;
    int dayIndexMax;

    boolean planListViewMode_daily = true;


    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private GestureDetector gestureScanner;

    String SQL;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_list);



        dateLayout = (LinearLayout) findViewById(R.id.dateLayout);

        planListOptBtn = (Button) findViewById(R.id.planListOptBtn);
        planListOptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog(BasicInfo.PLAN_LIST_OPT).show();
            }
        });


        tourTitleTxt = (TextView) findViewById(R.id.tourTitleTxt);
        tourPeriodTxt = (TextView) findViewById(R.id.tourPeriodTxt);

        nDayTxt = (TextView)findViewById(R.id.nDayTxt);
        curDateTxt = (TextView)findViewById(R.id.curDateTxt);


        intent = getIntent();
        processIntent(intent);


        prevDayBtn = (Button)findViewById(R.id.prevDayBtn);
        nextDayBtn = (Button)findViewById(R.id.nextDayBtn);
        prevDayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPreDay();
            }
        });
        nextDayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNextDay();
            }
        });

        newPlanfabtb = (FloatingActionButton) findViewById(R.id.newPlanfab);
        newPlanfabtb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PlanInsertActivity.class);

                intent.putExtra(BasicInfo.KEY_MODE, BasicInfo.MODE_INSERT);

                intent.putExtra(BasicInfo.KEY_TOUR_ID, mTourId);
                intent.putExtra(BasicInfo.KEY_TOUR_FIRSTDATE,mTourFirstDate);
                intent.putExtra(BasicInfo.KEY_TOUR_LASTDATE,mTourLastDate);
                intent.putExtra(BasicInfo.KEY_PLAN_DATETIME, BasicInfo.dateTimeFormat.format(curDate.getTime()));

                startActivityForResult(intent, BasicInfo.REQ_PLAN_INSERT_ACTIVITY);
            }
        });

        mPlanListView = (ListView) findViewById(R.id.planListView);
        mPlanListAdapter = new PlanListAdapter(this);
        mPlanListView.setAdapter(mPlanListAdapter);

        itemCount = (TextView)findViewById(R.id.itemCount);

        gestureScanner = new GestureDetector(this, gestureScannerListener);
        mPlanListView.setOnTouchListener(gestureListener);
    }

//-------------------------------------------------------------------------------------------------//


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_planlist,menu);
        Log.d(TAG, "onCreateOptionsMenu() called" );

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (planListViewMode_daily){
            if(BasicInfo.language.equals("ko")){
                menu.getItem(3).setTitle("Plan 모아 보기");
            }else if(BasicInfo.language.equals("ja")){
                menu.getItem(3).setTitle("Plan 集めてみる");
            }else{
                menu.getItem(3).setTitle("Plan All");
            }
        }else {
            if(BasicInfo.language.equals("ko")){
                menu.getItem(3).setTitle("Plan 일 별로 보기");
            }else if(BasicInfo.language.equals("ja")){
                menu.getItem(3).setTitle("Plan 日付別に見る");
            }else{
                menu.getItem(3).setTitle("Plan See by Day");
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.tourTitleModify:
                createDialog(BasicInfo.CONFIRM_TOUR_TITLE_MODIFY).show();
                return true;

            case R.id.tourDateModify:
                Intent intent = new Intent(getApplicationContext(),TourDateInsertActivity.class);
                intent.putExtra(BasicInfo.KEY_MODE,BasicInfo.MODE_MODIFY);
                intent.putExtra(BasicInfo.KEY_TOUR_ID, mTourId);
                intent.putExtra(BasicInfo.KEY_TOUR_TITLE, mTourTitle);
                intent.putExtra(BasicInfo.KEY_TOUR_FIRSTDATE,mTourFirstDate);
                intent.putExtra(BasicInfo.KEY_TOUR_LASTDATE,mTourLastDate);
                startActivityForResult(intent, BasicInfo.REQ_TOUR_DATE_INSERT_ACTIVITY);
                break;

            case R.id.tourDelete:
                createDialog(BasicInfo.CONFIRM_TOUR_DELETE).show();
                break;

            case R.id.PlanListViewModeChange:
                Toast.makeText(getApplicationContext(), "PlanList 보기 모드 변경", Toast.LENGTH_SHORT).show();

                if (planListViewMode_daily){
                    loadPlanListDataViewAll();
                } else {
                    loadPlanListData();
                }
                planListViewMode_daily = !planListViewMode_daily;
                break;

            default:
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    View.OnTouchListener gestureListener = new View.OnTouchListener(){
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return  gestureScanner.onTouchEvent(event);
        }

    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureScanner.onTouchEvent(event);
    }

    GestureDetector.SimpleOnGestureListener gestureScannerListener = new GestureDetector.SimpleOnGestureListener(){
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            if (!planListViewMode_daily) return false;

            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) return false;

                // right to left swipe
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    setNextDay();
                }
                // left to right swipe
                else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    setPreDay();
                }

            } catch (Exception e) {   }
            return true;
        }
    };


    protected void onStart() {
        loadPlanListData();
        super.onStart();
    }

    public void processIntent(Intent intent) {

        mTourId = intent.getStringExtra(BasicInfo.KEY_TOUR_ID);
        mTourTitle = intent.getStringExtra(BasicInfo.KEY_TOUR_TITLE);
        mTourFirstDate = intent.getStringExtra(BasicInfo.KEY_TOUR_FIRSTDATE);
        mTourLastDate = intent.getStringExtra(BasicInfo.KEY_TOUR_LASTDATE);

        //  first/last (String -> Date)
        firstDate = new Date();
        try {
            firstDate = BasicInfo.datetimeFormat_Date.parse(mTourFirstDate);
        } catch (Exception ex) {
            Log.d(TAG, "Exception in parsing date : " + mTourFirstDate);
        }

        lastDate = new Date();
        try {
            lastDate = BasicInfo.datetimeFormat_Date.parse(mTourLastDate);
        } catch (Exception ex) {
            Log.d(TAG, "Exception in parsing date : " + mTourLastDate);
        }

        tourTitleTxt.setText(mTourTitle);
        curDate.setTime(firstDate);

        dayIndex = 0;
        dayIndexMax =0;

        setTourPeriodTxtView();
        setCurDateTxtView();

    }





    /**
     * setCalendar
     * DATE / TIME Btn click listener
     * <p>
     * <p>
     * private void setCalendar(){
     * insert_dateBtn = (Button)findViewById(R.id.insert_dateBtn);
     * insert_dateBtn.setOnClickListener(new View.OnClickListener() {
     *
     * @Override public void onClick(View v) {
     * String mDateStr = insert_dateBtn.getText().toString();          // 버튼값 mDateStr 에 넣기
     * <p>
     * Calendar calendar = Calendar.getInstance();
     * Date date = new Date();
     * try {
     * date = BasicInfo.datetimeFormat_Date.parse(mDateStr);
     * <p>
     * } catch(Exception ex) {
     * Log.d(TAG, "Exception in parsing date : " + date);
     * }
     * calendar.setTime(date);
     * <p>
     * new DatePickerDialog(
     * PlanListActivity.this,
     * dateSetListener,
     * calendar.get(Calendar.YEAR),
     * calendar.get(Calendar.MONTH),
     * calendar.get(Calendar.DAY_OF_MONTH)
     * ).show();
     * }
     * });
     * <p>
     * insert_timeBtn = (Button) findViewById(R.id.);
     * insert_timeBtn.setOnClickListener(new View.OnClickListener() {
     * public void onClick(View v) {
     * String mTimeStr = insert_timeBtn.getText().toString();
     * Calendar calendar = Calendar.getInstance();
     * Date date = new Date();
     * try {
     * date = BasicInfo.datetimeFormat_Time.parse(mTimeStr);
     * } catch(Exception ex) {
     * Log.d(TAG, "Exception in parsing date : " + date);
     * }
     * <p>
     * calendar.setTime(date);
     * <p>
     * new TimePickerDialog(
     * PlanListActivity.this,
     * timeSetListener,
     * calendar.get(Calendar.HOUR_OF_DAY),
     * calendar.get(Calendar.MINUTE),
     * true
     * ).show();
     * <p>
     * }
     * });
     * <p>
     * Date curDate = new Date();
     * mCalendar.setTime(curDate);
     * <p>
     * int year = mCalendar.get(Calendar.YEAR);
     * int monthOfYear = mCalendar.get(Calendar.MONTH);
     * int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);
     * <p>
     * String monthStr = String.valueOf(monthOfYear+1);
     * if (monthOfYear < 9) {
     * monthStr = "0" + monthStr;
     * }
     * <p>
     * String dayStr = String.valueOf(dayOfMonth);
     * if (dayOfMonth < 10) {
     * dayStr = "0" + dayStr;
     * }
     * <p>
     * insert_dateBtn.setText(year + "-" + monthStr + "-" + dayStr);
     * <p>
     * int hourOfDay = mCalendar.get(Calendar.HOUR_OF_DAY);
     * int minute = mCalendar.get(Calendar.MINUTE);
     * <p>
     * String hourStr = String.valueOf(hourOfDay);
     * if (hourOfDay < 10) {
     * hourStr = "0" + hourStr;
     * }
     * <p>
     * String minuteStr = String.valueOf(minute);
     * if (minute < 10) {
     * minuteStr = "0" + minuteStr;
     * }
     * <p>
     * insert_timeBtn.setText(hourStr + ":" + minuteStr);
     * <p>
     * }
     */


    /**
     * 여행 기간 n 구하기 (n-1박 n일)
     */
    private void setTourPeriodTxtView() {

        String firstdate = BasicInfo.datetimeFormat_Date.format(firstDate);
        String lastdate = BasicInfo.datetimeFormat_Date.format(lastDate);

        Log.d(TAG, "setTourPeriodTxtView() called / firstDate : "+firstdate+" lastDate : "+lastdate );

        tourPeriod = (int)(lastDate.getTime()-firstDate.getTime())/(24 * 60 * 60 * 1000) + 1;
        dayIndexMax = tourPeriod-1;

        Log.d(TAG, "tourPeriod : " +tourPeriod+ " dayIndexMax : "+dayIndexMax);


        if(BasicInfo.language.equals("ko")){
            tourPeriodTxt.setText(tourPeriod-1 + "박 "+tourPeriod+"일 ["+ mTourFirstDate+"~"+mTourLastDate+"]");
        }else if(BasicInfo.language.equals("ja")){
            tourPeriodTxt.setText(tourPeriod-1 + "泊 "+tourPeriod+"日 ["+ mTourFirstDate+"~"+mTourLastDate+"]");
        }else{
            tourPeriodTxt.setText(tourPeriod-1 + "night "+tourPeriod+"days ["+ mTourFirstDate+"~"+mTourLastDate+"]");
        }

    }
    private void setCurDateTxtView(){

        if(BasicInfo.language.equals("ko")){
            nDayTxt.setText((dayIndex+1) + " 일차");
        }else if(BasicInfo.language.equals("ja")){
            nDayTxt.setText((dayIndex+1) + " 日目");
        }else{
            nDayTxt.setText("The "+ (dayIndex+1) + " Day");
        }


        curDateTxt.setText("( "+BasicInfo.datetimeFormat_Date.format(curDate.getTime())+" )");

    }




    public void setPreDay() {
        Log.d(TAG, "setPreDay() called" );

        if (dayIndex <= 0){
            if(BasicInfo.language.equals("ko")){
                Toast.makeText(getApplicationContext(),"첫째 날입니다.",Toast.LENGTH_SHORT).show();
            }else if(BasicInfo.language.equals("ja")){
                Toast.makeText(getApplicationContext(),"初日です。",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(),"It's first day.",Toast.LENGTH_SHORT).show();
            }


        }else{
            curDate.add(Calendar.DATE,-1);
            dayIndex -= 1;
            setCurDateTxtView();
            loadPlanListData();
        }

    }

    public void setNextDay() {
        Log.d(TAG, "setNextDay() called" );

        if(dayIndex==dayIndexMax){

            if(BasicInfo.language.equals("ko")){
                Toast.makeText(getApplicationContext(),"마지막 날입니다.",Toast.LENGTH_SHORT).show();
            }else if(BasicInfo.language.equals("ja")){
                Toast.makeText(getApplicationContext(),"最終日です。",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(),"It's last day.",Toast.LENGTH_SHORT).show();
            }


        }
        else {
            curDate.add(Calendar.DATE,1);
            dayIndex += 1;
            setCurDateTxtView();
            loadPlanListData();
        }

    }


    /**
     * 날짜 설정 리스너
     */
    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mCalendar.set(year, monthOfYear, dayOfMonth);

            String monthStr = String.valueOf(monthOfYear + 1);
            if (monthOfYear < 9) {
                monthStr = "0" + monthStr;
            }

            String dayStr = String.valueOf(dayOfMonth);
            if (dayOfMonth < 10) {
                dayStr = "0" + dayStr;
            }

            insert_dateBtn.setText(year + "-" + monthStr + "-" + dayStr);

        }
    };

    /**
     * 시간 설정 리스너
     */
    TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hour_of_day, int minute) {
            mCalendar.set(Calendar.HOUR_OF_DAY, hour_of_day);
            mCalendar.set(Calendar.MINUTE, minute);

            String hourStr = String.valueOf(hour_of_day);
            if (hour_of_day < 10) {
                hourStr = "0" + hourStr;
            }

            String minuteStr = String.valueOf(minute);
            if (minute < 10) {
                minuteStr = "0" + minuteStr;
            }

            insert_timeBtn.setText(hourStr + ":" + minuteStr);
        }
    };

    /**
     *   TOUR 리스트 데이터 로딩
     */
    public void loadPlanListData()
    {

        if (dayIndex==0){
            prevDayBtn.setEnabled(false);
            prevDayBtn.setBackground(ContextCompat.getDrawable(this,R.drawable.ic_left_disable));
        }else{
            prevDayBtn.setEnabled(true);
            prevDayBtn.setBackground(ContextCompat.getDrawable(this,R.drawable.ic_left_24dp));
        }


        if (dayIndex==dayIndexMax){
            nextDayBtn.setEnabled(false);
            nextDayBtn.setBackground(ContextCompat.getDrawable(this,R.drawable.ic_right_disable));
        }else{
            nextDayBtn.setEnabled(true);
            nextDayBtn.setBackground(ContextCompat.getDrawable(this,R.drawable.ic_right_24dp));

        }

        Log.d(TAG, " load the PlanListData ");
        dateLayout.setVisibility(View.VISIBLE);

        String SQL = "select _id, TOUR_ID, PLAN_DATETIME, PLAN_TITLE, PLAN_BODY from "+ PlanDatabase.TABLE_PLAN
                +" WHERE TOUR_ID = '"+mTourId+ "' AND"
                +" PLAN_DATETIME like '"+BasicInfo.datetimeFormat_Date.format(curDate.getTime())+"%'" +
                " order by PLAN_DATETIME asc";

        int recordCount = -1;
        if (MainActivity.mDatabase != null) {
            Cursor outCursor = MainActivity.mDatabase.rawQuery(SQL);

            recordCount = outCursor.getCount();
            Log.d(TAG, "cursor count : " + recordCount + "\n");

            mPlanListAdapter.clear();

            for (int i = 0; i < recordCount; i++) {
                outCursor.moveToNext();

                String planId = outCursor.getString(0);
                String tourId = outCursor.getString(1);
                String planDateTimeStr = outCursor.getString(2);
                if (planDateTimeStr != null) {
                    try {
                        Date inDate = BasicInfo.dateTimeFormat.parse(planDateTimeStr);
                        planDateTimeStr = BasicInfo.dateTimeFormat.format(inDate);
                    } catch(Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    planDateTimeStr = "";
                }

                String planTitle = outCursor.getString(3);
                String planBody = outCursor.getString(4);

                mPlanListAdapter.addItem(new PlanListItem(planId, tourId, planDateTimeStr, planTitle, planBody));
            }

            outCursor.close();
            mPlanListAdapter.notifyDataSetChanged();
            itemCount.setText(recordCount + " Plan Item");
            itemCount.invalidate();
            Log.d(TAG, " load plan Items = "+recordCount);
        }
    }

    /**
     *   TOUR 리스트 데이터 로딩
     */
    public void loadPlanListDataViewAll(){

        dateLayout.setVisibility(View.GONE);


        Log.d(TAG, " load the PlanListData View All ");

        String SQL = "select _id, TOUR_ID, PLAN_DATETIME, PLAN_TITLE, PLAN_BODY from "+ PlanDatabase.TABLE_PLAN
                +" WHERE TOUR_ID = '"+mTourId+ "'"
                +" order by PLAN_DATETIME asc";

        int recordCount = -1;
        if (MainActivity.mDatabase != null) {
            Cursor outCursor = MainActivity.mDatabase.rawQuery(SQL);

            recordCount = outCursor.getCount();
            Log.d(TAG, "cursor count : " + recordCount + "\n");

            mPlanListAdapter.clear();

            for (int i = 0; i < recordCount; i++) {
                outCursor.moveToNext();

                String planId = outCursor.getString(0);
                String tourId = outCursor.getString(1);
                String planDateTimeStr = outCursor.getString(2);
                if (planDateTimeStr != null) {
                    try {
                        Date inDate = BasicInfo.dateTimeFormat.parse(planDateTimeStr);
                        planDateTimeStr = BasicInfo.dateTimeFormat.format(inDate);
                    } catch(Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    planDateTimeStr = "";
                }

                String planTitle = outCursor.getString(3);
                String planBody = outCursor.getString(4);

                mPlanListAdapter.addItem(new PlanListItem(planId, tourId, planDateTimeStr, planTitle, planBody));
            }

            outCursor.close();
            mPlanListAdapter.notifyDataSetChanged();
            itemCount.setText(recordCount + " Plan Item");
            itemCount.invalidate();
            Log.d(TAG, " load plan Items = "+recordCount);
        }
    }



    public void viewPlanList(int position){

        PlanListItem item = (PlanListItem)mPlanListAdapter.getItem(position);

        Intent intent = new Intent(getApplicationContext(), PlanInsertActivity.class);
        intent.putExtra(BasicInfo.KEY_MODE, BasicInfo.MODE_VIEW);
        intent.putExtra(BasicInfo.KEY_PLAN_ID, item.getId());
        intent.putExtra(BasicInfo.KEY_TOUR_ID, item.getData(0));
        intent.putExtra(BasicInfo.KEY_PLAN_DATETIME, item.getData(1));
        intent.putExtra(BasicInfo.KEY_PLAN_TITLE, item.getData(2));
        intent.putExtra(BasicInfo.KEY_PLAN_BODY, item.getData(3));

        intent.putExtra(BasicInfo.KEY_TOUR_FIRSTDATE,mTourFirstDate);
        intent.putExtra(BasicInfo.KEY_TOUR_LASTDATE,mTourLastDate);

        startActivityForResult(intent, BasicInfo.REQ_PLAN_INSERT_VIEW_ACTIVITY);           // plan insert / view mode
    }

    /**
     * 다른 액티비티의 응답 처리
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case BasicInfo.REQ_PLAN_LIST_ACTIVITY:
                if(resultCode == RESULT_OK) {
                    loadPlanListData();
                }
                break;
            case BasicInfo.REQ_TOUR_DATE_INSERT_ACTIVITY:
                if(resultCode == RESULT_OK) {
                    processIntent(data);
                    loadPlanListData();
                }
                break;


        }
    }


    protected Dialog createDialog(int id) {

        AlertDialog.Builder builder = null;


        switch(id) {
            case BasicInfo.PLAN_LIST_OPT:

                final CharSequence[] items = {"Tour Title 수정하기", "Tour 날짜 수정하기", "삭제하기", "PlaList 보기 모드 변경"};

                builder = new AlertDialog.Builder(this);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                Toast.makeText(getApplicationContext(), items[which], Toast.LENGTH_SHORT).show();
                                createDialog(BasicInfo.CONFIRM_TOUR_TITLE_MODIFY).show();
                                break;
                            case 1:
                                Toast.makeText(getApplicationContext(), items[which], Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(),TourDateInsertActivity.class);
                                intent.putExtra(BasicInfo.KEY_MODE,BasicInfo.MODE_MODIFY);
                                intent.putExtra(BasicInfo.KEY_TOUR_ID, mTourId);
                                intent.putExtra(BasicInfo.KEY_TOUR_TITLE, mTourTitle);
                                intent.putExtra(BasicInfo.KEY_TOUR_FIRSTDATE,mTourFirstDate);
                                intent.putExtra(BasicInfo.KEY_TOUR_LASTDATE,mTourLastDate);
                                startActivityForResult(intent, BasicInfo.REQ_TOUR_DATE_INSERT_ACTIVITY);
                                break;
                            case 2:
                                Toast.makeText(getApplicationContext(), items[which], Toast.LENGTH_SHORT).show();
                                createDialog(BasicInfo.CONFIRM_TOUR_DELETE).show();
                                break;

                            case 3:
                                Toast.makeText(getApplicationContext(), items[which], Toast.LENGTH_SHORT).show();
                                Toast.makeText(getApplicationContext(), "PlanList 보기 모드 변경", Toast.LENGTH_SHORT).show();

                                if (planListViewMode_daily){
                                    loadPlanListDataViewAll();
                                } else {
                                    loadPlanListData();
                                }

                                planListViewMode_daily = !planListViewMode_daily;


                            default:
                                break;
                        }
                    }

                });
                break;
            case BasicInfo.CONFIRM_TOUR_TITLE_MODIFY:

                final EditText editTitle = new EditText(this);
                editTitle.setText(mTourTitle);
                editTitle.setSelection(editTitle.length());
                builder = new AlertDialog.Builder(this);
                builder.setTitle("Tour Title Modify");
                builder.setView(editTitle);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mTourTitle = editTitle.getText().toString().trim();
                        tourTitleTxt.setText(mTourTitle);

                        Log.d(TAG, "update TOUR record : " + mTourId);
                        SQL = "update " + PlanDatabase.TABLE_TOUR +
                                " set " +
                                "TOUR_TITLE = '" + mTourTitle + "' " +
                                "where _id = '" + mTourId + "'";

                        Log.d(TAG, "SQL : " + SQL);
                        if (MainActivity.mDatabase != null) {
                            MainActivity.mDatabase.execSQL(SQL);
                        }



                    }
                });
                builder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                break;

            case BasicInfo.CONFIRM_TOUR_DELETE:
                builder = new AlertDialog.Builder(this);
                builder.setTitle(mTourTitle +"\n이 TOUR를 삭제하시겠습니까 ?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tourTitleTxt.setText(mTourTitle);

                        Log.d(TAG, "deleting previous TOUR record : " + mTourId);
                        SQL = "delete from " + PlanDatabase.TABLE_TOUR +
                                " where _id = '" + mTourId + "'";
                        Log.d(TAG, "SQL : " + SQL);
                        if (MainActivity.mDatabase != null) {
                            MainActivity.mDatabase.execSQL(SQL);
                        }
                        finish();


                    }
                });
                builder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                break;



            default:
                break;
        }

        return builder.create();
    }

}

