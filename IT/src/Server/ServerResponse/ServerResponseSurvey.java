package Server.ServerResponse;

import Server.MessageProcessor;
import Server.Survey.Answer;
import Server.Survey.Question;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;


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

        //Starting the survey
        if (request.startsWith("SURVEY START")){
            if (mp.getServer().getClientsHashMapSize()<3){
                mp.sendMessage("FAIL05 Not enough users for survey");
            }else {
                mp.sendMessage("SURVEY_OK");
                mp.createSurvey();
            }
        }

        //Gathering the questions for the survey
        if (request.startsWith("SURVEY Q")){
            String[] req = request.split("/");
            if (checkQuestion(req)) {
                addQuestion(req);
            }
        }

        //Sending Client List
        if (request.startsWith("SURVEY Q_STOP")){
            mp.sendMessage("SURVEY_LIST "+ mp.getServer().getClientList(mp.getName()));
        }

        //Receiving Client List for survey
        if (request.startsWith("SURVEY LIST_RESPONSE")){
            if (checkIfClientExists(request)){
                ArrayList<String>names=getListOfClients(request);
                mp.getSurvey().setParticipants(names);
                mp.sendMessage("SURVEY_LIST_OK");
                mp.getServer().broadcastMessageToListOfClients("SURVEY_EVENT "+mp.getName(),names);
                Timer surveyTimer = new Timer();
                TimerTask SendSummary = new TimerTask() {
                    @Override
                    public void run() {
                        if (mp.getSurvey()!=null && mp.getSurvey().isFinished()!=true){
                            String respond=mp.getSurvey().getSummary();
                            mp.getServer().broadcastMessageToListOfClients(respond,mp.getSurvey().getParticipants());
                        }
                    }
                };
                surveyTimer.schedule(SendSummary, 300000);
            }

        }
    }
    private boolean checkQuestion(String[] request){
        System.out.println(Arrays.toString(request));
        if (mp.getSurvey().getQuestions().size()>10){
            mp.sendMessage("SURVEY_LIST "+ mp.getServer().getClientList(mp.getName()));
            return false;
        }
        if (request.length <= 3 || request.length >= 6){
            mp.sendMessage("FAIL06 Invalid question or wrong number of answers");
            return false;
        }
        return true;
    }
    private boolean checkIfClientExists(String request){
        ArrayList<String>names=getListOfClients(request);
        for (int i = 0; i <names.size() ; i++) {
            if (!mp.getServer().checkIfClientExists(names.get(i))) {
                mp.sendMessage("FAIL07 usernames are incorrect");
                return false;
            }

        }
        return true;
    }
    private void addQuestion(String[]req){
        Question q = new Question(req[1]);
        for (int i=2;i<req.length;i++){
            q.addAnswers(new Answer(req[i]));
        }
        mp.getSurvey().addQuestion(q);
        mp.sendMessage("SURVEY_Q_OK");
    }
    private ArrayList<String> getListOfClients(String request){
        String[] response = request.split("/");
        ArrayList<String>names=new ArrayList<>();
        for (int i = 1; i <response.length ; i++) {
            names.add(response[i]);
        }
        return names;
    }
}
