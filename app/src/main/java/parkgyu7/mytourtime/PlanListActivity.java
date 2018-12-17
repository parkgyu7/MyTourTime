package parkgyu7.mytourtime;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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

    EditText tourTitleTxt;
    TextView tourPeriodTxt;
    TextView curDateTxt;
    TextView itemCount;

    Button prevDayBtn;
    Button nextDayBtn;
    Button addPlanBtn;


    Button insert_dateBtn;
    Button insert_timeBtn;

    ListView mPlanListView;
    PlanListAdapter mPlanListAdapter;



    Calendar mCalendar = Calendar.getInstance();

    Date firstDate;
    Date lastDate;

    Calendar curDate = Calendar.getInstance();

    int tourPeriod;
    int dayIndex;
    int dayIndexMax;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_list);

        intent = getIntent();
        processIntent(intent);

        // title setting
        tourTitleTxt = (EditText) findViewById(R.id.tourTitleTxt);
        tourTitleTxt.setText(mTourTitle);

        // tour period txt setting
        tourPeriodTxt = (TextView) findViewById(R.id.tourPeriodTxt);
        setTourPeriod();

        curDateTxt = (TextView)findViewById(R.id.curDateTxt);
        curDate.setTime(firstDate);
        dayIndex = 0;
        dayIndexMax = tourPeriod-1;
        curDay();

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

        addPlanBtn = (Button)findViewById(R.id.addPlanBtn);
        addPlanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PlanInsertActivity.class);
                intent.putExtra(BasicInfo.KEY_PLAN_MODE, BasicInfo.MODE_INSERT);
                intent.putExtra(BasicInfo.KEY_TOUR_ID, mTourId);
                intent.putExtra(BasicInfo.KEY_TOUR_FIRSTDATE,mTourFirstDate);
                intent.putExtra(BasicInfo.KEY_TOUR_LASTDATE,mTourLastDate);

                intent.putExtra(BasicInfo.KEY_PLAN_DATETIME, BasicInfo.dateTimeFormat.format(curDate.getTime()));
                startActivityForResult(intent, BasicInfo.REQ_PLAN_INSERT_ACTIVITY);
            }
        });

        /**
         *  plan 리스트
         */
        mPlanListView = (ListView) findViewById(R.id.planListView);
        mPlanListAdapter = new PlanListAdapter(this);
        mPlanListView.setAdapter(mPlanListAdapter);


        itemCount = (TextView)findViewById(R.id.itemCount);

    }

    protected void onStart() {
        loadPlanListData();     // 메모 데이터 로딩
        super.onStart();
    }

    public void processIntent(Intent intent) {

        mTourId = intent.getStringExtra(BasicInfo.KEY_TOUR_ID);
        Toast.makeText(this, "TOUR ID : " + mTourId, Toast.LENGTH_LONG).show();

        mTourTitle = intent.getStringExtra(BasicInfo.KEY_TOUR_TITLE);
        mTourFirstDate = intent.getStringExtra(BasicInfo.KEY_TOUR_FIRSTDATE);
        mTourLastDate = intent.getStringExtra(BasicInfo.KEY_TOUR_LASTDATE);

        Toast.makeText(this, "title : " + mTourTitle +
                "\nfirstDate : " + mTourFirstDate +
                "\nlastDate : " + mTourLastDate, Toast.LENGTH_LONG).show();


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
    private void setTourPeriod() {
        Log.d(TAG, "setTourPeriod() called" );

        tourPeriod = (int)(lastDate.getTime()-firstDate.getTime())/(24 * 60 * 60 * 1000) + 1;
        tourPeriodTxt.setText(tourPeriod-1 + "박 "+tourPeriod+"일 ["+ mTourFirstDate+"~"+mTourLastDate+"]");
    }


    private void curDay(){
        Log.d(TAG, "curDay() called" );
        curDateTxt.setText(dayIndex+1 + "일차 ["+ BasicInfo.datetimeFormat_Date.format(curDate.getTime())+"]");
    }


    public void setPreDay() {
        Log.d(TAG, "setPreDay() called" );

        if (dayIndex <= 0){
            Toast.makeText(getApplicationContext(),"첫째 날입니다.",Toast.LENGTH_LONG).show();

        }else{
            curDate.add(Calendar.DATE,-1);
            dayIndex -= 1;
            curDay();
            loadPlanListData();
        }

    }
    public void setNextDay() {
        Log.d(TAG, "setNextDay() called" );

        if(dayIndex>=dayIndexMax){
            Toast.makeText(getApplicationContext(),"마지막 날입니다.",Toast.LENGTH_LONG).show();
        }
        else {
            curDate.add(Calendar.DATE,1);
            dayIndex += 1;
            curDay();
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
        Log.d(TAG, " load the PlanListData ");
        Toast.makeText(this, "loadPlanListData",Toast.LENGTH_LONG).show();

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

    public void viewPlanList(int position){

        PlanListItem item = (PlanListItem)mPlanListAdapter.getItem(position);

        Intent intent = new Intent(getApplicationContext(), PlanInsertActivity.class);
        intent.putExtra(BasicInfo.KEY_PLAN_MODE, BasicInfo.MODE_VIEW);
        intent.putExtra(BasicInfo.KEY_PLAN_ID, item.getId());
        intent.putExtra(BasicInfo.KEY_TOUR_ID, item.getData(0));
        intent.putExtra(BasicInfo.KEY_PLAN_DATETIME, item.getData(1));
        intent.putExtra(BasicInfo.KEY_PLAN_TITLE, item.getData(2));
        intent.putExtra(BasicInfo.KEY_PLAN_BODY, item.getData(3));

        startActivityForResult(intent, BasicInfo.REQ_PLAN_VIEW_ACTIVITY);
    }
    /**
     * 다른 액티비티의 응답 처리
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case BasicInfo.REQ_TOUR_VIEW_ACTIVITY:
                if(resultCode == RESULT_OK) {
                    loadPlanListData();
                }

                break;

            case BasicInfo.REQ_TOUR_INSERT_ACTIVITY:
                loadPlanListData();
                break;

        }
    }

}

