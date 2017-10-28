package controllers.events;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class CoreMouseDraggedHandler {

    //obsługa przesuwania rdzenia poprzez myszkę
    public final EventHandler<MouseEvent> coreOnMouseDraggedEventHandler;

    public CoreMouseDraggedHandler(controllers.FXMLDocumentController FXMLDocumentController) {
        this.coreOnMouseDraggedEventHandler = t -> {
            double offsetX = t.getSceneX() - FXMLDocumentController.getOrgSceneX();
            double offsetY = t.getSceneY() - FXMLDocumentController.getOrgSceneY();
            double newTranslateX = FXMLDocumentController.getOrgTranslateX() + offsetX;
            double newTranslateY = FXMLDocumentController.getOrgTranslateY() + offsetY;
            ((VBox) (t.getSource())).setTranslateX(newTranslateX);
            ((VBox) (t.getSource())).setTranslateY(newTranslateY);
        };
    }
}