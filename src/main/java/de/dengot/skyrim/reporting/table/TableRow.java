package de.dengot.skyrim.reporting.table;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.comparators.NullComparator;

public class TableRow<T> {

    private Map<String, T> rowdata;
    private Comparator<T> comparator;

    public TableRow(Comparator<T> comparator) {
        this.rowdata = new HashMap<String, T>();
        this.comparator = comparator;
    }

    @SuppressWarnings("unchecked")
    public TableRow() {
        this(new NullComparator());
    }

    public T getCell(String column) {
        return this.rowdata.get(column);
    }

    public void setCell(String column, T value) {
        this.rowdata.put(column, value);
    }

    public T getMaxValue() {
        return Collections.max(this.rowdata.values(), this.comparator);
    }

    public boolean isMaxValue(String column) {
        T maxVal = getMaxValue();
        int occurences = getValueOccurence(maxVal);
        return occurences == 1 && maxVal.equals(getCell(column));
    }
    
    protected int getValueOccurence(T value){
        return Collections.frequency(this.rowdata.values(), value);
    }
}
