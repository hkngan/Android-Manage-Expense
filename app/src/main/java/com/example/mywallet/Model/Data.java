package com.example.mywallet.Model;

public class Data {

    int amount;
    String type;
    String id;
    String note;
    String date;

    @Override
    public String toString() {
        return "Data{" +
                "amount=" + amount +
                ", type='" + type + '\'' +
                ", id='" + id + '\'' +
                ", note='" + note + '\'' +
                ", date='" + date + '\'' +
                '}';
    }




    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Data(int amount, String type, String id, String note, String date) {
        this.amount = amount;
        this.type = type;
        this.id = id;
        this.note = note;
        this.date = date;
    }


    public Data(){

    }
}
