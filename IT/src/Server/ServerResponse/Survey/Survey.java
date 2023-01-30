package Server.ServerResponse.Survey;

import java.util.ArrayList;

public class Survey {

    private ArrayList<Question> questions;
    private ArrayList<String>participants;

    public Survey() {
        this.questions = new ArrayList<>();
        this.participants = new ArrayList<>();
    }

    public ArrayList<String> getParticipants() {
        return participants;
    }
    public ArrayList<Question> getQuestions() {
        return questions;
    }
    public Question getQuestion(int index) {
        return questions.get(index);
    }
    public void addQuestion(Question question) {
        this.questions.add(question);
    }
    public void setParticipants(ArrayList<String> participants) {
        this.participants = participants;
    }
}
