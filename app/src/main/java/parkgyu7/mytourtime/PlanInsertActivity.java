package parkgyu7.mytourtime;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import parkgyu7.mytourtime.db.PlanDatabase;

public class PlanInsertActivity extends AppCompatActivity {

    public static final String TAG = " ** PlanInsert 액티비티 ";


    // DB Str 변수

    //  -> plan //
    String mPlanMode;               // MODE
    String mTourId;                 // Tour Id
    String mPlanId;                 // Plan Id
    String mPlanDateTime;           // 날짜+시간

    String mTourFirstDate;
    String mTourLastDate;

    // plan -> //
    String mPlanDateTimeStr;         // (저장) plan 날짜+시간 값 -> DB 처리 변수
    String mPlanTitle;               // plan Title
    String mPlanBody;                // plan Body
    String mTimeStr;                 // (예제용 삭제하기)

    Date planDateTime;
    Calendar mCalendar = Calendar.getInstance();
    Date tourFirstDate;
    Date tourLastDate;

    Intent intent;

    Button planDateBtn;
    Button planTimeBtn;

    EditText planTitleEditTxt;
    EditText planBodyEditTxt;

    Button planSaveBtn;
    Button planCancelBtn;
    Button deleteBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_insert);
        Log.d(TAG, "  * PlanInsert 액티비티 Open ");

        planTitleEditTxt = (EditText)findViewById(R.id.planTitleEdit);
        planBodyEditTxt = (EditText)findViewById(R.id.planBodyEdit);
        deleteBtn = (Button)findViewById(R.id.deleteBtn);

        setCalender();
        intent = getIntent();
        processIntent(intent);
        Toast.makeText(this,
                "Plan Id : "+mPlanId+
                "firstDate : "+mTourFirstDate+" / lastDate : "+mTourLastDate,Toast.LENGTH_SHORT).show();

        setBottomButtons();

        // MODE : new / modify 구분
        Log.d(TAG, "  * mPlanMode : ** " + mPlanMode);
        if (mPlanMode.equals(BasicInfo.MODE_MODIFY) || mPlanMode.equals(BasicInfo.MODE_VIEW)) {
            setTitle("계획 수정");
            planSaveBtn.setText("modify");
            deleteBtn.setVisibility(View.VISIBLE);
        } else {
            setTitle("새로운 계획 추가");
            planSaveBtn.setText("save");
            deleteBtn.setVisibility(View.GONE);
        }

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletePlan();
            }
        });


    }

    public void processIntent(Intent intent) {

        mPlanMode = intent.getStringExtra(BasicInfo.KEY_PLAN_MODE);
        mTourId = intent.getStringExtra(BasicInfo.KEY_TOUR_ID);
        mPlanId = intent.getStringExtra(BasicInfo.KEY_PLAN_ID);
        mPlanDateTime = intent.getStringExtra(BasicInfo.KEY_PLAN_DATETIME);
        setPlanDate(mPlanDateTime);                                             // 날짜시간버튼 초기세팅
        mPlanTitle = intent.getStringExtra(BasicInfo.KEY_PLAN_TITLE);
        planTitleEditTxt.setText(mPlanTitle);
        mPlanBody = intent.getStringExtra(BasicInfo.KEY_PLAN_BODY);
        planBodyEditTxt.setText(mPlanBody);
        mTourFirstDate = intent.getStringExtra(BasicInfo.KEY_TOUR_FIRSTDATE);
        mTourLastDate = intent.getStringExtra(BasicInfo.KEY_TOUR_LASTDATE);


    }
    /**
     *  하단 메뉴 버튼 설정 (저장 / 닫기)
     */
    public void setBottomButtons() {

        planSaveBtn = (Button)findViewById(R.id.planSaveBtn);
        planCancelBtn = (Button)findViewById(R.id.planCancelBtn);

        // 저장 버튼
        planSaveBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean isParsed = parseValues();   // 시간 / 날짜 입력값 확인
                if (isParsed) {
                    if(mPlanMode.equals(BasicInfo.MODE_INSERT)) {
                        saveInput();
                    } else if(mPlanMode.equals(BasicInfo.MODE_MODIFY) || mPlanMode.equals(BasicInfo.MODE_VIEW)) {
                        modifyInput();
                    }
                }
            }
        });
        // 닫기 버튼
        planCancelBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     *  데이터베이스 PLAN 레코드 추가
     */
    private void saveInput() {
        Log.d(TAG, " ** saveInput 저장하기 **");

        // insert PLAN table
        String SQL = null;


        SQL = "insert into " + PlanDatabase.TABLE_PLAN +
                "(TOUR_ID, PLAN_DATETIME, PLAN_TITLE, PLAN_BODY) values(" +
                "'"+ mTourId + "', " +                          // TOUR_ID
                "DATETIME('" + mPlanDateTimeStr + "'), " +      // PLAN 날짜 + 시간  (format : "yyyy-MM-dd HH:mm:ss")
                "'"+ mPlanTitle + "', " +                       // PLAN SIMPLE
                "'"+ mPlanBody + "')";  		                // PLAN Body

        Log.d(TAG, "SQL : " + SQL);
        if (MainActivity.mDatabase != null) {
            MainActivity.mDatabase.execSQL(SQL);
        }
        Intent intent = getIntent();
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * 데이터베이스 레코드 수정
     */
    private void modifyInput(){

        Intent intent = getIntent();

        String SQL = null;
        // update memo info TABLE_PLAN : TOUR_ID, PLAN_DATETIME, PLAN_TITLE, PLAN_BODY
        SQL = "update " + PlanDatabase.TABLE_PLAN +
                " set " +
                " PLAN_DATETIME = DATETIME('" + mPlanDateTimeStr + "'), " +
                " PLAN_TITLE = '" + mPlanTitle + "', " +
                " PLAN_BODY = '" + mPlanBody + "'" +
                " where _id = '" + mPlanId + "'";

        Log.d(TAG, "SQL : " + SQL);
        if (MainActivity.mDatabase != null) {
            MainActivity.mDatabase.execSQL(SQL);
        }
        setResult(RESULT_OK, intent);
        finish();
    }
    /**
     *  Plan Delete
     */
    private void deletePlan() {

        // delete plan record
        Log.d(TAG, "deleting previous plan record : " + mPlanId);
        Toast.makeText(this, "delete plan id : "+ mPlanId, Toast.LENGTH_LONG).show();

        String SQL = "delete from " + PlanDatabase.TABLE_PLAN +
                " where _id = '" + mPlanId + "'";
        Log.d(TAG, "SQL : " + SQL);
        if (MainActivity.mDatabase != null) {
            MainActivity.mDatabase.execSQL(SQL);
        }

        setResult(RESULT_OK);
        finish();
    }

    /**
     *  DATE/TIME Btn -> 클릭 시, 버튼 값을 picker에 넘기기
     */
    private void setCalender() {

        planDateBtn = (Button)findViewById(R.id.planDateBtn);

        planDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mDateStr = planDateBtn.getText().toString();          // 버튼값 mDateStr 에 넣기
                Calendar calendar = Calendar.getInstance();
                try {
                    planDateTime = BasicInfo.datetimeFormat_Date.parse(mDateStr);
                    tourFirstDate = BasicInfo.datetimeFormat_Date.parse(mTourFirstDate);
                    tourLastDate = BasicInfo.datetimeFormat_Date.parse(mTourLastDate);

                } catch(Exception ex) {
                    Log.d(TAG, "Exception in parsing date : " + planDateTime);
                }
                calendar.setTime(planDateTime);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        PlanInsertActivity.this,
                        dateSetListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                );
                datePickerDialog.getDatePicker().setMinDate(tourFirstDate.getTime());
                datePickerDialog.getDatePicker().setMaxDate(tourLastDate.getTime());

                datePickerDialog.show();



            }
        });

        planTimeBtn = (Button) findViewById(R.id.planTimeBtn);
        planTimeBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String mTimeStr = planTimeBtn.getText().toString();
                Calendar calendar = Calendar.getInstance();
                Date date = new Date();
                try {
                    date = BasicInfo.datetimeFormat_Time.parse(mTimeStr);
                } catch(Exception ex) {
                    Log.d(TAG, "Exception in parsing date : " + date);
                }

                calendar.setTime(date);

                new TimePickerDialog(
                        PlanInsertActivity.this,
                        timeSetListener,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                ).show();

            }
        });




    }

    // mPlanDate 변수를 받아 insert_dateBtn, insert_timeBtn에 날짜/시간 넣기

    private void setPlanDate(String dateStr) {
        Log.d(TAG, "  * setPlanDate() called :  " + dateStr);

        Date curDate = new Date();
        Date curTime = new Date();
        // dateString -> date  형변환
        try {
            curDate = BasicInfo.dateTimeFormat.parse(dateStr);
            curTime = BasicInfo.dateTimeFormat.parse(dateStr);
        } catch(Exception ex) {
            Log.d(TAG, "Exception in parsing date : " + dateStr);
        }
        planDateBtn.setText(BasicInfo.datetimeFormat_Date.format(curDate));

        if (mPlanMode.equals(BasicInfo.MODE_INSERT)){

        }else {
            planTimeBtn.setText( BasicInfo.datetimeFormat_Time.format(curTime));
        }

    }


    /**
     * 날짜 설정 리스너
     */
    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

        mCalendar.set(year, monthOfYear, dayOfMonth);

        String monthStr = String.valueOf(monthOfYear+1);
        if (monthOfYear < 9) {
            monthStr = "0" + monthStr;
        }

        String dayStr = String.valueOf(dayOfMonth);
        if (dayOfMonth < 10) {
            dayStr = "0" + dayStr;
        }


        planDateBtn.setText(year + "-" + monthStr + "-" + dayStr);

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
        planTimeBtn.setText(hourStr + ":" + minuteStr);

        }
    };


    /**
     *      Date/Time/ Plan 입력값 확인
     */
    private boolean parseValues() {

        String insertDateStr = planDateBtn.getText().toString();     // insert_dateBtn 버튼 값 받아오기
        String insertTimeStr = planTimeBtn.getText().toString();     // insert_timeBtn 버튼 값 받아오기
        // todo 시간이 입력되지 않으면 00:00 값으로 넘기기 (나중에 '공지'같은 기능으로 넣기)

        if (insertTimeStr.equals("")){
            insertTimeStr = "00:00";
        }


        String srcDateStr = insertDateStr + " " + insertTimeStr;                // srcDateStr = date + time
        Log.d(TAG, " ** source Date string : " + srcDateStr);

        try {
                Date insertDate = BasicInfo.dateTimeFormat.parse(srcDateStr);   // (String) srcDateStr -> (Date) insertDate 변환
                mPlanDateTimeStr = BasicInfo.dateTimeFormat.format(insertDate); // (Date) insertDate -> (String) "yyyy-MM-dd HH:mm" -> DB 저장용
        } catch(ParseException ex) {
            Log.e(TAG, "Exception in parsing date : " + insertDateStr);
        }

        mPlanTitle = planTitleEditTxt.getText().toString();
        // check text Plan
        if (mPlanTitle.trim().length() < 1) {

            Toast.makeText(getApplicationContext(),"계획을 작성해 주세요",Toast.LENGTH_LONG).show();
            return false;
        }


        mPlanBody = planBodyEditTxt.getText().toString();
        if (mPlanBody.trim().length() < 1) {
            mPlanBody="";
        }

        return true;
    }
}
