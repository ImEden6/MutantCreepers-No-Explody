package me.mervyn.m_creepers_no_explody.mixin;

import fuzs.mutantmonsters.world.level.MutatedExplosion;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import me.mervyn.m_creepers_no_explody.MutantCreepersNoExplody;

/**
 * Mixin into MutatedExplosion to invoke OPAC's explosion protection.
 *
 * MutatedExplosion overrides Explosion.collectBlocksAndDamageEntities()
 * entirely,
 * which bypasses OPAC's MixinFabricExplosion that hooks into the vanilla
 * method.
 * This mixin intercepts the entity list after getOtherEntities() is called,
 * and passes it through OPAC's onExplosionDetonate() handler to filter out
 * blocks and entities in protected claimed chunks.
 */
@Mixin(MutatedExplosion.class)
public abstract class MutatedExplosionMixin extends Explosion {

    // Reflection handles resolved once at class load — avoids per-explosion lookup
    // cost.
    private static final Field OPAC_INSTANCE_FIELD;
    private static final Class<?> OPAC_FABRIC_CLASS;
    private static final Method OPAC_GET_COMMON_EVENTS;
    private static final Method OPAC_ON_EXPLOSION_DETONATE;
    private static final boolean OPAC_AVAILABLE;

    static {
        Field instanceField = null;
        Class<?> fabricClass = null;
        Method getCommonEvents = null;
        Method onExplosionDetonate = null;
        boolean available = false;

        try {
            Class<?> opacClass = Class.forName("xaero.pac.OpenPartiesAndClaims");
            instanceField = opacClass.getField("INSTANCE");

            fabricClass = Class.forName("xaero.pac.OpenPartiesAndClaimsFabric");
            getCommonEvents = fabricClass.getMethod("getCommonEvents");

            Class<?> commonEventsClass = Class.forName("xaero.pac.common.event.CommonEventsFabric");
            onExplosionDetonate = commonEventsClass.getMethod(
                    "onExplosionDetonate",
                    Explosion.class,
                    List.class,
                    World.class);
            available = true;
        } catch (ReflectiveOperationException e) {
            MutantCreepersNoExplody.LOGGER.error(
                    "OPAC initialization failed, explosion claim protection will be inactive", e);
        }

        OPAC_INSTANCE_FIELD = instanceField;
        OPAC_FABRIC_CLASS = fabricClass;
        OPAC_GET_COMMON_EVENTS = getCommonEvents;
        OPAC_ON_EXPLOSION_DETONATE = onExplosionDetonate;
        OPAC_AVAILABLE = available;
    }

    @Shadow(remap = false)
    @Final
    private World world;

    // Dummy constructor required because we extend Explosion
    private MutatedExplosionMixin() {
        super(null, null, null, null, 0, 0, 0, 0, false, Explosion.DestructionType.KEEP);
    }

    @ModifyVariable(method = "collectBlocksAndDamageEntities", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/World;getOtherEntities("
            + "Lnet/minecraft/entity/Entity;"
            + "Lnet/minecraft/util/math/Box;"
            + "Ljava/util/function/Predicate;"
            + ")Ljava/util/List;"))
    private List<Entity> filterProtectedEntities(List<Entity> entityList) {
        if (!OPAC_AVAILABLE || !(this.world instanceof ServerWorld)) {
            return entityList;
        }

        try {
            Object instance = OPAC_INSTANCE_FIELD.get(null);
            if (OPAC_FABRIC_CLASS.isInstance(instance)) {
                Object commonEvents = OPAC_GET_COMMON_EVENTS.invoke(instance);
                OPAC_ON_EXPLOSION_DETONATE.invoke(commonEvents, this, entityList, this.world);
            }
        } catch (ReflectiveOperationException e) {
            Throwable cause = e instanceof InvocationTargetException ite && ite.getCause() != null
                    ? ite.getCause()
                    : e;
            MutantCreepersNoExplody.LOGGER.error(
                    "Could not invoke OPAC explosion protection", cause);
        }

        return entityList;
    }
}
