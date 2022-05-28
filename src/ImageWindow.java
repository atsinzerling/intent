import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;

public class ImageWindow extends Application {

    Image imgg;

    public ImageWindow(Image imgg) {
        this.imgg = imgg;
    }

    @Override
    public void start(Stage secondStage) {

        ImageView imgview = new ImageView(imgg);
        imgview.setFitHeight(700);
        imgview.setFitWidth(1244);
        imgview.setPreserveRatio(true);

        BorderPane pane = new BorderPane(imgview);

        Scene scene = new Scene(pane, imgview.getFitWidth(), imgview.getFitHeight());
        Stage imageViewStage = new Stage();
        imageViewStage.setTitle("Image View");
        imageViewStage.setScene(scene);
        imageViewStage.initOwner(secondStage);
        imageViewStage.initModality(Modality.WINDOW_MODAL);
        imageViewStage.setResizable(true);
        imageViewStage.show();
        scene.setOnKeyPressed((KeyEvent e) -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                imageViewStage.close();
            }
        });
    }
}
