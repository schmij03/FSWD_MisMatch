<template>
  <b-container fluid class="chat-list">
    <b-row class="full-height">
      <b-col cols="4" class="d-flex flex-column">
        <b-card class="chat-list-window">
          <div class="chat-list-title">Deine Chats</div>
          <b-list-group class="flex-grow-1 chat-list-group">
            <b-list-group-item 
              v-for="chat in chats" 
              :key="chat.uuid" 
              @click="loadChatMessages(chat.uuid, chat.displayName)"
              class="chat-list-item"
            >
              <div class="d-flex justify-content-between align-items-center w-100">
                <img :src="'/api/profile/' + chat.chatWithUUID + '/profileImage'"  alt="Profil" style="width:47px;height: 47px;margin-right: 5px;" class="rounded-pill">
                <b-button 
                  :class="{'chat-button': !isSelected(chat.uuid), 'selected-chat-button': isSelected(chat.uuid)}" 
                  block
                >
                  {{ chat.displayName }}
                </b-button>
                <RouterLink 
                  :to="{ name: 'profileDetails', params: { uuid: chat.chatWithUUID } }" 
                  class="profile-button ml-2"
                >
                  Profil
                </RouterLink>
              </div>
            </b-list-group-item>
          </b-list-group>
        </b-card>
      </b-col>
      <template v-if="chats.length > 0">
        <b-col cols="8">
          <b-card class="chat-window">
            <div class="chat-title">Chatverlauf</div>
            <div class="messages" id="messages">
              <div v-if="chatMessages.length === 0" class="no-messages">
                No messages yet. Start the conversation!
              </div>
              <div v-else v-for="message in chatMessages" :key="message.sentAt" :class="['message', message.senderUuid === profile.uuid ? 'self' : 'other']">
                <div class="message-content">{{ message.content }}</div>
                <div class="message-timestamp">{{ new Date(message.sentAt).toLocaleTimeString('de-DE', { hour: '2-digit', minute: '2-digit', hour12: false }) }}</div>
              </div>
            </div>
            <div class="input-container">
              <b-form-input class="mx-1" placeholder="Gib deine Nachricht ein" v-model="messageInput" @keydown.enter="sendMessage"></b-form-input>
              <b-button class="send-button" @click="sendMessage">Senden</b-button>
            </div>
          </b-card>
        </b-col>
      </template>
      <template v-else>
        <b-col cols="8" class="d-flex align-items-center justify-content-center">
          <div class="no-chats-message">Keine Chats vorhanden</div>
        </b-col>
      </template>
    </b-row>
  </b-container>
</template>

<style>
.full-height {
  height: 100vh;
}

.chat-list {
  height: 100vh !important;
}

.chat-list-window {
  display: flex;
  flex-direction: column;
  max-height: 100vh !important;
  /* border: 1px solid #ccc;
  border-radius: 10px;
  box-shadow: inset 0 0 10px rgba(0, 0, 0, 0.1); */
  padding: 0px;
  /* background-color: #f9f9f9; */
}

.chat-list-title {
  font-size: 1.5em;
  font-weight: bold;
  padding: 10px;
  margin: 0;
  text-align: left;
}

.chat-list-group {
  margin: 0;
  padding: 10px;
}

.chat-list-item {
  background-color: #f9f9f9;
  border: none;
  margin-bottom: 5px;
  border-radius: 5px;
  padding: 0;
}

.chat-window {
  display: flex;
  flex-direction: column;
  max-height: 100vh;
  /* border: 1px solid #ccc;
  border-radius: 10px;
  box-shadow: inset 0 0 10px rgba(0, 0, 0, 0.1); */
  padding: 10px;
  /* background-color: #f9f9f9; */
}

.chat-title {
  font-size: 1.5em;
  font-weight: bold;
  margin-bottom: 10px;
  text-align: left;
}

.messages {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
  height: 70vh;
  -ms-overflow-style: none;
  scrollbar-width: none;
}

.messages::-webkit-scrollbar {
  display: none;
}

.input-container {
  display: flex;
  align-items: center;
  padding: 10px;
  background-color: #f9f9f9;
  height: 60px;
  flex-shrink: 0;
}

.message {
  display: flex;
  flex-direction: column;
  max-width: 60%;
  margin: 10px 0;
  padding: 10px 15px;
  border-radius: 15px;
  position: relative;
  word-wrap: break-word;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.message.self {
  margin-left: auto;
  background-color: #65558F;
  color: white;
  align-items: flex-end;
}

.message.self .message-content {
  text-align: right;
}

.message.self::after {
  content: "";
  position: absolute;
  top: 50%;
  right: -10px;
  border-width: 10px;
  border-style: solid;
  border-color: #65558F transparent transparent transparent;
}

.message.other {
  margin-right: auto;
  background-color: #DAC6E1;
  color: black;
  align-items: flex-start;
}

.message.other .message-content {
  text-align: left;
}

.message.other::after {
  content: "";
  position: absolute;
  top: 50%;
  left: -10px;
  border-width: 10px;
  border-style: solid;
  border-color: #DAC6E1 transparent transparent transparent;
}

.message-content {
  padding-bottom: 5px;
}

.message-timestamp {
  font-size: 0.8em;
  text-align: right;
}

.message.self .message-timestamp {
  color: #ccc;
}

.message.other .message-timestamp {
  color: black;
}

.no-messages {
  text-align: center;
  color: #aaa;
}

.chat-button {
  background-color: #65558F;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  text-align: left;
  width: 100%;
  margin-right: 5px; /* Added margin for spacing */
}

.chat-button:hover {
  background-color: #DAC6E1;
  color: black;
}

.selected-chat-button {
  background-color: #DAC6E1 !important;
  color: black !important;
  border: none !important;
  padding: 10px 20px !important;
  border-radius: 2px !important;
  border-color: #65558F !important;
  text-align: left !important;
  width: 100% !important;
  margin-right: 5px; /* Added margin for spacing */
}

.profile-button {
  background-color: #65558f93;
  color: white;
  border: none;
  border-radius: 5px;
  padding: 10px 20px;
  text-align: center;
  width: auto;
}

.profile-button:hover {
  background-color: #514373;
  color: white;
}

.selected-profile-button {
  background-color: #DAC6E1 !important;
  color: black !important;
  border: none !important;
}

.send-button {
  background-color: #65558F;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
}

.send-button:hover {
  background-color: #514373;
}
</style>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue';

const profile = ref({});
const chats = ref([]);
const chatMessages = ref([]);
const selectedChatId = ref('');
const messageInput = ref('');
const selectedChatName = ref(''); 
const selectedChatWithUuid = ref('');

const socket = new WebSocket("ws://localhost:8080/ws");

socket.onmessage = (m) => {
  console.log('Received message from the chat server');
  loadChatMessages(selectedChatId.value);
};
socket.onopen = (m) => {
  console.log("Connected to the chat server");
};
socket.onerror = (m) => {
  console.error("Error connecting to the chat server");
};

function loadChatMessages(chatId, chatName) {
  selectedChatId.value = chatId;
  selectedChatName.value = chatName;  // Chat Name setzen
  axios.get('/api/chat/detail/' + chatId).then(response => {
    chatMessages.value = response.data;
    nextTick(scrollToBottom); // Sicherstellen, dass nach der DOM-Aktualisierung gescrollt wird
  });
}

function loadAllChats() {
  axios.get('/api/chat/' + profile.value.uuid).then(response => {
    chats.value = response.data;
    if (chats.value.length > 0) {
      const firstChat = chats.value[0];
      selectedChatId.value = firstChat.uuid;
      selectedChatWithUuid.value = firstChat.chatWithUuid;
      selectedChatName.value = firstChat.displayName;  // Chat Name setzen
      loadChatMessages(firstChat.uuid, firstChat.displayName); // Load the first chat by default
    }
  });
}


function sendMessage() {
  if (messageInput.value.trim() === '') return;

  const newMessage = {
    chatId: selectedChatId.value,
    content: messageInput.value,
    messageType: 'TEXT',
    senderUUID: profile.value.uuid
  };
  axios.put('/api/chat/detail/', newMessage).then(response => {
    loadChatMessages(selectedChatId.value);
    messageInput.value = ''; // Leeren des Eingabefeldes nach dem Senden der Nachricht
  }).catch(error => {
    console.error("Error sending message:", error);
  });
}

function scrollToBottom() {
  const messagesContainer = document.getElementById('messages');
  messagesContainer.scrollTop = messagesContainer.scrollHeight;
}

const isSelected = (chatId) => {
  return selectedChatId.value === chatId;
};

onMounted(() => {
  axios.get('/api/profile/me').then(response => {
    profile.value = response.data;
    loadAllChats();
  });
});

onBeforeUnmount(() => {
  if (socket.value) {
    socket.value.close();
  }
});
</script>