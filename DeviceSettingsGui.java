import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class DeviceSettingsGui extends JDialog {

    // Define constants for the fields
    private static final int FIELD_WIDTH = 20;
    private static final String GENERIC_TEXT = "-";

    // Define member variables
    private final JFrame ownerFrame;
    private final DevicesDataModel devicesDataModel;
    private final Device currentDevice;
    private JTextField deviceNameField;
    private JComboBox<String> osSelectionBox;
    private JTextField ramTextField;
    private JTextField cpuTextField;
    private JTextField storageTextField;

    /**
     * Class constructor taking the required arguments.
     *
     * @param ownerFrame Reference to the owner frame.
     * @param devicesDataModel Instance of the devices data model.
     * @param currentDevice Current device to be edited.
     */
    public DeviceSettingsGui(JFrame ownerFrame, DevicesDataModel devicesDataModel, Device currentDevice) {
        super(ownerFrame, "Edit Device Settings", Dialog.ModalityType.DOCUMENT_MODAL);
        this.ownerFrame = ownerFrame;
        this.devicesDataModel = devicesDataModel;
        this.currentDevice = currentDevice;

        // Build the UI components
        createFormFields();
        createButtons();
        arrangeFormElements();

        // Wire listener hooks
        wireListeners();

        // Establish initial field states
        updateFormFields();

        // Pack the dialog and centre it
        pack();
        setLocationRelativeTo(ownerFrame);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        setVisible(true);
    }

    /**
     * Method responsible for initializing the UI components.
     */
    private void createFormFields() {
        deviceNameField = new JTextField(FIELD_WIDTH);
        deviceNameField.setText(GENERIC_TEXT);

        String[] osOptions = {"Select Operating System", "Custom Os #1", "Custom Os #2"};
        osSelectionBox = new JComboBox<>(osOptions);
        osSelectionBox.setSelectedIndex(0);

        ramTextField = new JTextField(FIELD_WIDTH);
        ramTextField.setText(GENERIC_TEXT);

        cpuTextField = new JTextField(FIELD_WIDTH);
        cpuTextField.setText(GENERIC_TEXT);

        storageTextField = new JTextField(FIELD_WIDTH);
        storageTextField.setText(GENERIC_TEXT);
    }

    /**
     * Method accountable for organizing the UI components within the container.
     */
    private void arrangeFormElements() {
        Container contentPane = getContentPane();
        GroupLayout groupLayout = new GroupLayout(contentPane);
        contentPane.setLayout(groupLayout);

        groupLayout.setAutoCreateContainerGaps(true);
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addGap(10)
                    .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(deviceNameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(osSelectionBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(ramTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(cpuTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(storageTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
        );

        groupLayout.linkSize(SwingConstants.HORIZONTAL, new Component[]{deviceNameField, osSelectionBox, ramTextField, cpuTextField, storageTextField});

        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addGroup(groupLayout.createSequentialGroup()
                    .addGap(10)
                    .addComponent(deviceNameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(10)
                    .addComponent(osSelectionBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(10)
                    .addComponent(ramTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(10)
                    .addComponent(cpuTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(10)
                    .addComponent(storageTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(10))
        );
    }

    /**
     * Method attaching listeners to UI components.
     */
    private void wireListeners() {
        PropertyChangeListener listener = new FormFieldUpdater();

        deviceNameField.addPropertyChangeListener("text", listener);
        osSelectionBox.addItemListener(listener);
        ramTextField.addPropertyChangeListener("text", listener);
        cpuTextField.addPropertyChangeListener("text", listener);
        storageTextField.addPropertyChangeListener("text", listener);
    }

    /**
     * Method responsible for updating the UI components based on the current device.
     */
    private void updateFormFields() {
        deviceNameField.setText(Optional.ofNullable(currentDevice.getDeviceName()).orElse(GENERIC_TEXT));
        osSelectionBox.setSelectedIndex(getCurrentOsPosition());
        ramTextField.setText(formatMemory(currentDevice.getRam()));
        cpuTextField.setText(Integer.toString(currentDevice.getCpuCount()));
        storageTextField.setText(formatMemory(currentDevice.getStorage()));
    }

    /**
     * Helper method returning the correct position for the operating system within the combobox.
     *
     * @return Zero-based index of the OS within the dropdown menu.
     */
    private int getCurrentOsPosition() {
        String currentOs = Optional.ofNullable(currentDevice.getOperatingSystem()).orElse("");

        switch (currentOs) {
            case "Custom Os #1":
                return 1;
            case "Custom Os #2":
                return 2;
            default:
                return 0;
        }
    }

    /**
     * Helper method formatting the memory value to GiB units.
     *
     * @param memory Memory value to be formatted.
     * @return Formatted memory value represented in GiB.
     */
    private String formatMemory(double memory) {
        long gib = Math.round(memory / (1024 * 1024 * 1024));
        return Long.toString(gib);
    }

    /**
     * Custom PropertyChangeListener monitoring changes made to the form fields.
     */
    private class FormFieldUpdater implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            String fieldName = evt.getPropertyName();
            String fieldValue = (String) evt.getNewValue();

            switch (fieldName) {
                case "text":
                    if (!fieldValue.isEmpty() && !fieldValue.equalsIgnoreCase(GENERIC_TEXT)) {
                        updateDeviceBasedOnField("deviceName", fieldValue);
                    }
                    break;
                case "selectedIndex":
                    if (((JComboBox<?>) evt.getSource()).getSelectedIndex() > 0) {
                        updateDeviceBasedOnField("operatingSystem", ((JComboBox<?>) evt.getSource()).getSelectedItem());
                    }
                    break;
            }
        }
    }

    /**
     * Method updating the device properties based on the modified form fields.
     *
     * @param fieldName Field to be updated.
     * @param fieldValue Value of the field.
     */
    private void updateDeviceBasedOnField(String fieldName, Object fieldValue) {
        Device updatedDevice = currentDevice.clone();

        switch (fieldName) {
            case "deviceName":
                updatedDevice.setDeviceName((String) fieldValue);
                break;
            case "operatingSystem":
                updatedDevice.setOperatingSystem((String) fieldValue);
                break;
        }

        devicesDataModel.editDeviceProperties(updatedDevice);
    }
}
