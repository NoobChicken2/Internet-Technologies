package Server.ServerResponse;

import Server.MessageProcessor;
import Server.Server;
import Server.ServerResponse.Survey.Answer;
import Server.ServerResponse.Survey.Question;


public class ServerResponseSurvey implements ServerResponse {
    private MessageProcessor mp;

    public ServerResponseSurvey(MessageProcessor mp) {
        this.mp=mp;
    }
    @Override
    public void respond(String request) {
        String[] response = request.split(" ");
        if (response.length<2){//check if there are any arguments after header
            mp.sendMessage("FAIL00 Unkown command");
        }else {
            if (mp.checkClientLoggedIn()) {
                surveyProcessor(request);
            }
        }
    }
    private void surveyProcessor(String request){
        System.out.println(request);
        //Starting the survey
        if (request.startsWith("SURVEY START")){
            if (Server.clients.size()<3){
                mp.sendMessage("FAIL05 Not enough users for survey");
            }else {
                mp.sendMessage("SURVEY_OK");
                mp.createSurvey();
            }
        }
        //Gathering the questions for the survey
        if (request.startsWith("SURVEY Q")){
            String[] req = request.split(" ");
            if (checkQuestion(req)) {
                addQuestion(req);
            }
        }
        //Sending Client List
        if (request.startsWith("SURVEY Q_STOP")){
            mp.sendMessage("SURVEY_LIST "+ Server.getClientList(mp.getName()));
        }
    }

    public boolean checkQuestion(String[] request){
        if (mp.getSurvey().getQuestions().size()>10){
            mp.sendMessage("SURVEY_LIST "+ Server.getClientList(mp.getName()));
            return false;
        }
        if (request.length<5){
            mp.sendMessage("FAIL06 Invalid question or wrong number of answers");
            return false;
        }
        if (request.length>7){
            mp.sendMessage("FAIL06 Invalid question or wrong number of answers");
            return false;
        }
        return true;
    }

    public void addQuestion(String[]req){
        Question q = new Question(req[1]);
        for (int i=2;i<req.length;i++){
            q.addAnswers(new Answer(req[i]));
        }
        mp.getSurvey().addQuestion(q);
        mp.sendMessage("SURVEY_Q_OK");
    }
}
