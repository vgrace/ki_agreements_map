package kiagreementsmap.android.se.cnet.kiagreementsmap;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import kiagreementsmap.android.se.cnet.kiagreementsmap.model.Agreement;
import kiagreementsmap.android.se.cnet.kiagreementsmap.model.UniversityInfo;

/**
 * Created by Vivi on 2016-11-18.
 */

public class GridViewAdapter extends BaseAdapter {

    public static final String TAG = GridViewAdapter.class.getSimpleName();
    private final Context mContext;
    //private final ArrayList<Agreement> mAgreements;
    private final Agreement[] mAgreements;

    public GridViewAdapter(Context context, Agreement[] agreements){
        this.mContext = context;
        this.mAgreements = agreements;
    }

    @Override
    public int getCount() {
        return mAgreements.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "Agreements: " + mAgreements.length);
        final Agreement agreement = mAgreements[position];
        if(convertView == null){
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.grid_item, null);
        }
        final TextView year = (TextView) convertView.findViewById(R.id.itemYearTextView);
        final TextView education = (TextView) convertView.findViewById(R.id.itemEducationTextView);
        final TextView exchange = (TextView) convertView.findViewById(R.id.itemExchangeTextView);
        final TextView agreementT = (TextView) convertView.findViewById(R.id.itemAgreementTextView);
        final TextView date = (TextView) convertView.findViewById(R.id.itemDateTextView);
        final TextView level = (TextView) convertView.findViewById(R.id.itemLevelTextView);

        year.setText(agreement.getLasar());
        education.setText(agreement.getStudyProgram());
        exchange.setText(agreement.getExchangeProgram());
        agreementT.setText(agreement.getAOwner());
        date.setText(agreement.getExpirationDate());

        level.setText(getLevels(agreement.getNivaA(), agreement.getNivaD(), agreement.getNivaUG()));
        return convertView;
    }

    protected String getLevels(boolean levelA, boolean levelD, boolean levelUG){
        ArrayList<String> levelArray = new ArrayList<String>();
        String levelStr = "";
        if(levelA){
            levelArray.add("A");
        }
        if(levelD){
            levelArray.add("D");
        }
        if(levelUG){
            levelArray.add("UG");
        }
        for(int i = 0; i< levelArray.size(); i++){
            if(i == levelArray.size()-1) {
                levelStr += levelArray.get(i);
            }
            else{
                levelStr += levelArray.get(i) + ", ";
            }
        }
        return  levelStr;
    }
}
