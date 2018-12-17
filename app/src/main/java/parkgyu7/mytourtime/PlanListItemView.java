package parkgyu7.mytourtime;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


public class PlanListItemView extends LinearLayout {

    // test
    private TextView itemTime;          // plan time
    private TextView itemPlanTitle;     // plan title
    Button optBtn;                      // opt Btn

    public PlanListItemView(Context context) {
        super(context);
        init(context);
    }

    public PlanListItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.plan_listitem,this,true);
        itemTime = (TextView) findViewById(R.id.itemTime);
        itemPlanTitle = (TextView) findViewById(R.id.itemPlanTitle);
        optBtn = (Button)findViewById(R.id.optBtn);

    }

    public void setContents(int index, String data) {
        if (index == 0) {
            itemTime.setText(data);
        } else if (index == 1) {
            itemPlanTitle.setText(data);
        } else {
            throw new IllegalArgumentException();
        }
    }

}
