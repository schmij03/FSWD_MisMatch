<template>
  <div class="container-md">
    <div class="row">
      <a href="#" @click="back">
        <div class="col-6" style=" display: flex">
          <svg xmlns="http://www.w3.org/2000/svg" width="36" height="30" fill="currentColor" class="bi bi-arrow-left"
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
        <p/>
        <img width="100%" :src="imagePath">
        <p/>
      </div>
      <div class="col">
        <div id="profilbackground">
          <b-form>

            <label class="sr-only" for="displayName"></label>
            <b-form-input
                id="displayName"
                class="displayName"
                placeholder="displayName"
                v-model="profile.displayName"
                plaintext="readonly"
            ></b-form-input>
            <label class="sr-only" for="age"></label>
            <b-form-input
                id="age"
                class="age"
                placeholder="age"
                v-model="profile.age"
                plaintext="readonly"
            ></b-form-input>
            <label class="sr-only" for="biography"></label>
            <b-form-textarea
                id="biography"
                class="biography"
                placeholder="biography"
                rows="3"
                v-model="profile.biography"
                plaintext="readonly"
            ></b-form-textarea>
            <label class="sr-only" for="interests"></label>
              <div v-for="interest in interests" :key="interest.id" class="checkbox-item">
                <ul>{{ interest.name }}</ul>
              </div>
          </b-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue';
import {useRouter} from 'router';


const props = defineProps({
  uuid: String
});

const profile = ref({});
const interests = ref([]);
const imagePath = ref("");

const router = useRouter();


// Fetch data from APIs on component mount
onMounted(() => {
  console.log(props);

  axios.get('/api/profile/' + props.uuid).then(response => {
    profile.value = response.data;
    interests.value = response.data.interests;console.log(profile)
    imagePath.value = '/api/profile/' + profile.value.uuid + '/profileImage'
  });


});

function back() {
  router.push('/home');
  console.log('Login button clicked');
}
</script>