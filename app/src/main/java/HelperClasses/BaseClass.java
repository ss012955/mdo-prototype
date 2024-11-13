package HelperClasses;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
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

}
