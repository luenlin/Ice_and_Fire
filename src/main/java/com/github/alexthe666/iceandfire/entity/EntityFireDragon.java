package com.github.alexthe666.iceandfire.entity;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.entity.ai.*;
import net.ilexiconn.llibrary.server.animation.Animation;
import net.ilexiconn.llibrary.server.animation.IAnimatedEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtByTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtTarget;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import com.github.alexthe666.iceandfire.core.ModItems;
import com.github.alexthe666.iceandfire.core.ModSounds;
import com.google.common.base.Predicate;

import fossilsarcheology.api.EnumDiet;

public class EntityFireDragon extends EntityDragonBase {

	public static Animation ANIMATION_TAILWHACK;
	public static Animation ANIMATION_FIRECHARGE;
	public static float[] growth_stage_1 = new float[]{1F, 3F};
	public static float[] growth_stage_2 = new float[]{3F, 7F};
	public static float[] growth_stage_3 = new float[]{7F, 12.5F};
	public static float[] growth_stage_4 = new float[]{12.5F, 20F};
	public static float[] growth_stage_5 = new float[]{20F, 30F};

	public EntityFireDragon(World worldIn) {
		super(worldIn, EnumDiet.CARNIVORE, 1, 18, 20, 500, 0.2F, 0.5F);
		this.setSize(0.78F, 1.2F);
		this.isImmuneToFire = true;
		this.ignoreFrustumCheck = true;
		ANIMATION_SPEAK = Animation.create(45);
		ANIMATION_BITE = Animation.create(35);
		ANIMATION_SHAKEPREY = Animation.create(65);
		ANIMATION_TAILWHACK = Animation.create(40);
		ANIMATION_FIRECHARGE = Animation.create(40);
		this.growth_stages = new float[][]{growth_stage_1, growth_stage_2, growth_stage_3, growth_stage_4, growth_stage_5};
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(2, this.aiSit = new EntityAISit(this));
		this.tasks.addTask(3, new DragonAIRiding(this, 1.5D));
		this.tasks.addTask(4, new DragonAIAttackMelee(this, 1.5D, true));
		this.tasks.addTask(5, new DragonAIAirTarget(this));
		this.tasks.addTask(6, new DragonAIWander(this, 1.0D));
		this.tasks.addTask(7, new DragonAIWatchClosest(this, EntityLivingBase.class, 6.0F));
		this.tasks.addTask(7, new DragonAILookIdle(this));
		this.tasks.addTask(8, new DragonAIBreakBlocks(this));
		this.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
		this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
		this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, true, new Class[0]));
		this.targetTasks.addTask(4, new DragonAITarget(this, EntityLivingBase.class, false, new Predicate<Entity>() {
			@Override
			public boolean apply(@Nullable Entity entity) {
				return entity instanceof EntityLivingBase;
			}
		}));
		this.targetTasks.addTask(5, new DragonAITargetItems(this, false));
	}

	@Override
	public String getTexture() {
		if (this.isModelDead()) {
			if (this.getDeathStage() >= (this.getAgeInDays() / 5) / 2) {
				return "iceandfire:textures/models/firedragon/skeleton";
			} else {
				return "iceandfire:textures/models/firedragon/" + this.getVariantName(this.getVariant()) + this.getDragonStage() + "_sleep";
			}
		}
		if (this.isSleeping()) {
			return "iceandfire:textures/models/firedragon/" + this.getVariantName(this.getVariant()) + this.getDragonStage() + "_sleep";
		} else {
			return "iceandfire:textures/models/firedragon/" + this.getVariantName(this.getVariant()) + this.getDragonStage() + "";
		}
	}

	public String getVariantName(int variant) {
		switch (variant) {
		default:
			return "red_";
		case 1:
			return "green_";
		case 2:
			return "bronze_";
		case 3:
			return "gray_";
		}
	}

	public Item getVariantScale(int variant) {
		switch (variant) {
		default:
			return ModItems.dragonscales_red;
		case 1:
			return ModItems.dragonscales_green;
		case 2:
			return ModItems.dragonscales_bronze;
		case 3:
			return ModItems.dragonscales_gray;
		}
	}

	public Item getVariantEgg(int variant) {
		switch (variant) {
		default:
			return ModItems.dragonegg_red;
		case 1:
			return ModItems.dragonegg_green;
		case 2:
			return ModItems.dragonegg_bronze;
		case 3:
			return ModItems.dragonegg_gray;
		}
	}

	public boolean canBeSteered() {
		return true;
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		switch (this.getRNG().nextInt(2)) {
		case 0:
			System.out.println("borke 1");
			if (this.getAnimation() != this.ANIMATION_BITE) {
				this.setAnimation(this.ANIMATION_BITE);
				return false;
			} else if (this.getAnimationTick() > 15 && this.getAnimationTick() < 25) {
				boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), ((int) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));
				this.attackDecision = false;
				return flag;
			}
			break;
		case 1:
			System.out.println("borke 2");
			if (entityIn.width < this.width * 0.5F) {
				if (this.getAnimation() != this.ANIMATION_SHAKEPREY) {
					this.setAnimation(this.ANIMATION_SHAKEPREY);
					entityIn.startRiding(this, true);
					return false;
				}
			} else {
				System.out.println("borke 3");
				if (this.getAnimation() != this.ANIMATION_BITE) {
					this.setAnimation(this.ANIMATION_BITE);
					return false;
				} else if (this.getAnimationTick() > 15 && this.getAnimationTick() < 25) {
					boolean flag1 = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), ((int) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));
					this.attackDecision = false;
					return flag1;
				}
			}
			break;
		case 2:
			if (this.getAnimation() != this.ANIMATION_TAILWHACK) {
				this.setAnimation(this.ANIMATION_TAILWHACK);
				return false;
			} else if (this.getAnimationTick() > 20 && this.getAnimationTick() < 25) {
				boolean flag2 = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), ((int) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));
				if (entityIn instanceof EntityLivingBase) {
					((EntityLivingBase) entityIn).knockBack(entityIn, 1, 1, 1);
				}
				this.attackDecision = false;
				return flag2;
			}
			break;
		}

		return false;
	}

	public void moveEntityTowards(Entity entity, double x, double y, double z, float velocity, float inaccuracy) {
		float f = MathHelper.sqrt_double(x * x + y * y + z * z);
		x = x / (double) f;
		y = y / (double) f;
		z = z / (double) f;
		x = x + this.rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
		y = y + this.rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
		z = z + this.rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
		x = x * (double) velocity;
		y = y * (double) velocity;
		z = z * (double) velocity;
		entity.motionX = x;
		entity.motionY = y;
		entity.motionZ = z;
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if(this.getDragonStage() < 2 && this.getRNG().nextInt(200) == 0){
			for(int i = 0; i < 10; i++){
				float radius = 1.8F + (i * 0.1F);
				float headPosX = (float) (radius * getRenderSize() * 0.3F * Math.cos((rotationYawHead + 90) * Math.PI / 180));
				float headPosZ = (float) (radius * getRenderSize() * 0.3F * Math.sin((rotationYawHead + 90) * Math.PI / 180));
				float headPosY = (float) (0.5 * getRenderSize() * 0.3F);
				this.worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, posX + headPosX, posY + headPosY, posZ + headPosZ, 0, 0, 0, new int[]{});
			}
		}
		if (this.getAttackTarget() != null && !this.isSleeping()) {
			if ((!attackDecision || this.isFlying())) {
				shootFireAtMob(this.getAttackTarget());
			} else {
				if (this.getEntityBoundingBox().expand((this.getRenderSize() / this.growth_stages[this.getDragonStage() - 1][1]), (this.getRenderSize() / this.growth_stages[this.getDragonStage() - 1][1]), (this.getRenderSize() / this.growth_stages[this.getDragonStage() - 1][1])).intersectsWith(this.getAttackTarget().getEntityBoundingBox())) {
					attackEntityAsMob(this.getAttackTarget());
				}

			}
		} else {
			this.setBreathingFire(false);
		}
	}

	public void riderShootFire(Entity controller) {
		if (this.getRNG().nextInt(5) == 0 && this.getDragonStage() > 2) {
			if (this.getAnimation() != this.ANIMATION_FIRECHARGE) {
				this.setAnimation(this.ANIMATION_FIRECHARGE);
			} else if (this.getAnimationTick() == 15) {
				rotationYaw = renderYawOffset;
				float headPosX = (float) (posX + 1.8F * getRenderSize() * 0.3F * Math.cos((rotationYaw + 90) * Math.PI / 180));
				float headPosZ = (float) (posZ + 1.8F * getRenderSize() * 0.3F * Math.sin((rotationYaw + 90) * Math.PI / 180));
				float headPosY = (float) (posY + 0.5 * getRenderSize() * 0.3F);

				worldObj.playEvent((EntityPlayer) null, 1016, new BlockPos(this), 0);
				EntityDragonFireCharge entitylargefireball = new EntityDragonFireCharge(worldObj, this, 0, 0, 0);
				float f = (float) 32000 / 20.0F;
				f = (f * f + f * 2.0F) / 3.0F;
				if (f > 1.0F) {
					f = 1.0F;
				}
				entitylargefireball.setAim(entitylargefireball, controller, controller.rotationPitch, controller.rotationYaw, 0.0F, f * 3.0F, 1.0F);
				float size = this.isChild() ? 0.4F : this.isAdult() ? 1.3F : 0.8F;
				entitylargefireball.setSizes(size, size);
				entitylargefireball.setPosition(headPosX, headPosY, headPosZ);
				if (!worldObj.isRemote) {
					worldObj.spawnEntityInWorld(entitylargefireball);
				}
			}
		} else {
			if (this.isBreathingFire()) {
				if (this.isActuallyBreathingFire() && this.ticksExisted % 3 == 0) {
					rotationYaw = renderYawOffset;
					float headPosX = (float) (posX + 1.8F * getRenderSize() * 0.3F * Math.cos((rotationYaw + 90) * Math.PI / 180));
					float headPosZ = (float) (posZ + 1.8F * getRenderSize() * 0.3F * Math.sin((rotationYaw + 90) * Math.PI / 180));
					float headPosY = (float) (posY + 0.5 * getRenderSize() * 0.3F);
					EntityDragonFire entitylargefireball = new EntityDragonFire(worldObj, this, 0, 0, 0);
					float f = (float) 32000 / 20.0F;
					f = (f * f + f * 2.0F) / 3.0F;
					if (f > 1.0F) {
						f = 1.0F;
					}
					entitylargefireball.setAim(entitylargefireball, controller, controller.rotationPitch, controller.rotationYaw, 0.0F, f * 3.0F, 1.0F);
					worldObj.playEvent((EntityPlayer) null, 1016, new BlockPos(this), 0);
					float size = this.isChild() ? 0.4F : this.isAdult() ? 1.3F : 0.8F;
					entitylargefireball.setPosition(headPosX, headPosY, headPosZ);
					if (!worldObj.isRemote) {
						worldObj.spawnEntityInWorld(entitylargefireball);
					}
				}
			} else {
				this.setBreathingFire(true);
			}
		}
	}

	private void shootFireAtMob(EntityLivingBase entity) {
		if (!this.attackDecision) {
			if(this.getDragonStage() < 2){
				this.setBreathingFire(false);
				this.attackDecision = true;
				return;
			}
			if (this.getRNG().nextInt(5) == 0 && !this.isChild()) {
				if (this.getAnimation() != this.ANIMATION_FIRECHARGE) {
					this.setAnimation(this.ANIMATION_FIRECHARGE);
				} else if (this.getAnimationTick() == 15) {
					rotationYaw = renderYawOffset;
					float headPosX = (float) (posX + 1.8F * getRenderSize() * 0.3F * Math.cos((rotationYaw + 90) * Math.PI / 180));
					float headPosZ = (float) (posZ + 1.8F * getRenderSize() * 0.3F * Math.sin((rotationYaw + 90) * Math.PI / 180));
					float headPosY = (float) (posY + 0.5 * getRenderSize() * 0.3F);
					double d1 = -1D;
					Vec3d vec3 = this.getLook(1.0F);
					double d2 = entity.posX - (headPosX + vec3.xCoord * d1);
					double d3 = entity.getEntityBoundingBox().minY + (double) (entity.height / 2.0F) - (0.5D + headPosY + (double) (this.height / 2.0F));
					double d4 = entity.posZ - (headPosZ + vec3.zCoord * d1);
					worldObj.playEvent((EntityPlayer) null, 1016, new BlockPos(this), 0);
					EntityDragonFireCharge entitylargefireball = new EntityDragonFireCharge(worldObj, this, d2, d3, d4);
					float size = this.isChild() ? 0.4F : this.isAdult() ? 1.3F : 0.8F;
					entitylargefireball.setSizes(size, size);
					entitylargefireball.setPosition(headPosX, headPosY, headPosZ);
					if (!worldObj.isRemote) {
						worldObj.spawnEntityInWorld(entitylargefireball);
					}
					if (entity.isDead) {
						this.setBreathingFire(false);
						this.attackDecision = true;
					}
				}
			} else {
				if (this.isBreathingFire()) {
					if (this.isActuallyBreathingFire() && this.ticksExisted % 3 == 0) {
						rotationYaw = renderYawOffset;
						float headPosX = (float) (posX + 1.8F * getRenderSize() * 0.3F * Math.cos((rotationYaw + 90) * Math.PI / 180));
						float headPosZ = (float) (posZ + 1.8F * getRenderSize() * 0.3F * Math.sin((rotationYaw + 90) * Math.PI / 180));
						float headPosY = (float) (posY + 0.5 * getRenderSize() * 0.3F);
						double d1 = 0D;
						Vec3d vec3 = this.getLook(1.0F);
						double d2 = entity.posX - headPosX;
						double d3 = entity.posY - headPosY;
						double d4 = entity.posZ - headPosZ;
						// double d2 = entity.posX - (headPosX + vec3.xCoord *
						// d1);
						// double d3 = entity.getEntityBoundingBox().minY +
						// (double) (entity.height / 2.0F) - (0.5D + headPosY +
						// (double) (this.height / 2.0F));
						// double d4 = entity.posZ - (headPosZ + vec3.zCoord *
						// d1);
						worldObj.playEvent((EntityPlayer) null, 1016, new BlockPos(this), 0);
						EntityDragonFire entitylargefireball = new EntityDragonFire(worldObj, this, d2, d3, d4);
						float size = this.isChild() ? 0.4F : this.isAdult() ? 1.3F : 0.8F;
						entitylargefireball.setPosition(headPosX, headPosY, headPosZ);
						if (!worldObj.isRemote && !entity.isDead) {
							worldObj.spawnEntityInWorld(entitylargefireball);
						}
						entitylargefireball.setSizes(size, size);
						if (entity.isDead) {
							this.setBreathingFire(false);
							this.attackDecision = true;
						}
					}
				} else {
					this.setBreathingFire(true);
				}
			}
		}
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return this.isTeen() ? ModSounds.firedragon_teen_idle : this.isAdult() ? ModSounds.firedragon_adult_idle : ModSounds.firedragon_child_idle;
	}

	@Override
	protected SoundEvent getHurtSound() {
		return this.isTeen() ? ModSounds.firedragon_teen_hurt : this.isAdult() ? ModSounds.firedragon_adult_hurt : ModSounds.firedragon_child_hurt;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return this.isTeen() ? ModSounds.firedragon_teen_death : this.isAdult() ? ModSounds.firedragon_adult_death : ModSounds.firedragon_child_death;
	}

	@Override
	public Animation[] getAnimations() {
		return new Animation[] { IAnimatedEntity.NO_ANIMATION, EntityDragonBase.ANIMATION_EAT, EntityDragonBase.ANIMATION_SPEAK, EntityDragonBase.ANIMATION_BITE, EntityDragonBase.ANIMATION_SHAKEPREY, EntityFireDragon.ANIMATION_TAILWHACK, EntityFireDragon.ANIMATION_FIRECHARGE };
	}

	@Override
	public String getTextureOverlay() {
		return this.isSleeping() || this.isModelDead() ? null : "iceandfire:textures/models/firedragon/" + this.getVariantName(this.getVariant()) + this.getDragonStage() + "_eyes";
	}

}
