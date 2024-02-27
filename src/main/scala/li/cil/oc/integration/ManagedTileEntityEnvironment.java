package li.cil.oc.integration;

import li.cil.oc.api.Network;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.AbstractManagedEnvironment;

public class ManagedTileEntityEnvironment<T> extends AbstractManagedEnvironment {
    protected final T blockEntity;

    public ManagedTileEntityEnvironment(final T blockEntity, final String name) {
        this.blockEntity = blockEntity;
        setNode(Network.newNode(this, Visibility.Network).
                withComponent(name).
                create());
    }
}
