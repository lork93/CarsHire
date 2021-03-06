package carshire.ui;

import carshire.CarService;
import carshire.ClientService;
import carshire.HireService;
import carshire.domain.Car;
import carshire.domain.Client;
import carshire.domain.Hire;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.Month;
import static java.time.temporal.ChronoUnit.DAYS;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tornadofx.control.DateTimePicker;

/**
 *
 * @author Kuba
 */
@Component
public class TabEmployeeSubtabHire {

    @Autowired
    ClientService clientService;

    @Autowired
    CarService carService;

    @Autowired
    HireService hireService;

    private CarsPresenter main;

    /**
     *
     * @param carsPresenter
     */
    public void init(CarsPresenter carsPresenter) {
        main = carsPresenter;
    }

    //Employee tab - Client subtab
    @FXML
    TableView<Client> clients;
    @FXML
    TableColumn<Client, String> firstNameColumn;
    @FXML
    TableColumn<Client, String> lastNameColumn;
    @FXML
    TableColumn<Client, Integer> discountClientColumn;
    @FXML
    TextField idClient;

    //Manager tab - CarsPresenter subtab
    @FXML
    TableView<Car> cars;
    @FXML
    TableColumn<Car, String> manufacturerColumn;
    @FXML
    TableColumn<Car, BigDecimal> pricePerDayAfterDiscountColumn;
    @FXML
    TableColumn<Car, Car.CarStatus> statusColumn;
    @FXML
    TableColumn<Car, String> registrationNumberColumn;
    @FXML
    TextField idCar;

    @FXML
    TextField totalPay;
    @FXML
    DateTimePicker endDate;
    @FXML
    DateTimePicker startDate;

    Car car;
    LocalDateTime end;
    LocalDateTime start;
    BigDecimal totalPayBD;

    void configureTable() {
        //Clients table
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<Client, String>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<Client, String>("lastName"));
        discountClientColumn.setCellValueFactory(new PropertyValueFactory<Client, Integer>("discount"));

        //Cars table
        manufacturerColumn.setCellValueFactory(new PropertyValueFactory<Car, String>("manufacturer"));
        pricePerDayAfterDiscountColumn.setCellValueFactory(new PropertyValueFactory<Car, BigDecimal>("pricePerDayAfterDiscount"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<Car, Car.CarStatus>("status"));
        registrationNumberColumn.setCellValueFactory(new PropertyValueFactory<Car, String>("registrationNumber"));
    }

    /**
     * This method provides calculating total payment for hire,
     * If somebody choose hiring from today to next day with same hour and minute, it will be one day,
     * but if will be one minute more, it will be two days.
     */
    @FXML
    public void btnCalculateTotalPay() {
        if (!idCar.getText().isEmpty()
                && !idClient.getText().isEmpty()  
                && endDate.getValue() != null
                && startDate.getValue() != null) {

            Integer startYear = endDate.getDateTimeValue().getYear();
            Integer startMonth = endDate.getDateTimeValue().getMonthValue();
            Integer startDay = endDate.getDateTimeValue().getDayOfMonth();
            Integer startHour = endDate.getDateTimeValue().getHour();
            Integer startMinute = endDate.getDateTimeValue().getMinute();
            end = LocalDateTime.of(startYear, Month.of(startMonth), startDay, startHour, startMinute);

            Integer endYear = startDate.getDateTimeValue().getYear();
            Integer endMonth = startDate.getDateTimeValue().getMonthValue();
            Integer endDay = startDate.getDateTimeValue().getDayOfMonth();
            Integer endHour = startDate.getDateTimeValue().getHour();
            Integer endMinute = startDate.getDateTimeValue().getMinute();
            start = LocalDateTime.of(endYear, Month.of(endMonth), endDay, endHour, endMinute);

            long numberOfHireDays = DAYS.between(start, end);
            
            //ifTimeOfDayIsExceed - this value is checking is day is exceed or not, 1 if passed, 0 if not
            Integer ifTimeOfDayIsExceed = end.toLocalTime().compareTo(start.toLocalTime());

            if(ifTimeOfDayIsExceed.equals(1)){numberOfHireDays++;}

            Client client = clientService.findById(Long.parseLong(idClient.getText()));
            car = cars.getSelectionModel().getSelectedItem();
            BigDecimal carPricePerMonth = car.getPricePerDayAfterDiscount();
            BigDecimal clientDiscount = new BigDecimal(client.getDiscount().toString());
            BigDecimal numberOfHireDaysBD = new BigDecimal(numberOfHireDays);

            totalPayBD = (carPricePerMonth.multiply(numberOfHireDaysBD))
                    .multiply(((new BigDecimal(BigInteger.ONE)).subtract(clientDiscount.divide(new BigDecimal("100")))));

            totalPayBD = totalPayBD.setScale(2, BigDecimal.ROUND_HALF_EVEN);

            if(totalPayBD.intValue()<0){
                
            }else{
                totalPay.setText(totalPayBD.toString());
            }
            
        }
    }

    /**
     * 
     */
    @FXML
    public void btnClearHireCar() {
        idCar.clear();
        idClient.clear();
        totalPay.clear();
        endDate.getEditor().clear();
        startDate.getEditor().clear();
    }

    /**
     * This method provides adding new hire, if client will agree.
     */
    @FXML
    public void btnHireCar() {
        if(!idCar.getText().isEmpty()
                && !idClient.getText().isEmpty()  
                && endDate.getValue() != null
                && startDate.getValue() != null
                && !totalPay.getText().isEmpty()){
            
            Hire hire = new Hire();
            hire.setClientId(Long.parseLong(idClient.getText()));
            hire.setCarId(Long.parseLong(idCar.getText()));
            hire.setSellerId(main.loggedSeller.getId());
            hire.setHireDate(start);
            hire.setHireEndDate(end);
            hire.setStatus(Hire.HireStatus.NotPaid);
            hire.setPriceForHire(totalPayBD);
            hire.setDefaultInterest(new BigDecimal("0"));
            
            main.deleteAllCarsViews();
            main.deleteAllHiresViews();
         
            car.setStatus(Car.CarStatus.NotAvalible);
            carService.save(car);
            hireService.save(hire);
            
            main.addAllCarsViews();
            main.addAllHiresViews();
            
            btnClearHireCar();
        }
    }

    void fillTable() {
        configureTable();
        carService.findAllAvalibleCars().stream().forEach(v -> cars.getItems().add(v));
        cars.getSelectionModel().selectFirst();

        clientService.findAllClients().stream().forEach(v -> clients.getItems().add(v));
        clients.getSelectionModel().selectFirst();
    }

    void deleteAllViewRecords() {
        carService.findAllAvalibleCars().stream().forEach(v -> cars.getItems().remove(v));
        clientService.findAllClients().stream().forEach(v -> clients.getItems().remove(v));
    }

    void addAllViewRecords() {
        carService.findAllAvalibleCars().stream().forEach(v -> cars.getItems().add(v));
        clientService.findAllClients().stream().forEach(v -> clients.getItems().add(v));
    }

    /**
     *
     */
    public void fillCarTextFields() {
        car = cars.getSelectionModel().getSelectedItem();
        idCar.setText(car.getId().toString());
    }

    /**
     *
     */
    public void fillClientTextFields() {
        Client client = clients.getSelectionModel().getSelectedItem();
        idClient.setText(client.getId().toString());
    }
}
