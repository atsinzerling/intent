package imgg;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import org.apache.commons.io.FileUtils;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;


import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ImgMain extends Application {
    ArrayList<Image> images = new ArrayList<>();


    @Override
    public void start(Stage primaryStage) {
        setImages();

        BorderPane pane = new BorderPane();

        Label labl = new Label("drag and drop images");
        labl.setMinSize(200, 80);
        labl.setStyle("-fx-background-color: #d5d5d5;");
        labl.setAlignment(Pos.CENTER);

        labl.setOnDragOver((EventHandler) e -> {
            if (e instanceof DragEvent){
                DragEvent event = (DragEvent) e;
                if (event.getDragboard().hasFiles()) {
                    event.acceptTransferModes(TransferMode.ANY);
                }
                event.consume();
            }
        });
        FileChooser fil_chooser = new FileChooser();







        pane.setTop(labl);

        Scene scene = new Scene(pane, 800, 500);
        primaryStage.setTitle("imagesssss");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();

        Label status = new Label("initial");
        pane.setRight(status);
        labl.setOnMouseEntered(e -> {
            labl.setCursor(Cursor.HAND);
        });


        class additMethods{
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
                for (Image imgg: images){
//            ImageView imgview = new ImageView("...");
//            imgview.setFitWidth(200);
//            imgview.setFitHeight(200);
//            try{
//                imgview.setImage(new Image(new FileInputStream(fl)));
//            } catch (FileNotFoundException ex) {
//                ex.printStackTrace();
//            }
//            StackPane imgWrap = new StackPane(imgview);
//            imgWrap.setMaxSize(200, 200);
//            imgWrap.setStyle("-fx-border-radius: 20; " +
//                "-fx-border-color: black;" +
//                "-fx-border-width: 5 ;");
//            vbox.getChildren().add(imgWrap);
//            VBox.setMargin(imgWrap, new Insets(10, 10, 10, 10));




                    try{
                        Rectangle rectangle = new Rectangle(0, 0, 120, 80);
                        rectangle.setArcWidth(10.0);   // Corner radius
                        rectangle.setArcHeight(10.0);

//                        System.out.println(fl.getAbsolutePath());
                        ImagePattern pattern = new ImagePattern(
//                            new Image("file:"+fl.getAbsolutePath(), 280, 180, false, false) // Resizing
                            imgg
                        );

                        rectangle.setFill(pattern);
//                rectangle.setEffect(new DropShadow(20, Color.BLACK));  // Shadow
                        StackPane imgWrap = new StackPane(rectangle);
                        imgWrap.setMaxSize(rectangle.getWidth(), rectangle.getHeight());
                        imgWrap.setStyle("-fx-background-color: #ffb3b3;");
                        Label del = new Label("x");
                        del.setStyle("-fx-background-color: rgba(231,231,231,0.76);");
                        del.setMaxSize(30, 10);
                        del.setAlignment(Pos.CENTER);
                        imgWrap.setAlignment(Pos.TOP_RIGHT);
                        del.setFont(Font.font("Arial", FontWeight.BOLD, 15));
                        imgWrap.getChildren().add(del);
                        del.setOnMouseClicked(e -> {
                            images.remove(imgg);
                            pane.setCenter(generateImages());
                        });
                        del.setOnMouseEntered(e -> {
                            del.setCursor(Cursor.HAND);
                        });


                        vbox.getChildren().add(imgWrap);
                        FlowPane.setMargin(imgWrap, new Insets(10, 10, 10, 10));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                return vbox;
            }
        }
        pane.setCenter(new additMethods().generateImages());


        labl.setOnDragDropped((EventHandler) e -> {
            if (e instanceof DragEvent){
                DragEvent event = (DragEvent) e;
                for (File fl: event.getDragboard().getFiles()){
                    images.add(new Image(fl.toURI().toString()));
                }
                System.out.println("Got " + images.size() + " files");

                pane.setCenter(new additMethods().generateImages());
                event.consume();
            }
        });

        labl.setOnMousePressed((e) ->{
            new additMethods().configureFileChooser(fil_chooser);
            List<File> files = fil_chooser.showOpenMultipleDialog(primaryStage);
            if (files != null) {
                for (File fl : files) {
                    images.add(new Image(fl.toURI().toString()));
                }
                System.out.println("Got " + images.size() + " files");

                pane.setCenter(new additMethods().generateImages());
            }
        });


        Button save = new Button("save");
        save.setOnMousePressed((e) ->{
            status.setText("saving images...");
        });
        save.setOnAction((EventHandler) e -> {
            long start = System.currentTimeMillis();
            status.setText("saving images...");
//            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.001), ev -> {
//                status.setText("");
//            }));
//            timeline.setCycleCount(1);
//            timeline.play();
            status.getScene().getWindow().setWidth(status.getScene().getWidth() + 0.001);
//            new Thread(() -> {
//
//                Platform.runLater(() -> {
//                    status.setText("saving...");
//                });
//
//            }).start();




            try {
                String direct = "\\\\DESKTOP-E5VI6AD\\application\\Images\\"
                    + "66666666666\\";
                FileUtils.cleanDirectory(new File(direct));
                Files.createDirectories(Paths.get(direct));

                int count = 1;
                Thread[] thrds = new Thread[images.size()];

                for (Image imgg: images){
                    int finalCount = count;
                    Thread newThread = new Thread(() -> {
                        try {
                            System.out.println("started "+finalCount);
                            File saveTo = new File("\\\\DESKTOP-E5VI6AD\\application\\Images\\"
                                + "66666666666\\" + "666_" + String.format("%02d", finalCount) + ".jpg"
                            );
                            BufferedImage buffImg = SwingFXUtils.fromFXImage(imgg, null);
                            ImageIO.write(buffImg , "jpg", saveTo);
                            System.out.println(finalCount+" done");


                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    });
                    System.out.println("loop count "+finalCount+" done");
                    status.setText("saving image_" +finalCount);
                    thrds[count-1] = newThread;
                    newThread.start();
                    count++;
                }

                for(Thread thr: thrds){
                    thr.join();
                }
                status.setText("Images saved!");
            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
            }

            long timeElapsed = System.currentTimeMillis() - start;
            System.out.println(timeElapsed);

        });
        pane.setLeft(save);

//testing different actions, useless
        Button but = new Button("test");
        but.setOnAction((e) ->{
            System.out.println("action");
        });
        but.setOnMouseClicked((e) ->{
            System.out.println("mouseclicked");
        });
        but.setOnMousePressed((e) ->{
            System.out.println("mousepressed");
        });

        pane.setBottom(but);
    }


//    public static void main(String[] args) {
//        launch(args);
//    }
    public void setImages(){
        images.add(new Image(new File("D:\\Mine stuff\\Wallpapers\\Wolf)))\\216583.jpg").toURI().toString()));
        images.add(new Image(new File("D:\\Mine stuff\\Wallpapers\\Wolf)))\\716592.jpg").toURI().toString()));
        images.add(new Image(new File("D:\\Mine stuff\\Wallpapers\\Wolf)))\\102853.jpg").toURI().toString()));
    }








}
