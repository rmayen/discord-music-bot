import com.sedmelluq.discord.lavaplayer.player.*;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SimpleBot extends ListenerAdapter {

    private final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
    private final AudioPlayer player = playerManager.createPlayer();
    private final TrackScheduler trackScheduler = new TrackScheduler(player);

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getMessage().getContentRaw().startsWith("!")) return;

        String[] parts = event.getMessage().getContentRaw().substring(1).split(" ", 2);
        String command = parts[0];
        String content = parts.length > 1 ? parts[1] : "";

        switch (command.toLowerCase()) {
            case "play":
                loadAndPlay(event.getTextChannel(), content);
                break;
            case "pause":
                togglePause();
                break;
            case "skip":
            case "next":
                trackScheduler.nextTrack();
                break;
            case "identify":
                identify(event.getTextChannel());
                break;
        }
    }

    private void loadAndPlay(TextChannel channel, String url) {
        playerManager.loadItemOrdered(player, url, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                channel.sendMessage("Adding to queue: " + track.getInfo().title).queue();
                trackScheduler.queue(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();
                if (firstTrack == null) firstTrack = playlist.getTracks().get(0);
                channel.sendMessage("Adding playlist starting with: " + firstTrack.getInfo().title).queue();
                trackScheduler.queue(firstTrack);
            }

            @Override
            public void noMatches() {
                channel.sendMessage("No track found for: " + url).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                channel.sendMessage("Could not load track: " + exception.getMessage()).queue();
            }
        });
    }

    private void togglePause() {
        player.setPaused(!player.isPaused());
    }

    private void identify(TextChannel textChannel) {
        AudioTrack currentTrack = trackScheduler.getCurrentTrack();
        if (currentTrack != null) {
            textChannel.sendMessage("Currently playing: " + currentTrack.getInfo().title).queue();
        } else {
            textChannel.sendMessage("No track is currently playing.").queue();
        }
    }

    public static void main(String[] args) throws Exception {
        String token = System.getenv("DISCORD_TOKEN");
        if (token == null || token.isEmpty()) {
            System.err.println("Error: DISCORD_TOKEN environment variable is not set.");
            System.err.println("Set it before running (see README and .env.example for details).");
            System.exit(1);
        }

        JDABuilder.createDefault(token)
            .addEventListeners(new SimpleBot())
            .build();
    }

    private static class TrackScheduler {
        private final AudioPlayer player;
        private final BlockingQueue<AudioTrack> queue;

        public TrackScheduler(AudioPlayer player) {
            this.player = player;
            this.queue = new LinkedBlockingQueue<>();
        }

        public void queue(AudioTrack track) {
            if (!player.startTrack(track, true)) {
                queue.offer(track);
            }
        }

        public void nextTrack() {
            player.startTrack(queue.poll(), false);
        }

        public AudioTrack getCurrentTrack() {
            return player.getPlayingTrack();
        }
    }
}
