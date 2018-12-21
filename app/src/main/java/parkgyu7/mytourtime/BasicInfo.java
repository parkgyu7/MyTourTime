package parkgyu7.mytourtime;

import java.text.SimpleDateFormat;

/**
 * Created by Administrator on 2018-02-19.
 */


public class BasicInfo {

    public static String language = "";


    /**
     * 외장 메모리 패스
     */
    public static String ExternalPath = "/mnt/sdcard/";
    /**
     * 외장 메모리 패스 체크 여부
     */
    public static boolean ExternalChecked = false;

    /**
     * 데이터베이스 이름
     */
    public static String DATABASE_NAME = "plan.db";

    //========== 인텐트 부가정보 전달을 위한 키값 ==========//
    public static final String KEY_MODE = "MODE";

    public static final String KEY_TOUR_ID = "TOUR_ID";
    public static final String KEY_TOUR_TITLE = "TOUR_TITLE";
    public static final String KEY_TOUR_FIRSTDATE = "TOUR_FIRSTDATE";
    public static final String KEY_TOUR_LASTDATE = "TOUR_LASTDATE";

    public static final String KEY_PLAN_DATETIME = "PLAN_DATETIME";
    public static final String KEY_PLAN_ID = "PLAN_ID";
    public static final String KEY_PLAN_TITLE = "PLAN_TITLE";
    public static final String KEY_PLAN_BODY = "PLAN_BODY";


    //========== 액티비티 요청 코드  ==========//
    public static final int REQ_TOUR_DATE_INSERT_ACTIVITY = 1001;

    public static final int REQ_PLAN_LIST_ACTIVITY = 2001;

    public static final int REQ_PLAN_INSERT_VIEW_ACTIVITY = 3001;
    public static final int REQ_PLAN_INSERT_ACTIVITY = 3002;

    //========== 재확인 요청 코드  ==========//
    public static final int CONFIRM_DELETE = 5001;
    public static final int CONFIRM_TEXT_INPUT = 5002;

    //========== 모드 상수 ==========//
    public static final String MODE_INSERT = "MODE_INSERT";
    public static final String MODE_MODIFY = "MODE_MODIFY";
    public static final String MODE_VIEW = "MODE_VIEW";


    public static final String LIST_NORMAL = "LIST_NORMAL";
    public static final String LIST_SELECTABLE = "LIST_SELECTABLE";

    //========== Dialog 코드 =========//

    public static final int TOUR_LIST_OPT = 21;
    public static final int TOUR_ITEM_OPT = 31;
    public static final int CONFIRM_TOUR_DELETE = 32;
    public static final int PLAN_LIST_OPT = 41;
    public static final int CONFIRM_TOUR_TITLE_MODIFY = 42;



    //========== 날짜 포맷  ==========//
    public static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public static SimpleDateFormat datetimeFormat_Date = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat datetimeFormat_Time = new SimpleDateFormat("HH:mm");

}


