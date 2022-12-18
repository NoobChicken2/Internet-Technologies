package Server.ServerResponse.Survey;

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

    public void addAnswers(Answer answer) {
        this.answers.add(answer);
    }
}
