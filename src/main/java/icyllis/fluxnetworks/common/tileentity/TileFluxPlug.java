package icyllis.fluxnetworks.common.tileentity;

import icyllis.fluxnetworks.api.tile.ConnectionType;
import icyllis.fluxnetworks.system.registry.RegistryTiles;
import net.minecraft.util.Direction;

public class TileFluxPlug extends TileFluxConnector {

    public TileFluxPlug() {
        super(RegistryTiles.FLUX_PLUG);
    }

    @Override
    public long addPhantomEnergyToNetwork(Direction side, long amount, boolean simulate) {
        return handler.addPhantomEnergyToNetwork(amount, side, simulate);
    }

    @Override
    public ConnectionType getConnectionType() {
        return ConnectionType.PLUG;
    }
}
