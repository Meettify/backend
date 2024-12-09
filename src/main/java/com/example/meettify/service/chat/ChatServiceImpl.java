package com.example.meettify.service.chat;

import com.example.meettify.document.chat.ChatMessage;
import com.example.meettify.dto.chat.*;
import com.example.meettify.dto.member.ResponseMemberDTO;
import com.example.meettify.entity.chat_room.ChatRoomEntity;
import com.example.meettify.entity.member.MemberEntity;
import com.example.meettify.exception.chat.ChatRoomException;
import com.example.meettify.exception.member.MemberException;
import com.example.meettify.repository.chat.ChatMessageRepository;
import com.example.meettify.repository.chat.ChatRoomRepository;
import com.example.meettify.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Log4j2
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;

    @Override
    public void sendMessage(ChatMessageDTO message) {
        try {
            ChatMessage chatMessage = ChatMessage.create(message);
            // 몽고 디비에 저장
            chatMessageRepository.save(chatMessage);
        } catch (Exception e) {
            log.error(e);

        }
    }

    // 채팅방의 채팅 내역 조회
    @Override
    public List<ChatMessageDTO> getMessagesByRoomId(Long roomId) {
        List<ChatMessage> findChatByRoomId = chatMessageRepository.findByRoomId(roomId);
        return findChatByRoomId
                .stream().map(ChatMessageDTO::change)
                .toList();
    }

    // 단체 채팅하는 채팅방 생성
    @Override
    public ChatRoomDTO createRoom(String roomName, String email) {
        try {
            // 회원 조회
            MemberEntity findMember = memberRepository.findByMemberEmail(email);
            // 채팅방 엔티티 생성
            ChatRoomEntity chatRoomEntity = ChatRoomEntity.create(roomName, findMember.getNickName(), RoomStatus.OPEN);
            // 채팅방 디비에 저장
            ChatRoomEntity saveChatRoom = chatRoomRepository.save(chatRoomEntity);
            return ChatRoomDTO.change(saveChatRoom);
        } catch (Exception e) {
            throw new ChatRoomException("채팅방을 생성하는데 실패했습니다.");
        }
    }

    // 본인에게 해당된
    @Override
    public List<ChatRoomDTO> getRooms(String email) {
        try {
            // 회원 조회
            MemberEntity findMember = memberRepository.findByMemberEmail(email);
            // 본인의 채팅방 모두 조회
            List<ChatRoomEntity> findAllByMember = chatRoomRepository.findChatRoomsByCreatedOrInvited(findMember.getNickName(), findMember.getMemberId());
            return findAllByMember.stream()
                    .map(ChatRoomDTO::change)
                    .toList();
        } catch (Exception e) {
            throw new ChatRoomException("채팅방을 조회하는데 실패했습니다.");
        }
    }

    // 채팅방 입장시 체크
    @Override
    public boolean joinRoom(String email, Long roomId) {
        // 회원 조회
        MemberEntity findMember = memberRepository.findByMemberEmail(email);
        // 방번호와 초대받은 회원번호가 일치한지 조회
        ChatRoomEntity findChatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatRoomException("채팅방이 존재하지 않습니다."));

        if (findChatRoom == null) {
            log.info("초대받지 않은 유저입니다.");
            return false;
        }
        // 회원을 채팅방에 초대
        findChatRoom.getInviteMemberIds().add(findMember.getMemberId());
        return true;
    }

    // 채팅방에 들어간 유저 리스트
    @Override
    public List<ChatMemberDTO> getRoomMembers(Long roomId) {
        try {
            // 채팅방 조회
            ChatRoomEntity findChatRoom = chatRoomRepository.findById(roomId)
                    .orElseThrow(() -> new ChatRoomException("채팅방이 존재하지 않습니다."));

            List<MemberEntity> memberList = findChatRoom.getInviteMemberIds()
                    .stream()
                    .map(id -> memberRepository.findById(id)
                            .orElseThrow(() -> new MemberException("유저가 존재하지 않습니다.")))
                    .toList();

            return memberList
                    .stream().map(ChatMemberDTO::getMember)
                    .toList();
        } catch (ChatRoomException e) {
            throw new ChatRoomException(e.getMessage());
        } catch (MemberException e) {
            throw new MemberException(e.getMessage());
        }
    }
}
