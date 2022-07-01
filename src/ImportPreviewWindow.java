import javafx.application.Application;
import javafx.application.Platform;
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
import org.apache.poi.ss.usermodel.DateUtil;
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

//import com.monitorjbl.xlsx.StreamingReader;

public class ImportPreviewWindow  extends Application {

    ObservableList<Item> importData;
    String errorString = "";
    int itemCount = 0;

    public ImportPreviewWindow(ObservableList<Item> importData, String errorString, int itemCount) {
        this.importData = importData; this.errorString = errorString; this.itemCount = itemCount;
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
        TableColumn POnumCol = new TableColumn("PO#");
        TableColumn specsCol = new TableColumn("Specs");
        TableColumn historyCol = new TableColumn("History");

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
        historyCol.setCellValueFactory(new PropertyValueFactory<Item, String>("OtherRecords"));
        tablePr.setItems(importData);

        tablePr.getColumns().addAll(SKUCol, SNCol, PNCol, UPCCol, gradeCol, locCol, notesCol, userCol, timeCol, dateModifiedCol, POnumCol, specsCol, historyCol);


        Button cancel = new Button("Cancel");
        Button addItems = new Button("Add Items");

        Label errorsLbl = new Label ("Errors:");

        TextArea errorsArea = new TextArea();
        errorsArea.setMaxWidth(Double.MAX_VALUE);
        errorsArea.setPrefRowCount(18);
        errorsArea.setEditable(false);
        errorsArea.setWrapText(true);
        errorsArea.setText(errorString);

        Label previewLbl = new Label("Preview Items that will be added: (total "+itemCount+" items)");

        VBox mainVbox = new VBox(8,previewLbl,tablePr, errorsLbl,errorsArea);
        HBox buttonsHbox = new HBox(10, cancel, addItems);

        BorderPane pane = new BorderPane();
//        pane.setTop();
        pane.setCenter(mainVbox);
        pane.setBottom(buttonsHbox);

        BorderPane.setMargin(mainVbox, new Insets(10,10,0,10));
        BorderPane.setMargin(buttonsHbox, new Insets(10));


        Scene scene = new Scene(pane, 700, 650);
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

            new Thread(()->{
                Platform.runLater(() -> {
                    MainPage.importLoadLbl.setText("adding items");
                    AddItem.dotAnim(MainPage.importLoadLbl);
                    MainPage.loadingPane.toFront();
                    MainPage.pane.setDisable(true);
                    importPreviewStage.hide();
                });

                int countAdded = 0;

                try {
                    Connection conn = DriverManager.getConnection(MainPage.urll, MainPage.user, MainPage.passw);
                    Statement stmt = conn.createStatement();


                    for (Item it: importData){
                        String values =
                            "'" + it.SKU + "'" + "," +
                                (it.SN == null ? null : "'" + it.SN + "'") + "," +
                                (it.PN == null ? null : "'" + it.PN + "'") + "," +
                                (it.UPC == null ? null : "'" + it.UPC + "'") + "," +
                                (it.Grade == null ? null : "'" + it.Grade + "'") + "," +
                                (it.Location == null ? null : "'" + it.Location + "'") + "," +
                                (it.Notes == null ? null : "'" + it.Notes + "'") + "," +
                                (it.User == null ? null : "'" + it.User + "'") + "," +
                                (it.time == null ? null : "'" + it.time + "'") + "," +
                                (it.Images == null ? null : "'" + it.Images + "'") + "," +
                                (it.OtherRecords == null ? null : "'" + it.OtherRecords + "'") + "," +
                                (it.DateModified == null ? null : "'" + it.DateModified + "'") + "," +
                                (it.POnumber == null ? null : "'" + it.POnumber + "'") + "," +
                                (it.Specs == null ? null : "'" + it.Specs + "'");
                        String sql = ("INSERT INTO items " +
                            "VALUES (" + values + ")");
                        countAdded += stmt.executeUpdate(sql);
                        System.out.println("added element "+it.SKU);
                    }

                    stmt.close();
                    conn.close();



                } catch (SQLException ex) {
                    MainPage.databaseErrorAlert(ex).showAndWait();
                }

                int finalCountAdded = countAdded;
                Methods.updateUserLog(MainPage.user,"imported "+(finalCountAdded == 1 ? "1 item" :finalCountAdded + " items"));
                Platform.runLater(() -> {
                    MainPage.pane.toFront();
                    MainPage.pane.setDisable(false);

                    MainPage.updateTable();
                    importPreviewStage.close();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Import Successful!");
//            alert.setHeaderText("Look, a Confirmation Dialog");
                    alert.setContentText("Successfully added "+ finalCountAdded +" items.");

                    alert.showAndWait();

                });
            }).start();


        });
    }

    public void importBtnAction(FileChooser importFileChooser, String user, Stage primaryStage){

        //configuring file chooser
        importFileChooser.setTitle("Choose Excel File");
//        importFileChooser.setInitialDirectory(
//            new File("D:\\Download")
//        );
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

        new Thread(()->{
            Platform.runLater(() -> {
                MainPage.importLoadLbl.setText("processing import file");
                AddItem.dotAnim(MainPage.importLoadLbl);
                MainPage.loadingPane.toFront();
                MainPage.pane.setDisable(true);
            });
            int itemCountMethod = 0;

//            StringBuffer errorStringBuff = new StringBuffer(errorString);

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
                    colsHashm.put("History", null);
                    colsHashm.put("PO#", null);
                    colsHashm.put("Specs", null);
                    colsHashm.put("Date Created", null);
                    colsHashm.put("Date Modified", null);

                    for (int i = 0; i < num_columns; i++) {
                        String colmnString = cellIterator.next().getStringCellValue();
                        if (colsHashm.containsKey(colmnString)){
                            colsHashm.put(colmnString, i);
                        } else{
                            errorString += "\""+colmnString+"\", ";
                        }
                    }
                    if (!errorString.equals("")){
                        errorString = errorString.substring(0,errorString.length()-2);
                        errorString = "Column"+(errorString.contains(",")?"s ":" ")+errorString+(errorString.contains(",")?" are":" is")+" not found in the database, make sure that the columns are named " +
                            "\"SKU\", \"SN\", \"PN\", \"UPC\", \"Grade\", \"Location\", \"PO#\", \"Specs\", \"Notes\", \"User\", \"Date Created\", \"Date Modified\", \"History\"";

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

                        Timestamp currDateCreated = null;
                        Timestamp currDateModified = new Timestamp(System.currentTimeMillis());

                        currRow = rowIterator.next();
                        //now i go throw values in hashmap, and then get a cell with corresponding number; and then use
                        // algo to generate SKU, and then check by creating and deleting the item

                        //1
                        boolean needGenerating = false;
                        if (colsHashm.get("SKU") != null){
                            Cell SKUcell = currRow.getCell(colsHashm.get("SKU"));
                            if (SKUcell == null){
                                needGenerating = true;
                            } else if (SKUcell.getCellType() == CellType.NUMERIC || (SKUcell.getCellType() == CellType.FORMULA && SKUcell.getCachedFormulaResultType() == CellType.NUMERIC)) {
                                currSKU = (long) SKUcell.getNumericCellValue();
                            } else {
//                                System.out.println(SKUcell.getCellType());
                                errorString += "Error in row "+rowNum+": couldn't retrieve SKU number from a cell;\n";
                            }
                        } else{
                            //generate SKU
//                            currSKU =
                            needGenerating = true;
                        }

                        if (needGenerating){
                            ResultSet rs = stmt.executeQuery("SELECT SKUgenerated from addit_data");
                            rs.next();
                            long lastSKU = rs.getLong(1);
                            currSKU = lastSKU+1;
                            System.out.println("generated sku "+ currSKU+ "; execute update - "+stmt.executeUpdate("UPDATE addit_data SET SKUgenerated="+currSKU+" WHERE IDcolumn=1"));
                            rs.close();
                            generatedSKUcount++;
                        }
                        System.out.println(rowNum + " "+ currSKU);

                        //2
                        if (colsHashm.get("SN") != null){
                            Cell SNcell = currRow.getCell(colsHashm.get("SN"));
                            currSN = (SNcell == null? null: readCell(SNcell));
                        } else{
                            currSN = null;
                        }

                        //3
                        if (colsHashm.get("PN") != null){
                            Cell PNcell = currRow.getCell(colsHashm.get("PN"));
                            currPN = (PNcell == null? null: readCell(PNcell));
                        } else{
                            currPN = null;
                        }

                        //4
                        if (colsHashm.get("UPC") != null){
                            Cell UPCcell = currRow.getCell(colsHashm.get("UPC"));
                            currUPC = (UPCcell == null? null: readCell(UPCcell));
                        } else{
                            currUPC = null;
                        }

                        //5
                        if (colsHashm.get("Grade") != null){
                            Cell gradecell = currRow.getCell(colsHashm.get("Grade"));
                            currGrade = (gradecell == null? null: readCell(gradecell));
                        } else{
                            currGrade = null;
                        }

                        //6
                        if (colsHashm.get("Location") != null){
                            Cell Locationcell = currRow.getCell(colsHashm.get("Location"));
                            currLocation = (Locationcell == null? null: readCell(Locationcell));
                        } else{
                            currLocation = null;
                        }

                        //7
                        if (colsHashm.get("Notes") != null){
                            Cell Notescell = currRow.getCell(colsHashm.get("Notes"));
                            currNotes = (Notescell == null? null: readCell(Notescell));
                        } else{
                            currNotes = null;
                        }

                        //8
                        if (colsHashm.get("User") != null){
                            Cell Usercell = currRow.getCell(colsHashm.get("User"));
                            currUser = (Usercell == null? user: readCell(Usercell));
                        } else{
                            currUser = user;
                        }

                        //9
                        if (colsHashm.get("Images") != null){
                            Cell Imagescell = currRow.getCell(colsHashm.get("Images"));
                            currImages = (Imagescell == null? null: readCell(Imagescell));
                        } else{
                            currImages = null;
                        }

                        //10
                        if (colsHashm.get("History") != null){
                            Cell Historycell = currRow.getCell(colsHashm.get("History"));
                            currOtherRecords = (Historycell == null? null: readCell(Historycell));
                        } else{
                            currOtherRecords = null;
                        }

                        //11
                        if (colsHashm.get("PO#") != null){
                            Cell POnumcell = currRow.getCell(colsHashm.get("PO#"));
                            currPOnumber = (POnumcell == null? null: readCell(POnumcell));
                        } else{
                            currPOnumber = null;
                        }

                        //12
                        if (colsHashm.get("Specs") != null){
                            Cell specscell = currRow.getCell(colsHashm.get("Specs"));
                            currSpecs = (specscell == null? null: readCell(specscell));
                        } else{
                            currSpecs = null;
                        }

                        //13
                        if (colsHashm.get("Date Created") != null){
                            Cell datecrcell = currRow.getCell(colsHashm.get("Date Created"));
                            System.out.println("date cell "+datecrcell.getCellType());
                            if (datecrcell == null){
                                currDateCreated = currDateModified;
                            } else if (datecrcell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(datecrcell)){
                                System.out.println("getting data from ");
                                try {
                                    currDateCreated = new Timestamp(datecrcell.getDateCellValue().getTime());
                                } catch (Exception eee){
                                    currDateCreated = currDateModified;
                                    System.out.println("error reading DateCreated");
                                }
                            } else{
                                System.out.println("error with date cell, incorect type; dsate creatredf is set to modified date, type "+datecrcell.getCellType());
                                currDateCreated = currDateModified;
                            }
                        } else{
                            currDateCreated = currDateModified;
                        }

                        //14
                        if (colsHashm.get("Date Modified") != null){
                            Cell datecrcell = currRow.getCell(colsHashm.get("Date Modified"));
                            System.out.println("date cell "+datecrcell.getCellType());
                            if (datecrcell == null){
                                currDateModified = null;
                            } else if (datecrcell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(datecrcell)){
                                try{
                                    currDateModified = new Timestamp(datecrcell.getDateCellValue().getTime());
                                } catch (Exception eee){
                                    currDateModified = null;
                                    System.out.println("error reading DateModified");
                                }
                            } else{
                                System.out.println("error with date cell, incorect type; dsate creatredf is set to modified date, type "+datecrcell.getCellType());
                                currDateModified = null;
                            }
                        } else{
                            currDateModified = null;
                        }

                        System.out.println(currDateCreated);
                        System.out.println(currDateModified);

                        rowNum++;
                        if (currSKU == 0) {
                            System.out.println("WTF LOL BREAKED");
                            errorString+="\nCATASTROPHIC FAILURE";
                            continue;
                        }


                        if (needGenerating &&
                            currSN==null &&
                            currPN==null&&
                            currUPC==null&&
                            currGrade==null&&
                            currLocation==null&&
                            currNotes==null&&
                            currUser==null&&
                            currImages==null&&
                            currOtherRecords==null&&
                            currPOnumber==null&&
                            currSpecs==null
                        ){
                            errorString += "\nThere was an empty item";
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
                            currUser,
                            currDateCreated,
                            currImages,
                            currOtherRecords,
                            currDateModified,
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
                                (currItem.User == null ? null : "'" + currItem.User + "'") + "," +
                                (currItem.time == null ? null : "'" + currItem.time + "'") + "," +
                                (currItem.Images == null ? null : "'" + currItem.Images + "'") + "," +
                                (currItem.OtherRecords == null ? null : "'" + currItem.OtherRecords + "'") + "," +
                                (currItem.DateModified == null ? null : "'" + currItem.DateModified + "'") + "," +
                                (currItem.POnumber == null ? null : "'" + currItem.POnumber + "'") + "," +
                                (currItem.Specs == null ? null : "'" + currItem.Specs + "'");
                        String sql = ("INSERT INTO items " +
                            "VALUES (" + values + ")");
                        try {
                            int addingResult = stmt.executeUpdate(sql);
                            int deleteResult = stmt.executeUpdate("DELETE from items WHERE SKU=" + currItem.SKU);

                            importDataMethod.add(currItem);
                            itemCountMethod++;
                        } catch (SQLIntegrityConstraintViolationException ex) {
                            //possible errors: duplicate SKU, duplicate SN, other error
                            String message = ex.getMessage();
                            if (message.contains("Duplicate entry ") && message.contains(" for key 'items.PRIMARY'")) {
                                System.out.println("Duplicate entry for key 'items.PRIMARY'");
                                errorString += "\nError adding item "+currItem.SKU+": Duplicate SKU = "+currItem.SKU +";";

                            } else if (message.contains("Duplicate entry ") && message.contains(" for key 'items.SN'")) {
                                System.out.println("Duplicate entry for key 'items.SN'");
                                errorString += "\nError adding item "+currItem.SKU+": Duplicate SN = "+currItem.SN+";";

                            } else{
                                errorString += "\nError adding item "+currItem.SKU+": Uncatched error;";
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
                errorString += "\nERROR: uncatched database error;";
                MainPage.databaseErrorAlert(ex).showAndWait();
            } catch (IOException ioe){
                errorString += "\nERROR: uncatched IO Files error;";
                Platform.runLater(() -> {
                    MainPage.ioErrorAlert(ioe).showAndWait();
                });

            } catch (Exception eeee){
                Platform.runLater(() -> {
                    MainPage.unknownErrorAlert(eeee).showAndWait();

                });
            }


            int finalItemCountMethod = itemCountMethod;
            Platform.runLater(() -> {
                MainPage.pane.toFront();
                MainPage.pane.setDisable(false);
                new ImportPreviewWindow(importDataMethod, errorString, finalItemCountMethod).start(primaryStage);
            });
        }).start();



    }
    public String readCell(Cell cell){
        switch (cell.getCellType()){
        case STRING:
            return (cell.getStringCellValue().strip().equals("")?null:cell.getStringCellValue().strip());
        case NUMERIC:
            return Integer.toString((int) cell.getNumericCellValue());
        case FORMULA:
            switch (cell.getCachedFormulaResultType()){
            case NUMERIC:
                return Integer.toString((int) cell.getNumericCellValue());
            case STRING:
                return (cell.getStringCellValue().strip().equals("")?null:cell.getStringCellValue().strip());
            default:
//                System.out.println("readCell() returned null,lol");
                return null;
            }
        default:
//            System.out.println("readCell() returned null,lol");
            return null;
        }
    }
}
