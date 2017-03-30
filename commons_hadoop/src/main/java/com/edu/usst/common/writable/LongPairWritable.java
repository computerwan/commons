package com.edu.usst.common.writable;

import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by pengcheng.wan on 2017/3/6.
 */
public class LongPairWritable implements WritableComparable<LongPairWritable> {
    long first;
    long second;

    public LongPairWritable(long first, long second) {
        this.first = first;
        this.second = second;
    }

    public LongPairWritable() {
    }

    public long getFirst() {
        return first;
    }

    public void set(long first, long second) {
        this.first = first;
        this.second = second;
    }

    public long getSecond() {
        return second;
    }


    @Override
    public int compareTo(LongPairWritable o) {
        if (first != o.first) {
            return first < o.first ? -1 : 1;
        } else if (second != o.second) {
            return second < o.second ? -1 : 1;
        } else {
            return 0;
        }
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeLong(first);
        out.writeLong(second);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        first = in.readLong();
        second = in.readLong();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LongPairWritable longPairWritable = (LongPairWritable) o;

        if (first != longPairWritable.first) return false;
        return second == longPairWritable.second;
    }

    @Override
    public int hashCode() {
        int result = (int) (first ^ (first >>> 32));
        result = 31 * result + (int) (second ^ (second >>> 32));
        return result;
    }
}
