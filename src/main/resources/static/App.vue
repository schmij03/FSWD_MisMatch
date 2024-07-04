<template>
  <div>
    <nav class="navbar bg-body-tertiary py-3">
      <div class="container-fluid">
        <a v-if=!isLoggedIn class="navbar-brand" @click="toWelcome" style="color:#65558F">
          <b>MisMatch</b>
        </a>
        <a v-if=isLoggedIn class="navbar-brand">
          <b-button variant="link" class="navbar-brand position-relative" @click="toHome">
            <!-- <b>Home</b> -->
            <img src="/assets/logo.jpg" alt="Logo" width="94" height="49">
          </b-button>
          <b-button variant="link" class="navbar-brand position-relative" @click="toChats">
            <b>Chats</b>
            <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">
              {{ amountChats }}
            </span>
          </b-button>
        </a>
        <div v-if=isLoggedIn class="d-flex">
          <b-dropdown size="lg" variant="link" toggle-class="text-decoration-none" no-caret>
            <template #button-content>
              <img :src="imagePath" alt="Profil" style="width:55px;" class="rounded-pill">
            </template>
            <b-dropdown-item href="#" to="ownProfileEdit">Profil</b-dropdown-item>
            <b-dropdown-item href="#" @click="handleLogout">Abmelden</b-dropdown-item>
          </b-dropdown>
        </div>
      </div>
    </nav>
    <RouterView/>
  </div>
</template>

<script setup>
import {getCurrentInstance, ref, onMounted, provide, watch} from 'vue';
import {createRouter, createWebHashHistory} from 'router';


const routes = [
  {path: '/', name: 'home', component: () => import('./Home.vue')},
  {path: '/welcome', name: 'welcome', component: () => import('./Welcome.vue')},
  {path: '/login', name: 'login', component: () => import('./Login.vue')},
  {path: '/ownProfileEdit', name: 'ownProfileEdit', component: () => import('./OwnProfileEdit.vue')},
  {path: '/register', name: 'register', component: () => import('./Register.vue')},
  {path: '/register2', name: 'register2', component: () => import('./Register2.vue')},
  {path: '/profileDetails/:uuid', name: 'profileDetails', component: () => import('./ProfileDetails.vue'), props: true},
  {path: '/chat', name: 'chat', component: () => import('./Chat.vue')},
  {path: '/regSuccess', name: 'regSuccess', component: () => import('./RegSuccess.vue')},
];

const router = createRouter({
  history: createWebHashHistory(),
  routes
});

const isLoggedIn = ref(false);
const loginName = ref("");
const imagePath = ref("");
const uuid = ref(null);
const amountChats = ref(undefined);

const registrationData = ref({
  name: '',
  email: '',
  phone: '',
  birthdate: '',
  sex: '',
  zip: '',
  city: '',
  password: '',
  passwordcheck: '',
  about: ''
});

provide('registrationData', registrationData);
provide('isLoggedIn', isLoggedIn);
provide('loginName', loginName);

watch(isLoggedIn, (newValue) => {
  if (newValue) {
    fetchProfileData();
  } else {
    resetData();
  }
});

onMounted(() => {
  console.log("App.vue mounted");
  checkLogin();
});

const toHome = () => {
  router.push({ name: 'home' });
}

const toChats = () => {
  router.push({name: 'chat'});
}

const toWelcome = () => {
  router.push({name: 'welcome'});
}

const fetchProfileData = async () => {
  try {
    const response = await axios.get('/api/profile/me');
    uuid.value = response.data.uuid;
    imagePath.value = `/api/profile/${uuid.value}/profileImage`;
    await getAmountUnreadChats();
  } catch (error) {
    console.error("Error fetching profile data:", error);
  }
};

const handleLogout = async () => {
  try {
    await axios.post('/auth/logout', '{}');
    resetData();
    clearRegistrationData();
    router.push({name: 'login'});
  } catch (error) {
    console.error("Error during logout:", error);
    resetData();
    clearRegistrationData();
    router.push({name: 'login'});
  }
}

const clearRegistrationData = () => {
  registrationData.value = {
    name: '',
    email: '',
    phone: '',
    birthdate: '',
    sex: '',
    zip: '',
    city: '',
    password: '',
    passwordcheck: '',
    about: ''
  };
}

const checkLogin = async () => {
  try {
    const response = await axios.get('/auth/current', '{}');
    if (response.data) {
      loginName.value = response.data;
      isLoggedIn.value = true;
    } else {
      resetData();
    }
  } catch (error) {
    console.error("Error checking login:", error);
    resetData();
  }
}

const getAmountUnreadChats = async () => {
  if (!uuid.value) return;

  try {
    const response = await axios.get(`/api/chat/amountUnreadChats/${uuid.value}`);
    let amount = response.data;
    amountChats.value = amount > 0 ? amount : undefined;
  } catch (error) {
    console.error("Error getting unread chats:", error);
    amountChats.value = undefined;
  }
}

const resetData = () => {
  isLoggedIn.value = false;
  loginName.value = "";
  uuid.value = null;
  imagePath.value = "";
  amountChats.value = undefined;
};

const app = getCurrentInstance().appContext.app;
app.use(router);

axios.get('/auth/current', '{}')
    .then(response => {
      loginName.value = response.data;
      isLoggedIn.value = true;
    }).catch(error => {
  console.error("Error in initial login check:", error);
  router.push({name: 'welcome'});
  resetData();
});
</script>
