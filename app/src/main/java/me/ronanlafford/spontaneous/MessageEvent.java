package me.ronanlafford.spontaneous;

import java.util.ArrayList;


public class MessageEvent {
    ArrayList<Event> messageList;
    String test;

    public MessageEvent(String test, ArrayList<Event> messageList) {
        this.test = test;
        this.messageList = messageList;
    }


    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public ArrayList<Event> getMessageList() {
        return messageList;
    }

    public void setMessageList(ArrayList<Event> messageList) {
        this.messageList = messageList;
    }
}
