import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

public class Installer {

    private JFrame frame;

    public static void main(String... args) {
        new Installer().run();
    }

    private void run() {
        frame = new JFrame("Installing Scala MT bridge");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        buildContent();
        frame.pack();
        center();
        frame.setVisible(true);
        downloadLauncher();
    }

    private void downloadLauncher() {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            URL url = new URL(
                    "http://repo.typesafe.com/typesafe/ivy-releases/org.scala-sbt/sbt-launch/0.13.2/sbt-launch.jar"
            );
            inputStream = url.openStream();
            String path = System.getProperty("user.home") + File.separator + "sbt.jar";
            System.out.println(String.format("Downloading the launcher to %s", path));
            outputStream = new FileOutputStream(path);

            final int BUF_SIZE = 100 * 1024;
            byte [] buffer = new byte[BUF_SIZE];
            int numRead = inputStream.read(buffer);
            while (numRead > -1) {
                outputStream.write(buffer, 0, numRead);
                numRead = inputStream.read(buffer);
            }

            System.out.println("Download complete successfully");
        } catch (MalformedURLException e) {
            throw new RuntimeException("This shouldn't ever happen!", e);
        } catch (IOException e) {
            System.err.println(String.format("Failed to read URL: %s", e.getMessage()));
        }
        finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
            catch (IOException e) {}
        }
    }

    private void buildContent() {
        JPanel panel = new JPanel();
        Dimension size = new Dimension(500, 100);
        panel.setMinimumSize(size);
        panel.setPreferredSize(size);
        JLabel label = new JLabel("Downloading sbt-launcher.jar");
        panel.add(label);
        JButton ok = new JButton("OK");
        panel.add(ok);
        frame.getContentPane().add(panel);
    }

    private void center() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = frame.getSize();
        frame.setLocation(
                (screenSize.width - frameSize.width) / 2,
                (screenSize.height - frameSize.height) / 2
        );

        System.out.println(
                String.format("Width: %s, height: %s", frameSize.width, frameSize.height)
        );
    }

}