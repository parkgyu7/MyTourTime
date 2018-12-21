package parkgyu7.mytourtime;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import parkgyu7.mytourtime.db.PlanDatabase;

public class TourDateInsertActivity extends AppCompatActivity {
    public static final String TAG = "** TourDateInsertAct";


    TextView tourTitle;
    Button tourTitleModifyBtn;

    Button firstDateBtn;
    Button lastDateBtn;

    Button nextBtn;

    String mMode;
    String mTourId;
    String mTourTitle;
    String mTourFirstDate;
    String mTourLastDate;

    Calendar firstDate = Calendar.getInstance();
    Calendar lastDate = Calendar.getInstance();
    String dateStr;

    TextView totalView;
    TextView step2;


    // sample
    Button sampleDateSetting;
    Calendar ex_FirstDate = Calendar.getInstance();
    Calendar ex_LastDate = Calendar.getInstance();

    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_date_insert);
        Log.d(TAG, "onCreate");

        //////////// view matching ///////////////////////
        tourTitle = (TextView)findViewById(R.id.tourTitle);
        tourTitleModifyBtn = (Button)findViewById(R.id.tourTitleModifyBtn);
        firstDateBtn = (Button) findViewById(R.id.firstDateBtn);
        lastDateBtn= (Button) findViewById(R.id.lastDateBtn);
        nextBtn = (Button)findViewById(R.id.nextBtn);
        totalView = (TextView) findViewById(R.id.totalView);

        sampleDateSetting = (Button)findViewById(R.id.sampleSetting);

        step2 = (TextView) findViewById(R.id.step2);

        if(BasicInfo.language.equals("ko")){
            step2.setText("step 2 : 날짜를 정해 주세요.");
        }else if(BasicInfo.language.equals("ja")){
            step2.setText("step 2 : 日付を決めてください。");
        }else{
            step2.setText("step 2 : Please fix the date.");
        }

        //////////////////////////////////////////////////

        intent = getIntent();
        processIntent(intent);

        tourTitleModifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog(BasicInfo.CONFIRM_TOUR_TITLE_MODIFY).show();
            }
        });



        sampleDateSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ex_setDateTitle();
                firstDateBtn.setText(mTourFirstDate);
                lastDateBtn.setText(mTourLastDate);

            }
        });



        firstDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mDateStr = firstDateBtn.getText().toString();
                if (mDateStr.length()>=0){
                    Calendar calendar = Calendar.getInstance();
                    Date date = new Date();
                    try {
                        date = BasicInfo.datetimeFormat_Date.parse(mDateStr);
                    } catch(Exception ex) {
                        Log.d(TAG, "Exception in parsing date : " + date);
                    }
                    calendar.setTime(date);
                    new DatePickerDialog(
                            TourDateInsertActivity.this,
                            dateSetListener,
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                    ).show();
                }
            }
        });

        lastDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mDateStr = firstDateBtn.getText().toString();
                if (mDateStr.length()>1){
                    Calendar minDate = Calendar.getInstance();
                    Date date = new Date();
                    try {
                        date = BasicInfo.datetimeFormat_Date.parse(mDateStr);
                    } catch(Exception ex) {
                        Log.d(TAG, "Exception in parsing date : " + date);
                    }
                    minDate.setTime(date);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(
                            TourDateInsertActivity.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                                    lastDate.set(year, month, dayOfMonth);
                                    String monthStr = String.valueOf(month+1);
                                    if (month < 9) {
                                        monthStr = "0" + monthStr;
                                    }
                                    String dayStr = String.valueOf(dayOfMonth);
                                    if (dayOfMonth < 10) {
                                        dayStr = "0" + dayStr;
                                    }
                                    dateStr = year + "-" + monthStr + "-" + dayStr;
                                    lastDateBtn.setText(dateStr);
                                }

                            },
                            minDate.get(Calendar.YEAR),
                            minDate.get(Calendar.MONTH),
                            minDate.get(Calendar.DAY_OF_MONTH)
                    );
                    datePickerDialog.getDatePicker().setMinDate(minDate.getTime().getTime());
                    datePickerDialog.show();
                    datePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            setTotal();
                        }
                    });

                    lastDateBtn.setText(mTourLastDate);
                    nextBtn.setEnabled(true);

                }else {
                    Toast.makeText(getApplicationContext(),"Please fix a First Day.",Toast.LENGTH_LONG).show();
                }
            }
        });




        /**
         *  next to Btn 리스너
         *  DB TOUR table insert
         */
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMode.equals(BasicInfo.MODE_MODIFY)) {
                    modifyDate();
                }else {
                    saveTour();
                }
            }
        });


    }

    public void processIntent(Intent intent){
        mMode = intent.getStringExtra(BasicInfo.KEY_MODE);
        mTourId = intent.getStringExtra(BasicInfo.KEY_TOUR_ID);
        mTourTitle = intent.getStringExtra(BasicInfo.KEY_TOUR_TITLE);
        mTourFirstDate = intent.getStringExtra(BasicInfo.KEY_TOUR_FIRSTDATE);
        mTourLastDate = intent.getStringExtra(BasicInfo.KEY_TOUR_LASTDATE);

        tourTitle.setText(mTourTitle);
        firstDateBtn.setText(mTourFirstDate);
        lastDateBtn.setText(mTourLastDate);
        setMode(mMode);

    }

    public void setTotal(){
        String tourTitlestr = tourTitle.getText().toString().trim();
        String firstDatestr = firstDateBtn.getText().toString();
        String lastDatestr = lastDateBtn.getText().toString();
        Date firstDate = new Date();
        Date lastDate = new Date();
        try {
            firstDate = BasicInfo.datetimeFormat_Date.parse(firstDatestr);
        } catch (Exception ex) {
            Log.d(TAG, "Exception in parsing date : " + firstDatestr);
        }
        try {
            lastDate = BasicInfo.datetimeFormat_Date.parse(lastDatestr);
        } catch (Exception ex) {
            Log.d(TAG, "Exception in parsing date : " + lastDatestr);
        }

        int tourPeriod = (int)(lastDate.getTime()-firstDate.getTime())/(24 * 60 * 60 * 1000) + 1;

        String total;

        if(BasicInfo.language.equals("ko")){
            total = firstDatestr + " 부터 "+ lastDatestr + " 까지 "+ (tourPeriod-1)+"박"+tourPeriod+"일간의\n" +tourTitlestr+"여행이 만들어집니다." ;
            totalView.setText(total);
        }else if(BasicInfo.language.equals("ja")){
            total = firstDatestr + " から "+ lastDatestr + " まで "+ (tourPeriod-1)+"泊"+tourPeriod+"日の\n" +tourTitlestr+"旅行が作られます。" ;
            totalView.setText(total);
        }else{
            total = " from the "+ firstDatestr + " to the "+ lastDatestr + (tourPeriod-1)+"night"+tourPeriod+"days\n" +tourTitlestr+"A trip is made." ;
            totalView.setText(total);
        }



    }

    public void setMode(String mode){
        Log.d(TAG, "  * mMode : ** " + mMode);
        if (mMode.equals(BasicInfo.MODE_MODIFY)) {
            nextBtn.setText("modify");
        }

    }



    /**
     *  샘플 DATE setting
     *  2월1일-3일 2박 3일
     */
    public void ex_setDateTitle(){
        ex_FirstDate.set(2018,2-1,28);
        ex_LastDate.set(2018,3-1,3);
        mTourFirstDate = BasicInfo.datetimeFormat_Date.format(ex_FirstDate.getTime());
        mTourLastDate = BasicInfo.datetimeFormat_Date.format(ex_LastDate.getTime());
        nextBtn.setEnabled(true);
    }

    /**
     * 날짜 설정 리스너
     */
    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            firstDate.set(year, monthOfYear, dayOfMonth);
            String monthStr = String.valueOf(monthOfYear+1);
            if (monthOfYear < 9) {
                monthStr = "0" + monthStr;
            }
            String dayStr = String.valueOf(dayOfMonth);
            if (dayOfMonth < 10) {
                dayStr = "0" + dayStr;
            }
            dateStr = year + "-" + monthStr + "-" + dayStr;
            firstDateBtn.setText(dateStr);

        }
    };




    private void saveTour(){

        mTourTitle = tourTitle.getText().toString().trim();
        mTourFirstDate = firstDateBtn.getText().toString();
        mTourLastDate = lastDateBtn.getText().toString();
        //  INSERT - TABLE_TOUR (title / firstdate / lastdate)
        String SQL = null;
        SQL = "insert into " + PlanDatabase.TABLE_TOUR +
                "(TOUR_TITLE, TOUR_firstDATE, TOUR_lastDATE) values(" +
                "'"+ mTourTitle + "', " +
                "DATE('" + mTourFirstDate + "'), " +
                "DATE('" + mTourLastDate + "'))";

        Log.d(TAG, "SQL : " + SQL);
        if (MainActivity.mDatabase != null) {
            MainActivity.mDatabase.execSQL(SQL);
        }
        // 위에 추가한 TOUR 로우의 id 얻어오기.
        String getTourID_SQL = null;
        getTourID_SQL = "select _id from TOUR order by CREATE_DATE desc limit 1 ";      // SELECT - TOUR TABLE (마지막 로우 ID)
        Cursor getIdCursor = MainActivity.mDatabase.rawQuery(getTourID_SQL);
        getIdCursor.moveToNext();
        mTourId = getIdCursor.getString(0);
        getIdCursor.close();

        intent = new Intent(getApplicationContext(),PlanListActivity.class);
        intent.putExtra(BasicInfo.KEY_TOUR_ID, mTourId);
        intent.putExtra(BasicInfo.KEY_TOUR_TITLE, mTourTitle);
        intent.putExtra(BasicInfo.KEY_TOUR_FIRSTDATE,mTourFirstDate);
        intent.putExtra(BasicInfo.KEY_TOUR_LASTDATE,mTourLastDate);

        startActivity(intent);
        setResult(RESULT_OK);
        finish();

    }

    private void modifyDate(){

        mTourTitle = tourTitle.getText().toString().trim();
        mTourFirstDate = firstDateBtn.getText().toString();
        mTourLastDate = lastDateBtn.getText().toString();
        String SQL = "update " + PlanDatabase.TABLE_TOUR +
                " set " +
                " TOUR_TITLE = '" + mTourTitle + "', " +
                " TOUR_firstDATE = DATE('" + mTourFirstDate + "'), " +
                " TOUR_lastDATE = DATE('" + mTourLastDate + "') " +
                " where _id = '" + mTourId + "'";

        Log.d(TAG, "SQL : " + SQL);
        if (MainActivity.mDatabase != null) {
            MainActivity.mDatabase.execSQL(SQL);
        }
        intent.putExtra(BasicInfo.KEY_TOUR_ID, mTourId);
        intent.putExtra(BasicInfo.KEY_TOUR_TITLE, mTourTitle);
        intent.putExtra(BasicInfo.KEY_TOUR_FIRSTDATE,mTourFirstDate);
        intent.putExtra(BasicInfo.KEY_TOUR_LASTDATE,mTourLastDate);
        setResult(RESULT_OK, intent);
        finish();

    }


    protected Dialog createDialog(int code) {

        AlertDialog.Builder builder = null;

        switch(code) {

            // TOUR TITLE 수정
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
                        tourTitle.setText(editTitle.getText().toString().trim());
                        setTotal();
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

