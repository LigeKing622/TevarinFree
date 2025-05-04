 package dev.tevarin.utils;


 import dev.tevarin.utils.player.RotationUtil;
         import net.minecraft.client.entity.EntityOtherPlayerMP;
 import net.minecraft.entity.Entity;
 import net.minecraft.entity.player.EntityPlayer;
 import net.minecraft.world.World;

 public class FakePlayer extends EntityOtherPlayerMP {
       public static int idIndex = 0; private final EntityPlayer player;
    
       public FakePlayer(EntityPlayer player) {
             super((World) RotationUtil.mc.theWorld, player.getGameProfile());
             this.player = player;
             copyLocationAndAnglesFrom((Entity)player);
             setHealth(player.getHealth());
             setAbsorptionAmount(player.getAbsorptionAmount());
             setPositionAndRotation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
        
        
        
        
        
        
        
             this.rotationYaw = player.rotationYaw;
            this.rotationPitch = player.rotationPitch;
             this.rotationYawHead = player.rotationYawHead;
        
             RotationUtil.mc.theWorld.addEntityToWorld(--idIndex, (Entity)this);
        
            if (idIndex <= -100000) {
                  idIndex = -1;
                 }
           }
    
    
       public boolean isInvisibleToPlayer(EntityPlayer player) {
            return isInvisible();
           }
    
    
       public boolean isInvisible() {
             return (RotationUtil.mc.scheduledTasks.size() <= 3);
           }
    
    
    
    
    
    
       public void onUpdate() {
             if (this.player == null || !this.player.isEntityAlive()) {
                  RotationUtil.mc.theWorld.removeEntity((Entity)this);
                 }
             setSprinting(false);
           super.onUpdate();
           }
     }


