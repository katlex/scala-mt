package com.katlex.scala_mt;

import javax.swing.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Installer {

    private JFrame frame;
    private DownloadForm downloadForm = new DownloadForm();

    public static String SBT_LAUNCHER_DEST = path(
            System.getProperty("user.home"),
            "sbt.jar"
    );

    public static void main(String... args) {
        new Installer().run();
    }

    private void run() {
        frame = new JFrame("Scala MT bridge");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(downloadForm.getContainer());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setResizable(false);
        downloadLauncher();
    }

    private void downloadLauncher() {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            URL url = new URL(
                    "http://repo.typesafe.com/typesafe/ivy-releases/org.scala-sbt/sbt-launch/0.13.2/sbt-launch.jar"
            );
            URLConnection connection = url.openConnection();
            connection.connect();
            int total = connection.getHeaderFieldInt("Content-Length", -1);
            System.out.println(String.format("Content size is: %d", total));
            JProgressBar downloadProgress = downloadForm.getDownloadProgress();
            if (total > -1) {
                downloadProgress.setMaximum(total);
            }
            inputStream = connection.getInputStream();
            String path = SBT_LAUNCHER_DEST;
            System.out.println(String.format("Downloading the launcher to %s", path));
            outputStream = new FileOutputStream(path);

            final int BUF_SIZE = 1024;
            byte [] buffer = new byte[BUF_SIZE];
            int numRead;
            int loaded = 0;
            while ((numRead = inputStream.read(buffer)) > -1) {
                outputStream.write(buffer, 0, numRead);
                if (total > -1) {
                    downloadProgress.setValue(loaded += numRead);
                }
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

    private static String path(String... args) {
        return Arrays.mkString(args, File.separator);
    }
}