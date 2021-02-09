package jssi.registrar.service;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.hyperledger.indy.sdk.LibIndy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ITON Solutions
 */
public class ConfigService {
    
    private static final Logger LOG = LoggerFactory.getLogger(ConfigService.class);
    private static ConfigService INSTANCE = null;
    
    public static final String NODE   = "node";
    public static final String ANCHOR = "anchor";
    public static final String AGENT  = "agent";
    
    private static final  FileBasedConfigurationBuilder<FileBasedConfiguration> builder
                = new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class);
    
    private Configuration config = null;
    private String genesis       = null;
    private String lib           = null;
    
    public static ConfigService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ConfigService();
        }
        return INSTANCE;
    }

    private ConfigService(){
        Parameters params = new Parameters();
        builder.configure(params.properties().setFileName("registrar.properties"));
        
        try {
            config = builder.getConfiguration();
        } catch (ConfigurationException e) {
            LOG.error(String.format("Configuration error %s", e.getMessage()));
        }

        genesis = config.getString("registrar.config");
        lib = config.getString("registrar.native");
        LibIndy.init(lib);
    }

    public Configuration getConfig() {
        return config;
    }

    public String getGenesis() {
        return genesis;
    }
    
    public void setProperty(String name, Object value){
        config.setProperty(name, value);
    }
   
    public void save() {
        try {
            builder.save();
        } catch (ConfigurationException e) {
            LOG.error(String.format("Configuration error %s", e.getMessage()));
        }
    }
    
    public String getVaue(String key){
        return config.getString(key);
    }
}
