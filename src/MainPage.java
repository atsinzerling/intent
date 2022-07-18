import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

//import com.fasterxml.jackson.*;
//import com.fasterxml.jackson.map.JsonMappingException;
//import com.fasterxml.jackson.map.ObjectMapper;


public class MainPage extends Application {

    static TableView<Item> table = new TableView<Item>();
    static String urll =
//        "jdbc:mysql://DESKTOP-E5VI6AD:3306/products";
        "jdbc:mysql://localhost:3306/products";
    //jdbc:mysql://DESKTOP-E5VI6AD:3306/?user=newuser
    static String user =
        "admin";
    static String passw =
//        "658etra";
    "newpass";
    static String bartendPath = null;
    static String imagesPath =
//        "\\\\DESKTOP-E5VI6AD\\application\\Images";
            "D:\\Mine stuff\\Compiling\\Images";

    static Label importLoadLbl;
    static BorderPane loadingPane;
    static BorderPane pane;

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
            alert.setContentText(Methods.wrap("An error reading configuration file at \"C:\\Program Files Intent\\configg.csv\" occurred."));
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
            version += datime;
        } catch (Exception e) {
            version += "---";
            e.printStackTrace();
        }

        String deskto = "---";
        try {
            deskto = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        Thread.UncaughtExceptionHandler h = new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread th, Throwable ex) {
                System.out.println("\n\n\nUncaught Thread exception: \n" + ex+"\n\n\n");
            }
        };
        Thread.currentThread().setUncaughtExceptionHandler(h);

        table = new TableView<Item>();

        pane = new BorderPane();
        pane.setPadding(new Insets(8));

        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        TableColumn[] tablArr = updateTable();
        BorderPane.setMargin(table,new Insets(14,9,12,0));

        table.getColumns()
            .addAll(tablArr[13], tablArr[0], tablArr[1], tablArr[2], tablArr[3], tablArr[4], tablArr[5], tablArr[7], tablArr[9],
                tablArr[8], tablArr[6],tablArr[11], tablArr[10],tablArr[12]);
        table.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2){
//                System.out.println(table.getSelectionModel().getSelectedItem());
                if (table.getSelectionModel().getSelectedItem()!= null){
                    new AddItem("previewing", table.getSelectionModel().getSelectedItem(), user).start(primaryStage);
                }
            }
        });

        MenuItem itemCopy = new MenuItem("Copy");
        itemCopy.setOnAction(e-> {
            Methods.copyAct(new ArrayList<>(table.getSelectionModel().getSelectedItems()));
        });
        MenuItem itemDel = new MenuItem("Delete");
        itemDel.setOnAction(e-> {
            deleteAct();
        });
        ContextMenu menu = new ContextMenu();
        menu.getItems().addAll(itemCopy, itemDel);
        table.setContextMenu(menu);

        pane.setCenter(table);

        Label versionLbl = new Label(version);
        HBox bottomHbox = new HBox(5, versionLbl);
        bottomHbox.setAlignment(Pos.BOTTOM_RIGHT);
        HBox.setMargin(versionLbl, new Insets(0,130,0,0));
        pane.setBottom(bottomHbox);

        /** loading animation for import */ //creating new node

        importLoadLbl = new Label("processing import file");
        importLoadLbl.setFont(Font.font(null, 35));
        BorderPane.setAlignment(importLoadLbl, Pos.CENTER);

        loadingPane = new BorderPane(importLoadLbl);
        loadingPane.setVisible(true);

        StackPane root = new StackPane(loadingPane, pane);

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
            alert.setContentText(Methods.wrap("The columns in the imported xlsx file should be named \"SKU\", \"SN\", \"PN\", \"UPC\", \"Grade\", \"Location\", \"PO#\", \"Specs\", \"Notes\", \"User\", \"Date Created\", \"Date Modified\", \"History\"."));

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
            exportDirChooser.setTitle("Choose the destination for the export file");
//            exportDirChooser.setInitialDirectory(
//                new File("D:\\Download")
//            );

            File exportFile = exportDirChooser.showDialog(primaryStage);
            if (exportFile == null){
                return;
            }
            System.out.println(exportFile.getAbsolutePath().toString());

            SimpleDateFormat sdf = new SimpleDateFormat("MM_dd_yyyy-HH-mm");
            String dateeime = sdf.format(new Timestamp(System.currentTimeMillis()));

            String fullpath = exportFile.getAbsolutePath().toString() + "\\Intent_db_export_"+dateeime+".xlsx";

            Methods.export_func(fullpath);
            Methods.updateUserLog(user,"created new export");

            File file = new File (fullpath);
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
        String finalDeskto = deskto;
        signOut.setOnMouseClicked(e->{
            new LoginPage().start(primaryStage);
            primaryStage.close();
            Methods.updateUserLog(user,"signed out on "+ finalDeskto);
        });

        Label seeActivity = new Label("See my activity");
        seeActivity.setOnMouseEntered(e -> {
            seeActivity.setCursor(Cursor.HAND);
//            signOut.setFont(Font.font("Verdana", FontWeight.NORMAL, FontPosture.ITALIC, signOut.getFont().getSize()));
            seeActivity.setUnderline(true);
            seeActivity.setTextFill(Color.web("#0089CCFF"));
//            signOut.setStyle("-fx-background-color: #0089cc;");
        });
        seeActivity.setOnMouseExited(e -> {
//            signOut.setCursor(Cursor.HAND);
//            signOut.setFont(Font.font("Verdana", FontWeight.NORMAL, FontPosture.ITALIC, signOut.getFont().getSize()));
            seeActivity.setUnderline(false);
            seeActivity.setTextFill(Color.BLACK);
//            signOut.setStyle("-fx-background-color: #0089cc;");
        });
        seeActivity.setOnMouseClicked(e->{
            new UserActivity(user).start(primaryStage);
        });




        VBox.setMargin(loggedInLbl, new Insets(5,0,0,0));
        ImageView profilePic = new ImageView("file:C:\\Program Files Intent\\Intent Database 1.0.0\\img\\profile_icon.png");
        profilePic.setFitHeight(40);
        profilePic.setPreserveRatio(true);
        VBox profileTextVbox = new VBox(0,loggedInLbl, signOut, seeActivity);
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
        final KeyCodeCombination keyCodeCopy = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_ANY);
        table.setOnKeyPressed((KeyEvent e) -> {
            if (e.getCode() == KeyCode.DELETE) {
                deleteAct();
            } else if (keyCodeCopy.match(e)) {
                Methods.copyAct(new ArrayList<>(table.getSelectionModel().getSelectedItems()));
            }

        });
        pane.setRight(new VBox(8,refresh, addNew));
        pane.setLeft(delete);


//        primaryStage.setScene(new Scene(root));
//        primaryStage.show();


        //importLoadLbl, loadingPane, pane
//        loadingPane.toFront();
//        pane.setDisable(true);
////
//        pane.toFront();
//        pane.setDisable(false);


        primaryStage.getIcons().add(new Image("file:C:\\Program Files Intent\\Intent Database 1.0.0\\img\\intent_logo.png"));
        Scene scene = new Scene(root, 950, 700);
        primaryStage.setTitle("Intent - database tool");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();

        Methods.updateUserLog(user,"logged in on "+deskto);
        primaryStage.setOnCloseRequest(e->{
            primaryStage.close();
            Methods.updateUserLog(user,"signed out on "+finalDeskto);
        });

    }

    public static Item[] extractItemsFromDb(String query) {
        System.out.println("\t SQL extracting  " + query.replace("\n", "\t"));

        /** just copy of convResultSetToItem(getResultSet(query)) but closing connection */

        ResultSet rs = null;
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection(urll,user,passw);
            stmt = conn.createStatement();
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
        TableColumn numCol = new TableColumn("#");

        othrecCol.setPrefWidth(500);
        notesCol.setPrefWidth(250);
        numCol.setPrefWidth(40);

//        othrecCol.setStyle( "-fx-alignment: CENTER-LEFT;");

        Item[] arr = extractItemsFromDb(
            "SELECT * FROM products.items" + "\n" + "ORDER BY CASE WHEN DateModified IS NULL THEN DateTime ELSE DateModified END DESC"
        );
        ObservableList<Item> data = FXCollections.observableArrayList();
        for (Item ite : arr) {
            data.add(ite);
        }

        AtomicInteger rowNum = new AtomicInteger(1);

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

        numCol.setCellFactory(new LineNumbersCellFactory<>());

        table.setItems(data);

//        table.getColumns().addAll(SKUCol,SNCol,PNCol,UPCCol,isPerfCol, imgCol);
        return new TableColumn[] {SKUCol, SNCol, PNCol, UPCCol, gradeCol, locCol, notesCol, userCol, timeCol, dateModifiedCol, POnumCol, specsCol, othrecCol, numCol};
    }
    public static void deleteAct(){

//        new Thread(()->{
//            Platform.runLater(() -> {
//                MainPage.importLoadLbl.setText("processing");
//                AddItem.dotAnim(MainPage.importLoadLbl);
//                MainPage.loadingPane.toFront();
//                MainPage.pane.setDisable(true);
//            });
//
//            Platform.runLater(() -> {
//                MainPage.pane.toFront();
//                MainPage.pane.setDisable(false);
//
//            });
//        }).start();




        ArrayList<Item> itemsSelect = new ArrayList<>(table.getSelectionModel().getSelectedItems());
        if (itemsSelect.isEmpty()){
            return;
        }

        MainPage.importLoadLbl.setText("processing");
        AddItem.dotAnim(MainPage.importLoadLbl);
        MainPage.loadingPane.toFront();
        MainPage.pane.setDisable(true);
        StringBuilder itemsStr = new StringBuilder("");
        int count = 0;
        for (Item it : itemsSelect){
            itemsStr.append(it.SKU + ", ");
            count++;
            if (count == 100){
                itemsStr.append("..., ");
                break;
            }
        }
        String itemsStrrr = itemsStr.toString().substring(0, itemsStr.length()-2);

        if (itemsStrrr != "") {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Deletion Confirmation");
//            alert.setHeaderText("Look, a Confirmation Dialog");
            alert.setContentText(Methods.wrap("Are you sure you want to delete " +(itemsSelect.size() == 1 ? "this item: " : "these "+itemsSelect.size()+" items: ") +itemsStrrr+ "?"));

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                new Thread(()->{

                    Platform.runLater(() -> {
                        MainPage.importLoadLbl.setText("deleting");
                        AddItem.dotAnim(MainPage.importLoadLbl);
                        MainPage.loadingPane.toFront();
                        MainPage.pane.setDisable(true);
                    });
                    int itemsDeleted = 0;


                    try {
                        Connection conn = DriverManager.getConnection(urll,user,passw);
                        Statement stmt = conn.createStatement();
                        StringBuilder failed = new StringBuilder("");


                        //automatic backup

                        SimpleDateFormat sdf = new SimpleDateFormat("MM_dd_yyyy-HH-mm");
                        String dateeime = sdf.format(new Timestamp(System.currentTimeMillis()));

                        String folderpath = System.getProperty("user.home") +"\\Downloads\\Intent_automatic_export";
                        System.out.println(folderpath);
                        try {
                            Files.createDirectories(Paths.get(folderpath));
                        } catch (IOException e) {
                            MainPage.ioErrorAlert(e).showAndWait();
                            return;
                        }
                        Methods.export_func(folderpath + "\\Intent_automatic_export_"+dateeime+".xlsx");



                        for (Item ite: itemsSelect){
                            if (!user.equals("admin") && !user.equals(ite.User)){
                                failed.append(ite.SKU + ", ");
                                continue;
                            }
                            itemsDeleted++;
                            System.out.println("\tSQL   DELETE from products.items WHERE SKU=" + ite.SKU);
                            stmt.executeUpdate("DELETE from products.items WHERE SKU=" + ite.SKU);
                        }
                        conn.close();
                        stmt.close();
                        System.out.println("deleted");


                        if (!failed.equals("")){
                            String finalFailed = failed.toString();
                            Platform.runLater(() -> {
                                Alert alert2 = new Alert(Alert.AlertType.ERROR);
                                alert2.setTitle("Delete failed");
                                //                      alert.setHeaderText("Look, a Confirmation Dialog");
                                String failedStr = finalFailed.substring(0, finalFailed.length()-2);
                                alert2.setContentText(Methods.wrap("User "+user+" is not allowed to delete elements "+
                                    failedStr +" created by other users."));
                                alert2.showAndWait();

                                MainPage.pane.toFront();
                                MainPage.pane.setDisable(false);
                            });
                        }

                    } catch (SQLException ex) {
                        MainPage.databaseErrorAlert(ex).showAndWait();
                    }
                    Methods.updateUserLog(user,"deleted "+(itemsDeleted == 1 ? "1 item" :itemsDeleted + " items"));

                    Platform.runLater(() -> {
                        updateTable();
                        MainPage.pane.toFront();
                        MainPage.pane.setDisable(false);
                    });

                }).start();

            }
            else{
                MainPage.pane.toFront();
                MainPage.pane.setDisable(false);
            }
        }

    }
    public static Alert databaseErrorAlert(SQLException e){
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Database Error");
//                    alert2.setHeaderText("Look, a Confirmation Dialog");
        alert.setContentText(Methods.wrap(e.getMessage()));

        return alert;
    }
    public static Alert ioErrorAlert(IOException e){
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("IO File Error");
        alert.setContentText(Methods.wrap(e.getMessage()));
        return alert;
    }
    public static Alert unknownErrorAlert(Exception e){
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Unknown error occured");
        alert.setContentText(Methods.wrap(e.getMessage()));
        return alert;
    }
}



