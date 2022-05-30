import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ManageUsers extends Application {


//    public ManageUsers(Image imgg) {
//        this.imgg = imgg;
//    }

    @Override
    public void start(Stage secondStage) {

//        ImageView imgview = new ImageView(imgg);
//        imgview.setFitHeight(700);
//        imgview.setFitWidth(1244);
//        imgview.setPreserveRatio(true);
        Button addUserBtn = new Button("Add user");

        VBox usersVbox = new VBox(5,generateUsers());




        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setPrefWidth(500);
//        scrollPane.setPrefHeight();
        scrollPane.setContent(new HBox(20, usersVbox, addUserBtn));

        BorderPane pane = new BorderPane();

        Scene scene = new Scene(pane, 600, 400);
        Stage manageUsersStage = new Stage();
        manageUsersStage.setTitle("Manage Users Window");
        manageUsersStage.setScene(scene);
        manageUsersStage.initOwner(secondStage);
        manageUsersStage.initModality(Modality.WINDOW_MODAL);
        manageUsersStage.setResizable(true);
        manageUsersStage.show();
//        scene.setOnKeyPressed((KeyEvent e) -> {
//            if (e.getCode() == KeyCode.ESCAPE) {
//                manageUsersStage.close();
//            }
//        });
    }
    public VBox generateUsers(){

        return null;
    }
    public BorderPane generateUserBox(String user, String role, String password){
        BorderPane pane = new BorderPane();
//        pane.setStyle("-fx-background-color: #e8e8e8;"); //this is for the background copy color
        pane.setBackground(new Background(new BackgroundFill(Color.web("#e8e8e8"), new CornerRadii(5), Insets.EMPTY)));
        pane.setBorder(new Border(new BorderStroke(Color.web("#b4b4b4"), BorderStrokeStyle.SOLID, new CornerRadii(5),
            new BorderWidths(2))));
        VBox.setMargin(pane,new Insets(30));

        Button edit = new Button("Edit");
        Button delete = new Button("Delete user");
        Button save = new Button("Save changes");

        Label userLbl = new Label(user);
        TextField userTextF = new TextField(user);
        StackPane userStackPane = new StackPane(userTextF, userLbl);

        Label rightsLbl = new Label("rights");
        //dropdown

        PasswordField passwPassF = new PasswordField();
        passwPassF.setText(password);
        passwPassF.setPromptText("password");
        TextField passwTextF = new TextField(password);
        passwTextF.setPromptText("password");
        StackPane passwStackPane = new StackPane(passwTextF, passwPassF);

        Label seeActivity = new Label("see activity");
        seeActivity.setVisible(false);

        return null;
    }
}
