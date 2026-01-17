import com.google.common.collect.RangeSet;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.snmp4j.smi.OctetString;
import org.springframework.util.CollectionUtils;

public final class SnmpVlanUtils {

    private SnmpVlanUtils() {
    }

    public static OctetString vlanToOctect(Set<Integer> vlanList) {
        if (CollectionUtils.isEmpty(vlanList)) {
            return new OctetString();
        }

        byte[] temp = new byte[512];
        for (Integer vlan : vlanList) {
            BitsUtils.setBits(temp, vlan - 1);
        }
        return new OctetString(temp);
    }

    /**
     * 功能：将按位的OctetString值转换为1,5-10的vlan值.
     *
     * @param octetString octetString
     * @return 字符串已经将连续的数字用"-"分开了，类似1-10,25格式
     */
    public static String vlanListToVlanStr2(OctetString octetString) {
        if (octetString == null || octetString.length() == 0) {
            return StringUtils.EMPTY;
        }
        byte[] bytes = octetString.getValue();

        StringBuilder vlanStr = new StringBuilder();
        int start = -1;
        int end = -1;
        int vlan;
        for (int i = 0; i < bytes.length << 3; i++) {
            if (bitListTst2(bytes, i) > 0) {
                vlan = i + 1;
                if (start < 0) {
                    start = vlan;
                    end = vlan;
                    vlanStr.append(start);
                }
                if (vlan - end > 1) {
                    if (end > start) {
                        vlanStr.append("-");
                        vlanStr.append(end);
                    }
                    start = vlan;
                    end = vlan;
                    vlanStr.append(",");
                    vlanStr.append(vlan);
                } else {
                    end = vlan;
                }
            }
        }

        if (end > start) {
            vlanStr.append("-");
            vlanStr.append(end);
        }

        return vlanStr.toString();
    }

    private static int bitListTst2(byte[] vlanList, int bit) {
        return ((vlanList[bit / 8] << bit % 8) & 0x80) == 0 ? 0 : 1;
    }

    public static RangeSet<Integer> octetToVlanRange(OctetString octetString) {
        String vlanStr = vlanListToVlanStr2(octetString);
        return RangeSetUtil.convertToRangeSet(vlanStr);
    }

    public static Set<Integer> octetToVlanList(OctetString octetString) {
        RangeSet<Integer> rangeSet = octetToVlanRange(octetString);

        return RangeSetUtil.convertToList(rangeSet);
    }
}
