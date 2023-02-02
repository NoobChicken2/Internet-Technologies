package Server.Survey;

import java.util.ArrayList;

public class Question {
    private String question;
    private ArrayList<Answer> answers;

    public Question(String question) {
        this.question = question;
        answers=new ArrayList<>();
    }

    public String getQuestion() {
        return question;
    }

    public ArrayList<Answer> getAnswers() {
        return answers;
    }

    public Answer getOneAnswer(String answer){
        Answer result=new Answer();
        for (int i = 0; i < answers.size(); i++) {
            if (answer.equals(answers.get(i))){
                result=answers.get(i);
            }
        }
        return result;
    }

    public void addAnswers(Answer answer) {
        this.answers.add(answer);
    }
    public int getNumOfAnswers(){
        return answers.size();
    }
}
