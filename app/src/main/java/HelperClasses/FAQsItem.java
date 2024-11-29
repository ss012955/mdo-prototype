package HelperClasses;

public class FAQsItem {
    String Questions, Answer;

    public  FAQsItem(String Questions, String Answer){
        this.Questions = Questions;
        this.Answer = Answer;
    }

    public String getQuestions(){
        return Questions;
    }

    public String getAnswer() {
        return Answer;
    }

    public void setAnswer(String answer) {
        Answer = answer;
    }

    public void setQuestions(String questions) {
        Questions = questions;
    }
}
