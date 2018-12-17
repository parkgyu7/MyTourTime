package parkgyu7.mytourtime;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import parkgyu7.mytourtime.db.PlanDatabase;

public class TourDateInsertActivity extends AppCompatActivity {
    public static final String TAG = "** TourDateInsertAct";


    EditText tourTitle;
    Button firstDateBtn;
    Button lastDateBtn;

    Button nextBtn;

    String mTourId;
    String mTourTitle;
    String mTourFirstDate;
    String mTourLastDate;

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

        tourTitle = (EditText)findViewById(R.id.tourTitle);
        sampleDateSetting = (Button)findViewById(R.id.sampleSetting);
        firstDateBtn = (Button) findViewById(R.id.firstDateBtn);
        lastDateBtn= (Button) findViewById(R.id.lastDateBtn);
        nextBtn = (Button)findViewById(R.id.nextBtn);

        intent = getIntent();
        mTourTitle = intent.getStringExtra(BasicInfo.KEY_TOUR_TITLE);
        tourTitle.setText(mTourTitle);

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

                Calendar pickedDate = Calendar.getInstance();
                Calendar minDate = Calendar.getInstance();
                Calendar maxDate = Calendar.getInstance();

                pickedDate.set(2018,2-1,12);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        TourDateInsertActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            }
                        },
                        pickedDate.get(Calendar.YEAR),
                        pickedDate.get(Calendar.MONTH),
                        pickedDate.get(Calendar.DATE)
                );


                minDate.set(2018,2-1,10);
                datePickerDialog.getDatePicker().setMinDate(minDate.getTime().getTime());

                maxDate.set(2018,2-1,17);
                datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());


                datePickerDialog.show();
                Toast.makeText(getApplicationContext(),"calendar date : "+ pickedDate.get(Calendar.YEAR)+pickedDate.get(Calendar.MONTH)+pickedDate.get(Calendar.DATE),Toast.LENGTH_SHORT).show();
                firstDateBtn.setText(mTourFirstDate);
            }
        });

        lastDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mDateStr = "2018-02-12";
                Calendar calendar = Calendar.getInstance();
                Calendar minDate = Calendar.getInstance();
                Calendar maxDate = Calendar.getInstance();

                Date date = new Date();

                try {
                    date = BasicInfo.datetimeFormat_Date.parse(mDateStr);

                } catch(Exception ex) {
                    Log.d(TAG, "Exception in parsing date : " + date);
                }
                calendar.setTime(date);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        TourDateInsertActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                            }
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                );

                minDate.set(2018,2-1,10);
                datePickerDialog.getDatePicker().setMinDate(minDate.getTime().getTime());

                maxDate.set(2018,2-1,17);
                datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());


                datePickerDialog.show();
                Toast.makeText(getApplicationContext(),"calendar date : "+ calendar.get(Calendar.YEAR)+calendar.get(Calendar.MONTH)+calendar.get(Calendar.DATE),Toast.LENGTH_SHORT).show();
                lastDateBtn.setText(mTourLastDate);

            }
        });


        /**
         *  next to Btn 리스너
         *  DB TOUR table insert
         */
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTour();
            }
        });
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
    }


    private void saveTour(){

        mTourTitle = tourTitle.getText().toString().trim();

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

}

