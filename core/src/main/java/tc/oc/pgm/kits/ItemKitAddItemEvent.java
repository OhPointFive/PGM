package tc.oc.pgm.kits;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import tc.oc.pgm.api.player.MatchPlayer;

/**
 * Fired when an {@link ItemKit} is applied to a player. The kit can be modified through the
 * containers returned by the various getter methods. Note that {@link ArmorKit}s fire a generic
 * {@link ApplyKitEvent}, not this one.
 */
public class ItemKitAddItemEvent extends Event {
  private MatchPlayer player;
  private int index;
  private ItemStack itemStack;

  public ItemKitAddItemEvent(MatchPlayer player, int index, ItemStack itemStack) {
    this.player = player;
    this.index = index;
    this.itemStack = itemStack;
  }

  public MatchPlayer getPlayer() {
    return player;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public ItemStack getItemStack() {
    return itemStack;
  }

  public void setItemStack(ItemStack itemStack) {
    this.itemStack = itemStack;
  }

  /* Handler junk */
  private static final HandlerList handlers = new HandlerList();

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }
}
