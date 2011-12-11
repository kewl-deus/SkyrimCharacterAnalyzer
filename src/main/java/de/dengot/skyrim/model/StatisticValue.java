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

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return MessageFormat.format("{0}={1}", getName(), getValue());
    }
}
