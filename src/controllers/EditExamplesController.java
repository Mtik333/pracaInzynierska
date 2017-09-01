/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import data.DataAccessor;
import data.roughsets.Attribute;
import data.roughsets.DataObject;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * FXML Controller class
 *
 * @author Mateusz
 */
public class EditExamplesController implements Initializable {

    @FXML
    private final TableView datasetTable = new TableView();

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        datasetTable.setEditable(false);
        for (int i = 0; i < DataAccessor.getAllAttributes().size(); i++) {
            TableColumn<DataObject, String> column = new TableColumn<>(DataAccessor.getAllAttributes().get(i).getName());
            column.setCellValueFactory(param -> {
                int index = param.getTableView().getColumns().indexOf(param.getTableColumn());
                List<Attribute> attributes = param.getValue().getAttributes();
                return new SimpleStringProperty(attributes.size() > index ? attributes.get(index).getValue() : null);
            });
            datasetTable.getColumns().add(column);
        }
//        datasetTable.getItems().setAll(DataAccessor.getDataset());
//        datasetTable.setEditable(false);
//        List<TableColumn> tableColumns = new ArrayList<>();
//        for (Attribute attribute : DataAccessor.getAllAttributes()){
//            TableColumn tableColumn = new TableColumn(attribute.getName());
//            tableColumn.setCellValueFactory(new PropertyValueFactory<>(attribute.getName()));
//            tableColumns.add(new TableColumn(attribute.getName()));
//        }
//        datasetTable.getColumns().addAll(tableColumns);
//        ObservableList data = FXCollections.observableArrayList(DataAccessor.getDataset());
//        datasetTable.getItems().setAll(data);
    }

}
