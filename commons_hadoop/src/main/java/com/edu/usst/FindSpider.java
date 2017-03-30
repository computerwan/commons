package com.edu.usst; /**
 * reference by https://www.polarxiong.com/archives/Hadoop-Intellij%E7%BB%93%E5%90%88Maven%E6%9C%AC%E5%9C%B0%E8%BF%90%E8%A1%8C%E5%92%8C%E8%B0%83%E8%AF%95MapReduce%E7%A8%8B%E5%BA%8F-%E6%97%A0%E9%9C%80%E6%90%AD%E8%BD%BDHadoop%E5%92%8CHDFS%E7%8E%AF%E5%A2%83.html
 * Created by pengcheng.wan on 2017/2/13.
 */


import java.io.IOException;
import java.util.*;

import com.edu.usst.common.writable.LongPairWritable;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 规则：以ip为key，time为value，并对time进行排序
 * 认定为爬虫：
 * 1. 总数超过一定数量(暂定10000)
 * 2. 规定访问数量时间小于一定阈值(暂定10秒超过10次的次数超过2836次)
 * 3. 是否以固定频率抓取（判断相同间隔的时间是否超过一半）
 * 4. 夜间访问异常（夜间访问次数超过589次）
 * 5. 每秒钟访问频率超过一定数量很多次（肯定是...）
 */
public class FindSpider {
    private static final Logger log = LoggerFactory.getLogger(FindSpider.class);

    /**
     * 输入：value：log
     * 输出：key：ip ，value： timestamp
     */
    public static class TokenizerMapper extends Mapper<LongWritable, Text, LongPairWritable, LongWritable> {

        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            String[] split = line.split("\u001A");//ServerLogParser.LOG_SPLIT

            String ip = split[7];
            String timestamp = split[3];//毫秒时间戳
            if (split[2].equals("17")) {
                timestamp = split[4];
                ip = split[9];
            }
            if (ip.length() < 4 || ip.length() > 15 || ip.equals("unknown")) {
                return;
            }
            long ipNum = 0;
            try {
                ipNum = convertIpToLong(ip);
            } catch (Exception e) {
                log.error("转换失败...");
            }
            long time = Long.parseLong(timestamp);
            context.write(new LongPairWritable(ipNum, time), new LongWritable(time));
        }
    }

    /**
     * 输入：key：com.edu.usst.common.writable.LongPairWritable（ip,timestamp）,value:sorted timestamp
     * 输出：key:ip,value : 次数
     */
    public static class IntSumReducer extends Reducer<LongPairWritable, LongWritable, Text, IntWritable> {
        private static int MAX_NUM_TODAY = 10000;
        private static int MAX_NUM_NIGHT = 589;
        private static int FIX_QUEUE_TIME_DIFF = 10000;//时间为秒
        private static int QUEUE_SIZE = 30;
        private static int MAX_FREQUENCY_TIMES = 2836;
        private static int MAX_ZERO_TIMES = 1000;
        //debug
        int count = 0;

        public void reduce(LongPairWritable key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
            long numToday = 0;
            long numNight = 0;
            long zeroTimes = 0;
            Queue recentNRecordQueue = new LinkedList();
            int frequencyTimes = 0;
            List intervalTimeList = new ArrayList();
            LongWritable lastValue = new LongWritable();
            IntWritable isSpider = new IntWritable();


            /*for (LongWritable val : values) {
                context.write(new Text(convertIpToString(key.getFirst())), val);
            }*/
            for (LongWritable val : values) {
                recentNRecordQueue.offer(val);
                if (recentNRecordQueue.size() == QUEUE_SIZE) {
                    LongWritable lastNVal = (LongWritable) recentNRecordQueue.poll();
                    if ((val.get() - lastNVal.get()) < FIX_QUEUE_TIME_DIFF) {
                        frequencyTimes++;
                    }
                }
                if (lastValue.get() != 0) {
                    intervalTimeList.add((val.get() - lastValue.get()) / 1000);
                    if ((val.get() - lastValue.get()) / 1000 <= 1) {
                        zeroTimes++;
                    }
                }
                lastValue.set(val.get());
                numToday += 1;
                if (judgeNight(val.get(), 2, 5)) {
                    numNight++;
                }
                count++;
            }
            boolean isHalf = judgeHalfMajor(intervalTimeList);
            if (numToday > MAX_NUM_TODAY || frequencyTimes > MAX_FREQUENCY_TIMES || numNight > MAX_NUM_NIGHT) {
                isSpider.set(1);
            }
            if (intervalTimeList.size() > 100 && (isHalf)) {
                isSpider.set(1);
            }
            // log.info("ip为{}的搜索总数是{},夜间搜索总次数{},在{}次中搜索时间小于{}的次数为{},次数是否超过一半{},是否判定为爬虫{}", key.getFirst(), numToday, numNight, QUEUE_SIZE, FIX_QUEUE_TIME_DIFF, frequencyTimes, isHalf, isSpider.get());

            //无实际功能，仅仅是查看进度的作用
            if (count % 1000 == 0) {
                log.info("当前插入到{}", count);
            }

/*            //将参数转为json保存到文本里面
            com.edu.usst.domain.LogIndex logIndex = new com.edu.usst.domain.LogIndex();
            logIndex.setIp(key.getFirst());
            logIndex.setNumToday(numToday);
            logIndex.setNumNight(numNight);
            logIndex.setFrequencyTimes(frequencyTimes);
            logIndex.setHalf(isHalf);
            logIndex.setIsSpider(isSpider.get());
            logIndex.setZeroTimes(zeroTimes);
            String res = JSON.toJSONString(logIndex);

            FileUtils.writeStringToFile(new File("C:\\Users\\pengcheng.wan\\Desktop\\res.txt"), res + "\r\n", true);
            FileUtils.writeStringToFile(new File("C:\\Users\\pengcheng.wan\\Desktop\\list.txt"), intervalTimeList.toString() + "------" + isHalf + "\r\n", true);
*/
            if (isSpider.get() == 1) {
                context.write(new Text(convertIpToString(key.getFirst())), isSpider);
            }
            isSpider.set(0);
            isHalf = false;
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "find spider");
        job.setJarByClass(FindSpider.class);
        job.setMapperClass(TokenizerMapper.class);
        job.setReducerClass(IntSumReducer.class);
        job.setPartitionerClass(FirstPartitioner.class);//设置分区函数
        job.setGroupingComparatorClass(GroupingComparator.class);//设置分组函数
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        job.setMapOutputKeyClass(LongPairWritable.class);
        job.setMapOutputValueClass(LongWritable.class);
        FileInputFormat.addInputPath(job, new Path("E:\\51job_log\\ys\\-r-00000"));
        FileOutputFormat.setOutputPath(job, new Path("E:\\51job_log\\ys\\res"));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

    /**
     * 将String类型ip转化为补位好的long类型
     */
    public static long convertIpToLong(String ip) {
        StringBuilder sb = new StringBuilder();
        String[] ipEle = ip.trim().split("\\.");
        for (int i = 0; i < ipEle.length; i++) {
            String value = StringUtils.leftPad(ipEle[i], 3, "0");
            sb.append(value);
        }
        return Long.parseLong(sb.toString());
    }

    /**
     * 将补位好的long类型转换为String类型的ip
     */
    public static String convertIpToString(long ip) {
        StringBuilder sb = new StringBuilder();
        String values = StringUtils.leftPad(Long.toString(ip), 12, "0");
        for (int i = 0; i < 4; i++) {
            String value = values.substring(3 * i, 3 * i + 3);
            if (value.startsWith("00")) {
                value = value.substring(2);
            } else if (value.startsWith("0")) {
                value = value.substring(1);
            }
            sb.append(value);
            if (i != 3) {
                sb.append(".");
            }
        }
        return sb.toString();
    }

    /**
     * 判断其中是否有间隔时间超过一半次数
     */
    public static boolean judgeHalfMajor(List<Long> arr) {
        long cand = 0;
        int times = 0;
        for (int i = 0; i < arr.size(); i++) {
            if (times == 0) {
                cand = arr.get(i);
                times = 1;
            } else if (arr.get(i) == cand) {
                times++;
            } else {
                times--;
            }
        }
        times = 0;
        for (int i = 0; i < arr.size(); i++) {
            if (arr.get(i) == cand) {
                times++;
            }
        }
        if (times > arr.size() / 2) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否在夜间的时间段
     */
    public static boolean judgeNight(long timestamp, int startTime, int endTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        if (cal.getTime().getHours() > startTime && cal.getTime().getHours() < endTime) {
            return true;
        }
        return false;
    }

    /**
     * 分区函数
     */
    public static class FirstPartitioner extends Partitioner<LongPairWritable, NullWritable> {

        @Override
        public int getPartition(LongPairWritable key, NullWritable value, int numPartitions) {
            return (int) (Math.abs(key.getFirst() * 127) % numPartitions);
        }
    }

    /**
     * 分组函数
     */
    public static class GroupingComparator extends WritableComparator {
        protected GroupingComparator() {
            super(LongPairWritable.class, true);
        }

        @Override
        public int compare(WritableComparable w1, WritableComparable w2) {
            LongPairWritable ip1 = (LongPairWritable) w1;
            LongPairWritable ip2 = (LongPairWritable) w2;
            long l = ip1.getFirst();
            long r = ip2.getFirst();
            return l == r ? 0 : (l < r ? -1 : 1);
        }
    }

}