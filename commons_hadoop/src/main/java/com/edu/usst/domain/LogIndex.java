package com.edu.usst.domain;

/**
 * Created by pengcheng.wan on 2017/3/7.
 */
public class LogIndex {
    private long ip;
    private long numToday;//总搜索次数
    private long numNight;//夜间搜索次数
    private long zeroTimes;
    private int frequencyTimes;//范围内超过的次数
    private boolean isHalf;//是否超过一半
    private int isSpider;

    public long getIp() {
        return ip;
    }

    public void setIp(long ip) {
        this.ip = ip;
    }

    public long getNumToday() {
        return numToday;
    }

    public void setNumToday(long numToday) {
        this.numToday = numToday;
    }

    public long getNumNight() {
        return numNight;
    }

    public void setNumNight(long numNight) {
        this.numNight = numNight;
    }

    public int getFrequencyTimes() {
        return frequencyTimes;
    }

    public void setFrequencyTimes(int frequencyTimes) {
        this.frequencyTimes = frequencyTimes;
    }

    public boolean isHalf() {
        return isHalf;
    }

    public void setHalf(boolean half) {
        isHalf = half;
    }

    public int getIsSpider() {
        return isSpider;
    }

    public void setIsSpider(int isSpider) {
        this.isSpider = isSpider;
    }

    public long getZeroTimes() {
        return zeroTimes;
    }

    public void setZeroTimes(long zeroTimes) {
        this.zeroTimes = zeroTimes;
    }

    @Override
    public String toString() {
        return "com.edu.usst.domain.LogIndex{" +
                "ip=" + ip +
                ", numToday=" + numToday +
                ", numNight=" + numNight +
                ", zeroTimes=" + zeroTimes +
                ", frequencyTimes=" + frequencyTimes +
                ", isHalf=" + isHalf +
                ", isSpider=" + isSpider +
                '}';
    }
}
