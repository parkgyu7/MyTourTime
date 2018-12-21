package parkgyu7.mytourtime;


public class TourListItem {

    private String[] mData;
    private String mId;
    /**
     * True if this item is selectable
     */
    private boolean mSelectable = true;

    public TourListItem(String itemId, String[] obj){
        this.mId = itemId;
        this.mData= obj;
    }

    public TourListItem(String tourId, String tourTitle, String tourFirstDate, String tourLastDate, String tourFavorite, String tourDone) {
        mId = tourId;
        mData = new String[6];
        mData[0] = tourId;
        mData[1] = tourTitle;
        mData[2] = tourFirstDate;
        mData[3] = tourLastDate;
        mData[4] = tourFavorite;
        mData[5] = tourDone;

    }


    /**
     * True if this item is selectable
     */
    public boolean isSelectable() {
        return mSelectable;
    }


    /**
     * Set selectable flag
     */
    public void setSelectable(boolean selectable) {
        mSelectable = selectable;
    }

    public String getId() {return mId;}

    public void setId(String mId) {this.mId = mId;}
    public String[] getData() {return mData;}

    public String getData(int index){
        if(mData == null || index>= mData.length){
            return null;
        }
        return mData[index];
    }

    public void setData(String[] mData) {this.mData = mData;}




    /**
     * Compare with the input object
     *
     * @param other
     * @return
     */
    public int compareTo(TourListItem other) {
        if (mData != null) {
            Object[] otherData = other.getData();
            if (mData.length == otherData.length) {
                for (int i = 0; i < mData.length; i++) {
                    if (!mData[i].equals(otherData[i])) {
                        return -1;
                    }
                }
            } else {
                return -1;
            }
        } else {
            throw new IllegalArgumentException();
        }

        return 0;
    }


}
