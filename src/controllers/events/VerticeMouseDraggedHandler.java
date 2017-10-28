package controllers.events;

import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class VerticeMouseDraggedHandler {

    //obsługa przesuwania wierzchołków poprzez myszkę
    public final EventHandler<MouseEvent> circleOnMouseDraggedEventHandler;

    public VerticeMouseDraggedHandler(controllers.FXMLDocumentController FXMLDocumentController) {
        this.circleOnMouseDraggedEventHandler = t -> {
            double offsetX = t.getSceneX() - FXMLDocumentController.getOrgSceneX();
            double offsetY = t.getSceneY() - FXMLDocumentController.getOrgSceneY();
            double newTranslateX = FXMLDocumentController.getOrgTranslateX() + offsetX;
            double newTranslateY = FXMLDocumentController.getOrgTranslateY() + offsetY;
            ((Label) (t.getSource())).setTranslateX(newTranslateX);
            ((Label) (t.getSource())).setTranslateY(newTranslateY);
        };
    }
}