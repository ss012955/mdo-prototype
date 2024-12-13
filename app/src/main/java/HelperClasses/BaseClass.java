package HelperClasses;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.prototype.R;

public class BaseClass {

    public void showTwoButtonDialog(Context context, String title, String message, String positiveText, String negativeText,
                                  View.OnClickListener positiveAction, View.OnClickListener negativeAction) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_twobutton);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
        TextView dialogMessage = dialog.findViewById(R.id.dialog_message);
        Button positiveButton = dialog.findViewById(R.id.positive_button);
        Button negativeButton = dialog.findViewById(R.id.negative_button);

        dialogTitle.setText(title);
        dialogMessage.setText(message);
        positiveButton.setText(positiveText);
        negativeButton.setText(negativeText);

        positiveButton.setOnClickListener(v -> {
            positiveAction.onClick(v);
            dialog.dismiss();
        });

        negativeButton.setOnClickListener(v -> {
            negativeAction.onClick(v);
            dialog.dismiss();
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void showOneButtonDialog(Context context, String title, String message, String positiveText,
                                     View.OnClickListener oneAction){
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_onebutton);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
        TextView dialogMessage = dialog.findViewById(R.id.dialog_message);
        Button oneButton = dialog.findViewById(R.id.retry_button);

        dialogTitle.setText(title);
        dialogMessage.setText(message);
        oneButton.setText(positiveText);

        oneButton.setOnClickListener(v -> {
            oneAction.onClick(v);
            dialog.dismiss();
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void showOneButtonDialogNotCancellable(Context context, String title, String message, String positiveText,
                                    View.OnClickListener oneAction){
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_onebutton);

        // Prevent the dialog from being canceled
        dialog.setCancelable(false);

        TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
        TextView dialogMessage = dialog.findViewById(R.id.dialog_message);
        Button oneButton = dialog.findViewById(R.id.retry_button);

        dialogTitle.setText(title);
        dialogMessage.setText(message);
        oneButton.setText(positiveText);

        oneButton.setOnClickListener(v -> {
            oneAction.onClick(v);
            dialog.dismiss(); // Dismiss only when the button is clicked
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public Dialog showProgressDialog(Context context, String message) {
        // Create the dialog instance
        Dialog dialog = new Dialog(context);

        // Set the custom layout for the dialog
        dialog.setContentView(R.layout.progress_dialog);

        // Make the dialog non-cancelable
        dialog.setCancelable(false);

        // Find the ProgressBar and TextView in the dialog
        ProgressBar progressBar = dialog.findViewById(R.id.progress_bar);
        TextView dialogMessage = dialog.findViewById(R.id.progress_message);

        // Set the message text
        dialogMessage.setText(message);

        // Optionally, you can set progress to 0 at the start (or adjust based on your needs)
        progressBar.setProgress(0);

        // Show the dialog
        dialog.show();

        return dialog;
    }

}
