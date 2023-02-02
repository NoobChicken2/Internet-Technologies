package Server.Survey;

public class Answer {
    private String answer;
    private int numOfPeople;

    public Answer(String answer) {
        this.answer = answer;
        numOfPeople=0;
    }

    public Answer() {
        this.numOfPeople = 0;
    }

    public String getAnswer() {
        return answer;
    }

    public int getNumOfPeople() {
        return numOfPeople;
    }

    public void setNumOfPeople() {
        this.numOfPeople++;
    }
}
