package com.zxc.community.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    private static final String REPLACEMENT = "***";

    //ROOT
    private TrieNode rootNode = new TrieNode();

    @PostConstruct
    public void init() {
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-word.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));)
        {
            String keyWord;
            while ((keyWord = reader.readLine()) != null) {
                addKeyWord(keyWord);
            }
        } catch (IOException e) {
            logger.error("加载敏感词失败" + e.getMessage());
        }
    }

    //将敏感词加到前缀树中
    private void addKeyWord(String keyword) {
        TrieNode temp = rootNode;
        for (int i = 0; i < keyword.length(); i++) {
            Character c = keyword.charAt(i);
            TrieNode subNode = temp.getSubNode(c);
            if(subNode == null) {
                temp.addSubNode(c, new TrieNode());
            }

            temp = subNode;
            if (i == keyword.length() - 1) {
                temp.setEnd(true);
            }
        }
    }

    

    //前缀树
    private class TrieNode {
        //结束标记
        private boolean isEnd = false;
        //子节点
        private Map<Character, TrieNode> subNode = new HashMap<>();

        public boolean isEnd() {
            return isEnd;
        }

        public void setEnd(boolean end) {
            isEnd = end;
        }

        public void addSubNode(Character c, TrieNode node) {
            subNode.put(c, node);
        }

        public TrieNode getSubNode(Character c) {
            return subNode.get(c);
        }
    }
}
