/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jssi.registrar.service;

import jssi.registrar.service.WalletService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.hyperledger.indy.sdk.did.Did;
import org.hyperledger.indy.sdk.wallet.Wallet;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author UBICUA
 */
public class WalletServiceTest {
    
    private static final Logger LOG = LoggerFactory.getLogger(WalletServiceTest.class);
    private static final Gson gson = new Gson();
    
    public WalletServiceTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void readSubmitterWallet() throws Exception {
        
        String id = "wallet.resolver.id";
        String key = "wallet.resolver.key";
        WalletService instance = new WalletService();
        instance.openWallet(id, key);
        Wallet wallet = instance.getWallet();
       
        JsonArray dids = gson.fromJson(Did.getListMyDidsWithMeta(wallet).get(), JsonArray.class);
        for(JsonElement element : dids){
            String did = element.getAsJsonObject().getAsJsonPrimitive("did").getAsString();
            String verkey = element.getAsJsonObject().getAsJsonPrimitive("verkey").getAsString();
            LOG.debug(String.format("Submitter did: %s, verkey: %s", did, verkey));
        }
        
        wallet.close();
    }
    
    @Test
    public void readTargetWallet() throws Exception {
        String id = "wallet.ubicua.id";
        String key = "wallet.ubicua.key";
        WalletService instance = new WalletService();
        instance.openWallet(id, key);
        Wallet wallet = instance.getWallet();
       
        JsonArray dids = gson.fromJson(Did.getListMyDidsWithMeta(wallet).get(), JsonArray.class);
        for(JsonElement element : dids){
            String did = element.getAsJsonObject().getAsJsonPrimitive("did").getAsString();
            String verkey = element.getAsJsonObject().getAsJsonPrimitive("verkey").getAsString();
            LOG.debug(String.format("Target Did: %s, Verkey: %s", did, verkey));
        }
        
        wallet.close();
    }
        
}

