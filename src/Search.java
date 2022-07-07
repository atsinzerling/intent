import com.sun.tools.javac.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;

public class Search {
    public static void executeSearch(String searchText) {
        if (searchText == "") {
            MainPage.updateTable();
            return;
        }
        TableColumn SKUCol = new TableColumn("SKU");
        TableColumn SNCol = new TableColumn("SN");
        TableColumn PNCol = new TableColumn("PN");
        TableColumn UPCCol = new TableColumn("UPC");
        TableColumn gradeCol = new TableColumn("Grade");
        TableColumn locCol = new TableColumn("Location");
        TableColumn notesCol = new TableColumn("Notes");
        TableColumn userCol = new TableColumn("User");
        TableColumn timeCol = new TableColumn("Date&Time");
        TableColumn dateModCol = new TableColumn("DateModified");
        TableColumn POnumCol = new TableColumn("PO number");
        TableColumn specsCol = new TableColumn("Specs");

        /** Getting the results */
        Item[] arr;
        try {
            arr = MainPage.extractItemsFromDb(
                    "SELECT * FROM items WHERE SKU=" + Integer.parseInt(searchText)
//                + "\n" + "ORDER BY CASE WHEN DateTime IS NULL THEN 1 ELSE 0 END, DateTime ASC"

            );
        } catch (NumberFormatException e) {
            arr = new Item[0];
        }

//        System.out.println(arr);
//        System.out.println(arr.length);
//        for (Item elm: arr){
//            System.out.println(elm);
//        }
//        System.out.println(arr.length == 0);
        ObservableList<Item> data = FXCollections.observableArrayList();

        System.out.println(arr.length);
        if (arr.length == 0) {
            arr = MainPage.extractItemsFromDb(
                    "SELECT * FROM items WHERE SN=" + "'" + searchText + "'"
//                + "\n" + "ORDER BY CASE WHEN DateTime IS NULL THEN 1 ELSE 0 END, DateTime ASC"

            );
        }
        if (arr.length == 0) {
            System.out.println("full search");

//                Statement stmt = conn.createStatement();

//                ArrayList<Item> arrList = new ArrayList<Item>(Arrays.asList(
            Item[] arrBig =
                MainPage.extractItemsFromDb(
                        "SELECT * FROM items" + "\n" +
                            "ORDER BY CASE WHEN DateTime IS NULL THEN 1 ELSE 0 END, DateTime DESC"
                    );

//                ));
            for (Item it: arrBig){
                System.out.println(it == null);
            }

            //i ll be adding elements
            for (Item ite : arrBig) {
                System.out.println("Item in SEARCH - "+ite);
                System.out.println(ite.Location == null);
                if (
                    Long.toString(ite.SKU).contains(searchText) ||
                        ite.SN != null && ite.SN.toLowerCase().contains(searchText.toLowerCase()) ||
                        ite.PN != null && ite.PN.toLowerCase().contains(searchText.toLowerCase()) ||
                        ite.UPC != null && ite.UPC.toLowerCase().contains(searchText.toLowerCase()) ||
                        ite.Grade != null && ite.Grade.toLowerCase().contains(searchText.toLowerCase()) ||
                        ite.Location != null && ite.Location.toLowerCase().contains(searchText.toLowerCase()) ||
                        ite.Notes != null && ite.Notes.toLowerCase().contains(searchText.toLowerCase()) ||
                        ite.User != null && ite.User.toLowerCase().contains(searchText.toLowerCase()) ||
                        ite.time!=null && ite.time.toString().toLowerCase().contains(searchText.toLowerCase()) ||
                        ite.DateModified!=null && ite.DateModified.toString().toLowerCase().contains(searchText.toLowerCase()) ||
                        ite.POnumber != null && ite.POnumber.toLowerCase().contains(searchText.toLowerCase())||
                        ite.Specs != null && ite.Specs.toLowerCase().contains(searchText.toLowerCase())||
                        ite.OtherRecords != null && ite.OtherRecords.toLowerCase().contains(searchText.toLowerCase())
                ) {
                    data.add(ite);
                    System.out.println("ADDED!!!!!!!!!!");
                }

                //add ite to arraylist, then convert to arr
            }


//                int row_count = stmt.executeQuery("SELECT COUNT(*) from items").getInt(1);
//
//                for (int i = 0; i < row_count; i++){
//
//                    Timestamp timest = stmt.executeQuery("SELECT DateTime from items WHERE rowid=" + (i+1)).getTimestamp(1);
//                    System.out.println(timest);
//                    System.out.println("SELECT DateTime from items WHERE rowid=" + (i+1) +"\n"+
//                        "UPDATE items SET DateTime='" + (timest == null ? "" : timest) + "' WHERE rowid=" + (i+1)
//                    );
//                    if (timest!= null){
//                        stmt.executeUpdate("UPDATE items SET DateTime='" + timest + "' WHERE rowid=" + (i+1));
//                    }
//                }


        }

//        System.out.println(arr);
//        System.out.println(arr.length);
//        for (Item elm : arr) {
//            System.out.println(elm);
//        }


        /** updating table */

        if (data.isEmpty()) {
            for (Item ite : arr) {
                data.add(ite);
//                System.out.println(ite);
            }
        }

        SKUCol.setCellValueFactory(new PropertyValueFactory<Item, Integer>("SKU"));
        SNCol.setCellValueFactory(new PropertyValueFactory<Item, String>("SN"));
        PNCol.setCellValueFactory(new PropertyValueFactory<Item, String>("PN"));
        UPCCol.setCellValueFactory(new PropertyValueFactory<Item, String>("UPC"));
        gradeCol.setCellValueFactory(new PropertyValueFactory<Item, String>("Grade"));
        locCol.setCellValueFactory(new PropertyValueFactory<Item, String>("Location"));
        notesCol.setCellValueFactory(new PropertyValueFactory<Item, String>("Notes"));
        userCol.setCellValueFactory(new PropertyValueFactory<Item, String>("User"));
        timeCol.setCellValueFactory(new PropertyValueFactory<Item, Timestamp>("time"));
        dateModCol.setCellValueFactory(new PropertyValueFactory<Item, Timestamp>("DateModified"));
        specsCol.setCellValueFactory(new PropertyValueFactory<Item, String>("Specs"));
        POnumCol.setCellValueFactory(new PropertyValueFactory<Item, String>("POnumber"));
        MainPage.table.setItems(data);
    }
}
