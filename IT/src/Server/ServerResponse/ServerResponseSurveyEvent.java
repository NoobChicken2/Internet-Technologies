package Server.ServerResponse;

import Server.MessageProcessor;
import Server.Survey.Answer;
import Server.Survey.Question;

import java.util.ArrayList;

public class ServerResponseSurveyEvent implements ServerResponse{
    private MessageProcessor mp;
    private MessageProcessor sc;

    public ServerResponseSurveyEvent(MessageProcessor mp) {
        this.mp=mp;
    }

    @Override
    public void respond(String request) {
        String[] response = request.split(" ");
        if (response.length<2){//check if there are any arguments after header
            mp.sendMessage("FAIL00 Unkown command");
        }else {
            if (mp.checkClientLoggedIn()) {
                surveyEventProcessor(request);
            }
        }
    }

    private void surveyEventProcessor(String request) {

        //Sending first question to participants
        if (request.startsWith("SURVEY_EVENT JOIN")){
            sc.getSurvey().setRespondents();
            String[] response = request.split(" ");
            String nameCreator=response[2];
            sc=sc.getServer().getMessageProcessor(nameCreator);

            Question q= sc.getSurvey().getQuestion(0);
            ArrayList<Answer> answers=q.getAnswers();
            String message="SURVEY_EVENT Q "+q.getQuestion()+";0";
            for (int i = 0; i < q.getNumOfAnswers(); i++) {
                message=message+";"+answers.get(i).getAnswer();
            }
            sc.sendMessage(message);
        }

        //Receiving answers and sending next question
        if (request.startsWith("SURVEY_EVENT A ")){

            String[] response = request.split(" ");
            String message=response[2];
            String[] res = message.split(";");

            int qNumber=Integer.parseInt(res[1]);
            if (qNumber<=sc.getSurvey().getQuestionNum()) {
                Question q = sc.getSurvey().getQuestion(qNumber);
                Answer a = q.getOneAnswer(res[0]);

                a.setNumOfPeople();
                Question nextQuestion = sc.getSurvey().getQuestion(qNumber + 1);
                String mes = "SURVEY_EVENT Q " + nextQuestion.getQuestion() + ";" + qNumber + 1;

                ArrayList<Answer> answers = nextQuestion.getAnswers();
                for (int i = 0; i < nextQuestion.getNumOfAnswers(); i++) {
                    mes = mes + ";" + answers.get(i).getAnswer();
                }
                sc.sendMessage(mes);
            }else {
                sc.getSurvey().getFinishedSurvey();
                if (sc.getSurvey().checkLastRespondent()){
                    String respond=sc.getSurvey().getSummary();
                    mp.getServer().broadcastMessageToListOfClients(respond,sc.getSurvey().getParticipants());
                }
            }
        }
    }
}
