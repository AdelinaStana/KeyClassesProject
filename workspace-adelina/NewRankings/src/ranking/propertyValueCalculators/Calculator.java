package ranking.propertyValueCalculators;

import ranking.RankingEntry;

import java.util.List;

public interface Calculator {

    void addResultToRanking(List<RankingEntry> ranking);

}
