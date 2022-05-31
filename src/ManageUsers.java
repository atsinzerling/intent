import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ManageUsers extends Application {
    ArrayList<User> users = new ArrayList<>();
    VBox usersVbox = null;


//    public ManageUsers(Image imgg) {
//        this.imgg = imgg;
//    }

    @Override
    public void start(Stage secondStage) {
        retrieveUsers();
//        createUser("sampleuser3", "sample3");

//        ImageView imgview = new ImageView(imgg);
//        imgview.setFitHeight(700);
//        imgview.setFitWidth(1244);
//        imgview.setPreserveRatio(true);

        usersVbox = generateUsersVbox();
        HBox.setMargin(usersVbox, new Insets(0,0,80,0));

        Button addUserBtn = new Button("Add new user");
        addUserBtn.setMinSize(100,40);
        HBox.setMargin(addUserBtn, new Insets(12,12,12,0));


        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setPrefWidth(530);
//        scrollPane.setPrefHeight();
        scrollPane.setContent(new HBox(20, usersVbox, addUserBtn));

        BorderPane pane = new BorderPane();
        pane.setLeft(scrollPane);

        Scene scene = new Scene(pane, 700, 500);
        Stage manageUsersStage = new Stage();
        manageUsersStage.setTitle("Manage Users Window");
        manageUsersStage.setScene(scene);
        manageUsersStage.initOwner(secondStage);
        manageUsersStage.initModality(Modality.WINDOW_MODAL);
        manageUsersStage.setResizable(true);
        manageUsersStage.show();

        addUserBtn.setOnAction(e->{
            System.out.println("addUser btn pressed");
            users.add(0, new User("","", false));
            setUsersBox();
            scrollPane.setContent(new HBox(20, usersVbox, addUserBtn));
            pane.setLeft(scrollPane);
            for(User us: users){
                System.out.println((us==null?"null":us));
            }
        });
//        scene.setOnKeyPressed((KeyEvent e) -> {
//            if (e.getCode() == KeyCode.ESCAPE) {
//                manageUsersStage.close();
//            }
//        });
    }
    public void setUsersBox(){
        usersVbox = generateUsersVbox();
    }
    public VBox generateUsersVbox(){
        VBox vbox = new VBox();
        for (User user: users){
            vbox.getChildren().add(generateUserBox(user));
            System.out.println("added userbox for "+(user==null?"null":user));
        }

        return vbox;
    }
    public BorderPane generateUserBox(User userObj){


        Button edit = new Button("Edit user");
        Button delete = new Button("Delete user");
        Button save = new Button("Save changes");
        save.setDisable(true);
//        edit.setMaxHeight(5);
        edit.setPrefHeight(20);
        edit.setMinHeight(22);
        delete.setMinHeight(22);
        delete.setPrefHeight(20);
        save.setMinHeight(22);
        save.setPrefHeight(20);
        VBox.setMargin(save, new Insets(45,0,0,0));

        Label userLbl = new Label(userObj.getUsername());
        userLbl.setAlignment(Pos.CENTER_LEFT);
        userLbl.setMinHeight(25);
        TextField userTextF = new TextField(userObj.getUsername());
        userTextF.setPromptText("username");
        userTextF.setMaxWidth(100);
        userTextF.setMaxHeight(25);
        userTextF.setMinHeight(25);
        userLbl.setStyle("-fx-font-size: 14;-fx-font-weight: bold;");
        userTextF.setStyle("-fx-font-size: 14;-fx-font-weight: bold;");
        userTextF.setVisible(false);
//        userLbl.setMinHeight(userTextF.getMinHeight());
        StackPane.setMargin(userLbl, new Insets(0,0,0,8));
        StackPane userStackPane = new StackPane(userLbl,userTextF);
        userStackPane.setAlignment(Pos.TOP_LEFT);
//        System.out.println(userTextF.getMinHeight());

        ObservableList<String> userTypes = FXCollections.observableArrayList();
        userTypes.addAll("worker","admin","customer");

        Label rightsLbl = new Label("user privileges");
        ChoiceBox choicebox = new ChoiceBox(userTypes);
        choicebox.setValue("worker");
        choicebox.setMinHeight(22);
        choicebox.setPrefHeight(20);
//        choicebox.setDisable(true);
        choicebox.setMaxWidth(80);

        VBox rightsVbox = new VBox(2,rightsLbl, choicebox);
        rightsVbox.setDisable(true);
        //dropdown

        PasswordField passwPassF = new PasswordField();
        passwPassF.setText(userObj.getPassword());
        passwPassF.setPromptText("password");
        passwPassF.setMaxWidth(170);
        passwPassF.setEditable(false);
        TextField passwTextF = new TextField(userObj.getPassword());
        passwTextF.setPromptText("password");
        passwTextF.setMaxWidth(170);
        passwTextF.setEditable(false);
        StackPane passwStackPane = new StackPane(passwTextF, passwPassF);
        passwStackPane.setAlignment(Pos.TOP_LEFT);

        Label showPass = new Label("show");
        VBox.setMargin(showPass,new Insets(0,5,0,0));

        /** password functionality*/
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
                passwTextF.toFront();
                passwTextF.requestFocus();
                passwTextF.setText(passwPassF.getText());
                passwTextF.positionCaret(passwTextF.getText().length());

                showPass.setText("hide");
            } else{
                passwPassF.toFront();
                passwPassF.requestFocus();
                passwPassF.setText(passwTextF.getText());
                passwPassF.positionCaret(passwPassF.getText().length());

                showPass.setText("show");
            }
        });

        /** edit and save funcs */
        edit.setOnAction(e->{
            passwPassF.setEditable(true);
            passwTextF.setEditable(true);
            save.setDisable(false);
            userTextF.setVisible(true);
        });
        save.setOnAction(e->{
            System.out.println(userObj);
            //when saved need to check if smth changed, then update user, password and role in sql, then update in arraylist,
            // then update this user obj
            String newPassw = (showPass.getText().equals("show")?passwPassF.getText():passwTextF.getText()).strip();
            String newUsern = userTextF.getText().strip();

            if (newUsern.equals("")){


                return;
            }
            if (newPassw.equals("")){


                return;
            }
            //errors in updating passw or username
            if (userObj.wasCreated == false){
                //create in sql, then update obj
                try{
                    Connection conn = DriverManager.getConnection(MainPage.urll, MainPage.user, MainPage.passw);
                    Statement stmt = conn.createStatement();
                    String sqlCreate = "CREATE USER '"+newUsern+"'@'%' IDENTIFIED WITH mysql_native_password BY '"+newPassw+"'";
                    String sqlUpdate = "UPDATE mysql.user SET authentication_string = '"+newPassw+"' WHERE User = '"+newUsern+"';";

                    stmt.executeUpdate(sqlCreate);
                    stmt.executeUpdate(sqlUpdate);

                    stmt.close();
                    conn.close();
                    users.remove(0);
                    userObj.setWasCreated(true);
                    userObj.setUsername(newUsern);
                    userObj.setPassword(newPassw);
                    users.set(0,userObj);

                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

            } else
            /** updating password */
            if (!userObj.getPassword().equals(newPassw) || !userObj.getUsername().equals(newUsern)) {
                try {
                    Connection conn = DriverManager.getConnection(MainPage.urll, MainPage.user, MainPage.passw);
                    Statement stmt = conn.createStatement();
                    String sqlUpdate =
                        "UPDATE mysql.user SET " +
                            "authentication_string = '" + newPassw + "', " +
                            "user = '"+ newUsern+"'"+
                            "WHERE (User = '" + userObj.getUsername() + "') and (host='%')";

                    System.out.println(sqlUpdate+" ------ "+stmt.executeUpdate(sqlUpdate));
                    ;
                    stmt.close();
                    conn.close();

                    userObj.setUsername(newUsern);
                    userObj.setPassword(newPassw);
                } catch (SQLException exx) {
                    exx.printStackTrace();
                }
            } else{
                //no changes were made
            }
//            user = newUsern;




            passwPassF.toFront();
            showPass.setText("show");
            userLbl.setText(newUsern);

            passwPassF.setEditable(false);
            passwTextF.setEditable(false);
            save.setDisable(true);
            userTextF.setVisible(false);

            System.out.println(userObj);

        });

        VBox passwVbox = new VBox(3,passwStackPane, showPass);
        passwVbox.setMaxWidth(170);
        passwVbox.setAlignment(Pos.TOP_RIGHT);

        Label seeActivity = new Label("see user activity");
        seeActivity.setDisable(true);

        VBox leftVbox = new VBox(8, new HBox(20,userStackPane, rightsVbox), passwVbox, seeActivity);
        BorderPane.setMargin(leftVbox, new Insets(10));

        VBox rightVbox = new VBox(5,edit, delete, save);
        rightVbox.setAlignment(Pos.TOP_RIGHT);
        BorderPane.setMargin(rightVbox, new Insets(10));


        BorderPane pane = new BorderPane();
//        pane.setStyle("-fx-background-color: #e8e8e8;"); //this is for the background copy color
        pane.setBackground(new Background(new BackgroundFill(Color.web("#e8e8e8"), new CornerRadii(5), Insets.EMPTY)));
        pane.setBorder(new Border(new BorderStroke(Color.web("#b4b4b4"), BorderStrokeStyle.SOLID, new CornerRadii(5),
            new BorderWidths(2))));
        VBox.setMargin(pane,new Insets(12));

        pane.setLeft(leftVbox);
        pane.setRight(rightVbox);
        pane.setMinSize(350, 135);
        if (userObj.wasCreated == false){
            edit.fire();
        }


        return pane;
    }

    public void retrieveUsers(){
        try{
            Connection conn = DriverManager.getConnection(MainPage.urll, MainPage.user, MainPage.passw);
            Statement stmt = conn.createStatement();
//            String sqlCreate = "CREATE USER '"+usern+"'@'%' IDENTIFIED WITH mysql_native_password BY '"+password+"'";
//            String sqlUpdate = "UPDATE `mysql`.`user` SET `authentication_string` = '"+password+"' WHERE `User` = '"+usern+"';";

            ResultSet rs = stmt.executeQuery("select user,host,authentication_string from mysql.user where (host = '%') and (user != 'admin')  order by password_last_changed desc;");
            while (rs.next()){
                users.add(new User(rs.getString("user"), rs.getString("authentication_string"), true));
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}





