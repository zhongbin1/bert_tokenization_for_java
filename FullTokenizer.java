package bert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FullTokenizer {
    private Map<String, Integer> vocab;
    private BasicTokenizer basicTokenizer;
    private WordpieceTokenizer wordpieceTokenizer;
    private Preprocess preprocesser;
    boolean has_chinese;
    boolean do_lower;

    public FullTokenizer(String filePath, boolean has_chinese, boolean do_lower) {
        this.has_chinese = has_chinese;
        this.do_lower = do_lower;
        this.preprocesser = new Preprocess();
        this.vocab = this.preprocesser.load(filePath);

        this.basicTokenizer = new BasicTokenizer();
        this.wordpieceTokenizer = new WordpieceTokenizer(vocab);
    }

    public List<String> tokenize(String text) {
        if (has_chinese) {
            text = preprocesser.full2HalfChange(text);
        }
        if (do_lower) {
            text = text.toLowerCase();
        }
        this.do_lower = do_lower;
        List<String> splitTopkens = new ArrayList<String>();

        for (String token : basicTokenizer.tokenize(text)) {
            for (String subToken : wordpieceTokenizer.tokenize(token)) {
                splitTopkens.add(subToken);
            }
        }
        return splitTopkens;
    }

    public List<Integer> convertTokensToIds(List<String> tokens) {
        List<Integer> outputIds = new ArrayList<Integer>();
        for (String token : tokens) {
            outputIds.add(this.vocab.get(token));
        }
        return outputIds;
    }

    // 单句映射id
    public TokenIds getTokenIdsSingle(List<String> tokensQuery, int maxSeqLength) {
        while (true) {
            int totalLength = tokensQuery.size();
            if (totalLength <= maxSeqLength - 2) {
                break;
            } else {
                tokensQuery.remove(tokensQuery.size() - 1);
            }
        }

        List<String> tokens = new ArrayList<String>();
        List<Integer> segmentIds = new ArrayList<Integer>();
        tokens.add("[CLS]");
        segmentIds.add(0);
        for (String token : tokensQuery) {
            tokens.add(token);
            segmentIds.add(0);
        }
        tokens.add("[SEP]");
        segmentIds.add(0);

        List<Integer> inputIds = convertTokensToIds(tokens);
        List<Integer> inputMask = new ArrayList<Integer>();

        for (int i = 0; i < inputIds.size(); i++) {
            inputMask.add(1);
        }

        while (inputIds.size() < maxSeqLength) {
            inputIds.add(0);
            inputMask.add(0);
            segmentIds.add(0);
        }

        return new TokenIds(inputIds, inputMask, segmentIds);
    }

    // 句对映射id
    public TokenIds getTokenIdsPair(List<String> tokensQuery, List<String> tokensDoc, int maxSeqLength) {
        while (true) {
            int totalLength = tokensQuery.size() + tokensDoc.size();
            if (totalLength <= maxSeqLength - 3) {
                break;
            }
            if (tokensQuery.size() > tokensDoc.size()) {
                tokensQuery.remove(tokensQuery.size() - 1);
            } else {
                tokensDoc.remove(tokensDoc.size() - 1);
            }
        }

        List<String> tokens = new ArrayList<String>();
        List<Integer> segmentIds = new ArrayList<Integer>();
        tokens.add("[CLS]");
        segmentIds.add(0);
        for (String token : tokensQuery) {
            tokens.add(token);
            segmentIds.add(0);
        }
        tokens.add("[SEP]");
        segmentIds.add(0);

        for (String token : tokensDoc) {
            tokens.add(token);
            segmentIds.add(1);
        }
        tokens.add("[SEP]");
        segmentIds.add(1);

        List<Integer> inputIds = convertTokensToIds(tokens);
        List<Integer> inputMask = new ArrayList<Integer>();

        for (int i = 0; i < inputIds.size(); i++) {
            inputMask.add(1);
        }

        while (inputIds.size() < maxSeqLength) {
            inputIds.add(0);
            inputMask.add(0);
            segmentIds.add(0);
        }
        return new TokenIds(inputIds, inputMask, segmentIds);
    }

    public TokenIds TokenlizeSingle(String query, int maxSeqLength) {
        List<String> tokensQuery = tokenize(query);
        return  getTokenIdsSingle(tokensQuery, maxSeqLength);
    }

    public List<TokenIds> TokenlizeMultiPairs(String query, List<String> docs, int maxSeqLength) {
        List<String> tokensQuery = tokenize(query);

        List<TokenIds> tokenIds = new ArrayList<TokenIds>();
        for (String doc : docs) {
            List<String> tokensDoc = tokenize(doc);
            TokenIds e = getTokenIdsPair(tokensQuery, tokensDoc, maxSeqLength);
            tokenIds.add(e);
        }
        return tokenIds;
    }
}

class TokenIds {
    private List<Integer> inputIds;
    private List<Integer> inputMask;
    private List<Integer> segmentIds;

    public TokenIds(List<Integer> inputIds, List<Integer> inputMask, List<Integer> segmentIds) {
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