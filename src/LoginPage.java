import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;


public class LoginPage extends Application {
    //    private String locUser = "localuser";
//    private String locPass = "intentgroup";
    String connectionUrl =
        "mysql://DESKTOP-E5VI6AD:3306/products";
    //    "mysql://localhost:3306/products";
    String schema = "products";

    @Override
    public void start(Stage primaryStage) {
        System.out.println("Number of active threads from the given thread: " + Thread.activeCount());
        try {
            Class.forName("org.apache.poi.ss.usermodel.Cell");
//            Class.forName("com.mysql.cj.jdbc.com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String initMessage =
            "+----------------------------------------------------------------+\n" +
                "| +------------------------------------------------------------+ |\n" +
                "| | +--------------------------------------------------------+ | |\n" +
                "| | |                                                        | | |\n" +
                "| | |  Don't close this command-line window!                 | | |\n" +
                "| | |  Closing it will exit the Intent-Database application  | | |\n" +
                "| | |                                                        | | |\n" +
                "| | +--------------------------------------------------------+ | |\n" +
                "| +------------------------------------------------------------+ |\n" +
                "+----------------------------------------------------------------+";
        System.out.println(initMessage);

        try {
            File fl = new File("C:\\Program Files Intent\\configg.csv");

            Scanner myReader = new Scanner(fl);
            myReader.nextLine();
            connectionUrl = myReader.nextLine().split(",")[1].strip();
            schema = connectionUrl.split("/")[connectionUrl.split("/").length - 1];

            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error reading configuration file");
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Config file error");
            alert.setContentText(Methods.wrap(
                "An error reading configuration file at \"C:\\Program Files Intent\\configg.csv\" occurred."));
            alert.showAndWait();
        }


        Label signinLbl = new Label("Sign in");
        signinLbl.setMaxWidth(Double.MAX_VALUE);
        signinLbl.setAlignment(Pos.CENTER);
        signinLbl.setFont(Font.font("Verdana", FontWeight.BOLD, FontPosture.REGULAR, 40));

        Label loginSuccessLbl = new Label("");
        loginSuccessLbl.setAlignment(Pos.CENTER);
        loginSuccessLbl.setMaxWidth(Double.MAX_VALUE);

        Label loginSuccessCommentLbl = new Label("");
        loginSuccessCommentLbl.setAlignment(Pos.CENTER);
        loginSuccessCommentLbl.setMaxWidth(Double.MAX_VALUE);

        TextField username = new TextField();
        username.setPromptText("username");

        TextField passwordShow = new TextField("");

        PasswordField passwordHid = new PasswordField();
        passwordHid.setPromptText("password");


        StackPane passwordStack = new StackPane(passwordShow, passwordHid);

        Label showPass = new Label("show");
        showPass.setOnMouseEntered(e -> {
            showPass.setCursor(Cursor.HAND);
            showPass.setUnderline(true);
            showPass.setTextFill(Color.web("#0089CCFF"));
        });
        showPass.setOnMouseExited(e -> {
            showPass.setUnderline(false);
            showPass.setTextFill(Color.BLACK);
        });
        showPass.setOnMousePressed(e -> {
            if (showPass.getText().equals("show")) {
                passwordShow.toFront();
                passwordShow.requestFocus();
                passwordShow.setText(passwordHid.getText());
                passwordShow.positionCaret(passwordShow.getText().length());

                showPass.setText("hide");
            } else {
                passwordHid.toFront();
                passwordHid.requestFocus();
                passwordHid.setText(passwordShow.getText());
                passwordHid.positionCaret(passwordHid.getText().length());

                showPass.setText("show");
            }
        });

        username.setOnKeyPressed((KeyEvent e) -> {
            if (e.getCode().getCode() == 10 || e.getCode() == KeyCode.TAB) {
                if (showPass.getText().equals("show")) {
                    passwordHid.requestFocus();
                    passwordHid.setText(passwordShow.getText());
                } else {
                    passwordShow.requestFocus();
                    passwordShow.setText(passwordHid.getText());
                }
            }
        });

        Button signinBtn = new Button("Sign in");
        signinBtn.setPrefWidth(Double.MAX_VALUE);
        signinBtn.setPrefHeight(50);

        passwordShow.setOnKeyPressed((KeyEvent e) -> {
            if ((e.getCode().getCode() == 10 || e.getCode() == KeyCode.TAB) && !username.getText().equals("")) {
                signinBtn.fire();
            }
        });
        passwordHid.setOnKeyPressed((KeyEvent e) -> {
            if ((e.getCode().getCode() == 10 || e.getCode() == KeyCode.TAB) && !username.getText().equals("")) {
                signinBtn.fire();
            }
        });


        VBox fieldsVbox =
            new VBox(3, loginSuccessLbl, loginSuccessCommentLbl, username, passwordStack, showPass, signinBtn);
        fieldsVbox.setMaxWidth(260);
        fieldsVbox.setAlignment(Pos.TOP_RIGHT);


        BorderPane.setMargin(signinLbl, new Insets(20, 0, 10, 0));
        VBox.setMargin(signinBtn, new Insets(10, 0, 0, 0));


        BorderPane pane = new BorderPane();
        pane.setTop(signinLbl);
        pane.setCenter(fieldsVbox);
//        BorderPane.setMargin(fieldsVbox, new Insets(20,0,0,0));

        /** connection URl */

        Label connectUrlLbl = new Label("connection URL:");
        TextField connectUrlField = new TextField(connectionUrl);
        connectUrlField.setPromptText("put your connection url here");
        connectUrlField.setEditable(false);
        connectUrlField.setText(connectionUrl);

        VBox connUrlVbox = new VBox(3, connectUrlLbl, connectUrlField);
        connUrlVbox.setMaxWidth(260);
        BorderPane.setAlignment(connUrlVbox, Pos.CENTER);
        BorderPane.setMargin(connUrlVbox, new Insets(10, 0, 15, 0));

        pane.setBottom(connUrlVbox);
        /** connect url ended */


        Scene scene = new Scene(pane, 400, 380);
        Stage loginStage = new Stage();
        loginStage.setTitle("Sign in");
        loginStage.setScene(scene);
//        imageViewStage.initModality(Modality.WINDOW_MODAL);
        loginStage.setResizable(false);
        loginStage.show();

        signinLbl.requestFocus();

        signinBtn.setOnAction(e -> {
            String passw = (showPass.getText().equals("show") ? passwordHid.getText() : passwordShow.getText()).strip();
            String usern = username.getText().strip();

            if (checkCredetials("jdbc:" + connectionUrl, usern, passw)) {

                AddItem.setLblStatus(loginSuccessLbl, "Sign in successful!", 0, "green");
                AddItem.setLblStatus(loginSuccessCommentLbl, "Launching the Main page.", 0, "");
                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.4), ev -> {

                    new MainPage("jdbc:" + connectionUrl, schema, usern, passw).start(primaryStage);
                    loginStage.close();
                }));
                timeline.setCycleCount(1);
                timeline.play();

                //close curr window
            } else {
                AddItem.setLblStatus(loginSuccessLbl, "Sign in failed.", 3, "red");
                AddItem.setLblStatus(loginSuccessCommentLbl, "Check the credentials or the connection URL.", 3, "");
            }
        });

        //initial credentials for easy sign in
        loginStage.getIcons()
            .add(new Image("file:C:\\Program Files Intent\\Intent Database 1.0.0\\img\\intent_logo.png"));

    }

    public static boolean checkCredetials(String connUrl, String user, String passw) {
        try {
            Connection conn = DriverManager.getConnection(connUrl, user, passw);
            boolean validd = conn.isValid(5);
            conn.close();
            return validd;
        } catch (SQLException e) {
            System.out.println("checking credentials failed: ");
            e.printStackTrace();
            System.out.println("Just outputted the error");
            return false;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
