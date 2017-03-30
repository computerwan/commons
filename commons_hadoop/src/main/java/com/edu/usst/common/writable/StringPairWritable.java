package com.edu.usst.common.writable;

import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by pengcheng.wan on 2017/3/6.
 */
public class StringPairWritable implements WritableComparable<StringPairWritable> {
    String first;
    String second;

    public StringPairWritable(String first, String second) {
        this.first = first;
        this.second = second;
    }

    public StringPairWritable() {
    }

    public String getFirst() {
        return first;
    }

    public void set(String first, String second) {
        this.first = first;
        this.second = second;
    }

    public String getSecond() {
        return second;
    }

    @Override
    public int compareTo(StringPairWritable o) {
        int cmp = first.compareTo(o.first);
        if (cmp != 0) {
            return cmp;
        }
        return second.compareTo(o.second);
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(first);
        out.writeUTF(second);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        first = in.readUTF();
        second = in.readUTF();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StringPairWritable stringPair = (StringPairWritable) o;

        if (first != stringPair.first) return false;
        return second == stringPair.second;
    }

    @Override
    public int hashCode() {
        int result = first != null ? first.hashCode() : 0;
        result = 31 * result + (second != null ? second.hashCode() : 0);
        return result;
    }


}
