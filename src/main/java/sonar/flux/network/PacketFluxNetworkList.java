package sonar.flux.network;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import sonar.core.SonarCore;
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.flux.FluxNetworks;
import sonar.flux.api.network.IFluxCommon;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.connection.BasicFluxNetwork;

//TODO make a single packet version for updates
public class PacketFluxNetworkList implements IMessage {
	public ArrayList<? extends IFluxNetwork> networks;
    public boolean update;

	public PacketFluxNetworkList() {
	}

    public PacketFluxNetworkList(ArrayList<? extends IFluxNetwork> toSend, boolean update) {
		this.networks = toSend;
        this.update = update;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
        update = buf.readBoolean();

		NBTTagCompound compound = ByteBufUtils.readTag(buf);
		NBTTagList list = compound.getTagList("nets", 10);
        ArrayList<IFluxNetwork> networks = new ArrayList<>();
		for (int i = 0; i < list.tagCount(); i++) {
			BasicFluxNetwork net = NBTHelper.instanceNBTSyncable(BasicFluxNetwork.class, list.getCompoundTagAt(i));
			UUID name = net.getOwnerUUID();
			if (name != null) {
				networks.add(net);
			}
		}
		this.networks = networks;
	}

	@Override
	public void toBytes(ByteBuf buf) {
        buf.writeBoolean(update);
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagList list = new NBTTagList();
		for (IFluxCommon network : networks) {
			if (network != null && !network.isFakeNetwork() && network.getNetworkID() != -1) {
				list.appendTag(network.writeData(new NBTTagCompound(), SyncType.SAVE));
			}
		}
		tag.setTag("nets", list);
		ByteBufUtils.writeTag(buf, tag);
	}

	public static class Handler implements IMessageHandler<PacketFluxNetworkList, IMessage> {

		@Override
		public IMessage onMessage(PacketFluxNetworkList message, MessageContext ctx) {
			if (ctx.side == Side.CLIENT) {
                SonarCore.proxy.getThreadListener(ctx).addScheduledTask(() -> {
				ClientNetworkCache cache = FluxNetworks.getClientCache();
                    ConcurrentHashMap<UUID, ArrayList<IFluxNetwork>> newNetworks = new ConcurrentHashMap<>();
				message.networks.forEach(network -> {
					if (network.getOwnerUUID() != null) {
                            newNetworks.putIfAbsent(network.getOwnerUUID(), new ArrayList<>());
						IFluxNetwork target = cache.getNetwork(network.getNetworkID());
						if (target != null && target.getOwnerUUID() != null && target.getOwnerUUID().equals(network.getOwnerUUID())) {
							newNetworks.get(network.getOwnerUUID()).add(target.updateNetworkFrom(network));
						} else
							newNetworks.get(network.getOwnerUUID()).add(network);
					}
				});
                    if (!message.update)
				cache.networks = newNetworks;
                });
			}

			return null;
		}
	}
}
