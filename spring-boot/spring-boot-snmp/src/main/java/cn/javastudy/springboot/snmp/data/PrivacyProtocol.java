package cn.javastudy.springboot.snmp.data;

import java.util.Arrays;
import java.util.Optional;
import org.snmp4j.security.Priv3DES;
import org.snmp4j.security.PrivAES128;
import org.snmp4j.security.PrivAES192;
import org.snmp4j.security.PrivAES256;
import org.snmp4j.security.PrivDES;
import org.snmp4j.smi.OID;

public enum PrivacyProtocol {
    NONE(null),
    DES(PrivDES.ID),
    TRIPLE_DES(Priv3DES.ID),
    AES_128(PrivAES128.ID),
    AES_192(PrivAES192.ID),
    AES_256(PrivAES256.ID);

    // oids taken from org.snmp4j.security.SecurityProtocol implementations
    private final OID oid;

    PrivacyProtocol(OID oid) {
        this.oid = oid;
    }

    public OID getOid() {
        return oid;
    }

    public static Optional<PrivacyProtocol> forName(String name) {
        return Arrays.stream(values())
            .filter(protocol -> protocol.name().equalsIgnoreCase(name))
            .findFirst();
    }
}
