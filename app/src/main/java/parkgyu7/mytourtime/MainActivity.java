package parkgyu7.mytourtime;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import parkgyu7.mytourtime.db.PlanDatabase;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "** MainActivity";
    ListView mTourListView;
    TourListAdapter mTourListAdapter;
    TextView itemCount;

    /**
     *  DB instance
     */
     public static PlanDatabase mDatabase = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         *  Tour List
         */
        mTourListView = (ListView)findViewById(R.id.tourList);
        mTourListAdapter = new TourListAdapter(this);
        mTourListView.setAdapter(mTourListAdapter);


        Button newTourBtn = (Button)findViewById(R.id.newTourBtn);
        newTourBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, " ** newTourBtn clicked.");
                Intent intent = new Intent(getApplicationContext(),TourTitleInsertActivity.class);
                intent.putExtra(BasicInfo.KEY_TOUR_MODE,BasicInfo.MODE_INSERT);
                startActivity(intent);
            }
        });

        itemCount = (TextView) findViewById(R.id.tourCount);
        checkDangerousPermissions();



    }


//----------------------------------------------------------------------------------------------//

    private void checkDangerousPermissions() {
        String[] permissions = {
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.RECORD_AUDIO
        };

        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (int i = 0; i < permissions.length; i++) {
            permissionCheck = ContextCompat.checkSelfPermission(this, permissions[i]);
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                break;
            }
        }

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "권한 있음", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "권한 없음", Toast.LENGTH_LONG).show();

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                Toast.makeText(this, "권한 설명 필요함.", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this, permissions, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, permissions[i] + " 권한이 승인됨.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, permissions[i] + " 권한이 승인되지 않음.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    protected void onStart() {
        openDatabase();         // 데이터베이스 열기
        loadTourListData();     // 메모 데이터 로딩
        super.onStart();
    }



    /**
     *  데이터베이스 열기 ( if null -> create)
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


    /**
     *   TOUR 리스트 데이터 로딩
     */
    public void loadTourListData()
    {
        String SQL = "select _id, TOUR_TITLE, TOUR_firstDATE, TOUR_lastDATE from TOUR order by CREATE_DATE desc";

        int recordCount = -1;
        if (MainActivity.mDatabase != null) {
            Cursor outCursor = MainActivity.mDatabase.rawQuery(SQL);

            recordCount = outCursor.getCount();
            Log.d(TAG, "cursor count : " + recordCount + "\n");

            mTourListAdapter.clear();

            for (int i = 0; i < recordCount; i++) {
                outCursor.moveToNext();

                String tourId = outCursor.getString(0);
                String tourTitle = outCursor.getString(1);

                String tourFirstDateStr = outCursor.getString(2);
                if (tourFirstDateStr != null) {
                    //dateStr = dateStr.substring(0, 10);
                    try {
                        Date inDate = BasicInfo.dateTimeFormat.parse(tourFirstDateStr);
                        tourFirstDateStr = BasicInfo.datetimeFormat_Date.format(inDate);
                    } catch(Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    tourFirstDateStr = "";
                }

                String tourLastDateStr = outCursor.getString(3);
                if (tourFirstDateStr != null) {
                    //dateStr = dateStr.substring(0, 10);
                    try {
                        Date inDate = BasicInfo.dateTimeFormat.parse(tourLastDateStr);
                        tourLastDateStr = BasicInfo.datetimeFormat_Date.format(inDate);
                    } catch(Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    tourLastDateStr = "";
                }

                 mTourListAdapter.addItem(new TourListItem(tourId, tourTitle, tourFirstDateStr, tourLastDateStr));
            }

            outCursor.close();
            mTourListAdapter.notifyDataSetChanged();
            itemCount.setText(recordCount + " Tours");
            itemCount.invalidate();
        }
    }


    /**
     * Tour plan보기
     * @param position
     */


    public void viewTourPlanList(int position){

        Log.d(TAG, " **** viewTour  clicked");

        TourListItem item = (TourListItem) mTourListAdapter.getItem(position);

        Intent intent = new Intent(getApplicationContext(), PlanListActivity.class);

        intent.putExtra(BasicInfo.KEY_TOUR_MODE, BasicInfo.MODE_VIEW);

        intent.putExtra(BasicInfo.KEY_TOUR_ID, item.getId());
        intent.putExtra(BasicInfo.KEY_TOUR_TITLE, item.getData(0));
        intent.putExtra(BasicInfo.KEY_TOUR_FIRSTDATE, item.getData(1));
        intent.putExtra(BasicInfo.KEY_TOUR_LASTDATE, item.getData(2));

        startActivityForResult(intent, BasicInfo.REQ_TOUR_VIEW_ACTIVITY);

    }



    /**
     * 다른 액티비티의 응답 처리
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case BasicInfo.REQ_TOUR_VIEW_ACTIVITY:
                if(resultCode == RESULT_OK) {
                    loadTourListData();
                }

                break;

            case BasicInfo.REQ_TOUR_INSERT_ACTIVITY:
                loadTourListData();

                break;

        }
    }



}
