import org.apache.hadoop.io.LongWritable;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by pengcheng.wan on 2017/2/17.
 */
public class Test {

    public static void main(String[] args) throws Exception {
/*        String timestamp="1487260800000";
        String res = timestamp.substring(4, timestamp.length() - 3);
        System.out.println(res);*/
/*        Format df = new DecimalFormat("000");
        String ip = "121.32.251.73";
        String[] split = ip.split("\\.");
//        System.out.println(split[0] + "....." + split[1] + "......" + split[2] + "..." + split[3]);
        System.out.println(df.format(Integer.parseInt(split[1])));*/

/*        String ip = "36.5.132.92";
        com.edu.usst.FindSpider findSpider = new com.edu.usst.FindSpider();
        System.out.println(findSpider.convertIpToLong(ip));
        System.out.println(findSpider.convertIpToString(findSpider.convertIpToLong(ip)));*/


/*        String tar="4\u001Asearchweb4.2017021609\u001A17\u001A2\u001A1487207103088\u001Asearch.51job.com\u001A01\u001A\u001A1487207103813690072\u001A222.79.173.243\u001A1\u001A231\u001A0\u001A1\u001A1#0#9|9|0000|0|000000|000000|00|0000|00|99|99|99|99|99|99|0|0|-1#1#200#石狮联侨彩塑企业公司\u0010\n";

        String[] split = tar.split("\u001A");
        System.out.println(split[2]+"....................."+split[4]+"............."+split[9]);*/
/*        long timestamp = 1487240430057l;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        System.out.println(cal.get(Calendar.HOUR_OF_DAY));


        boolean b = com.edu.usst.FindSpider.judgeNight(1487190030000l, 3, 5);
        System.out.println(b);*/


/*        List<Long> tar = new ArrayList<>();
        tar.add(2l);
        tar.add(3l);
        tar.add(2l);
        tar.add(5l);
        tar.add(2l);
        System.out.println(com.edu.usst.FindSpider.judgeHalfMajor(tar));*/

/*        LongWritable lastValue = new LongWritable();
        lastValue.set(12343434);
        System.out.println(lastValue.get()/1000);*/

/*        FileInputStream fstream = new FileInputStream("E:\\part-r-00000-backup");
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
        String strLine;
        while ((strLine = br.readLine()) != null) {
            String[] spl = strLine.split("\t");
            System.out.println(spl[0]);
        }
        br.close();*/


        String sub ="/data/openstack/stat";
        String res = sub.substring(0,sub.lastIndexOf("/"));
        System.out.println(res);
    }


}
