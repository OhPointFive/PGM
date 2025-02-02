package tc.oc.pgm.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import tc.oc.pgm.api.PGM;
import tc.oc.pgm.api.match.MatchManager;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.api.player.event.PlayerVanishEvent;

public class JoinLeaveAnnouncer implements Listener {

  private final MatchManager mm;

  public JoinLeaveAnnouncer(MatchManager mm) {
    this.mm = mm;
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void announceJoin(final PlayerJoinEvent event) {
    MatchPlayer player = this.mm.getPlayer(event.getPlayer());
    if (player == null) return;

    if (event.getJoinMessage() != null) {
      event.setJoinMessage(null);
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void announceLeave(PlayerQuitEvent event) {
    MatchPlayer player = this.mm.getPlayer(event.getPlayer());
    if (player == null) return;

    if (event.getQuitMessage() != null && !PGM.get().getConfiguration().showQuitMessages()) {
      event.setQuitMessage(null);
    }
  }

  @EventHandler
  public void onPlayerVanish(PlayerVanishEvent event) {
    MatchPlayer player = event.getPlayer();
    if (player == null) return;
    if (event.isQuiet()) return;
  }
}
