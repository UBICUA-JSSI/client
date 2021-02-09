/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jssi.registrar.service;

import com.google.gson.JsonObject;
import java.util.concurrent.ExecutionException;
import org.hyperledger.indy.sdk.IndyException;
import org.hyperledger.indy.sdk.wallet.Wallet;
import org.hyperledger.indy.sdk.wallet.WalletExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author UBUCUA
 */
public class WalletService {
    
    private static final Logger LOG = LoggerFactory.getLogger(WalletService.class);
    private static final ConfigService configService = ConfigService.getInstance();
    
    private final JsonObject walletConfig;
    private final JsonObject walletCredentials;
    private Wallet wallet;
    
    
    
    public WalletService(){
        walletConfig = new JsonObject();
        walletCredentials = new JsonObject();
    }
    
    public void openWallet(String id, String key) throws Exception{
        // create wallet
        try {
            walletConfig.addProperty("id", configService.getVaue(id));
            walletCredentials.addProperty("key", configService.getVaue(key));

            Wallet.createWallet(walletConfig.toString(), walletCredentials.toString()).get();
            if (LOG.isInfoEnabled()) {
                LOG.info(String.format("Wallet '%s' successfully created.", configService.getVaue(id)));
            }
        } catch (IndyException | InterruptedException | ExecutionException ex) {

            IndyException iex = null;
            if (ex instanceof IndyException) {
                iex = (IndyException) ex;
            }
            if (ex instanceof ExecutionException && ex.getCause() instanceof IndyException) {
                iex = (IndyException) ex.getCause();
            }
            if (iex instanceof WalletExistsException) {
                if (LOG.isInfoEnabled()){
                    LOG.info(String.format("Wallet '%s' has already been create.", configService.getVaue(id)));
                }
            } else {
                throw new Exception(String.format("Cannot create wallet '%s:' %s", configService.getVaue(id),  ex.getMessage()), ex);
            }
        }

        // open wallet
        try {
            wallet = Wallet.openWallet(walletConfig.toString(), walletCredentials.toString()).get();
            LOG.info(String.format("Wallet '%s' has been open.", configService.getVaue(id)));
        } catch (IndyException | InterruptedException | ExecutionException ex) {
            throw new Exception(String.format("Cannot open wallet '%s:' %s", configService.getVaue(id),  ex.getMessage(), ex));
        }
    }

    public Wallet getWallet() {
        return wallet;
    }
            
            
}
