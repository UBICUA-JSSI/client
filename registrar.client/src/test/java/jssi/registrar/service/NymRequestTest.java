/*
 * The MIT License
 *
 * Copyright 2020 UBICUA.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package jssi.registrar.service;

import jssi.registrar.service.WalletService;
import jssi.registrar.service.ConfigService;
import jssi.registrar.service.PoolService;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.hyperledger.indy.sdk.IndyException;
import org.hyperledger.indy.sdk.did.Did;
import org.hyperledger.indy.sdk.did.DidJSONParameters;
import org.hyperledger.indy.sdk.did.DidResults;
import org.hyperledger.indy.sdk.ledger.Ledger;
import static org.hyperledger.indy.sdk.ledger.Ledger.buildNymRequest;
import static org.hyperledger.indy.sdk.ledger.Ledger.signAndSubmitRequest;
import org.hyperledger.indy.sdk.pool.Pool;
import org.hyperledger.indy.sdk.wallet.Wallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static jssi.registrar.service.PoolService.PROTOCOL_VERSION;
import uniregistrar.RegistrationException;

/**
 *
 * @author UBICUA
 */
public class NymRequestTest {
    
    private static final Logger LOG = LoggerFactory.getLogger(NymRequestTest.class);
    private static final ConfigService configService = ConfigService.getInstance();
    private static final PoolService poolService = PoolService.getInstance();
    
    private static final Gson gson = new Gson();
    
    public NymRequestTest() {
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

    /**
     * Test of demo method, of class Attributes.
     */
    @Test
    public void getNymRequest() throws Exception {
        LOG.debug("GET NYM Request test -> started");
        
        // Set protocol version 2 to work with Indy Node 1.4
        Pool.setProtocolVersion(PROTOCOL_VERSION).get();
        // 1. Create ledger config from genesis txn file
        Pool pool = poolService.getPool();

        // 2. Create and Open Ubicua Wallet
        WalletService instance = new WalletService();
        instance.openWallet("wallet.ubicua.id", "wallet.ubicua.key");
        Wallet ubicuaWallet = instance.getWallet();

        // 3. Create and Open Resolver Wallet
        instance = new WalletService();
        instance.openWallet("wallet.resolver.id", "wallet.resolver.key");
        Wallet endorserWallet = instance.getWallet();

        // 4. Create Ubicua Did
        String targetDid = "Rx8H1TmV3C9QaGqCfZEJax";
        String endorserDid = "V4SGRU86Z58d6TV7PBUe6f";
        
        String response;
        
        try {

            String targetNymRequest = Ledger.buildGetNymRequest(endorserDid, targetDid).get();
            response = Ledger.signAndSubmitRequest(pool, endorserWallet, endorserDid, targetNymRequest).get();

            JsonObject nymResponse = gson.fromJson(response, JsonObject.class);
            JsonObject nymResult = nymResponse == null ? null : nymResponse.getAsJsonObject("result");
            JsonElement nymData = nymResult == null ? null : nymResult.get("data");
            assertFalse(nymData instanceof JsonNull);

        } catch (IndyException | InterruptedException | ExecutionException ex) {
            throw new RegistrationException("Cannot send GET_NYM request: " + ex.getMessage(), ex);
        }
        long millis = TimeUnit.MILLISECONDS.convert(1608334989570680400L, TimeUnit.NANOSECONDS);
        Date date = new Date(millis);
        
        ubicuaWallet.close();
        endorserWallet.close();
        LOG.debug("GET NYM Request test -> ended");
    }
    
     /**
     * Test of demo method, of class Attributes.
     */
//    @Test
    public void nymRequest() throws Exception {
        LOG.debug("NYM Request test -> started");
        
        // Set protocol version 2 to work with Indy Node 1.4
        Pool.setProtocolVersion(PROTOCOL_VERSION).get();
        // 1. Create ledger config from genesis txn file
        Pool pool = poolService.getPool();

        // 2. Create and Open Target Wallet
        WalletService instance = new WalletService();
        instance.openWallet("wallet.ubicua.id", "wallet.ubicua.key");
        Wallet targetWallet = instance.getWallet();

        // 3. Create and Open Endorser Wallet
        instance = new WalletService();
        instance.openWallet("wallet.resolver.id", "wallet.resolver.key");
        Wallet endorserWallet = instance.getWallet();

        // 4. Create Target Did
        DidJSONParameters.CreateAndStoreMyDidJSONParameter targetDidJson
                = new DidJSONParameters.CreateAndStoreMyDidJSONParameter(null, null, null, null);
        DidResults.CreateAndStoreMyDidResult createResolverDidResult = Did.createAndStoreMyDid(targetWallet, targetDidJson.toJson()).get();
        String targetDid = createResolverDidResult.getDid();
        String targetVerkey = createResolverDidResult.getVerkey();
        
        String response;
        
        try {
                String resolverSeed = "000000000000000000000000Trustee1";
                DidJSONParameters.CreateAndStoreMyDidJSONParameter endorserDidJson
                        = new DidJSONParameters.CreateAndStoreMyDidJSONParameter(null, resolverSeed, null, null);
                DidResults.CreateAndStoreMyDidResult createEndorserDid = Did.createAndStoreMyDid(endorserWallet, endorserDidJson.toJson()).get();
                String submitterDid = createEndorserDid.getDid();

                // 6. Build Nym Request
                String nymRequest = buildNymRequest(submitterDid, targetDid, targetVerkey, null, null).get();
                // 7. Trustee Sign Nym Request
                response = signAndSubmitRequest(pool, endorserWallet, submitterDid, nymRequest).get();
                
                JsonObject nymResponse = gson.fromJson(response, JsonObject.class);
                JsonObject nymResult = nymResponse == null ? null : nymResponse.getAsJsonObject("result");
                JsonObject nymTxn = nymResult == null ? null : nymResult.getAsJsonObject("txn");
                JsonElement nymData = nymTxn == null ? null : nymTxn.get("data");
                assertFalse(nymData instanceof JsonNull);
            
        } catch (IndyException | InterruptedException | ExecutionException ex) {
            throw new RegistrationException("Cannot send GET_NYM request: " + ex.getMessage(), ex);
        }
        long millis = TimeUnit.MILLISECONDS.convert(1608334989570680400L, TimeUnit.NANOSECONDS);
        Date date = new Date(millis);
        
        targetWallet.close();
        endorserWallet.close();
        LOG.debug("NYM Request test -> ended");
    }
}
