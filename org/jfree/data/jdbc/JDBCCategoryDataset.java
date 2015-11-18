package org.jfree.data.jdbc;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.layout.FormatLayout;
import org.jfree.ui.Align;
import org.jfree.util.AbstractObjectList;
import org.jfree.util.LineBreakIterator;
import org.jfree.util.LogTarget;

public class JDBCCategoryDataset extends DefaultCategoryDataset {
    static final long serialVersionUID = -3080395327918844965L;
    private transient Connection connection;
    private boolean transpose;

    public JDBCCategoryDataset(String url, String driverName, String user, String passwd) throws ClassNotFoundException, SQLException {
        this.transpose = true;
        Class.forName(driverName);
        this.connection = DriverManager.getConnection(url, user, passwd);
    }

    public JDBCCategoryDataset(Connection connection) {
        this.transpose = true;
        if (connection == null) {
            throw new NullPointerException("A connection must be supplied.");
        }
        this.connection = connection;
    }

    public JDBCCategoryDataset(Connection connection, String query) throws SQLException {
        this(connection);
        executeQuery(query);
    }

    public boolean getTranspose() {
        return this.transpose;
    }

    public void setTranspose(boolean transpose) {
        this.transpose = transpose;
    }

    public void executeQuery(String query) throws SQLException {
        executeQuery(this.connection, query);
    }

    public void executeQuery(Connection con, String query) throws SQLException {
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = con.createStatement();
            resultSet = statement.executeQuery(query);
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            if (columnCount < 2) {
                throw new SQLException("JDBCCategoryDataset.executeQuery() : insufficient columns returned from the database.");
            }
            int i = getRowCount();
            while (true) {
                i--;
                if (i < 0) {
                    break;
                }
                removeRow(i);
            }
            while (resultSet.next()) {
                Comparable rowKey = resultSet.getString(1);
                for (int column = 2; column <= columnCount; column++) {
                    Comparable columnKey = metaData.getColumnName(column);
                    Number value;
                    switch (metaData.getColumnType(column)) {
                        case -6:
                        case -5:
                        case LogTarget.INFO /*2*/:
                        case LogTarget.DEBUG /*3*/:
                        case Align.WEST /*4*/:
                        case Align.TOP_LEFT /*5*/:
                        case Align.SOUTH_WEST /*6*/:
                        case FormatLayout.LCBLCB /*7*/:
                        case AbstractObjectList.DEFAULT_INITIAL_CAPACITY /*8*/:
                            value = (Number) resultSet.getObject(column);
                            if (!this.transpose) {
                                setValue(value, rowKey, columnKey);
                                break;
                            } else {
                                setValue(value, columnKey, rowKey);
                                break;
                            }
                        case LineBreakIterator.DONE /*-1*/:
                        case LogTarget.WARN /*1*/:
                        case Align.FIT_HORIZONTAL /*12*/:
                            try {
                                value = Double.valueOf((String) resultSet.getObject(column));
                                if (!this.transpose) {
                                    setValue(value, rowKey, columnKey);
                                    break;
                                } else {
                                    setValue(value, columnKey, rowKey);
                                    break;
                                }
                            } catch (NumberFormatException e) {
                                break;
                            }
                        case 91:
                        case 92:
                        case 93:
                            value = new Long(((Date) resultSet.getObject(column)).getTime());
                            if (!this.transpose) {
                                setValue(value, rowKey, columnKey);
                                break;
                            } else {
                                setValue(value, columnKey, rowKey);
                                break;
                            }
                    }
                }
            }
            fireDatasetChanged();
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (Exception e2) {
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (Exception e3) {
                }
            }
        } catch (Throwable th) {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (Exception e4) {
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (Exception e5) {
                }
            }
        }
    }
}
