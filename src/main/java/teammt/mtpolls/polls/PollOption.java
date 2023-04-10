package teammt.mtpolls.polls;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PollOption {

    private UUID pollOptionId;
    private UUID pollId;
    private String option;
    private int votes;
    private List<UUID> playersVoted;

    public PollOption(String option, UUID pollId) {
        this.pollOptionId = UUID.randomUUID();
        this.pollId = pollId;
        this.option = option;
        this.votes = 0;
        this.playersVoted = new ArrayList<>();
    }

    public void incrementVotes() {
        this.votes++;
    }

    public void decrementVotes() {
        this.votes--;
    }
}
