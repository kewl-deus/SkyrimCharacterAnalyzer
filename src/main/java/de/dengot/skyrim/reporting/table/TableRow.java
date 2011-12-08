package de.dengot.skyrim.reporting.table;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.comparators.NullComparator;

public class TableRow<T> {

    private Map<String, T> rowdata;
    private Comparator<T> comparator;
    private T defaulCellValue;

    public TableRow(Comparator<T> comparator) {
        this.rowdata = new HashMap<String, T>();
        this.comparator = comparator;
        this.defaulCellValue = null;
    }

    public void setDefaulCellValue(T defaulCellValue) {
        this.defaulCellValue = defaulCellValue;
    }

    public T getDefaulCellValue() {
        return defaulCellValue;
    }

    @SuppressWarnings("unchecked")
    public TableRow() {
        this(new NullComparator());
    }

    public T getCell(String column) {
        T val = this.rowdata.get(column);
        return (val == null) ? getDefaulCellValue() : val;
    }

    public void setCell(String column, T value) {
        this.rowdata.put(column, value);
    }

    public T getMaxValue() {
        T val = Collections.max(this.rowdata.values(), this.comparator);
        return (val == null) ? getDefaulCellValue() : val;
    }

    public boolean isMaxValue(String column) {
        T maxVal = getMaxValue();
        int occurences = getValueOccurence(maxVal);
        return occurences == 1 && maxVal.equals(getCell(column));
    }

    protected int getValueOccurence(T value) {
        return Collections.frequency(this.rowdata.values(), value);
    }
}
