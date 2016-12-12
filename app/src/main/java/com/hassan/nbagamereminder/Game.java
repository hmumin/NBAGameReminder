package com.hassan.nbagamereminder;

/**
 * Created by hmumin on 12/9/16.
 */

public class Game {

    public String visitorTeam;
    public String homeTeam;
    public String dateOfGame;
    public String timeOfGame;
    public String emptyField;



    public Game()
    {

    }

    public Game(String dateOfGame, String timeOfGame, String visitorTeam, String homeTeam,
                String emptyField)
    {
        this.dateOfGame = dateOfGame;
        this.timeOfGame = timeOfGame;
        this.visitorTeam = visitorTeam;
        this.homeTeam = homeTeam;
        this.emptyField = emptyField;
    }

    //getters and setters

    public String getVisitorTeam() {
        return visitorTeam;
    }

    public void setVisitorTeam(String visitorTeam) {
        this.visitorTeam = visitorTeam;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }

    public String getDateOfGame() {
        return dateOfGame;
    }

    public void setDateOfGame(String dateOfGame) {
        this.dateOfGame = dateOfGame;
    }

    public String getTimeOfGame() {
        return timeOfGame;
    }

    public void setTimeOfGame(String timeOfGame) {
        this.timeOfGame = timeOfGame;
    }

    public String getEmptyField() {
        return emptyField;
    }

    public void setEmptyField(String emptyField) {
        this.emptyField = emptyField;
    }
}

