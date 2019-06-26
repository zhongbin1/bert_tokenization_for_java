package bert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FullTokenizer {

    private Map<String, Integer> vocab;

    private BasicTokenizer basicTokenizer;
    private WordpieceTokenizer wordpieceTokenizer;

    public FullTokenizer(Map<String, Integer> vocab){
        this.vocab = vocab;
        this.basicTokenizer = new BasicTokenizer();
        this.wordpieceTokenizer = new WordpieceTokenizer(vocab);
    }


    public List<String> tokenize(String text){
        List<String> splitTopkens = new ArrayList<String>();

        for(String token : basicTokenizer.tokenize(text)){
            for(String subToken : wordpieceTokenizer.tokenize(token)){
                splitTopkens.add(subToken);
            }
        }

        return splitTopkens;
    }

    public List<Integer>  convertTokensToIds(List<String> tokens){
        List<Integer> outputIds = new ArrayList<Integer>();
        for(String token : tokens){
            outputIds.add(this.vocab.get(token));
        }
        return outputIds;
    }

}
