package de.dengot.skyrim.reporting;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Table<T> {

    private LinkedList<String> columns;
    
    private LinkedList<Map<String, T>> rows;
    
    
    public Table() {
        this.columns = new LinkedList<String>();
        this.rows = new LinkedList<Map<String,T>>();
    }

    public List<String> getColumns() {
        return columns;
    }

    public List<Map<String, T>> getRows() {
        return rows;
    }

    public void addColumn(String column) {
        columns.addLast(column);
    }

    public void addRow(Map<String, T> row) {
        rows.addLast(row);
    }
    
    public Map<String, T> newRow(){
        Map<String, T> row = new HashMap<String, T>();
        rows.addLast(row);
        return row;
    }
    
    
    
}
