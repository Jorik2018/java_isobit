package org.isobit.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ext extends HashMap implements Serializable{

    private boolean phanton = true;
    private List selectedList;
    private List removedList;

    public Ext() {
    }

    public Ext(boolean b) {
        phanton = b;
    }

    public List getSelectedList() {
        return selectedList;
    }

    public void setSelectedList(List selectedList) {
        this.selectedList = selectedList;
    }

    public List getRemovedList() {
        return removedList;
    }

    public void setRemovedList(List removedList) {
        this.removedList = removedList;
    }

    public boolean isPhanton() {
        return phanton;
    }

    public void setPhanton(boolean phanton) {
        this.phanton = phanton;
    }

    public Map getProperties() {
        return this;
    }

}
