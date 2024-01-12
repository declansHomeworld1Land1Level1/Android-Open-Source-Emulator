import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

public class Device implements Serializable {

    @SerializedName("identifier")
    private UUID id;

    @SerializedName("fileName")
    private String fileName;

    @SerializedName("fileExtension")
    private String fileExtension;

    @SerializedName("totalSpaceGB")
    private Double totalSpaceGB;

    @SerializedName("freeSpaceGB")
    private Double freeSpaceGB;

    @SerializedName("lastUsed")
    private ZonedDateTime lastUsed;

    /**
     * Primary constructor instantiating a brand-new device entry.
     *
     * @param uuid           Unique identifier.
     * @param fileName       Human-friendly title of the boot image file.
     * @param fileExtension  Associated extension suffix of the boot image file.
     * @param totalSpaceGB    Compute volume attributed to the device.
     * @param freeSpaceGB     Amount of spare capacity left vacant.
     * @param lastUsed       Moment tracking the last time the device was utilized.
     */
    public Device(UUID uuid, String fileName, String fileExtension, double totalSpaceGB, double freeSpaceGB, ZonedDateTime lastUsed) {
        this.id = uuid;
        this.fileName = fileName;
        this.fileExtension = fileExtension;
        this.totalSpaceGB = totalSpaceGB;
        this.freeSpaceGB = freeSpaceGB;
        this.lastUsed = lastUsed;
    }

    /**
     * Derives a random alphanumeric string acting as a universally unique identifier.
     *
     * @return Randomized alpha-numeric sequence.
     */
    public static UUID deriveUniqueIdentifier() {
        return UUID.randomUUID();
    }

    /**
     * Responsible for serializing boot image file paths to strings.
     *
     * @param newFilePath Absolute URI of the boot image file.
     */
    public void updateBootImageFilePath(String newFilePath) {
        this.fileName = new File(newFilePath).getName();
        this.fileExtension = FilenameUtils.getExtension(newFilePath);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public Double getTotalSpaceGB() {
        return totalSpaceGB;
    }

    public void setTotalSpaceGB(Double totalSpaceGB) {
        this.totalSpaceGB = totalSpaceGB;
    }

    public Double getFreeSpaceGB() {
        return freeSpaceGB;
    }

    public void setFreeSpaceGB(Double freeSpaceGB) {
        this.freeSpaceGB = freeSpaceGB;
    }

    public ZonedDateTime getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(ZonedDateTime lastUsed) {
        this.lastUsed = lastUsed;
    }
}
