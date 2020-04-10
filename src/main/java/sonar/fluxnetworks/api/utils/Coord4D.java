package sonar.fluxnetworks.api.utils;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class Coord4D {

    private int x, y, z, dimension; //TODO should we change to DimensionType?

    public Coord4D(CompoundNBT tag) {
        read(tag);
    }

    public Coord4D(TileEntity tile) {
        x = tile.getPos().getX();
        y = tile.getPos().getY();
        z = tile.getPos().getZ();
        dimension = tile.getWorld().getDimension().getType().getId();
    }

    public Coord4D(ByteBuf buf) {
        read(buf);
    }

    public CompoundNBT write(CompoundNBT tag) {
        tag.putInt("x", x);
        tag.putInt("y", y);
        tag.putInt("z", z);
        tag.putInt("dimension", dimension);
        return tag;
    }

    public void read(CompoundNBT tag) {
        x = tag.getInt("x");
        y = tag.getInt("y");
        z = tag.getInt("z");
        dimension = tag.getInt("dimension");
    }

    public void write(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(dimension);
    }

    public void read(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        dimension = buf.readInt();
    }

    public String getStringInfo() {
        return "X: " + x + " Y: " + y + " Z: " + z + " Dim: " + dimension;
    }

    public BlockPos getPos(){
        return new BlockPos(x,y,z);
    }

    public int getDimension(){
        return dimension;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Coord4D)) {
            return false;
        }
        Coord4D c = (Coord4D) obj;
        return x == c.x && y == c.y && z == c.z && dimension == c.dimension;
    }
}