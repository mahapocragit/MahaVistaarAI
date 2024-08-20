package com.ai.ai_disc;

import com.ai.ai_disc.Farmer.Model_expert_query_content;
import com.ai.ai_disc.Farmer.QueryDetails1;

public class chat_expert_singleton {


    private chat_expert_singleton(){

    };


    private Model_expert_query_content chat;


    public Model_expert_query_content getchat() {
        return chat;
    }

    public void setchat(Model_expert_query_content chat) {
        this.chat = chat;
    }



    private static chat_expert_singleton instance;

      public static chat_expert_singleton getInstance(){


          if(instance==null){
              instance = new chat_expert_singleton();
          }

          return instance;
      }









}
