package com.example.sigma_blue.utility;

import com.example.sigma_blue.entity.item.Item;

import java.util.Objects;

public class MakeFilterField extends FilterField<Item>{
    public MakeFilterField(String filterText) {
        super(filterText);
    }

    @Override
    public boolean match(Item entity) {
        if (enabled) {
            return Objects.equals(entity.getMake(), this.getFilterText());
        } else {
            return true;
        }
    }
}