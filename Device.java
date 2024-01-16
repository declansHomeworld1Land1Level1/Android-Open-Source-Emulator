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
    public Screen() {
        private class Screen {

            import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import javax.imageio.ImageIO;
import javax.swing.*;

public class DirectFrameExtractor {
    private static final String PIPE_NAME = "my_pipe";
    private static VolatileImage lastFrame;
    private static volatile boolean stopFetchingFrames = false;

    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Direct Frame Extractor");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            JLabel label = new JLabel();
            frame.add(label, BorderLayout.CENTER);

            new Thread(() -> {
                while (!stopFetchingFrames) {
                    try {
                        BufferedImage frame = getLatestFrame();
                        if (frame != null) {
                            SwingUtilities.invokeLater(() -> lastFrame = createVolatileImage(frame.getWidth(), frame.getHeight()));
                            SwingUtilities.invokeLater(() -> drawImageIntoVolatileImage(lastFrame, frame));
                        }
                    } catch (Exception ignore) {
                    }
                }
            }).start();

            new Timer(100, (actionEvent) -> {
                if (lastFrame != null) {
                    Graphics g = lastFrame.getGraphics();
                    g.drawImage(lastFrame, 0, 0, null);
                    label.setIcon(new ImageIcon(lastFrame));
                    g.dispose();
                }
                frame.repaint();
            }).start();

            // Establish a connection to the pipe
            Selector selector = Selector.open();
            WatchKey watchKey = new FileInputStream(PIPE_NAME).getChannel().register(selector, SelectionKey.OP_READ, null);

            while (!stopFetchingFrames) {
                int n = selector.select();

                if (n > 0) {
                    for (Iterator<SelectionKey> i = selector.selectedKeys().iterator(); i.hasNext(); ) {
                        SelectionKey sk = i.next();

                        if (sk.isValid() && sk.isReadable()) {
                            ReadableByteChannel src = (ReadableByteChannel) sk.attachment();

                            ByteBuffer buffer = ByteBuffer.allocate(3 * 800 * 600);
                            while (src.read(buffer) != -1) {
                                buffer.rewind();
                                int w = 800;
                                int h = 600;
                                int pitch = w * 3;
                                int offset = 0;

                                for (int y = 0; y < h; ++y) {
                                    for (int x = 0; x < w; ++x) {
                                        int b = buffer.get(offset++);
                                        int g = buffer.get(offset++);
                                        int r = buffer.get(offset++);

                                        lastFrame.getGraphics().setColor(new Color(r, g, b));
                                        lastFrame.getGraphics().drawLine(x, y, x, y);
                                    }
                                }
                            }

                            src.close();
                            sk.cancel();
                            sk.interestOps(0);
                        }

                        i.remove();
                    }
                }
            }

            selector.close();
        });
    }

    private static VolatileImage createVolatileImage(int width, int height) {
        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        return gc.createCompatibleVolatileImage(width, height);
    }

    private static void drawImageIntoVolatileImage(VolatileImage volImg, BufferedImage bi) {
        Graphics2D g = volImg.createGraphics();
        g.drawImage(bi, 0, 0, null);
        g.dispose();
    }
}

            
        }
    }
}
