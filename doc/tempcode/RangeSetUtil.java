import com.google.common.collect.BoundType;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RangeSetUtil {

    private static final Logger LOG = LoggerFactory.getLogger(RangeSetUtil.class);

    private static final String SEPRATE = ",";
    private static final String VLAN_RANGE_SEPRATE = "-";

    private RangeSetUtil() {
    }

    // text e.g.  1,3,5-7,15-17
    public static RangeSet<Integer> convertToRangeSet(String vlanStr) {
        if (StringUtils.isEmpty(vlanStr)) {
            return null;
        }
        RangeSet<Integer> rangeSet = TreeRangeSet.create();

        String[] vlans = StringUtils.split(vlanStr, SEPRATE);
        if (ArrayUtils.isEmpty(vlans)) {
            return null;
        }

        Arrays.stream(vlans)
            .map(StringUtils::trim)
            .filter(StringUtils::isNotEmpty)
            .collect(Collectors.toList())
            .forEach(index -> {
                String[] split = StringUtils.split(index, VLAN_RANGE_SEPRATE);
                if (split.length > 1) {
                    int lower = Integer.parseInt(split[0]);
                    int upper = Integer.parseInt(split[1]);
                    rangeSet.add(Range.closed(lower, upper).canonical(DiscreteDomain.integers()));
                } else {
                    rangeSet.add(Range.singleton(Integer.parseInt(index)).canonical(DiscreteDomain.integers()));
                }
            });
        return rangeSet;
    }

    // vlan字符串格式化
    // 默认toString()格式 [[1..1], [3..3], [5..7], [15..19]]
    // 目标格式 1,3,5-7,15-17
    public static String vlanFormat(RangeSet<Integer> value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        return value.asRanges().stream()
            .map(integerRange -> {
                Pair<Integer, Integer> pair = resovleRange(integerRange);

                if (pair.getLeft() > pair.getRight()) {
                    return "";
                }

                if (pair.getLeft().equals(pair.getRight())) {
                    return Integer.toString(pair.getLeft());
                }

                return pair.getLeft() + VLAN_RANGE_SEPRATE + pair.getRight();


            }).filter(StringUtils::isNotEmpty).collect(Collectors.joining(SEPRATE));
    }

    public static Set<Integer> convertToList(RangeSet<Integer> value) {
        Set<Integer> result = new HashSet<>();

        if (value == null || value.isEmpty()) {
            return result;
        }

        value.asRanges().forEach(integerRange -> {
            Pair<Integer, Integer> pair = resovleRange(integerRange);

            if (pair.getLeft().equals(pair.getRight())) {
                result.add(pair.getLeft());
                return;
            }

            for (int i = pair.getLeft(); i <= pair.getRight(); i++) {
                result.add(i);
            }
        });

        return result;
    }

    private static Pair<Integer, Integer> resovleRange(Range<Integer> integerRange) {
        int lower = integerRange.lowerEndpoint();
        BoundType lowerBoundType = integerRange.lowerBoundType();

        int upper = integerRange.upperEndpoint();
        BoundType upperBoundType = integerRange.upperBoundType();

        if (BoundType.OPEN.equals(lowerBoundType)) {
            lower = lower + 1;
        }

        if (BoundType.OPEN.equals(upperBoundType)) {
            upper = upper - 1;
        }

        return Pair.of(lower, upper);
    }

    public static String excludeVlan(String vlanRange, Integer vlanId) {
        RangeSet<Integer> rangeSet = RangeSetUtil.convertToRangeSet(vlanRange);
        if (rangeSet == null) {
            LOG.debug("vlanStr: {} is illeagal", vlanRange);
            return vlanRange;
        }

        rangeSet.remove(Range.singleton(vlanId));
        return RangeSetUtil.vlanFormat(rangeSet);
    }
}
