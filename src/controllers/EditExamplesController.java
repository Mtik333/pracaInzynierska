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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * FXML Controller class
 *
 * @author Mateusz
 */
public class EditExamplesController implements Initializable {

    @FXML
    private TableView datasetTable;
    @FXML
    private Button firstObjects;
    @FXML
    private Button previousObjects;
    @FXML
    private Button nextObjects;
    @FXML
    private Button lastObjects;

    private int page = 0;
    private int itemsSize = 0;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getItemsSize() {
        return itemsSize;
    }

    public void setItemsSize(int itemsSize) {
        this.itemsSize = itemsSize;
    }

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
        datasetTable.getItems().setAll(DataAccessor.getDataset().subList((getPage()) * 15, ((1 + getPage()) * 15 - 1)));
        setItemsSize(DataAccessor.getDataset().size());
    }

    @FXML
    private void firstObjects(ActionEvent event) {
        setPage(0);
        datasetTable.getItems().clear();
        datasetTable.getItems().setAll(DataAccessor.getDataset().subList((getPage()) * 15, ((1 + getPage()) * 15 - 1)));
        firstObjects.setDisable(true);
        previousObjects.setDisable(true);
        nextObjects.setDisable(false);
        lastObjects.setDisable(false);
    }

    @FXML
    private void previousObjects(ActionEvent event) {
        setPage(--page);
        datasetTable.getItems().clear();
        datasetTable.getItems().setAll(DataAccessor.getDataset().subList((getPage()) * 15, ((1 + getPage()) * 15 - 1)));
        if (getPage() == 0) {
            firstObjects.setDisable(true);
            previousObjects.setDisable(true);
        }
        nextObjects.setDisable(false);
        lastObjects.setDisable(false);
    }

    @FXML
    private void nextObjects(ActionEvent event) {
        setPage(++page);
        datasetTable.getItems().clear();
        if (getPage() == (itemsSize / 15)) {
            nextObjects.setDisable(true);
            lastObjects.setDisable(true);
            datasetTable.getItems().setAll(DataAccessor.getDataset().subList((getPage()) * 15, itemsSize - 1));
        } else {
            datasetTable.getItems().setAll(DataAccessor.getDataset().subList((getPage()) * 15, ((1 + getPage()) * 15 - 1)));
        }
        firstObjects.setDisable(false);
        previousObjects.setDisable(false);
    }

    @FXML
    private void lastObjects(ActionEvent event) {
        setPage(itemsSize / 15);
        datasetTable.getItems().clear();
        datasetTable.getItems().setAll(DataAccessor.getDataset().subList((getPage()) * 15, itemsSize - 1));
        firstObjects.setDisable(false);
        previousObjects.setDisable(false);
        nextObjects.setDisable(true);
        lastObjects.setDisable(true);
    }
}
