/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * Created by pharris on 24/02/16.
 */
public class EmailHelper {

    public static void sendEmail(Context context, @NonNull String recipient, String subject, String body, String userInstruction){
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", recipient, null));
        intent.putExtra(Intent.EXTRA_SUBJECT, null != subject ? subject : "");
        intent.putExtra(Intent.EXTRA_TEXT, null != body ? body : "");

        context.startActivity(Intent.createChooser(intent, userInstruction));
    }
}
