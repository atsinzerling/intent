import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
import javafx.util.Duration;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;

public class AddItem_old extends Application {

    Label status = new Label("");
    Stack<Integer> statusStack = new Stack<Integer>();
    Label imageStatus = new Label("");
    String windowMode = "adding"; //can be "editing" "previewing", "adding"
    Item currItem = null;
    ArrayList<Image> images = new ArrayList<Image>();
    //    String initDirectory =
//        "\\\\DESKTOP-E5VI6AD\\application\\Images";
    //        "D:\\Mine stuff\\Compiling\\Images";
    String user = "";

    public AddItem_old(String windowMode, Item currItem, String user) {
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
        grid.add(new Label("PO#:"), 0, 6);
        rightGrid.add(new Label("Notes:"), 0, 2);
        rightGrid.add(new Label("Specs:"), 0, 0);

        Label createdBy = new Label("");
        GridPane.setMargin(createdBy, new Insets(8, 0, 0, 0));
        grid.add(createdBy, 0, 7, 2, 1);

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
        notesArea.setWrapText(true);
        specsArea.setPrefWidth(250);
        specsArea.setPrefRowCount(3);
        specsArea.setWrapText(true);

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
        historyArea.setPrefWidth(465);
//        historyArea.setMaxWidth(650);

        historyArea.setPrefRowCount(11);
        historyArea.setEditable(false);
        historyArea.setWrapText(true);

        VBox historyVbox = new VBox(5, new Label("History of changes to the Item:"), historyArea);
        historyVbox.setVisible(false);

        Button save = new Button("Add Item");
        save.setMinHeight(35);
        Button cancel = new Button("Cancel");
        cancel.setMinSize(52, 28);
        HBox.setMargin(cancel, new Insets(0, 6, 0, 0));
        HBox bottomHistoryHbox = new HBox(10, historyVbox);
        bottomHistoryHbox.setAlignment(Pos.BOTTOM_LEFT);


        HBox.setMargin(historyArea, new Insets(0, 20, 0, 0));

        Button edit = new Button("Edit Item");
        edit.setMinSize(120, 35);
        edit.setVisible(false);


        HBox editButHBox = new HBox(10);
        editButHBox.setAlignment(Pos.CENTER_RIGHT);
        editButHBox.getChildren().addAll(cancel, save);
        HBox.setMargin(editButHBox, new Insets(10, 16, 0, 0));


        grid.setStyle("-fx-padding: 10 0 0 30; "
//            +"-fx-background-color: #dbdbff;"
        );
        rightGrid.setStyle("-fx-padding: 12 0 0 20;"
//            +"-fx-background-color: #8989ff;"
        );


        BorderPane inputpane = new BorderPane();
        inputpane.setCenter(rightGrid);
        inputpane.setLeft(grid);
        inputpane.setBottom(bottomHistoryHbox);
        BorderPane.setMargin(bottomHistoryHbox, new Insets(70, 10, 10, 30));

        status.setAlignment(Pos.CENTER);
        status.setMaxWidth(Double.MAX_VALUE);
        HBox.setMargin(imageStatus, new Insets(0, 50, 0, 0));

        imageStatus.setAlignment(Pos.CENTER);
        HBox statusHbox = new HBox(60, status, imageStatus);
        statusHbox.setAlignment(Pos.CENTER);


        BorderPane pane = new BorderPane();
        pane.setCenter(inputpane);
        HBox topHbox = new HBox(statusHbox, editButHBox);
        topHbox.setAlignment(Pos.CENTER_RIGHT);
        pane.setTop(topHbox);


        Scene secondScene = new Scene(pane, 1000, 610);
        Stage newWindow = new Stage();
        newWindow.setTitle("Add New Item");
        newWindow.setScene(secondScene);
        newWindow.initOwner(primaryStage);
        newWindow.initModality(Modality.WINDOW_MODAL);

        newWindow.setX(primaryStage.getX() + primaryStage.getWidth() / 2 - secondScene.getWidth() / 2);
        newWindow.setY(primaryStage.getY() + primaryStage.getHeight() / 2 - secondScene.getHeight() / 2);


        newWindow.show();


        /** imagepane image block*/

        BorderPane imageBlockPane = new BorderPane();

        Label dragDropLbl = new Label("drag and drop images");
        dragDropLbl.setMinSize(200, 80);
        dragDropLbl.setBackground(
            new Background(new BackgroundFill(Color.web("#d5d5d5"), new CornerRadii(10), Insets.EMPTY)));
        dragDropLbl.setBorder(
            new Border(new BorderStroke(Color.web("#b4b4b4"), BorderStrokeStyle.SOLID, new CornerRadii(10),
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
        Label directr = new Label("Current directory: " + MainPage.imagesPath + "\\");
        directr.setOnMouseEntered(e -> {
            directr.setCursor(Cursor.HAND);
        });
        directr.setOnMouseClicked(e -> {
            String directory = MainPage.imagesPath + "\\" + currItem.SKU;
            try {
                Files.createDirectories(Paths.get(directory));
            } catch (IOException ee) {
                MainPage.ioErrorAlert(ee).showAndWait();
            }
            File file = new File(directory);
            if (file.exists()) {
                try {
                    Desktop desktop = Desktop.getDesktop();
                    desktop.open(file);
                } catch (IOException exxxx) {
                    exxxx.printStackTrace();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Folder unavailable");
                alert.setContentText(Methods.wrap("Folder " + MainPage.imagesPath + " is unavailable"));

                alert.showAndWait();

            }
        });
        imageBlockPane.setTop(new VBox(10, dragHbox, directr));
        imageBlockPane.setMargin(dragDropLbl, new Insets(10, 10, 10, 10));

        Label load = new Label("");
        load.setMinSize(425, 310);
        load.setAlignment(Pos.CENTER);
        load.setBorder(
            new Border(new BorderStroke(Color.web("#000000"), BorderStrokeStyle.SOLID, null,
                new BorderWidths(0.3))));


        AtomicBoolean otherFiles = new AtomicBoolean(false);
        Thread.UncaughtExceptionHandler h = new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread th, Throwable ex) {
                System.out.println("\n\n\nUncaught Thread exception: \n" + ex + "\n\n\n");
            }
        };

        class imageAdditMethods {
            private void configureFileChooser(
                final FileChooser fileChooser) {
                fileChooser.setTitle("Choose Pictures");
                fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("All Images", "*.jpg; *.png; *.bmp")
                );
            }

            ScrollPane generateImages() {
                FlowPane vbox = new FlowPane();
                vbox.setPrefWidth(435);
                vbox.setMaxWidth(435);

                ScrollPane scrollPane = new ScrollPane();
                scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
                scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                scrollPane.setPrefWidth(425);
                scrollPane.setMaxWidth(425);
                scrollPane.setPrefHeight(310);
                scrollPane.setMaxHeight(310);
                int counter = 0;
                if (images != null) {
                    for (Image imgg : images) {
                        if (imgg == null) {
                            continue;
                        }
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
                        rectangle.setOnMouseClicked(e -> {
                            new ImageWindow(imgg).start(primaryStage);
                        });

                        if (!windowMode.equals("previewing")) {
                            Label del = new Label("x");
                            del.setMaxSize(30, 10);
                            del.setAlignment(Pos.CENTER);

                            del.setBackground(new Background(
                                new BackgroundFill(Color.rgb(231, 231, 231, 0.76), new CornerRadii(3), Insets.EMPTY)));
                            del.setBorder(new Border(
                                new BorderStroke(Color.web("#000000FF"), BorderStrokeStyle.SOLID, new CornerRadii(3),
                                    new BorderWidths(0.3))));
                            imgWrap.setAlignment(Pos.TOP_RIGHT);
                            del.setFont(Font.font("Arial", FontWeight.BOLD, 15));
                            imgWrap.getChildren().add(del);
                            del.setOnMouseClicked(e -> {
                                images.remove(imgg);
                                imageBlockPane.setCenter(generateImages());
                            });
                            del.setOnMouseEntered(e -> {
                                del.setCursor(Cursor.HAND);
                            });
                        }
                        counter++;

                        vbox.getChildren().add(imgWrap);
                        FlowPane.setMargin(imgWrap, new Insets(6));
                    }
                    scrollPane.setContent(vbox);
                }
                if (counter == 0) {
                    Label noimgLbl = new Label(
                        otherFiles.get() ? "no images found, but there are other files in the folder" :
                            "no images yet");
                    noimgLbl.setMinSize(425, 300);
                    noimgLbl.setAlignment(Pos.CENTER);
                    scrollPane.setContent(noimgLbl);
                }
                BorderPane.setAlignment(scrollPane, Pos.TOP_CENTER);
                return scrollPane;
            }


            /** methods for save button, only in editing and adding*/

            void saveOnActionImages() {
                if (windowMode.equals("previewing")) {
                    return;
                }

                AtomicBoolean allSaved = new AtomicBoolean(true);

                Thread newThr = new Thread(() -> {
                    Platform.runLater(() -> {
                        setImageStatus("saving images...", 0, "");
                    });

                    int count = 1;
                    Thread[] thrds = new Thread[images.size()];

                    String directory = MainPage.imagesPath + "\\" + currItem.SKU;
                    try {
                        Files.createDirectories(Paths.get(directory));
                        FileUtils.cleanDirectory(new File(directory));
                    } catch (IOException e) {
                        MainPage.ioErrorAlert(e).showAndWait();
                        return;
                    }

                    for (Image imgg : images) {
                        int finalCount = count;
                        Thread newThread = new Thread(() -> {
                            try {
                                System.out.println("saving image " + finalCount);
                                File saveTo = new File(directory + "\\"
                                    + currItem.SKU + "_" + String.format("%02d", finalCount) + ".jpg"
                                );
                                BufferedImage buffImg = SwingFXUtils.fromFXImage(imgg, null);
                                ImageIO.write(buffImg, "jpg", saveTo);
                                System.out.println("image " + finalCount + " successfully saved");


                            } catch (IOException ex) {
                                ex.printStackTrace();
                                allSaved.set(false);
                            }
                        });
                        newThread.setUncaughtExceptionHandler(h);
                        thrds[count - 1] = newThread;
                        newThread.start();
                        count++;
                    }

                    try {
                        for (Thread thr : thrds) {
                            thr.join();
                        }
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                        allSaved.set(false);
                    }

                    try {
                        Connection conn = DriverManager.getConnection(MainPage.urll, MainPage.user, MainPage.passw);
                        Statement stmt = conn.createStatement();
                        String sqll = "UPDATE " + MainPage.schema + ".items SET DateModified='" +
                            new Timestamp(System.currentTimeMillis()) + "' WHERE SKU=" + currItem.SKU;

                        stmt.executeUpdate(new String(sqll));
                        stmt.close();
                        conn.close();
                        MainPage.updateTable();
                    } catch (SQLException e) {
                        MainPage.databaseErrorAlert(e).showAndWait();
                    }

                    Platform.runLater(() -> {
                        if (allSaved.get()) {
                            setImageStatus("Images saved!", 3, "green");
                        } else {
                            setImageStatus("There was an error saving images", 3, "red");
                        }
                    });
                });
                newThr.setUncaughtExceptionHandler(h);
                newThr.start();


            }

            void loadImagesFromDir() {

                Thread newThr = new Thread(() -> {
                    try {
                        Platform.runLater(() -> {
                            load.setText("loading images");
                            dotAnim(load);
                            imageBlockPane.setCenter(load);
                        });

                        File dir = new File(MainPage.imagesPath + "\\" + currItem.SKU);
                        if (dir.exists()) {
                            File[] fls = dir.listFiles();
                            Thread[] thrds = new Thread[fls.length];
                            System.out.println("files length " + fls.length);
                            images.clear();
                            for (int i = 0; i < fls.length; i++) {
                                System.out.println(fls[i].toURI().toString());


                                int finalI = i;
                                Thread newThread = new Thread(() -> {
                                    try {
                                        if (isImage(fls[finalI])) {
                                            images.add(new Image(fls[finalI].toURI().toString()));
                                        } else {
                                            System.out.println(fls[finalI].toURI().toString() + " not an image");
                                            otherFiles.set(true);
                                        }
                                    } catch (Exception e) {
                                        System.out.println("exception loading image " + fls[finalI].toURI().toString());
                                    }
                                });
                                newThread.setUncaughtExceptionHandler(h);
                                thrds[i] = newThread;
                                newThread.start();
                            }

                            try {
                                for (Thread thr : thrds) {
                                    thr.join();
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        Platform.runLater(() -> {

                            try {
                                if (imageBlockPane != null) {
                                    imageBlockPane.setCenter(new imageAdditMethods().generateImages());
                                }
                            } catch (Exception e) {
                                System.out.println("runlater exc catched");
                                e.printStackTrace();
                            }
//                            for (Image i: images){
//                                System.out.println(i!=null);
//                            }
                        });
                    } catch (Exception e) {
                        System.out.println("some runlater exception");
                        e.printStackTrace();
                    }
                });
                newThr.setUncaughtExceptionHandler(h);
                newThr.start();
            }
        }
        imageAdditMethods imageAdditMethod = new imageAdditMethods();
        imageBlockPane.setCenter(imageAdditMethod.generateImages());


        FileChooser fil_chooser = new FileChooser();

        dragDropLbl.setOnDragOver((EventHandler) e -> {
            if (e instanceof DragEvent) {
                DragEvent event = (DragEvent) e;
                if (event.getDragboard().hasFiles()) {
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }
                event.consume();
            }
        });
        dragDropLbl.setOnDragDropped((EventHandler) e -> {
            System.out.println("dragdrop event started");
            if (e instanceof DragEvent) {
                DragEvent event = (DragEvent) e;
                for (File fl : event.getDragboard().getFiles()) {
                    try {
                        if (fl.isFile()) {
                            String[] star = fl.toString().split("\\.");
                            if (star.length > 1 &&
                                (star[star.length - 1].equals("jpg") || star[star.length - 1].equals("png") ||
                                    star[star.length - 1].equals("bmp"))) {
                                Image img = (new Image(fl.toURI().toString()));
                                if (img != null) {
                                    images.add(img);
                                }
                            }
                        }
                    } catch (Exception exc) {
                        System.out.println("There was an error processing file " + fl.toURI());
                    }
                }
                System.out.println("Got " + images.size() + " images");
                imageBlockPane.setCenter(imageAdditMethod.generateImages());
//                newWindow.setHeight(secondScene.getHeight()+200);
                event.consume();
            }
        });

        dragDropLbl.setOnMousePressed((e) -> {
            imageAdditMethod.configureFileChooser(fil_chooser);
            List<File> files = fil_chooser.showOpenMultipleDialog(newWindow);
            if (files != null) {
                for (File fl : files) {
                    try {
                        if (fl.isFile()) {
                            String[] star = fl.toString().split("\\.");
                            if (star.length > 1 &&
                                (star[star.length - 1].equals("jpg") || star[star.length - 1].equals("png") ||
                                    star[star.length - 1].equals("bmp"))) {
                                Image img = (new Image(fl.toURI().toString()));
                                if (img != null) {
                                    images.add(img);
                                }
                            }
                        }
                    } catch (Exception exc) {
                        System.out.println("There was an error processing file " + fl.toURI());
                    }
                }
                System.out.println("Got " + images.size() + " images");

                imageBlockPane.setCenter(imageAdditMethod.generateImages());
            }
        });
        updateImages.setOnAction(e -> {
            imageAdditMethod.saveOnActionImages();
            String timm = new Timestamp(System.currentTimeMillis()).toString();
            if (timm.split("\\.").length > 0) {
                timm = timm.split("\\.")[0];
            }
            String otherRecs =
                MainPage.user + " on " + timm + " : saved images (" +
                    (images.size() == 1 ? "1 image" : images.size() + " images") + " total)";
            if (otherRecs.length() > 2 &&
                otherRecs.substring(otherRecs.length() - 2, otherRecs.length()).equals(", ")) {
                otherRecs = otherRecs.substring(0, otherRecs.length() - 2);
            }
            otherRecs = otherRecs + ";<<<:::===" + (currItem.OtherRecords == null ? "" : currItem.OtherRecords);

            String sqll =
                "UPDATE " + MainPage.schema + ".items SET OtherRecords='" + otherRecs + "' WHERE SKU=" + currItem.SKU;
            System.out.println("\tSQL " + sqll);

            try {
                Connection conn = DriverManager.getConnection(MainPage.urll, MainPage.user, MainPage.passw);
                Statement stmt = conn.createStatement();
                stmt.executeUpdate(sqll);
                currItem.OtherRecords = otherRecs;
                stmt.close();
                conn.close();
            } catch (SQLException ee) {
                ee.printStackTrace();
                MainPage.databaseErrorAlert(ee).showAndWait();
            }
            Methods.updateUserLog(user,
                "uploaded " + (images.size() == 1 ? "1 image" : images.size() + " images") + " for item SKU=\"" +
                    currItem.SKU + "\"");

        });

        pane.setRight(imageBlockPane);
        BorderPane.setMargin(imageBlockPane, new Insets(20, 8, 0, 10));
        /** images ended*/

        /** print block added to the bottom of imageblockpane*/

        Label chosePrinLbl = new Label("Choose printer:");
        VBox.setMargin(chosePrinLbl, new Insets(5, 0, 0, 0));

        ObservableList<String> printersObsList = FXCollections.observableArrayList();
//        PrintServiceLookup.lookupPrintServices(null, null)
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        PrintService defaultPrinter = PrintServiceLookup.lookupDefaultPrintService();
        String defaultPr = defaultPrinter.toString();
        for (PrintService ps : printServices) {
            String pss = ps.toString();
            if (pss.contains("Win32 Printer : ")) {
                pss = pss.replace("Win32 Printer : ", "");
            }
            if (ps.equals(defaultPrinter)) {
                pss += " (default)";
                defaultPr = pss;
            }
            printersObsList.add(pss);
        }

        ChoiceBox printerChoiceBox = new ChoiceBox(printersObsList);
        printerChoiceBox.setValue(defaultPr);
        VBox.setMargin(printerChoiceBox, new Insets(0, 0, 8, 0));
        //
        printerChoiceBox.setDisable(true);
        //

        VBox printerVbox = new VBox(8, chosePrinLbl, printerChoiceBox);
        printerVbox.setAlignment(Pos.BOTTOM_LEFT);


        ObservableList<String> labelsObsList = FXCollections.observableArrayList();
        labelsObsList.addAll("default label");
        ChoiceBox labelChoiceBox = new ChoiceBox((labelsObsList));
        labelChoiceBox.setValue("default label");
        labelChoiceBox.setDisable(true);

        Button printBtn = new Button("Print");
        printBtn.setMinSize(100, 40);

        VBox printVbox = new VBox(3, labelChoiceBox, printBtn);

        HBox printHBox = new HBox(30, printerVbox, printVbox);
        printHBox.setAlignment(Pos.BOTTOM_RIGHT);
        BorderPane.setMargin(printHBox, new Insets(9, 14, 25, 9));
        printHBox.setPadding(new Insets(7));
        printHBox.setBorder(
            new Border(new BorderStroke(Color.web("#000000"), BorderStrokeStyle.SOLID, new CornerRadii(3),
                new BorderWidths(0.5))));

        printHBox.setVisible(false);

        imageBlockPane.setBottom(printHBox);

        printBtn.setOnAction(e -> {
            if (!new File(MainPage.bartendPath + "\\bartend.exe").exists()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error location \"bartend.exe\" file");
//            alert.setHeaderText("Look, a Confirmation Dialog");
                alert.setContentText(Methods.wrap(
                    "Make sure that \"bartend.exe\" exists at the directory\"" + MainPage.bartendPath +
                        "\", specified in the configg.csv file"));
                alert.showAndWait();
                return;
            }
            String printer = (String) printerChoiceBox.getValue();
            if (printer.contains(" (default)")) {
                printer = printer.replace(" (default)", "");
            }
            System.out.println(printer);
            printNewLbl(currItem.PN, currItem.SN, currItem.Grade, currItem.Specs, "Win32 Printer : " + printer);
        });

        /** end printing*/


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

                if (!user.equals(currItem.User)) {
                    SNField.setEditable(false);
                    specsArea.setEditable(false);
                    SNField.setStyle(notEditableColorCSS);
                    specsArea.setStyle(notEditableColorCSS);
                }

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
                printHBox.setVisible(false);

                save.setVisible(true);
                save.setText("Save Changes");
                save.setOnAction(e -> {
                    saveChangesAction();
                });
                cancel.setVisible(true);
                cancel.setText("Exit");
                cancel.setOnAction(e -> {
                    System.out.println("firing exit from cancel button");
                    exitConfirmAction("exit");
                });
                edit.setVisible(true);
                edit.setText("Back to Preview");
                edit.setOnAction(e -> {
                    System.out.println("firing exit from edit button");
                    exitConfirmAction("preview");
                });

                editButHBox.getChildren().clear();
                editButHBox.getChildren().addAll(cancel, save, edit);

                dragDropLbl.setDisable(false);
                updateImages.setVisible(true);
                directr.setText("Current directory: " + MainPage.imagesPath + "\\" + currItem.SKU + "\\");

                imageBlockPane.setCenter(imageAdditMethod.generateImages());
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

                new additMethods().updateCurItem();

                SNField.setText(currItem.SN);
                PNField.setText(currItem.PN);
                UPCField.setText(currItem.UPC);
                gradeField.setText(currItem.Grade);
                locField.setText(currItem.Location);
                POnumField.setText(currItem.POnumber);
                notesArea.setText(currItem.Notes);
                specsArea.setText(currItem.Specs);

                historyArea.setText((currItem.OtherRecords != null ?
                    "   " + currItem.OtherRecords.replaceAll("\n", "\t").replaceAll("<<<:::===", "\n\n   ") : ""));
                historyVbox.setVisible(true);

                printHBox.setVisible(true);
                if (MainPage.bartendPath == null) {
                    printHBox.setDisable(true);
                }
                save.setVisible(false);

                cancel.setVisible(true);
                cancel.setText("Exit");
                cancel.setOnAction(e -> {
                    newWindow.close();
//                    setSelectionOfCurrItem();
                    //write some code to saving changes lol
                });

                edit.setVisible(true);
                edit.setText("Edit Item");
                edit.setOnAction(e -> {
                    setEditing();
                });
                editButHBox.getChildren().clear();
                editButHBox.getChildren().addAll(cancel, edit);

                createdBy.setText("Item created by " + currItem.User);

                System.out.println("previewing item " + currItem);
                updateFields();

                dragDropLbl.setDisable(true);
                updateImages.setVisible(false);
                directr.setText("Current directory: " + MainPage.imagesPath + "\\" + currItem.SKU + "\\");

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
                    if (SKUfieldText.equals("")) {
                        SKUmessage = true;
                    } else {
                        try {
                            SKU = Integer.parseInt(SKUfieldText);

                            if (SKUfieldText.length() != 6 || SKUfieldText.charAt(0) == '0') {
                                SKUmessage = true;
                            }
                        } catch (NumberFormatException eeeeeerocks) {
//                            setStatus("SKU should be a number", 5, "red");
//                            setImageStatus("", 0, "");
                            SKUmessage = true;
//                            return;
                        }
                    }

                    if (SKUmessage) {
                        setStatus("SKU should be a 6-digit number without leading zeros", 5, "red");
                        setImageStatus("", 0, "");
                        return;
                    }
                    if (locField.getText() != null && locField.getText().strip().equals("")) {
                        setStatus("Cannot add an item without Location", 3, "red");
                        setImageStatus("", 0, "");
                        return;
                    }

                    Connection conn = DriverManager.getConnection(MainPage.urll, MainPage.user, MainPage.passw);

                    //can make this shorter, do that later
                    Statement stmt = conn.createStatement();
                    String values =
                        "'" + SKU + "'" + "," +
                            (SNField.getText().strip().equals("") ? null : "'" + SNField.getText().strip() + "'") +
                            "," +
                            (PNField.getText().strip().equals("") ? null : "'" + PNField.getText().strip() + "'") +
                            "," +
                            (UPCField.getText().strip().equals("") ? null : "'" + UPCField.getText().strip() + "'") +
                            "," +
                            (gradeField.getText().strip().equals("") ? null :
                                "'" + gradeField.getText().strip() + "'") + "," +
                            (locField.getText().strip().equals("") ? null : "'" + locField.getText().strip() + "'") +
                            "," +
                            (notesArea.getText().strip().equals("") ? null : "'" + notesArea.getText().strip() + "'") +
                            "," +
                            "'" + MainPage.user + "'" + "," +
                            "'" + new Timestamp(System.currentTimeMillis()) + "'" + "," +
                            null + "," +
                            null + "," +
                            "'" + new Timestamp(System.currentTimeMillis()) + "'" + "," +
                            (POnumField.getText().strip().equals("") ? null :
                                "'" + POnumField.getText().strip() + "'") + "," +
                            (specsArea.getText().strip().equals("") ? null : "'" + specsArea.getText().strip() + "'");
                    String sql = ("INSERT INTO " + MainPage.schema + ".items " +
//                        "(SKU,SN,PN,UPC,Grade,Location,Notes,User,DateTime,Images,OtherRecords) " +
                        "VALUES (" + values + ")");

//                        "\n"+
//                        "UPDATE items SET DateTime='" + new Timestamp(System.currentTimeMillis()) + "' WHERE SKU=" + SKU);
                    System.out.println("\tSQl " + sql);

                    try {
                        stmt.executeUpdate(sql);
                    } catch (SQLIntegrityConstraintViolationException ex) {
                        String message = ex.getMessage();

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
                        } else {
                            System.out.println("some other error - " + message);
                        }

                    } finally {
//                        System.out.println("finally reached");
                        stmt.close();
                        conn.close();
                    }
                    setStatus("An Item Successfully Added! Saving Images and opening preview window", 3, "green");
//                    new additMethods().updateCurItem();
                    imageAdditMethod.saveOnActionImages();
                    MainPage.updateTable();
                    new additMethods().setPreviewing();
                    Methods.updateUserLog(user, "created item SKU=\"" + currItem.SKU + "\"");
                } catch (SQLException ex) {
                    System.out.println("some big unhandled error");
                    MainPage.databaseErrorAlert(ex).showAndWait();
                }

            }

            void saveChangesAction() {
                try {
                    Connection conn = DriverManager.getConnection(MainPage.urll, MainPage.user, MainPage.passw);
                    Statement stmt = conn.createStatement();

                    String SNnew =
                        (SNField.getText() != null && SNField.getText().equals("") ? null : SNField.getText());
                    String PNnew =
                        (PNField.getText() != null && PNField.getText().equals("") ? null : PNField.getText());
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

                    if (locField.getText() != null && locField.getText().strip().equals("")) {
                        setStatus("Cannot save an item without Location", 3, "red");
                        return;
                    }

                    String sqll = "UPDATE " + MainPage.schema + ".items SET " +
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
                            (Specsnew != null ? "Specs='" + Specsnew + "', " : "Specs=" + Specsnew + ", ") : "") +
                        "WHERE SKU=" + currItem.SKU;
                    if (sqll.contains(", WHERE SKU=" + currItem.SKU)) {
                        sqll = sqll.replace(", WHERE SKU=" + currItem.SKU, " WHERE SKU=" + currItem.SKU);
                    }

                    if (!sqll.contains("UPDATE " + MainPage.schema + ".items SET WHERE SKU=")) {
                        try {
                            String timm = new Timestamp(System.currentTimeMillis()).toString();
                            if (timm.split("\\.").length > 0) {
                                timm = timm.split("\\.")[0];
                            }
                            String otherRecs =
                                MainPage.user + " on " + timm + " : " +
                                    (sqll.contains("Notes=") ? "updated Notes to \"" + Notesnew + "\", " : "") +
                                    (sqll.contains("SN=") ?
                                        "updated SN from \"" + (currItem.SN == null ? "" : currItem.SN) + "\" to \"" +
                                            SNnew + "\", " : "") +
                                    (sqll.contains("PN=") ?
                                        "updated PN from \"" + (currItem.PN == null ? "" : currItem.PN) + "\" to \"" +
                                            PNnew + "\", " : "") +
                                    (sqll.contains("UPC=") ?
                                        "updated UPC from \"" + (currItem.UPC == null ? "" : currItem.UPC) +
                                            "\" to \"" + UPCnew + "\", " : "") +
                                    (sqll.contains("Grade=") ?
                                        "updated Grade from \"" + (currItem.Grade == null ? "" : currItem.Grade) +
                                            "\" to \"" + Gradenew + "\", " : "") +
                                    (sqll.contains("Location=") ? "updated Location from \"" +
                                        (currItem.Location == null ? "" : currItem.Location) + "\" to \"" +
                                        Locationnew + "\", " :
                                        "") +
                                    (sqll.contains("POnumber=") ?
                                        "updated PO# from \"" + (currItem.POnumber == null ? "" : currItem.POnumber) +
                                            "\" to \"" + POnumnew + "\", " : "") +
                                    (sqll.contains("Specs=") ? "updated Specs to \"" + Specsnew + "\"" : "");
                            if (otherRecs.length() > 2 &&
                                otherRecs.substring(otherRecs.length() - 2, otherRecs.length()).equals(", ")) {
                                otherRecs = otherRecs.substring(0, otherRecs.length() - 2);
                            }
                            otherRecs =
                                otherRecs + ";<<<:::===" + (currItem.OtherRecords == null ? "" : currItem.OtherRecords);
                            sqll = sqll.replace(" WHERE SKU=" + currItem.SKU, ", OtherRecords='" + otherRecs + "', " +
                                "DateModified='" + new Timestamp(System.currentTimeMillis()) + "' WHERE SKU=" +
                                currItem.SKU);
                            System.out.println("\tSQL " + sqll);

                            stmt.executeUpdate(sqll);
                            currItem.OtherRecords = otherRecs;


                            String userLog =
                                (sqll.contains("Location=") ? "Loc, " : "") +
                                    (sqll.contains("Grade=") ? "Grade, " : "") +
                                    (sqll.contains("Notes=") ? "Notes, " : "") +
                                    (sqll.contains("SN=") ? "SN, " : "") +
                                    (sqll.contains("PN=") ? "PN, " : "") +
                                    (sqll.contains("UPC=") ? "UPC, " : "") +
                                    (sqll.contains("POnumber=") ? "PO#, " : "") +
                                    (sqll.contains("Specs=") ? "Specs, " : "");
                            if (userLog.length() > 2 &&
                                userLog.substring(userLog.length() - 2, userLog.length()).equals(", ")) {
                                userLog = userLog.substring(0, userLog.length() - 2);
                            }

                            setStatus("Changes successfully saved!", 3, "green");
                            Methods.updateUserLog(user, "edited item SKU=\"" + currItem.SKU + "\" (" + userLog + ")");

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
                //given null or actual text

                boolean unsavedChanges = false;
                if (windowMode.equals("adding")) {
                    unsavedChanges = needReplacement(SNnew, null) ||
                        needReplacement(UPCnew, null) ||
                        needReplacement(PNnew, null) ||
                        needReplacement(Gradenew, null) ||
                        needReplacement(Locationnew, null) ||
                        needReplacement(Notesnew, null) ||
                        needReplacement(POnumnew, null) ||
                        needReplacement(Specsnew, null);
                } else if (windowMode.equals("editing")) {
                    unsavedChanges = needReplacement(SNnew, currItem.SN) ||
                        needReplacement(PNnew, currItem.PN) ||
                        needReplacement(UPCnew, currItem.UPC) ||
                        needReplacement(Gradenew, currItem.Grade) ||
                        needReplacement(Locationnew, currItem.Location) ||
                        needReplacement(Notesnew, currItem.Notes) ||
                        needReplacement(POnumnew, currItem.POnumber) ||
                        needReplacement(Specsnew, currItem.Specs);
                }

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
//                            setSelectionOfCurrItem();
                            break;
                        case "preview":
//                            new additMethods().updateCurItem();
                            setPreviewing();
                            imageAdditMethod.loadImagesFromDir();
                            break;
                        }
                    } else {
                        System.out.println("cancel presed");
                    }
                } else {
                    switch (typeOfAction) {
                    case "exit":
                        newWindow.close();
//                        setSelectionOfCurrItem();
                        break;
                    case "preview":
                        setPreviewing();
                        imageAdditMethod.loadImagesFromDir();
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
                currItem.copy(
                    MainPage.extractItemsFromDb("SELECT * FROM " + MainPage.schema + ".items WHERE SKU=" + SKU)[0]);
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

//            void setSelectionOfCurrItem() {
//                if (currItem != null) {
//                    int rowcount = 0;
//                    for (Item it : MainPage.extractItemsFromDb("SELECT * FROM "+MainPage.schema+".items" + "\n" +
//                        "ORDER BY CASE WHEN DateTime IS NULL THEN 1 ELSE 0 END, DateTime DESC"
//                    )) {
//                        if (currItem.SKU == it.SKU) {
//                            MainPage.table.getSelectionModel().select(rowcount);
//                            return;
//                        }
//                        rowcount++;
//                    }
//                }
//            }
        }
        additMethods additMethod = new additMethods();

        cancel.setOnAction(e -> {
            additMethod.exitConfirmAction("exit");
//            additMethod.setSelectionOfCurrItem();
        });
        save.setOnAction(e -> {
            additMethod.addItemAction();
        });
        save.setOnMousePressed(e -> {
            if (windowMode != "editing") {
                setImageStatus("saving images...", 0, "");
            }
        });
        newWindow.setOnCloseRequest(e -> {
            additMethod.exitConfirmAction("exit");
            e.consume();
        });
        secondScene.setOnKeyReleased((KeyEvent e) -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                additMethod.exitConfirmAction("exit");
            }
        });

        switch (windowMode) {
        case "adding":
            SKUField.setText(String.valueOf(Methods.generateCustomSKU()));
            break;
        case "editing":
            additMethod.setEditing();
            break;
        case "previewing":
            additMethod.setPreviewing();
            imageAdditMethod.loadImagesFromDir();
//            imageBlockPane.setCenter(imageAdditMethod.generateImages());

            break;
        default:
            System.out.println("WTF LOL DEBUG THIS IMMIDIATELY");
        }
    }

    /**
     * setting status
     */
    public void setStatus(String message, double seconds, String color) {
        status.setText(message);
        status.setTextFill((color.equals("red") ? Color.RED : (color.equals("green") ? Color.GREEN : Color.BLACK)));
        int num = statusStack.size();
        statusStack.push(num);
        if (seconds != 0) {
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(seconds), ev -> {
                if (status.getText().equals(message) && !statusStack.isEmpty() && statusStack.peek() == num) {
                    status.setText("");
                    statusStack.clear();
                }
            }));
            timeline.setCycleCount(1);
            timeline.play();
        }
    }

    public void setImageStatus(String message, double seconds, String color) {
        imageStatus.setText(message);
        imageStatus.setTextFill(
            (color.equals("red") ? Color.RED : (color.equals("green") ? Color.GREEN : Color.BLACK)));
        if (seconds != 0) {
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(seconds), ev -> {
                if (imageStatus.getText().equals(message)) {
                    imageStatus.setText("");
                }
            }));
            timeline.setCycleCount(1);
            timeline.play();
        } else {

        }
    }

    public void setImageLoadingStatus(String message, String color) {
        imageStatus.setText(message);
        imageStatus.setTextFill(
            (color.equals("red") ? Color.RED : (color.equals("green") ? Color.GREEN : Color.BLACK)));
        dotAnim(imageStatus);
    }

    public static void setLblStatus(Label labl, String message, double seconds, String color) {
        labl.setText(message);
        labl.setTextFill((color.equals("red") ? Color.RED : (color.equals("green") ? Color.GREEN : Color.BLACK)));
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

    void printNewLbl(String pn, String sn, String grade, String specs, String printer) {
        String filepath = "C:\\Program Files Intent\\Intent Database 1.0.0\\zpprint";

        if (grade == null) {
            grade = "-";
        } else {
            if (!grade.equals("")) {
                grade = String.valueOf(grade.charAt(0));
            }
            switch (grade.toUpperCase()) {
            case "A":
                grade = "";
                break;
            case "B":
                grade = ".";
                break;
            case "U":
                grade = "..";
                break;
            case "C":
                grade = "...";
                break;
            default:
                grade = "-";
                System.out.println("didn't recognize grade");
                break;
            }
        }

        //write to xml

        String xmlShit = "" +
            "<XMLScript Version=\"2.0\" Name=\"09232006_103601_Job1\" ID=\"123\">\n" +
            "\t<Command Name=\"Job1\">\n" +
            "\t\t<Print> \n" +
            "\t\t\t<Format>" + filepath + "\\pn-sn-grade-specs.btw</Format>\n" +
            "\t\t\t<PrintSetup>\n" +
            "\t\t\t\t<IdenticalCopiesOfLabel>1</IdenticalCopiesOfLabel>\n" +
            "\t\t\t</PrintSetup>\n" +
            "\t\t\t<NamedSubString Name=\"pn\"> \n" +
            "\t\t\t\t<Value>" + pn + "</Value>\n" +
            "\t\t\t</NamedSubString> \n" +
            "\t\t\t<NamedSubString Name=\"sn\"> \n" +
            "\t\t\t\t<Value>" + sn + "</Value>\n" +
            "\t\t\t</NamedSubString> \n" +
            "\t\t\t<NamedSubString Name=\"grade\"> \n" +
            "\t\t\t\t<Value>" + grade + "</Value>\n" +
            "\t\t\t</NamedSubString>\n" +
            "\t\t\t<NamedSubString Name=\"specs\"> \n" +
            "\t\t\t\t<Value>" + specs + "</Value>\n" +
            "\t\t\t</NamedSubString>\n" +
            "\t\t</Print> \n" +
            "\t</Command> \n" +
            "</XMLScript>" +
            "";


        try (PrintWriter out = new PrintWriter(filepath + "\\pn-sn-grade-specs.xml")) {
            out.println(xmlShit);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            MainPage.ioErrorAlert(e);
        }

        String scriptShit = "cd \"" + MainPage.bartendPath + "\"\n" +
            "bartend.exe /XMLScript=\"C:\\Program Files Intent\\Intent Database 1.0.0\\zpprint\\pn-sn-grade-specs.xml\" /PRN=\"" +
            printer + "\" /X";

        try (PrintWriter out = new PrintWriter(filepath + "\\pn-sn-grade-specs.bat")) {
            out.println(scriptShit);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            MainPage.ioErrorAlert(e);
        }
//        System.out.println("printing script "+scriptShit);

        //print this shit     .bat file will be stored in the folder, and i will manually adjust where the crack of the program is stored
        //xml file is stored at the same folder

        try {
            Process process2 = Runtime.getRuntime().exec("cmd /c pn-sn-grade-specs.bat",
                null, new File(filepath));


//            Process runtime = Runtime.getRuntime().exec("ping www.delftstack.com");
            Show_Output(process2);
        } catch (IOException e) {
            e.printStackTrace();
            MainPage.ioErrorAlert(e);
        }
    }


    public static String Show_Output(Process process) throws IOException {
        BufferedReader output_reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String output = "";
        String allout = "";
        while ((output = output_reader.readLine()) != null) {
            System.out.println(output);
            allout += output;
        }
        return allout;
    }

    public static void dotAnim(Label lbl) {
        Timeline timeline = new Timeline(Timeline.INDEFINITE, new KeyFrame(Duration.seconds(1), ev -> {
            if (numOfDots(lbl.getText()) < 3) {
                lbl.setText(lbl.getText() + ".");
            } else {
                lbl.setText(lbl.getText().substring(0, lbl.getText().length() - numOfDots(lbl.getText())));
            }
        }));
        timeline.setCycleCount(100);
        timeline.play();
    }

    public static int numOfDots(String str) {
        int num = 0;
        for (char ch : str.toCharArray()) {
            if (ch == '.') {
                num++;
            }
        }
        return num;
    }

    public static boolean isImage(File file) {
        boolean b = false;
        try {
            b = (ImageIO.read(file) != null);
        } catch (IOException e) {
        }
        return b;
    }

}
