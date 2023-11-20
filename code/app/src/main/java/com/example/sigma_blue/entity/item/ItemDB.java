package com.example.sigma_blue.entity.item;

import android.util.Log;


import com.example.sigma_blue.database.ADatabaseHandler;
import com.example.sigma_blue.entity.account.Account;
import com.example.sigma_blue.database.DatabaseNames;
import com.example.sigma_blue.query.SortField;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * This class handles database handling.
 */
public class ItemDB extends ADatabaseHandler<Item> {

    private FirebaseFirestore firestoreInjection;
    private Account account;
    private List<Item> items;

    /**
     * newInstance method for hiding construction.
     * @param a is the account that is doing the database transactions.
     * @return a new ItemDB instance tied to the account.
     */
    public static ItemDB newInstance(Account a) {
        ItemDB ret = new ItemDB();
        ret.setAccount(a);
        return ret;
    }

    /**
     * Firestore injection construction. Required for testing database
     * @param f the Firestore that is being used
     * @param a Account that the item database handler is controlling.
     * @return a new ItemDB instance.
     */
    public static ItemDB newInstance(FirebaseFirestore f,
                                     Account a) {
        ItemDB ret = new ItemDB();
        ret.setFirestore(f);
        ret.setAccount(a);
        return ret;
    }

    /**
     * Bare Constructor
     */
    private ItemDB() {
    }


    /**
     * Embed the account into the database. Only used when creating a new
     * instance.
     * @param a is an Account object that the instance of the database is
     *          querying.
     */
    private void setAccount(Account a) {
        /* Allows for legacy usage */
        if (this.firestoreInjection == null) {
            this.ref = FirebaseFirestore.getInstance()
                    .collection(DatabaseNames.PRIMARY_COLLECTION.getName())
                    .document(a.getUsername())
                    .collection(DatabaseNames.ITEMS_COLLECTION.getName());
            firestoreInjection = FirebaseFirestore.getInstance();
        }
        /* Injection already exists */
        else this.ref = firestoreInjection
                .collection(DatabaseNames.PRIMARY_COLLECTION.getName())
                .document(a.getUsername())
                .collection(DatabaseNames.ITEMS_COLLECTION.getName());
        this.account = a;
    }

    private void setFirestore(FirebaseFirestore f) {
        this.firestoreInjection = f;
    }

    public Account getAccount() {
        return this.account;
    }

    public CollectionReference getCollectionReference() {
        return this.ref;
    }
}
