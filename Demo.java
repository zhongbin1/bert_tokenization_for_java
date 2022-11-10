package bert;

import java.io.*;
import java.util.*;

public class Demo {

    public static void main(String[] args) {
        String vocab_path = "F:\\0_codes\\java\\vocab_bert-base-chinese.txt";
        boolean has_chinese = true;
        boolean do_lower = false;
        int maxSeqLength = 64;

        FullTokenizer fullTokenizer = new FullTokenizer(vocab_path, has_chinese, do_lower);

        String query = "这是BERT tokenizer。 I'm working hard for programing GPU CPU";
        // List<String> tokensQuery = fullTokenizer.tokenize(query);
        // TokenIds e = fullTokenizer.getTokenIdsSingle(tokensQuery, maxSeqLength);

        TokenIds e = fullTokenizer.TokenlizeSingle(query, maxSeqLength);
        System.out.println("print token ids of query: " + query);
        System.out.println("input_ids " + e.getInputIds());
        System.out.println("input_mask " + e.getInputMask());
        System.out.println("segment_ids " + e.getSegmentIds());

        // demo of pairs
        List<String> docs = new ArrayList<String>();
        docs.add("求七公主漫画1-52全集给我发一下谢谢");
        docs.add("您也可以留一下邮箱的，QQ邮箱也是可以的。？");

        List<TokenIds> tokenIds = fullTokenizer.TokenlizeMultiPairs(query, docs, maxSeqLength);
        System.out.println("print token ids of pairs");
        System.out.println("input_ids " + tokenIds.get(0).getInputIds());
        System.out.println("input_mask " + tokenIds.get(0).getInputMask());
        System.out.println("segment_ids " + tokenIds.get(0).getSegmentIds());

    }

}