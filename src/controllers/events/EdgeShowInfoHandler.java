package controllers.events;

import data.ConstStrings;
import data.DataAccessor;
import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class EdgeShowInfoHandler {

    //obsługa kliknięcia na krawędź
    public final EventHandler<MouseEvent> lineOnMouseEventHandler;

    public EdgeShowInfoHandler(controllers.FXMLDocumentController FXMLDocumentController) {
        this.lineOnMouseEventHandler = (MouseEvent t) -> {
            if (t.getButton() == MouseButton.SECONDARY) {
                DataAccessor.setAnalyzedEdge(controllers.FXMLDocumentController.edgeLines.get(t.getSource()));
                FXMLDocumentController.showFXML(ConstStrings.SHOW_EDGE_FXML_RES, ConstStrings.SHOW_EDGE_TITLE);
            }
        };
    }
}