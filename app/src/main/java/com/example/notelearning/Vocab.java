package com.example.notelearning;

public class Vocab {
    private String word;
    private String definition;

    public Vocab(String word, String definition){
        this.word = word;
        this.definition = definition;
    }

    public String getWord() {return word;}

    public String getDefinition() {return definition;}

    public void setWord(String input_word) {word = input_word;}
    public void setDefinition(String input_definition) {definition = input_definition;}


}
