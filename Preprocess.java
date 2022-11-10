package bert;

import java.io.*;
import java.util.*;

public class Preprocess {

    public Preprocess() {
    }

    public Map<String, Integer> load(String filePath) {
        Map<String, Integer> map = new HashMap<String, Integer>();

        /* 读取数据 */
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filePath)),
                                                   "UTF-8"));
            int index = 0;
            String token = null;
            while ((token = br.readLine()) != null) {
                map.put(token, index);
                index += 1;
            }
            br.close();
        } catch (Exception e) {
            System.err.println("read errors :" + e);
        }
        return map;
    }

    // 全角转半角
    public String full2HalfChange(String QJstr) {

        StringBuffer outStrBuf = new StringBuffer();

        String Tstr = "";

        byte[] b = null;
        try {
            for (int i = 0; i < QJstr.length(); i++) {

                Tstr = QJstr.substring(i, i + 1);

                if (Tstr.equals("　")) {
                    outStrBuf.append(" ");
                    continue;
                }

                b = Tstr.getBytes("unicode");

                // 得到 unicode 字节数据

                if (b[2] == -1) {

                    // 表示全角？

                    b[3] = (byte) (b[3] + 32);

                    b[2] = 0;

                    outStrBuf.append(new String(b, "unicode"));

                } else {
                    outStrBuf.append(Tstr);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return QJstr;
        }
        return outStrBuf.toString();
    }
}
