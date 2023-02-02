package Server.Survey;

import java.util.ArrayList;

public class Survey {

    private ArrayList<Question> questions;
    private ArrayList<String>participants;
    private int respondents;
    private int finishedSurvey;
    private boolean isFinished;

    public Survey() {
        this.questions = new ArrayList<>();
        this.participants = new ArrayList<>();
        this.respondents=0;
        this.finishedSurvey=0;
        this.isFinished=false;

    }

    public ArrayList<String> getParticipants() {
        return participants;
    }

    public int getFinishedSurvey() {
        return this.finishedSurvey;
    }
    public void setRespondents() {
        this.respondents++;
    }
    public ArrayList<Question> getQuestions() {
        return questions;
    }
    public Question getQuestion(int index) {
        return questions.get(index);
    }
    public int getQuestionNum() {
        return questions.size();
    }
    public void addQuestion(Question question) {
        this.questions.add(question);
    }
    public void setParticipants(ArrayList<String> participants) {
        this.participants = participants;
    }
    public boolean checkLastRespondent(){
        if (finishedSurvey==respondents){
            return true;
        }
        return false;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public String getSummary(){
        isFinished=true;
        String response="SURVEY_SUMMARY "+finishedSurvey+"|";
        for (int i = 0; i < questions.size(); i++) {
            response=response+questions.get(i).getQuestion()+";";
            for (int j = 0; j < questions.get(i).getNumOfAnswers(); j++) {
                Answer answer=questions.get(i).getAnswers().get(i);
                response=response+answer.getAnswer()+"/"+answer.getNumOfPeople();
                response=response+";";
            }
            response=response+"|";
        }
        return response;
    }
}
