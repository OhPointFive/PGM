package tc.oc.pgm.autojoin;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;
import tc.oc.pgm.api.PGM;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.match.MatchModule;
import tc.oc.pgm.api.match.MatchScope;
import tc.oc.pgm.api.match.factory.MatchModuleFactory;
import tc.oc.pgm.api.module.exception.ModuleLoadException;
import tc.oc.pgm.api.party.Competitor;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.api.setting.SettingKey;
import tc.oc.pgm.blitz.BlitzMatchModule;
import tc.oc.pgm.events.ListenerScope;
import tc.oc.pgm.events.PlayerJoinMatchEvent;
import tc.oc.pgm.join.JoinMatchModule;
import tc.oc.pgm.join.JoinRequest;
import tc.oc.pgm.join.JoinResult;
import tc.oc.pgm.join.JoinResultOption;
import tc.oc.pgm.teams.Team;
import tc.oc.pgm.teams.TeamMatchModule;

@ListenerScope(MatchScope.LOADED)
public class AutoJoinMatchModule implements MatchModule, Listener {

  public static class Factory implements MatchModuleFactory<AutoJoinMatchModule> {
    @Override
    public Collection<Class<? extends MatchModule>> getWeakDependencies() {
      return ImmutableList.of(BlitzMatchModule.class);
    }

    @Override
    public Collection<Class<? extends MatchModule>> getHardDependencies() {
      return ImmutableList.of(JoinMatchModule.class);
    }

    @Override
    public AutoJoinMatchModule createMatchModule(Match match) throws ModuleLoadException {
      if (!(PGM.get()
          .getConfiguration()
          .getExperiments()
          .getOrDefault("auto-join", "false")
          .equals(true))) {
        return null;
      }
      return new AutoJoinMatchModule(match);
    }
  }

  private final Match match;
  private boolean isBlitz;

  private AutoJoinMatchModule(Match match) {
    this.match = match;
    this.isBlitz = match.hasModule(BlitzMatchModule.class);
  }

  protected boolean settingEnabled(MatchPlayer player) {
    switch (player.getSettings().getValue(SettingKey.AUTOJOIN)) {
      case AUTOJOIN_OFF:
        return false;
      case AUTOJOIN_ON:
        return true;
      default:
        return true;
    }
  }

  private boolean hasJoined(MatchPlayer joining) {
    return joining.isParticipating()
        || match.needModule(JoinMatchModule.class).isQueuedToJoin(joining);
  }

  private boolean canAutoJoin(MatchPlayer joining) {
    JoinResult result =
        match
            .needModule(JoinMatchModule.class)
            .queryJoin(joining, JoinRequest.fromPlayer(joining, null));
    return result.isSuccess() || result.getOption() == JoinResultOption.FULL;
  }

  private boolean canChooseMultipleTeams(MatchPlayer joining) {
    return getChoosableTeams(joining).size() > 1;
  }

  private Set<Team> getChoosableTeams(MatchPlayer joining) {
    TeamMatchModule tmm = match.getModule(TeamMatchModule.class);
    if (tmm == null) return Collections.emptySet();

    Set<Team> teams = new HashSet<>();
    for (Team team : tmm.getTeams()) {
      JoinResult result = tmm.queryJoin(joining, JoinRequest.fromPlayer(joining, team));
      // We still want to show the button if the team is full
      // or the player doesn't have join perms.
      if (result.isSuccess()
          || result.getOption() == JoinResultOption.FULL
          || result.getOption() == JoinResultOption.CHOICE_DENIED) {

        teams.add(team);
      }
    }

    return teams;
  }

  /** Does the player have any use for the picker? */
  private boolean canJoin(MatchPlayer player) {
    if (player == null) return false;

    // Player is eliminated from Blitz
    if (isBlitz && match.isRunning()) return false;

    // Player is not observing or dead
    if (!(player.isObserving() || player.isDead())) return false;

    // Not allowed to join teams
    if (!canAutoJoin(player)) return false;

    return true;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void join(PlayerJoinMatchEvent event) {
    final MatchPlayer player = event.getPlayer();
    if (event.getNewParty() instanceof Competitor) return;
    if (!settingEnabled(player)) return;
    if (!canJoin(event.getPlayer())) return;

    scheduleJoin(player, null);
  }

  private void scheduleJoin(final MatchPlayer player, @Nullable final Team team) {
    if (team == player.getParty() || (team == null && hasJoined(player))) return;
    if (!settingEnabled(player)) return;

    final Player bukkit = player.getBukkit();
    match
        .getExecutor(MatchScope.LOADED)
        .schedule(
            () -> {
              if (bukkit.isOnline() && settingEnabled(player)) {
                match.needModule(JoinMatchModule.class).join(player, team);
              }
            },
            (long)(new Random().nextDouble() * 500),
            TimeUnit.MICROSECONDS);
  }
}
