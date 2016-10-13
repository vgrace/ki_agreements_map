package kiagreementsmap.android.se.cnet.kiagreementsmap.model;

/**
 * Created by Vivi on 2016-10-13.
 */
public class Agreement {
    private int mUniversitetId;
    private String mLasar;
    private String mDiarienummer;
    private String mAType;
    private String mAOwner;
    private Boolean mNivaUG;
    private Boolean mNivaA;
    private Boolean mNivaD;
    private String mStudyProgram;
    private String mExchangeProgram;
    private String mExpirationDate;
    private Boolean mInactive;

    public int getUniversitetId() {
        return mUniversitetId;
    }

    public void setUniversitetId(int universitetId) {
        mUniversitetId = universitetId;
    }

    public String getLasar() {
        return mLasar;
    }

    public void setLasar(String lasar) {
        mLasar = lasar;
    }

    public String getDiarienummer() {
        return mDiarienummer;
    }

    public void setDiarienummer(String diarienummer) {
        mDiarienummer = diarienummer;
    }

    public String getAType() {
        return mAType;
    }

    public void setAType(String AType) {
        mAType = AType;
    }

    public String getAOwner() {
        return mAOwner;
    }

    public void setAOwner(String AOwner) {
        mAOwner = AOwner;
    }

    public Boolean getNivaUG() {
        return mNivaUG;
    }

    public void setNivaUG(Boolean nivaUG) {
        mNivaUG = nivaUG;
    }

    public Boolean getNivaA() {
        return mNivaA;
    }

    public void setNivaA(Boolean nivaA) {
        mNivaA = nivaA;
    }

    public Boolean getNivaD() {
        return mNivaD;
    }

    public void setNivaD(Boolean nivaD) {
        mNivaD = nivaD;
    }

    public String getStudyProgram() {
        return mStudyProgram;
    }

    public void setStudyProgram(String studyProgram) {
        mStudyProgram = studyProgram;
    }

    public String getExchangeProgram() {
        return mExchangeProgram;
    }

    public void setExchangeProgram(String exchangeProgram) {
        mExchangeProgram = exchangeProgram;
    }

    public String getExpirationDate() {
        return mExpirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        mExpirationDate = expirationDate;
    }

    public Boolean getInactive() {
        return mInactive;
    }

    public void setInactive(Boolean inactive) {
        mInactive = inactive;
    }



    /*"UniversitetId": 1024,
        "Lasar": "16/17",
        "Diarienummer": "2-4221/2013 kopplat till beslut Programnämnd 10 - dnr 2-4/201",
        "ATitle": "",
        "AType": "Education",
        "AOwner": "Study programme",
        "NivaUG": true,
        "NivaA": false,
        "NivaD": false,
        "StudyProgram": "Dental Hygiene",
        "ExchangeProgram": "Erasmus",
        "MoU": 0,
        "ExpirationDate": "2017-06-30",
        "Inactive": false*/
}
