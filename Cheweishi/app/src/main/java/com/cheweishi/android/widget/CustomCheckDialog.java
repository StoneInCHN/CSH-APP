package com.cheweishi.android.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.cheweishi.android.cheweishi.R;

/**
 * Created by tangce on 4/18/2016.
 */
public class CustomCheckDialog extends Dialog {
    public CustomCheckDialog(Context context) {
        this(context, 0);
    }

    public CustomCheckDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected CustomCheckDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public static class Builder implements View.OnClickListener {
        private Context context;
        private String title;
        private CharSequence message;
        private String positiveButtonText;
        private String negativeButtonText;
        private View contentView;
        private boolean showEtFlag = false;
        private DialogInterface.OnClickListener positiveButtonClickListener;
        private DialogInterface.OnClickListener negativeButtonClickListener;
        private EditText et;
        private View bottom_line;
        private boolean flag;


        private Button confirm;
        private Button cancel;
        private CustomCheckDialog dialog;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder(Context context, boolean flag) {
            this.context = context;
            this.flag = flag;
        }

        public Builder setMessage(CharSequence message) {
            this.message = message;
            return this;
        }

        /**
         * Set the Dialog message from resource
         *
         * @return
         */
        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }

        /**
         * Set the Dialog title from resource
         *
         * @param title
         * @return
         */
        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        /**
         * Set the Dialog title from String
         *
         * @param title
         * @return
         */

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        /**
         * Set the positive button resource and it's listener
         *
         * @param positiveButtonText
         * @return
         */
        public Builder setPositiveButton(int positiveButtonText,
                                         DialogInterface.OnClickListener listener) {
            this.positiveButtonText = (String) context
                    .getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText,
                                         DialogInterface.OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(int negativeButtonText,
                                         DialogInterface.OnClickListener listener) {
            this.negativeButtonText = (String) context
                    .getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText,
                                         DialogInterface.OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        public void hideLayout() {
            bottom_line.setVisibility(View.GONE);
        }

        public CustomCheckDialog create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            dialog = new CustomCheckDialog(context,
                    R.style.Dialog);
            View layout = inflater.inflate(R.layout.insurance_check_view, null);
            dialog.addContentView(layout, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));


            confirm = (Button) layout.findViewById(R.id.positiveButton);
            cancel = (Button) layout.findViewById(R.id.negativeButton);
            cancel.setOnClickListener(this);
            confirm.setOnClickListener(this);


            dialog.setContentView(layout);
            return dialog;
        }

        public boolean isShowEtFlag() {
            return showEtFlag;
        }

        public void setShowEtFlag(boolean showEtFlag) {
            this.showEtFlag = showEtFlag;
        }

        public EditText getEt() {
            return et;
        }

        public void setEt(EditText et) {
            this.et = et;
        }

        @Override
        public void onClick(View v) {

            int id = v.getId();
            switch (id) {
                case R.id.positiveButton: // 确认

                    break;
                case R.id.negativeButton: // 取消
                    if (null != dialog)
                        dialog.dismiss();
                    break;
            }
        }
    }

}

