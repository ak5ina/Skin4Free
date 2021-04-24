package com.scrippy3.skin4free;

public class Question {

    private String type;
    private String question;
    private String answer;

    public Question(String type, String question, String answer){
        this.type = type;
        this.question = question;
        this.answer = answer;
    }



    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
