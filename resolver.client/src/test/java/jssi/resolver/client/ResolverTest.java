/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jssi.resolver.client;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import uniresolver.result.ResolveResult;

/**
 *
 * @author UBICUA
 */
public class ResolverTest {
    
    public ResolverTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test of resolve method, of class Resolver.
     */
    @Test
    public void resolveIndy() throws Exception {
        String identifier = "did:sov:ubicua:CkUUjdPWfNo3UovcfNKD2v";
        Resolver instance = new Resolver();
        ResolveResult result = instance.resolve(identifier);
        assertNotNull(result);
    }
    
//    @Test
    public void resolveBTCR() throws Exception {
        String identifier = "did:btcr:x705-jznz-q3nl-srs";
        Resolver instance = new Resolver();
        ResolveResult result = instance.resolve(identifier);
        assertNotNull(result);
    }
    
    @Test
    public void resolveCCP() throws Exception {
        String identifier = "did:ccp:ceNobbK6Me9F5zwyE3MKY88QZLw";
        Resolver instance = new Resolver();
        ResolveResult result = instance.resolve(identifier);
        assertNotNull(result);
    }
}
