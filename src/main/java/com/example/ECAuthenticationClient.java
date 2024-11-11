package com.example;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.time.Instant;
import java.util.TreeMap;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ECAuthenticationClient {
    private final PrivateKey privateKey;
    private final ObjectMapper objectMapper;
    
    public ECAuthenticationClient(String privateKeyPath) throws Exception {
        // Initialize ObjectMapper for JSON handling
        this.objectMapper = new ObjectMapper();
        
        // Read private key file
        String privateKeyPEM = new String(Files.readAllBytes(Paths.get(privateKeyPath)))
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replaceAll("\\s", "");
        
        // Decode private key from Base64
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        this.privateKey = keyFactory.generatePrivate(privateKeySpec);
    }
    
    public String generateSignature(Object requestBody) throws Exception {
        // Convert request body to sorted JSON string
        String jsonBody = (requestBody instanceof String) 
            ? (String) requestBody 
            : objectMapper.writeValueAsString(requestBody);
        
        // Get current timestamp
        String timestamp = String.valueOf(Instant.now().getEpochSecond());
        
        // Create string to sign (timestamp + requestBody)
        String dataToSign = timestamp + jsonBody;
        
        // Create signature
        Signature ecdsaSign = Signature.getInstance("SHA256withECDSA");
        ecdsaSign.initSign(privateKey);
        ecdsaSign.update(dataToSign.getBytes(StandardCharsets.UTF_8));
        byte[] signature = ecdsaSign.sign();
        
        // Encode signature in Base64
        String encodedSignature = Base64.getEncoder().encodeToString(signature);
        
        // Return formatted authorization header
        return String.format("t=%s,v1=%s", timestamp, encodedSignature);
    }
    
    public static class VerificationClient {
        private final PublicKey publicKey;
        private final ObjectMapper objectMapper;
        
        public VerificationClient(String publicKeyPath) throws Exception {
            this.objectMapper = new ObjectMapper();
            
            // Read public key file
            String publicKeyPEM = new String(Files.readAllBytes(Paths.get(publicKeyPath)))
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
            
            // Decode public key from Base64
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyPEM);
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            this.publicKey = keyFactory.generatePublic(publicKeySpec);
        }
        
        public boolean verifySignature(String authHeader, Object requestBody) throws Exception {
            // Parse authorization header
            String[] parts = authHeader.split(",");
            String timestamp = parts[0].substring(2); // Remove "t="
            String signature = parts[1].substring(3); // Remove "v1="
            
            // Convert request body to sorted JSON string
            String jsonBody = (requestBody instanceof String)
                ? (String) requestBody
                : objectMapper.writeValueAsString(requestBody);
            
            // Recreate signed string
            String dataToVerify = timestamp + jsonBody;
            
            // Verify signature
            Signature ecdsaVerify = Signature.getInstance("SHA256withECDSA");
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(dataToVerify.getBytes(StandardCharsets.UTF_8));
            
            return ecdsaVerify.verify(Base64.getDecoder().decode(signature));
        }
    }
}
