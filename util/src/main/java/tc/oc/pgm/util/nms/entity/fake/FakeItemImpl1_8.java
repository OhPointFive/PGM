package tc.oc.pgm.util.nms.entity.fake;

import net.minecraft.server.v1_8_R3.EntityItem;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import tc.oc.pgm.util.nms.NMSHacks;

public class FakeItemImpl1_8 extends FakeEntityImpl1_8<EntityItem> {

  public FakeItemImpl1_8(World world) {
    super(new EntityItem(((CraftWorld) world).getHandle()));
  }

  @Override
  public void spawn(Player viewer, Location location, Vector velocity) {
    entity.setPositionRotation(
        location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    entity.motX = velocity.getX();
    entity.motY = velocity.getY();
    entity.motZ = velocity.getZ();
    NMSHacks.sendPacket(viewer, spawnPacket());
  }

  public Packet<?> spawnPacket() {
    return new PacketPlayOutSpawnEntity(entity, 2);
  }
}
