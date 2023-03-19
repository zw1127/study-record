package cn.simulator.netconf.ops;

import static java.util.Objects.requireNonNull;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.Deque;
import org.opendaylight.netconf.api.ModifyAction;
import org.opendaylight.yangtools.yang.data.api.YangInstanceIdentifier;
import org.opendaylight.yangtools.yang.data.api.YangInstanceIdentifier.PathArgument;
import org.opendaylight.yangtools.yang.data.api.schema.NormalizedNode;

public final class DataTreeChange {
    private final NormalizedNode changeRoot;
    private final YangInstanceIdentifier path;
    private final ModifyAction action;

    DataTreeChange(final NormalizedNode changeRoot, final ModifyAction action, final Deque<PathArgument> path) {
        this.changeRoot = requireNonNull(changeRoot);
        this.action = requireNonNull(action);

        final Builder<PathArgument> builder = ImmutableList.builderWithExpectedSize(path.size());
        path.descendingIterator().forEachRemaining(builder::add);
        this.path = YangInstanceIdentifier.create(builder.build());
    }

    public NormalizedNode getChangeRoot() {
        return changeRoot;
    }

    public ModifyAction getAction() {
        return action;
    }

    public YangInstanceIdentifier getPath() {
        return path;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("action", action).add("path", path).add("root", changeRoot)
                .toString();
    }
}
