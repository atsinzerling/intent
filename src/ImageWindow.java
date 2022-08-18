import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

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
        scene.setOnKeyReleased((KeyEvent e) -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                imageViewStage.close();
            }
        });
    }
}
