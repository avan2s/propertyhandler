package org.andy;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Andy on 05.03.2017.
 */
public class PropertyHandler {

    public static final String APP_MIN = "app.min";
    private Logger logger;

    private static final String CONFIG_DEFAULT_FILENAME = "config_default.properties";
    private static PropertyHandler instance;
    private Properties properties = null;

    private PropertyHandler() {
        this.properties = new Properties();
        this.logger = Logger.getLogger(this.getClass().getName());
        this.loadConfigFile();
    }

    public static synchronized PropertyHandler getInstance() {
        if (instance == null) {
            instance = new PropertyHandler();
        }
        return instance;
    }

    private InputStream loadConfigFile(){
        final String ownPropertiesPath = "./config.properties";
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(ownPropertiesPath);
            this.logger.log(Level.INFO, "Your own config.properties is used!");
        } catch (FileNotFoundException ex){
            inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(CONFIG_DEFAULT_FILENAME);
            this.logger.log(Level.INFO, "Default config.properties file is used!");
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                this.properties.load(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return inputStream;
    }

    public String getValue(String propKey) {
        return this.properties.getProperty(propKey);
    }

    public int getAppMin() {
        return Integer.valueOf(this.properties.getProperty(APP_MIN));
    }

}
