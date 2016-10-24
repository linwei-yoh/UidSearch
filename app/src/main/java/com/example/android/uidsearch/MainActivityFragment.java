package com.example.android.uidsearch;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.android.uidsearch.uidprovider.UidContract;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static final String[] UID_COLUMNS = {
            UidContract.UidStore.TABLE_NAME + "." + UidContract.UidStore._ID,
            UidContract.UidStore.COLUMN_PW
    };

    static final int COL_PW = 1;

    private QueryHandler mQueryHandler;
    private static final int QUERY_TOKEN = 1;

    public MainActivityFragment() {
    }

    @BindView(R.id.Input_Uid)
    EditText Input_Uid;
    @BindView(R.id.result)
    EditText Output_result;
    @BindView(R.id.calculate)
    Button Btncal;

    @OnClick(R.id.calculate)
    public void btnClick() {
        String id = Input_Uid.getText().toString().trim();
        if (id.equals("")) {
            Output_result.setText("");
            return;
        }
        String sUidStoreWithUidSel =
                UidContract.UidStore.TABLE_NAME + "." + UidContract.UidStore.COLUMN_UID + " = ? ";

        mQueryHandler.startQuery(QUERY_TOKEN, null,
                UidContract.UidStore.CONTENT_URI, UID_COLUMNS,
                sUidStoreWithUidSel, new String[]{id}, null);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mQueryHandler = new QueryHandler(getContext().getContentResolver());
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);

        Input_Uid.setTransformationMethod(new AllCapTransformationMethod());
        return rootView;
    }

    private class QueryHandler extends AsyncQueryHandler {
        public QueryHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            super.onQueryComplete(token, cookie, cursor);

            if (cursor != null && cursor.moveToFirst()) {
                Output_result.setText(cursor.getString(COL_PW));
            }
            else{
                Output_result.setText("无记录");
            }
        }
    }
}
