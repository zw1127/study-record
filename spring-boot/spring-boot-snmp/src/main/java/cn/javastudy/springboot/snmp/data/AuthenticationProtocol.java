package cn.javastudy.springboot.snmp.data;

import java.util.Arrays;
import java.util.Optional;
import org.snmp4j.security.AuthHMAC128SHA224;
import org.snmp4j.security.AuthHMAC192SHA256;
import org.snmp4j.security.AuthHMAC256SHA384;
import org.snmp4j.security.AuthHMAC384SHA512;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.AuthSHA;
import org.snmp4j.smi.OID;

public enum AuthenticationProtocol {
    NONE(null),
    MD5(AuthMD5.ID),
    SHA_1(AuthSHA.ID),
    SHA_224(AuthHMAC128SHA224.ID),
    SHA_256(AuthHMAC192SHA256.ID),
    SHA_384(AuthHMAC256SHA384.ID),
    SHA_512(AuthHMAC384SHA512.ID);

    private final OID oid;

    AuthenticationProtocol(OID oid) {
        this.oid = oid;
    }

    public OID getOid() {
        return oid;
    }

    public static Optional<AuthenticationProtocol> forName(String name) {
        return Arrays.stream(values())
                .filter(protocol -> protocol.name().equalsIgnoreCase(name))
                .findFirst();
    }
}
