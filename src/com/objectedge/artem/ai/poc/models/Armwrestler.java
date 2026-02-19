package com.objectedge.artem.ai.poc.models;

public class Armwrestler {
    private int id;
    private String name;
    private String surname;
    private int age;
    private String hand; // "left" or "right"
    private int wins = 0;
    private int losses = 0;

    public Armwrestler(String name, String surname, int age, String hand) {
        this.id = generateRandomId();
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.hand = hand;
        this.wins = 0;
        this.losses = 0;
    }

    private int generateRandomId() {
        return (int) (Math.random() * 1000000) + 1;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public int getAge() {
        return age;
    }

    public String getHand() {
        return hand;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public boolean isEliminated() {
        return losses >= 2;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setHand(String hand) {
        this.hand = hand;
    }

    // Win/Loss counters
    public void incrementWins() {
        this.wins++;
    }

    public void decrementWins() {
        if (this.wins > 0) this.wins--;
    }

    public void incrementLosses() {
        this.losses++;
    }

    public void decrementLosses() {
        if (this.losses > 0) this.losses--;
    }

    @Override
    public String toString() {
        return "Armwrestler{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", age=" + age +
                ", hand='" + hand + '\'' +
                ", wins=" + wins +
                ", losses=" + losses +
                '}';
    }
}

