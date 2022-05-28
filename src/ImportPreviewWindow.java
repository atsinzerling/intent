import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class ImportPreviewWindow  extends Application {

    ObservableList<Item> importData;

    public ImportPreviewWindow(ObservableList<Item> importData) {
        this.importData = importData;
    }

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


        Button cancel = new Button("cancel");
        Button addItems = new Button("Add Items");


        BorderPane pane = new BorderPane();
        Label previewLbl = new Label("Preview Items that will be added");
        pane.setTop(previewLbl);
        pane.setCenter(tablePr);
        pane.setBottom(new HBox(10, cancel, addItems));

        Scene scene = new Scene(pane, 700, 500);
        Stage importPreviewStage = new Stage();
        importPreviewStage.setTitle("Import data preview");
        importPreviewStage.setScene(scene);
        importPreviewStage.initOwner(primaryStage);
        importPreviewStage.initModality(Modality.WINDOW_MODAL);
        importPreviewStage.setResizable(true);
        importPreviewStage.show();
//        scene.setOnKeyPressed((KeyEvent e) -> {
//            if (e.getCode() == KeyCode.ESCAPE) {
//                imageViewStage.close();
//            }
//        });



        cancel.setOnAction(e->{
            importPreviewStage.close();
        });
        addItems.setOnAction(e->{

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
                            "'" + it.User + "'" + "," +
                            "'" + new Timestamp(System.currentTimeMillis()) + "'" + "," +
                            (it.Images == null ? null : "'" + it.Images + "'") + "," +
                            (it.OtherRecords == null ? null : "'" + it.OtherRecords + "'") + "," +
                            "'" + new Timestamp(System.currentTimeMillis()) + "'" + "," +
                            (it.POnumber == null ? null : "'" + it.POnumber + "'") + "," +
                            (it.Specs == null ? null : "'" + it.Specs + "'");
                    String sql = ("INSERT INTO items " +
                        "VALUES (" + values + ")");
                    stmt.executeUpdate(sql);
                }

                MainPage.updateTable();
                importPreviewStage.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }


        });
    }
}
