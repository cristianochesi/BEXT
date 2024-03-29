package tools;

import java.sql.*;

public class DBLayer {
  static Connection DbConn;

  private DBLayer() {
  }

  public static void connect(String serverName, String dbName, String userName, String password) {
    try {
      /**
       * Inizializza la libreria per la connessione con MySQL
       */
      Class.forName("com.mysql.cj.jdbc.Driver").newInstance();

      DbConn = DriverManager.getConnection(
          // "jdbc:mysql://localhost:3306/yourair?user=root&password=root");
          "jdbc:mysql://" + serverName + ":3306/" + dbName + "?user=" + userName + "&password=" + password);

    } catch (SQLException E) {
      System.out.println("SQLException: " + E.getMessage());
      System.out.println("SQLState: " + E.getSQLState());
      System.out.println("VendorError: " + E.getErrorCode());

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Creates a new statement to perform a query
   * 
   * @return
   */
  private static Statement createStatement() {
    try {
      return DbConn.createStatement();
    } catch (SQLException e) {
      handleSQLException(e, "");
      throw new Error("Fatal error.");
    }
  }

  /**
   * Handles a SQL exception caused by query executions
   * 
   * @param sqle
   * @param query
   */
  private static void handleSQLException(SQLException e, String Query) {
    System.out.println("\nSQL Exception:\n");
    while (e != null) {
      System.out.println("Message:   " + e.getMessage());
      System.out.println("SQLState:  " + e.getSQLState());
      System.out.println("ErrorCode: " + e.getErrorCode());
      e = e.getNextException();
      System.out.println("");
    }

    System.out.println("Query:     " + Query);
  }

  /**
   * Performs a query
   * 
   * @param query
   * @return
   */
  public static ResultSet SQL(String Query) {
    Statement S = createStatement();

    try {
      if (Query.startsWith("INSERT") || Query.startsWith("UPDATE") || Query.startsWith("DELETE")) {
        S.executeUpdate(Query);
        S.close();
        return null;
      }
      ResultSet R = S.executeQuery(Query);
      return R;
    } catch (SQLException e) {
      handleSQLException(e, Query);
      throw new Error("Fatal error.");
    }
  }

}
