package com.kltyton.kltytonspawnegg.mixin;

import com.kltyton.kltytonspawnegg.config.KltytonConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(ItemRenderer.class)
public class GuiItemMixin {
    @Unique
    private final KltytonConfig config = AutoConfig.getConfigHolder(KltytonConfig.class).getConfig();

    @Unique
    private final Map<EntityType<?>, MobEntity> entityCache = new HashMap<>();

    @Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
            at = @At("HEAD"), cancellable = true)
    private void onRenderItemStack(
            ItemStack stack,
            ModelTransformationMode renderMode,
            boolean leftHanded,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            int overlay,
            BakedModel model,
            CallbackInfo ci
    ) {
        renderCustomSpawnEgg(stack, renderMode, matrices, vertexConsumers, light, ci);
    }

    @Unique
    private void renderCustomSpawnEgg(
            ItemStack item,
            ModelTransformationMode renderMode,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            CallbackInfo ci
    ) {
        if (!(item.getItem() instanceof SpawnEggItem spawnEggItem)) {
            return;
        }

        EntityType<?> entityType = spawnEggItem.getEntityType(item.getNbt());
        if (entityType == null) {
            return;
        }

        Identifier entityId = Registries.ENTITY_TYPE.getId(entityType);
        if ("mythicmounts".equals(entityId.getNamespace())) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.currentScreen == null) {
                return;
            }
        }

        World world = MinecraftClient.getInstance().world;
        if (world == null) {
            return;
        }

        MobEntity mobEntity = entityCache.computeIfAbsent(entityType, type -> (MobEntity) type.create(world));
        if (mobEntity == null) {
            return;
        }
        // 重置实体状态并锁定动画
        resetEntityState(mobEntity);
        lockAnimationState(mobEntity);
        matrices.push();
        //总体缩放比例
        float scale = calculateScale(mobEntity)* config.totalZoomRate;
        float iconZoomRate = config.iconZoomRate;
        float rotationAngle = config.rotationAngle;
        float itemZoomRate = config.itemZoomRate;

        if (renderMode == ModelTransformationMode.GUI) {
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotationAngle));
            matrices.translate(0.0, -0.5, 0.0);
            matrices.scale(scale * 1.6f * iconZoomRate, scale * 1.6f * iconZoomRate, scale * 1.6f * iconZoomRate);
        } else {
            matrices.scale(scale * itemZoomRate, scale * itemZoomRate, scale * itemZoomRate);
        }

        EntityRenderDispatcher dispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        dispatcher.render(mobEntity, 0, 0, 0, 0.0f, 1.0f, matrices, vertexConsumers, light);

        matrices.pop();
        ci.cancel();
    }

    /**
     * 重置实体的旋转、位置和状态，确保它在渲染时是固定的。
     */
    @Unique
    private void resetEntityState(MobEntity mobEntity) {
        mobEntity.handSwingTicks = 0;
        mobEntity.setYaw(0.0f);
        mobEntity.setPitch(0.0f);
        mobEntity.headYaw = 0.0f;
        mobEntity.prevHeadYaw = 0.0f;
        mobEntity.bodyYaw = 0.0f;
        mobEntity.prevBodyYaw = 0.0f;
        mobEntity.setVelocity(Vec3d.ZERO);
        mobEntity.limbAnimator.updateLimbs(0.0f,0.0f);
        mobEntity.limbAnimator.setSpeed(0.0f);
    }
    @Unique
    private void lockAnimationState(MobEntity mobEntity) {
        mobEntity.age = 0;
        mobEntity.hurtTime = 0;
        mobEntity.deathTime = 0;
        mobEntity.handSwingProgress = 0.0f;
        mobEntity.handSwingTicks = 0;
    }
    /**
     * 根据实体的尺寸计算缩放比例，确保生成蛋的显示比例合适。
     */
    @Unique
    private float calculateScale(MobEntity mobEntity) {
        float maxZoomRate = config.maxZoomRate;
        float minZoomRate = config.minZoomRate;
        Vec3d dimensions = new Vec3d(mobEntity.getWidth(), mobEntity.getHeight(), mobEntity.getWidth());
        float maxDimension = (float) Math.max(dimensions.x, Math.max(dimensions.y, dimensions.z));
        float scale = 0.45f / maxDimension;
        return Math.max(minZoomRate, Math.min(scale, maxZoomRate));
    }
}
