package carshire.ui;

import carshire.SellerService;
import carshire.domain.Seller;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Michał
 */
@Component
public class TabManagerSubtabEmployees {

    @Autowired
    SellerService service;

    private CarsPresenter main;

    /**
     *
     * @param carsPresenter
     */
    public void init(CarsPresenter carsPresenter) {
        main = carsPresenter;
    }

    //Manager tab - Employee subtab
    @FXML
    TableView<Seller> sellers;
    @FXML
    TableColumn<Seller, String> firstNameColumn;
    @FXML
    TableColumn<Seller, String> lastNameColumn;
    @FXML
    TableColumn<Seller, String> loginColumn;
    @FXML
    TableColumn<Seller, String> eMailColumn;
    @FXML
    TableColumn<Seller, String> passwordColumn;
    @FXML
    TableColumn<Seller, String> cityColumn;
    @FXML
    TableColumn<Seller, String> streetColumn;
    @FXML
    TableColumn<Seller, String> houseNumberColumn;
    @FXML
    TableColumn<Seller, Seller.Rights> rightsColumn;
    @FXML
    TextField id, firstName, lastName, login, eMail, password, city, street, houseNumber, rights;
    @FXML
    Label info;

    /**
     *
     */
    @FXML
    public void btnAddEmployee() {
        if (firstName.getText().isEmpty() || lastName.getText().isEmpty()
                || login.getText().isEmpty()) {
            info.setText("Imię, nazwisko i login są wymagane");
        } else {
            Seller seller = new Seller();
            if (!id.getText().isEmpty()) {
                seller.setId(Long.parseLong(id.getText()));
            }
            seller.setFirstName(firstName.getText());
            seller.setLastName(lastName.getText());
            seller.setLogin(login.getText());
            seller.setEMail(eMail.getText());
            seller.setPassword(password.getText());
            seller.setCity(city.getText());
            seller.setStreet(street.getText());
            seller.setHouseNumber(houseNumber.getText());
            seller.setRights(Seller.Rights.Employee);

            try {
                service.save(seller);
                main.deleteAllSellersViews();
                main.addAllSellersViews();

            } catch (RuntimeException a) {
                a.printStackTrace();
                info.setText("Taki login jest już zajęty");
            }
        }
    }

    /**
     *
     */
    @FXML
    public void btnDeleteEmployee() {
        if (sellers.getSelectionModel().isEmpty()) {

        } else {
            Seller seller = sellers.getSelectionModel().getSelectedItem();
            main.deleteAllSellersViews();
            service.delete(seller);
            main.addAllSellersViews();
            btnClearEmployee();
        }
    }

    /**
     *
     */
    @FXML
    public void btnClearEmployee() {
        id.clear();
        firstName.clear();
        lastName.clear();
        login.clear();
        eMail.clear();
        password.clear();
        city.clear();
        street.clear();
        houseNumber.clear();
        street.clear();
    }

    /**
     *
     */
    public void fillTextFields() {
        Seller seller = sellers.getSelectionModel().getSelectedItem();
        id.setText(seller.getId().toString());
        firstName.setText(seller.getFirstName());
        lastName.setText(seller.getLastName());
        login.setText(seller.getLogin());
        eMail.setText(seller.getEMail());
        password.setText(seller.getPassword());
        city.setText(seller.getCity());
        street.setText(seller.getStreet());
        houseNumber.setText(seller.getHouseNumber());
    }

    void configureTable() {
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<Seller, String>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<Seller, String>("lastName"));
        loginColumn.setCellValueFactory(new PropertyValueFactory<Seller, String>("login"));
        eMailColumn.setCellValueFactory(new PropertyValueFactory<Seller, String>("eMail"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<Seller, String>("password"));
        cityColumn.setCellValueFactory(new PropertyValueFactory<Seller, String>("city"));
        streetColumn.setCellValueFactory(new PropertyValueFactory<Seller, String>("street"));
        houseNumberColumn.setCellValueFactory(new PropertyValueFactory<Seller, String>("houseNumber"));
        rightsColumn.setCellValueFactory(new PropertyValueFactory<Seller, Seller.Rights>("rights"));
    }

    void fillTable() {
        configureTable();
        service.findAllEmployess().stream().forEach(v -> sellers.getItems().add(v));
        sellers.getSelectionModel().selectFirst();
    }

    void deleteAllViewRecords() {
        service.findAllEmployess().stream().forEach(v -> sellers.getItems().remove(v));
    }

    void addAllViewRecords() {
        service.findAllEmployess().stream().forEach(v -> sellers.getItems().add(v));
    }
}
