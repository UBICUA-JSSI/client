package jssi.registrar.service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import org.hyperledger.indy.sdk.IndyException;
import org.hyperledger.indy.sdk.pool.Pool;
import org.hyperledger.indy.sdk.pool.PoolJSONParameters;
import org.hyperledger.indy.sdk.pool.PoolLedgerConfigExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ITON Solutions
 */
public class PoolService {
    
    private static final Logger LOG = LoggerFactory.getLogger(PoolService.class);
    
    public static final int PROTOCOL_VERSION = 2;
    
    private static PoolService INSTANCE = null;
    private static final ConfigService config = ConfigService.getInstance();
    
    private String name;
    private Pool pool;
   
    public static PoolService getInstance(){
        if(INSTANCE == null){
            INSTANCE = new PoolService();
        }
        return INSTANCE;
    }

    public PoolService(){
        name = config.getConfig().getString("pool.name");
        LOG.debug(String.format("Pool name: %s", name));
     
        File file = new File(Paths.get(config.getGenesis(), "ubicua.genesis").toString());
        
        StringBuilder builder = new StringBuilder();
        try {
            Stream<String> stream = Files.lines(Paths.get(file.getAbsolutePath()), StandardCharsets.UTF_8);
            stream.forEach(s -> builder.append(s).append("\n"));
            LOG.debug(String.format("Genesis pool definition:\n%s", builder.toString()));

            config(name, file);
            
            pool = Pool.openPoolLedger(name, null).get();
            LOG.debug(String.format("Open pool: %s", name));
        } catch (IOException | IndyException | InterruptedException | ExecutionException e) {
            LOG.debug("Open pool exception", e);
        }
    }
    
    private void config(String name, File genesis) {

        try {
            Pool.setProtocolVersion(PROTOCOL_VERSION).get();
            
            PoolJSONParameters.CreatePoolLedgerConfigJSONParameter params
                    = new PoolJSONParameters.CreatePoolLedgerConfigJSONParameter(genesis.getAbsolutePath());
    
            Pool.createPoolLedgerConfig(name, params.toJson()).get();
        } catch (ExecutionException e) {
            if (e.getCause() instanceof PoolLedgerConfigExistsException) {
                LOG.debug(String.format("Pool config already exist: %s", e.getCause().getMessage()));
            }
        } catch (IndyException | InterruptedException e) {
            LOG.debug("Pool config exception", e);
        }
    }
    
    public void close() {
        try {
            pool.close();
        } catch (ExecutionException | IndyException | InterruptedException e) {
            LOG.debug("Pool close exception", e);
        }
    }

    public String getName() {
        return name;
    }

    public Pool getPool() {
        return pool;
    }
}
