package cn.javastudy.springboot.simulator.netconf.ops;

import org.opendaylight.yangtools.yang.data.api.schema.NormalizedNode;
import org.opendaylight.yangtools.yang.data.impl.schema.ImmutableMetadataNormalizedNodeStreamWriter;
import org.opendaylight.yangtools.yang.data.impl.schema.NormalizedNodeMetadataResult;

/**
 * This is a single component writer, which results in some amount.
 */
final class ComponentNormalizedNodeStreamWriter extends ImmutableMetadataNormalizedNodeStreamWriter {
    private ComponentNormalizedNodeStreamWriter(final State state) {
        super(state);
    }

    ComponentNormalizedNodeStreamWriter(final NormalizedNodeMetadataResult result) {
        super(result);
    }

    NormalizedNode build() {
        return popState().getDataBuilder().build();
    }
}
