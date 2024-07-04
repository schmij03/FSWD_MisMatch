<template>
  <div id="login-container">
    <div id="login-form">
      <b-form @submit.prevent="handleLogin">
        <h1 id="title">Anmelden</h1>
        <div class="form-group">
          <label for="email"></label>
          <input type="text" id="email" placeholder="Benutzername" v-model="email" class="form-text" required>
        </div>
        <div class="form-group">
          <label for="password"></label>
          <input class="form-text" type="password" id="password" placeholder="Passwort" v-model="password" required>
        </div>
        <div class="button-container">
          <b-button id="login-btn" type="submit" variant="primary">Login</b-button>
        </div>
        <div id="register-link">
          <router-link to="/register">Ich habe noch keinen Account.</router-link>
        </div>
        <div v-if="error" class="error-message" style="margin-top: 15px; text-align: center; color: red;">
          {{ error }}
        </div>
      </b-form>
    </div>
  </div>
</template>

<script setup>
import { ref, inject } from 'vue'
import { useRouter } from 'router'

const router = useRouter();

const email = ref('');
const password = ref('');
const error = ref(null); // New ref for error message
const isLoggedIn = inject('isLoggedIn');
const loginName = inject('loginName');
const checkLogin = async () => {
  try {
    const response = await axios.get('/auth/current', '{}');
    if (response.data) {
      isLoggedIn.value = true;
      loginName.value = response.data;
    } else {
      isLoggedIn.value = false;
      loginName.value = "";
    }
  } catch (error) {
    isLoggedIn.value = false;
    loginName.value = "";
  }
}

const handleLogin = async () => {
  error.value = null; // Clear previous error
  try {
    await axios.post('/auth/login', `username=${email.value}&password=${password.value}`, {
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
    });
    await checkLogin();
    router.push({ name: 'home' });
  } catch (err) {
    console.error('Login failed:', err);
    error.value = 'Login fehlgeschlagen. Bitte überprüfen Sie Ihre Anmeldedaten.'; // Set error message
  }
};

</script>

<style scoped>
#login-btn {
  border-radius: 20px; 
  background-color:#65558F; 
  width: 100%; 
  padding: 10px; 
  font-size: 16px;
}

#register-link {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 15px;
  color: #65558F;
}

.form-text {
  border-radius: 20px; 
  background-color:#ECE6F0; 
  padding: 10px; 
  font-size: 16px;
}

  #title {
    text-align: center;
  }

#login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #FEF7FF;
}

#login-form {
  background-color: #FEF7FF;
  padding: 20px;
  border-radius: 1rem;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  width: 400px;
}

.form-group {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
}

input {
  width: 100%;
  padding: 8px;
  box-sizing: border-box;
  border: 1px solid #ccc;
  border-radius: 4px;
}

button {
  width: 100%;
  padding: 10px;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.error-message {
  margin-top: 15px;
  text-align: center;
  color: red;
}

</style>
