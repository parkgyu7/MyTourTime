package parkgyu7.mytourtime;


public class PlanListItem {

	private String[] mData;
	private String mId;
	/**
	 * True if this item is selectable
	 */
	private boolean mSelectable = true;

	public PlanListItem(String itemId, String[] obj) {
		this.mId = itemId;
		this.mData = obj;
	}


	public PlanListItem(String planId, String tourId, String planDateTime, String planTitle, String planBody)
	{
		mId = planId;
		mData = new String[4];
		mData[0] = tourId;
		mData[1] = planDateTime;
		mData[2] = planTitle;
		mData[3] = planBody;
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

	public String getId() {
		return mId;
	}

	public void setId(String itemId) {
		mId = itemId;
	}

	public String[] getData() {
		return mData;
	}

	public String getData(int index) {
		if (mData == null || index >= mData.length) {
			return null;
		}

		return mData[index];
	}

	public void setData(String[] obj) {
		this.mData = obj;
	}


	/**
	 * Compare with the input object
	 *
	 */
	public int compareTo(PlanListItem other) {
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
