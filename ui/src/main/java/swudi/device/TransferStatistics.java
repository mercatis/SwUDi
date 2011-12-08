/*
 * Copyright 2011 mercatis technologies AG
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package swudi.device;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created: 03.12.11   by: Armin Haaf
 * <p/>
 *
 *
 * @author Armin Haaf
 */
public class TransferStatistics {

    private StatisticsEntry[] sentStatistics;
    private StatisticsEntry[] receivedStatistics;

    private int maxAgeMillis;
    private int aggregationMillis;

    private StatisticsEntry overallSent = new StatisticsEntry();
    private StatisticsEntry overallReceived = new StatisticsEntry();

    public TransferStatistics() {
        this(15 * 60000, 1000);
    }

    public TransferStatistics(final int pMaxAgeMillis, final int pAggregationMillis) {
        maxAgeMillis = pMaxAgeMillis;
        aggregationMillis = pAggregationMillis;

        adaptStatisticsStructures();
    }

    /**
     * this discards all statistics and creates new structures
     */
    private void adaptStatisticsStructures() {
        sentStatistics = new StatisticsEntry[maxAgeMillis / aggregationMillis];
        receivedStatistics = new StatisticsEntry[maxAgeMillis / aggregationMillis];

        for (int i = 0; i < sentStatistics.length; i++) {
            sentStatistics[i] = new StatisticsEntry(0, 0);
        }

        for (int i = 0; i < receivedStatistics.length; i++) {
            receivedStatistics[i] = new StatisticsEntry(0, 0);
        }
    }

    public int getMaxAgeMillis() {
        return maxAgeMillis;
    }

    /**
     * this discards all statistics and creates new structures
     */
    public void setMaxAgeMillis(final int pMaxAgeMillis) {
        if (pMaxAgeMillis != maxAgeMillis) {
            maxAgeMillis = pMaxAgeMillis;
            adaptStatisticsStructures();
        }
    }

    /**
     * this discards all statistics and creates new structures
     */
    public int getAggregationMillis() {
        return aggregationMillis;
    }

    public void setAggregationMillis(final int pAggregationMillis) {
        if (aggregationMillis != pAggregationMillis) {
            aggregationMillis = pAggregationMillis;
            adaptStatisticsStructures();
        }
    }

    public void addBytesSent(final int pBytesSent) {
        overallSent.add(pBytesSent);
        addEntry(pBytesSent, sentStatistics);
    }

    public void addBytesReceived(final int pBytesReceived) {
        overallReceived.add(pBytesReceived);
        addEntry(pBytesReceived, receivedStatistics);
    }

    private void addEntry(final int pByteCount, final StatisticsEntry[] pStatistics) {
        final long tCurrentTime = System.currentTimeMillis();
        final int tIndex = (int) ((tCurrentTime / aggregationMillis) % pStatistics.length);
        // check if the current data is outtimed
        if (pStatistics[tIndex].millis + aggregationMillis < tCurrentTime) {
            pStatistics[tIndex].reset(tCurrentTime);
        }
        pStatistics[tIndex].add(pByteCount);
    }

    public long getSentBytes(final long pPeriodInMillis) {
        return getByteCount(pPeriodInMillis, sentStatistics);
    }

    public long getSentBytesPerSecond(final long pPeriodInMillis) {
        return (getByteCount(pPeriodInMillis, sentStatistics) * 1000) / pPeriodInMillis;
    }

    public long getSentsPerSecond(final long pPeriodInMillis) {
        return (getAddCount(pPeriodInMillis, sentStatistics) * 1000) / pPeriodInMillis;
    }

    public long getReceivedBytes(final long pPeriodInMillis) {
        return getByteCount(pPeriodInMillis, receivedStatistics);
    }

    public long getReceivedBytesPerSeconds(final long pPeriodInMillis) {
        return (getByteCount(pPeriodInMillis, receivedStatistics) * 1000) / pPeriodInMillis;
    }

    public long getReceivesPerSeconds(final long pPeriodInMillis) {
        return (getAddCount(pPeriodInMillis, receivedStatistics) * 1000) / pPeriodInMillis;
    }

    public long getOverallSent() {
        return overallSent.byteCount;
    }

    public long getOverallReceived() {
        return overallReceived.byteCount;
    }

    private long getByteCount(final long pPeriodInMillis, final StatisticsEntry[] pStatistics) {
        long tResult = 0;
        final long tCutOfTimeEnd = System.currentTimeMillis() - aggregationMillis;
        final long tCutOfTimeStart = tCutOfTimeEnd - pPeriodInMillis;
        for (int i = pStatistics.length - 1; i >= 0; i--) {
            if (pStatistics[i].millis > tCutOfTimeStart && pStatistics[i].millis < tCutOfTimeEnd) {
                tResult += pStatistics[i].byteCount;
            }
        }

        return tResult;
    }

    private long getAddCount(final long pPeriodInMillis, final StatisticsEntry[] pStatistics) {
        long tResult = 0;
        final long tCutOfTimeEnd = System.currentTimeMillis() - aggregationMillis;
        final long tCutOfTimeStart = tCutOfTimeEnd - pPeriodInMillis;
        for (int i = pStatistics.length - 1; i >= 0; i--) {
            if (pStatistics[i].millis > tCutOfTimeStart && pStatistics[i].millis < tCutOfTimeEnd) {
                tResult += pStatistics[i].addCount;
            }
        }

        return tResult;
    }

    public List<StatisticsEntry> getSentStatistics() {
        return Collections.unmodifiableList(Arrays.asList(sentStatistics));
    }

    public List<StatisticsEntry> getReceivedStatistics() {
        return Collections.unmodifiableList(Arrays.asList(receivedStatistics));
    }

    // i'm not sure if we need synchronized access here
    public static class StatisticsEntry {
        private int addCount;
        private int byteCount;
        private long millis;

        StatisticsEntry() {
        }

        public StatisticsEntry(final int pByteCount, final long pMillis) {
            byteCount = pByteCount;
            millis = pMillis;
        }


        public void add(final int pByteCount) {
            addCount++;
            byteCount += pByteCount;
        }

        public void reset(final long pMillis) {
            millis = pMillis;
            byteCount = 0;
            addCount = 0;
        }

        public int getAddCount() {
            return addCount;
        }

        public int getByteCount() {
            return byteCount;
        }

        public long getMillis() {
            return millis;
        }

        @Override
        public String toString() {
            return new Date(millis) + " " + byteCount + "(" + addCount + ")";
        }
    }


}
