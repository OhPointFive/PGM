package tc.oc.pgm.util.nms.packets;

import java.time.Duration;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public interface PlayerPackets {
  void playDeathAnimation(Player player);

  void showBorderWarning(Player player, boolean show);

  void fakePlayerItemPickup(Player player, Item item);

  void sendLegacyHelmet(Player player, ItemStack item);

  void updateVelocity(Player player);

  // Why are we doing this in PGM? Why not our own plugin?
  // This is unclear to me.
  void showFakeItems(
      Plugin plugin,
      Player viewer,
      Location location,
      ItemStack item,
      int count,
      Duration duration);
}
