import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;

public class ImportPreviewWindow  extends Application {

    ObservableList<Item> importData;
    String errorString = "";

    public ImportPreviewWindow(ObservableList<Item> importData, String errorString) {
        this.importData = importData; this.errorString = errorString;
    }
    public ImportPreviewWindow() {}

    @Override
    public void start(Stage primaryStage) {


        TableView<Item> tablePr = new TableView<Item>();

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
        tablePr.setItems(importData);

        tablePr.getColumns().addAll(SKUCol, SNCol, PNCol, UPCCol, gradeCol, locCol, notesCol, userCol, timeCol, dateModifiedCol, POnumCol, specsCol);


        Button cancel = new Button("Cancel");
        Button addItems = new Button("Add Items");

        Label errorsLbl = new Label ("Errors:");

        TextArea errorsArea = new TextArea();
        errorsArea.setMaxWidth(Double.MAX_VALUE);
        errorsArea.setPrefRowCount(10);
        errorsArea.setEditable(false);
        errorsArea.setWrapText(true);
        errorsArea.setText(errorString);

        Label previewLbl = new Label("Preview Items that will be added:");

        VBox mainVbox = new VBox(8,previewLbl,tablePr, errorsLbl,errorsArea);
        HBox buttonsHbox = new HBox(10, cancel, addItems);

        BorderPane pane = new BorderPane();
//        pane.setTop();
        pane.setCenter(mainVbox);
        pane.setBottom(buttonsHbox);

        BorderPane.setMargin(mainVbox, new Insets(10,10,0,10));
        BorderPane.setMargin(buttonsHbox, new Insets(10));


        Scene scene = new Scene(pane, 700, 500);
        Stage importPreviewStage = new Stage();
        importPreviewStage.setTitle("Import data preview");
        importPreviewStage.setScene(scene);
        importPreviewStage.initOwner(primaryStage);
        importPreviewStage.initModality(Modality.WINDOW_MODAL);
        importPreviewStage.setResizable(true);
        importPreviewStage.show();


        cancel.setOnAction(e->{
            importPreviewStage.close();
        });
        addItems.setOnAction(e->{

            try {
                Connection conn = DriverManager.getConnection(MainPage.urll, MainPage.user, MainPage.passw);
                Statement stmt = conn.createStatement();

                int countAdded = 0;

                for (Item it: importData){
                    String values =
                        "'" + it.SKU + "'" + "," +
                            (it.SN == null ? null : "'" + it.SN + "'") + "," +
                            (it.PN == null ? null : "'" + it.PN + "'") + "," +
                            (it.UPC == null ? null : "'" + it.UPC + "'") + "," +
                            (it.Grade == null ? null : "'" + it.Grade + "'") + "," +
                            (it.Location == null ? null : "'" + it.Location + "'") + "," +
                            (it.Notes == null ? null : "'" + it.Notes + "'") + "," +
                            "'" + it.User + "'" + "," +
                            "'" + new Timestamp(System.currentTimeMillis()) + "'" + "," +
                            (it.Images == null ? null : "'" + it.Images + "'") + "," +
                            (it.OtherRecords == null ? null : "'" + it.OtherRecords + "'") + "," +
                            "'" + new Timestamp(System.currentTimeMillis()) + "'" + "," +
                            (it.POnumber == null ? null : "'" + it.POnumber + "'") + "," +
                            (it.Specs == null ? null : "'" + it.Specs + "'");
                    String sql = ("INSERT INTO items " +
                        "VALUES (" + values + ")");
                    countAdded += stmt.executeUpdate(sql);
                }

                stmt.close();
                conn.close();

                MainPage.updateTable();
                importPreviewStage.close();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Import Successful!");
//            alert.setHeaderText("Look, a Confirmation Dialog");
                alert.setContentText("Successfully added "+countAdded+" items.");

                alert.showAndWait();

            } catch (SQLException ex) {
                MainPage.databaseErrorAlert(ex).showAndWait();
            }
        });
    }

    public void importBtnAction(FileChooser importFileChooser, String user, Stage primaryStage){

        //configuring file chooser
        importFileChooser.setTitle("Choose Excel File");
        importFileChooser.setInitialDirectory(
            new File("D:\\Download")
        );
        importFileChooser.getExtensionFilters().addAll(
//            new FileChooser.ExtensionFilter("All Images", "*.*"),
            new FileChooser.ExtensionFilter("Microsoft Excel Worksheet", "*.xlsx")
//            new FileChooser.ExtensionFilter("PNG", "*.png")
        );


        File importFile = importFileChooser.showOpenDialog(primaryStage);
//            (System.getProperty("user.dir") + "/src/data/table.xlsx")
        if (importFile == null){
            return;
        }
        ObservableList<Item> importDataMethod = FXCollections.observableArrayList();
        try {
            FileInputStream fis = new FileInputStream(importFile);
            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(importFile));
            XSSFSheet sheet = workbook.getSheetAt(0);
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
                        errorString = "Column \""+colmnString+"\" is not found in the database, make sure that the name of the columns of the table include " +
                            "\"SKU\", " +
                            "\"SN\", " +
                            "\"POnumber\", " +
                            "\"Specs\".\n";
                    }
                }
                //now need to
                Row currRow = null;


                Connection conn = DriverManager.getConnection(MainPage.urll, MainPage.user, MainPage.passw);
                Statement stmt = conn.createStatement();
                int generatedSKUcount = 0;
                int rowNum = 2;

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

                    boolean needGenerating = false;
                    if (colsHashm.get("SKU") != null){
                        Cell SKUcell = currRow.getCell(colsHashm.get("SKU"));
                        if (SKUcell == null){
                            needGenerating = true;
                        } else if (SKUcell.getCellType() == CellType.NUMERIC) {
                            currSKU = (long) SKUcell.getNumericCellValue();
                        } else{
                            errorString += "Error in row "+rowNum+": couldn't retrieve SKU number from a cell;\n";
                        }
                    } else{
                        //generate SKU
//                            currSKU =
                        needGenerating = true;
                    }

                    if (needGenerating){
                        Connection conn2 = DriverManager.getConnection(MainPage.urll, MainPage.user, MainPage.passw);
                        Statement stmt2 = conn.createStatement();
                        ResultSet rs2 = stmt2.executeQuery("SELECT SKUgenerated from addit_data");
                        rs2.next();
                        long lastSKU = rs2.getLong(1);
                        currSKU = lastSKU+1;
                        System.out.println("execute update"+stmt2.executeUpdate("UPDATE addit_data SET SKUgenerated="+currSKU+" WHERE IDcolumn=1"));
                        rs2.close();
                        conn2.close();
                        stmt2.close();
                        generatedSKUcount++;
                        System.out.println("generating sku "+ currSKU);
                    }
                    System.out.println(rowNum + " "+ currSKU);

                    if (colsHashm.get("SN") != null){
                        Cell SNcell = currRow.getCell(colsHashm.get("SN"));
                        currSN = (SNcell == null? null:(readCell(SNcell).equals("")?null: readCell(SNcell)));
                    } else{
                        currSN = null;
                    }

                    if (colsHashm.get("POnumber") != null){
                        Cell POnumcell = currRow.getCell(colsHashm.get("POnumber"));
                        currPOnumber = (POnumcell == null? null: (readCell(POnumcell).equals("")?null: readCell(POnumcell)));
                    } else{
                        currPOnumber = null;
                    }

                    if (colsHashm.get("Specs") != null){
                        Cell specscell = currRow.getCell(colsHashm.get("Specs"));
                        currSpecs = (specscell == null? null: (readCell(specscell).equals("")?null: readCell(specscell)));
                    } else{
                        currSpecs = null;
                    }

                    rowNum++;
                    if (currSKU == 0) {
                        System.out.println("lol breaked");
                        continue;
                    }

                    Item currItem = new Item(
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
                    );

                    String values =
                        "'" + currItem.SKU + "'" + "," +
                            (currItem.SN == null ? null : "'" + currItem.SN + "'") + "," +
                            (currItem.PN == null ? null : "'" + currItem.PN + "'") + "," +
                            (currItem.UPC == null ? null : "'" + currItem.UPC + "'") + "," +
                            (currItem.Grade == null ? null : "'" + currItem.Grade + "'") + "," +
                            (currItem.Location == null ? null : "'" + currItem.Location + "'") + "," +
                            (currItem.Notes == null ? null : "'" + currItem.Notes + "'") + "," +
                            "'" + currItem.User + "'" + "," +
                            "'" + currItem.time + "'" + "," +
                            (currItem.Images == null ? null : "'" + currItem.Images + "'") + "," +
                            (currItem.OtherRecords == null ? null : "'" + currItem.OtherRecords + "'") + "," +
                            "'" + currItem.DateModified + "'" + "," +
                            (currItem.POnumber == null ? null : "'" + currItem.POnumber + "'") + "," +
                            (currItem.Specs == null ? null : "'" + currItem.Specs + "'");
                    String sql = ("INSERT INTO items " +
                        "VALUES (" + values + ")");
                    try {
                        int addingResult = stmt.executeUpdate(sql);
                        int deleteResult = stmt.executeUpdate("DELETE from items WHERE SKU=" + currItem.SKU);

                        importDataMethod.add(currItem);
                    } catch (SQLIntegrityConstraintViolationException ex) {
                        //possible errors: duplicate SKU, duplicate SN, other error
                        String message = ex.getMessage();
                        if (message.contains("Duplicate entry ") && message.contains(" for key 'items.PRIMARY'")) {
                            System.out.println("Duplicate entry for key 'items.PRIMARY'");
                            errorString += "Error adding item "+currItem.SKU+": Duplicate SKU = "+currItem.SKU +";\n";

                        } else if (message.contains("Duplicate entry ") && message.contains(" for key 'items.SN'")) {
                            System.out.println("Duplicate entry for key 'items.SN'");
                            errorString += "\nError adding item "+currItem.SKU+": Duplicate SN = "+currItem.SN+";\n";

                        } else{
                            errorString += "\nError adding item "+currItem.SKU+": Uncatched error;\n";
                        }
                    }

                }
                fis.close();
                workbook.close();
                stmt.close();
                conn.close();
                System.out.println("num of rows "+ rowNum);

            }
        } catch (SQLException ex) {
            errorString += "ERROR: uncatched database error;";
            MainPage.databaseErrorAlert(ex).showAndWait();
        } catch (IOException ioe){
            errorString += "ERROR: uncatched IO Files error;";
            for (int i=0; i<100; i++){
                System.out.println("ААААААААААААААААААААААААААААААААААААААААААААААААААААААААААААААААА ОШИБКА");
            }
            MainPage.ioErrorAlert(ioe).showAndWait();
            for (int i=0; i<100; i++){
                System.out.println("ААААААААААААААААААААААААААААААААААААААААААААААААААААААААААААААААА ОШИБКА");
            }
        }
        new ImportPreviewWindow(importDataMethod, errorString).start(primaryStage);

    }
    public String readCell(Cell cell){
        switch (cell.getCellType()){
        case STRING:
            return cell.getStringCellValue();
        case NUMERIC:
            return Integer.toString((int) cell.getNumericCellValue());
        default:
            return null;
        }
    }
}
