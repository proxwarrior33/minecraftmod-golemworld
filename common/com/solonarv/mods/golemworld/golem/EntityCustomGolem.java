package com.solonarv.mods.golemworld.golem;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityOwnable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/**
 * GolemWorld
 * 
 * ABC of all our golems. Defines stats and implementation of gaussian-random
 * attack damage. Extends {@link EntityIronGolem} for convenience: most code
 * would be copypasted anyway; it also lets me use the same model without any
 * fuss.
 * 
 * @author Solonarv
 * @license LGPL v3
 */
public abstract class EntityCustomGolem extends EntityIronGolem implements EntityOwnable{
    
    /**
     * Overridden by all non-abstract subclasses: Holds this golem's maximum
     * health, attack damage curve parameters, and drops.
     */
    private static GolemStats stats;
    /**
     * Cached reference to the stats from this golem's actual class (instead of
     * this ABC), needed because attributes are not virtual (unlike methods)
     */
    protected GolemStats        actualStats = null;
    
    // Make private attackTimer from superclass visible
    protected int             attackTimer;
    private String creator;
    
    public EntityCustomGolem(World world) {
        super(world);
        // func_94058_c(this.getStats().name);
    }
    
    /**
     * Utility function that makes sure actualStats is initialized, then returns
     * it. Uses reflection to do a virtual access instead of static access to
     * private static {@link GolemStats} stats. Prevents NPEs from occuring as
     * long as reflection does not fail.
     * 
     * @return
     */
    public GolemStats stats() {
        if (this.actualStats == null) {
            try {
                this.actualStats = (GolemStats) this.getClass().getField("stats").get(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this.actualStats;
    }
    
    /**
     * Identical to super method except for amount of damage done: It's
     * gaussian-random, parametrized in stats.attackDamageMean and
     * stats.attackDamageStdDev
     */
    @Override
    public boolean attackEntityAsMob(Entity par1Entity) {
        attackTimer = 10;
        worldObj.setEntityState(this, (byte) 4);
        boolean flag = par1Entity.attackEntityFrom(
                DamageSource.causeMobDamage(this), this.getAttackStrength(par1Entity));
        
        if (flag) {
            par1Entity.motionY += 0.4000000059604645D;
        }
        
        playSound("mob.irongolem.throw", 1.0F, 1.0F);
        return flag;
    }
    
    /**
     * Generates the gaussian-random attack damage using Random.nextGaussian
     * @param attackTarget the entity to be attacked (provided for subclass overrides)
     * 
     * @return randomized attack damage
     */
    public float getAttackStrength(Entity attackTarget) {
        return this.stats().attackDamageMean + ((float) (rand.nextGaussian()))
                * this.stats().attackDamageStdDev;
    };
    
    /**
     * Safe access of the golem's name
     * 
     * @return the golem's name
     */
    public final String getName() {
        return this.stats().name;
    }
    
    /**
     * Updates the mob's attributes, probably gets called on init.
     */
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute((SharedMonsterAttributes.maxHealth))
                .setAttribute(this.stats().maxHealth);
    }
    
    /**
     * Drops what is specified in this golem's drop list.
     * super.dropFewItems(...) deliberately not called, as I don't want to drop
     * iron ingots and a rose for all the golems.
     */
    @Override
    public void dropFewItems(boolean recentlyHit, int lootingLevel) {
        for (ItemStack is : this.stats().droppedItems) {
            entityDropItem(is.copy(), 0F);
        }
    }
    
    @Override
    public boolean canAttackClass(Class clazz){
        // Allows attacking of creepers only if the golem is very likely to one-hit the crepper, and everything else unconditionally.
        return clazz != EntityCreeper.class || this.stats().attackDamageMean - this.stats().attackDamageStdDev >= 20;
    }

    public void setCreator(String username) {
        this.creator = username;
        this.setPlayerCreated(username != null);
    }
    
    public String getOwnerName(){
        return this.creator;
    }
    
    public Entity getOwner(){
        return this.worldObj.getPlayerEntityByName(this.getOwnerName());
    }
    
    @Override
    public void writeEntityToNBT(NBTTagCompound nbt){
        super.writeEntityToNBT(nbt);
        nbt.setString("creator", this.creator == null ? "$null$" : this.creator);
    }
    
    @Override
    public void readEntityFromNBT(NBTTagCompound nbt){
        super.readEntityFromNBT(nbt);
        String cr=nbt.getString("creator");
        this.creator = cr.equals("$null$") ? null : cr;
    }
}
