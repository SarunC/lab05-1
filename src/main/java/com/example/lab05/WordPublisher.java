package com.example.lab05;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.ArrayList;

@RestController
public class WordPublisher {
    @Autowired
    private RabbitTemplate rabbit;
    protected Word words = new Word();

    public WordPublisher() {
        words.goodWords.add("happy");
        words.goodWords.add("enjoy");
        words.goodWords.add("life");
        words.badWords.add("fuck");
        words.badWords.add("olo");
    }

    @RequestMapping(value="/addBad/{word}", method= RequestMethod.GET)
    public ArrayList<String> addBadWord(@PathVariable("word") String s) {
        words.badWords.add(s);
        return words.badWords;
    }
    @RequestMapping(value="/delBad/{word}", method= RequestMethod.GET)
    public ArrayList<String> deleteBadWord(@PathVariable("word") String s) {
        words.badWords.remove(s);
        return words.badWords;
    }
    @RequestMapping(value="/addGood/{word}", method= RequestMethod.GET)
    public ArrayList<String> addGoodWord(@PathVariable("word") String s) {
        words.goodWords.add(s);
        return words.goodWords;
    }
    @RequestMapping(value="/delGood/{word}", method= RequestMethod.GET)
    public ArrayList<String> deleteGoodWord(@PathVariable("word") String s) {
        words.goodWords.remove(s);
        return words.goodWords;
    }
    @RequestMapping(value="/proof/{sentence}", method= RequestMethod.GET)
    public void proofSentence(@PathVariable("sentence") String s) {
        boolean haveGood = false;
        boolean haveBad = false;
        for (String word : words.goodWords) {
            if (s.contains(word)) {
                haveGood = true;
            }
        }
        for (String word : words.badWords) {
            if (s.contains(word)) {
                haveBad = true;
            }
        }
        if (haveGood) {
            rabbit.convertAndSend("Direct", "good", s);
        } else if (haveBad) {
            rabbit.convertAndSend("Direct", "bad", s);
        } else if (haveGood && haveBad) {
            rabbit.convertAndSend("Fanout", "", s);
        }

    }
}
