package com.katlex.scala_mt;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(downloadForm.getContainer());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setResizable(false);
        
        final Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                downloadLauncher();
            }
        }, "Downloader");
        t.start();
        
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                t.interrupt();
            }
        });
    }
    
    private URL createUrl(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private void downloadLauncher() {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        URL url = createUrl(
                "http://repo.typesafe.com/typesafe/ivy-releases/org.scala-sbt/sbt-launch/0.13.2/sbt-launch.jar"
        );
        
        try {
            URLConnection connection = url.openConnection();
            connection.connect();
            int total = connection.getHeaderFieldInt("Content-Length", -1);
            System.out.println(String.format("Content size is: %d", total));
            JProgressBar downloadProgress = downloadForm.getDownloadProgress();
            if (total > -1) {
                downloadProgress.setMaximum(total);
            }
            inputStream = connection.getInputStream();
            String targetPath = SBT_LAUNCHER_DEST;
            System.out.println(String.format("Downloading the launcher to %s", targetPath));
            outputStream = new FileOutputStream(targetPath);

            final int BUF_SIZE = 512;
            byte [] buffer = new byte[BUF_SIZE];
            int numRead;
            int loaded = 0;
            while ((numRead = inputStream.read(buffer)) > -1) {
                outputStream.write(buffer, 0, numRead);
                if (total > -1) {
                    downloadProgress.setValue(loaded += numRead);
                }
                if (Thread.interrupted()) {
                    break;
                }
            }

            if (loaded == total) {
                System.out.println("Download complete successfully");
            }
            else {
                System.out.println("Download interrupted by the user");
                new File(targetPath).delete();
            }
        } catch (IOException e) {
            System.err.printf("Failed to read the URL: %s\n", url.toString());
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
            catch (IOException e) {
                System.out.printf("Failed while closing streams %s", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static String path(String... args) {
        return Arrays.mkString(args, File.separator);
    }
}
