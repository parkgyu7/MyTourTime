package parkgyu7.mytourtime;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.Locale;

import parkgyu7.mytourtime.db.PlanDatabase;

public class MainActivity extends AppCompatActivity {

    // todo main(TOUR LIST) - add listview- / complite / list popup menu / language

    public static final String TAG = "** MainActivity";

    ListView mTourListView;
    ListView mFavoriteTourListView;
    ListView mDoneTourListView;

    TourListAdapter mTourListAdapter;
    TourListAdapter mFavoriteTourListAdapter;
    TourListAdapter mDoneTourListAdapter;

    TextView itemCount;
    TextView favoriteItemCount;
    TextView doneItemCount;


    Button selecModeBtn;



    String mTourId;
    String mTourTitle;
    String mTourFirstDate;
    String mTourLastDate;

    String mfavoriteChecker;
    String mDoneChecker;

    String SQL;

    FloatingActionButton newTourfab;

    /**
     * DB instance
     */
    public static PlanDatabase mDatabase = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //---------------------------------------------------------------//

        // Set current locale
        Locale curLocale = getResources().getConfiguration().locale;
        BasicInfo.language = curLocale.getLanguage();
        Log.d(TAG, "current language : " + BasicInfo.language);



        TabHost tabHost1 = (TabHost) findViewById(R.id.tabHost1);
        tabHost1.setup();

        // 첫 번째 Tab. (탭 표시 텍스트:"TAB 1"), (페이지 뷰:"content1")
        TabHost.TabSpec ts1 = tabHost1.newTabSpec("Tab Spec 1");
        ts1.setContent(R.id.content1);
        ts1.setIndicator("TOUR ALL");
        tabHost1.addTab(ts1);


        // 두 번째 Tab. (탭 표시 텍스트:"TAB 2"), (페이지 뷰:"content2")
        TabHost.TabSpec ts2 = tabHost1.newTabSpec("Tab Spec 2");
        ts2.setContent(R.id.content2);
        Resources res = getResources();
        Drawable drawable = res.getDrawable(R.drawable.fav_check);
        ts2.setIndicator("FAVORITE", drawable);


        tabHost1.addTab(ts2);
        tabHost1.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if (tabId.equals("Tab Spec 1")) {
                    Toast.makeText(getApplicationContext(), "tab1 boot", Toast.LENGTH_SHORT).show();
                    loadTourListData();
                } else if (tabId.equals("Tab Spec 2")) {
                    Toast.makeText(getApplicationContext(), "tab2 boot", Toast.LENGTH_SHORT).show();
                    loadFavoriteTourListData();

                } else if(tabId.equals("Tab Spec 3")){
                    Toast.makeText(getApplicationContext(), "tab2 boot", Toast.LENGTH_SHORT).show();
                    loadDoneTourListData();
                }

            }
        });

        // 세 번째 Tab. (탭 표시 텍스트:"TAB 3"), (페이지 뷰:"content3")
        TabHost.TabSpec ts3 = tabHost1.newTabSpec("Tab Spec 3");
        ts3.setContent(R.id.content3);
        ts3.setIndicator("DONE");
        tabHost1.addTab(ts3);


        //---------------------------------------------------------------//
        /**
         *  Tour List
         */
        mTourListView = (ListView) findViewById(R.id.tourList);
        mTourListAdapter = new TourListAdapter(this);
        mTourListView.setAdapter(mTourListAdapter);

        mFavoriteTourListView = (ListView) findViewById(R.id.favoritetourlist);
        mFavoriteTourListAdapter = new TourListAdapter(this);
        mFavoriteTourListView.setAdapter(mFavoriteTourListAdapter);

        mDoneTourListView = (ListView) findViewById(R.id.donetourlist);
        mDoneTourListAdapter = new TourListAdapter(this);
        mDoneTourListView.setAdapter(mDoneTourListAdapter);

        itemCount = (TextView) findViewById(R.id.tourCount);
        favoriteItemCount = (TextView) findViewById(R.id.favoriteTourCount);
        doneItemCount = (TextView) findViewById(R.id.doneTourCount);


        // Floating Action Button
        newTourfab = (FloatingActionButton) findViewById(R.id.newTourfab);
        newTourfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, " ** newTourfab clicked.");
                Intent intent = new Intent(getApplicationContext(), TourTitleInsertActivity.class);
                intent.putExtra(BasicInfo.KEY_MODE, BasicInfo.MODE_INSERT);
                startActivity(intent);
            }
        });

        selecModeBtn = (Button) findViewById(R.id.selectModeBtn);


        selecModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),TourListSelectActivity.class);
                startActivity(intent);
            }
        });


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
        openDatabase();         // open DB
        loadTourListData();     // load Tour List
        loadFavoriteTourListData();
        loadDoneTourListData();
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

    /**
     * TOUR 리스트 데이터 로딩
     */
    public void loadTourListData() {

        String SQL = "select _id, TOUR_TITLE, TOUR_firstDATE, TOUR_lastDATE, TOUR_FAVORITE, TOUR_DONE from TOUR order by CREATE_DATE desc";

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

                mTourListAdapter.addItem(new TourListItem(tourId, tourTitle, tourFirstDateStr, tourLastDateStr, tourFavorite, tourDone));
            }

            outCursor.close();

            mTourListAdapter.notifyDataSetChanged();

            itemCount.setText(recordCount + " Tours");
            itemCount.invalidate();
            if(recordCount>0) selecModeBtn.setEnabled(true);
            else selecModeBtn.setEnabled(false);


        }
    }

    /**
     * TOUR 리스트 데이터 로딩
     */
    public void loadFavoriteTourListData() {
        String SQL = "select _id, TOUR_TITLE, TOUR_firstDATE, TOUR_lastDATE, TOUR_FAVORITE, TOUR_DONE from TOUR where TOUR_FAVORITE = 'on' order by CREATE_DATE desc";

        int recordCount = -1;
        if (MainActivity.mDatabase != null) {
            Cursor outCursor = MainActivity.mDatabase.rawQuery(SQL);

            recordCount = outCursor.getCount();
            Log.d(TAG, "where TOUR_FAVORITE 'on' cursor count : " + recordCount + "\n");

            mFavoriteTourListAdapter.clear();

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

                mFavoriteTourListAdapter.addItem(new TourListItem(tourId, tourTitle, tourFirstDateStr, tourLastDateStr, tourFavorite, tourDone));
            }

            outCursor.close();

            mFavoriteTourListAdapter.notifyDataSetChanged();


            favoriteItemCount.setText(recordCount + " Tours");
            favoriteItemCount.invalidate();
        }
    }
    /**
     * TOUR 리스트 데이터 로딩
     */
    public void loadDoneTourListData() {
        String SQL = "select _id, TOUR_TITLE, TOUR_firstDATE, TOUR_lastDATE, TOUR_FAVORITE, TOUR_DONE from TOUR where TOUR_DONE = 'on' order by CREATE_DATE desc";

        int recordCount = -1;
        if (MainActivity.mDatabase != null) {
            Cursor outCursor = MainActivity.mDatabase.rawQuery(SQL);

            recordCount = outCursor.getCount();
            Log.d(TAG, "where TOUR_DONE 'on' cursor count : " + recordCount + "\n");

            mDoneTourListAdapter.clear();

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

                mDoneTourListAdapter.addItem(new TourListItem(tourId, tourTitle, tourFirstDateStr, tourLastDateStr, tourFavorite, tourDone));
            }

            outCursor.close();

            mDoneTourListAdapter.notifyDataSetChanged();


            doneItemCount.setText(recordCount + " Tours");
            doneItemCount.invalidate();
        }
    }

    public void viewTourPlanList(String tourId, String tourTitle, String tourFirstDay, String tourLastDay, String favoriteChecker) {

        Log.d(TAG, " **** viewTour  clicked");
        Intent intent = new Intent(getApplicationContext(), PlanListActivity.class);
        intent.putExtra(BasicInfo.KEY_MODE, BasicInfo.MODE_VIEW);
        intent.putExtra(BasicInfo.KEY_TOUR_ID, tourId);
        intent.putExtra(BasicInfo.KEY_TOUR_TITLE, tourTitle);
        intent.putExtra(BasicInfo.KEY_TOUR_FIRSTDATE, tourFirstDay);
        intent.putExtra(BasicInfo.KEY_TOUR_LASTDATE, tourLastDay);

        startActivityForResult(intent, BasicInfo.REQ_PLAN_LIST_ACTIVITY);

    }


    public void tourItemOption(String tourId, String tourTitle, String tourFirstDay, String tourLastDay, String favoriteChecker, String doneChecker) {

        mTourId = tourId;
        mTourTitle = tourTitle;
        mTourFirstDate = tourFirstDay;
        mTourLastDate = tourLastDay;
        mfavoriteChecker = favoriteChecker;
        mDoneChecker = doneChecker;
        Toast.makeText(getApplicationContext(), "id : " + mTourId, Toast.LENGTH_LONG).show();
        createDialog(BasicInfo.TOUR_ITEM_OPT).show();

    }

    /**
     * 즐겨찾기 설정
     */
    public void changeFavorite(String tourID, String tourFavorite) {

        String mTourId = tourID;
        String mfavoriteChecker = tourFavorite;

        if (mfavoriteChecker.equals("off")) {
            mfavoriteChecker = "on";
        } else {
            mfavoriteChecker = "off";
        }

        Toast.makeText(getApplicationContext(), "tourID :" + tourID + " / a : " + mfavoriteChecker, Toast.LENGTH_LONG).show();


        Log.d(TAG, "update TOUR record : " + mTourId);
        SQL = "update " + PlanDatabase.TABLE_TOUR +
                " set " +
                "TOUR_FAVORITE = '" + mfavoriteChecker + "' " +
                "where _id = '" + mTourId + "'";

        Log.d(TAG, "SQL : " + SQL);

        if (MainActivity.mDatabase != null) {
            MainActivity.mDatabase.execSQL(SQL);
        }

        loadTourListData();
        loadFavoriteTourListData();
        loadDoneTourListData();
    }
    /**
     * 완료 설정
     */
    public void changeDone(String tourID, String tourDone){
        String mTourId = tourID;
        String mDoneChecker = tourDone;

        if (mDoneChecker.equals("off")) {
            mDoneChecker = "on";
        } else {
            mDoneChecker = "off";
        }

        Toast.makeText(getApplicationContext(), "tourID :" + tourID + " / Done Chk : " + mDoneChecker, Toast.LENGTH_LONG).show();


        Log.d(TAG, "update TOUR record : " + mTourId);
        SQL = "update " + PlanDatabase.TABLE_TOUR +
                " set " +
                "TOUR_DONE = '" + mDoneChecker + "' " +
                "where _id = '" + mTourId + "'";

        Log.d(TAG, "SQL : " + SQL);

        if (MainActivity.mDatabase != null) {
            MainActivity.mDatabase.execSQL(SQL);
        }

        loadTourListData();
        loadFavoriteTourListData();
        loadDoneTourListData();
    }


    /**
     * 메모 삭제
     */
    public void deleteTour(String mTourId) {


        // delete PLAN record
        Log.d(TAG, "deleting previous PLAN ");

        String SQL = "delete from " + PlanDatabase.TABLE_PLAN +
                " where TOUR_ID = '" + mTourId + "'";
        Log.d(TAG, "SQL : " + SQL);
        if (MainActivity.mDatabase != null) {
            MainActivity.mDatabase.execSQL(SQL);
        }

        // delete TOUR record
        Log.d(TAG, "deleting previous TOUR record : " + mTourId);
        SQL = "delete from " + PlanDatabase.TABLE_TOUR +
                " where _id = '" + mTourId + "'";
        Log.d(TAG, "SQL : " + SQL);
        if (MainActivity.mDatabase != null) {
            MainActivity.mDatabase.execSQL(SQL);
        }

        loadTourListData();
    }


    /**
     * process Activity Result
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case BasicInfo.REQ_TOUR_DATE_INSERT_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    loadTourListData();
                }

                break;

        }
    }

    protected Dialog createDialog(int code) {

        AlertDialog.Builder builder = null;
        switch (code) {

            case BasicInfo.TOUR_ITEM_OPT:

                String favoriteCheck = "즐겨찾기";
                if (mfavoriteChecker.equals("on")) {
                    favoriteCheck = "즐겨찾기 해제하기";
                } else {
                    favoriteCheck = "즐겨찾기 설정하기";
                }

                String doneCheck = "완료";
                if (mDoneChecker.equals("on")) {
                    doneCheck = "완료 해제하기";
                } else {
                    doneCheck = "완료 설정하기";
                }

                final CharSequence[] items = {favoriteCheck, "TITLE 수정", "DATE 수정", "삭제", doneCheck};

                builder = new AlertDialog.Builder(this);

                // item infomation
                builder.setTitle("Tour_ID : " + mTourId + " TITLE : " + mTourTitle);

                Log.d(TAG, "alert Tour_ID : " + mTourId + " TITLE : " + mTourTitle);

                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: // 즐겨찾기
                                Toast.makeText(getApplicationContext(), items[which], Toast.LENGTH_SHORT).show();
                                changeFavorite(mTourId, mfavoriteChecker);

                                break;
                            case 1: // TITLE 수정
                                Toast.makeText(getApplicationContext(), items[which], Toast.LENGTH_SHORT).show();
                                createDialog(BasicInfo.CONFIRM_TOUR_TITLE_MODIFY).show();
                                break;

                            case 2: // DATE 수정
                                Intent intent = new Intent(getApplicationContext(), TourDateInsertActivity.class);
                                intent.putExtra(BasicInfo.KEY_MODE, BasicInfo.MODE_MODIFY);
                                intent.putExtra(BasicInfo.KEY_TOUR_ID, mTourId);
                                intent.putExtra(BasicInfo.KEY_TOUR_TITLE, mTourTitle);
                                intent.putExtra(BasicInfo.KEY_TOUR_FIRSTDATE, mTourFirstDate);
                                intent.putExtra(BasicInfo.KEY_TOUR_LASTDATE, mTourLastDate);
                                startActivityForResult(intent, BasicInfo.REQ_TOUR_DATE_INSERT_ACTIVITY);

                            case 3: // 삭제
                                Toast.makeText(getApplicationContext(), items[which], Toast.LENGTH_SHORT).show();

                                Log.d(TAG, "alert TOUR delete Clicked  tour id : " + mTourId);
                                createDialog(BasicInfo.CONFIRM_TOUR_DELETE).show();
                                break;

                            case 4:
                                Toast.makeText(getApplicationContext(), items[which], Toast.LENGTH_SHORT).show();
                                changeDone(mTourId,mDoneChecker);
                                break;
                            default:
                                break;
                        }
                    }

                });
                break;
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
                        mTourTitle = editTitle.getText().toString().trim();
                        Log.d(TAG, "update TOUR record : " + mTourId);
                        SQL = "update " + PlanDatabase.TABLE_TOUR +
                                " set " +
                                "TOUR_TITLE = '" + mTourTitle + "' " +
                                "where _id = '" + mTourId + "'";

                        Log.d(TAG, "SQL : " + SQL);
                        if (MainActivity.mDatabase != null) {
                            MainActivity.mDatabase.execSQL(SQL);
                        }
                        loadTourListData();
                        loadFavoriteTourListData();
                        loadDoneTourListData();

                    }
                });
                builder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                break;
            // TOUR ITEM 삭제하기.
            case BasicInfo.CONFIRM_TOUR_DELETE:

                Log.d(TAG, "alert TOUR_DELETE Clicked tour_id : " + mTourId + " / tour_title : " + mTourTitle);

                builder = new AlertDialog.Builder(this);

                builder.setTitle(mTourTitle + "\n이 TOUR를 삭제하시겠습니까 ?");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteTour(mTourId);
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


    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }
}




