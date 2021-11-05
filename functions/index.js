const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp(functions.config().firebase);

exports.newUserAdded = functions.auth.user().onCreate(event => {
  const user = event.data;
  const email = user;

  const payload = {
    notification: {
      title: "Nuevo usuario registrado",
      body: "El usuario " + email + " acaba de unirse.",
    },
  };

  const topic = "OnlineUsers";

  return admin.messaging().sendToTopic(topic, payload)
      .then(function(response) {
        console.log("Mensaje enviado con éxito:", response);
      })
      .catch(function(error) {
        console.log("Error enviando el mensaje:", error);
      });
});