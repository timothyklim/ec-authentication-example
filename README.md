# Keys generation

```
# Generate private key using secp256r1 (P-256) curve
openssl ecparam -name prime256v1 -genkey -noout -out private.pem

# Export the private key to PKCS8 format
openssl pkcs8 -topk8 -nocrypt -in private.pem -out private_pkcs8.pem

# Generate public key from private key
openssl ec -in private.pem -pubout -out public.pem
```

# Test

```
nix-shell -p sbt --run 'sbt test'
```
