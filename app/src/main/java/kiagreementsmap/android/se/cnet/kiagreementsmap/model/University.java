package kiagreementsmap.android.se.cnet.kiagreementsmap.model;

/**
 * Created by Vivi on 2016-09-19.
 */
public class University {
    private int mId;
    private String mNamn;
    private String mAdress;
    private String mOrt;
    private String mLand;
    private String mLatCoord;
    private String mLngCoord;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getNamn() {
        return mNamn;
    }

    public void setNamn(String namn) {
        mNamn = namn;
    }

    public String getAdress() {
        return mAdress;
    }

    public void setAdress(String adress) {
        mAdress = adress;
    }

    public String getOrt() {
        return mOrt;
    }

    public void setOrt(String ort) {
        mOrt = ort;
    }

    public String getLand() {
        return mLand;
    }

    public void setLand(String land) {
        mLand = land;
    }

    public String getLatCoord() {
        return mLatCoord;
    }

    public void setLatCoord(String latCoord) {
        mLatCoord = latCoord;
    }

    public String getLngCoord() {
        return mLngCoord;
    }

    public void setLngCoord(String lngCoord) {
        mLngCoord = lngCoord;
    }
}
