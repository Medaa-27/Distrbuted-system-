package client;

import java.io.*;
import java.util.Properties;

public class ClientConfig {
    private static final File CONFIG_FILE = new File(System.getProperty("user.home"), ".dwms_client_config.properties");
    private static Properties props = new Properties();

    static {
        // defaults
        props.setProperty("host", "localhost");
        props.setProperty("ports", "5000,5001");
        load();
    }

    private static void load() {
        if (CONFIG_FILE.exists()) {
            try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
                props.load(fis);
            } catch (IOException ignored) {
            }
        }
    }

    public static String getHost() {
        return props.getProperty("host", "localhost");
    }

    public static int[] getPorts() {
        String p = props.getProperty("ports", "5000,5001");
        String[] parts = p.split(",");
        int[] ports = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            try {
                ports[i] = Integer.parseInt(parts[i].trim());
            } catch (NumberFormatException ex) {
                ports[i] = 5000;
            }
        }
        return ports;
    }

    public static void setHost(String host) {
        props.setProperty("host", host);
        save();
    }

    public static void setPorts(int[] ports) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ports.length; i++) {
            if (i > 0)
                sb.append(',');
            sb.append(ports[i]);
        }
        props.setProperty("ports", sb.toString());
        save();
    }

    private static void save() {
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            props.store(fos, "DWMS client config");
        } catch (IOException ignored) {
        }
    }
}
