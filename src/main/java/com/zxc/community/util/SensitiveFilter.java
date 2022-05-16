package com.zxc.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
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

//    @PostConstruct
//    public void init() {
//        try (
//                InputStream is = this.getClass().getClassLoader().getResourceAsStream("/sensitive-word.txt");
//                BufferedReader reader = new BufferedReader(new InputStreamReader(is));)
//        {
//            String keyWord;
//            while ((keyWord = reader.readLine()) != null) {
//                addKeyWord(keyWord);
//            }
//        } catch (IOException e) {
//            logger.error("加载敏感词失败" + e.getMessage());
//        }
//    }
    @PostConstruct
    public void init() {
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-word");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ) {
            String keyword;
            while ((keyword = reader.readLine()) != null) {
                // 添加到前缀树
                this.addKeyWord(keyword);
            }
        } catch (IOException e) {
            logger.error("加载敏感词文件失败: " + e.getMessage());
        }
    }

    //将敏感词加到前缀树中
    private void addKeyWord(String keyword) {
        TrieNode temp = rootNode;
        for (int i = 0; i < keyword.length(); i++) {
            Character c = keyword.charAt(i);
            TrieNode subNode = temp.getSubNode(c);
            if(subNode == null) {
                subNode = new TrieNode();
                temp.addSubNode(c, subNode);
            }

            temp = subNode;
            if (i == keyword.length() - 1) {
                temp.setEnd(true);
            }
        }
    }
    
    public String filter(String text) {
        if (StringUtils.isBlank(text)){
            return null;
        }

        TrieNode temp = rootNode;
        int begin = 0;
        int position = 0;
        StringBuilder sb = new StringBuilder();
        while(position < text.length()) {
            char c = text.charAt(position);
            if (isSymbol(c)) {
                if (temp == rootNode){
                    begin++;
                    sb.append(c);
                }
                position++;
                continue;
            }

            // next
            temp = temp.getSubNode(c);
            if (temp == null) {
                sb.append(c);
                position = ++begin;
                temp = rootNode;
            }
            else if (temp.isEnd()) {
                begin = ++position;
                sb.append(REPLACEMENT);
                temp = rootNode;
            }
            else {
                ++position;
            }
        }

        return sb.toString();
    }

    //// 判断是否为符号
    private boolean isSymbol(char c) {
        // 0x2E80~0x9FFF 是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
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
