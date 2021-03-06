package carshire.ui;

import carshire.SellerService;
import carshire.domain.Seller;
import carshire.domain.Seller.Rights;
import java.io.File;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 *
 * @author Kuba
 */
@Component
public class CarsPresenter {

    TabManagerSubtabCars tabManagerSubtabCars;
    TabManagerSubtabCarsDiscount tabManagerSubtabCarsDiscount;
    TabEmployeeSubtabClients tabEmployeeSubtabClients;
    TabManagerSubtabClientsDiscount tabManagerSubtabClientsDiscount;
    TabManagerSubtabEmployees tabManagerSubtabEmployees;
    TabAdminSubtabManagers tabAdminSubtabManagers;
    TabEmployeeSubtabInvoice tabEmployeeSubtabInvoice;
    TabEmployeeSubtabHire tabEmployeeSubtabHire;
    TabEmployeeSubtabHireReturn tabEmployeeSubtabHireReturn;
    TabManagerSubtabReport tabManagerSubtabReport;
    SellerService sellerService;

    /**
     *
     * @param tabManagerSubtabCars
     * @param tabManagerSubtabCarsDiscount
     * @param tabEmployeeSubtabClients
     * @param tabManagerSubtabClientsDiscount
     * @param tabManagerSubtabEmployees
     * @param tabAdminSubtabManagers
     * @param tabEmployeeSubtabInvoice
     * @param tabEmployeeSubtabHire
     * @param sellerService
     * @param tabEmployeeSubtabHireReturn
     * @param tabManagerSubtabReport
     */
    @Autowired
    public CarsPresenter(TabManagerSubtabCars tabManagerSubtabCars,
            TabManagerSubtabCarsDiscount tabManagerSubtabCarsDiscount,
            TabEmployeeSubtabClients tabEmployeeSubtabClients,
            TabManagerSubtabClientsDiscount tabManagerSubtabClientsDiscount,
            TabManagerSubtabEmployees tabManagerSubtabEmployees,
            TabAdminSubtabManagers tabAdminSubtabManagers,
            TabEmployeeSubtabInvoice tabEmployeeSubtabInvoice,
            TabEmployeeSubtabHire tabEmployeeSubtabHire,
            SellerService sellerService,
            TabEmployeeSubtabHireReturn tabEmployeeSubtabHireReturn,
            TabManagerSubtabReport tabManagerSubtabReport) {
        this.tabManagerSubtabCars = tabManagerSubtabCars;
        this.tabManagerSubtabCarsDiscount = tabManagerSubtabCarsDiscount;
        this.tabEmployeeSubtabClients = tabEmployeeSubtabClients;
        this.tabManagerSubtabClientsDiscount = tabManagerSubtabClientsDiscount;
        this.tabManagerSubtabEmployees = tabManagerSubtabEmployees;
        this.tabAdminSubtabManagers = tabAdminSubtabManagers;
        this.tabEmployeeSubtabInvoice = tabEmployeeSubtabInvoice;
        this.tabEmployeeSubtabHire = tabEmployeeSubtabHire;
        this.sellerService = sellerService;
        this.tabEmployeeSubtabHireReturn = tabEmployeeSubtabHireReturn;
        this.tabManagerSubtabReport = tabManagerSubtabReport;
    }

    //Main tabs - Admin, manager, employee
    @FXML
    private Tab adminTab;
    @FXML
    private Tab managerTab;
    @FXML
    private Tab employeeTab;

    @FXML
    TextField login;
    @FXML
    TextField password;
    @FXML
    Label info;

    Seller loggedSeller;

    /**
     *
     */
    @FXML
    public void initialize() {
        disableTabs();

        tabManagerSubtabCars.init(this);
        tabManagerSubtabCarsDiscount.init(this);
        fillCarsTables();

        tabEmployeeSubtabClients.init(this);
        tabManagerSubtabClientsDiscount.init(this);
        fillClientsTables();

        tabManagerSubtabEmployees.init(this);
        tabAdminSubtabManagers.init(this);
        fillSellersTables();

        tabEmployeeSubtabInvoice.init(this);
        tabEmployeeSubtabHire.init(this);
        tabEmployeeSubtabHireReturn.init(this);
        fillHiresTables();

        tabManagerSubtabCars.cars.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                tabManagerSubtabCars.fillTextFields();
            }
        });

        tabAdminSubtabManagers.sellers.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                tabAdminSubtabManagers.fillTextFields();
            }
        });

        tabManagerSubtabEmployees.sellers.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                tabManagerSubtabEmployees.fillTextFields();
            }
        });

        tabManagerSubtabClientsDiscount.clients.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                tabManagerSubtabClientsDiscount.fillTextFields();
            }
        });

        tabManagerSubtabCarsDiscount.cars.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                tabManagerSubtabCarsDiscount.fillTextFields();
            }
        });

        tabEmployeeSubtabClients.clients.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                tabEmployeeSubtabClients.fillTextFields();
            }
        });

        tabEmployeeSubtabHire.cars.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                tabEmployeeSubtabHire.fillCarTextFields();
            }
        });

        tabEmployeeSubtabHire.clients.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                tabEmployeeSubtabHire.fillClientTextFields();
            }
        });

        tabEmployeeSubtabHireReturn.cars.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                tabEmployeeSubtabHireReturn.fillTextFields();
            }
        });

        tabEmployeeSubtabInvoice.hires.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                tabEmployeeSubtabInvoice.fillTextFields();
            }
        });

        tabEmployeeSubtabInvoice.directoryChooser.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                Stage primaryStage = null;
                File selectedDirectory
                        = directoryChooser.showDialog(primaryStage);

                if (selectedDirectory == null) {
                    tabEmployeeSubtabInvoice.directory.setText("No Directory selected");
                } else {
                    tabEmployeeSubtabInvoice.directory.setText(selectedDirectory.getAbsolutePath());
                }
            }
        });

        tabManagerSubtabReport.directoryChooser.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                Stage primaryStage = null;
                File selectedDirectory
                        = directoryChooser.showDialog(primaryStage);

                if (selectedDirectory == null) {
                    tabManagerSubtabReport.directory.setText("No Directory selected");
                } else {
                    tabManagerSubtabReport.directory.setText(selectedDirectory.getAbsolutePath());
                }
            }
        });
    }
    
    void fillHiresTables() {
        tabEmployeeSubtabInvoice.fillTable();
        tabEmployeeSubtabHire.fillTable();
        tabEmployeeSubtabHireReturn.fillTable();
    }

    void fillSellersTables() {
        tabManagerSubtabEmployees.fillTable();
        tabAdminSubtabManagers.fillTable();
    }

    void fillClientsTables() {
        tabEmployeeSubtabClients.fillTable();
        tabManagerSubtabClientsDiscount.fillTable();
    }

    void fillCarsTables() {
        tabManagerSubtabCars.fillTable();
        tabManagerSubtabCarsDiscount.fillTable();
    }

    void deleteAllHiresViews() {
        tabEmployeeSubtabInvoice.deleteAllViewRecords();
        tabEmployeeSubtabHireReturn.deleteAllViewRecords();
    }

    void deleteAllSellersViews() {
        tabAdminSubtabManagers.deleteAllViewRecords();
        tabManagerSubtabEmployees.deleteAllViewRecords();
    }

    void deleteAllCarsViews() {
        tabManagerSubtabCars.deleteAllViewRecords();
        tabManagerSubtabCarsDiscount.deleteAllViewRecords();
        tabEmployeeSubtabHire.deleteAllViewRecords();
    }

    void deleteAllClientsViews() {
        tabManagerSubtabClientsDiscount.deleteAllViewRecords();
        tabEmployeeSubtabClients.deleteAllViewRecords();
        tabEmployeeSubtabHire.deleteAllViewRecords();
    }

    void addAllSellersViews() {
        tabAdminSubtabManagers.addAllViewRecords();
        tabManagerSubtabEmployees.addAllViewRecords();
    }

    void addAllHiresViews() {
        tabEmployeeSubtabInvoice.addAllViewRecords();
        tabEmployeeSubtabHireReturn.addAllViewRecords();
    }

    void addAllCarsViews() {
        tabManagerSubtabCars.addAllViewRecords();
        tabManagerSubtabCarsDiscount.addAllViewRecords();
        tabEmployeeSubtabHire.addAllViewRecords();
    }

    void addAllClientsViews() {
        tabManagerSubtabClientsDiscount.addAllViewRecords();
        tabEmployeeSubtabClients.addAllViewRecords();
        tabEmployeeSubtabHire.addAllViewRecords();
    }

    /**
     * This method provides loging into application
     */
    @FXML
    public void btnLogin() {
        if (!login.getText().isEmpty()
                && !password.getText().isEmpty()) {

            loggedSeller = sellerService.findByLogin(login.getText());
            Optional<Seller> sellerOpt = Optional.ofNullable(loggedSeller);

            if (sellerOpt.isPresent() && loggedSeller.getPassword().equals(password.getText())) {
                if (loggedSeller.getRights() == Rights.Admin) {
                    btnEnableAdmin();
                } else if (loggedSeller.getRights() == Rights.Manager) {
                    btnEnableManager();
                } else if (loggedSeller.getRights() == Rights.Employee) {
                    btnEnableEmployee();
                }
                info.setText("Zalogowany jako: " + loggedSeller.getFirstName() + " " + loggedSeller.getLastName());
            } else {
                info.setText("Nieudana próba");
            }
        } else {
            info.setText("Nieudana próba");
        }
    }

    /**
     *
     */
    @FXML
    public void disableTabs() {
        adminTab.setDisable(true);
        managerTab.setDisable(true);
        employeeTab.setDisable(true);
        info.setText("");
    }

    private void btnEnableAdmin() {
        adminTab.setDisable(false);
        managerTab.setDisable(false);
        employeeTab.setDisable(false);
    }

    private void btnEnableManager() {
        adminTab.setDisable(true);
        managerTab.setDisable(false);
        employeeTab.setDisable(false);
    }

    private void btnEnableEmployee() {
        adminTab.setDisable(true);
        managerTab.setDisable(true);
        employeeTab.setDisable(false);
    }
}
