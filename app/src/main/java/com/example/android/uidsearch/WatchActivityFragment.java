package com.example.android.uidsearch;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.uidsearch.uidprovider.UidContract;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A placeholder fragment containing a simple view.
 */
public class WatchActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private UidAdapter mUidAdapter;
    private static final int UID_LOADER = 1;

    private static final String[] UID_COLUMNS = {
            UidContract.UidStore.TABLE_NAME + "." + UidContract.UidStore._ID,
            UidContract.UidStore.COLUMN_DATE,
            UidContract.UidStore.COLUMN_UID,
            UidContract.UidStore.COLUMN_PW
    };

    static final int COL_DATE = 1;
    static final int COL_UID = 2;
    static final int COL_PW = 3;


    public WatchActivityFragment() {
    }

    @BindView(R.id.uid_list)
    ListView uidlist;

    @BindView(R.id.listview_empty)
    View emptyView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_watch, container, false);
        mUidAdapter = new UidAdapter(getContext(), null, 0);
        ButterKnife.bind(this, rootView);

        uidlist.setAdapter(mUidAdapter);
        uidlist.setEmptyView(emptyView);
        registerForContextMenu(uidlist);
        return rootView;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_context,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String uid, pw;
        Cursor elem = (Cursor) mUidAdapter.getItem(info.position);

        switch (item.getItemId()){
            case R.id.item_copy:
                if (elem != null) {
                    uid = elem.getString(COL_UID);
                    pw = elem.getString(COL_PW);
                    ClipboardManager clip = (ClipboardManager) getContext().
                            getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("item-info", uid + " " + pw);
                    clip.setPrimaryClip(clipData);
                    Toast.makeText(getContext(), "已复制", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.item_del:
                if (elem != null) {
                    String sUidStoreWithUidSel =
                            UidContract.UidStore.TABLE_NAME + "." + UidContract.UidStore.COLUMN_UID + " = ? ";
                    uid = elem.getString(COL_UID);
                    getContext().getContentResolver().delete(
                            UidContract.UidStore.CONTENT_URI,
                            sUidStoreWithUidSel,
                            new String[]{uid});
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(UID_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri mUri = UidContract.UidStore.buildUidUri();
        String sortOrder = UidContract.UidStore.COLUMN_DATE + " DESC";
        return new CursorLoader(getActivity(), mUri, UID_COLUMNS,
                null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mUidAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mUidAdapter.swapCursor(null);
    }
}
