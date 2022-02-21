package cs455.overlay.node;

import java.util.ArrayList;

import cs455.overlay.protocols.Message;


public class Buffer{

    final static int capacity = 2500;
    static int count = 0;
    
    ArrayList<Message> items = new ArrayList<Message>(2500);

    public synchronized void insert(Message in)throws InterruptedException{
        items.add(in);
        count++;
    }

    public synchronized Message remove() throws InterruptedException{
        Message payload = items.get(count - 1);
        items.remove(count - 1);
        count--;
        return payload;    
    }

    public synchronized boolean isFull(){
        if(items.size() == 2500)
            return true;
        else 
            return false;
    }

    public synchronized boolean isEmpty(){
        if(items.size() == 0)
            return true;
        else
            return false;
    }
}