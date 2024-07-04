<template>
  <div class="register-container">
    <div class="register-form">
      <!-- Registration form -->
      <b-form @submit.prevent="completeRegistration">
        <h1 id="title">Registrieren</h1>
        <div class="form-grid">
          <!-- Checkbox Group -->
          <div class="form-group full-width">
            <label>Interessen / Hobbies:</label>
            <div class="checkbox-container">
              <div class="checkbox-grid">
                <div v-for="interest in interests" :key="interest.id" class="checkbox-item">
                  <input class=checkboxitemself type="checkbox" :value="interest.id" v-model="selectedOptions" :id="'interest-' + interest.id" />
                  <label :for="'interest-' + interest.id" class="small-text">
                    {{ interest.name }}:
                  </label>
                  <small class="small-text2">{{ interest.examples }}</small>
                  <hr>
                </div>
              </div>
            </div>
          </div>
          <!-- File Upload -->
          <div class="form-group full-width">
            <label for="profileImage">Profilbild hochladen:</label>
            <input type="file" class="form-control form-text" id="profileImage" @change="onFileChange" accept="image/*" required>
          </div>
          <!-- Interested in -->
          <div class="form-group full-width">
            <label for="sexinterest">Interessiert an:</label>
            <select class="form-text" id="sexinterest" v-model="sexinterest" required>
              <option value="" disabled selected>Bitte auswählen...</option>
              <option value="MALE">Männlich</option>
              <option value="FEMALE">Weiblich</option>
            </select>
          </div>
        </div>
        <div v-if="error" class="error-message">
          {{ error }}
        </div>
        <b-button id="login-btn" type="submit" variant="primary">Registration abschliessen</b-button>
        <div id="register-link">
          <router-link to="/register">Zurück</router-link>
        </div>
      </b-form>
    </div>
  </div>
</template>

<script>
import { inject, ref, onMounted } from 'vue';
import { useRouter } from 'router';

export default {
  name: 'Register2',
  setup() {
    const router = useRouter();
    const registrationData = inject('registrationData');
    const sexinterest = ref('');
    const error = ref('');
    const selectedOptions = ref([]);
    const profileImage = ref(null);
    const interests = ref([]);

    onMounted(() => {
      axios.get('http://mismatch-907b17d7298c.herokuapp.com/api/interest/all')
        .then(response => {
          interests.value = response.data;
        })
        .catch(error => {
          console.error("There was an error fetching the interests!", error);
        });
    });

    const onFileChange = (event) => {
      const file = event.target.files[0];
      if (file) {
        profileImage.value = file;
      }
    };

    const completeRegistration = async () => {
      const selectedInterests = selectedOptions.value.map(id => {
        const interest = interests.value.find(interest => interest.id === id);
        return {
          id: interest.id,
          name: interest.name,
          examples: interest.examples
        };
      });

      const formData = {
        displayName: registrationData.value.name,
        loginName: registrationData.value.name,
        passwordHash: registrationData.value.password,
        userIntro: registrationData.value.about,
        interests: selectedInterests,
        email: registrationData.value.email,
        mobile: registrationData.value.phone,
        birthdate: registrationData.value.birthdate,
        gender: registrationData.value.sex,
        genderInterest: sexinterest.value,
        zip: registrationData.value.zip,
        city: registrationData.value.city,
        rawPNGImageData: profileImage.value ? await formatImage(profileImage.value) : ""
      };

      // Send formData as JSON to the server
      axios.put('http://mismatch-907b17d7298c.herokuapp.com/api/profile/register', formData)
        .then(response => {
          console.log('Registration data successfully sent to the server:', response.data);
          router.push({ name: 'regSuccess' }); // Navigate to the regSuccess page on success
        })
        .catch(error => {
          console.error('There was an error sending the registration data to the server:', error);
        });
    };

    const formatImage = (file) => {
      return new Promise((resolve, reject) => {
        const img = new Image();
        img.src = URL.createObjectURL(file);
        img.onload = () => {
          const canvas = document.createElement('canvas');
          canvas.width = img.width;
          canvas.height = img.height;
          const ctx = canvas.getContext('2d');
          ctx.drawImage(img, 0, 0, canvas.width, canvas.height);
          const dataUrl = canvas.toDataURL('image/png');
          const imageOnlyBase64 = dataUrl.split(';base64,').pop();
          resolve(imageOnlyBase64);
        };
        img.onerror = (error) => {
          reject(error);
        };
      });
    };

    return {
      sexinterest,
      error,
      selectedOptions,
      profileImage,
      interests,
      onFileChange,
      completeRegistration,
    };
  }
};
</script>

<style scoped>
#title {
  text-align: center;
}

.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #FEF7FF;
}

.register-form {
  background-color: #FEF7FF;
  padding: 20px;
  border-radius: 1rem;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  width: 75%;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.full-width {
  grid-column: span 2;
}

.checkbox-container {
  padding: 10px;
  background-color: #ECE6F0;
  border-radius: 1rem;
  max-height: 300px; /* Set the height of the container */
  overflow-y: auto;  /* Enable vertical scrolling */
}

.checkbox-grid {
  display: grid;
  grid-template-columns: 1fr; /* Single column layout */
  gap: 2px; /* Space between rows */
  align-items:left;
}

.checkbox-item {
  align-items: left;
  margin-bottom: 0;
  text-align: left;

}

.small-text {
  font-size: 1rem;
  margin-right: 10px;
  font-weight: bold;
}
.checkboxitemself{
  margin-left: 15px;
  align-items:left;
  text-align:center;
  margin-right: 10px;
}
.small-text2 {
  font-size: 0.8rem;
  color: #555;
}

label {
  align-items: center;
  margin-bottom: 0;
}

select {
  width: 100%;
  padding: 10px;
  box-sizing: border-box;
  border: 1px solid #ccc;
  border-radius: 20px;
  background-color: #ECE6F0;
  font-size: 16px;
  margin-bottom: 15px;
}

button {
  width: 100%;
  padding: 10px;
  background-color: #65558F;
  color: white;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  margin-top: 15px;
}

button:hover {
  background-color: #4d3d73;
}

#error {
  color: red;
  margin-bottom: 15px;
  text-align: center;
}

#register-link {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 15px;
  color: #65558F;
}

.error-message {
  margin-top: 15px;
  text-align: center;
  color: red;
}


</style>
