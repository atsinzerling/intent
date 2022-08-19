import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class ManageUsers extends Application {
    Stage manageUsersStage;
    ArrayList<User> users = new ArrayList<>();
    VBox usersVbox = null;

    @Override
    public void start(Stage secondStage) {
        retrieveUsers();

        usersVbox = generateUsersVbox();
        HBox.setMargin(usersVbox, new Insets(0, 0, 80, 0));

        Button addUserBtn = new Button("Add new user");
        addUserBtn.setMinSize(100, 40);
        HBox.setMargin(addUserBtn, new Insets(12, 12, 12, 0));


        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setPrefWidth(570);
        HBox scrollHbox = new HBox(10, usersVbox, addUserBtn);
        scrollPane.setContent(scrollHbox);

        BorderPane pane = new BorderPane();
        pane.setLeft(scrollPane);

        Scene scene = new Scene(pane, 700, 500);
        manageUsersStage = new Stage();
        manageUsersStage.setTitle("Manage Users Window");
        manageUsersStage.setScene(scene);
        manageUsersStage.initOwner(secondStage);
        manageUsersStage.initModality(Modality.WINDOW_MODAL);
        manageUsersStage.setResizable(true);
        manageUsersStage.show();

        addUserBtn.setOnAction(e -> {
            User newwUser = new User("", "", false);
            users.add(0, newwUser);
            usersVbox.getChildren().add(0, generateUserBox(newwUser));
        });
        Methods.boostScroll(scrollPane, scrollHbox, 50);

    }

    public void setUsersBox() {
        usersVbox = generateUsersVbox();
    }

    public VBox generateUsersVbox() {
        VBox vbox = new VBox();
        for (User user : users) {
            vbox.getChildren().add(generateUserBox(user));
//            System.out.println("added userbox for "+(user==null?"null":user));
        }
        return vbox;
    }

    public BorderPane generateUserBox(User userObj) {

        AtomicBoolean passwWasAltered = new AtomicBoolean(false);

        Button edit = new Button("Edit user");
        Button delete = new Button("Delete user");
        Button save = new Button("Save changes");
        Button cancel = new Button("Cancel");
        save.setDisable(true);
//        edit.setMaxHeight(5);
        edit.setPrefHeight(20);
        edit.setMinHeight(22);
        delete.setMinHeight(22);
        delete.setPrefHeight(20);
        save.setMinHeight(22);
        save.setPrefHeight(20);
        cancel.setMinHeight(22);
        cancel.setPrefHeight(20);
        cancel.setVisible(false);
        HBox saveCancelHbox = new HBox(5, cancel, save);
        VBox.setMargin(saveCancelHbox, new Insets(45, 0, 0, 0));

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
        StackPane.setMargin(userLbl, new Insets(0, 0, 0, 8));
        StackPane userStackPane = new StackPane(userLbl, userTextF);
        userStackPane.setAlignment(Pos.TOP_LEFT);

        ObservableList<String> userTypes = FXCollections.observableArrayList();
        userTypes.addAll("admin", "default");

        Label rightsLbl = new Label("user privileges");
        ChoiceBox<String> choicebox = new ChoiceBox<String>(userTypes);
        choicebox.setValue(Methods.isAdmin(userObj.getUsername()) ? "admin" : "default");
        choicebox.setMinHeight(22);
        choicebox.setPrefHeight(20);
        choicebox.setMaxWidth(80);


        VBox rightsVbox = new VBox(2, rightsLbl, choicebox);
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
        showPass.setDisable(true);
        VBox.setMargin(showPass, new Insets(0, 5, 0, 0));


        VBox passwVbox = new VBox(3, passwStackPane, showPass);
        passwVbox.setMaxWidth(170);
        passwVbox.setAlignment(Pos.TOP_RIGHT);

        Label currUserLbl = new Label((userObj.getUsername().equals(MainPage.user) ? "current user" : ""));
        VBox.setMargin(currUserLbl, new Insets(0, 0, 0, 8));
        Label seeActivity = new Label("see user activity");


        VBox leftVbox =
            new VBox(8, new HBox(20, new VBox(userStackPane, currUserLbl), rightsVbox), passwVbox, seeActivity);
        BorderPane.setMargin(leftVbox, new Insets(10));

        VBox rightVbox = new VBox(5, edit, delete, saveCancelHbox);
        rightVbox.setAlignment(Pos.TOP_RIGHT);
        BorderPane.setMargin(rightVbox, new Insets(10));


        BorderPane pane = new BorderPane();
//        pane.setStyle("-fx-background-color: #e8e8e8;"); //this is for the background copy color
        pane.setBackground(new Background(new BackgroundFill(Color.web("#e8e8e8"), new CornerRadii(5), Insets.EMPTY)));
        pane.setBorder(new Border(new BorderStroke(Color.web("#b4b4b4"), BorderStrokeStyle.SOLID, new CornerRadii(5),
            new BorderWidths(2))));
        VBox.setMargin(pane, new Insets(12));

        pane.setLeft(leftVbox);
        pane.setRight(rightVbox);
        pane.setMinSize(350, 135);


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
        seeActivity.setOnMouseClicked(e -> {
            new UserActivity(userObj.getUsername()).start(manageUsersStage);
        });

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
            } else {
                passwPassF.toFront();
                passwPassF.requestFocus();
                passwPassF.setText(passwTextF.getText());
                passwPassF.positionCaret(passwPassF.getText().length());

                showPass.setText("show");
            }
        });

        /** edit and save funcs */
        edit.setOnAction(e -> {
            passwPassF.setOnMousePressed((eee) -> {

                showPass.setDisable(false);
                passwPassF.setText("");
                passwTextF.setText("");
                passwPassF.setPromptText("type new password");
                passwTextF.setPromptText("type new password");
                showPass.setDisable(false);
                passwPassF.setOnMousePressed((eeee) -> {

                });
                passwWasAltered.set(true);
            });

            cancel.setVisible(true);
            passwPassF.setEditable(true);
            passwTextF.setEditable(true);
            save.setDisable(false);
            userTextF.setVisible(true);
            rightsVbox.setDisable(false);
        });

        save.setOnAction(e -> {

            //when saved need to check if smth changed, then update user, password and role in sql, then update in arraylist,
            // then update this user obj
            String newPassw = (showPass.getText().equals("show") ? passwPassF.getText() : passwTextF.getText()).strip();
            String newUsern = userTextF.getText().strip();

            if (newUsern.equals("")) {
                Methods.errorAlert("Empty username",
                    "Cannot " + (userObj.wasCreated ? "save changes to" : "create") + " user with empty username.");
                return;
            }
            if (choicebox.getValue().equals("admin")) {
                if (!Methods.isAdmin(newUsern)) {
                    Methods.errorAlert("Username format error", "Username of admin must end with \"/A\".");
                    return;
                }
            } else {
                if (newUsern.toLowerCase().contains("/a")) {
                    Methods.errorAlert("Username format error", "Username of default user must not contain \"/A\".");
                    return;
                }
            }
            if (newPassw.equals("")) {
                Methods.errorAlert("Empty password",
                    "Cannot " + (userObj.wasCreated ? "save changes to" : "create") + " user with empty password.");
                return;
            }
            //errors in updating passw or username
            if (!userObj.wasCreated) {
                //create in sql, then update obj
                try {
                    Connection conn = DriverManager.getConnection(MainPage.urll, MainPage.user, MainPage.passw);
                    Statement stmt = conn.createStatement();
                    String sqlCreate =
                        "CREATE USER '" + newUsern + "'@'%' IDENTIFIED WITH mysql_native_password BY '" + newPassw +
                            "'";
//                    String sqlUpdate = "UPDATE mysql.user SET authentication_string = '"+newPassw+"' WHERE User = '"+newUsern+"';";

                    stmt.executeUpdate(sqlCreate);
                    stmt.execute(new String("FLUSH PRIVILEGES;"));
                    String granttt;
                    if (!choicebox.getValue().equals("admin")) {
                        granttt = "GRANT SELECT, INSERT, DELETE, UPDATE ON " + MainPage.schema + ".* TO '" + newUsern +
                            "'@'%';";
                    } else {
                        granttt =
                            "GRANT SELECT,INSERT, DELETE, UPDATE, CREATE USER, ALTER, RELOAD ON *.* TO '" + newUsern +
                                "'@'%' WITH GRANT OPTION;";
                    }
                    stmt.executeUpdate(granttt);
                    stmt.execute(new String("FLUSH PRIVILEGES;"));

//                    stmt.execute(new String("SET GLOBAL authentication_policy='mysql_native_password';"));
//                    stmt.execute(new String("FLUSH PRIVILEGES;"));

                    System.out.println("\t SQL " + sqlCreate + " | " + granttt);
                    stmt.close();
                    conn.close();
                    userObj.setWasCreated(true);
                    userObj.setUsername(newUsern);
                    userObj.setPassword(newPassw);
                    Methods.updateUserLog(MainPage.user, "created user " + newUsern);

                    edit.setDisable(false);
                    delete.setDisable(false);

                } catch (SQLException ex) {
                    MainPage.databaseErrorAlert(ex).showAndWait();
                    return;
                }

            } else
            /** updating password and username */
                if (!userObj.getPassword().equals(newPassw) || !userObj.getUsername().equals(newUsern)) {
                    try {
                        Connection conn = DriverManager.getConnection(MainPage.urll, MainPage.user, MainPage.passw);
                        Statement stmt = conn.createStatement();
                        String sqlUpdate =
                            "RENAME USER '" + userObj.getUsername() + "'@'%' TO '" + newUsern + "'@'%';";

                        String sqlOAOA = "ALTER USER '" + newUsern + "'@'%'" +
                            " IDENTIFIED WITH mysql_native_password" +
                            " BY '" + newPassw + "';";

                        if (!userObj.getUsername().equals(newUsern)) {
                            stmt.execute(sqlUpdate);
                            stmt.execute(new String("FLUSH PRIVILEGES;"));
                            System.out.println("\tSQL " + sqlUpdate);

                            ResultSet rs = stmt.executeQuery(
                                "SELECT * FROM " + MainPage.schema + ".useractions WHERE User='" +
                                    userObj.getUsername() + "'");
                            int coun = 0;
                            conn.setAutoCommit(false);
                            while (rs.next()) {
                                stmt.addBatch(
                                    "UPDATE " + MainPage.schema + ".useractions SET User='" + newUsern +
                                        "' WHERE (User, Date) = ('" +
                                        userObj.getUsername() + "','" + rs.getDate("Date") + "')");
                                coun++;
                            }
                            stmt.executeBatch();
                            conn.commit();
                            conn.setAutoCommit(true);
                            if (userObj.getUsername().equals(MainPage.user)) {
                                MainPage.user = newUsern;
                                MainPage.loggedInLbl.setText("logged in as " + MainPage.user);
                            }
                            System.out.println("updated " + coun + " records in .useractions");

                            Methods.updateUserLog(MainPage.user, "changed username for user " + userObj.getUsername());
                        }
                        if (!userObj.getPassword().equals(newPassw)) {
                            stmt.execute(sqlOAOA);
                            stmt.execute(new String("FLUSH PRIVILEGES;"));
//                            stmt.execute(new String("SET GLOBAL authentication_policy='mysql_native_password';"));
//                            stmt.execute(new String("FLUSH PRIVILEGES;"));
                            if (userObj.getUsername().equals(MainPage.user)) {
                                MainPage.passw = newPassw;
                            }
                            System.out.println("\tSQL " + sqlOAOA);
                            Methods.updateUserLog(MainPage.user, "changed password for user " + newUsern);
                        }

                        if (Methods.isAdmin(userObj.getUsername()) && !Methods.isAdmin(newUsern)) {
                            String granttt = "REVOKE ALL PRIVILEGES, GRANT OPTION FROM '" + newUsern + "'@'%';";
                            stmt.execute(granttt);
                            stmt.execute(new String("FLUSH PRIVILEGES;"));

                            String grantt =
                                "GRANT SELECT, INSERT, DELETE,UPDATE ON " + MainPage.schema + ".* TO '" + newUsern +
                                    "'@'%';";
                            stmt.executeUpdate(grantt);
                            stmt.execute(new String("FLUSH PRIVILEGES;"));

                        } else if (!Methods.isAdmin(userObj.getUsername()) && Methods.isAdmin(newUsern)) {
                            String granttt =
                                "GRANT SELECT,INSERT, DELETE, UPDATE, CREATE USER, ALTER, RELOAD ON *.* TO '" +
                                    newUsern + "'@'%' WITH GRANT OPTION;";
                            stmt.executeUpdate(granttt);
                            stmt.execute(new String("FLUSH PRIVILEGES;"));
                        }

//                        System.out.println("\tSQL "+grantt);

                        stmt.close();
                        conn.close();

                        userObj.setUsername(newUsern);
                        userObj.setPassword(newPassw);
                    } catch (SQLException exx) {
                        MainPage.databaseErrorAlert(exx).showAndWait();
                        return;
                    }
                } else {
                    //no changes were made
                }


            passwPassF.setOnMousePressed((ee) -> {

            });

            cancel.setVisible(false);
            passwPassF.toFront();
            showPass.setText("show");
            showPass.setDisable(true);
            userLbl.setText(newUsern);

            passwPassF.setEditable(false);
            passwTextF.setEditable(false);
            save.setDisable(true);
            userTextF.setVisible(false);
            rightsVbox.setDisable(true);

            System.out.println(userObj);

        });
        delete.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Deletion Confirmation");
//            alert.setHeaderText("Look, a Confirmation Dialog");
            alert.setContentText("Are you sure you want to delete user " + userObj.getUsername() + "?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                try {
                    Connection conn = DriverManager.getConnection(MainPage.urll, MainPage.user, MainPage.passw);
                    Statement stmt = conn.createStatement();
                    String sqlDelete = "DROP User '" + userObj.getUsername() + "'@'%';";
//                String sqlUpdate = "UPDATE mysql.user SET authentication_string = '"+newPassw+"' WHERE User = '"+newUsern+"';";

                    stmt.executeUpdate(sqlDelete);
//                stmt.executeUpdate(sqlUpdate);

                    stmt.close();
                    conn.close();

                    usersVbox.getChildren().remove(users.indexOf(userObj));
                    users.remove(userObj);

                    Methods.updateUserLog(MainPage.user, "deleted user " + userObj.getUsername());

                } catch (SQLException ex) {
                    MainPage.databaseErrorAlert(ex).showAndWait();
                    return;
                }
            }
        });
        cancel.setOnAction(e -> {

            cancel.setVisible(false);
            passwPassF.toFront();
            showPass.setText("show");
            showPass.setDisable(true);
            userLbl.setText(userObj.getUsername());
            passwPassF.setText(userObj.getPassword());
            rightsVbox.setDisable(true);


            passwPassF.setEditable(false);
            passwTextF.setEditable(false);
            save.setDisable(true);
            userTextF.setVisible(false);
            if (userObj.wasCreated == false) {
                usersVbox.getChildren().remove(users.indexOf(userObj));
                users.remove(userObj);
            }
        });

        choicebox.getSelectionModel().selectedIndexProperty().addListener(
            (ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
//                System.out.println(userTypes.get((int) new_val)+" "+new_val);
//                System.out.println(userTextF.getText());
                String textt = userTextF.getText();
                if ((int) new_val == 0) { //admin
                    if (!textt.contains("/")) {
                        userTextF.setText(textt + "/A");
                    }
                } else {
                    if (Methods.isAdmin(textt)) {
                        userTextF.setText(textt.substring(0, textt.length() - 2));
                    }
                }
            });

        if (!userObj.wasCreated) {
            edit.fire();
            edit.setDisable(true);
            delete.setDisable(true);
        }
        if (userObj.getUsername().equals("admin")
//            || userObj.getUsername().equals(MainPage.user)
        ) {
            edit.setDisable(true);
            delete.setDisable(true);
        }


        return pane;
    }

    public void retrieveUsers() {
        try {
            Connection conn = DriverManager.getConnection(MainPage.urll, MainPage.user, MainPage.passw);
            Statement stmt = conn.createStatement();
//            String sqlCreate = "CREATE USER '"+usern+"'@'%' IDENTIFIED WITH mysql_native_password BY '"+password+"'";
//            String sqlUpdate = "UPDATE `mysql`.`user` SET `authentication_string` = '"+password+"' WHERE `User` = '"+usern+"';";

            ResultSet rs =
                stmt.executeQuery("select user,host,authentication_string from mysql.user where (host = '%')" +
//                " and (user != 'admin')" +
                    "  order by password_last_changed desc;");
            while (rs.next()) {
                users.add(new User(rs.getString("user"), rs.getString("authentication_string"), true));
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            MainPage.databaseErrorAlert(e).showAndWait();
            return;
        }
    }
}





