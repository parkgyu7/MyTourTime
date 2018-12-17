package parkgyu7.mytourtime;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Tour List Item View
 * Created by Administrator on 2018-02-27.
 */

public class TourListItemView extends LinearLayout {

    private TextView tourItemFirstDate;
    private TextView tourItemLastDate;
    private TextView tourItemTourTitle;

    private Button tourItemOptBtn;

    private Context mContext;

    public TourListItemView(Context context) {
        super(context);
        mContext = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.tour_listitem, this, true);

        tourItemFirstDate = (TextView) findViewById(R.id.tourItemFirstDate);
        tourItemLastDate = (TextView) findViewById(R.id.tourItemLastDate);
        tourItemTourTitle = (TextView) findViewById(R.id.tourItemTourTitle);

        tourItemOptBtn = (Button) findViewById(R.id.tourItemOptBtn);

    }

    public void setContents(int index, String data) {
        if (index == 0) {
            tourItemTourTitle.setText(data);
        } else if (index == 1) {
            tourItemFirstDate.setText(data);
        } else if (index == 2) {
            tourItemLastDate.setText(data);
        } else {
            throw new IllegalArgumentException();
        }
    }

}
