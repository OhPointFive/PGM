package tc.oc.pgm.score;

import org.bukkit.event.HandlerList;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.api.player.event.MatchPlayerEvent;

/** Called when a {@link MatchPlayer} enters a scorebox. */
public class PlayerScoreboxEvent extends MatchPlayerEvent {

  private final ScoreBox box;
  private final double points;

  public PlayerScoreboxEvent(ScoreBox box, MatchPlayer player, double points) {
    super(player);
    this.box = box;
    this.points = points;
  }

  /**
   * The scorebox entered.
   *
   * @return The {@link ScoreBox}.
   */
  public final ScoreBox getBox() {
    return box;
  }

  /**
   * The points earned by going in the scorebox
   *
   * @return The points.
   */
  public final double getPoints() {
    return points;
  }

  private static final HandlerList handlers = new HandlerList();

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }
}
