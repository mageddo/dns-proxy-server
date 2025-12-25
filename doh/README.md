## Configure Doh on firefox

* https://gemini.google.com/app/28a3034438bb1b72
* about:networking#dns
* about:preferences#privacy -> dns over https -> Increased Protection = `https://localhost:8443/dns-query`

Restrictions

* Import ca.crt on firefox under `about:preferences#privacy` -> Certificates -> Authorities to make the DPS doh server
  be accepted.
* Activate `network.trr.allow-rfc1918` on `about:config` to make it work with private ip addresses;
* Some real domains like `.dev` won't work depending on the combination of private ip + default port (80, 443), so evict
  them, .com seems to work

## Generate Certificates for DOH Server

### 1 - Criar uma CA local (uma vez só)

```bash
openssl genrsa -out ca.key 4096
```

```bash
openssl req -x509 -new -nodes \
  -key ca.key \
  -sha256 \
  -days 36500 \
  -out ca.crt \
  -subj "/C=BR/ST=SP/L=SaoPaulo/O=Local Dev CA/CN=Local Dev Root CA"
```

✔ Esse **é o certificado que você importa no Firefox**
✔ Ele já vem com `CA:TRUE`

### 2 - Criar chave do servidor DoH

```bash
openssl genrsa -out doh.key 2048
```

### 3 - Criar CSR do servidor DoH

```bash
openssl req -new \
  -key doh.key \
  -out doh.csr \
  -subj "/C=BR/ST=SP/L=SaoPaulo/O=Local Dev/CN=localhost"
```

---

## 4 - Criar arquivo de extensões

Crie `doh.ext`:

```ini
authorityKeyIdentifier=keyid,issuer
basicConstraints=CA:FALSE
keyUsage = digitalSignature, keyEncipherment
extendedKeyUsage = serverAuth

subjectAltName = @alt_names

[alt_names]
DNS.1 = localhost
IP.1 = 127.0.0.1
```

## 5 - Assinar o certificado do servidor com a CA

```bash
openssl x509 -req \
  -in doh.csr \
  -CA ca.crt \
  -CAkey ca.key \
  -CAcreateserial \
  -out doh.crt \
  -days 36500 \
  -sha256 \
  -extfile doh.ext
```

Agora você tem:

* `ca.crt` → **Autoridade**
* `doh.crt` → **Servidor**
* `doh.key` → **Chave do servidor**

---

### 6 - Importar a CA no Firefox

1. `about:preferences#privacy`
2. **Certificados → Ver certificados**
3. Aba **Autoridades**
4. **Importar `ca.crt`**
5. Marcar:

* ✅ *Confiar nesta CA para identificar sites*

⚠️ **NÃO importe o `doh.crt` no Firefox**

### 7 - Gerar `server.p12` a partir de PEM (OpenSSL) para usar no DohServer no DPS

```bash
# Opcional: cria fullchain (server + CA) (bom pra browsers)
cat doh.crt ca.crt > fullchain.crt

# cria PKCS12 com private key + cert chain
openssl pkcs12 -export \
  -inkey doh.key \
  -in doh.crt \
  -certfile ca.crt \
  -name local \
  -out server.p12 \
  -passout pass:changeit
```

Isso cria um keystore `server.p12` contendo:

* **PrivateKey**: `doh.key`
* **Cert**: `doh.crt`
* **Chain**: `ca.crt`

Use o arquivo `server.p12`  no server no DPS.

[[ref][1]]

[1]: https://chatgpt.com/c/694d8e4e-13c8-8325-bef7-25fb4240b6c9
