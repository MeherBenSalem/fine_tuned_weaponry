package io.nightbeam.runeborn.content;

import io.nightbeam.runeborn.mc.Shrines;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Ember Shrine: kindle it to rest, level up, travel and smith. */
public class ShrineBlock extends Block {
    private static final VoxelShape SHAPE = Shapes.or(
            box(1, 0, 1, 15, 3, 15),
            box(3, 3, 3, 13, 10, 13),
            box(2, 10, 2, 14, 13, 14));

    public ShrineBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide && player instanceof ServerPlayer sp) {
            Shrines.use(sp, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.9;
        double z = pos.getZ() + 0.5;
        level.addParticle(ParticleTypes.FLAME, x + (random.nextDouble() - 0.5) * 0.3, y, z + (random.nextDouble() - 0.5) * 0.3,
                0, 0.02, 0);
        if (random.nextInt(3) == 0) {
            level.addParticle(ParticleTypes.SMALL_FLAME, x + (random.nextDouble() - 0.5) * 0.5, y + 0.1,
                    z + (random.nextDouble() - 0.5) * 0.5, 0, 0.03, 0);
        }
        if (random.nextInt(6) == 0) {
            level.addParticle(ParticleTypes.END_ROD, x + (random.nextDouble() - 0.5) * 0.8, y + 0.3 + random.nextDouble(),
                    z + (random.nextDouble() - 0.5) * 0.8, 0, 0.01, 0);
        }
    }
}
