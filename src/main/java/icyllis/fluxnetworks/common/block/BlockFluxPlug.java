package icyllis.fluxnetworks.common.block;

import icyllis.fluxnetworks.common.tileentity.TileFluxPlug;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;
import java.util.List;

public class BlockFluxPlug extends BlockSidedConnection {

    public BlockFluxPlug() {
        bounding = VoxelShapes.create(0.25F, 0.25F, 0.25F, 0.75F, 0.75F, 0.75F);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileFluxPlug();
    }
}