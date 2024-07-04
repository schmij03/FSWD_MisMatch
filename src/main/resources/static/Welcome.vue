<template xmlns="http://www.w3.org/1999/html">
  <div class="container-fluid">
    <div id="welcome-background">
      <div class="row py-lg-5" >
        <div class="col-lg-6 col-md-8 mx-auto" id="intro-box">
          <h1 class="fw-light text-center" id="title">Willkommen bei MisMatch</h1>
          <p class="lead text-body-secondary text-center">
            <b>Du bist ein Morgenmensch, sie ein Nachtschwärmer? Zusammen seid ihr eine 24-Stunden-Party! Erlebe die Magie der Unterschiedlichkeit – gemeinsam unschlagbar! </b><br><br>  Bei uns kannst du neue Leute kennenlernen, aber nicht wie du es kennst, basiernd auf deinen Intressen. Sondern basierend auf euren Unterschieden. Ganz nach dem Motto “was sich neckt, dass liebt sich”. Melde dich an und starte dein Abenteuer noch heute!</p>
          <p>
            <div class="container-fluid text-center">
              <button @click="LoginButtonAction" class="btn btn-primary btn-lg m-1 welcome-btn">Anmelden</button>
              <button @click="RegisterButtonAction" class="btn btn-primary btn-lg m-1 welcome-btn">Registrieren</button>
            </div>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'PlaceholderComponent',
  data() {
    return {
      imageSrc: './assets/mismatch_logo.jpg'
    };
  },

  methods: {
    LoginButtonAction() {
      this.$router.push('/login');
      console.log('Login button clicked');
    },
    RegisterButtonAction() {
      this.$router.push('/register');
      console.log('Register button clicked');
    }
  }
}
</script>

<style scoped>
  #intro-box {
    background: rgba(255, 255, 255, .85);
    border-radius: 1rem;
    padding: 20px;
  }

  #welcome-background {
    background: url(./assets/background.jpg) center center / cover no-repeat; 
    border-radius: 1rem;
    width: 100%;
    height: 600px;
  }

  #title {
    color: #65558F;
  }

  .welcome-btn {
    border-color: #65558F;
    background-color:#65558F
  }
</style>
