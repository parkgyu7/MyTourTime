package parkgyu7.mytourtime;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Tour List Item View
 * Created by Administrator on 2018-02-27.
 */

public class TourListItemViewCheckable extends LinearLayout implements Checkable{

    private TextView tourItemFirstDate;
    private TextView tourItemLastDate;
    private TextView tourItemTourTitle;

    private Context mContext;

    public TourListItemViewCheckable(Context context) {
        super(context);
        mContext = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.tour_listitem_checkable, this, true);


        tourItemFirstDate = (TextView) findViewById(R.id.tourItemFirstDate);
        tourItemLastDate = (TextView) findViewById(R.id.tourItemLastDate);
        tourItemTourTitle = (TextView) findViewById(R.id.tourItemTourTitle);

    }

    public void setContents(int index, String data) {

        if (index == 1) {
            tourItemTourTitle.setText(data);
        } else if (index == 2) {
            tourItemFirstDate.setText(data);
        } else if (index == 3) {
            tourItemLastDate.setText(data);
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public boolean isChecked() {
        CheckBox cb = (CheckBox) findViewById(R.id.checkBox) ;

        return cb.isChecked() ;
    }

    @Override
    public void toggle() {
        CheckBox cb = (CheckBox) findViewById(R.id.checkBox) ;

        setChecked(cb.isChecked() ? false : true) ;
    }

    @Override
    public void setChecked(boolean checked) {
        CheckBox cb = (CheckBox) findViewById(R.id.checkBox) ;

        if (cb.isChecked() != checked) {
            cb.setChecked(checked) ;
        }
    }

}
