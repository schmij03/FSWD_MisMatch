<template>
  <div id="login-container">
    <div id="login-form">
      <b-form @submit.prevent="continueRegistration">
        <h1 id="title">Registrierung</h1>
        <div class="form-grid">
          <div class="form-group">
            <label for="name">Name (login):</label>
            <input type="text" id="name" v-model="registrationData.name" class="form-text" required>
          </div>
          <div class="form-group">
            <label for="birthdate">Geburtsdatum:</label>
            <input type="date" id="birthdate" v-model="registrationData.birthdate" class="form-text" required>
          </div>
          <div class="form-group">
            <label for="email">Email:</label>
            <input type="email" id="email" v-model="registrationData.email" placeholder="name@example.com" class="form-text" required>
          </div>
          <div class="form-group">
            <label for="phone">Telefonnummer:</label>
            <input type="text" id="phone" v-model="registrationData.phone" placeholder="+41791234567" class="form-text" required>
          </div>
          <div class="form-group">
            <label for="sex">Geschlecht:</label>
            <select class="form-text" id="sex" v-model="registrationData.sex" required>
              <option value="" disabled selected>Bitte auswählen...</option>
              <option value="MALE">Männlich</option>
              <option value="FEMALE">Weiblich</option>
            </select>
          </div>
          <div class="form-group">
            <label for="about">Über dich:</label>
            <input type="text" id="about" v-model="registrationData.about" class="form-text" required>
          </div>
          <div class="form-group">
            <label for="zip">PLZ:</label>
            <input type="number" id="zip" v-model="registrationData.zip" @change="fetchLocation" class="form-text" required>
          </div>
          <div class="form-group">
            <label for="city">Ort:</label>
            <input type="text" id="city" v-model="registrationData.city" class="form-text" readonly required>
          </div>
          <div class="form-group">
            <label for="password">Passwort:</label>
            <input type="password" id="password" v-model="registrationData.password" class="form-text" required>
          </div>
          <div class="form-group">
            <label for="passwordcheck">Passwort bestätigen:</label>
            <input type="password" id="passwordcheck" v-model="registrationData.passwordcheck" class="form-text" required>
          </div>
        </div>
        <div v-if="error" class="error-message">
          {{ error }}
        </div>
        <b-button id="login-btn" type="submit" variant="primary">Weiter</b-button>
        <div id="register-link">
          <router-link to="/login">Ich habe bereits einen Account.</router-link>
        </div>
      </b-form>
    </div>
  </div>
</template>

<script>
import { ref, inject } from 'vue';
import { useRouter } from 'router';

export default {
  name: 'Register',
  setup() {
    const registrationData = inject('registrationData');
    const error = ref('');
    const router = useRouter();

    const validatePasswords = () => {
      const password = registrationData.value.password;
      const passwordCheck = registrationData.value.passwordcheck;
      const name = registrationData.value.name.toLowerCase();
      const passwordRegex = /^(?=.*[A-Z])(?=.*\d)[A-Za-z\d]{8,}$/;
      const forbiddenWords = ['test', '1234'];

      if (password !== passwordCheck) {
        error.value = "Die Passwörter stimmen nicht überein.";
        return false;
      } else if (!passwordRegex.test(password)) {
        error.value = "Das Passwort muss mindestens 8 Zeichen lang sein und sowohl Großbuchstaben als auch Zahlen enthalten.";
        return false;
      } else if (password.toLowerCase().includes(name)) {
        error.value = "Das Passwort darf den Namen nicht enthalten.";
        return false;
      } else if (forbiddenWords.some(word => password.toLowerCase().includes(word))) {
        error.value = "Das Passwort darf 'test' oder '1234' nicht enthalten.";
        return false;
      } else {
        error.value = '';
        return true;
      }
    };

    const validateEmail = (email, callback) => {
      const url = `https://emailvalidation.abstractapi.com/v1/?api_key=3e23141ece37454fa29233444dafee4a&email=${email}`;
      const xmlHttp = new XMLHttpRequest();
      xmlHttp.onreadystatechange = function() {
        if (xmlHttp.readyState === 4 && xmlHttp.status === 200)
          callback(xmlHttp.responseText);
      }
      xmlHttp.open("GET", url, true); // true for asynchronous
      xmlHttp.send(null);
    };

    const validatePhone = (phone, callback) => {
      const url = `https://phonevalidation.abstractapi.com/v1/?api_key=79721f91be334674805391f62c466c79&phone=${phone}`;
      const xmlHttp = new XMLHttpRequest();
      xmlHttp.onreadystatechange = function() {
        if (xmlHttp.readyState === 4 && xmlHttp.status === 200)
          callback(xmlHttp.responseText);
      }
      xmlHttp.open("GET", url, true); // true for asynchronous
      xmlHttp.send(null);
    };

    const continueRegistration = () => {
      if (validatePasswords()) {
        validateEmail(registrationData.value.email, (emailResponse) => {
          const emailResult = JSON.parse(emailResponse);
          if (emailResult.is_valid_format.value) {
            validatePhone(registrationData.value.phone, (phoneResponse) => {
              const phoneResult = JSON.parse(phoneResponse);
              if (phoneResult.valid) {
                router.push('/register2');
              } else {
                error.value = "Die Telefonnummer ist ungültig.";
              }
            });
          } else {
            error.value = "Die E-Mail-Adresse ist ungültig.";
          }
        });
      }
    };

    const fetchLocation = () => {
      const locationApi = 'https://api.zippopotam.us/CH/';
      const zipValue = registrationData.value.zip;
      if (!zipValue) return;

      const request = `${locationApi}${zipValue}`;
      fetch(request)
        .then(response => response.json())
        .then(data => {
          if (data.places && data.places.length > 0) {
            const cityValue = data.places[0]['place name'];
            registrationData.value.city = cityValue;
          } else {
            registrationData.value.city = '';
          }
        })
        .catch(() => {
          registrationData.value.city = '';
        });
    };

    return {
      registrationData,
      error,
      continueRegistration,
      fetchLocation,
    };
  },
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
  width: 600px;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.form-group {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
}

input,
select {
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
