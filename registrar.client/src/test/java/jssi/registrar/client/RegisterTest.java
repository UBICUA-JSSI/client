/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jssi.registrar.client;

import jssi.registrar.client.Registrar;
import com.google.gson.Gson;
import java.util.List;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonObject;
import org.hyperledger.indy.sdk.wallet.Wallet;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import jssi.registrar.service.ConfigService;
import jssi.registrar.service.PoolService;
import jssi.registrar.service.WalletService;
import uniregistrar.request.RegisterRequest;
import uniregistrar.state.RegisterState;

/**
 *
 * @author UBICUA
 */
public class RegisterTest {
    
    public static final String[] DIDDOCUMENT_PUBLICKEY_TYPES = new String[]{"Ed25519VerificationKey2018"};
    
    private static final ConfigService configService = ConfigService.getInstance();
    private static final PoolService poolService = PoolService.getInstance();
    
    private static final Gson gson = new Gson();
    
    public RegisterTest() {
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
     * Test of register method, of class Registrar.
     */
    @Test
    public void testRegister() throws Exception {
        
        String id = "wallet.ubicua.id";
        String key = "wallet.ubicua.key";
        WalletService instance = new WalletService();
        instance.openWallet(id, key);
        Wallet wallet = instance.getWallet();
        
        
//        DidJSONParameters.CreateAndStoreMyDidJSONParameter targetDid
//                = new DidJSONParameters.CreateAndStoreMyDidJSONParameter(null, null, null, null);
//        DidResults.CreateAndStoreMyDidResult createResolverDidResult = Did.createAndStoreMyDid(wallet, targetDid.toJson()).get();
        String did = "R3yJEjRR7pavjN14x44qkD"; //createResolverDidResult.getDid();
        String verkey = "E7JmmyywVqvBgBUaST7icAZZjk4zyh8tyqHwXgLMuGyJ"; //createResolverDidResult.getVerkey();
        
        JsonObject request = Json.createObjectBuilder()
                .add("jobId", "123456")
                .add("options", Json.createObjectBuilder()
                        .add("network", "ubicua")
                        .add("keytype", "ed25519"))
                .add("secret", Json.createObjectBuilder()
                        .add("keys", Json.createArrayBuilder()
                                .add(Json.createObjectBuilder()
                                        .add("id", did)
                                        .add("verkey", verkey))))
                .build();
        
        RegisterRequest registerRequest = RegisterRequest.fromJson(request.toString());

        Registrar registrar = new Registrar();
        RegisterState state = registrar.register("sov", registerRequest);
    }
    
    @Test
    public void testRegisterRequest() throws Exception {
                                
        JsonObject request = Json.createObjectBuilder()
                .add("jobId", "123456")
                .add("options", Json.createObjectBuilder()
                        .add("network", "ubicua")
                        .add("keytype", "ed25519"))
                .add("secret", Json.createObjectBuilder()
                        .add("keys", Json.createArrayBuilder()
                                .add(Json.createObjectBuilder()
                                        .add("id", "CkUUjdPWfNo3UovcfNKD2v")
                                        .add("verkey", "7QRjHZaokZNPAH7t6nNsqDS7946Do68Ne2XUTXyzmS7c"))))
                .build();
        
        RegisterRequest registerRequest = RegisterRequest.fromJson(request.toString());

        List list = (List) registerRequest.getSecret().get("keys");
        Map map = (Map) list.get(0);
        String verkey = (String) map.get("verkey");
        assertEquals(verkey, "7QRjHZaokZNPAH7t6nNsqDS7946Do68Ne2XUTXyzmS7c");
        
        String network = (String) registerRequest.getOptions().get("network");
        assertEquals(network, "ubicua");
    }
}
