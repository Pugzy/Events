package dev.pgm.events.ready;

import dev.pgm.events.Tournament;
import dev.pgm.events.config.AppData;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import tc.oc.pgm.api.match.MatchPhase;
import tc.oc.pgm.api.match.event.MatchLoadEvent;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.events.CountdownCancelEvent;
import tc.oc.pgm.events.CountdownStartEvent;
import tc.oc.pgm.events.PlayerLeaveMatchEvent;
import tc.oc.pgm.events.PlayerPartyChangeEvent;
import tc.oc.pgm.start.StartCountdown;
import tc.oc.pgm.teams.Team;

import java.time.Duration;
import java.util.Optional;

public class ReadyListener implements Listener {

  private final ReadyManager readyManager;

  public ReadyListener(ReadyManager readyManager) {
    this.readyManager = readyManager;
  }

  @EventHandler
  public void onQueueStart(CountdownStartEvent event) {
    if (event.getCountdown() instanceof StartCountdown)
      readyManager.getReadySystem().onStart(
          ((StartCountdown) event.getCountdown()).getRemaining(),
          readyManager.getReadyParties().allReady(event.getMatch()));
  }

  @EventHandler
  public void onCancel(CountdownCancelEvent event) {
    if (!(event.getCountdown() instanceof StartCountdown)) return;

    Duration remaining = readyManager.getReadySystem().onCancel(readyManager.getReadyParties().allReady(event.getMatch()));
    if (remaining != null)
      readyManager.createMatchStart(event.getMatch(), remaining);
  }

  @EventHandler
  public void onStart(MatchLoadEvent event) {
    readyManager.getReadySystem().reset();
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onLeave(PlayerLeaveMatchEvent event) {
    if (!AppData.autoUnready()) {
      return;
    }

    // if match starting and team was ready unready them
    if (event.getMatch().getPhase() == MatchPhase.STARTING) {
      if (readyManager.getReadyParties().isReady(event.getParty())) {
        readyManager.unreadyTeam(event.getPlayer().getParty());
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPartyChange(PlayerPartyChangeEvent event) {
    if (!AppData.readyReminders()) {
      return;
    }

    MatchPlayer player = event.getPlayer();
    Optional<Team> playerTeam = Tournament.get().getTeamManager().playerTeam(player.getId());

    // Add hint to ready up once all players joined
    if (playerTeam.isPresent() && !event.getMatch().isRunning()) {
      if (readyManager.playerTeamFull(player.getParty())) {
        event.getPlayer().getParty().sendMessage(ChatColor.GREEN + "Mark your team as ready using " +
                ChatColor.YELLOW +  "/ready" + ChatColor.GREEN + ".");
      }
    }
  }

}
