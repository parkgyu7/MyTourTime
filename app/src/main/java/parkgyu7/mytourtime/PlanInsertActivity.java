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
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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
    String mPlanTitle;               // plan Title
    String mPlanBody;                // plan Body

    Date planDateTime;
    Calendar mCalendar = Calendar.getInstance();
    Date tourFirstDate;
    Date tourLastDate;

    Intent intent;



    Button planDateBtn;
    Button planTimeBtn;
    TextView planDateTxtView;
    TextView planTimeTxtView;


    EditText planTitleEditTxt;
    EditText planBodyEditTxt;
    TextView planTitleView;
    TextView planBodyView;

    Button planModifyBtn;
    Button planSaveBtn;

    Button planCancelBtn;
    Button deleteBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_insert);
        Log.d(TAG, "  * PlanInsert 액티비티 Open ");

        // ////////////////// view matching /////////////////////////
        // - head
        planDateBtn = (Button)findViewById(R.id.planDateBtn);
        planTimeBtn = (Button) findViewById(R.id.planTimeBtn);


        planDateTxtView = (TextView)findViewById(R.id.planDateTxtView);
        planTimeTxtView = (TextView)findViewById(R.id.planTimeTxtView);

        // - contents
        planTitleEditTxt = (EditText)findViewById(R.id.planTitleEdit);
        planBodyEditTxt = (EditText)findViewById(R.id.planBodyEdit);
        planTitleView = (TextView)findViewById(R.id.planTitleView);
        planBodyView = (TextView)findViewById(R.id.planBodyView);

        // - bottom
        planSaveBtn = (Button)findViewById(R.id.planSaveBtn);
        planCancelBtn = (Button)findViewById(R.id.planCancelBtn);
        deleteBtn = (Button)findViewById(R.id.deleteBtn);
        planModifyBtn = (Button)findViewById(R.id.planModifyBtn);
        //////////////////////////////////////////////////////////


        intent = getIntent();
        processIntent(intent);

        setCalender();
        setBottomButtons();

    }

    public void setByMode(String mode, String dateStr){

        Log.d(TAG, "  *mPlanMode* : " + mode);

        Date curDate = new Date();
        Date curTime = new Date();
        // dateString -> date  형변환
        try {
            curDate = BasicInfo.dateTimeFormat.parse(dateStr);
            curTime = BasicInfo.dateTimeFormat.parse(dateStr);
        } catch(Exception ex) {
            Log.d(TAG, "Exception in parsing date : " + dateStr);
        }

        String planDate = BasicInfo.datetimeFormat_Date.format(curDate);
        String planTime = BasicInfo.datetimeFormat_Time.format(curTime);


        if (mode.equals(BasicInfo.MODE_INSERT)){    // new plan
            setTitle("new Plan");
            planSaveBtn.setText("save");
            deleteBtn.setVisibility(View.GONE);

            // view value set
            planDateTxtView.setText(planDate);


            if(BasicInfo.language.equals("ko")){
                planTimeTxtView.setHint("시간을 정해 주세요.");
            }else if(BasicInfo.language.equals("ja")){
                planTimeTxtView.setHint("時間を決めてください。");
            }else{
                planTimeTxtView.setHint("Please fix the time.");
            }






        } else if (mode.equals(BasicInfo.MODE_VIEW)){      // view plan
            setTitle("view Plan");

            // -> GONE
            planDateBtn.setVisibility(View.INVISIBLE);
            planTimeBtn.setVisibility(View.INVISIBLE);

            planTitleEditTxt.setVisibility(View.GONE);
            planBodyEditTxt.setVisibility(View.GONE);

            // -> VISIBLE

            planTitleView.setVisibility(View.VISIBLE);
            planBodyView.setVisibility(View.VISIBLE);

            // bottom btn set
            planSaveBtn.setVisibility(View.GONE);
            planModifyBtn.setVisibility(View.VISIBLE);
            deleteBtn.setVisibility(View.VISIBLE);
            planCancelBtn.setText("Close");

            // view value set
            planDateTxtView.setText(planDate);
            planTimeTxtView.setText(planTime);
            planTitleView.setText(mPlanTitle);
            planBodyView.setText(mPlanBody);

        }  else if (mode.equals(BasicInfo.MODE_MODIFY) ) {         // modify plan
            setTitle("modify Plan");
            // -> VISIBLE
            planDateBtn.setVisibility(View.VISIBLE);
            planTimeBtn.setVisibility(View.VISIBLE);

            planTitleEditTxt.setVisibility(View.VISIBLE);
            planTitleEditTxt.setSelection(planTitleEditTxt.length());
            planBodyEditTxt.setVisibility(View.VISIBLE);

            // -> GONE
            planTitleView.setVisibility(View.GONE);
            planBodyView.setVisibility(View.GONE);

            // bottom btn set
            planSaveBtn.setVisibility(View.VISIBLE);
            planModifyBtn.setVisibility(View.GONE);
            deleteBtn.setVisibility(View.VISIBLE);
            planCancelBtn.setText("Cancle");

            // view value set
            planDateTxtView.setText(planDate);
            planTimeTxtView.setText(planTime);
            planTitleEditTxt.setText(mPlanTitle);
            planBodyEditTxt.setText(mPlanBody);



        }




    }


    public void processIntent(Intent intent) {

        mPlanMode = intent.getStringExtra(BasicInfo.KEY_MODE);
        mTourId = intent.getStringExtra(BasicInfo.KEY_TOUR_ID);
        mPlanId = intent.getStringExtra(BasicInfo.KEY_PLAN_ID);
        mPlanDateTime = intent.getStringExtra(BasicInfo.KEY_PLAN_DATETIME);
        mPlanTitle = intent.getStringExtra(BasicInfo.KEY_PLAN_TITLE);
        mPlanBody = intent.getStringExtra(BasicInfo.KEY_PLAN_BODY);

        mTourFirstDate = intent.getStringExtra(BasicInfo.KEY_TOUR_FIRSTDATE);
        mTourLastDate = intent.getStringExtra(BasicInfo.KEY_TOUR_LASTDATE);


        setByMode(mPlanMode,mPlanDateTime);
    }



    /**
     *  set Bottom Buttons click listener
     */
    public void setBottomButtons() {

        // modify
        planModifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlanMode = BasicInfo.MODE_MODIFY;
                setByMode(mPlanMode,mPlanDateTime);

            }
        });

        // save
        planSaveBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                boolean isParsed = parseValues();   // 시간 / 날짜 입력값 확인

                if (isParsed) {
                    if(mPlanMode.equals(BasicInfo.MODE_INSERT)) {
                        saveInput();

                    } else if(mPlanMode.equals(BasicInfo.MODE_MODIFY)) {
                        modifyInput();
                    }
                }
            }
        });
        // close
        planCancelBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(mPlanMode.equals(BasicInfo.MODE_MODIFY)) {
                    mPlanMode = BasicInfo.MODE_VIEW;
                    setByMode(mPlanMode,mPlanDateTime);

                }else if (mPlanMode.equals(BasicInfo.MODE_VIEW)){
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });


        // delete
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletePlan();
            }
        });

    }

    /**
     *   saveInput -> DB PLAN 레코드 추가
     */
    private void saveInput() {
        Log.d(TAG, " * save plan * ");

        // insert PLAN table
        String SQL = null;
        SQL = "insert into " + PlanDatabase.TABLE_PLAN +
                "(TOUR_ID, PLAN_DATETIME, PLAN_TITLE, PLAN_BODY) values(" +
                "'"+ mTourId + "', " +                          // TOUR_ID
                "DATETIME('" + mPlanDateTime + "'), " +         // PLAN 날짜 + 시간  (format : "yyyy-MM-dd HH:mm:ss")
                "'"+ mPlanTitle + "', " +                       // PLAN SIMPLE
                "'"+ mPlanBody + "')";  		                // PLAN Body

        Log.d(TAG, "SQL : " + SQL);
        if (MainActivity.mDatabase != null) {
            MainActivity.mDatabase.execSQL(SQL);
        }


        // insert -> view change
        mPlanMode = BasicInfo.MODE_VIEW;
        setByMode(mPlanMode, mPlanDateTime);

    }

    /**
     * 데이터베이스 레코드 수정
     */
    private void modifyInput(){

        String SQL = null;
        // update memo info TABLE_PLAN : TOUR_ID, PLAN_DATETIME, PLAN_TITLE, PLAN_BODY
        SQL = "update " + PlanDatabase.TABLE_PLAN +
                " set " +
                " PLAN_DATETIME = DATETIME('" + mPlanDateTime + "'), " +
                " PLAN_TITLE = '" + mPlanTitle + "', " +
                " PLAN_BODY = '" + mPlanBody + "'" +
                " where _id = '" + mPlanId + "'";

        Log.d(TAG, "SQL : " + SQL);
        if (MainActivity.mDatabase != null) {
            MainActivity.mDatabase.execSQL(SQL);
        }

        // modify -> view change

        setByMode(BasicInfo.MODE_VIEW, mPlanDateTime);

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

        planDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mDateStr = planDateTxtView.getText().toString();          // 버튼값 mDateStr 에 넣기
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

        planTimeBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String mTimeStr = planTimeTxtView.getText().toString();
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

            planDateTxtView.setText(year + "-" + monthStr + "-" + dayStr);
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
            planTimeTxtView.setText(hourStr + ":" + minuteStr);

        }
    };


    /**
     *      Date/Time/ Plan 입력값 확인
     */
    private boolean parseValues() {

        String insertDateStr = planDateTxtView.getText().toString();     // insert_dateBtn 버튼 값 받아오기
        String insertTimeStr = planTimeTxtView.getText().toString();     // insert_timeBtn 버튼 값 받아오기
        // todo 시간이 입력되지 않으면 00:00 값으로 넘기기 (나중에 '공지'같은 기능으로 넣기)

        if (insertTimeStr.equals("")){
            insertTimeStr = "00:00";
        }


        String srcDateStr = insertDateStr + " " + insertTimeStr;                // srcDateStr = date + time
        Log.d(TAG, " ** source Date string : " + srcDateStr);

        try {
                Date insertDate = BasicInfo.dateTimeFormat.parse(srcDateStr);   // (String) srcDateStr  -> (Date) insertDate 변환
                mPlanDateTime = BasicInfo.dateTimeFormat.format(insertDate);    // (Date) insertDate    -> (String) "yyyy-MM-dd HH:mm" -> DB 저장용
        } catch(ParseException ex) {
            Log.e(TAG, "Exception in parsing date : " + insertDateStr);
        }

        mPlanTitle = planTitleEditTxt.getText().toString();
        // check text Plan
        if (mPlanTitle.trim().length() < 1) {
            String str = "";
            if(BasicInfo.language.equals("ko")){
                str = "계획을 작성해 주세요";
            }else if(BasicInfo.language.equals("ja")){
                str = "計画を作成してください";
            }else{
                str="Please make out a plan.";
            }

            Toast.makeText(getApplicationContext(),str,Toast.LENGTH_LONG).show();
            return false;
        }


        mPlanBody = planBodyEditTxt.getText().toString();
        if (mPlanBody.trim().length() < 1) {
            mPlanBody="";
        }

        return true;
    }
}
