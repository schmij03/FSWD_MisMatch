<template>
  <div class="container-lg">
    <div class="row">
      <a href="#" @click="back">
        <div class="col-6" style=" padding: 20px; display: flex">
          <svg xmlns="http://www.w3.org/2000/svg" width="36" height="36" fill="currentColor" class="bi bi-arrow-left"
              viewBox="0 0 16 16">
            <path fill-rule="evenodd"
                  d="M15 8a.5.5 0 0 0-.5-.5H2.707l3.147-3.146a.5.5 0 1 0-.708-.708l-4 4a.5.5 0 0 0 0 .708l4 4a.5.5 0 0 0 .708-.708L2.707 8.5H14.5A.5.5 0 0 0 15 8"/>
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

          <label class="sr-only" for="displayName"></label>
          <b-form-input
              id="displayName"
              class="displayName"
              placeholder="displayName"
              v-model="profile.displayName"
          ></b-form-input>
          <label class="sr-only" for="age"></label>
          <b-form-input
              id="age"
              class="age"
              placeholder="age"
              v-model="profile.age"
          ></b-form-input>
          <label class="sr-only" for="location"></label>
          <b-form-input
              id="location"
              class="location"
              placeholder="location"
              v-model="profile.location"
          ></b-form-input>
          <label class="sr-only" for="occupation"></label>
          <b-form-input
              id="occupation"
              class="occupation"
              placeholder="occupation"
              v-model="profile.occupation"
          ></b-form-input>
          <label class="sr-only" for="biography"></label>
          <b-form-input
              id="biography"
              class="biography"
              placeholder="biography"
              v-model="profile.biography"
          ></b-form-input>
          <label class="sr-only" for="interests"></label>
          <b-form-input
              id="interests"
              class="interests"
              placeholder="interests"
              v-model="profile.interests"
          ></b-form-input>
          <label class="sr-only" for="personalityTraits"></label>
          <b-form-input
              id="personalityTraits"
              class="personalityTraits"
              placeholder="personalityTraits"
              v-model="profile.personalityTraits"
          ></b-form-input>
          <label class="sr-only" for="lifestyle"></label>
          <b-form-input
              id="lifestyle"
              class="lifestyle"
              placeholder="lifestyle"
              v-model="profile.lifestyle"
          ></b-form-input>
          <b-button type="submit" variant="primary"
                    style=" margin-top: 20px; border-radius: 20px; width: 100px; border-color: #65558F; background-color:#65558F;">
            Save
          </b-button>
        </b-form>


      </div>
    </div>
  </div>

</template>

<script setup>
import {ref, onMounted} from 'vue';
import {useRouter} from 'router';

const router = useRouter();

const profile = ref({});
const imagePath = ref("");


// Fetch data from APIs on component mount
onMounted(() => {
  axios.get('/api/profile/me').then(response => {
    profile.value = response.data;
    imagePath.value = '/api/profile/' + profile.value.uuid + '/profileImage'
  });
});

const handleSaveRequest = () => {
  axios.post('/api/profile/me', profile.value).then(response => {
    router.push({name: 'home'});
  });
}

</script>

<script>
export default {
  methods: {
    back() {
      this.$router.push('/');
      console.log('Login button clicked');
    },
  }
}
</script>