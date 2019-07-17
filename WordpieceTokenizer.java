package bert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class WordpieceTokenizer {

    private Map<String, Integer> vocab;
    private String unkToken = "[UNK]";
    private int maxInputCharsPerWord = 200;

    public WordpieceTokenizer(Map<String, Integer> vocab){
        this.vocab = vocab;
    }

    /*
        For example:
        input = "unaffable"
        output = ["un", "##aff", "##able"]
    */
    public List<String> tokenize(String text){

        List<String> tokens = whiteSpaceTokenize(text);

        List<String> outputTokens = new ArrayList<String>();
        for(String token : tokens){
            int length = token.length();
            if(length > this.maxInputCharsPerWord){
                outputTokens.add(this.unkToken);
                continue;
            }

            boolean isBad = false;
            int start = 0;
            List<String> subTokens = new ArrayList<String>();

            while(start < length){
                int end = length;
                String curSubStr = null;
                while(start < end){
                    String subStr = token.substring(start, end);
                    if(start > 0){
                        subStr = "##" + subStr;
                    }
                    if(this.vocab.containsKey(subStr)){
                        curSubStr = subStr;
                        break;
                    }
                    end -= 1;
                }
                if(null == curSubStr){
                    isBad = true;
                    break;
                }
                subTokens.add(curSubStr);
                start = end;
            }

            if(isBad){
                outputTokens.add(this.unkToken);
            }else{
                outputTokens.addAll(subTokens);
            }

        }
        return outputTokens;
    }

    private List<String> whiteSpaceTokenize(String text){
        List<String> result = new ArrayList<String>();

        text = text.trim();
        if(null == text){
            return result;
        }
        String[] tokens = text.split(" ");
        result = Arrays.asList(tokens);

        return result;
    }

}
