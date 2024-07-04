<template>
  <div class="success-container">
    <h1>Herzlich Willkommen, {{ name }}!</h1>
    <p>Die Registration ist abgeschlossen.</p>
    <button @click="goToOverview">Zum Login</button>
  </div>
</template>

<script>
import { inject } from 'vue';
import { useRouter } from 'router';

export default {
  name: 'RegSuccess',
  setup() {
    const router = useRouter();
    const registrationData = inject('registrationData');

    const name = registrationData.value.name;

    const goToOverview = () => {
      router.push({ name: 'login' }); // Navigate to the overview page
    };

    return {
      name,
      goToOverview
    };
  }
};
</script>

<style scoped>
.success-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #FEF7FF;
  text-align: center;
}

h1 {
  margin-bottom: 20px;
  color: #65558F;
}

p {
  margin-bottom: 40px;
  color: #65558F;
}

button {
  padding: 10px 20px;
  background-color: #65558F;
  color: white;
  border: none;
  border-radius: 20px;
  cursor: pointer;
}

button:hover {
  background-color: #4d3d73;
}
</style>
