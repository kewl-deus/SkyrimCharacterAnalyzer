package de.dengot.skyrim.model;

import java.text.MessageFormat;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("StatisticValue")
public class StatisticValue {

    @XStreamAlias("CategoryId")
    @XStreamAsAttribute
    private int categoryId;
    
    @XStreamAlias("Name")
    @XStreamAsAttribute
	private String name;

    @XStreamAlias("Value")
    @XStreamAsAttribute
	private int value;

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
    
    @Override
    public String toString() {
        return MessageFormat.format("{0}={1}", getName(), getValue());
    }
}
