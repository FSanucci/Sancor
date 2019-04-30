package ar.com.sancorsalud.asociados.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ar.com.sancorsalud.asociados.R;

/**
 * Created by sergio on 1/9/17.
 */

@SuppressLint("NewApi")
public class DialogHelper {

    public static void showNoSDCardDialog(final Activity origin) {
        showMessage(origin, origin.getResources().getString(R.string.no_sd_card_msg));
    }

    public static void showStandardErrorMessage(final Activity origin, final DialogInterface.OnClickListener positiveListener) {
        String title = origin.getResources().getString(R.string.error);
        String message = origin.getResources().getString(R.string.http_exception_msg);
        showMessage(origin,title,message,positiveListener);
    }

    public static void showNoInternetErrorMessage(final Activity origin, final DialogInterface.OnClickListener positiveListener) {
        String title = origin.getResources().getString(R.string.no_internet_title);
        String message = origin.getResources().getString(R.string.no_internet_msg);
        showMessage(origin,title,message,positiveListener);
    }

    public static void showCopagosErrorMessage(final Activity origin, final String copago, final DialogInterface.OnClickListener positiveListener) {
        String title = origin.getResources().getString(R.string.affil_copagos_error_title);
        String message = origin.getResources().getString(R.string.affil_copagos_error_message) + " " + copago;
        showMessage(origin,title,message,positiveListener);
    }

    public static void showMessage(final Activity origin, final String msj) {
        showMessage(origin, msj, (DialogInterface.OnClickListener) null);
    }

    public static void showMessage(final Activity origin, final String msj, final DialogInterface.OnClickListener listener) {
        showMessage(origin, origin.getResources().getString(R.string.app_name), msj, listener);
    }

    public static void showMessage(final Activity origin, final String title, final String msj) {
        showMessage(origin, title, msj, null);
    }

    public static void showMessage(final Activity origin, final int title, final int msj) {
        showMessage(origin, origin.getResources().getString(title), origin.getResources().getString(msj), null);
    }


    // Internet Dialog
    public static void showMessage(final Activity origin, final String title, final String msj, final int iconStatus, final DialogInterface.OnClickListener positiveListener) {
        origin.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                AlertDialog.Builder builder = getBuilder(origin);
                if (title != null) {
                    builder.setTitle(title);
                } else {
                    builder.setTitle(origin.getResources().getString(R.string.app_name));
                }
                if ((msj == null) || (msj.trim().length() == 0)) {
                    builder.setMessage(origin.getResources().getString(R.string.http_exception_msg));
                } else {
                    builder.setMessage(msj);
                }
                builder.setIcon(iconStatus);
                builder.setPositiveButton(origin.getResources().getString(R.string.option_accept), positiveListener);

                builder.show();
            }
        });
    }


    public static void showArrayMessage(final Activity origin, final String title, final List<String> msjList, final DialogInterface.OnClickListener positiveListener) {
        origin.runOnUiThread(new Runnable() {

            ArrayAdapter mssgAdapter = new ArrayAdapter<String>(origin, android.R.layout.simple_spinner_dropdown_item, msjList);

            @Override
            public void run() {
                AlertDialog.Builder builder = getBuilder(origin);
                if (title != null) {
                    builder.setTitle(title);
                } else {
                    builder.setTitle(origin.getResources().getString(R.string.app_name));
                }

                builder.setAdapter(mssgAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //etregstate.setTextColor(getResources().getColor(R.color.black));
                        //etregstate.setText(stateList.get(which));
                        dialog.dismiss();
                    }
                });

                builder.setPositiveButton(origin.getResources().getString(R.string.option_accept), positiveListener);
                builder.show();
            }
        });
    }



    // Ok Dialog
    public static void showMessage(final Activity origin, final String title, final String msj, final DialogInterface.OnClickListener positiveListener) {
        origin.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                AlertDialog.Builder builder = getBuilder(origin);
                if (title != null) {
                    builder.setTitle(title);
                } else {
                    builder.setTitle(origin.getResources().getString(R.string.app_name));
                }
                if ((msj == null) || (msj.trim().length() == 0)) {
                    builder.setMessage(origin.getResources().getString(R.string.http_exception_msg));
                } else {
                    builder.setMessage(msj);
                }
                builder.setPositiveButton(origin.getResources().getString(R.string.option_accept), positiveListener);
                if(!origin.isFinishing())
                    builder.show();
            }
        });
    }

    public static void showMessageWithCustomButton(final Activity origin, final String title, final int msj, final int resourceId, final DialogInterface.OnClickListener positiveListener) {
        origin.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                AlertDialog.Builder builder = getBuilder(origin);
                if (title != null) {
                    builder.setTitle(title);
                } else {
                    builder.setTitle(origin.getResources().getString(R.string.app_name));
                }
                builder.setMessage(origin.getResources().getString(msj));

                builder.setPositiveButton(origin.getResources().getString(resourceId), positiveListener);
                if(!origin.isFinishing())
                    builder.show();
            }
        });
    }



    //Ok Dialog not cancelable
    public static void showMessageNotCancelable(final Activity origin, final String title, final String msj, final DialogInterface.OnClickListener positiveListener) {
        origin.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                AlertDialog.Builder builder = getBuilder(origin);
                builder.setCancelable(false);
                if (title != null) {
                    builder.setTitle(title);
                } else {
                    builder.setTitle(origin.getResources().getString(R.string.app_name));
                }
                if ((msj == null) || (msj.trim().length() == 0)) {
                    builder.setMessage(origin.getResources().getString(R.string.http_exception_msg));
                } else {
                    builder.setMessage(msj);
                }
                builder.setPositiveButton(origin.getResources().getString(R.string.option_accept), positiveListener);

                if(!origin.isFinishing())
                    builder.show();
            }
        });
    }

    //Only title, Ok Cancel Dialog
    public static void showDialog(final Activity origin, final String title, final String positive, final DialogInterface.OnClickListener positiveListener,
                                  final String negative, final DialogInterface.OnClickListener negativeListener, final boolean cancelListener) {
        origin.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                AlertDialog.Builder builder = getBuilder(origin);
                builder.setTitle(title);
                builder.setCancelable(cancelListener);
                builder.setPositiveButton(positive, positiveListener);
                builder.setNegativeButton(negative, negativeListener);
                AlertDialog dialog = builder.show();
                TextView messageText = (TextView) dialog.findViewById(android.R.id.message);
                if (messageText != null) {
                    messageText.setGravity(Gravity.CENTER);
                }
            }
        });
    }

    //Only Title, Ok Cancel Dialog Cancelable
    public static void showDialog(final Activity origin, final String title, final String msj, final String positive,
                                  final DialogInterface.OnClickListener positiveListener, final String negative, final DialogInterface.OnClickListener negativeListener) {
        origin.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                AlertDialog.Builder builder = getBuilder(origin);
                builder.setTitle(title);
                builder.setMessage(msj);
                builder.setCancelable(false);
                builder.setPositiveButton(positive, positiveListener);
                builder.setNegativeButton(negative, negativeListener);
                AlertDialog dialog = builder.show();
                TextView messageText = (TextView) dialog.findViewById(android.R.id.message);
                if (messageText != null) {
                    messageText.setGravity(Gravity.CENTER);
                }
            }
        });
    }

    //Ok Cancel Dialog
    public static void showDialog(final Activity origin, final String title, final String msj, final String positive,
                                  final DialogInterface.OnClickListener positiveListener, final String negative, final DialogInterface.OnClickListener negativeListener,
                                  final boolean cancelListener) {
        origin.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                AlertDialog.Builder builder = getBuilder(origin);
                builder.setTitle(title);
                builder.setMessage(msj);
                builder.setCancelable(cancelListener);
                builder.setPositiveButton(positive, positiveListener);
                builder.setNegativeButton(negative, negativeListener);
                AlertDialog dialog = builder.show();
                TextView messageText = (TextView) dialog.findViewById(android.R.id.message);
                if (messageText != null) {
                    messageText.setGravity(Gravity.CENTER);
                }
            }
        });
    }

    // List Dialog
    public static void showOptionDialog(final Activity origin, final String title, final String[] items, final DialogInterface.OnClickListener listener,
                                        final DialogInterface.OnCancelListener onCancelListener, final boolean cancelable) {
        origin.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = getBuilder(origin);
                if (title != null) {
                    builder.setTitle(title);
                } else {
                    builder.setTitle(origin.getResources().getString(R.string.app_name));
                }
                builder.setCancelable(cancelable);
                builder.setItems(items, listener);
                if (onCancelListener == null) {
                    builder.setCancelable(false);
                } else {
                    builder.setOnCancelListener(onCancelListener);
                }
                builder.show();
            }
        });
    }

    public static void showOptionDialog(final Activity origin, final String[] items, final DialogInterface.OnClickListener listener, final boolean cancelable) {
        showOptionDialog(origin, null, items, listener, null, cancelable);
    }

    public static void showOptionDialog(final Activity origin, String title, String[] items, DialogInterface.OnClickListener listener, final boolean cancelable) {
        showOptionDialog(origin, title, items, listener, null, cancelable);
    }

    private static AlertDialog.Builder getBuilder(Activity origin) {
        if (Build.VERSION.SDK_INT >= 11) {
            return new AlertDialog.Builder(origin);
        }
        return new AlertDialog.Builder(origin);
    }

}