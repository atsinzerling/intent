import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.DayOfWeek;
import java.util.HashSet;
import java.util.Set;

public class UserActivity extends Application {
    String user = "admin";

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
        scrollPane.setPrefWidth(570);
//        scrollPane.setPrefHeight();
        VBox vbox = renderStats();
        scrollPane.setContent(vbox);


        BorderPane pane = new BorderPane();
        pane.setCenter(scrollPane);

        Scene scene = new Scene(pane, 700, 570);
        Stage importPreviewStage = new Stage();
        importPreviewStage.setTitle("User Activity - "+user);
        importPreviewStage.setScene(scene);
        importPreviewStage.initOwner(primaryStage);
        importPreviewStage.initModality(Modality.WINDOW_MODAL);
        importPreviewStage.setResizable(true);
        importPreviewStage.show();

    }

    public VBox renderStats(){
        VBox vbox = new VBox();
        boolean haveActivity = false;
        try{
            Connection conn = DriverManager.getConnection(MainPage.urll, MainPage.user, MainPage.passw);
            Statement stmt = conn.createStatement();
//            String sqlCreate = "CREATE USER '"+usern+"'@'%' IDENTIFIED WITH mysql_native_password BY '"+password+"'";
//            String sqlUpdate = "UPDATE `mysql`.`user` SET `authentication_string` = '"+password+"' WHERE `User` = '"+usern+"';";

            ResultSet rs = stmt.executeQuery("SELECT * from products.useractions WHERE User = '"+user+"' order by Date desc");
            while (rs.next()){
                BorderPane dayBorderPane = new BorderPane();
                dayBorderPane.setMinWidth(520);

                Date date = rs.getDate("Date");
                String dayOfWeek = DayOfWeek.from(rs.getDate("Date").toLocalDate()).name();
                System.out.println(dayOfWeek);
                Label dateLbl = new Label(date+" "+dayOfWeek);
                BorderPane.setMargin(dateLbl, new Insets(8,3,3,15));
                dayBorderPane.setTop(dateLbl);

                TextArea historyArea = new TextArea();
                historyArea.setMaxWidth(320);
                historyArea.setPrefRowCount(7);
                historyArea.setEditable(false);
                historyArea.setWrapText(true);

                String origTxt = rs.getString("Log");
                String formatted = origTxt.replaceAll("<<<:::===","\n");
                historyArea.setText(formatted);
                BorderPane.setMargin(historyArea, new Insets(3));

                dayBorderPane.setLeft(historyArea);

                Label stat1Lbl = new Label("worked on "+countItems(origTxt)+" unique items");
                VBox.setMargin(stat1Lbl,new Insets((3)));
                VBox statsVbox = new VBox(2, stat1Lbl);

                dayBorderPane.setCenter(statsVbox);



                dayBorderPane.setBorder(new Border(new BorderStroke(
                    Color.web("#b4b4b4"), BorderStrokeStyle.SOLID, new CornerRadii(5),
                    new BorderWidths(1))));
                VBox.setMargin(dayBorderPane,new Insets(6));

                vbox.getChildren().add(dayBorderPane);
                haveActivity = true;
            }
            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
            MainPage.databaseErrorAlert(e).showAndWait();
        }
        if (!haveActivity){
            Label noActivityLbl = new Label("no user activity");
            vbox.setAlignment(Pos.CENTER);
            noActivityLbl.setAlignment(Pos.CENTER);
            vbox.getChildren().add(noActivityLbl);
        }

        return vbox;
    }

    public int countItems(String origTxt){
        Set<Integer> hash_set = new HashSet<>();
        for (String str: origTxt.split("<<<:::===")){
            if (str.contains("SKU=")){
                int SKU =  Integer.parseInt(str.split("\"")[1]);
                hash_set.add(SKU);
            }
        }
        return hash_set.size();
    }

//    public static void main(String[] args) {
//        launch(args);
//    }

}
