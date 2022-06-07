import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

//import java.awt.TextArea;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.util.Duration;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;

public class AddItem extends Application {

    Label status = new Label("");
    Label imageStatus = new Label("");
    String windowMode = "adding";
    Item currItem = null;
    ArrayList<Image> images = new ArrayList<>();
    String initDirectory =
        "\\\\DESKTOP-E5VI6AD\\application\\Images";
//        "D:\\Mine stuff\\Compiling\\Images";
    String user = "";

    public AddItem(String windowMode, Item currItem, String user) {
        this.windowMode = windowMode;
        this.currItem = currItem;
        this.user = user;
    }

    public void start(Stage primaryStage) {

        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);
        GridPane rightGrid = new GridPane();
        rightGrid.setVgap(7);

        grid.add(new Label("SKU:"), 0, 0);
        grid.add(new Label("SN:"), 0, 1);
        grid.add(new Label("PN:"), 0, 2);
        grid.add(new Label("UPC:"), 0, 3);
        grid.add(new Label("Grade:"), 0, 4);
        grid.add(new Label("Location:"), 0, 5);
        grid.add(new Label("PO number:"), 0, 6);
        rightGrid.add(new Label("Notes:"), 0, 2);
        rightGrid.add(new Label("Specs:"), 0, 0);

        Label createdBy = new Label("");
        GridPane.setMargin(createdBy, new Insets(8,0,0,0));
        grid.add(createdBy, 0,7, 2,1);

        TextField SKUField = new TextField();
        TextField SNField = new TextField();
        TextField PNField = new TextField();
        TextField UPCField = new TextField();
        TextField gradeField = new TextField();
        TextField locField = new TextField();
        TextField POnumField = new TextField();
        TextArea notesArea = new TextArea();
        TextArea specsArea = new TextArea();

//        notesArea.setPrefHeight(40);

        notesArea.setPrefWidth(250);
        notesArea.setPrefRowCount(5);
        specsArea.setPrefWidth(250);
        specsArea.setPrefRowCount(2);

//        notesArea.setPrefColumnCount(15);

        grid.add(SKUField, 1, 0);
        grid.add(SNField, 1, 1);
        grid.add(PNField, 1, 2);
        grid.add(UPCField, 1, 3);
        grid.add(gradeField, 1, 4);
        grid.add(locField, 1, 5);
        grid.add(POnumField, 1, 6);
//        VBox vbox = new VBox(notesArea);
        rightGrid.add(notesArea, 0, 3);
        rightGrid.add(specsArea, 0, 1);

        TextArea historyArea = new TextArea();
        historyArea.setMaxWidth(320);
        historyArea.setPrefRowCount(11);
        historyArea.setEditable(false);
        historyArea.setWrapText(true);

        VBox historyVbox = new VBox(5, new Label("History of changes to the Item:"), historyArea);
        historyVbox.setVisible(false);

        Button save = new Button("Add Item");
        Button cancel = new Button("Cancel");
        HBox hbox = new HBox(10, historyVbox, cancel, save);
        hbox.setAlignment(Pos.TOP_LEFT);


        hbox.setStyle("-fx-padding: 0 0 10 0; "
//            +"-fx-background-color: #dbdbff;"
        );
        GridPane.setMargin(hbox, new Insets(50,0,0,0));
        HBox.setMargin(historyArea, new Insets(0,20,0,0));

        Button edit = new Button("Edit");
        edit.setVisible(false);

        HBox editButHBox = new HBox();
        editButHBox.setAlignment(Pos.CENTER_RIGHT);
        editButHBox.getChildren().addAll(edit);
        editButHBox.setStyle("-fx-padding: 5 10 0 0; "
//            +"-fx-background-color: #dbdbff;"
        );


        grid.setStyle("-fx-padding: 10 0 0 30; "
//            +"-fx-background-color: #dbdbff;"
        );
        rightGrid.setStyle("-fx-padding: 12 0 0 20;"
//            +"-fx-background-color: #8989ff;"
        );


        BorderPane inputpane = new BorderPane();
//        inputpane.alignment="CENTER";
        rightGrid.add(hbox, 0,4);
        inputpane.setCenter(rightGrid);
//        inputpane.setAlignment(rightGrid, Pos.TOP_LEFT);
        inputpane.setLeft(grid);
        inputpane.setBottom(hbox);
        BorderPane.setMargin(hbox, new Insets(50, 10, 10, 30));

        status.setAlignment(Pos.CENTER);
        status.setMaxWidth(Double.MAX_VALUE);

        imageStatus.setAlignment(Pos.CENTER);
        HBox statusHbox = new HBox(60, status,imageStatus);
        statusHbox.setAlignment(Pos.CENTER);


        BorderPane pane = new BorderPane();
        pane.setCenter(inputpane);
//        pane.setBottom(hbox);
        pane.setTop(new VBox(editButHBox, statusHbox));


        Scene secondScene = new Scene(pane, 1000, 600);
        Stage newWindow = new Stage();
//        newWindow.setMaxWidth(800);
        newWindow.setTitle("Add New Item");
        newWindow.setScene(secondScene);
        newWindow.initOwner(primaryStage);
        newWindow.initModality(Modality.WINDOW_MODAL);

        newWindow.setX(primaryStage.getX() + primaryStage.getWidth() / 2 - secondScene.getWidth() / 2);
        newWindow.setY(primaryStage.getY() + primaryStage.getHeight() / 2 - secondScene.getHeight() / 2);


        newWindow.show();



        /** imagepane */

        BorderPane imageBlockPane = new BorderPane();

        Label dragDropLbl = new Label("drag and drop images");
        dragDropLbl.setMinSize(200, 80);
//        dragDropLbl.setStyle("-fx-background-color: #b4b4b4;"); //this is for the background copy color
//        dragDropLbl.setStyle("-fx-background-color: #d5d5d5;"+
////                "-fx-border-radius: 10; " +
////                "-fx-border-color: #5f5f5f;" +
////                "-fx-border-width: 3 ;" +
//            "");
        dragDropLbl.setBackground(new Background(new BackgroundFill(Color.web("#d5d5d5"), new CornerRadii(10), Insets.EMPTY)));
        dragDropLbl.setBorder(new Border(new BorderStroke(Color.web("#b4b4b4"), BorderStrokeStyle.SOLID, new CornerRadii(10),
            BorderWidths.DEFAULT)));
        dragDropLbl.setAlignment(Pos.CENTER);
        dragDropLbl.setOnMouseEntered(e -> {
            dragDropLbl.setCursor(Cursor.HAND);
        });

        Button updateImages = new Button("Update Images");
        updateImages.setVisible(false);
        HBox dragHbox = new HBox(dragDropLbl, updateImages);
        HBox.setMargin(updateImages, new Insets(0, 0, 10, 30));
        dragHbox.setAlignment(Pos.BOTTOM_LEFT);
        Label directr = new Label("Current directory: "+initDirectory+"\\");
        imageBlockPane.setTop(new VBox(10, dragHbox, directr));
        imageBlockPane.setMargin(dragDropLbl, new Insets(10, 10, 10, 10));


        class imageAdditMethods{
            private void configureFileChooser(
                final FileChooser fileChooser) {
                fileChooser.setTitle("Choose Pictures");
                fileChooser.setInitialDirectory(
                    new File(System.getProperty("user.home"))
                );
                fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("All Images", "*.*"),
                    new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                    new FileChooser.ExtensionFilter("PNG", "*.png")
                );
            }
            FlowPane generateImages(){
                FlowPane vbox = new FlowPane();
                vbox.setPrefWidth(450);
                if (images != null) {
                    for (Image imgg : images) {
                        Rectangle rectangle = new Rectangle(0, 0, 120, 80);
                        rectangle.setArcWidth(10.0);   // Corner radius
                        rectangle.setArcHeight(10.0);

//                        System.out.println(fl.getAbsolutePath());
                        ImagePattern pattern = new ImagePattern(imgg);

                        rectangle.setFill(pattern);
                        rectangle.setStrokeType(StrokeType.OUTSIDE);
                        rectangle.setStrokeWidth(0.5);
                        rectangle.setStroke(Color.BLACK);
                        rectangle.setEffect(new DropShadow(2, Color.BLACK));  // Shadow
                        StackPane imgWrap = new StackPane(rectangle);
                        imgWrap.setMaxSize(rectangle.getWidth(), rectangle.getHeight());
//                            imgWrap.setStyle("-fx-background-color: #ffb3b3;");
                        rectangle.setOnMouseClicked(e ->{
                            new ImageWindow(imgg).start(primaryStage);
                        });

                        if (!windowMode.equals("previewing")){
                            Label del = new Label("x");
//                                del.setStyle("-fx-background-color: rgba(231,231,231,0.76);");
                            del.setMaxSize(30, 10);
                            del.setAlignment(Pos.CENTER);

                            del.setBackground(new Background(new BackgroundFill(Color.rgb(231,231,231,0.76), new CornerRadii(3), Insets.EMPTY)));
                            del.setBorder(new Border(new BorderStroke(Color.web("#000000FF"), BorderStrokeStyle.SOLID, new CornerRadii(3),
                                new BorderWidths(0.3))));
                            imgWrap.setAlignment(Pos.TOP_RIGHT);
                            del.setFont(Font.font("Arial", FontWeight.BOLD, 15));
                            imgWrap.getChildren().add(del);
                            del.setOnMouseClicked(e -> {
                                images.remove(imgg);
                                imageBlockPane.setCenter(generateImages());
//                                    pane.setRight(imageBlockPane);
                            });
                            del.setOnMouseEntered(e -> {
                                del.setCursor(Cursor.HAND);
                            });
                        }

                        vbox.getChildren().add(imgWrap);
                        FlowPane.setMargin(imgWrap, new Insets(6));

                    }
                }
                return vbox;
            }
            /** methods for save button, only in editing and adding*/

            void saveOnActionImages(){
                if (windowMode.equals("previewing")){
                    return;
                }


                int count = 1;
                Thread[] thrds = new Thread[images.size()];
                AtomicBoolean allSaved = new AtomicBoolean(true);

                String directory = initDirectory+"\\"+ currItem.SKU;
                try {
                    Files.createDirectories(Paths.get(directory));
                    FileUtils.cleanDirectory(new File(directory));
                } catch (IOException e) {
                    MainPage.ioErrorAlert(e).showAndWait();
                    return;
                }

                for (Image imgg: images){
                    int finalCount = count;
                    Thread newThread = new Thread(() -> {
                        try {
                            System.out.println("started "+finalCount);
//                            File directoryFile = new File(directory);
//                            if (directoryFile.exists()){
//                                FileUtils.cleanDirectory(directoryFile);
//                            } else {
//                                Files.createDirectories(Paths.get(directory));
//                            }
                            File saveTo = new File(directory+"\\"
                                + currItem.SKU +"_" + String.format("%02d", finalCount) + ".jpg"
                            );
                            BufferedImage buffImg = SwingFXUtils.fromFXImage(imgg, null);
                            ImageIO.write(buffImg , "jpg", saveTo);
                            System.out.println(finalCount+" saved");


                        } catch (IOException ex) {
                            ex.printStackTrace();
                            allSaved.set(false);
                        }
                    });
                    thrds[count-1] = newThread;
                    newThread.start();
                    count++;
                }

                try{
                    for (Thread thr : thrds) {
                        thr.join();
                    }
                }catch (InterruptedException ex) {
                    ex.printStackTrace();
                    allSaved.set(false);
                }
                if (allSaved.get()){
                    setImageStatus("Images saved!", 3, "green");
                } else{
                    setImageStatus("There was an error saving images", 3, "red");
                }

            }
            void loadImagesFromDir(){
                File dir = new File(initDirectory+"\\"+currItem.SKU);
                if (dir.exists()){
                    File[] fls = dir.listFiles();
                    Thread[] thrds = new Thread[fls.length];
                    images = new ArrayList<Image>();
                    for (int i = 0; i< fls.length; i++){


                        int finalI = i;
                        Thread newThread = new Thread(() -> {
                            images.add(new Image(fls[finalI].toURI().toString()));
                        });
                        thrds[i] = newThread;
                        newThread.start();

//                        images.add(new Image(fls[i].toURI().toString()));
                    }

                    try {
                        for (Thread thr: thrds){
                            thr.join();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    imageBlockPane.setCenter(new imageAdditMethods().generateImages());
//                    pane.setRight(imageBlockPane);
                }
            }

        }
        imageBlockPane.setCenter(new imageAdditMethods().generateImages());


        FileChooser fil_chooser = new FileChooser();

        dragDropLbl.setOnDragOver((EventHandler) e -> {
            if (e instanceof DragEvent){
                DragEvent event = (DragEvent) e;
                if (event.getDragboard().hasFiles()) {
                    event.acceptTransferModes(TransferMode.ANY);
                }
                event.consume();
            }
        });
        dragDropLbl.setOnDragDropped((EventHandler) e -> {
            System.out.println("dropped");
            System.out.println(e instanceof DragEvent);
            if (e instanceof DragEvent){
                DragEvent event = (DragEvent) e;
                for (File fl: event.getDragboard().getFiles()){
                    images.add(new Image(fl.toURI().toString()));
                }
                System.out.println("Got " + images.size() + " files");
                imageBlockPane.setCenter(new imageAdditMethods().generateImages());
//                pane.setRight(imageBlockPane);
//                newWindow.setHeight(secondScene.getHeight()+200);
                event.consume();
            }
        });

        dragDropLbl.setOnMousePressed((e) ->{
            new imageAdditMethods().configureFileChooser(fil_chooser);
            List<File> files = fil_chooser.showOpenMultipleDialog(newWindow);
            if (files != null) {
                for (File fl : files) {
                    images.add(new Image(fl.toURI().toString()));
                }
                System.out.println("Got " + images.size() + " files");

                imageBlockPane.setCenter(new imageAdditMethods().generateImages());
//                pane.setRight(imageBlockPane);
            }
        });
        updateImages.setOnAction(e ->{
            new imageAdditMethods().saveOnActionImages();
        });

        pane.setRight(imageBlockPane);
        BorderPane.setMargin(imageBlockPane, new Insets(0,0,0, 10));
        /** images ended*/



        class additMethods {

            private String notEditableColorCSS = "-fx-text-inner-color: #5f5f5f;";
            private String editableColorCSS = "-fx-text-inner-color: #000000;";

            void setEditing() {
                newWindow.setTitle("Edit an Item");
                windowMode = "editing";

                SKUField.setEditable(false);
                SNField.setEditable(true);
                PNField.setEditable(true);
                UPCField.setEditable(true);
                gradeField.setEditable(true);
                locField.setEditable(true);
                POnumField.setEditable(true);
                notesArea.setEditable(true);
                specsArea.setEditable(true);

                SKUField.setStyle(notEditableColorCSS);
                SNField.setStyle(editableColorCSS);
                PNField.setStyle(editableColorCSS);
                UPCField.setStyle(editableColorCSS);
                gradeField.setStyle(editableColorCSS);
                locField.setStyle(editableColorCSS);
                POnumField.setStyle(editableColorCSS);
                notesArea.setStyle(editableColorCSS);
                specsArea.setStyle(editableColorCSS);

                SKUField.setText(Long.toString(currItem.SKU));
                SNField.setText(currItem.SN);
                PNField.setText(currItem.PN);
                UPCField.setText(currItem.UPC);
                gradeField.setText(currItem.Grade);
                locField.setText(currItem.Location);
                POnumField.setText(currItem.POnumber);
                notesArea.setText(currItem.Notes);
                specsArea.setText(currItem.Specs);

                historyVbox.setVisible(false);

                save.setVisible(true);
                save.setText("Save Changes");
                save.setOnAction(e -> {
                    saveChangesAction();
                });
                cancel.setVisible(true);
                cancel.setText("Exit");
                cancel.setOnAction(e -> {
                    exitConfirmAction("exit");
                });
                edit.setVisible(true);
                edit.setText("Back to Preview");
                edit.setOnAction(e -> {
                    exitConfirmAction("preview");
                });

//                dragDropLbl.setVisible(true);
                dragDropLbl.setDisable(false);
                updateImages.setVisible(true);
                directr.setText("Current directory: "+initDirectory+"\\"+ currItem.SKU+ "\\");

                imageBlockPane.setCenter(new imageAdditMethods().generateImages());
//                pane.setRight(imageBlockPane);

            }

            void setPreviewing() {
                newWindow.setTitle("Preview an Item");
                windowMode = "previewing";


                SKUField.setEditable(false);
                SNField.setEditable(false);
                PNField.setEditable(false);
                UPCField.setEditable(false);
                gradeField.setEditable(false);
                locField.setEditable(false);
                POnumField.setEditable(false);
                notesArea.setEditable(false);
                specsArea.setEditable(false);

                SKUField.setStyle(notEditableColorCSS);
                SNField.setStyle(notEditableColorCSS);
                PNField.setStyle(notEditableColorCSS);
                UPCField.setStyle(notEditableColorCSS);
                gradeField.setStyle(notEditableColorCSS);
                locField.setStyle(notEditableColorCSS);
                POnumField.setStyle(notEditableColorCSS);
                notesArea.setStyle(notEditableColorCSS);
                specsArea.setStyle(notEditableColorCSS);

                SKUField.setText(Long.toString(currItem.SKU));
                SNField.setText(currItem.SN);
                PNField.setText(currItem.PN);
                UPCField.setText(currItem.UPC);
                gradeField.setText(currItem.Grade);
                locField.setText(currItem.Location);
                POnumField.setText(currItem.POnumber);
                notesArea.setText(currItem.Notes);
                specsArea.setText(currItem.Specs);

                historyArea.setText((currItem.OtherRecords!=null?"   "+currItem.OtherRecords.replaceAll("\n","\t").replaceAll("<<<:::===", "\n\n   "):""));
                historyVbox.setVisible(true);

                save.setVisible(false);

                cancel.setVisible(true);
                cancel.setText("Exit");
                cancel.setOnAction(e -> {
                    newWindow.close();
                    setSelectionOfCurrItem();
                    //write some code to saving changes lol
                });

                edit.setVisible(true);
                edit.setText("Edit");
                edit.setOnAction(e -> {
                    setEditing();
                });
//                if (!user.equals("admin") && !user.equals(currItem.User)){
//                    edit.setDisable(true);
//                }
                //add field that was created by user
                createdBy.setText("Item created by "+currItem.User);

                System.out.println(currItem);
                updateFields();

//                dragDropLbl.setVisible(false);
                dragDropLbl.setDisable(true);
                updateImages.setVisible(false);
                directr.setText("Current directory: "+initDirectory+"\\"+ currItem.SKU+ "\\");

//                pane.setRight(imageBlockPane);
            }

            /**
             * Button Actions
             */

            void addItemAction() {
                try {
                    // I "manually" handle sku = null and sku containing text, and I have to handle uniqueness of sku and sn with try catch

                    //check if text=="", text is number, and then that its 6-digit

                    long SKU = 0;
                    boolean SKUmessage = false;
                    String SKUfieldText = SKUField.getText().strip();
                    if (SKUfieldText== "") {
//                        setStatus("Cannot have empty SKU", 5, "red");
//                        setImageStatus("", 0, "");
                        SKUmessage = true;
//                        return;
                    }else {
                        try {
                            SKU = Integer.parseInt(SKUfieldText);

                            if (SKUfieldText.length()!=6 || SKUfieldText.charAt(0)=='0'){
                                SKUmessage = true;
                            }
                        } catch (NumberFormatException eeeeeerocks) {
//                            setStatus("SKU should be a number", 5, "red");
//                            setImageStatus("", 0, "");
                            SKUmessage = true;
//                            return;
                        }
                    }

                    if (SKUmessage){
                        setStatus("SKU should be a 6-digit number without leading zeros", 5, "red");
                        setImageStatus("", 0, "");
                        return;
                    }

//                String SKU = SKUFieled.getText();
                    Connection conn = DriverManager.getConnection(MainPage.urll, MainPage.user, MainPage.passw);
//                PreparedStatement pstmt = conn.prepareStatement("INSERT INTO items(SKU,SN,PN,UPC,Grade,Location,Notes,User,DateTime,Images,OtherRecords) VALUES(?,?,?,?,?,?,?,?,?,?,?)");
//                pstmt.setInt(1, SKU);
//                pstmt.setString(2, SNFieled.getText());
//                pstmt.setString(3, PNFieled.getText());
//                pstmt.setString(4, UPCFieled.getText());
//                pstmt.setString(5, gradeFieled.getText());
//                pstmt.setString(6, locFieled.getText());
//                pstmt.setString(7, notesArea.getText());
//                pstmt.setString(8, "undefined user");
////                pstmt.setTimestamp(9, new Timestamp(System.currentTimeMillis()));
//                pstmt.setString(10, "");
//                pstmt.setString(11, "");
//                pstmt.executeUpdate();
                    //can make this shorter, do that later
                    Statement stmt = conn.createStatement();
//                    System.out.println((SNField.getText() == ""));
//                    System.out.println((SNField.getText().equals("") ? null :  "'"+SNField.getText() + "'"));
                    String values =
                        "'" + SKU + "'" + "," +
                        (SNField.getText().strip().equals("") ? null : "'" + SNField.getText().strip() + "'") + "," +
                        (PNField.getText().strip().equals("") ? null : "'" + PNField.getText().strip() + "'") + "," +
                        (UPCField.getText().strip().equals("") ? null : "'" + UPCField.getText().strip() + "'") + "," +
                        (gradeField.getText().strip().equals("") ? null : "'" + gradeField.getText().strip() + "'") + "," +
                        (locField.getText().strip().equals("") ? null : "'" + locField.getText().strip() + "'") + "," +
                        (notesArea.getText().strip().equals("") ? null : "'" + notesArea.getText().strip() + "'") + "," +
                        "'" + MainPage.user + "'" + "," +
                        "'" + new Timestamp(System.currentTimeMillis()) + "'" + "," +
                        null + "," +
                        null + "," +
                        "'" + new Timestamp(System.currentTimeMillis()) + "'" + "," +
                        (POnumField.getText().strip().equals("") ? null : "'" + POnumField.getText().strip() + "'") + "," +
                        (specsArea.getText().strip().equals("") ? null : "'" + specsArea.getText().strip() + "'");
                    String sql = ("INSERT INTO items " +
//                        "(SKU,SN,PN,UPC,Grade,Location,Notes,User,DateTime,Images,OtherRecords) " +
                        "VALUES (" + values + ")");

//                        "\n"+
//                        "UPDATE items SET DateTime='" + new Timestamp(System.currentTimeMillis()) + "' WHERE SKU=" + SKU);
                    System.out.println(sql);

                    try {
                        stmt.executeUpdate(sql);
                    } catch (SQLIntegrityConstraintViolationException ex) {
                        String message = ex.getMessage();
                        System.out.println("another error - " + message);

                        if (message.contains("Duplicate entry ") && message.contains(" for key 'items.PRIMARY'")) {
                            System.out.println("Duplicate entry for key 'items.PRIMARY'");
                            setStatus("This SKU already exists in the system", 5, "red");
                            setImageStatus("", 0, "");

                            return;
                        } else if (message.contains("Duplicate entry ") && message.contains(" for key 'items.SN'")) {
                            System.out.println("Duplicate entry for key 'items.SN'");
                            setStatus("This SN already exists in the system", 5, "red");
                            setImageStatus("", 0, "");

                            return;
                        }

                    } finally {
                        System.out.println("finally reached");
                        stmt.close();
                        conn.close();
                    }
                    setStatus("An Item Successfully Added! Saving Images and opening preview window", 3, "green");
                    new additMethods().updateCurItem();
                    new imageAdditMethods().saveOnActionImages();
                    MainPage.updateTable();
                    new additMethods().setPreviewing();
                } catch (SQLException ex) {
                    System.out.println("some big unhandled error");
                    MainPage.databaseErrorAlert(ex).showAndWait();
                }

            }

            void saveChangesAction() {
                try {
                    Connection conn = DriverManager.getConnection(MainPage.urll, MainPage.user, MainPage.passw);
                    Statement stmt = conn.createStatement();

                    String SNnew = (SNField.getText() != null && SNField.getText().equals("") ? null : SNField.getText());
                    String PNnew = (PNField.getText() != null && PNField.getText().equals("") ? null : PNField.getText());
                    String UPCnew = (UPCField.getText() != null && UPCField.getText().equals("") ? null : UPCField.getText());
                    String Gradenew = (gradeField.getText() != null && gradeField.getText().equals("") ? null : gradeField.getText());
                    String Locationnew = (locField.getText() != null && locField.getText().equals("") ? null : locField.getText());
                    String Notesnew = (notesArea.getText() != null && notesArea.getText().equals("") ? null : notesArea.getText());
                    String POnumnew = (POnumField.getText() != null && POnumField.getText().equals("") ? null : POnumField.getText());
                    String Specsnew = (specsArea.getText() != null && specsArea.getText().equals("") ? null : specsArea.getText());

                    String sql = "UPDATE items SET " +
                        (needReplacement(SNnew, currItem.SN) ?
                            (SNnew != null ? "SN='" + SNnew + "', " : "SN=" + SNnew + ", ") : "") +
                        (needReplacement(PNnew, currItem.PN) ?
                            (PNnew != null ? "PN='" + PNnew + "', " : "PN=" + PNnew + ", ") : "") +
                        (needReplacement(UPCnew, currItem.UPC) ?
                            (UPCnew != null ? "UPC='" + UPCnew + "', " : "UPC=" + UPCnew + ", ") : "") +
                        (needReplacement(Gradenew, currItem.Grade) ?
                            (Gradenew != null ? "Grade='" + Gradenew + "', " : "Grade=" + Gradenew + ", ") : "") +
                        (needReplacement(Locationnew, currItem.Location) ?
                            (Locationnew != null ? "Location='" + Locationnew + "', " :
                                "Location=" + Locationnew + ", ") : "") +
                        (needReplacement(Notesnew, currItem.Notes) ?
                            (Notesnew != null ? "Notes='" + Notesnew + "', " : "Notes=" + Notesnew + ", ") : "") +
                        (needReplacement(POnumnew, currItem.POnumber) ?
                            (POnumnew != null ? "POnumber='" + POnumnew + "', " :
                                "POnumber=" + POnumnew + ", ") : "") +
                        (needReplacement(Specsnew, currItem.Specs) ?
                            (Specsnew != null ? "Specs='" + Specsnew + "', " : "Specs=" + Specsnew + ", ") : "")+
                        "WHERE SKU=" + currItem.SKU;
                    if (sql.contains(", WHERE SKU=" + currItem.SKU)) {
                        sql = sql.replace(", WHERE SKU=" + currItem.SKU, " WHERE SKU=" + currItem.SKU);
                    }

                    if (!sql.contains("UPDATE items SET WHERE SKU=")) {
                        try {
                            String otherRecs = MainPage.user + " on " + new Timestamp(System.currentTimeMillis()) + ": " +
                                (sql.contains("Notes=")?"updated Notes to \""+ Notesnew+"\", ":"") +
                                (sql.contains("SN=")?"updated SN to \""+ SNnew+"\", ":"") +
                                (sql.contains("PN=")?"updated PN to \""+ PNnew+"\", ":"") +
                                (sql.contains("UPC=")?"updated UPC to \""+ UPCnew+"\", ":"") +
                                (sql.contains("Grade=")?"updated Grade to \""+ Gradenew+"\", ":"") +
                                (sql.contains("Location=")?"updated Location to \""+ Locationnew+"\", ":"") +
                                (sql.contains("POnumber=")?"updated POnumber to \""+ POnumnew+"\", ":"") +
                                (sql.contains("Specs=")?"updated Specs to \""+ Specsnew+"\"":"");
                            if (otherRecs.length()>2 && otherRecs.substring(otherRecs.length()-2, otherRecs.length()).equals(", ")){
                                otherRecs = otherRecs.substring(0, otherRecs.length()-2);
                            }
                            otherRecs = otherRecs + ";<<<:::==="+(currItem.OtherRecords==null?"":currItem.OtherRecords);
                            sql = sql.replace(" WHERE SKU=" + currItem.SKU,", OtherRecords='"+otherRecs+"', "+
                                "DateModified='" + new Timestamp(System.currentTimeMillis()) +"' WHERE SKU=" + currItem.SKU);
                            System.out.println(sql);

                            stmt.executeUpdate(sql);
                        } catch (SQLIntegrityConstraintViolationException ex) {
                            String message = ex.getMessage();
                            if (message.contains("Duplicate entry ") && message.contains(" for key 'items.SN'")) {
                                setStatus("This SN already exists in the system", 5, "red");
                                setImageStatus("", 0, "");

                                return;
                            }
                        } finally {
                            stmt.close();
                            conn.close();
                        }
                        setStatus("Changes successfully saved!", 3, "green");
                        updateCurItem();
                        MainPage.updateTable();

                    } else {
                        setStatus("No changes were made to the data", 3, "red");
                        setImageStatus("", 0, "");
                    }

                } catch (SQLException e) {
                    System.out.println("error saving changes");
                    setImageStatus("", 0, "");
//            System.out.println(e.getMessage());
                    MainPage.databaseErrorAlert(e).showAndWait();
                }


            }

            void exitConfirmAction(String typeOfAction) {

                String SNnew = (SNField.getText() != null && SNField.getText().equals("") ? null : SNField.getText());
                String PNnew = (PNField.getText() != null && PNField.getText().equals("") ? null : PNField.getText());
                String UPCnew =
                    (UPCField.getText() != null && UPCField.getText().equals("") ? null : UPCField.getText());
                String Gradenew =
                    (gradeField.getText() != null && gradeField.getText().equals("") ? null : gradeField.getText());
                String Locationnew =
                    (locField.getText() != null && locField.getText().equals("") ? null : locField.getText());
                String Notesnew =
                    (notesArea.getText() != null && notesArea.getText().equals("") ? null : notesArea.getText());
                String POnumnew =
                    (POnumField.getText() != null && POnumField.getText().equals("") ? null : POnumField.getText());
                String Specsnew =
                    (specsArea.getText() != null && specsArea.getText().equals("") ? null : specsArea.getText());

                boolean unsavedChanges = needReplacement(SNnew, currItem.SN) ||
                    needReplacement(PNnew, currItem.PN) ||
                    needReplacement(UPCnew, currItem.UPC) ||
                    needReplacement(Gradenew, currItem.Grade) ||
                    needReplacement(Locationnew, currItem.Location) ||
                    needReplacement(Notesnew, currItem.Notes) ||
                    needReplacement(POnumnew, currItem.POnumber) ||
                    needReplacement(Specsnew, currItem.Specs);


                if (unsavedChanges) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Unsaved Changes Confirmation");
//            alert.setHeaderText("Look, a Confirmation Dialog");
                    alert.setContentText("You have unsaved changes left. Want to " +
                        (typeOfAction.equals("preview") ? "go to preview" : "exit") + " anyway?");

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.OK) {
                        switch (typeOfAction) {
                        case "exit":
                            newWindow.close();
                            setSelectionOfCurrItem();
                            break;
                        case "preview":
                            setPreviewing();
                            new imageAdditMethods().loadImagesFromDir();
                            break;
                        }
                    }
                } else {
                    switch (typeOfAction) {
                    case "exit":
                        newWindow.close();
                        setSelectionOfCurrItem();
                        break;
                    case "preview":
                        setPreviewing();
                        new imageAdditMethods().loadImagesFromDir();
                        break;
                    }
                }

            }

            boolean needReplacement(String newText, String oldText) {
                if (newText == null) {
                    return newText != oldText;
                } else {
                    return !newText.equals(oldText);
                }
            }

            void updateCurItem() {
                long SKU = Long.parseLong(SKUField.getText());
                currItem = MainPage.extractItemsFromDb("SELECT * FROM items WHERE SKU=" + SKU)[0];
            }

            void updateFields() {
                SNField.setText(currItem.SN);
                PNField.setText(currItem.PN);
                UPCField.setText(currItem.UPC);
                gradeField.setText(currItem.Grade);
                locField.setText(currItem.Location);
                POnumField.setText(currItem.POnumber);
                notesArea.setText(currItem.Notes);
                specsArea.setText(currItem.Specs);
            }

            void setSelectionOfCurrItem() {
                if (currItem != null) {
                    int rowcount = 0;
                    for (Item it : MainPage.extractItemsFromDb("SELECT * FROM items" + "\n" +
                        "ORDER BY CASE WHEN DateTime IS NULL THEN 1 ELSE 0 END, DateTime DESC"
                    )) {
                        if (currItem.SKU == it.SKU) {
                            MainPage.table.getSelectionModel().select(rowcount);
                            return;
                        }
                        rowcount++;
                    }
                }
            }
        }

        cancel.setOnAction(e -> {
            newWindow.close();
            new additMethods().setSelectionOfCurrItem();
        });
        save.setOnAction(e -> {
            new additMethods().addItemAction();
        });
        save.setOnMousePressed(e ->{
            if (windowMode!="editing") {
                setImageStatus("saving images...", 0, "");
            }
        });
        updateImages.setOnMousePressed(e ->{
            setImageStatus("saving images...", 0, "");
        });

        switch (windowMode) {
        case "adding":
            break;
        case "editing":
            new additMethods().setEditing();
            break;
        case "previewing":
            new additMethods().setPreviewing();
            new imageAdditMethods().loadImagesFromDir();
//            imageBlockPane.setCenter(new imageAdditMethods().generateImages());

            break;
        default:
            System.out.println("WTF LOL DEBUG THIS IMMIDIATELY");
        }


    }

    /** setting status */
    public void setStatus(String message, double seconds, String color) {
        status.setText(message);
        status.setTextFill((color.equals("red")?Color.RED : (color.equals("green")?Color.GREEN:Color.BLACK)));
        if (seconds != 0) {
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(seconds), ev -> {
                if (status.getText().equals(message)) {
                    status.setText("");
                }
            }));
            timeline.setCycleCount(1);
            timeline.play();
        }
    }

    public void setImageStatus(String message, double seconds, String color) {
        imageStatus.setText(message);
        imageStatus.setTextFill((color.equals("red")?Color.RED : (color.equals("green")?Color.GREEN:Color.BLACK)));
        if (seconds != 0) {
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(seconds), ev -> {
                if (imageStatus.getText().equals(message)) {
                    imageStatus.setText("");
                }
            }));
            timeline.setCycleCount(1);
            timeline.play();
        }
    }
    public static void setLblStatus(Label labl, String message, double seconds, String color) {
        labl.setText(message);
        labl.setTextFill((color.equals("red")?Color.RED : (color.equals("green")?Color.GREEN:Color.BLACK)));
        if (seconds != 0) {
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(seconds), ev -> {
                if (labl.getText().equals(message)) {
                    labl.setText("");
                }
            }));
            timeline.setCycleCount(1);
            timeline.play();
        }
    }


}
