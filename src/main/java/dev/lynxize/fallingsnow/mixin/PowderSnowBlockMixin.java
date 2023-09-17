package dev.lynxize.fallingsnow.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PowderSnowBlock.class)
public abstract class PowderSnowBlockMixin extends Block implements Fallable {
    public PowderSnowBlockMixin(Properties properties) {
        super(properties);
    }

    public void onPlace(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        level.scheduleTick(blockPos, this, this.getDelayAfterPlace());
    }

    public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState2, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2) {
        levelAccessor.scheduleTick(blockPos, this, this.getDelayAfterPlace());
        return super.updateShape(blockState, direction, blockState2, levelAccessor, blockPos, blockPos2);
    }

    public void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
        if (isFree(serverLevel.getBlockState(blockPos.below())) && blockPos.getY() >= serverLevel.getMinBuildHeight()) {
            FallingBlockEntity fbo = FallingBlockEntity.fall(serverLevel, blockPos, blockState);

        }
    }

    protected int getDelayAfterPlace() {
        return 2;
    }

    private static boolean isFree(BlockState blockState) {
        return blockState.isAir() || blockState.is(BlockTags.FIRE) || blockState.canBeReplaced();
    }

    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource) {
        if (randomSource.nextInt(16) == 0) {
            BlockPos blockPos2 = blockPos.below();
            if (isFree(level.getBlockState(blockPos2))) {
                ParticleUtils.spawnParticleBelow(level, blockPos, randomSource, new BlockParticleOption(ParticleTypes.FALLING_DUST, blockState));
            }
        }
    }
}
