package controllers.events;

import data.ConstStrings;
import data.DataAccessor;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class VerticeMousePressedHandler {

    //obsługa kliknięcia na wierzchołek
    public final EventHandler<MouseEvent> circleOnMousePressedEventHandler;

    public VerticeMousePressedHandler(controllers.FXMLDocumentController FXMLDocumentController) {
        this.circleOnMousePressedEventHandler = t -> {
            if (t.getButton() == MouseButton.SECONDARY) {
                DataAccessor.setAnalyzedVertice(controllers.FXMLDocumentController.verticeLabels.get(t.getSource()));
                FXMLDocumentController.showFXML(ConstStrings.SHOW_VERTICE_FXML_RES, ConstStrings.SHOW_VERTICE_TITLE);
            } else {
                FXMLDocumentController.setOrgSceneX(t.getSceneX());
                FXMLDocumentController.setOrgSceneY(t.getSceneY());
                FXMLDocumentController.setOrgTranslateX(((Label) (t.getSource())).getTranslateX());
                FXMLDocumentController.setOrgTranslateY(((Label) (t.getSource())).getTranslateY());
                ((Label) (t.getSource())).toFront();
            }
        };
    }
}