package com.example.meettify.exception.chat;
/*
 *   worker : 유요한
 *   work :
 *          채팅 예외처리
 *   date : 2024/11/26
 * */
public class ChatException extends RuntimeException{
    public ChatException(String msg) {
        super(msg);
    }
}
