/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import data.ConstStrings;
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

    private int page = ConstStrings.ZERO;
    
    private int itemsSize = ConstStrings.ZERO;

    private int getPage() {
        return page;
    }

    private void setPage(int page) {
        this.page = page;
    }

    private void setItemsSize(int itemsSize) {
        this.itemsSize = itemsSize;
    }

    /**
     * Initializes the controller class.
     *
     * @param url default URL
     * @param rb default ResourceBundle
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
        datasetTable.getItems().setAll(DataAccessor.getDataset().subList((getPage()) * ConstStrings.EXAMPLES_PAGE_SIZE, ((ConstStrings.ONE + getPage()) * ConstStrings.EXAMPLES_PAGE_SIZE - ConstStrings.ONE)));
        setItemsSize(DataAccessor.getDataset().size());
    }

    @FXML
    private void firstObjects(ActionEvent event) {
        setPage(ConstStrings.ZERO);
        datasetTable.getItems().clear();
        datasetTable.getItems().setAll(DataAccessor.getDataset().subList((getPage()) * ConstStrings.EXAMPLES_PAGE_SIZE, ((ConstStrings.ONE + getPage()) * ConstStrings.EXAMPLES_PAGE_SIZE - ConstStrings.ONE)));
        firstObjects.setDisable(true);
        previousObjects.setDisable(true);
        nextObjects.setDisable(false);
        lastObjects.setDisable(false);
    }

    @FXML
    private void previousObjects(ActionEvent event) {
        setPage(--page);
        datasetTable.getItems().clear();
        datasetTable.getItems().setAll(DataAccessor.getDataset().subList((getPage()) * ConstStrings.EXAMPLES_PAGE_SIZE, ((ConstStrings.ONE + getPage()) * ConstStrings.EXAMPLES_PAGE_SIZE - ConstStrings.ONE)));
        if (getPage() == ConstStrings.ZERO) {
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
        if (getPage() == (itemsSize / ConstStrings.EXAMPLES_PAGE_SIZE)) {
            nextObjects.setDisable(true);
            lastObjects.setDisable(true);
            datasetTable.getItems().setAll(DataAccessor.getDataset().subList((getPage()) * ConstStrings.EXAMPLES_PAGE_SIZE, itemsSize - ConstStrings.ONE));
        } else {
            datasetTable.getItems().setAll(DataAccessor.getDataset().subList((getPage()) * ConstStrings.EXAMPLES_PAGE_SIZE, ((ConstStrings.ONE + getPage()) * ConstStrings.EXAMPLES_PAGE_SIZE - ConstStrings.ONE)));
        }
        firstObjects.setDisable(false);
        previousObjects.setDisable(false);
    }

    @FXML
    private void lastObjects(ActionEvent event) {
        setPage(itemsSize / ConstStrings.EXAMPLES_PAGE_SIZE);
        datasetTable.getItems().clear();
        datasetTable.getItems().setAll(DataAccessor.getDataset().subList((getPage()) * ConstStrings.EXAMPLES_PAGE_SIZE, itemsSize - ConstStrings.ONE));
        firstObjects.setDisable(false);
        previousObjects.setDisable(false);
        nextObjects.setDisable(true);
        lastObjects.setDisable(true);
    }
}
