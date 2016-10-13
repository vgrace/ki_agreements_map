package kiagreementsmap.android.se.cnet.kiagreementsmap.model;

/**
 * Created by Vivi on 2016-10-13.
 */
public class UniversityInfo {
    private int mId;
    private String mName;
    private String mAdress;
    private String mOrt;
    private String mLand;
    private String mKod;
    private String mPostnr;
    private String mTelefonnr;
    private String mWWWadress;
    private Boolean mInaktivt;
    private String mKIBenamning;
    private String mLandNamn;
    private Agreement[] mAgreements;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Agreement[] getAgreements() {
        return mAgreements;
    }

    public void setAgreements(Agreement[] agreements) {
        mAgreements = agreements;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
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

    public String getKod() {
        return mKod;
    }

    public void setKod(String kod) {
        mKod = kod;
    }

    public String getPostnr() {
        return mPostnr;
    }

    public void setPostnr(String postnr) {
        mPostnr = postnr;
    }

    public String getTelefonnr() {
        return mTelefonnr;
    }

    public void setTelefonnr(String telefonnr) {
        mTelefonnr = telefonnr;
    }

    public String getWWWadress() {
        return mWWWadress;
    }

    public void setWWWadress(String WWWadress) {
        mWWWadress = WWWadress;
    }

    public Boolean getInaktivt() {
        return mInaktivt;
    }

    public void setInaktivt(Boolean inaktivt) {
        mInaktivt = inaktivt;
    }

    public String getKIBenamning() {
        return mKIBenamning;
    }

    public void setKIBenamning(String KIBenamning) {
        mKIBenamning = KIBenamning;
    }

    public String getLandNamn() {
        return mLandNamn;
    }

    public void setLandNamn(String landNamn) {
        mLandNamn = landNamn;
    }



    /*"Id": 1024,
            "Namn": "Aarhus Universitet",
            "Adress": "Vennelyst Boulevard 9",
            "Ort": "Århus C",
            "Land": "DK",
            "LatCoord": null,
            "LngCoord": null,
            "Kod": "DK ARHUS01",
            "Postnr": "DK- 8000",
            "Telefonnr": "+45 8942 1122",
            "WWWadress": "http://www.au.dk/",
            "Inaktivt": false,
            "KIBenamning": "DK Århus",
            "WWWadress_2": "",
            "LandNamn": "Danmark",
            "Agreements": [*/

}
