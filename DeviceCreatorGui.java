/**
 * User interface catering to users wanting to specify a boot image file.
 */
class DeviceCreatorGui {

    // Declare variables
    private final JFrame parentFrame;
    private final DevicesDataModel devicesDataModel;

    /**
     * Secondary constructor receiving requisite references for establishing communication channels.
     *
     * @param parentFrame Ownership hierarchy root node.
     * @param devicesDataModel Model responsible for maintaining the state of device records.
     */
    public DeviceCreatorGui(JFrame parentFrame, DevicesDataModel devicesDataModel) {
        this.parentFrame = parentFrame;
        this.devicesDataModel = devicesDataModel;

        // Initialize UI components
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);

        Arrays.stream(FileTypes.values()).forEach(fileType -> fileChooser.addChoosableFileFilter(fileType.choosableFileFilter));

        int result = fileChooser.showOpenDialog(parentFrame);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            assert file != null;

            UUID uuid = Device.deriveUniqueIdentifier();
            String fileName = file.getName();
            String fileExtension = FilenameUtils.getExtension(fileName);

            // TODO: Determine boot image size to compute available space
            double totalSpaceGB = 0.0;
            double freeSpaceGB = 0.0;

            devicesDataModel.appendNewDeviceEntry(uuid, fileName, fileExtension, totalSpaceGB, freeSpaceGB, ZonedDateTime.now(ZoneId.of("UTC")));
            devicesDataModel.serializeToJsonFile();
        }
    }
}

enum FileTypes {
    DIMAGEDISK("DIMG Disk Images (*.dimg)", new ChoosableFileFilterImpl(".dimg")),
    TAR("Tar Archives (*.tar)", new ChoosableFileFilterImpl(".tar")),
    TARGZIP("Compressed Tar Archive (*.tar.gz,*.tgz)", new ChoosableFileFilterImpl(".tar.gz", ".tgz")),
    IMG("Disk Images (*.img)", new ChoosableFileFilterImpl(".img")),
    ISO("Optical Disc Images (*.iso)", new ChoosableFileFilterImpl(".iso")),
    MD5TARBALL("MD5 Checksummed Tarballs (*.tar.md5, *.TAR.MD5)", new ChoosableFileFilterImpl(".tar.md5", ".TAR.MD5")),
    MD1("MD1 Summed Binaries (*.md1)", new ChoosableFileFilterImpl(".md1"));

    private final String description;
    private final ChoosableFileFilterImpl choosableFileFilter;

    FileTypes(String description, ChoosableFileFilterImpl... filters) {
        this.description = description;
        this.choosableFileFilter = new ChoosableFileFilterImpl(filters);
    }

    public String getDescription() {
        return description;
    }

    public ChoosableFileFilterImpl getChoosableFileFilter() {
        return choosableFileFilter;
    }
}

class ChoosableFileFilterImpl implements FileFilter {

    private final List<String> extensions;

    ChoosableFileFilterImpl(String... extensions) {
        this.extensions = Arrays.asList(extensions);
    }

    @Override
    public boolean accept(File file) {
        if (file.isDirectory()) {
            return true;
        }

        String fileName = file.getName();
        return extensions.stream().anyMatch(extension -> fileName.toLowerCase().endsWith(extension));
    }

    @Override
    public String toString() {
        return descriptionsToString();
    }

    String descriptionsToString() {
        StringBuilder sb = new StringBuilder();

        IntStream.range(0, extensions.size())
            .forEach(index -> {
                sb.append(extensions.get(index));

                if (index != extensions.size() - 1) {
                    sb.append(", ");
                }
            });

        return sb.toString();
    }
}

/**
 * Control plane navigating the lifecycle of the virtual devices.
 */
class DeviceControllerGui {

    // Declare variables
    private final JFrame parentFrame;
    private final DevicesDataModel devicesDataModel;

    /**
     * Secondary constructor accepting ownership hierarchy roots and models for coordinating updates.
     *
     * @param parentFrame Parent container holding child panels.
     * @param devicesDataModel State management hub.
     */
    public DeviceControllerGui(JFrame parentFrame, DevicesDataModel devicesDataModel) {
        this.parentFrame = parentFrame;
        this.devicesDataModel = devicesDataModel;

        // Register hotkeys
        registerHotKeyListeners();

        // Monitor hotkey presses
        monitorHotKeys();
    }

    /**
     * Hooks listeners to detect hotkey combinations.
     */
    private void registerHotKeyListeners() {
        InputMap map = parentFrame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        map.put(KeyStroke.getKeyStroke("ctrl R"), "startDevice");
    }

    /**
     * Monitors keyboard inputs scanning for prescribed hotkey sequences.
     */
    private void monitorHotKeys() {
        ActionMap map = parentFrame.getRootPane().getActionMap();

        map.put("startDevice", new StartDeviceAction());
    }

    private class StartDeviceAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO: Handle start device logic
        }
    }
}
