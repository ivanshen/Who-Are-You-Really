package org.jfree.data.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.layout.FormatLayout;
import org.jfree.ui.Align;
import org.jfree.util.AbstractObjectList;
import org.jfree.util.LogTarget;

public class JDBCPieDataset extends DefaultPieDataset {
    static final long serialVersionUID = -8753216855496746108L;
    private transient Connection connection;

    public JDBCPieDataset(String url, String driverName, String user, String password) throws SQLException, ClassNotFoundException {
        Class.forName(driverName);
        this.connection = DriverManager.getConnection(url, user, password);
    }

    public JDBCPieDataset(Connection con) {
        if (con == null) {
            throw new NullPointerException("A connection must be supplied.");
        }
        this.connection = con;
    }

    public JDBCPieDataset(Connection con, String query) throws SQLException {
        this(con);
        executeQuery(query);
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
            if (metaData.getColumnCount() != 2) {
                throw new SQLException("Invalid sql generated.  PieDataSet requires 2 columns only");
            }
            int columnType = metaData.getColumnType(2);
            while (resultSet.next()) {
                Comparable key = resultSet.getString(1);
                switch (columnType) {
                    case -5:
                    case LogTarget.INFO /*2*/:
                    case LogTarget.DEBUG /*3*/:
                    case Align.WEST /*4*/:
                    case Align.SOUTH_WEST /*6*/:
                    case FormatLayout.LCBLCB /*7*/:
                    case AbstractObjectList.DEFAULT_INITIAL_CAPACITY /*8*/:
                        setValue(key, resultSet.getDouble(2));
                        break;
                    case 91:
                    case 92:
                    case 93:
                        setValue(key, (double) resultSet.getTimestamp(2).getTime());
                        break;
                    default:
                        System.err.println("JDBCPieDataset - unknown data type");
                        break;
                }
            }
            fireDatasetChanged();
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (Exception e) {
                    System.err.println("JDBCPieDataset: swallowing exception.");
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (Exception e2) {
                    System.err.println("JDBCPieDataset: swallowing exception.");
                }
            }
        } catch (Throwable th) {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (Exception e3) {
                    System.err.println("JDBCPieDataset: swallowing exception.");
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (Exception e4) {
                    System.err.println("JDBCPieDataset: swallowing exception.");
                }
            }
        }
    }

    public void close() {
        try {
            this.connection.close();
        } catch (Exception e) {
            System.err.println("JdbcXYDataset: swallowing exception.");
        }
    }
}
