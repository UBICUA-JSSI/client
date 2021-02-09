/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jssi.registrar.client;

import jssi.registrar.client.Registrar;
import com.google.gson.Gson;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import static org.hyperledger.indy.sdk.anoncreds.Anoncreds.issuerCreateSchema;
import org.hyperledger.indy.sdk.anoncreds.AnoncredsResults;
import org.hyperledger.indy.sdk.ledger.Ledger;
import static org.hyperledger.indy.sdk.ledger.Ledger.appendRequestEndorser;
import static org.hyperledger.indy.sdk.ledger.Ledger.buildSchemaRequest;
import static org.hyperledger.indy.sdk.ledger.Ledger.multiSignRequest;
import org.hyperledger.indy.sdk.wallet.Wallet;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import jssi.registrar.service.ConfigService;
import jssi.registrar.service.WalletService;
import uniregistrar.request.UpdateRequest;
import uniregistrar.state.UpdateState;

/**
 *
 * @author UBICUA
 */
public class UpdateTest {
    
    public static final String[] DIDDOCUMENT_PUBLICKEY_TYPES = new String[]{"Ed25519VerificationKey2018"};
    
    private static final ConfigService config = ConfigService.getInstance();
    
    private static final Gson gson = new Gson();
    
    public UpdateTest() {
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
    public void testUpdateAttribute() throws Exception {
        
        String id = "wallet.ubicua.id";
        String key = "wallet.ubicua.key";
        WalletService instance = new WalletService();
        instance.openWallet(id, key);
        Wallet wallet = instance.getWallet();
        
        String did = "CkUUjdPWfNo3UovcfNKD2v";
        String verkey = "7QRjHZaokZNPAH7t6nNsqDS7946Do68Ne2XUTXyzmS7c";
        
//        String did = "R3yJEjRR7pavjN14x44qkD";
//        String verkey = "E7JmmyywVqvBgBUaST7icAZZjk4zyh8tyqHwXgLMuGyJ";
        
        JsonObject attribute = Json.createObjectBuilder()
                .add("endpoint", Json.createObjectBuilder()
                        .add("authentication", "http://ubicua.org:8080/authentication")
                        .add("issued", config.getConfig().getString("endorser.did")))
                .build();
        
        String attributeRequest = Ledger.buildAttribRequest(did, did, null, attribute.toString(), null).get();
        String attributeEndorser = appendRequestEndorser(attributeRequest, config.getConfig().getString("endorser.did")).get();
        
        String action = multiSignRequest(wallet, did, attributeEndorser).get();
        
        JsonObject request = Json.createObjectBuilder()
                .add("jobId", "123456")
                .add("identifier", did)
                .add("options", Json.createObjectBuilder()
                        .add("network", "ubicua")
                        .add("keytype", "ed25519"))
                .add("secret", Json.createObjectBuilder()
                        .add("keys", Json.createArrayBuilder()
                                .add(Json.createObjectBuilder()
                                        .add("id", did)
                                        .add("verkey", verkey))))
                .add("action", action)
                .build();
        
        UpdateRequest updateRequest = UpdateRequest.fromJson(request.toString());
        Registrar registrar = new Registrar();
        UpdateState status = registrar.update("sov", updateRequest);
    }
    
//    @Test
    public void testUpdateCredential() throws Exception {
        
        String id = "wallet.ubicua.id";
        String key = "wallet.ubicua.key";
        WalletService instance = new WalletService();
        instance.openWallet(id, key);
        Wallet wallet = instance.getWallet();
        
        String did = "R3yJEjRR7pavjN14x44qkD";
        String verkey = "E7JmmyywVqvBgBUaST7icAZZjk4zyh8tyqHwXgLMuGyJ";
        
        String schemaName = "goverment";
        String schemaVersion = "1.0";
        
        JsonArray schemaAttributes = Json.createArrayBuilder()
                .add("name")
                .add("height")
                .add("age")
                .add("sex")
                .build();
        
        AnoncredsResults.IssuerCreateSchemaResult schemaResult = issuerCreateSchema(did, schemaName, schemaVersion, schemaAttributes.toString()).get();
        
        String schemaId = schemaResult.getSchemaId();
        String schemaJson = schemaResult.getSchemaJson();
        //  Transaction Author builds Schema Request
        String schemaRequest = buildSchemaRequest(did, schemaJson).get();
        //  Transaction Author appends Endorser's DID into the request
        String schemaEndorser = appendRequestEndorser(schemaRequest, config.getConfig().getString("endorser.did")).get();
        //  Transaction Author signs the request with the added endorser field
        String action = multiSignRequest(wallet, did, schemaEndorser).get();
        
        JsonObject request = Json.createObjectBuilder()
                .add("jobId", "123456")
                .add("identifier", did)
                .add("options", Json.createObjectBuilder()
                        .add("network", "ubicua")
                        .add("keytype", "ed25519"))
                .add("secret", Json.createObjectBuilder()
                        .add("keys", Json.createArrayBuilder()
                                .add(Json.createObjectBuilder()
                                        .add("id", did)
                                        .add("verkey", verkey))))
                .add("action", action)
                .build();
        
        UpdateRequest updateRequest = UpdateRequest.fromJson(request.toString());
        Registrar registrar = new Registrar();
        UpdateState status = registrar.update("sov", updateRequest);
    }
}
