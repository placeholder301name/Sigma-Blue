package com.example.sigma_blue.entity.item;


import android.util.Log;

import com.example.sigma_blue.context.GlobalContext;
import com.example.sigma_blue.entity.tag.Tag;
import com.example.sigma_blue.database.IDatabaseItem;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Class that stores information about a single item
 */
public class Item implements Comparable<Item>, Serializable,
        IDatabaseItem<Item> {

    public static final String dbName = "NAME", dbDate = "DATE",
            dbDescription = "DESCRIPTION", dbMake = "MAKE", dbValue = "VALUE",
            dbModel = "MODEL", dbComment = "COMMENT", dbSerial = "SERIAL",
            dbTags = "TAGS";

    private String name;
    private Date date;
    private String description, make, model;
    private Double value;
    private String serialNumber, comment;
    private List<Tag> tags;
    private GlobalContext globalContext;

    /*TODO
        UNFINISHED ITEM OBJECT!!!
        decide the photograph storing method of the item
     */

    /**
     * newInstance pattern used so that construction is decoupled from outside
     * interface.
     * @param t Just the name of the object.
     * @return the constructed item.
     */
    public static Item newInstance(String t) {
        Item ret = new Item(t);

        /* Default setting */
        ret.setDate(new Date());
        ret.setComment(null);
        ret.setDescription(null);
        ret.setMake(null);
        ret.setModel(null);
        ret.setValue(0d);

        return ret;
    }

    public static Item newInstance(String t, Date date, String comment,
                                   String description, String make,
                                   String model, String serial, Double value,
                                   List<String> tags) {
        Item ret = new Item(t);

        /* Default setting */
        ret.setDate(date);
        ret.setComment(comment);
        ret.setDescription(description);
        ret.setMake(make);
        ret.setModel(model);
        ret.setSerialNumber(serial);
        ret.setValue(value);
        ret.setTags(tags.stream().map(e -> new Tag(e, 0xFFFF0000))
                .collect(Collectors.toList()));

        return ret;
    }

    public static Item newInstance(String t, Date date, String comment,
                                   String description, String make,
                                   String model, String serial, Double value) {
        Item ret = new Item(t);

        /* Default setting */
        ret.setDate(date);
        ret.setComment(comment);
        ret.setDescription(description);
        ret.setMake(make);
        ret.setModel(model);
        ret.setSerialNumber(serial);
        ret.setValue(value);

        return ret;
    }

    public static Item newInstance(final String t, final Date date,
                                   final String make, final String model,
                                   final String serial, final Double value) {
        Item ret = new Item(t);

        /* Default setting */
        ret.setDate(date);
        ret.setComment(null);
        ret.setDescription(null);
        ret.setMake(make);
        ret.setModel(model);
        ret.setSerialNumber(serial);
        ret.setValue(value);

        return ret;
    }

    /**
     * This is constructor of item object, take in required parameters only
     * @param name
     * This is name of the item
     * @param date
     * This is purchase date of the item
     * @param description
     * This is the description of the item
     * @param make
     * This is the make of the item
     * @param model
     * This is the model of the item
     * @param value
     * this is the estimated value of the item
     */
    public Item(String name, Date date, String description, String comment,
                String make, String serial, String model, Double value) {
        this.globalContext = GlobalContext.getInstance();
        this.name = name;
        this.date = date;
        this.description = description;
        this.make = make;
        this.model = model;
        this.serialNumber = serial;
        this.value = value;
        this.comment = comment;

        this.tags = new ArrayList<Tag>();
    }

    /**
     * Simpler construction, where the other parts can be included.
     * @param name is the name of the object.
     */
    public Item(String name) {
        /* Making just the most vital components */
        this.name = name;
        this.tags = new ArrayList<>();
    }

    /**
     * Constructor for an empty item
     */
    public Item()
    {
        this("",new Date(),"","","", "",
                "",0d);
    }

    /**
     * This returns the name of item
     * @return
     * Return the name
     */
    public String getName() {
        return name;
    }

    /**
     * This sets the name of item
     * @param name
     * This is a name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * This returns the purchase date of item
     * @return
     * Return the date of purchase
     */
    public Date getDate() {
        return date;
    }

    /**
     * This sets the purchase date of item
     * @param date
     * This is a purchase date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * This returns the description of item
     * @return
     * Return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * This sets the description of item
     * @param description
     * This is a description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * This returns the make of item
     * @return
     * Return the make
     */
    public String getMake() {
        return make;
    }

    /**
     * This sets the make of item
     * @param make
     * This is a make to set
     */
    public void setMake(String make) {
        this.make = make;
    }

    /**
     * This returns the model of item
     * @return
     * Return the model
     */
    public String getModel() {
        return model;
    }

    /**
     * This sets the model of item
     * @param model
     * This is a model to set
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * This returns the estimated value of item
     * @return
     * Return the value
     */
    public Double getValue() {
        return value;
    }

    public String getFormattedValue() {
        return String.format(Locale.ENGLISH, "%.2f", value);
    }

    /**
     * This sets the estimated value of item
     * @param value
     * This is a estimated to set
     */
    public void setValue(Double value) {
        this.value = value;
    }

    /**
     * This returns the serial number of item
     * @return
     * Return the serial number
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * This sets the serial number of item
     * @param serialNumber
     * This is a serial number to set
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * This returns the comment of item
     * @return
     * Return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * This sets the comment of item
     * @param comment
     * This is a comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<Tag> getTags() {
        return tags;
    }

    /**
     * Returns the list of tag names
     * @return
     */
    public ArrayList<String> getTagNames() {
        return new ArrayList<>(tags.stream().map(Tag::getDocID).collect(Collectors
                .toList()));
    }

    /**
     * Swaps the list of tags.
     * @param tags List containing tags.
     */
    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    /**
     * Adds a new tag if it doesn't already exist
     * @param tag the tag being added.
     */
    public void addTag(Tag tag) {
        if (!tags.contains(tag)) {
            this.tags.add(tag);
        }
    }

    /**
     * This sets the comment of item
     * @param tag
     * This is a tag to delete
     * @return boolean
     * if the tag is found and successfully deleted, return true
     * if the tag is not found, return false
     */
    public boolean deleteTag(Tag tag) {
        if (this.tags.contains((Object)tag)){
            this.tags.remove((Object)tag);
            return true;
        }
        return false;
    }

    /**
     * Method that checks if the Item contains a given tag
     * @param tag is the Tag object we are checking for
     * @return this.tags.contains((Object)tag) is a boolean that has value true if the Item contains the tag, false if not
     */
    public boolean hasTag(Tag tag){
        return this.tags.contains((Object)tag);
    }

    /**
     * Returns the unique DocID of the item.
     * @return String used as the database identifier.
     */
    public String getDocID() {
        String docID = "";
        if (name != null) {
            docID += name;
        }
        if (make != null) {
            docID += make;
        }
        if (model != null) {
            docID += model;
        }
        if (serialNumber != null) {
            docID += serialNumber;
        }

        return docID;
    }


    /**
     * This overrides equals method of super class
     * @param o
     * This the object for equals method
     */
    @Override
    public boolean equals (Object o) {

        // if is the object self
        if (this == o) {
            return true;
        }

        // if the object is not Item class, return false
        if (!(o instanceof Item)) {
            return false;
        }
        // two items are equals to each other if their docid is the same
        Item I = (Item) o;
        return this.getDocID().equals(I.getDocID());
    }


    /**
     * Comparable interface override. Returns -1 if lower value than the other,
     * 0 if equal, and 1 if greater than.
     * @param item the object to be compared.
     * @return the compared values
     */
    @Override
    public int compareTo(Item item) {
        return this.getName().compareTo(item.getName());
    }

    /**
     * Returns a hash code that entails uniqueness. Currently based on the name
     * of the item.
     * @return an integer that is the hashCode.
     */
    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    /**
     * Simple date format for string conversion.
     */
    public static final SimpleDateFormat simpledf =
            new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    /**
     * Function for converting Item object into HashMap, which is compatible
     * with Firestore database.
     */
    public static final Function<Item, HashMap<String, Object>> hashMapOfItem =
            item -> {
                HashMap<String, Object> ret = new HashMap<>();
                ret.put(dbName, item.getName());
                ret.put(dbDate, simpledf.format(item.getDate()));
                ret.put(dbMake, item.getMake());
                ret.put(dbModel, item.getModel());
                ret.put(dbComment, item.getComment());
                ret.put(dbDescription, item.getDescription());
                ret.put(dbSerial, item.getSerialNumber());
                ret.put(dbValue, item.getValue());
                ret.put(dbTags, item.getTagNames());
                Log.e("TAG NAMES", item.getTagNames().stream()
                        .reduce("", (acc, ele) -> acc + ele));
                return ret;
            };

    /**
     * This function is created for converting QueryDocumentSnapshot from the
     * database into an Item object.
     */
    public static final Function<QueryDocumentSnapshot, Item>
            itemOfQueryDocument = q -> {
        Item newItem;
        try {
            newItem = Item.newInstance(
                    q.getString(dbName),
                    simpledf.parse(q.getString(dbDate)),
                    q.getString(dbComment),
                    q.getString(dbDescription),
                    q.getString(dbMake),
                    q.getString(dbModel),
                    q.getString(dbSerial),
                    q.getDouble(dbValue),
                    (List<String>)q.get(dbTags)
            );
        } catch (ParseException e) {
            newItem = Item.newInstance(
                    q.getString(dbName),
                    new Date(),
                    q.getString(dbComment),
                    q.getString(dbDescription),
                    q.getString(dbMake),
                    q.getString(dbModel),
                    q.getString(dbSerial),
                    q.getDouble(dbValue),
                    (List<String>)q.get(dbTags)
            );
        }
        newItem.cleanTags();
        return newItem;
    };

    /**
     * When tags are deleted from the DB they are not removed from items.
     * This method removes any tags that are not in the tag list.
     */
    public void cleanTags() {
        ArrayList<Tag> newTags = new ArrayList<>();
        for (Tag t : this.tags) {
            if (globalContext.getTagList().containsTag(t)) {
                newTags.add(t);
            }
        }
        this.tags = newTags;
    }
}
