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
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.Desktop;
import java.awt.font.TextAttribute;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;

//import com.fasterxml.jackson.*;
//import com.fasterxml.jackson.map.JsonMappingException;
//import com.fasterxml.jackson.map.ObjectMapper;

import static javafx.stage.Modality.APPLICATION_MODAL;

public class MainPage extends Application {

    static TableView<Item> table = new TableView<Item>();
    static String urll =
//        "jdbc:mysql://DESKTOP-E5VI6AD:3306/products";
//        "";
        "jdbc:mysql://localhost:3306/products";
    //jdbc:mysql://DESKTOP-E5VI6AD:3306/?user=newuser
    static String user =
        "admin";
//        "";
    static String passw =
//        "658etra";
//        "";
    "newpass";
    static String bartendPath = null;
    static String imagesPath =
//        "\\\\DESKTOP-E5VI6AD\\application\\Images";
            "D:\\Mine stuff\\Compiling\\Images";

    public MainPage(String urll, String user, String passw) {
        this.urll = urll;
        this.user = user;
        this.passw = passw;
    }
    public MainPage() {}

    @Override
    public void start(Stage primaryStage) {
//        try {
//            Class.forName("com.mysql.cj.jdbc");
////            Class.forName("com.mysql.cj.jdbc.com.mysql.cj.jdbc.Driver");
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
        try {
            Class.forName("org.apache.poi.ss.usermodel.Cell");
//            Class.forName("com.mysql.cj.jdbc.com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("Current dir "+System.getProperty("user.dir"));

        //read path to bartend.exe

        try {
            File fl = new File("C:\\Program Files Intent\\configg.csv");

            Scanner myReader = new Scanner(fl);
            myReader.nextLine();
            String printEnabledStr = myReader.nextLine().split(",")[1].strip();
            boolean printEnabled = printEnabledStr.equalsIgnoreCase("true");
            String nextLi = myReader.nextLine();
            if (printEnabled){
                bartendPath = nextLi.split(",")[1].strip();
            }
            String imgPathStr = myReader.nextLine().split(",")[1].strip();
            boolean imgPath = imgPathStr.equalsIgnoreCase("true");
            nextLi = myReader.nextLine();
            if (printEnabled){
                imagesPath = nextLi.split(",")[1].strip();
            }

            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error reading configuration file");
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Config file error");
//            alert.setHeaderText("Look, a Confirmation Dialog");
            alert.setContentText("An error reading configuration file at \"C:\\Program Files Intent\\configg.csv\" occurred.");
            alert.showAndWait();
        }
        System.out.println("path to bartend "+bartendPath);

        String version = "version ";
        try {
//            File fl = new File("C:\\Program Files Intent\\Intent Database 1.0.0\\intent.jar");
//            String datime = Files.readAttributes(Paths.get("C:\\Program Files Intent\\Intent Database 1.0.0\\intent.jar"),
//                BasicFileAttributes.class).lastModifiedTime();


            File file = new File("C:\\Program Files Intent\\Intent Database 1.0.0\\intent.jar");
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy-HH:mm");
            String datime = sdf.format(file.lastModified());
            System.out.println("Date Modified Format : " + datime);
            version += datime;
        } catch (Exception e) {
            version += "---";
            e.printStackTrace();
        }

        table = new TableView<Item>();

        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(8));

        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        TableColumn[] tablArr = updateTable();
        BorderPane.setMargin(table,new Insets(14,9,12,0));

        table.getColumns()
            .addAll(tablArr[0], tablArr[1], tablArr[2], tablArr[3], tablArr[4], tablArr[5], tablArr[7], tablArr[9],
                tablArr[8], tablArr[6],tablArr[11], tablArr[10],tablArr[12]);
        table.setOnMouseClicked((MouseEvent event) -> {
            System.out.println("smth pressed");
//            System.out.println();
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2){
                System.out.println(table.getSelectionModel().getSelectedItem());
                if (table.getSelectionModel().getSelectedItem()!= null){
                    new AddItem("previewing", table.getSelectionModel().getSelectedItem(), user).start(primaryStage);
                }
            }
        });
        pane.setCenter(table);

        Label versionLbl = new Label(version);
        HBox bottomHbox = new HBox(5, versionLbl);
        bottomHbox.setAlignment(Pos.BOTTOM_RIGHT);
        HBox.setMargin(versionLbl, new Insets(0,130,0,0));
        pane.setBottom(bottomHbox);

        /** admin block */

        Label adminToolsLbl = new Label("Admin Tools");

        ImageView infoPic = new ImageView("file:C:\\Program Files Intent\\Intent Database 1.0.0\\img\\info_icon.png");
        infoPic.setFitHeight(15);
        infoPic.setPreserveRatio(true);
        BorderPane infoWrap = new BorderPane(infoPic);
        HBox.setMargin(infoWrap, new Insets(3,0,0,0));
        infoWrap.setOnMouseEntered(e->{
            infoPic.setCursor(Cursor.HAND);
        });
        infoWrap.setOnMouseClicked(e->{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information for importing");
//            alert.setHeaderText("Look, a Confirmation Dialog");
            alert.setContentText("The columns in the imported xlsx file should be named \"SKU\", \"SN\", \"PN\", \"UPC\", \"Grade\", \"Location\", \"PO#\", \"Specs\", \"Notes\", \"User\", \"Date Created\", \"Date Modified\", \"History\".");

            alert.showAndWait();
        });

        Button importBtn = new Button("Import ");
        FileChooser importFileChooser = new FileChooser();
        importBtn.setOnAction(e->{

            new ImportPreviewWindow().importBtnAction(importFileChooser, user, primaryStage);

        });

        Button exportBtn = new Button("Export");
        exportBtn.setOnAction(e->{
            DirectoryChooser exportDirChooser = new DirectoryChooser();
            exportDirChooser.setTitle("Choose the destination for the export");
//            exportDirChooser.setInitialDirectory(
//                new File("D:\\Download")
//            );

            File exportFile = exportDirChooser.showDialog(primaryStage);
            if (exportFile == null){
                return;
            }
            System.out.println(exportFile.getAbsolutePath().toString());

            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet spreadsheet = workbook.createSheet("Intent Data");
            XSSFRow row = spreadsheet.createRow(0);

            try {
                Connection conn = DriverManager.getConnection(MainPage.urll, MainPage.user, MainPage.passw);
                Statement stmt = conn.createStatement();

                ResultSet rs = stmt.executeQuery("SELECT * FROM items");
                ResultSetMetaData metadata = rs.getMetaData();

                int columnCount = metadata.getColumnCount();

                System.out.println("test_table columns : ");

                for (int i=0; i<columnCount; i++) {

                    String columnName = metadata.getColumnName(i+1);
                    switch (columnName){
                    case "DateTime":
                        columnName="Date Created";
                        break;
                    case "DateModified":
                        columnName="Date Modified";
                        break;
                    case "OtherRecords":
                        columnName="History";
                        break;
                    case "POnumber":
                        columnName="PO#";
                        break;
                    }
                    System.out.println(columnName);

                    row.createCell(i).setCellValue(columnName);
                }

                int rowCounter = 1;

                while(rs.next()){
                    row = spreadsheet.createRow(rowCounter);
                    for (int i = 0; i<columnCount; i++){
                        switch (i){
                        case 0:
                            row.createCell(i).setCellValue(rs.getLong(0+1));
                            break;
                        case 8:

                        case 11:
                            Cell timecell = row.createCell(i);
                            CellStyle timestyle = workbook.createCellStyle();
                            timestyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("d/m/yy h:mm"));
                            timecell.setCellStyle(timestyle);

                            if (rs.getTimestamp(i+1) != null) {
                                timecell.setCellValue(new Date(rs.getTimestamp(i + 1).getTime()));
                            }
                            break;
                        default:
                            row.createCell(i).setCellValue(rs.getString(i+1));
                            break;
                        }
                    }
                    rowCounter++;
                }

                String fullpath = exportFile.getAbsolutePath().toString() + "\\Database_export.xlsx";
                FileOutputStream out = new FileOutputStream(new File(fullpath));

                workbook.write(out);
                out.close();
                workbook.close();

                stmt.close();
                conn.close();
                rs.close();


                File file = new File (exportFile.getAbsolutePath() + "\\Database_export.xlsx");
                if (file.exists()){
                    try {
                        Desktop desktop = Desktop.getDesktop();
                        desktop.open(file);
                    } catch (IOException exxxx) {
                        exxxx.printStackTrace();
                    }
                } else{
//                    Alert alert = new Alert(Alert.AlertType.ERROR);
//                    alert.setTitle("Folder unavailable");
////            alert.setHeaderText("Look, a Confirmation Dialog");
//                    alert.setContentText("Folder "+initDirectory+" is unavailable");
//
//                    alert.showAndWait();
                }

//                Alert alert = new Alert(Alert.AlertType.INFORMATION);
//                alert.setTitle("Export Successful!");
////            alert.setHeaderText("Look, a Confirmation Dialog");
//                alert.setContentText("Successfully exported to\n"+fullpath);
//
//                alert.showAndWait();
            } catch (SQLException ex) {
                MainPage.databaseErrorAlert(ex).showAndWait();
                ex.printStackTrace();
            } catch (IOException iox){
                MainPage.ioErrorAlert(iox).showAndWait();
                iox.printStackTrace();
            }
        });

        Button manageUsersBtn = new Button("Manage Users");
        manageUsersBtn.setOnAction(e -> {
            new ManageUsers().start(primaryStage);
        });

        HBox adminOptHbox = new HBox(15, importBtn, infoWrap, exportBtn, manageUsersBtn);

        VBox adminOptVbox = new VBox(3,adminToolsLbl, adminOptHbox);
//        adminOptHbox.setPrefWidth(Double.MAX_VALUE);
//        adminOptHbox.setAlignment(Pos.CENTER_LEFT);
        adminOptVbox.setPadding(new Insets(5));
        HBox.setMargin(adminOptVbox, new Insets(0, 50,0,0));
        adminOptVbox.setBorder(new Border(new BorderStroke(Color.web("#000000"), BorderStrokeStyle.SOLID, new CornerRadii(3),
            new BorderWidths(0.5))));
        if (!user.equals("admin")){
            adminOptVbox.setVisible(false);
        }


        /** profile */

        Label loggedInLbl = new Label("logged in as "+user.toUpperCase());
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
        ImageView profilePic = new ImageView("file:C:\\Program Files Intent\\Intent Database 1.0.0\\img\\profile_icon.png");
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
        searchField.setPrefWidth(300);
        searchField.setMinHeight(30);
        Button searchSubm = new Button("Search");
        searchSubm.setMinHeight(30);
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
        Button addNew = new Button("Add new Item");
        addNew.setMinSize(120,45);
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
        pane.setRight(new VBox(8,refresh, addNew));
        pane.setLeft(delete);


//        primaryStage.setScene(new Scene(root));
//        primaryStage.show();
        primaryStage.getIcons().add(new Image("file:C:\\Program Files Intent\\Intent Database 1.0.0\\img\\intent_logo.png"));
        Scene scene = new Scene(pane, 950, 700);
        primaryStage.setTitle("Intent - database tool");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    public static Item[] extractItemsFromDb(String query) {
        System.out.println("*** extracting   " + query.replace("\n", "\t\t") + " ***");

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
            MainPage.databaseErrorAlert(e).showAndWait();
        }


        ArrayList<Item> outList = new ArrayList<>();
        try {
            while (rs.next()) {
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
            MainPage.databaseErrorAlert(e).showAndWait();
        }
        try {rs.close();} catch (SQLException e) {e.printStackTrace();}
        try {conn.close();} catch (SQLException e) {e.printStackTrace();}
        try {stmt.close();} catch (SQLException e) {e.printStackTrace();}

        Item[] out = outList.toArray(new Item[outList.size()]);
        return out;
    }

    public static Item[] convResultSetToItem(ResultSet rs) {
        ArrayList<Item> outList = new ArrayList<>();
        try {
            while (rs.next()) {

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
            MainPage.databaseErrorAlert(e).showAndWait();
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
        TableColumn POnumCol = new TableColumn("PO#");
        TableColumn specsCol = new TableColumn("Specs");
        TableColumn othrecCol = new TableColumn("History");

        Item[] arr = extractItemsFromDb(
            "SELECT * FROM items" + "\n" + "ORDER BY CASE WHEN DateModified IS NULL THEN DateTime ELSE DateModified END DESC"
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
        othrecCol.setCellValueFactory(new PropertyValueFactory<Item, String>("OtherRecords"));
        table.setItems(data);

//        table.getColumns().addAll(SKUCol,SNCol,PNCol,UPCCol,isPerfCol, imgCol);
        return new TableColumn[] {SKUCol, SNCol, PNCol, UPCCol, gradeCol, locCol, notesCol, userCol, timeCol, dateModifiedCol, POnumCol, specsCol, othrecCol};
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
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Deletion Confirmation");
//            alert.setHeaderText("Look, a Confirmation Dialog");
            alert.setContentText("Are you sure you want to delete item" +(itemsSelect.size() == 1 ? " " : "s ") +itemsStr+ "?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                try {
                    Connection conn = DriverManager.getConnection(urll,user,passw);
                    Statement stmt = conn.createStatement();
                    String failed = "";

                    for (Item ite: itemsSelect){
                        if (!user.equals("admin") && !user.equals(ite.User)){
                            failed += ite.SKU + ", ";
                            continue;
                        }
                        System.out.println("*** deleting item SKU "+ ite.SKU+" *** - " +"DELETE from items WHERE SKU=" + ite.SKU);
                        stmt.executeUpdate("DELETE from items WHERE SKU=" + ite.SKU);
                    }
                    conn.close();
                    stmt.close();
                    updateTable();
                    if (failed.equals("")){
                        return;
                    } else{
                        Alert alert2 = new Alert(Alert.AlertType.INFORMATION, "Enter a 5-letter word maybe? LoL");
                        alert2.setTitle("Deletion Confirmation");
//                      alert.setHeaderText("Look, a Confirmation Dialog");
                        failed = failed.substring(0,failed.length()-2);
                        alert2.setContentText("User "+user+" is not allowed to delete elements "+ (failed.length()>12?"\n"+failed:failed)+"\ncreated by other users.");
                        alert2.showAndWait();
                    }
                } catch (SQLException ex) {
                    MainPage.databaseErrorAlert(ex).showAndWait();
                }

            }
        }

    }
    public static Alert databaseErrorAlert(SQLException e){
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Database Error");
//                    alert2.setHeaderText("Look, a Confirmation Dialog");
        alert.setContentText(e.getMessage());

        return alert;
    }
    public static Alert ioErrorAlert(IOException e){
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("IO File Error");
        alert.setContentText(e.getMessage());
        return alert;
    }
}



