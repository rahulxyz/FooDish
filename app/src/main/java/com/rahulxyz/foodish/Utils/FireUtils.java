package com.rahulxyz.foodish.Utils;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by raul_Will on 10/21/2017.
 */

public class FireUtils {

    private static FirebaseDatabase mDatabase;

    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }
        return mDatabase;
    }
}
