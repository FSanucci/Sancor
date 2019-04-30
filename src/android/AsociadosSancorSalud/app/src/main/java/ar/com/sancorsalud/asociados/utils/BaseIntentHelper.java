package ar.com.sancorsalud.asociados.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.io.Serializable;

import ar.com.sancorsalud.asociados.R;

/**
 * Created by sergio on 10/31/16.
 */

public class BaseIntentHelper {
    protected static AppController appInstance = AppController.getInstance();

    public static void shareURL(Activity activity, String url) {
        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    public static void shareItem(Activity activity, String extraText) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, extraText);
        activity.startActivity(Intent.createChooser(shareIntent, activity.getString(R.string.share_chooser)));
    }

    protected static <T> Intent launchIntent(Activity activity, Class<T> className) {
        return launchIntent(activity, className, false, null);
    }

    protected static <T> Intent launchIntentAndFinish(Activity activity, Class<T> className) {
        return launchIntent(activity, className, true, null);
    }

    protected static <T> Intent launchIntentAndFinish(Activity activity, Class<T> className, Bundle params) {
        return launchIntent(activity, className, true, params);
    }

    protected static <T> Intent launchIntent(Activity activity, Class<T> className, boolean finish, Bundle params, int requestCode) {
        Intent intent = new Intent(appInstance, className);
        if (params != null) {
            intent.putExtras(params);
        }
        activity.startActivityForResult(intent, requestCode);

        if (finish) {
            activity.finish();
        }
        return intent;
    }

    protected static <T> Intent launchIntent(Fragment fragment, Class<T> className, boolean finish, Bundle params, int requestCode) {
        Intent intent = new Intent(appInstance, className);
        if (params != null) {
            intent.putExtras(params);
        }
        fragment.getActivity().startActivityForResult(intent, requestCode);

        if (finish && fragment.getActivity()!=null) {
            fragment.getActivity().finish();
        }
        return intent;
    }

    protected static <T> Intent launchIntent(Activity activity, Class<T> className, Bundle params, int requestCode) {
        Intent intent = new Intent(appInstance, className);
        if (params != null) {
            intent.putExtras(params);
        }
        activity.startActivityForResult(intent, requestCode);

        return intent;
    }

    protected static <T> Intent launchIntent(Activity activity, Class<T> className, boolean finish, Bundle params) {
        Intent intent = new Intent(appInstance, className);
        if (params != null) {
            intent.putExtras(params);
        }
        activity.startActivity(intent);

        if (finish) {
            activity.finish();
        }
        return intent;
    }

    protected static <T> Intent launchIntent(Activity activity, Class<T> className, boolean finish, String key, Serializable serializable) {
        Intent intent = new Intent(appInstance, className);
        if (key != null) {
            intent.putExtra(key, serializable);
        }
        activity.startActivity(intent);

        if (finish) {
            activity.finish();
        }
        return intent;
    }
}