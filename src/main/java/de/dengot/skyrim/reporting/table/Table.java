package de.dengot.skyrim.reporting.table;

import java.util.LinkedList;
import java.util.List;

public class Table<T> {

    // TODO add default values for columns and use them by creating newRow()
    private LinkedList<String> columns;

    private LinkedList<TableRow<T>> rows;

    private TableRow<T> footer;
    

    public Table() {
        this.columns = new LinkedList<String>();
        this.rows = new LinkedList<TableRow<T>>();
    }

    public List<String> getColumns() {
        return columns;
    }

    public List<TableRow<T>> getRows() {
        return rows;
    }

    public void addColumn(String column) {
        columns.addLast(column);
    }

    public void addRow(TableRow<T> row) {
        rows.addLast(row);
    }

    public TableRow<T> newRow() {
        TableRow<T> row = new TableRow<T>();
        rows.addLast(row);
        return row;
    }

    public int getColumnCount() {
        return this.columns.size();
    }

    public int getRowCount() {
        return this.rows.size();
    }

    public TableRow<T> getFooter() {
        return footer;
    }

    public TableRow<T> createFooter() {
        this.footer = new TableRow<T>();
        return this.footer;
    }
}
