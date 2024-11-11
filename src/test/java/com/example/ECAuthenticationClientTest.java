package com.example;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.TreeMap;

public class ECAuthenticationClientTest {
    
    @Test
    public void testSignatureGeneration() throws Exception {
        // Get path to resources
        String privateKeyPath = getClass().getResource("/private_pkcs8.pem").getPath();
        String publicKeyPath = getClass().getResource("/public.pem").getPath();
        
        // Create client and verifier
        ECAuthenticationClient client = new ECAuthenticationClient(privateKeyPath);
        ECAuthenticationClient.VerificationClient verifier = 
            new ECAuthenticationClient.VerificationClient(publicKeyPath);
        
        // Create test request body
        TreeMap<String, Object> requestBody = new TreeMap<>();
        requestBody.put("userId", "test123");
        requestBody.put("action", "test");
        requestBody.put("amount", 100);
        
        // Generate signature
        String authHeader = client.generateSignature(requestBody);
        
        // Verify signature
        assertTrue("Signature should be valid", verifier.verifySignature(authHeader, requestBody));
    }
}
