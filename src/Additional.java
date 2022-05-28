import com.fasterxml.jackson.core.JsonToken;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class Additional {
    public static String url = "jdbc:sqlite:D:/Mine stuff/CS python folder/JAVA/spring/src/GENERAL/products.db";


    /** a method to standartize time in the db, for proper sorting */


    public static void standartizeTime(){
        try {
            Connection conn = DriverManager.getConnection(MainPage.urll,"root","intentgroup");
            Statement stmt = conn.createStatement();
            int row_count = stmt.executeQuery("SELECT COUNT(*) from items").getInt(1);
//            ResultSet rs = stmt.executeQuery(query);
//            out = new Item[row_count];
//            rs.next();
            for (int i = 0; i < row_count; i++){

                Timestamp timest = stmt.executeQuery("SELECT DateTime from items WHERE rowid=" + (i+1)).getTimestamp(1);
                System.out.println(timest);
                System.out.println("SELECT DateTime from items WHERE rowid=" + (i+1) +"\n"+
                    "UPDATE items SET DateTime='" + (timest == null ? "" : timest) + "' WHERE rowid=" + (i+1)
                );
                if (timest!= null){
                    stmt.executeUpdate("UPDATE items SET DateTime='" + timest + "' WHERE rowid=" + (i+1));
                }
            }
            conn.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    // not sure if eliminating nulls is needed; can set all non-zero-legth strings to null, or anuthing
    // still, null is useful for timestamp values, don't run that eliminating zeros code

    public static void eliminateNulls(){
        try {
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            int row_count = stmt.executeQuery("SELECT COUNT(*) from items").getInt(1);
//            ResultSet rs = stmt.executeQuery(query);
//            out = new Item[row_count];
//            rs.next();

            for (int i = 0; i < row_count; i++){

//                String timestStr = stmt.executeQuery("SELECT DateTime from items WHERE rowid=" + (i+1)).getString(1);
//                Timestamp timest = Timestamp.valueOf(timestStr);
                Timestamp timest;
                try {
                    timest =
                        stmt.executeQuery("SELECT DateTime from items WHERE rowid=" + (i + 1)).getTimestamp(1);

                    if (timest == null){
                        stmt.executeUpdate("UPDATE items SET DateTime='' WHERE rowid=" + (i+1));
                    }

                } catch (SQLException e) {
                    System.out.println("timest is not null and equal to an empty str, lol");
                    System.out.println(e.getMessage());
                }

                String loc = stmt.executeQuery("SELECT Location from items WHERE rowid=" + (i+1)).getString(1);
                String img = stmt.executeQuery("SELECT Images from items WHERE rowid=" + (i+1)).getString(1);
                String othrec = stmt.executeQuery("SELECT OtherRecords from items WHERE rowid=" + (i+1)).getString(1);



                System.out.println(img + othrec);
                System.out.println(String.valueOf(img==null) + String.valueOf(othrec==null));
//                System.out.println("SELECT DateTime from items WHERE rowid=" + (i+1) +"\n"+
//                    "UPDATE items SET DateTime='" + (timest == null ? "" : timest) + "' WHERE rowid=" + (i+1)
//                );
//                if (timest!= null){
//                    stmt.executeUpdate("UPDATE items SET DateTime='" + timest + "' WHERE rowid=" + (i+1));
//                }


                if (loc == null){
                    stmt.executeUpdate("UPDATE items SET Location='' WHERE rowid=" + (i+1));
                }
                if (img == null){
                    stmt.executeUpdate("UPDATE items SET Images='' WHERE rowid=" + (i+1));
                }
                if (othrec == null){
                    stmt.executeUpdate("UPDATE items SET OtherRecords='' WHERE rowid=" + (i+1));
                }

            }
            conn.close();

        } catch (SQLException e) {
            System.out.println("error nullstuff");
            System.out.println(e.getMessage());
        }
    }
    public static void setNulls(){ //except for timestamp so far
        try {
            Connection conn = DriverManager.getConnection(MainPage.urll,"root","intentgroup");
            Statement stmt = conn.createStatement();
            int row_count = stmt.executeQuery("SELECT COUNT(*) from items").getInt(1);
//            ResultSet rs = stmt.executeQuery(query);
//            out = new Item[row_count];
//            rs.next();

            String sqlStatement = "";
            ResultSet rss = stmt.executeQuery("SELECT * FROM items ");
            rss.next();
            for (int i = 0; i < row_count; i++){


//                Timestamp timest;
//                try {
//                    timest =
//                        stmt.executeQuery("SELECT DateTime from items WHERE rowid=" + (i + 1)).getTimestamp(1);
//
//                    if (timest == null){
//                        stmt.executeUpdate("UPDATE items SET DateTime='' WHERE rowid=" + (i+1));
//                    }
//
//                } catch (SQLException e) {
//                    System.out.println("timest is not null and equal to an empty str, lol");
//                    System.out.println(e.getMessage());
//                }

//                String loc = stmt.executeQuery("SELECT Location from items WHERE rowid=" + (i+1)).getString(1);
//                String img = stmt.executeQuery("SELECT Images from items WHERE rowid=" + (i+1)).getString(1);
//                String othrec = stmt.executeQuery("SELECT OtherRecords from items WHERE rowid=" + (i+1)).getString(1);


//                System.out.println("SELECT * FROM items WHERE rowid=" + (i+1));
//                ResultSet rss = stmt.executeQuery("SELECT * FROM items WHERE rowid=" + (i+1));
//                System.out.println(rss.isClosed());
                Item it = new Item(
                    rss.getLong(1),
                    rss.getString(2),
                    rss.getString(3),
                    rss.getString(4),
                    rss.getString(5),
                    rss.getString(6),
                    rss.getString(7),
                    rss.getString(8),
                    rss.getTimestamp(9),
                    rss.getString(10),
                    rss.getString(11),
                    rss.getTimestamp(12),
                    rss.getString(13),
                    rss.getString(14)
                );
                rss.next();
//                System.out.println(it);

//                System.out.println(img + othrec);
//                System.out.println(String.valueOf(img==null) + String.valueOf(othrec==null));


//                if (loc == null){
//                    stmt.executeUpdate("UPDATE items SET Location='' WHERE rowid=" + (i+1));
//                }
//                if (img == null){
//                    stmt.executeUpdate("UPDATE items SET Images='' WHERE rowid=" + (i+1));
//                }
//                if (othrec == null){
//                    stmt.executeUpdate("UPDATE items SET OtherRecords='' WHERE rowid=" + (i+1));
//                }
                System.out.println(it);
                String sql = "UPDATE items SET " +
                    (it.SN!= null && it.SN.equals("") ? "SN=NULL, " : "") +
                    (it.PN!= null && it.PN.equals("") ? "PN=NULL, " : "") +
                    (it.UPC!= null && it.UPC.equals("") ? "UPC=NULL, " : "") +
                    (it.Grade!= null && it.Grade.equals("") ? "Grade=NULL, " : "") +
                    (it.Location!= null && it.Location.equals("") ? "Location=NULL, " : "") +
                    (it.Notes!= null && it.Notes.equals("") ? "Notes=NULL, " : "") +
                    (it.User!= null && it.User.equals("") ? "User=NULL, " : "") +
                    (it.Images!= null && it.Images.equals("") ? "Images=NULL, " : "") +
                    (it.OtherRecords!= null && it.OtherRecords.equals("") ? "OtherRecords=NULL " : "") +
                    "WHERE SKU=" + it.SKU;
                if (sql.contains(", WHERE")){
                    sql = sql.replace(", WHERE", " WHERE");
                }
                if (!sql.contains("UPDATE items SET WHERE SKU=")){
                    System.out.println(sql);
                    sqlStatement +=sql+";\n";
//                    stmt.executeUpdate(sql);
                }
            }
            rss.close();
//            System.out.println(sqlStatement);
            if (!sqlStatement.contains("UPDATE items SET WHERE rowid=")){
                for (String st: sqlStatement.split(";\n")){
                    stmt.addBatch(st);
                }

//                String tempsql = sqlStatement.replaceAll("NULL", "'temp'");
//                sqlStatement = tempsql+sqlStatement;
                System.out.println(sqlStatement);
//                System.out.println("supports"+ conn.getMetaData().supportsBatchUpdates());
//                stmt.addBatch(sqlStatement);
                stmt.executeBatch();
            }
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println("error nullstuff");
//            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

//    public static void main(String[] args) {
//        eliminateNulls();
//    }

}
