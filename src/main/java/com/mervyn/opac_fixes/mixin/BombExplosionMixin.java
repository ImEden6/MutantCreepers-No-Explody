package com.mervyn.opac_fixes.mixin;

import net.mehvahdjukaar.supplementaries.common.misc.explosion.BombExplosion;
import net.minecraft.entity.Entity;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import java.util.List;
import com.mervyn.opac_fixes.OpacCompat;

@Mixin(value = BombExplosion.class, priority = 1000)
public abstract class BombExplosionMixin extends Explosion {

    private BombExplosionMixin() {
        super(null, null, null, null, 0, 0, 0, 0, false, Explosion.DestructionType.KEEP);
    }

    @ModifyVariable(method = "collectBlocksAndDamageEntities", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/World;getOtherEntities("
            + "Lnet/minecraft/entity/Entity;"
            + "Lnet/minecraft/util/math/Box;"
            + ")Ljava/util/List;"))
    private List<Entity> filterProtectedEntities(List<Entity> entityList) {
        OpacCompat.onExplosionDetonate(this, entityList, ((ExplosionAccessor) this).opac_fixes$getWorld());
        return entityList;
    }
}
