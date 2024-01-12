import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class DeviceSettingsGui extends JDialog {

    // Constants
    private static final int FIELD_WIDTH = 20;
    private static final String GENERIC_TEXT = "-";

    // Member Variables
    private final JFrame ownerFrame;
    private final DevicesDataModel devicesDataModel;
    private final Device currentDevice;
    private JTextField deviceNameField;
    private JComboBox<String> osSelectionBox;
    private JButton chooseBootImageButton;
    private JLabel currentBootImageLabel;
    private JCheckBox enableVgpuAccelerationCheckBox;
    private JTextField vrAmTextField;
    private JTextField screenWidthTextField;
    private JTextField screenHeightTextField;
    private JButton applyChangesButton;

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

        chooseBootImageButton = new JButton("Choose Different Boot Image");
        currentBootImageLabel = new JLabel("<html><body style='width: 200px; margin: auto'>No boot image selected</body></html>", SwingConstants.CENTER);

        enableVgpuAccelerationCheckBox = new JCheckBox("Enable vGPU Acceleration");
        enableVgpuAccelerationCheckBox.setSelected(false);

        vrAmTextField = new JTextField(FIELD_WIDTH);
        vrAmTextField.setText(GENERIC_TEXT);

        screenWidthTextField = new JTextField(FIELD_WIDTH);
        screenWidthTextField.setText(GENERIC_TEXT);

        screenHeightTextField = new JTextField(FIELD_WIDTH);
        screenHeightTextField.setText(GENERIC_TEXT);

        applyChangesButton = new JButton("Apply Changes");
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
                        .addComponent(screenWidthTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(screenHeightTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(vrAmTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFFERED_SIZE)
                        .addComponent(enableVgpuAccelerationCheckBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(currentBootImageLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(chooseBootImageButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(osSelectionBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(deviceNameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        );

        groupLayout.linkSize(SwingConstants.HORIZONTAL, new Component[]{deviceNameField, osSelectionBox, screenWidthTextField, screenHeightTextField, vrAmTextField, currentBootImageLabel, chooseBootImageButton});

        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addGroup(groupLayout.createSequentialGroup()
                    .addGap(10)
                    .addComponent(deviceNameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(10)
                    .addComponent(osSelectionBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(10)
                    .addComponent(currentBootImageLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(10)
                    .addComponent(chooseBootImageButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(10)
                    .addComponent(screenWidthTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(10)
                    .addComponent(screenHeightTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(10)
                    .addComponent(vrAmTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(10)
                    .addComponent(enableVgpuAccelerationCheckBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(10)
                    .addComponent(applyChangesButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(10))
        );
    }

    /**
     * Method attaching listeners to UI components.
     */
    private void wireListeners() {
        ItemListener bootImageListener = e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                chooseBootImage();
            }
        };

        MouseAdapter chooseBootImageAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                chooseBootImage();
            }
        };

        osSelectionBox.addItemListener(bootImageListener);
        currentBootImageLabel.addMouseListener(chooseBootImageAdapter);

        applyChangesButton.addActionListener(actionEvent -> {
            try {
                applyChanges();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(DeviceSettingsGui.this, "Invalid numeric value.", "Warning", JOptionPane.WARNING_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(DeviceSettingsGui.this, "Error reading file.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    /**
     * Method responsible for updating the UI components based on the current device.
     */
    private void updateFormFields() {
        deviceNameField.setText(Objects.nonNull(currentDevice.getDeviceName()) ? currentDevice.getDeviceName() : GENERIC_TEXT);
        osSelectionBox.setSelectedIndex(Objects.nonNull(currentDevice.getOperatingSystem()) ? getOsIndex(currentDevice.getOperatingSystem()) : 0);
        currentBootImageLabel.setText(Objects.nonNull(currentDevice.getBootImage()) ? currentDevice.getBootImage() : "No boot image selected");
        enableVgpuAccelerationCheckBox.setSelected(Objects.nonNull(currentDevice.isEnableVgpuAcceleration()) && currentDevice.isEnableVgpuAcceleration());
        vrAmTextField.setText(Objects.nonNull(currentDevice.getVRam()) ? String.valueOf(currentDevice.getVRam()) : GENERIC_TEXT);
        screenWidthTextField.setText(Objects.nonNull(currentDevice.getScreenWidth()) ? String.valueOf(currentDevice.getScreenWidth()) : GENERIC_TEXT);
        screenHeightTextField.setText(Objects.nonNull(currentDevice.getScreenHeight()) ? String.valueOf(currentDevice.getScreenHeight()) : GENERIC_TEXT);
    }

    /**
     * Opens a file picker for choosing a new boot image.
     */
    private void chooseBootImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new BootImageFileFilter());
        fileChooser.setMultiSelectionEnabled(false);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                InputStream stream = ImageIO.createImageInputStream(fileChooser.getSelectedFile());
                currentDevice.setBootImage(fileChooser.getSelectedFile().getCanonicalPath());
                currentBootImageLabel.setText("<html><body style='width: 200px; margin: auto'>" + fileChooser.getSelectedFile().getCanonicalPath() + "</body></html>");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(DeviceSettingsGui.this, "Error reading file.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Validates and applies the changes to the current device.
     *
     * @throws NumberFormatException Invalid numeric value
     * @throws IOException              Reading file error
     */
    private void applyChanges() throws NumberFormatException, IOException {
        String deviceName = deviceNameField.getText().strip();
        String operatingSystem = Objects.nonNull(osSelectionBox.getSelectedItem()) ? osSelectionBox.getSelectedItem().toString() : "";
        String bootImage = Objects.nonNull(currentDevice.getBootImage()) ? currentDevice.getBootImage() : "";
        Boolean enableVgpuAcceleration = enableVgpuAccelerationCheckBox.isSelected();
        int vrAm = Objects.nonNull(vrAmTextField.getText()) && !vrAmTextField.getText().isEmpty() ? Integer.parseInt(vrAmTextField.getText().strip()) : -1;
        int screenWidth = Objects.nonNull(screenWidthTextField.getText()) && !screenWidthTextField.getText().isEmpty() ? Integer.parseInt(screenWidthTextField.getText().strip()) : -1;
        int screenHeight = Objects.nonNull(screenHeightTextField.getText()) && !screenHeightTextField.getText().isEmpty() ? Integer.parseInt(screenHeightTextField.getText().strip()) : -1;

        if (validateInputs(deviceName, operatingSystem, bootImage, vrAm, screenWidth, screenHeight)) {
            currentDevice.setDeviceName(deviceName);
            currentDevice.setOperatingSystem(operatingSystem);
            currentDevice.setBootImage(bootImage);
            currentDevice.setEnableVgpuAcceleration(enableVgpuAcceleration);
            currentDevice.setVRam(vrAm);
            currentDevice.setScreenWidth(screenWidth);
            currentDevice.setScreenHeight(screenHeight);

            devicesDataModel.editDeviceProperties(currentDevice);
        } else {
            JOptionPane.showMessageDialog(DeviceSettingsGui.this, "Validation failed.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Validates the input values for the device settings.
     *
     * @param deviceName          New device name
     * @param operatingSystem     New operating system
     * @param bootImage           New boot image
     * @param vrAm               New vRAM size
     * @param screenWidth         New screen width
     * @param screenHeight        New screen height
     * @return True if all inputs pass validation, otherwise false.
     */
    private boolean validateInputs(String deviceName, String operatingSystem, String bootImage, int vrAm, int screenWidth, int screenHeight) {
        return !(deviceName.isEmpty() || operatingSystem.isEmpty() || bootImage.isEmpty() || vrAm <= 0 || screenWidth <= 0 || screenHeight <= 0);
    }

    /**
     * Gets the index of the given operating system in the osSelectionBox.
     *
     * @param operatingSystem Target operating system
     * @return Index in the osSelectionBox
     */
    private int getOsIndex(String operatingSystem) {
        for (int i = 0; i < osSelectionBox.getItemCount(); ++i) {
            if (osSelectionBox.getItemAt(i).equals(operatingSystem)) {
                return i;
            }
        }

        return 0;
    }

    /**
     * Custom File Filter for filtering out non-supported file types.
     */
    class BootImageFileFilter extends FileFilter {

        @Override
        public boolean accept(File file) {
            return file.isDirectory() || getSupportedExtensions().contains(getFileExtension(file));
        }

        @Override
        public String getDescription() {
            return "Boot Image Files";
        }

        /**
         * Gets the supported file extensions for boot images.
         *
         * @return Supported file extensions
         */
        private List<String> getSupportedExtensions() {
            return List.of("dimg", "tar", "gz", "iso", "img", "md5", "gz.md5");
        }

        /**
         * Gets the file extension of a given file.
         *
         * @param file Target file
         * @return Normalized file extension
         */
        private String getFileExtension(File file) {
            String fileName = file.getName().toLowerCase();
            return fileName.substring(fileName.lastIndexOf('.'));
        }
    }
}
