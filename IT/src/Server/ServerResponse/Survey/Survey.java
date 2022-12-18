package Server.ServerResponse.Survey;

import java.util.ArrayList;

public class Survey {

    private ArrayList<Question> questions;

    public Survey() {
        this.questions = new ArrayList<>();
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public void addQuestion(Question question) {
        this.questions.add(question);
    }

}
