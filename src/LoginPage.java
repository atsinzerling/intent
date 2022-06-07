import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class LoginPage extends Application {
//    private String locUser = "localuser";
//    private String locPass = "intentgroup";
    String connectionUrl =
    "mysql://DESKTOP-E5VI6AD:3306/products";
//    "mysql://localhost:3306/products";

    @Override
    public void start(Stage primaryStage) {

        Label signinLbl = new Label("Sign in");
        signinLbl.setMaxWidth(Double.MAX_VALUE);
//        signinLbl.setMinHeight(80);
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
//        passwordShow.toFront();

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
            } else{
                passwordHid.toFront();
                passwordHid.requestFocus();
                passwordHid.setText(passwordShow.getText());
                passwordHid.positionCaret(passwordHid.getText().length());

                showPass.setText("show");
            }
        });

        username.setOnKeyPressed((KeyEvent e) -> {
            if (e.getCode().getCode() == 10) {
                if (showPass.getText().equals("show")){
                    passwordHid.requestFocus();
                    passwordHid.setText(passwordShow.getText());
                } else{
                    passwordShow.requestFocus();
                    passwordShow.setText(passwordHid.getText());
                }
            }
        });

        Button signinBtn = new Button("Sign in");
        signinBtn.setPrefWidth(Double.MAX_VALUE);
        signinBtn.setPrefHeight(50);

        passwordShow.setOnKeyPressed((KeyEvent e) -> {
            if (e.getCode().getCode() == 10 && !username.getText().equals("")) {
                signinBtn.fire();
            }
        });
        passwordHid.setOnKeyPressed((KeyEvent e) -> {
            if (e.getCode().getCode() == 10 && !username.getText().equals("")) {
                signinBtn.fire();
            }
        });


        VBox fieldsVbox = new VBox(3,loginSuccessLbl,loginSuccessCommentLbl, username, passwordStack,showPass, signinBtn);
        fieldsVbox.setMaxWidth(260);
        fieldsVbox.setAlignment(Pos.TOP_RIGHT);


        BorderPane.setMargin(signinLbl, new Insets(20, 0,10,0));
        VBox.setMargin(signinBtn, new Insets(10, 0,0,0));


        BorderPane pane = new BorderPane();
        pane.setTop(signinLbl);
        pane.setCenter(fieldsVbox);
//        BorderPane.setMargin(fieldsVbox, new Insets(20,0,0,0));

        /** connection URl */

        Label connectUrlLbl = new Label("connection URL:");
        TextField connectUrlField = new TextField(connectionUrl);
        connectUrlField.setPromptText("put your connection url here");
        connectUrlField.setDisable(true);

        Button editConn = new Button("Edit");

        Button saveConn = new Button("Save");
        editConn.setOnAction(e->{
            connectUrlField.setDisable(false);
            connectUrlField.requestFocus();
            connectUrlField.positionCaret(connectUrlField.getText().length());
            saveConn.setDisable(false);
        });
        saveConn.setOnAction(e->{
            connectionUrl = connectUrlField.getText().strip();
            connectUrlField.setDisable(true);
            saveConn.setDisable(true);
            signinLbl.requestFocus();
        });
        saveConn.setDisable(true);
        HBox editSaveHbox = new HBox(7,editConn,saveConn);
        editSaveHbox.setAlignment(Pos.CENTER);

        VBox connUrlVbox = new VBox(3,connectUrlLbl, connectUrlField, editSaveHbox);
        connUrlVbox.setMaxWidth(260);
        BorderPane.setAlignment(connUrlVbox, Pos.CENTER);
        BorderPane.setMargin(connUrlVbox, new Insets(10,0,10,0));

        pane.setBottom(connUrlVbox);
        /** connect url ended */

        Scene scene = new Scene(pane, 400, 400);
        Stage loginStage = new Stage();
        loginStage.setTitle("Sign in");
        loginStage.setScene(scene);
//        imageViewStage.initModality(Modality.WINDOW_MODAL);
        loginStage.setResizable(false);
        loginStage.show();

        signinLbl.requestFocus();

        signinBtn.setOnAction(e ->{
            String passw = (showPass.getText().equals("show")?passwordHid.getText():passwordShow.getText()).strip();
            String usern = username.getText().strip();

            if (checkCredetials("jdbc:"+connectionUrl, usern, passw)){

                AddItem.setLblStatus(loginSuccessLbl, "Sign in successful!", 0, "green");
                AddItem.setLblStatus(loginSuccessCommentLbl, "Launching the Main page.", 0, "");
                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1.2), ev -> {

                    new MainPage("jdbc:"+connectionUrl, usern, passw).start(primaryStage);
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
//        username.setText("admin");
//        passwordHid.setText("newpass");
        loginStage.getIcons().add(new Image("file:src/intent_facebook_logo edited 2.png"));
        class addMethod{
            void onEnter(){

            }
        }

    }
    public static boolean checkCredetials(String connUrl, String user, String passw){
        try {
            Connection conn = DriverManager.getConnection(connUrl,user,passw);
            boolean validd = conn.isValid(5);
            conn.close();
            return validd;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("checkCreds exception catched");
            return false;
        }
    }
    public static void main(String[] args) {
        launch(args);
    }

}
