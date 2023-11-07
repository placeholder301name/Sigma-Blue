package com.example.sigma_blue;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * This class creates the base database structure that an account will need when
 * using this application
 */
public class DatabaseInitializer {
    private CollectionReference dbRef;
    private Boolean exists;

    /**
     * Injection construction for better re-usability and defaults.
     * @return the DatabaseInitializer object that will be used to create a new
     * file structure.
     */
    public static DatabaseInitializer newInstance() {
        return new DatabaseInitializer( FirebaseFirestore.getInstance());
    }

    /**
     * Private constructor to enforce newInstance usage.
     * @param d is the database object
     */
    private DatabaseInitializer(final FirebaseFirestore d) {
        this.dbRef = d.collection(DatabaseNames.PRIMARY_COLLECTION.getName());
        this.exists = false;
    }

    /**
     * Method for checking if the account has been inserted into the database.
     * @param a is the Account that is being checked.
     * @return
     */
    public boolean checkExistence(final Account a) {
       dbRef.document(a.getUsername())
               .collection(DatabaseNames.PWDPATH.getName())
               .get().addOnCompleteListener(
            t -> {
                if (t.isSuccessful()) {
                    exists = t.getResult().size() != 0;
                }
                else Log.e("Document Error",
                        "Unable to get documents");
            }
        );
       return this.exists;
    }
}
