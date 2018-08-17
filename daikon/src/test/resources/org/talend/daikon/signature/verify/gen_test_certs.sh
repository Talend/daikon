#!/bin/sh
# This script is used to generate certificates required for unit-tests
# of the code signing feature of Talend's products

# The generated CA cert is valid for two weeks;
# The code signing cert(s) are valid for one week.
# 
# Although the validity period is arbitrary, shorter periods allow for
# easier short-term expiration and make time-related bugs easier to spot.
# Be sure to know what you are doing before modifying them, and keep them
# in sync with the tests (because they also check expiration times).

# Safety net. Really bail out in case of error.
set -eu

_cleanup() {
	if [ -d "$TMPDIR" ]; then
		rm -rf "$TMPDIR"
	fi
	exit 1
}
trap _cleanup EXIT INT TERM
TMPDIR=$(mktemp -d)

_setup() {
	# Setup some default sections of the CA + code-sign configurations
	export CACONF="$TMPDIR/ca.openssl.cnf"
	cat > "$CACONF" << EOF
[ req ]
distinguished_name      = ca_dn
x509_extensions		= ca_exts
prompt			= no

[ ca_dn ]
C=NO
ST=NO
L=NoLocality
O=Talend
OU=R&D
CN=Code-signing test CA

[ ca_exts ]
subjectKeyIdentifier    = hash
authorityKeyIdentifier  = keyid:always
keyUsage                = critical, keyCertSign, cRLSign
basicConstraints        = critical, CA:true, pathlen:0
EOF

	export CA2CONF="$TMPDIR/ca2.openssl.cnf"
	cat > "$CA2CONF" << EOF
[ req ]
distinguished_name      = ca_dn
x509_extensions		= ca_exts
prompt			= no

[ ca_dn ]
C=NO
ST=NO
L=NoLocality
O=Talend
OU=R&D
CN=Code-signing test CA #2

[ ca_exts ]
subjectKeyIdentifier    = hash
authorityKeyIdentifier  = keyid:always
keyUsage                = critical, keyCertSign, cRLSign
basicConstraints        = critical, CA:true, pathlen:0
EOF

	export CSCONF="$TMPDIR/cs.openssl.cnf"
	cat > "$CSCONF" << EOF
[ req ]
distinguished_name      = cs_dn
x509_extensions		= cs_exts
prompt			= no

[ cs_dn ]
C=NO
ST=NO
L=NoLocality
O=Talend
OU=R&D
CN=Code-signing test certificate

[ cs_exts ]
subjectKeyIdentifier    = hash
authorityKeyIdentifier  = keyid:always
keyUsage                = digitalSignature
extendedKeyUsage        = codeSigning
EOF

# A typical web-server certificate. Improper for code-signing.
	export WEBCONF="$TMPDIR/invalid.cs.openssl.cnf"
	cat > "$WEBCONF" << EOF
[ req ]
distinguished_name      = web_dn
x509_extensions		= web_exts
prompt			= no

[ web_dn ]
C=NO
ST=NO
L=NoLocality
O=Talend
OU=R&D
CN=Web-server test certificate

[ web_exts ]
subjectKeyIdentifier    = hash
authorityKeyIdentifier  = keyid:always
keyUsage                = digitalSignature, nonRepudiation, keyAgreement
extendedKeyUsage        = serverAuth
EOF


	export PASS="$(openssl rand -hex 14)"

	export CAPRIVKEY="$TMPDIR/ca.privkey.pem"
	export CACERTFILE="$TMPDIR/ca.cert.pem"
	export CA2PRIVKEY="$TMPDIR/ca2.privkey.pem"
	export CA2CERTFILE="$TMPDIR/ca2.cert.pem"

	export SERIAL="0x$(openssl rand -hex 8)"
	export ALIAS="talend-code-signing"
	export CSPRIVKEY="$TMPDIR/code-signing.privkey.pem"
	export CSRFILE="$TMPDIR/code-signing.csr"
	export CSCERTFILE="$TMPDIR/code-signing.cert.pem"

	export WEB_PRIVKEY="$TMPDIR/web.privkey.pem"
	export WEB_CSRFILE="$TMPDIR/web.cert.csr"
	export WEB_CERTFILE="$TMPDIR/web.cert.pem"
	export SELF_PRIVKEY="$TMPDIR/self.privkey.pem"
	export SELF_CERTFILE="$TMPDIR/self.cert.pem"

	# Final targets
	export TRUSTSTORE="truststore.jks"
	export TRUSTSTORE2="truststore2.jks"
	export CS_KEYSTORE="code-signing.jks"
	export SELF_KEYSTORE="self-signed.jks"
	export WEB_KEYSTORE="web.jks"
	# Default parameters for keystores
	export KS_DFLT="-passin env:PASS -passout env:PASS"
	export KS_DFLT="$KS_DFLT -certpbe PBE-SHA1-3DES -keypbe PBE-SHA1-3DES"
}

# This function creates RSA based certificates.
_gen_rsa_certs() {
	# Generate the real CA keypair - it is self-signed
	openssl req -x509 -days 360 -newkey rsa:2048 -out "$CACERTFILE" \
	    -passout env:PASS -keyout "$CAPRIVKEY"             	       \
	    -config "$CACONF"
	# Generate a fake CA keypair
	openssl req -x509 -days 360 -newkey rsa:2048 -out "$CA2CERTFILE" \
	    -passout env:PASS -keyout "$CA2PRIVKEY"               	\
	    -config "$CA2CONF"
	# Generate the code-signing private key
	openssl genrsa -passout env:PASS -out "$CSPRIVKEY" 2048
	# Make a CSR out of it
	openssl req -new -passin env:PASS -key "$CSPRIVKEY" \
	    -config "$CSCONF" -out "$CSRFILE"
	# Sign it with our previously generated CA
	openssl x509 -req -sha256 -days 180 -set_serial "$SERIAL" \
	    -CA "$CACERTFILE" -CAkey "$CAPRIVKEY"		\
	    -extfile "$CSCONF" -extensions "cs_exts"		\
	    -passin env:PASS -in "$CSRFILE" -out "$CSCERTFILE"

	# Generate the webserver private key
	openssl genrsa -passout env:PASS -out "$WEB_PRIVKEY" 2048
	# Make a CSR out of it
	openssl req -new -passin env:PASS -key "$WEB_PRIVKEY" \
	    -config "$WEBCONF" -out "$WEB_CSRFILE"
	# Sign it with our previously generated CA
	openssl x509 -req -sha256 -days 180 -set_serial "$SERIAL" \
	    -CA "$CACERTFILE" -CAkey "$CAPRIVKEY"		\
	    -extfile "$WEBCONF" -extensions "web_exts"		\
	    -passin env:PASS -in "$WEB_CSRFILE" -out "$WEB_CERTFILE"

	# Generate a self-signed code-signing cert. This one should not
	# be allowed for verification.
	openssl req -x509 -days 7 -newkey rsa:2048 		\
	    -config "$CSCONF" -passout env:PASS		\
	    -keyout "$SELF_PRIVKEY" -out "$SELF_CERTFILE"
}

_gen_keystores() {
	# Generate the truststore. Contains only the CA cert that allows
	# validating leaf code-signing certificates.
	# Note: Java expects the PKCS12 entry to be marked with a specific
	# entry type: "TrustedCertEntry". Only keytool supports this with PKCS12
	keytool -J-Duser.language=en -import -noprompt -file "$CACERTFILE" \
	    -storetype PKCS12 -storepass "$PASS"			   \
	    -alias "$ALIAS"-ca -keystore "$TRUSTSTORE"
	# Generate a second truststore, but this CA cannot validate any of the
	# currently issued certificates.
	keytool -J-Duser.language=en -import -noprompt -file "$CA2CERTFILE" \
	    -storetype PKCS12 -storepass "$PASS"			    \
	    -alias "$ALIAS"-ca2 -keystore "$TRUSTSTORE2"
	# Generate the code-signing keystore. Contains the code-signing cert
	# with its private key.
	# Note: the strongest suite for PKCS12 private keys is PBE-SHA1-3DES
	openssl pkcs12 -export $KS_DFLT			\
	    -inkey "$CSPRIVKEY" -in "$CSCERTFILE"	\
	    -name "$ALIAS" -out "$CS_KEYSTORE"		\
	    -chain -CAfile "$CACERTFILE"
	# Generate the bogus keystore. Contains a code-signing cert not
	# approved by any CA (as it is self-signed).
	openssl pkcs12 -export $KS_DFLT				\
            -inkey "$SELF_PRIVKEY" -in "$SELF_CERTFILE"		\
	    -name "$ALIAS" -out "$SELF_KEYSTORE"
	# Generate the web keystore. Contains a web server cert unfit
	# for code-signing
	openssl pkcs12 -export $KS_DFLT				\
            -inkey "$WEB_PRIVKEY" -in "$WEB_CERTFILE"		\
	    -name "$ALIAS" -out "$WEB_KEYSTORE"
}

_print_summary() {
	echo "Alias for signing: $ALIAS"
	echo "Password for all keystores: $PASS"
}

# Generate the ZIPs needed for the tests.
_gen_zip() {
	export RANDOMFILE="$TMPDIR/randomized"
	dd if=/dev/urandom of="$RANDOMFILE" bs=512 count=64 2>/dev/null
	cp "$RANDOMFILE" "not-a-zip.zip"
	zip -q "unsigned.zip" "$RANDOMFILE"
}

_checks() {
	# Alright, from now on, we let error go through.
	set +e

	printf "# TEST1: Sign with a valid code-signing cert: "
	cp "unsigned.zip" "signed-valid.zip"
	jarsigner -strict -storepass "$PASS" "signed-valid.zip" "$ALIAS" \
	    -keystore "$CS_KEYSTORE" >/dev/null
	_R=$?
	if [ "$_R" -ne 4 ]; then
		# Exit code must be 4 (ChainNotValidated), because the keyStore
		# does not contain the CA.
		echo "Signing returned unexpected error: $_R (expected 4)." >&2
		exit 2
	fi
	echo "OK"

	printf "# TEST2: Verify previous signature: "
	jarsigner -storepass "$PASS" "signed-valid.zip" \
	    -strict -verify -keystore "$TRUSTSTORE" >/dev/null
	_R=$?
	if [ "$_R" -ne 0 ]; then
		# Exit code must be 0, the verification must succeed without err.
		echo "Verify returned unexpected error: $_R (expected 0)." >&2
		exit 3
	fi
	echo "OK"

	printf "# TEST3: Sign with a webserver certificate: "
	cp "unsigned.zip" "signed-by-zip.zip"
	jarsigner -strict -storepass "$PASS" "signed-by-zip.zip" "$ALIAS" \
	    -keystore "$WEB_KEYSTORE" >/dev/null
	_R=$?
	if [ "$_R" -ne 12 ]; then
		# Exit code must be 12: "ChainNotValidated" | "badKeyUsage"
		echo "Signing returned unexpected error: $_R (expected 12)." >&2
		exit 4
	fi
	echo "OK"

	printf "# TEST4: Verify signature of webserver certificate: "
	# We drop -strict to allow the signing operation.
	jarsigner -storepass "$PASS" "signed-by-web.zip" "$ALIAS" \
	    -keystore "$WEB_KEYSTORE" >/dev/null
	jarsigner -strict -storepass "$PASS" "signed-by-web.zip" \
	    -verify -keystore "$TRUSTSTORE" >/dev/null
	_R=$?
	if [ "$_R" -ne 1 ]; then
		# Exit code must be 1: Verification failed (because of KeyUsage).
		echo "Verify returned unexpected error: $_R (expected 1)." >&2
		exit 5
	fi
	echo "OK"

	printf "# TEST5: Test entry modification to previously signed file: "
	# Take the valid signed ZIP, and overwrite the signed entry
	# Keep the same length so that its size does not change.
	cp "signed-valid.zip" "modified-signed-valid.zip"
	dd if=/dev/zero of="$RANDOMFILE" bs=512 count=64 2>/dev/null
	zip -q "modified-signed-valid.zip" "$RANDOMFILE" >/dev/null
	jarsigner -storepass "$PASS" "modified-signed-valid.zip" \
	    -strict -verify -keystore "$TRUSTSTORE" >/dev/null
	_R=$?
	if [ "$_R" -ne 1 ]; then
		# Exit code must be 1: Verification failed (because an entry was modified)
		echo "Signing returned unexpected error: $_R (expected 1)." >&2
		exit 6
	fi
	echo "OK"

	printf "# TEST6: Test entry deletion to previously signed file: "
	cp "signed-valid.zip" "deleted-signed-valid.zip"
	zip -qd "deleted-signed-valid.zip" "$RANDOMFILE" >/dev/null
	jarsigner -storepass "$PASS" "deleted-signed-valid.zip" \
	    -strict -verify -keystore "$TRUSTSTORE" >/dev/null
	_R=$?
	if [ "$_R" -ne 0 ]; then
		# Exit code must be 0: Missing file is not an error for jarsigner
		echo "Signing returned unexpected error: $_R (expected 0)." >&2
		exit 7
	fi
	echo "OK"

	# Generate a ZIP with an additional unsigned file. For that we re-import
	# the ZIP in itself.
	printf "# TEST7: Test arbitrary file addition to signed file: "
	cp "signed-valid.zip" "added-unsigned-file.zip"
	zip -q "added-unsigned-file.zip" "signed-valid.zip" 2>/dev/null
	jarsigner -storepass "$PASS" "deleted-signed-valid.zip" \
	    -strict -verify -keystore "$TRUSTSTORE" >/dev/null
	_R=$?
	if [ "$_R" -ne 0 ]; then
		# Exit code must be 0: Missing entry in Manifest is not an error for jarsigner
		echo "Signing returned unexpected error: $_R (expected 0)." >&2
		exit 8
	fi
	echo "OK"
}

_fini() {
	_gen_zip
	_checks
	_print_summary
}

_setup
_gen_rsa_certs
_gen_keystores
_fini
