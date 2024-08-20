package com.ai.ai_disc;

import com.ai.ai_disc.Farmer.QueryDetails1;

public class chat_singleton {


    private chat_singleton(){

    };


    private QueryDetails1 chat;


    public QueryDetails1 getchat() {
        return chat;
    }

    public void setchat(QueryDetails1 chat) {
        this.chat = chat;
    }



    private static chat_singleton instance;

      public static chat_singleton getInstance(){


          if(instance==null){
              instance = new chat_singleton();
          }

          return instance;
      }









}
