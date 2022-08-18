import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

public class Additional {
    public static String url = "jdbc:sqlite:D:/Mine stuff/CS python folder/JAVA/spring/src/GENERAL/products.db";


    /** a method to standartize time in the db, for proper sorting */


    public static void standartizeTime(){
        try {
            Connection conn = DriverManager.getConnection(MainPage.urll,"root","intentgroup");
            Statement stmt = conn.createStatement();
            int row_count = stmt.executeQuery("SELECT COUNT(*) from "+MainPage.schema+".items").getInt(1);
//            ResultSet rs = stmt.executeQuery(query);
//            out = new Item[row_count];
//            rs.next();
            for (int i = 0; i < row_count; i++){

                Timestamp timest = stmt.executeQuery("SELECT DateTime from "+MainPage.schema+".items WHERE rowid=" + (i+1)).getTimestamp(1);
                System.out.println(timest);
                System.out.println("SELECT DateTime from "+MainPage.schema+".items WHERE rowid=" + (i+1) +"\n"+
                    "UPDATE "+MainPage.schema+".items SET DateTime='" + (timest == null ? "" : timest) + "' WHERE rowid=" + (i+1)
                );
                if (timest!= null){
                    stmt.executeUpdate("UPDATE "+MainPage.schema+".items SET DateTime='" + timest + "' WHERE rowid=" + (i+1));
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
            int row_count = stmt.executeQuery("SELECT COUNT(*) from "+MainPage.schema+".items").getInt(1);
//            ResultSet rs = stmt.executeQuery(query);
//            out = new Item[row_count];
//            rs.next();

            for (int i = 0; i < row_count; i++){

//                String timestStr = stmt.executeQuery("SELECT DateTime from items WHERE rowid=" + (i+1)).getString(1);
//                Timestamp timest = Timestamp.valueOf(timestStr);
                Timestamp timest;
                try {
                    timest =
                        stmt.executeQuery("SELECT DateTime from "+MainPage.schema+".items WHERE rowid=" + (i + 1)).getTimestamp(1);

                    if (timest == null){
                        stmt.executeUpdate("UPDATE "+MainPage.schema+".items SET DateTime='' WHERE rowid=" + (i+1));
                    }

                } catch (SQLException e) {
                    System.out.println("timest is not null and equal to an empty str, lol");
                    System.out.println(e.getMessage());
                }

                String loc = stmt.executeQuery("SELECT Location from "+MainPage.schema+".items WHERE rowid=" + (i+1)).getString(1);
                String img = stmt.executeQuery("SELECT Images from "+MainPage.schema+".items WHERE rowid=" + (i+1)).getString(1);
                String othrec = stmt.executeQuery("SELECT OtherRecords from "+MainPage.schema+".items WHERE rowid=" + (i+1)).getString(1);



                System.out.println(img + othrec);
                System.out.println(String.valueOf(img==null) + String.valueOf(othrec==null));
//                System.out.println("SELECT DateTime from items WHERE rowid=" + (i+1) +"\n"+
//                    "UPDATE items SET DateTime='" + (timest == null ? "" : timest) + "' WHERE rowid=" + (i+1)
//                );
//                if (timest!= null){
//                    stmt.executeUpdate("UPDATE items SET DateTime='" + timest + "' WHERE rowid=" + (i+1));
//                }


                if (loc == null){
                    stmt.executeUpdate("UPDATE "+MainPage.schema+".items SET Location='' WHERE rowid=" + (i+1));
                }
                if (img == null){
                    stmt.executeUpdate("UPDATE "+MainPage.schema+".items SET Images='' WHERE rowid=" + (i+1));
                }
                if (othrec == null){
                    stmt.executeUpdate("UPDATE "+MainPage.schema+".items SET OtherRecords='' WHERE rowid=" + (i+1));
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
            int row_count = stmt.executeQuery("SELECT COUNT(*) from "+MainPage.schema+".items").getInt(1);
//            ResultSet rs = stmt.executeQuery(query);
//            out = new Item[row_count];
//            rs.next();

            String sqlStatement = "";
            ResultSet rss = stmt.executeQuery("SELECT * FROM "+MainPage.schema+".items ");
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

    public static void main(String[] args) {
        try {
            String newL = "location8";

            String timm = new Timestamp(System.currentTimeMillis()).toString();
            if (timm.split("\\.").length>0){
                timm = timm.split("\\.")[0];
            }
            Timestamp timmst = new Timestamp(System.currentTimeMillis());

            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/products2", "admin", "newpass");
            Statement stmt = conn.createStatement();

            Item[] arr = extractItemsFromDb(
                "SELECT * FROM " + "products2" + ".items"
//                    + "\n" +
//                    "ORDER BY CASE WHEN DateModified IS NULL THEN DateTime ELSE DateModified END DESC"
            );

            long start = System.currentTimeMillis();

            int count = 0;



            conn.setAutoCommit(false);

//            ResultSet rs = stmt.executeQuery("SELECT SKU, OtherRecords, Location FROM products2.items");
            for (Item it: arr){
                String otherRecs = "user on " + timm+ " : " + "updated Location from \""+(it.Location==null?"":it.Location)+"\" to \"" + newL + "\"" + ";<<<:::===" + (it.OtherRecords == null ? "" : it.OtherRecords );;
                String sqlll = "UPDATE products2.items SET Location='"+newL+"', OtherRecords='" + otherRecs + "', " +
                    "DateModified='" + timmst + "' WHERE SKU="+it.SKU;

                //sqlll updates history and item
//                System.out.println(sqlll);
                stmt.addBatch(new String(sqlll));
                count++;
            }
            System.out.println(stmt.executeBatch());

            conn.commit();
            conn.setAutoCommit(true);

            System.out.println(newL+" - "+count+" items; execution time: "+(System.currentTimeMillis()-start));
            stmt.close();
            conn.close();


        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static Item[] extractItemsFromDb(String query) {
        System.out.println("\t SQL extracting  " + query.replace("\n", "\t"));

        /** just copy of convResultSetToItem(getResultSet(query)) but closing connection */

        ResultSet rs = null;
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/products2", "admin", "newpass");
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
        } catch (SQLException e) {
            MainPage.databaseErrorAlert(e).showAndWait();
        }


        ArrayList<Item> outList = new ArrayList<>();
        try {
            while (rs.next()) {
                long st1 = rs.getInt(1);
                String st2 = rs.getString(2);
                String st3 = rs.getString(3);
                String st4 = rs.getString(4);
                String st5 = rs.getString(5);
                String st6 = rs.getString(6);
                String st7 = rs.getString(7);
                String st8 = rs.getString(8);
                Timestamp st9 = rs.getTimestamp(9);
                String st10 = rs.getString(10);
                String st11 = rs.getString(11);
                Timestamp st12 = rs.getTimestamp(12);
                String st13 = rs.getString(13);
                String st14 = rs.getString(14);
                outList.add(new Item(
                    rs.getInt(1),
                    (st2 == "" ? null : st2),
                    (st3 == "" ? null : st3),
                    (st4 == "" ? null : st4),
                    (st5 == "" ? null : st5),
                    (st6 == "" ? null : st6),
                    (st7 == "" ? null : st7),
                    (st8 == "" ? null : st8),
                    st9,
                    (st10 == "" ? null : st10),
                    (st11 == "" ? null : st11),
                    st12,
                    (st13 == "" ? null : st13),
                    (st14 == "" ? null : st14)
                ));

//                rs.next();
            }


        } catch (SQLException e) {
            MainPage.databaseErrorAlert(e).showAndWait();
        }
        try {
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Item[] out = outList.toArray(new Item[outList.size()]);
        return out;
    }

}
