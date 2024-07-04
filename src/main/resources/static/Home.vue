<template>
  <b-container fluid>
    <b-row>
      <h1 class="secondary-text" v-if="profiles.length == 0">Du hast leider noch keine Matches!</h1>
    </b-row>
    <b-row class="pt-3 background-color" align-h="center">
      <b-col cols="3" v-for="profile in profiles" :key="profile.data.uuid">
        <div class="card mb-3">
          <img width="100%" :src="'/api/profile/' + profile.data.uuid + '/profileImage'" />
          <div class="card-body">
            <h2>{{ profile.data.displayName }}</h2>
            <div class="row justify-content-md-center">
              <div class="col">
                <p v-if="profile.data.gender == 'FEMALE'">Weiblich, {{ profile.data.age }}</p>
                <p v-if="profile.data.gender == 'MALE'">Männlich, {{ profile.data.age }}</p>
              </div>
            </div>
            <div class="row">
              <div class="col-sm-12">{{ profile.data.userIntro }}</div>
            </div>
            <b-button class="m-4" id="show-btn" @click="toProfileDetails(profile.data.uuid)">Details</b-button>
          </div>
        </div>
      </b-col>
    </b-row>
    <!--  -->
    <!-- <div>
      <b-modal id="details " ref="details" prop="true" lazy="true" title="Profil">
        <h1>{{ selectedDisplayName }}</h1>
        <p />
        <img width="100%" :src="imagePath" />
        <p />
        <div class="row">
          <div class="col-sm-4">Biography:</div>
          <div class="col-sm-8">{{ selectedCity }}</div>
        </div>
        <div class="row">
          <div class="col-sm-4">Location:</div>
          <div class="col-sm-8">{{ selectedUserIntro }}</div>
        </div>
        <b-button class="mt-3" variant="outline-danger" block @click="hideModal">Close Me</b-button>
      </b-modal>
    </div> -->
  </b-container>
  <div class="container-sm text-centered">
    <h3>
      Mismatches aktualisieren sich in:<br />
      {{ countdown }}
    </h3>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "router";

const router = useRouter();

// Initialize reactive variables
const mismatches = ref([]);
const profiles = ref([]);
const countdown = ref('');

// Fetch data from APIs on component mount
onMounted(async () => {
  startCountdown();
  const currentProfile = await axios.get("/api/profile/me");
  var currentProfileUuid = currentProfile.data.uuid;

  const matchesResponse = await axios.get("/api/match/getMatches?userUuid=" + currentProfileUuid);
  mismatches.value = matchesResponse.data;

  if (mismatches.value.length > 3) {
    mismatches.value = mismatches.value.slice(-3);
  }

  // Make a request for each UUID in the trimmed mismatches.value array
  const profileRequests = mismatches.value.map((uuid) => axios.get(`/api/match/getMatchee/${uuid}`));
  profiles.value = await Promise.all(profileRequests);
});

function toProfileDetails(profileId) {
  router.push("./profileDetails/" + profileId);
}

function startCountdown() {
  updateCountdown();
  setInterval(updateCountdown, 1);
}

function updateCountdown() {
  const now = new Date();
  const midnight = new Date(now);
  midnight.setHours(24, 0, 0, 0); // Set to next midnight

  const diff = midnight - now;

  const hours = Math.floor(diff / 1000 / 60 / 60);
  const minutes = Math.floor((diff / 1000 / 60) % 60);
  const seconds = Math.floor((diff / 1000) % 60);

  countdown.value = `${hours}:${minutes}:${seconds}`;
}
</script>
