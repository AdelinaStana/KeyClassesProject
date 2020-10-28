package ranking.strategy;

import ranking.RankingEntry;

import java.util.List;

public abstract class RankingStrategy {

    private String description;

    /**
     * Sorts the given list in-place.
     */
    public abstract void rank(List<RankingEntry> ranking);

    protected void setDescription(String description){
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
