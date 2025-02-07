module com.aac.device {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;
    requires com.fasterxml.jackson.databind;
    requires freetts;


    opens com.aac.device to javafx.fxml;
    exports com.aac.device;
    exports com.aac.device.model;
}
