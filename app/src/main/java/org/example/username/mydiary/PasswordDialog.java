package org.example.username.mydiary;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by hiroaki on 2016/11/23.
 */

public class PasswordDialog extends DialogFragment {

    private static final String PARAM_CONTENT_VIEW = "content_view";
    private static final String PARAM_ICON = "icon";
    private static final String PARAM_TITLE = "title";
    private static final String PARAM_MESSAGE = "message";

    public interface PasswordDialogListener {
        void onDialogPositiveClick(PasswordDialog dialog);
        void onDialogNegativeClick(PasswordDialog dialog);
    }

    PasswordDialogListener mListener;

    public static PasswordDialog newInstance(int contentView, int icon, String title, String message) {
        Bundle args = new Bundle();
        args.putInt(PARAM_CONTENT_VIEW, contentView);
        args.putInt(PARAM_ICON, icon);
        args.putString(PARAM_TITLE, title);
        args.putString(PARAM_MESSAGE, message);

        PasswordDialog fragment = new PasswordDialog();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        int layout = args.getInt(PARAM_CONTENT_VIEW);
        int icon = args.getInt(PARAM_ICON);
        String title = args.getString(PARAM_TITLE);
        String message = args.getString(PARAM_MESSAGE);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(layout, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setIcon(icon)
                .setTitle(title)
                .setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // アクティビティに対しpositiveボタンが押されたイベントを送り返します
                        mListener.onDialogPositiveClick(PasswordDialog.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // アクティビティに対しnegativeボタンが押されたイベントを送り返します
                        mListener.onDialogNegativeClick(PasswordDialog.this);
                    }
                });
        // 最後にビルダーでAlertDialogオブジェクトを作成して戻り値とします

        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (PasswordDialogListener) activity;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }
}
