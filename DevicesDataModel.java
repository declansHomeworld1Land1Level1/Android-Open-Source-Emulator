import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Data model pertinent to virtual devices managed by the application.
 */
public class DevicesDataModel {

    public List<Device> devices;

    /**
     * Default constructor. Initializes an empty array of devices.
     */
    public DevicesDataModel() {
        devices = new ArrayList<>();
    }

    /**
     * Constructs a deep clone of the recipient devices collection.
     *
     * @param devices Collection of devices to be duplicated.
     */
    public DevicesDataModel(List<Device> devices) {
        this.devices = new ArrayList<>(devices);
    }

    /**
     * Retrieves the number of currently registered devices.
     *
     * @return Count of extant devices.
     */
    public int count() {
        return devices.size();
    }

    /**
     * Returns the index-based device entry.
     *
     * @param index Position of the sought device.
     * @return Desired device record.
     */
    public Device itemAtIndex(int index) {
        return devices.get(index);
    }

    /**
     * Append a novel device entity to the inventory.
     *
     * @param uuid           Unique ID identifying the device.
     * @param fileName       Title of the boot image file.
     * @param fileExtension  Extension suffix of the boot image file.
     * @param totalSpaceGB    Total space allocated to the device.
     * @param freeSpaceGB     Available unused space.
     * @param lastUsed       Timestamp indicating the last instant the device was accessed.
     */
    public void appendNewDeviceEntry(UUID uuid, String fileName, String fileExtension, double totalSpaceGB, double freeSpaceGB, ZonedDateTime lastUsed) {
        devices.add(new Device(uuid, fileName, fileExtension, totalSpaceGB, freeSpaceGB, lastUsed));
    }

    /**
     * Updates the file path attribute tied to the designated device record.
     *
     * @param index          Index position mapping to the targeted device entry.
     * @param newFilePath    Fresh boot image file path.
     */
    public void editDeviceBootImagePath(int index, String newFilePath) {
        devices.get(index).updateBootImageFilePath(newFilePath);
    }

    /**
     * Locates the device exhibit referenced by the furnished UUID token.
     *
     * @param id Identifier uniquely pinpointing a solitary device.
     * @return Requested device record. Null if none found.
     */
  
    public Device searchById(UUID id) {
        return devices.stream()
            .filter(device -> device.getId().equals(id))
            .findAny()
            .orElse(null);
    }

    /**
     * Serializes the JSON payload to persisted storage.
     *
     * @throws IOException Propagated from lower level IO layer.
     */
  
    public void serializeToJsonFile() throws IOException {
        Path filePath = Paths.get("device_config.json").toAbsolutePath();
        File jsonFile = filePath.toFile();

        if (!jsonFile.exists()) {
            jsonFile.createNewFile();
        } else {
            Files.deleteIfExists(filePath);
        }

        String serializedJson = new GsonBuilder().setPrettyPrinting().create().toJson(this);
        Files.write(filePath, serializedJson.getBytes());
    }

    /**
     * Materializes a deserialized JSON payload sourced from a file.
     *
     * @return Populated devices data model derived from the file.
     * @throws IOException Thrown when unable to read file.
     */

    public static DevicesDataModel deserializeFromJsonFile() throws IOException {
        Path filePath = Paths.get("device_config.json").toAbsolutePath();
        String serializedJson = Files.readString(filePath);
        return new GsonBuilder().create().fromJson(serializedJson, DevicesDataModel.class);
    }
}
