package ar.com.sancorsalud.asociados.manager;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;

import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.Storage;

/**
 * Created by sergio on 1/29/17.
 */

public abstract class BaseListController<T> {

    protected ArrayList<T> mList = new ArrayList();

    public BaseListController() {
        mList = loadData();
        if(mList==null)
            mList = new ArrayList<>();
    }

    public boolean updateInProgress;

    public abstract String getObjectKey();

    public abstract String getLastUpdateKey();

    public abstract long getSecondsToUpdate();

    public abstract String getLocalJsonName();

    public abstract ArrayList<T> parseData(String json);

    protected abstract Type typeToken();

    public abstract HRequest createService(Response.Listener<ArrayList<T>> listener, Response.ErrorListener errorListener);


    public void getItems(final Response.Listener<ArrayList<T>> listener, final Response.ErrorListener errorListener) {

        if (mList != null && mList.size() > 0) {
            listener.onResponse(mList);
            updateIfNeeded();
            return;
        }

        updateInProgress = true;
        HRequest request = createService(new Response.Listener<ArrayList<T>>() {
            @Override
            public void onResponse(ArrayList<T> responseData) {
                Log.e("SUCCESS", getObjectKey());
                saveData(responseData);
                mList.clear();
                mList.addAll(responseData);
                saveLastUpdateDate();
                updateInProgress = false;
                listener.onResponse(mList);
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                updateInProgress = false;
                errorListener.onErrorResponse(error);
            }

        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request);
    }

    public void updateIfNeeded() {
        if (updateInProgress)
            return;

        if (shouldUpdateList()) {
            updateInProgress = true;
            HRequest request = createService(new Response.Listener<ArrayList<T>>() {
                @Override
                public void onResponse(ArrayList<T> responseData) {
                    Log.e("SUCCESS", getObjectKey());
                    saveData(responseData);
                    mList.clear();
                    mList.addAll(responseData);
                    saveLastUpdateDate();
                    updateInProgress = false;
                }

            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    updateInProgress = false;
                }

            });

            AppController.getInstance().getRestEngine().addToRequestQueue(request);

        }
    }


    public boolean thereAreItems() {
        return !mList.isEmpty();
    }

    private void saveData(ArrayList<T> list) {
        String json = ParserUtils.parseToJSON(list);
        Storage.getInstance().putPreferences(getObjectKey(), json);
    }

    protected ArrayList<T> loadData() {
        String json = Storage.getInstance().getPreferences(getObjectKey());
        if (json == null || json.length() == 0) {
            return loadDataFromDisk();
        }

        ArrayList<T> list = ParserUtils.parseFromJSON(json, typeToken());
        return list;
    }

    private ArrayList<T> loadDataFromDisk() {
        String fileName = getLocalJsonName();
        if (fileName == null)
            return null;

        try {
            String json;
            StringBuilder buf = new StringBuilder();
            InputStream stream = AppController.getInstance().getApplicationContext().getAssets().open(fileName);
            BufferedReader in = new BufferedReader(new InputStreamReader(stream, "UTF-8"));

            while ((json = in.readLine()) != null) {
                buf.append(json);
            }

            in.close();
            json = buf.toString();
            ArrayList<T> list = parseData(json);
            return list;
        } catch (IOException e) {
            return new ArrayList<T>();
        }
    }

    private void saveLastUpdateDate() {
        Date date = new Date(System.currentTimeMillis());
        Storage.getInstance().putLongPreferences(getLastUpdateKey(), date.getTime() / 1000);
    }

    protected boolean shouldUpdateList() {
        long lastUpdate = Storage.getInstance().getLongPreferences(getLastUpdateKey());
        Date date = new Date(System.currentTimeMillis());
        long seconds = date.getTime() / 1000;
        return seconds - lastUpdate > getSecondsToUpdate();
    }

}