package parkgyu7.mytourtime;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class TourTitleInsertActivity extends AppCompatActivity {
    public static final String TAG = "** TourTitleInsert ACT";
    String mTourTitle;
    EditText tourTitle;
    Button nextBtn;
    Intent intent;


    TextView step1;



    int REQ_EXIT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_title_insert);
        Log.d(TAG, "onCreate");

        tourTitle = (EditText)findViewById(R.id.tourTitle);
        nextBtn = (Button) findViewById(R.id.nextBtn);
        step1 = (TextView) findViewById(R.id.step1);

        if(BasicInfo.language.equals("ko")){
            step1.setText("step 1 : 제목을 입력해 주세요.");
            tourTitle.setHint("제목을 입력해 주세요.");
        }else if(BasicInfo.language.equals("ja")){
            step1.setText("step 1 : タイトルを入力してください。");
            tourTitle.setHint("タイトルを入力してください。");
        }else{
            step1.setText("step 1 : Please enter a title.");
            tourTitle.setHint("Please enter a title.");
        }


        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTourTitle = tourTitle.getText().toString().trim();

                if(mTourTitle.length()<1){
                    mTourTitle = "Untitle";
                }

                intent = new Intent(getApplicationContext(),TourDateInsertActivity.class);
                intent.putExtra(BasicInfo.KEY_MODE, BasicInfo.MODE_INSERT);
                intent.putExtra(BasicInfo.KEY_TOUR_TITLE, mTourTitle);

                startActivityForResult(intent,REQ_EXIT);
            }
        });

    }


    @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_EXIT){
            if(resultCode == RESULT_OK){
                this.finish();
            }
        }


    }

}
