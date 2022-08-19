import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.math3.analysis.FunctionUtils.add;

public class UserActivity extends Application {
    String user = "admin";
    Label userActLbl = new Label("");
    ArrayList<Activity> activities = new ArrayList<>();
    VBox vbox = new VBox();

    public UserActivity(String user) {
        this.user = user;
    }

    public UserActivity() {
    }

    @Override
    public void start(Stage primaryStage) {

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setPrefWidth(650);

//        scrollPane.setPrefHeight();

        userActLbl.setFont(Font.font(null, 14));

        Label showLbl = new Label("Show");
        ObservableList<String> showObsList = FXCollections.observableArrayList();
        showObsList.add("5");
        showObsList.add("15");
        showObsList.add("30");
        showObsList.add("all");

        ChoiceBox showChoiceBox = new ChoiceBox(showObsList);
        showChoiceBox.setValue("15");
        showChoiceBox.setMaxHeight(15);
        showChoiceBox.setOnAction(event -> {
//            System.out.println(showChoiceBox.getValue());
            if (showChoiceBox.getValue().equals("all")) {
                generateStats(0);
            } else {
                generateStats(Integer.parseInt((String) showChoiceBox.getValue()));
            }
        });

        Button refreshBtn = new Button("Refresh");
        HBox.setMargin(refreshBtn, new Insets(0, 0, 0, 20));
        refreshBtn.setOnAction(e -> {
            System.out.println("refreshing...");
            retrieveStats();
            if (showChoiceBox.getValue().equals("all")) {
                generateStats(0);
            } else {
                generateStats(Integer.parseInt((String) showChoiceBox.getValue()));
            }
            System.out.println("refreshed");
        });

        HBox showHbox = new HBox(5, showLbl, showChoiceBox, refreshBtn);
        showHbox.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(showHbox, Priority.ALWAYS);

        HBox userHbox = new HBox(userActLbl, showHbox);
        userHbox.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(userHbox, new Insets(8, 10, 5, 12));

        retrieveStats();
        generateStats(15);

        VBox scrollVbox = new VBox(userHbox, vbox);
        scrollPane.setContent(scrollVbox);


        BorderPane pane = new BorderPane();
        pane.setCenter(scrollPane);

        Scene scene = new Scene(pane, 700, 650);
        Stage importPreviewStage = new Stage();
        importPreviewStage.setTitle("User Activity - " + user);
        importPreviewStage.setScene(scene);
        importPreviewStage.initOwner(primaryStage);
        importPreviewStage.initModality(Modality.WINDOW_MODAL);
        importPreviewStage.setResizable(true);
        importPreviewStage.show();

        Methods.boostScroll(scrollPane, scrollVbox, 50);
    }

    public void generateStats(int num) {
        vbox.getChildren().clear();
        int length = activities.size();
        userActLbl.setText(user + " activity (" + (length == 1 ? "1 day" : length + " days") + " on record):");

        if (length == 0) {
            Label noActivityLbl = new Label("no user activity");
            vbox.setAlignment(Pos.CENTER);
            noActivityLbl.setAlignment(Pos.CENTER);
            vbox.getChildren().add(noActivityLbl);
        } else {
            if (num == 0) {
                num = length;
            }
            int i = 0;
            for (Activity act : activities) {
                if (i == num) {
                    break;
                }
                BorderPane dayBorderPane = new BorderPane();
                dayBorderPane.setMinWidth(600);

                Date date = act.date;
                String dayOfWeek = DayOfWeek.from(act.date.toLocalDate()).name();
//                System.out.println(dayOfWeek);
                Label dateLbl = new Label(date + " " + dayOfWeek);
                BorderPane.setMargin(dateLbl, new Insets(8, 3, 3, 15));
                dayBorderPane.setTop(dateLbl);

                TextArea historyArea = new TextArea();
                historyArea.setMaxWidth(450);
                historyArea.setPrefRowCount(7);
                historyArea.setEditable(false);
//                historyArea.setWrapText(true);

                String origTxt = act.log;
                String formatted = origTxt.replaceAll("<<<:::===", "\n");
                historyArea.setText(formatted);
                BorderPane.setMargin(historyArea, new Insets(3));

                dayBorderPane.setLeft(historyArea);

                Label stat1Lbl = new Label("worked on " + countItems(origTxt) + " unique items");
                VBox.setMargin(stat1Lbl, new Insets((3)));
                VBox statsVbox = new VBox(2, stat1Lbl);

                dayBorderPane.setCenter(statsVbox);


                dayBorderPane.setBorder(new Border(new BorderStroke(
                    Color.web("#b4b4b4"), BorderStrokeStyle.SOLID, new CornerRadii(5),
                    new BorderWidths(1))));
                VBox.setMargin(dayBorderPane, new Insets(6));

                vbox.getChildren().add(dayBorderPane);
                i++;
            }
        }
    }

    public void retrieveStats() {
        try {
            Connection conn = DriverManager.getConnection(MainPage.urll, MainPage.user, MainPage.passw);
            Statement stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery(
                "SELECT * from " + MainPage.schema + ".useractions WHERE User = '" + user + "' order by Date desc");
            activities.clear();
            while (rs.next()) {
                activities.add(new Activity(rs.getDate("Date"), rs.getString("Log")));
            }
            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
            MainPage.databaseErrorAlert(e).showAndWait();
        }
    }

    public int countItems(String origTxt) {
        Set<Integer> hash_set = new HashSet<>();
        for (String str : origTxt.split("<<<:::===")) {
            if (str.contains("SKU=")) {
                int SKU = Integer.parseInt(str.split("\"")[1]);
                hash_set.add(SKU);
            }
        }
        return hash_set.size();
    }

//    public static void main(String[] args) {
//        launch(args);
//    }
}
