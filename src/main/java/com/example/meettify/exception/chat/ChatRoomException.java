package com.example.meettify.exception.chat;
/*
 *   worker : 유요한
 *   work :
 *          채팅방 예외처리
 *   date : 2024/11/26
 * */
public class ChatRoomException extends RuntimeException{
    public ChatRoomException(String msg) {
        super(msg);
    }
}
