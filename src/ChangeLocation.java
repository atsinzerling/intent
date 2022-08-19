import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class ChangeLocation extends Application {

    ObservableList<Item> changeData;
    String errorString = "";
    int itemCount = 0;

    public ChangeLocation(ObservableList<Item> changeData, String errorString, int itemCount) {
        this.changeData = changeData;
        this.errorString = errorString;
        this.itemCount = itemCount;
    }

    @Override
    public void start(Stage primaryStage) {

        Button cancel = new Button("Cancel");
        Button Confirm = new Button("Confirm");

        Label errorsLbl = new Label("Errors:");

        TextArea errorsArea = new TextArea();
        errorsArea.setMaxWidth(Double.MAX_VALUE);
        errorsArea.setPrefRowCount(18);
        errorsArea.setEditable(false);
        errorsArea.setWrapText(true);
        errorsArea.setText(errorString);

        VBox mainVbox = new VBox(8);

        HashMap<String, Integer> hash = new HashMap<>();
        for (Item it : changeData) {
            if (hash.containsKey(it.Location)) {
                hash.replace(it.Location, hash.get(it.Location) + 1);
            } else {
                hash.put(it.Location, 1);
            }
        }
        List<Map.Entry<String, Integer>> list
            = new LinkedList<Map.Entry<String, Integer>>(
            hash.entrySet());

        // Sort the list using lambda expression
        Collections.sort(
            list,
            (i1,
             i2) -> i2.getValue().compareTo(i1.getValue()));

        // put data from sorted list to hashmap
        hash
            = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> aa : list) {
            hash.put(aa.getKey(), aa.getValue());
        }

        StringBuilder prevLocs = new StringBuilder("");
        for (Map.Entry<String, Integer> me : hash.entrySet()) {
            Label lbl = new Label(
                me.getValue() + (me.getValue() == 1 ? " item" : " items") + " with Location \"" + me.getKey() + "\"");
            prevLocs.append("\"" + me.getKey() + "\"").append(", ");
            mainVbox.getChildren().add(lbl);
        }
        prevLocs.setLength(Math.max(prevLocs.length() - 2, 0));

        TextField textf = new TextField();
        Label newLocLbl = new Label("new Location:");
        HBox changeBox = new HBox(6, newLocLbl, textf);
        changeBox.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(changeBox, new Insets(0, 0, 0, 0));
        // change location to empty
        HBox buttonsHbox = new HBox(10, cancel, Confirm);
        VBox buttonsVbox = new VBox(8, changeBox, buttonsHbox);

        BorderPane pane = new BorderPane();
        pane.setCenter(mainVbox);
        pane.setBottom(buttonsVbox);

        BorderPane.setMargin(mainVbox, new Insets(10, 10, 0, 10));
        BorderPane.setMargin(buttonsVbox, new Insets(10));


        Scene scene = new Scene(pane, 400, 350);
        Stage importPreviewStage = new Stage();
        importPreviewStage.setTitle(
            "Change Location for " + changeData.size() + " item" + (changeData.size() == 1 ? "" : "s"));
        importPreviewStage.setScene(scene);
        importPreviewStage.initOwner(primaryStage);
        importPreviewStage.initModality(Modality.WINDOW_MODAL);
        importPreviewStage.setResizable(true);
        importPreviewStage.show();

        scene.setOnKeyPressed((KeyEvent e) -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                importPreviewStage.close();
            }
        });

        cancel.setOnAction(e -> {
            importPreviewStage.close();
        });
        Confirm.setOnAction(e -> {
            String newL = textf.getText().strip();
            if (newL == null || newL.equals("")) {
                Methods.errorAlert("Empty location", "Cannot change to empty Location");
                return;
            }
            new Thread(() -> {

                int countAdded = 0;

                try {
                    Platform.runLater(() -> {
                        MainPage.importLoadLbl.setText("changing location");
                        AddItem.dotAnim(MainPage.importLoadLbl);
                        MainPage.loadingPane.toFront();
                        MainPage.pane.setDisable(true);
                        importPreviewStage.hide();
                    });


                    Connection conn = DriverManager.getConnection(MainPage.urll, MainPage.user, MainPage.passw);
                    Statement stmt = conn.createStatement();

                    String timm = new Timestamp(System.currentTimeMillis()).toString();
                    if (timm.split("\\.").length > 0) {
                        timm = timm.split("\\.")[0];
                    }
                    Timestamp timmst = new Timestamp(System.currentTimeMillis());
                    conn.setAutoCommit(false);

                    for (Item it : changeData) {
                        String otherRecs = MainPage.user + " on " + timm + " : " + "updated Location from \"" +
                            (it.Location == null ? "" : it.Location) + "\" to \"" + newL + "\" (in a batch)" +
                            ";<<<:::===" + (it.OtherRecords == null ? "" : it.OtherRecords);
                        ;
                        String sqlll =
                            "UPDATE " + MainPage.schema + ".items SET Location='" + newL + "', OtherRecords='" +
                                otherRecs + "', " +
                                "DateModified='" + timmst + "' WHERE SKU=" + it.SKU;

                        //sqlll updates history and item
                        stmt.executeUpdate(new String(sqlll));
                        countAdded++;
                    }

//                    stmt.executeBatch();
                    conn.commit();
                    conn.setAutoCommit(true);

                    stmt.close();
                    conn.close();


                } catch (SQLException ex) {
                    MainPage.databaseErrorAlert(ex).showAndWait();
                } finally {
                    int finalCountAdded = countAdded;
                    Methods.updateUserLog(MainPage.user,
                        "changed location for " + (finalCountAdded == 1 ? "1 item" : finalCountAdded + " items") +
                            " from " + prevLocs + " to \"" + newL + "\"");
                    Platform.runLater(() -> {
                        MainPage.pane.toFront();
                        MainPage.pane.setDisable(false);

                        MainPage.updateTable();
                        importPreviewStage.close();
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Changing location successful!");
                        alert.setContentText(Methods.wrap("Successfully changed location for " +
                            (finalCountAdded == 1 ? "1 item" : finalCountAdded + " items") + " from " + prevLocs +
                            " to \"" + newL + "\""));
                        alert.showAndWait();
                    });
                }
            }).start();
        });
    }
}
