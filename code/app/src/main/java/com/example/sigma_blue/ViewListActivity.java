package com.example.sigma_blue;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public class ViewListActivity extends BaseActivity {

    /* Tracking views that gets reused. Using nested class because struct */
    // https://stackoverflow.com/questions/24471109/recyclerview-onclick
    private class ViewHolder extends RecyclerView.ViewHolder {
        public Button searchButton;
        public Button sortFilterButton;
        public Button optionsButton;
        public FloatingActionButton addEntryButton;
        public TextView summaryView;

        /**
         * Construction of this nested class will bind the UI element to a 'package'
         */
        private ViewHolder(View itemView) {
            super(itemView);
            this.searchButton = findViewById(R.id.searchButton);
            this.sortFilterButton = findViewById(R.id.sortButton);
            this.optionsButton = findViewById(R.id.optionButton);
            this.addEntryButton = findViewById(R.id.addButton);
            this.summaryView = findViewById(R.id.summaryTextView);

            /* Setting up defaults for the UI elements */
            setSummaryView(Optional.empty());
        }

        /**
         * This method updates the summary text view with the sum value. This
         * value can be empty.
         * @param sum is an Optional wrapper which will either contain the sum,
         *            or nothing.
         */
        public void setSummaryView(Optional<Float> sum) {
            if (sum.isPresent())this.summaryView
                    .setText(formatSummary(sum.get()));
            else this.summaryView
                    .setText(R.string.empty_summary_view);
        }

        /**
         * Returns the formatted output for the summary text view.
         * @param sum is a float that represents the sum
         * @return the formatted string.
         */
        public String formatSummary(Float sum) {
            return String.format(Locale.ENGLISH,
                    "The total value: %7.2f", sum);
        }
    }

    private FragmentLauncher fragmentLauncher;
    private final ActivityLauncher<Intent, ActivityResult> activityLauncher = ActivityLauncher.registerActivityForResult(this);
    private ViewHolder viewHolder;              // Encapsulation of the Views
    private ItemList itemList;
    private Account currentAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /* Setting up the basics of the activity */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_list);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentAccount = (Account) extras.getSerializable("account");
        } else {
            Log.e("DEBUG", "No Account object in bundle!");
            currentAccount = new Account("UI_Test_User", "password");
        }
        /* Code section for linking UI elements */
        RecyclerView rvItemListView = findViewById(R.id.listView);
        this.viewHolder = this.new ViewHolder(rvItemListView);


        /* ItemList encapsulates both the database and the adapter */
        this.itemList = ItemList.newInstance(currentAccount, this::handleClick);
        itemList.setSummaryView(viewHolder.summaryView);
        fragmentLauncher = FragmentLauncher.newInstance(this);  // Embedding the fragment

        /* Linking the adapter to the UI */
        rvItemListView.setAdapter(itemList.getAdapter());
        rvItemListView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));

        /* Setting up the on click listeners*/
        setUIOnClickListeners();

    }

    /**
     * listener used to deal with the user clicking on a thing in the list. This method is passed to
     * the ItemList constructor as a callback function, but not called directly from anywhere.
     * see <a href="https://stackoverflow.com/questions/24471109/recyclerview-onclick">...</a>,
     * answer from Marurban
     * @param item Clicked on item
     */
    private void handleClick(Item item) {
        Log.i("DEBUG", item.getName());
        Intent intent = new Intent(ViewListActivity.this, AddEditActivity.class);
        intent.putExtra("item", item);
        intent.putExtra("mode", "edit");
        activityLauncher.launch(intent, this::processNewItemResult);
    }

    /**
     * Either adds a new item to the list or updates an existing one.
     * @param result result from the AddEditActivity
     */
    protected void processNewItemResult(ActivityResult result) {
        Bundle extras = result.getData().getExtras();
        //Item testItem = new Item("ThinkPad", new Date(), "Nice UNIX book","", "IBM", "T460", 300f);
        Item updatedItem = null;
        String updatedItemID = null;
        boolean onDeletion = false;
        try {
            updatedItem = (Item) extras.getSerializable("item");
            updatedItemID = extras.getString("id");
            onDeletion = extras.getBoolean("onDeletion");
        } catch (NullPointerException e) {
            Log.e("DEBUG", "New intent without extras!");
        }

        if (updatedItem == null) {
            Log.e("DEBUG", "Null updated item");
            return;
        }

        if (onDeletion) {
            this.itemList.remove(updatedItem);
        } else if (Objects.equals(updatedItemID, "") || updatedItemID == null) {
            this.itemList.add(updatedItem);
        } else {
            this.itemList.updateItem(updatedItem, updatedItemID);
        }

    }

    /* Fragment result listeners are lambda expressions that controls what the class does when the
    * results are received.*/
    FragmentResultListener addFragmentResultListener = (requestKey, result) -> {};

    /**
     * This method sets all the on click listeners for all the interactive UI elements.
     */
    private void setUIOnClickListeners() {
        viewHolder.addEntryButton.setOnClickListener(v -> {
            Item newItem = new Item();
            Intent intent = new Intent(ViewListActivity.this, AddEditActivity.class);
            intent.putExtra("item", newItem);
            intent.putExtra("mode", "add");
            activityLauncher.launch(intent, this::processNewItemResult);

        });  // Launch add activity.
        viewHolder.searchButton.setOnClickListener(v -> {});    // Launch search fragment
        viewHolder.sortFilterButton.setOnClickListener(v -> {});
        viewHolder.optionsButton.setOnClickListener(v -> {});
    }
}
