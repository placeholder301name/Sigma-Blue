package com.example.sigma_blue.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sigma_blue.context.GlobalContext;
import com.example.sigma_blue.entity.item.Item;
import com.example.sigma_blue.R;
import androidx.annotation.ColorInt;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sigma_blue.entity.account.Account;
import com.example.sigma_blue.entity.item.Item;
import com.example.sigma_blue.entity.item.ItemDB;
import com.example.sigma_blue.entity.item.ItemList;
import com.example.sigma_blue.R;
import com.example.sigma_blue.entity.item.ItemListAdapter;
import com.example.sigma_blue.entity.tag.Tag;
import com.example.sigma_blue.fragments.FragmentLauncher;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.base.VerifyException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class ViewListActivity extends BaseActivity {

    /* Tracking views that gets reused. Using nested class because struct */
    // https://stackoverflow.com/questions/24471109/recyclerview-onclick
    private class ViewHolder {
        public Button searchButton;
        public Button sortFilterButton;
        public Button optionsButton;
        public Button deleteSelectedButton;
        public Button addTagsSelectedButton;
        public LinearLayout selectedItemsMenu;
        public FloatingActionButton addEntryButton;
        public TextView summaryView;
        public ListView listListView;

        /**
         * Construction of this nested class will bind the UI element to a 'package'
         */
        private ViewHolder() {
            this.listListView = findViewById(R.id.listView);
            this.searchButton = findViewById(R.id.searchButton);
            this.sortFilterButton = findViewById(R.id.sortButton);
            this.optionsButton = findViewById(R.id.optionButton);
            this.addEntryButton = findViewById(R.id.addButton);
            this.summaryView = findViewById(R.id.summaryTextView);
            this.deleteSelectedButton = findViewById(R.id.deleteSelectedButton);
            this.addTagsSelectedButton = findViewById(R.id.addTagsSelectedButton);
            this.selectedItemsMenu = findViewById(R.id.selectedItemsMenu);
        }
    }
    private final ActivityLauncher<Intent, ActivityResult> activityLauncher = ActivityLauncher.registerActivityForResult(this);
    private ViewHolder viewHolder;              // Encapsulation of the Views
    private GlobalContext globalContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /* Setting up the basics of the activity */
        super.onCreate(savedInstanceState); // Activity super
        setContentView(R.layout.view_list); // sets up the activity layout

        globalContext = GlobalContext.getInstance();

        if (globalContext.getAccount() == null) {
            // sometimes another thread ends up here with an empty global context
            // TODO: figure out why/how that happens.
            //throw new VerifyException("Must have an account");
            return;
        }

        /* Code section for linking UI elements */
        ListView itemListView = findViewById(R.id.listView);
        this.viewHolder = this.new ViewHolder();

        /* ItemList encapsulates both the database and the adapter */
        globalContext.setUpItemList();
        globalContext.getItemList().setListAdapter(new ItemListAdapter(this, viewHolder.summaryView));
        globalContext.getItemList().setSummaryView(viewHolder.summaryView);

        /* Linking the adapter to the UI */
        itemListView.setAdapter(globalContext.getItemList().getListAdapter());

        // set up thing for selected items
        this.viewHolder.selectedItemsMenu.setVisibility(View.GONE);
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
        Log.i("DEBUG", item.getName() + "Short Press");
        Intent intent = new Intent(ViewListActivity.this, AddEditActivity.class);
        globalContext.setCurrentItem(item);
        globalContext.newState("details_fragment");
        startActivity(intent);
    }

    /**
     * listened to deal with long presses
     * @param item Item that was long pressed on
     */
    private void handleLongClick(Item item) {
        Log.i("DEBUG", item.getName() + "Long Press");
        globalContext.toggleHighlightItem(item);

        if (globalContext.getHighlightedItems().size() > 0) {
            viewHolder.selectedItemsMenu.setVisibility(View.VISIBLE);
        } else {
            viewHolder.selectedItemsMenu.setVisibility(View.GONE);
        }
    }

    /**
     * Delete the selected items. Fully deletes them with no confirm
     */
    private void deleteSelectedItems() {
        for (Item i : globalContext.getHighlightedItems()) {
            globalContext.getItemList().remove(i);
        }
        globalContext.resetHighlightedItems();
        viewHolder.selectedItemsMenu.setVisibility(View.GONE);
    }

    /**
     * This method sets all the on click listeners for all the interactive UI elements.
     */
    private void setUIOnClickListeners() {
        viewHolder.addEntryButton.setOnClickListener(v -> {
            Intent intent = new Intent(ViewListActivity.this, AddEditActivity.class);
            globalContext.setCurrentItem(null);
            globalContext.newState("add_item_fragment");
            startActivity(intent);
        });  // Launch add activity.
        viewHolder.searchButton.setOnClickListener(v -> {});    // Launch search fragment
        viewHolder.sortFilterButton.setOnClickListener(v -> {});
        viewHolder.optionsButton.setOnClickListener(v -> {});
        viewHolder.deleteSelectedButton.setOnClickListener(v -> {this.deleteSelectedItems();});

        viewHolder.addTagsSelectedButton.setOnClickListener(v -> {
            viewHolder.selectedItemsMenu.setVisibility(View.GONE);
            globalContext.setCurrentItem(null);
            globalContext.newState("multi_select_tag_manager_fragment");
            Intent intent = new Intent(ViewListActivity.this, AddEditActivity.class);
            startActivity(intent);
        });
        /* The List View related control flow */
        viewHolder.listListView.setLongClickable(true); // Turning on long click

        viewHolder.listListView // This is for short clicks on a row
                .setOnItemClickListener((parent, view, position, id) -> {
                    this.handleClick(globalContext.getItemList().getItem(position));
        });

        /* The long click listener */
        viewHolder.listListView.setOnItemLongClickListener(
                (parent, view, position, id) -> {
                    this.handleLongClick(globalContext.getItemList().getItem(position));
                    return true;
                    // return true if the list 'consumes' the click, which it always
                    // does in our app
                });
    }

    /**
     * Method that will turn on the highlight of the view if it is selected,
     * otherwise reset it to the default background colour.
     * @param view is the view that is being checked.
     */
    private void highlightControl(View view, boolean selected) {
        @ColorInt int rowColor;
        if (selected) rowColor = MaterialColors
                .getColor(view, com.google.android.material.R.attr
                        .colorSecondary);
        else rowColor = MaterialColors
                .getColor(view, com.google.android.material.R.attr
                        .colorOnBackground);
        view.setBackgroundColor(rowColor);
    }
}