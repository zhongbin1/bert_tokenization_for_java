package bert;

import java.io.*;
import java.util.*;

public class Preprocess {

    private Map<String, Integer> vocab;
    private FullTokenizer fullTokenizer;
    private final static int maxSeqLength = 64;  // 放到配置文件中去读

    public Preprocess(){
        this.vocab = load("/Users/zb/code/pretrain_models/chinese_L-12_H-768_A-12/vocab.txt");
        this.fullTokenizer = new FullTokenizer(this.vocab);
    }

    private Map<String, Integer> load(String filePath){
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

    public static void main(String[] args){

        Preprocess pp = new Preprocess();
        String query = "您也可以留一下邮箱的，QQ邮箱也是可以的。？";
        String doc = "求七公主漫画1-52全集给我发一下谢谢";

        List<String> docs = new ArrayList<String>();
        docs.add(doc);
        docs.add(doc);

        List<Example> examples = pp.preProcess(query, docs);
        System.out.println(examples);

        System.out.println("input_ids " + examples.get(0).getInputIds());
        System.out.println("input_mask " + examples.get(0).getInputMask());
        System.out.println("segment_ids " + examples.get(0).getSegmentIds());

    }

    // 全角转半角
    private String full2HalfChange(String QJstr){

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
        }catch (Exception e){
            e.printStackTrace();
            return QJstr;
        }

        return outStrBuf.toString();
    }

    public List<Example> preProcess(String query, List<String> docs){
        String cleanQuery = full2HalfChange(query).toLowerCase();  //全角转半角+大写转小写
        //System.out.println(cleanQuery);
        List<String> tokensQuery = this.fullTokenizer.tokenize(cleanQuery);
        //System.out.println(tokensQuery);

        List<Example> examples = new ArrayList<Example>();
        for(String doc : docs){
            String cleanDoc = full2HalfChange(doc).toLowerCase();
            //System.out.println(cleanDoc);
            List<String> tokensDoc = this.fullTokenizer.tokenize(cleanDoc);
            //System.out.println(tokensDoc);
            Example e = getExample(tokensQuery, tokensDoc);
            examples.add(e);
        }
        return examples;
    }

    private Example getExample(List<String> tokensQuery, List<String> tokensDoc){
        while(true){
            int totalLength = tokensQuery.size() + tokensDoc.size();
            if(totalLength <= maxSeqLength - 3){
                break;
            }
            if(tokensQuery.size() > tokensDoc.size()){
                tokensQuery.remove(tokensQuery.size() - 1);
            }else{
                tokensDoc.remove(tokensDoc.size() - 1);
            }
        }

        List<String> tokens = new ArrayList<String>();
        List<Integer> segmentIds = new ArrayList<Integer>();
        tokens.add("[CLS]");
        segmentIds.add(0);
        for(String token : tokensQuery){
            tokens.add(token);
            segmentIds.add(0);
        }
        tokens.add("[SEP]");
        segmentIds.add(0);

        for(String token : tokensDoc){
            tokens.add(token);
            segmentIds.add(1);
        }
        tokens.add("[SEP]");
        segmentIds.add(1);

        List<Integer> inputIds = this.fullTokenizer.convertTokensToIds(tokens);
        List<Integer> inputMask = new ArrayList<Integer>();

        for(int i=0; i<inputIds.size(); i++){
            inputMask.add(1);
        }

        while(inputIds.size() < maxSeqLength){
            inputIds.add(0);
            inputMask.add(0);
            segmentIds.add(0);
        }

        return new Example(inputIds, inputMask, segmentIds);

    }

}

class Example{
    private List<Integer> inputIds;
    private List<Integer> inputMask;
    private List<Integer> segmentIds;

    public Example(List<Integer> inputIds, List<Integer> inputMask, List<Integer> segmentIds){
        this.inputIds = inputIds;
        this.inputMask = inputMask;
        this.segmentIds = segmentIds;

    }

    public List<Integer> getInputIds() {
        return inputIds;
    }

    public List<Integer> getInputMask() {
        return inputMask;
    }

    public void setInputMask(List<Integer> inputMask) {
        this.inputMask = inputMask;
    }

    public List<Integer> getSegmentIds() {
        return segmentIds;
    }

    public void setSegmentIds(List<Integer> segmentIds) {
        this.segmentIds = segmentIds;
    }

    public void setInputIds(List<Integer> inputIds) {
        this.inputIds = inputIds;
    }
}