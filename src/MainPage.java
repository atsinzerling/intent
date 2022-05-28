import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.font.TextAttribute;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

//import com.fasterxml.jackson.*;
//import com.fasterxml.jackson.map.JsonMappingException;
//import com.fasterxml.jackson.map.ObjectMapper;

import static javafx.stage.Modality.APPLICATION_MODAL;

public class MainPage extends Application {

    static TableView<Item> table = new TableView<Item>();
    static String urll =
//        "jdbc:mysql://DESKTOP-E5VI6AD:3306/products";
        "jdbc:mysql://localhost:3306/products";
    //jdbc:mysql://DESKTOP-E5VI6AD:3306/?user=newuser
    static String user = "admin";
    static String passw = "newpass";

    public MainPage(String urll, String user, String passw) {
        this.urll = urll;
        this.user = user;
        this.passw = passw;
    }
    public MainPage() {}

    @Override
    public void start(Stage primaryStage) {
//        Item[] lol = extractFromDb();
//        for (Item it: lol){
//            System.out.println(it);
//        }

//        Additional.setNulls();

        BorderPane pane = new BorderPane();

//        Label jordle = new Label("JORDLE...");
//        jordle.setMaxWidth(Double.MAX_VALUE);
//        jordle.setAlignment(Pos.CENTER);
//        pane.setMargin(jordle, new Insets(20, 0, 0, 0));
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        TableColumn[] tablArr = updateTable();

        table.getColumns()
            .addAll(tablArr[0], tablArr[1], tablArr[2], tablArr[3], tablArr[4], tablArr[5], tablArr[6], tablArr[7],
                tablArr[8], tablArr[9],tablArr[10], tablArr[11]);
        table.setOnMouseClicked((MouseEvent event) -> {
            System.out.println("smth pressed");
//            System.out.println();
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2){
                System.out.println(table.getSelectionModel().getSelectedItem());
                new AddItem("previewing", table.getSelectionModel().getSelectedItem(), user).start(primaryStage);
            }
        });
        pane.setCenter(table);


        /** admin block */

        Label adminToolsLbl = new Label("Admin Tools");

        Button importBtn = new Button("Import ");
        FileChooser importFileChooser = new FileChooser();
        importBtn.setOnAction(e->{
            configureImportFileChooser(importFileChooser);
            File importFile = importFileChooser.showOpenDialog(primaryStage);
//            (System.getProperty("user.dir") + "/src/data/table.xlsx")
            if (importFile == null){
                return;
            }
            try {
                XSSFSheet sheet = new XSSFWorkbook(new FileInputStream(importFile)).getSheetAt(0);;
                Iterator<Row> rowIterator = sheet.iterator();
                if (rowIterator.hasNext()) { //just the first column
                    Row firstRow = rowIterator.next();
                    int num_columns = firstRow.getLastCellNum();
                    String[] columns = new String[num_columns];
                    Iterator<Cell> cellIterator = firstRow.cellIterator();

                    HashMap<String, Integer> colsHashm = new HashMap<>();
                    colsHashm.put("SKU", null);
                    colsHashm.put("SN", null);
                    colsHashm.put("PN", null);
                    colsHashm.put("UPC", null);
                    colsHashm.put("Grade", null);
                    colsHashm.put("Location", null);
                    colsHashm.put("Notes", null);
                    colsHashm.put("User", null);
                    colsHashm.put("Images", null);
                    colsHashm.put("OtherRecords", null);
                    colsHashm.put("POnumber", null);
                    colsHashm.put("Specs", null);

                    for (int i = 0; i < num_columns; i++) {
                        String colmnString = cellIterator.next().getStringCellValue();
                        if (colsHashm.containsKey(colmnString)){
                            colsHashm.put(colmnString, i);
                        } else{
                            //columns are wrong output
                        }
                    }
                    //now need to
                    Row currRow = null;

                    ObservableList<Item> importData = FXCollections.observableArrayList();

                    while (rowIterator.hasNext()){
                        long currSKU = 0;
                        String currSN = null;
                        String currPN = null;
                        String currUPC = null;
                        String currGrade = null;
                        String currLocation = null;
                        String currNotes = null;
                        String currUser = null;
                        String currImages = null;
                        String currOtherRecords = null;
                        String currPOnumber = null;
                        String currSpecs = null;

                        currRow = rowIterator.next();
                        //now i go throw values in hashmap, and then get a cell with corresponding number; and then use
                        // algo to generate SKU, and then check by creating and deleting the item

                        if (colsHashm.get("SKU") != null){
                            Cell SKUcell = currRow.getCell(colsHashm.get("SKU"));
                            currSKU = (long) SKUcell.getNumericCellValue();
                        } else{
//                            currSKU =
                        }

                        if (colsHashm.get("SN") != null){
                            Cell SNcell = currRow.getCell(colsHashm.get("SN"));
                            currSN = (SNcell == null? null:SNcell.getStringCellValue());
                        } else{
                            currSN = null;
                        }

                        if (colsHashm.get("POnumber") != null){
                            Cell POnumcell = currRow.getCell(colsHashm.get("POnumber"));
                            currPOnumber = (POnumcell == null? null: POnumcell.getStringCellValue());
                        } else{
                            currPOnumber = null;
                        }
                        importData.add(new Item(
                            currSKU,
                            currSN,
                            currPN,
                            currUPC,
                            currGrade,
                            currLocation,
                            currNotes,
                            user,
                            new Timestamp(System.currentTimeMillis()),
                            currImages,
                            currOtherRecords,
                            new Timestamp(System.currentTimeMillis()),
                            currPOnumber,
                            currSpecs
                        ));
                    }
                    new ImportPreviewWindow(importData).start(primaryStage);

                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        Button exportBtn = new Button("Export");

        Button manageUsersBtn = new Button("Manage Users");

        HBox adminOptHbox = new HBox(15, importBtn, exportBtn, manageUsersBtn);

        VBox adminOptVbox = new VBox(3,adminToolsLbl, adminOptHbox);
//        adminOptHbox.setPrefWidth(Double.MAX_VALUE);
//        adminOptHbox.setAlignment(Pos.CENTER_LEFT);
        adminOptVbox.setPadding(new Insets(5));
        HBox.setMargin(adminOptVbox, new Insets(0, 50,0,0));
        adminOptVbox.setBorder(new Border(new BorderStroke(Color.web("#000000"), BorderStrokeStyle.SOLID, new CornerRadii(3),
            new BorderWidths(0.5))));


        /** profile */

        Label loggedInLbl = new Label("user "+user.toUpperCase());
//        loggedInLbl.setPrefHeight(30);
        loggedInLbl.setAlignment(Pos.CENTER);
        Label signOut = new Label("Sign out");
        signOut.setOnMouseEntered(e -> {
            signOut.setCursor(Cursor.HAND);
//            signOut.setFont(Font.font("Verdana", FontWeight.NORMAL, FontPosture.ITALIC, signOut.getFont().getSize()));
            signOut.setUnderline(true);
            signOut.setTextFill(Color.web("#0089CCFF"));
//            signOut.setStyle("-fx-background-color: #0089cc;");
        });
        signOut.setOnMouseExited(e -> {
//            signOut.setCursor(Cursor.HAND);
//            signOut.setFont(Font.font("Verdana", FontWeight.NORMAL, FontPosture.ITALIC, signOut.getFont().getSize()));
            signOut.setUnderline(false);
            signOut.setTextFill(Color.BLACK);
//            signOut.setStyle("-fx-background-color: #0089cc;");
        });
        signOut.setOnMouseClicked(e->{
            new LoginPage().start(primaryStage);
            primaryStage.close();
        });


        VBox.setMargin(loggedInLbl, new Insets(5,0,0,0));
        ImageView profilePic = new ImageView("file:src/profile_icon.png");
        profilePic.setFitHeight(40);
        profilePic.setPreserveRatio(true);
        VBox profileTextVbox = new VBox(0,loggedInLbl, signOut);
        profileTextVbox.setAlignment(Pos.TOP_RIGHT);
        HBox profileHbox = new HBox(10, adminOptVbox, profileTextVbox, profilePic);
        profileHbox.setAlignment(Pos.TOP_RIGHT);
        VBox.setMargin(profileHbox, new Insets(10, 15, 10 ,0));
        /////

        /** Search **/
        TextField searchField = new TextField();
        searchField.setPromptText("Search");
        Button searchSubm = new Button("Search");
        HBox searchHBox = new HBox(searchField, searchSubm);
        searchHBox.setAlignment(Pos.CENTER);

        VBox topVbox = new VBox(0,profileHbox, searchHBox);
        pane.setTop(topVbox);

        searchSubm.setOnAction(e -> {
            Search.executeSearch(searchField.getText().strip());
        });
        searchField.setOnKeyPressed((KeyEvent e) -> {
            if (e.getCode().getCode() == 10) {
                System.out.println("eter pressed");
                Search.executeSearch(searchField.getText().strip());
            }
        });

        /** Buttons **/
        Button addNew = new Button("Add Item");
        Button refresh = new Button("Refresh");
        Button delete = new Button("Delete selected items");

        addNew.setOnAction(e -> {
            new AddItem("adding", null, user).start(primaryStage);
        });
        refresh.setOnAction(e -> {
            searchField.setText("");
            updateTable();
        });
        delete.setOnAction(e -> {
            deleteAct();
        });
        table.setOnKeyPressed((KeyEvent e) -> {
            if (e.getCode() == KeyCode.DELETE) {
                System.out.println("delete pressed");
                deleteAct();
            }
        });
        pane.setRight(new VBox(refresh, addNew));
        pane.setLeft(delete);



//        primaryStage.setScene(new Scene(root));
//        primaryStage.show();
        Scene scene = new Scene(pane, 1000, 700);
        primaryStage.setTitle("Intent - database tool");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

//    public static void main(String[] args) {
//        launch(args);
//    }


//    public static Item[] readJson(){
//        JSONParser jsonParser = new JSONParser();
//
//        FileReader reader = null;
//        try {
//            reader = new FileReader("src/items.json");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        Object obj = null;
//        try {
//            obj = jsonParser.parse(reader);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        JSONArray arr = (JSONArray) obj;
//        System.out.println(arr);
//        Item[] out = new Item[arr.size()];
//        int coun = 0;
//        for (Object elm: arr){
//            JSONObject ell = (JSONObject) elm;
//            out[coun] = new Item((int)ell.get("SKU"),(String)ell.get("SN"),(String)ell.get("PN"),(String)ell.get("UPC"),true,null);
//            coun++;
//        }
//        return out;
//    }

//    public static boolean writeToJson(Item[] arr){
//        JSONArray jsonArr = new JSONArray();
//        for (Item ite: arr){
//            JSONObject jsonItem = new JSONObject();
//            jsonItem.put("SKU", ite.SKU);
//            jsonItem.put("SN", ite.SN);
//            jsonItem.put("PN", ite.PN);
//            jsonItem.put("UPC", ite.UPC);
//            jsonItem.put("isPerfect", ite.isPerfect);
//
//            JSONArray jsonImages = new JSONArray();
//            if (ite.images != null){
//                for (String img : ite.images) {
//                    jsonImages.add(img);
//                }
//            }
//            jsonItem.put("images", jsonImages);
//
//
//
//            jsonArr.add(jsonItem);
//        }
//        try (FileWriter file = new FileWriter("src/items.json")) {
//            //We can write any JSONArray or JSONObject instance to the file
////            String st = jsonArr.toString(4);
//            String stt = (new ObjectMapper()).writerWithDefaultPrettyPrinter().writeValueAsString(jsonArr);
//            file.write(stt);
//            System.out.println(stt);
//            file.flush();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return true;
//    }

    public static Item[] extractItemsFromDb(String query) {
        System.out.println("*** extracting   " + query.replace("\n", "\t\t") + " ***");

//        ArrayList<Item> outList = new ArrayList<>();
//
//
//        try {
//            Connection conn = DriverManager.getConnection(urll);
//            Statement stmt = conn.createStatement();
////            int row_count = stmt.executeQuery("SELECT COUNT(*) from items").getInt(1);
//            ResultSet rs = stmt.executeQuery(query);
////            out = new Item[row_count];
////            rs.next();
//
//
//            while (rs.next()) {
////                System.out.println(rs.getInt(1) +  "\t" +
////                    rs.getString(2) + "\t" +
////                    rs.getString(3) + "\t" +
////                    rs.getString(4) + "\t" +
////                    rs.getString(5) + "\t" +
////                    rs.getString(6) + "\t" +
////                    rs.getString(7) + "\t" +
////                    rs.getString(8) + "\t" +
////                    rs.getTimestamp(9) + "\t" +
////                    rs.getString(10) + "\t" +
////                    rs.getString(11) + "\t"
////                );
////                if (rs.getTimestamp(9) != null){
////                    System.out.println(rs.getTimestamp(9).getClass());
////                }
//                outList.add(new Item(
//                    rs.getInt(1),
//                    rs.getString(2),
//                    rs.getString(3),
//                    rs.getString(4),
//                    rs.getString(5),
//                    rs.getString(6),
//                    rs.getString(7),
//                    rs.getString(8),
//                    rs.getTimestamp(9),
//                    rs.getString(10),
//                    rs.getString(11)
//                ));
////                rs.next();
//                conn.close();
//            }
//        } catch (SQLException e) {
//            System.out.println(e.getMessage());
//        }
//        Item[] out = outList.toArray(new Item[outList.size()]);

        /** just copy of convResultSetToItem(getResultSet(query)) but closing connection */

        ResultSet rs = null;
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection(urll,user,passw);
            stmt = conn.createStatement();
//            int row_count = stmt.executeQuery("SELECT COUNT(*) from items").getInt(1);
            rs = stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }


        ArrayList<Item> outList = new ArrayList<>();
        try {
            while (rs.next()) {
//                System.out.println(rs.getInt(1) +  "\t" +
//                    rs.getString(2) + "\t" +
//                    rs.getString(3) + "\t" +
//                    rs.getString(4) + "\t" +
//                    rs.getString(5) + "\t" +
//                    rs.getString(6) + "\t" +
//                    rs.getString(7) + "\t" +
//                    rs.getString(8) + "\t" +
//                    rs.getTimestamp(9) + "\t" +
//                    rs.getString(10) + "\t" +
//                    rs.getString(11) + "\t"
//                );
//                if (rs.getTimestamp(9) != null){
//                    System.out.println(rs.getTimestamp(9).getClass());
//                }
                long st1 = rs.getInt(1);
                String st2=rs.getString(2);
                String st3=rs.getString(3);
                String st4=rs.getString(4);
                String st5=rs.getString(5);
                String st6=rs.getString(6);
                String st7=rs.getString(7);
                String st8=rs.getString(8);
                Timestamp st9=rs.getTimestamp(9);
                String st10=rs.getString(10);
                String st11=rs.getString(11);
                Timestamp st12=rs.getTimestamp(12);
                String st13=rs.getString(13);
                String st14=rs.getString(14);
                outList.add(new Item(
                    rs.getInt(1),
                    (st2==""?null:st2),
                    (st3==""?null:st3),
                    (st4==""?null:st4),
                    (st5==""?null:st5),
                    (st6==""?null:st6),
                    (st7==""?null:st7),
                    (st8==""?null:st8),
                    st9,
                    (st10==""?null:st10),
                    (st11==""?null:st11),
                    st12,
                    (st13==""?null:st13),
                    (st14==""?null:st14)
                ));

//                rs.next();
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {rs.close();} catch (SQLException e) {e.printStackTrace();}
        try {conn.close();} catch (SQLException e) {e.printStackTrace();}
        try {stmt.close();} catch (SQLException e) {e.printStackTrace();}

        Item[] out = outList.toArray(new Item[outList.size()]);
//        for (Item it: out){
//            System.out.println(it);
//        }
        return out;
    }

//    public static ResultSet getResultSet(String query) {
//        ResultSet rs = null;
//        try {
//            Connection conn = DriverManager.getConnection(urll);
//            Statement stmt = conn.createStatement();
////            int row_count = stmt.executeQuery("SELECT COUNT(*) from items").getInt(1);
//            rs = stmt.executeQuery(query);
////            conn.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return rs;
//    }

    public static Item[] convResultSetToItem(ResultSet rs) {
        ArrayList<Item> outList = new ArrayList<>();
        try {
            while (rs.next()) {
//                System.out.println(rs.getInt(1) +  "\t" +
//                    rs.getString(2) + "\t" +
//                    rs.getString(3) + "\t" +
//                    rs.getString(4) + "\t" +
//                    rs.getString(5) + "\t" +
//                    rs.getString(6) + "\t" +
//                    rs.getString(7) + "\t" +
//                    rs.getString(8) + "\t" +
//                    rs.getTimestamp(9) + "\t" +
//                    rs.getString(10) + "\t" +
//                    rs.getString(11) + "\t"
//                );
//                if (rs.getTimestamp(9) != null){
//                    System.out.println(rs.getTimestamp(9).getClass());
//                }
                outList.add(new Item(
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getString(5),
                    rs.getString(6),
                    rs.getString(7),
                    rs.getString(8),
                    rs.getTimestamp(9),
                    rs.getString(10),
                    rs.getString(11),
                    rs.getTimestamp(12),
                    rs.getString(13),
                    rs.getString(14)

                    ));
//                rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {rs.close();} catch (SQLException e) {e.printStackTrace();}
        Item[] out = outList.toArray(new Item[outList.size()]);
        return out;
    }


    public static TableColumn[] updateTable() {
//        table = new TableView<Item>();
//        table.setEditable(true);

        TableColumn SKUCol = new TableColumn("SKU");
        TableColumn SNCol = new TableColumn("SN");
        TableColumn PNCol = new TableColumn("PN");
        TableColumn UPCCol = new TableColumn("UPC");
        TableColumn gradeCol = new TableColumn("Grade");
        TableColumn locCol = new TableColumn("Location");
        TableColumn notesCol = new TableColumn("Notes");
        TableColumn userCol = new TableColumn("User");
        TableColumn timeCol = new TableColumn("Date Created");
        TableColumn dateModifiedCol = new TableColumn("Date Modified");
        TableColumn POnumCol = new TableColumn("PO number");
        TableColumn specsCol = new TableColumn("Specs");

        Item[] arr = extractItemsFromDb(
            "SELECT * FROM items" + "\n" + "ORDER BY CASE WHEN DateTime IS NULL THEN 1 ELSE 0 END, DateTime DESC"
        );
        ObservableList<Item> data = FXCollections.observableArrayList();
//        System.out.println("printing items in extracting to array, from UPDATE :");
        for (Item ite : arr) {
//            System.out.println(ite);
            data.add(ite);
        }
//        System.out.println("end");
        SKUCol.setCellValueFactory(new PropertyValueFactory<Item, Integer>("SKU"));
        SNCol.setCellValueFactory(new PropertyValueFactory<Item, String>("SN"));
        PNCol.setCellValueFactory(new PropertyValueFactory<Item, String>("PN"));
        UPCCol.setCellValueFactory(new PropertyValueFactory<Item, String>("UPC"));
        gradeCol.setCellValueFactory(new PropertyValueFactory<Item, String>("Grade"));
        locCol.setCellValueFactory(new PropertyValueFactory<Item, String>("Location"));
        notesCol.setCellValueFactory(new PropertyValueFactory<Item, String>("Notes"));
        userCol.setCellValueFactory(new PropertyValueFactory<Item, String>("User"));
        timeCol.setCellValueFactory(new PropertyValueFactory<Item, Timestamp>("time"));
        dateModifiedCol.setCellValueFactory(new PropertyValueFactory<Item, Timestamp>("DateModified"));
        POnumCol.setCellValueFactory(new PropertyValueFactory<Item, String>("POnumber"));
        specsCol.setCellValueFactory(new PropertyValueFactory<Item, String>("Specs"));
        table.setItems(data);

//        table.getColumns().addAll(SKUCol,SNCol,PNCol,UPCCol,isPerfCol, imgCol);
        return new TableColumn[] {SKUCol, SNCol, PNCol, UPCCol, gradeCol, locCol, notesCol, userCol, timeCol, dateModifiedCol, POnumCol, specsCol};
    }
    public static void deleteAct(){

        ArrayList<Item> itemsSelect = new ArrayList<>(table.getSelectionModel().getSelectedItems());
        if (itemsSelect.isEmpty()){
            return;
        }
        String itemsStr = "";
        for (Item it : itemsSelect){
            itemsStr += it.SKU + ", ";
        }
        itemsStr = itemsStr.substring(0, itemsStr.length()-2);

        if (itemsStr != "") {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Enter a 5-letter word maybe? LoL");
            alert.setTitle("Deletion Confirmation");
//            alert.setHeaderText("Look, a Confirmation Dialog");
            alert.setContentText("Are you sure you want to delete item" +(itemsSelect.size() == 1 ? " " : "s ") +itemsStr+ "?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                try {
                    Connection conn = DriverManager.getConnection(urll,user,passw);
                    Statement stmt = conn.createStatement();

                    //part before for not main functionality

//                        System.out.println("SELECT from items WHERE SKU=" + itemsSelect.get(0).SKU);
//                        ResultSet rss = stmt.executeQuery("SELECT * FROM items WHERE SKU=''");
//                        System.out.println(rss.isClosed());
//                        System.out.println((new Item(
//                            rss.getInt(1),
//                            rss.getString(2),
//                            rss.getString(3),
//                            rss.getString(4),
//                            rss.getString(5),
//                            rss.getString(6),
//                            rss.getString(7),
//                            rss.getString(8),
//                            rss.getTimestamp(9),
//                            rss.getString(10),
//                            rss.getString(11)
//                        )));


                    for (Item ite: itemsSelect){
                        System.out.println("*** deleting item SKU "+ ite.SKU+" *** - " +"DELETE from items WHERE SKU=" + ite.SKU);
                        stmt.executeUpdate("DELETE from items WHERE SKU=" + ite.SKU);
                    }
                    conn.close();
                    stmt.close();
                    updateTable();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

            }
        }

    }
    private static void configureImportFileChooser(final FileChooser fileChooser) {
        fileChooser.setTitle("Choose Pictures");
        fileChooser.setInitialDirectory(
            new File(System.getProperty("user.home"))
        );
        fileChooser.getExtensionFilters().addAll(
//            new FileChooser.ExtensionFilter("All Images", "*.*"),
            new FileChooser.ExtensionFilter("Microsoft Excel Worksheet", "*.xlsx")
//            new FileChooser.ExtensionFilter("PNG", "*.png")
        );
    }
}



