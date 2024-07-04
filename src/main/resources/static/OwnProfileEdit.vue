<template>
  <div class="container-lg">
    <div class="row">
      <a href="#" @click="back">
        <div class="col-6" style="padding: 20px; display: flex">
          <svg xmlns="http://www.w3.org/2000/svg" width="36" height="36" fill="currentColor" class="bi bi-arrow-left" viewBox="0 0 16 16">
            <path fill-rule="evenodd" d="M15 8a.5.5 0 0 0-.5-.5H2.707l3.147-3.146a.5.5 0 1 0-.708-.708l-4 4a.5.5 0 0 0 0 .708l4 4a.5.5 0 0 0 .708-.708L2.707 8.5H14.5A.5.5 0 0 0 15 8"/>
          </svg>
          <h1 style="margin-left: 20px">Profil</h1>
        </div>
      </a>
    </div>
    <div class="row row-cols-2">
      <div class="col">
        <img width="100%" style="border-radius: 20px; margin-right: 50px" :src="imagePath">
      </div>
      <div class="col-6">
        <h1>{{ profile.displayName }}</h1>
        <b-form @submit.prevent="handleSaveRequest">
          <label>Name:</label>
          <b-form-input id="displayName" class="displayName" placeholder="displayName" v-model="profile.displayName" class=inputformat></b-form-input>
          <label>PLZ:</label>
          <b-form-input id="zip" class="zip" placeholder="zip" v-model="profile.zip"class=inputformat></b-form-input>
          <label>Ortschaft:</label>
          <b-form-input id="city" class="city" placeholder="city" v-model="profile.city"class=inputformat></b-form-input>
          <label>Geschlecht:</label>
          <b-form-select id="gender" v-model="profile.gender" required class="form-select">
            <b-form-select-option value="">Bitte auswählen...</b-form-select-option>
            <b-form-select-option value="MALE">Männlich</b-form-select-option>
            <b-form-select-option value="FEMALE">Weiblich</b-form-select-option>
          </b-form-select>
          <label>Interessiert an:</label>
          <b-form-select id="genderInterest" v-model="profile.genderInterest" required class="form-select">
            <b-form-select-option value="">Bitte auswählen...</b-form-select-option>
            <b-form-select-option value="MALE">Männlich</b-form-select-option>
            <b-form-select-option value="FEMALE">Weiblich</b-form-select-option>
          </b-form-select>
          <label>Über dich:</label>
          <b-form-input id="userIntro" class="userIntro" placeholder="userIntro" v-model="profile.userIntro"class=inputformat></b-form-input>
          <div class="form-group">
            <label>Interessen / Hobbies:</label>
            <div class="checkbox-container">
              <div class="checkbox-grid">
                <div v-for="interest in interests" :key="interest.id" class="checkbox-item">
                  <input type="checkbox" :value="interest.id" v-model="selectedInterests" :id="'interest-' + interest.id" class="checkboxitemself">
                  <label :for="'interest-' + interest.id" class="small-text">{{ interest.name }}:</label>
                  <small class="small-text2">{{ interest.examples }}</small>
                </div>
              </div>
            </div>
          </div>
          <button type="submit" variant="primary" style="width: 100px;">Speichern</button>
        </b-form>
      </div>
    </div>
  </div>
</template>


<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'router';

const router = useRouter();
const profile = ref({});
const imagePath = ref("");
const interests = ref([]);
const selectedInterests = ref([]);

onMounted(async () => {
  try {
    const interestsResponse = await axios.get('/api/interest/all');
    interests.value = interestsResponse.data;
    console.log('Interests:', interests.value);

    const profileResponse = await axios.get('/api/profile/me');
    profile.value = profileResponse.data;
    console.log('Profile interests:', profile.value.interests);
    imagePath.value = '/api/profile/' + profile.value.uuid + '/profileImage';
    
    selectedInterests.value = profile.value.interests.map(interest => interest.id);
    console.log('Selected Interests:', selectedInterests.value);
  } catch (error) {
    console.error('Error fetching data:', error);
  }
});

const handleSaveRequest = async () => {
  const updatedInterests = interests.value.filter(interest => selectedInterests.value.includes(interest.id));
  profile.value.interests = updatedInterests;
  try {
    await axios.post('/api/profile/me', profile.value);
    router.push({ name: 'home' });
  } catch (error) {
    console.error('Error updating profile:', error);
  }
};
</script>
<style scoped>
.inputformat{
  margin-bottom: 15px;
}
.checkbox-container {
  padding: 10px;
  background-color: #ECE6F0;
  border-radius: 1rem;
  max-height: 125px;
  overflow-y: auto;
}

.checkbox-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 2px;
  align-items: left;
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

.checkboxitemself {
  margin-left: 15px;
  align-items: left;
  text-align: center;
  margin-right: 10px;
}

.small-text2 {
  font-size: 0.8rem;
  color: #555;
}

.form-select {
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
</style>
