package dev.pgm.events.team;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class DefaultTournamentTeam implements TournamentTeam {

  private final String name;
  private final List<TournamentPlayer> participants;

  public DefaultTournamentTeam(String name, List<TournamentPlayer> participants) {
    this.name = name;
    this.participants = participants;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public List<TournamentPlayer> getPlayers() {
    return this.participants;
  }

  @Override
  public void setPlayers(List<TournamentPlayer> players) {
    this.participants.clear();
    this.participants.addAll(players);
  }

  @Override
  public void addPlayer(UUID player, boolean canVeto) {
    this.participants.add(TournamentPlayer.create(player, canVeto));
  }

  @Override
  public void removePlayer(UUID player) {
    this.participants.removeIf(tournamentPlayer -> tournamentPlayer.getUUID().equals(player));
  }

  @Override
  public String toString() {
    return "Team "
        + getName()
        + ": "
        + getPlayers().stream().map(x -> x.getUUID().toString()).collect(Collectors.joining(", "));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    TournamentTeam that = (TournamentTeam) o;

    return name.equals(that.getName());
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }
}
