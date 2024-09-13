const { onRequest } = require("firebase-functions/v2/https");
const { getFirestore } = require("firebase-admin/firestore");
const OpenAI = require("openai");
const bodyParser = require('body-parser');
const express = require("express");
const admin = require("firebase-admin");

// Inicializa la aplicación Firebase Admin
admin.initializeApp();

// Configuración de OpenAI
const openai = new OpenAI({apiKey:"GPT_API_KEY_CUP"});

// Inicializa Firestore
const db = getFirestore();

const app = express();
app.use(bodyParser.json());

// ------- PARTE   LOGIN -------
app.post("/v1/api/singUp/adopter", async (req, res) => {
  try {
    const data = req.body;

    const userRef = await db.collection("users").add(data);

    const adopterDetails = { user_id: userRef.id };
    const adopterRef = await db.collection("adopters").add(adopterDetails);

    const rolDetails = { name: "ROLE_ADOPTER", user_id: userRef.id };
    const rolRef = await db.collection("user_roles").add(rolDetails);

    return res
      .status(201)
      .json({ username: data.username, password: data.password });
  } catch {
    // console.error("Error al crear adoptante:", error);
    return res
      .status(500)
      .json({ error: "Ocurrió un error al crear el adoptante" });
  }
});

app.post("/v1/api/singUp/giver", async (req, res) => {
  try {
    const data = req.body;

    const userRef = await db.collection("users").add(data);

    const giverDetails = { user_id: userRef.id };
    const giverRef = await db.collection("givers").add(giverDetails);

    const rolDetails = { name: "ROLE_GIVER", user_id: userRef.id };
    const rolRef = await db.collection("user_roles").add(rolDetails);

    return res
      .status(201)
      .json({ username: data.username, password: data.password });
  } catch {
    // console.error("Error al crear giver:", error);
    return res
      .status(500)
      .json({ error: "Ocurrió un error al crear el giver" });
  }
});

app.post("/v1/api/login", async (req, res) => {
  try {
    const { username, password } = req.body;

    if (!username || !password) {
      return res.status(400).json({ error: "Se requieren email y password" });
    }
    const userQuerySnapshot = await db
      .collection("users")
      .where("email", "==", username)
      .limit(1)
      .get();
      
    if (userQuerySnapshot.empty) {
      return res.status(404).json({ error: "Usuario no encontrado" });
    }
    const userDoc = userQuerySnapshot.docs[0];
    const userData = userDoc.data();

    const { enable } = userData;
    
    if (userData.password !== password) {
      return res.status(401).json({ error: "Contraseña incorrecta" });
    }

    
    const idUser = userDoc.id;

    const userRoleQuerySnapshot = await db
    .collection("user_roles")
    .where("user_id", "==", idUser)
    .limit(1)
    .get();

    if (userRoleQuerySnapshot.empty) {
      return res.status(404).json({ error: "Rol de usuario no encontrado" });
    }

    const userRoleDoc = userRoleQuerySnapshot.docs[0];
    const userRole = userRoleDoc.data().name;


    let userTypeQuerySnapshot = await db
      .collection("givers")
      .where("user_id", "==", idUser)
      .limit(1)
      .get();

    if (userTypeQuerySnapshot.empty) {
      userTypeQuerySnapshot = await db
        .collection("adopters")
        .where("user_id", "==", idUser)
        .limit(1)
        .get();
    }

    const userTypeDoc = userTypeQuerySnapshot.docs[0];
    const userTypeId = userTypeDoc.id;


    return res.status(200).json({
      password: null,
      username: username,
      authorities: [{ authority: userRole }],
      accountNonExpired: true,
      accountNonLocked: true,
      credentialsNonExpired: true,
      enable: enable,
      id: userTypeId
    });


  } catch (error) {
    return res
      .status(500)
      .json({ error: "Ocurrió un error al iniciar sesión" });
  }
});

// -----------------------------------

/**
 * Agregar una mascota
 */
app.post("/v1/api/pet/giverAddPet", async (req, res) => {
  try {
    const data = req.body;
    const giverID = data.idUser;

    delete data.idUser;

    const petRef = await db.collection("pets").add(data);

    const petGiverDetails = { petID: petRef.id, giverID: giverID };

    const giverPetsRef = await db
      .collection("petsOfGiver")
      .add(petGiverDetails);

    return res.status(201).json({
      message: "PetsOfGiver creado exitosamente",
      id: giverPetsRef.id,
    });
  } catch (error) {
    console.error("Error al crear PetsOfGiver:", error);
    return res
      .status(500)
      .json({ error: "Ocurrió un error al crear PetsOfGiver" });
  }
});

/**
 * Editar una mascota especifica, la data debe ir con los campos a reescribir
 * correctamente
 */
app.post("/v1/api/pet/giverEditPet", async (req, res) => {
  try {
    const data = req.body;
    const petID = data.petID;
    delete data.petID;

    const petRef = await db.collection("pets").doc(petID).update(data);

    return res.status(201).json({
      message: "Mascota editada exitosamente",
    });
  } catch (error) {
    console.error("Error al editar la mascota:", error);
    return res
      .status(500)
      .json({ error: "Ocurrió un error al editar la mascota" });
  }
});

/**
 * Una mascota especifica, para ver desde adopter
 */

app.post("/v1/api/pet/onePet", async (req, res) => {
  try {
    const petID = req.body.petID;

    const snapshot = await db.collection("pets").doc(petID).get();
    const petData = snapshot.data();

    // Obtener el nombre de la especie utilizando el specieID
    const breedID = petData.breed.id;
    const breedSnapshot = await db.collection("breeds").doc(breedID).get();
    const breedData = breedSnapshot.data();
    const specieID = breedData.specieID;

    // Obtener el nombre de la especie utilizando el specieID
    const specieSnapshot = await db.collection("species").doc(specieID).get();
    const specieData = specieSnapshot.data();
    const specieName = specieData.name;

    const giverSnapshot = await db.collection("petsOfGiver").where("petID", "==", petID).limit(1).get();

    giverID = giverSnapshot.docs[0].data().giverID;

    // Agregar el nombre de la especie a los datos de mascotas
    petData.breed.specieName = specieName;
    res.status(200).json({ petData:petData, giverID:giverID });
  } catch (error) {
    console.error("Failed to retrieve data:", error);
    res
      .status(500)
      .json({ error: "Failed to retrieve data due to an internal error." });
  }
});

/**
 * Una mascota especifica, para editar desde giver
 */

app.post("/v1/api/pet/onePetEdit", async (req, res) => {
  try {
    // Obtener el petID desde el cuerpo de la solicitud
    const petID = req.body.petID;

    // Obtener los datos de la mascota
    const petSnapshot = await db.collection("pets").doc(petID).get();
    const petData = petSnapshot.data();

    // Obtener el nombre de la especie utilizando el specieID
    const breedID = petData.breed.id;
    const breedSnapshot = await db.collection("breeds").doc(breedID).get();
    const breedData = breedSnapshot.data();
    const specieID = breedData.specieID;

    const specieSnapshot = await db.collection("species").doc(specieID).get();
    const specieData = specieSnapshot.data();
    const specieName = specieData.name;

    petData.breed.specieName = specieName;

    const breedsSnapshot = await db.collection("breeds").get();
    const breeds = breedsSnapshot.docs.map((doc) => ({
      id: doc.id,
      ...doc.data(),
    }));

    // Obtener todas las especies
    const speciesSnapshot = await db.collection("species").get();
    const species = speciesSnapshot.docs.map((doc) => ({
      id: doc.id,
      ...doc.data(),
    }));

    // Devolver los datos de mascotas, especies y razas como respuesta JSON
    res.status(200).json({ petData, species, breeds });
  } catch (error) {
    console.error("Failed to retrieve data:", error);
    res
      .status(500)
      .json({ error: "Failed to retrieve data due to an internal error." });
  }
});
app.post("/v1/api/pet/petsOfGiver", async (req, res) => {
  try {
    // Obtener el giverID desde el cuerpo de la solicitud
    const giverID = req.body.giverID;

    // Consultar la colección 'petsOfGiver' para obtener los documentos que coincidan con giverID
    const snapshot = await db
      .collection("petsOfGiver")
      .where("giverID", "==", giverID)
      .get();

    const petIDs = [];
    const petsData = [];

    // Iterar sobre cada documento en el snapshot
    snapshot.forEach((doc) => {
      // Obtener el ID de la mascota del documento actual
      petIDs.push(doc.data().petID);
    });

    // Obtener los datos de cada mascota en paralelo
    const petDataPromises = petIDs.map(async (petID) => {
      const petSnapshot = await db.collection("pets").doc(petID).get();

      const breedSnapshot = await db.collection("breeds").doc(petSnapshot.data().breed.id).get();
      const specieSnapshot = await db.collection("species").doc(breedSnapshot.data().specieID).get();
      const petDetails = petSnapshot.data();
      petDetails.breed = breedSnapshot.data();
      petDetails.breed.specieName = specieSnapshot.data().name;

      return { petID: petSnapshot.id, petData: petDetails };
    });

    // Esperar a que todas las promesas se completen
    const resolvedPetData = await Promise.all(petDataPromises);

    // Almacenar los datos de mascotas resueltos en petsData
    resolvedPetData.forEach((petData) => {
      petsData.push(petData);
    });

    // Devolver los datos de mascotas como respuesta JSON
    res.status(200).json({ "petsData":petsData });
  } catch (error) {
    console.error("Failed to retrieve data:", error);
    res
      .status(500)
      .json({ error: "Failed to retrieve data due to an internal error." });
  }
});

/**
 * Todas las mascotas que son de todos los giver, para leer desde adopter
 */

app.get("/v1/api/pet/giversPets", async (req, res) => {
  try {
    // Obtener todos los documentos de la colección 'petsOfGiver'
    const snapshot = await db.collection("petsOfGiver").get();
    // Inicializar un array para almacenar los IDs de las mascotas
    const petIDs = [];

    // Iterar sobre cada documento en el snapshot
    snapshot.forEach((doc) => {
      // Obtener el ID de la mascota del documento actual
      pet = doc.data().petID;
      petIDs.push(pet);
    });

    const petData = [];

    // Crear promesas para obtener los datos de las mascotas
    const petDataPromises = petIDs.map(async (petID) => {
      // Obtener el documento de la mascota usando su ID
      const petSnapshot = await db.collection("pets").doc(petID).get();

      // Verificar si el documento existe
      if (petSnapshot.exists) {
        const breedSnapshot = await db.collection("breeds").doc(petSnapshot.data().breed.id).get();
        const specieSnapshot = await db.collection("species").doc(breedSnapshot.data().specieID).get();
        const petDetails = petSnapshot.data();
        petDetails.breed = breedSnapshot.data();
        petDetails.breed.specieName = specieSnapshot.data().name;

        petData.push({ id: petSnapshot.id, details: petDetails });
      }
    });

    // Esperar a que todas las promesas se resuelvan
    await Promise.all(petDataPromises);

    // Enviar los datos de las mascotas como respuesta
    res.status(200).json({ petData });
  } catch (error) {
    console.error("Error al obtener las mascotas:", error);
    res.status(500).json({ error: "Ocurrió un error al obtener las mascotas" });
  }
});

/**
 * Lista de todas las vacunas
 */

app.post("/v1/api/pet/vaccines", async (req, res) => {
  try {
    const vaccSnapshot = await db.collection("vaccines").get();
    const data = [];

    vaccSnapshot.forEach((doc) => {
      // Obtener los datos de cada documento y agregarlos al array
      data.push({ name: doc.data().name, id: doc.id });
    });

    const petID = req.body.petID;

    const snapshot = await db.collection("pets").doc(petID).get();
    const vaccines = snapshot.data() && snapshot.data().vaccines ? snapshot.data().vaccines : [];

    let vaccineNames = [];
    if (vaccines && vaccines.length > 0) {
      const promises = vaccines.map(async (vaccineID) => {
        const snapVacc = await db.collection("vaccines").doc(vaccineID).get();
        return { id: snapVacc.id, name: snapVacc.data().name };
      });
      vaccineNames = await Promise.all(promises);
    }

    // Combine las respuestas en un solo objeto JSON
    const responseData = {
      petVaccines: vaccineNames,
      allVaccines: data.length > 0 ? data : [],
    };

    // Envía la respuesta
    res.status(200).json(responseData);
  } catch (error) {
    console.error("Error al obtener las vacunas:", error);
    res.status(500).json({ error: "Ocurrió un error al obtener las vacunas" });
  }
});

/**
 * Vacunas de una mascota especifica, para leer desde adopter
 */

app.post("/v1/api/pet/petVaccines", async (req, res) => {
  try {
    const petID = req.body.petID;

    const snapshot = await db.collection("pets").doc(petID).get();
    const vaccines = snapshot.data().vaccines;

    const promises = vaccines.map(async (vaccineID) => {
      const snapVacc = await db.collection("vaccines").doc(vaccineID).get();
      return { id: snapVacc.id, name: snapVacc.data().name };
    });

    const vaccineNames = await Promise.all(promises);

    res.status(200).json({ petVaccines: vaccineNames });
  } catch (error) {
    console.error("Error al obtener las vacunas:", error);
    res.status(500).json({ error: "Ocurrió un error al obtener las vacunas" });
  }
});

/**
 * Con el id de la mascota, se actualiza y agrega la lista de vacunas en la mascota
 * vaccines es el campo del documento a actualizar
 */
app.post("/v1/api/pet/addPetVaccines", async (req, res) => {
  try {
    const body = req.body;
    const petID = body.petID;
    delete body.petID;

    const snapshot = await db
      .collection("pets")
      .doc(petID)
      .update({ vaccines: body.vaccinesID });

    res.status(200).json({ message: "Vacunas guardadas con exito" });
  } catch (error) {
    console.error("Error al obtener las vacunas:", error);
    res.status(500).json({ error: "Ocurrió un error al obtener las vacunas" });
  }
});

app.get("/v1/api/pet/speciesAndBreeds", async (req, res) => {
  try {
    const breedsSnapshot = await db.collection("breeds").get();
    const breeds = breedsSnapshot.docs.map((doc) => ({
      id: doc.id,
      ...doc.data(),
    }));

    // Obtener todas las especies
    const speciesSnapshot = await db.collection("species").get();
    const species = speciesSnapshot.docs.map((doc) => ({
      id: doc.id,
      ...doc.data(),
    }));

    res.status(200).json({ breeds, species });
  } catch (error) {
    console.error("Error al obtener las especies:", error);
    res.status(500).json({ error: "Ocurrió un error al obtener las especies" });
  }
});


app.post("/v1/api/pet/recommendations", async (req, res) => {
  try {
    const body = req.body;
    
    const prompt = `Dame sugerencias de cuidados de un animal de:${body}\n`+
    "devuelve como un json los siguientes datos:\n"+
    "nutrition{foodType, portionSize, supplements}, exercise{daily,activities}, grooming{brushing,bathing,earCleaning,nailTrimming}, healthCare{vaccination}."+"\nHazlo en un string";

    const response = await openai.chat.completions.create({
      model: 'gpt-3.5-turbo', 
      messages: [{ role: 'user', content: prompt }]
    });
    return res.status(200).json(
      JSON.parse(response.choices[0].message.content)
    );

  } catch (error) {
    console.error("Error al generar la respuesta:", error);
    res.status(500).json({ error: "Ocurrió un error al generar la respuesta" });
  }
});


//---------------------------------------------------------------------------------

app.get("/v1/api/adopters/:id", async (req, res) => {
  const snapshot = await db.collection("adopters").doc(req.params.id).get();

  if (!snapshot.exists) {
    return res.status(404).send("Adopter not found");
  }

  const idUser = snapshot.data().user_id;
  const dataUser = await db.collection("users").doc(idUser).get();

  if (!dataUser.exists) {
    return res.status(404).send("User not found");
  }

  res.status(200).send(JSON.stringify({ id: idUser, ...dataUser.data() }));
});

app.get("/v1/api/givers/:id", async (req, res) => {
  const snapshot = await db.collection("givers").doc(req.params.id).get();

  if (!snapshot.exists) {
    return res.status(404).send("Giver not found");
  }

  const idUser = snapshot.data().user_id;
  const dataUser = await db.collection("users").doc(idUser).get();

  if (!dataUser.exists) {
    return res.status(404).send("User not found");
  }

  res.status(200).send(JSON.stringify({ id: idUser, ...dataUser.data() }));
});


app.put("/v1/api/givers/:id", async (req, res) => {
  try {
    const giverId = req.params.id;
    const giverSnapshot = await db.collection("givers").doc(giverId).get();

    if (!giverSnapshot.exists) {
      return res.status(404).send("Giver not found");
    }

    const idUser = giverSnapshot.data().user_id;
    const userRef = db.collection("users").doc(idUser);
    const userSnapshot = await userRef.get();

    if (!userSnapshot.exists) {
      return res.status(404).send("User not found");
    }

    const userData = req.body;

    // Actualizar datos del usuario
    await userRef.update(userData);

    // Obtener los datos actualizados
    const updatedUserSnapshot = await userRef.get();
    const updatedUserData = { id: idUser, ...updatedUserSnapshot.data() };

    res.status(200).send(JSON.stringify(updatedUserData));
  } catch (error) {
    console.error("Error updating giver: ", error);
    res.status(500).send("Internal Server Error");
  }
});

app.put("/v1/api/adopters/:id", async (req, res) => {
  try {
    const adopterId = req.params.id;
    const adopterSnapshot = await db.collection("adopters").doc(adopterId).get();

    if (!adopterSnapshot.exists) {
      return res.status(404).send("Adopter not found");
    }

    const idUser = adopterSnapshot.data().user_id;
    const userRef = db.collection("users").doc(idUser);
    const userSnapshot = await userRef.get();

    if (!userSnapshot.exists) {
      return res.status(404).send("User not found");
    }

    const userData = req.body;

    // Actualizar datos del usuario
    await userRef.update(userData);

    // Obtener los datos actualizados
    const updatedUserSnapshot = await userRef.get();
    const updatedUserData = { id: idUser, ...updatedUserSnapshot.data() };

    res.status(200).send(JSON.stringify(updatedUserData));
  } catch (error) {
    console.error("Error updating adopter: ", error);
    res.status(500).send("Internal Server Error");
  }
});


//----------------------------------------------------------------------------------

app.get("/v1/api/reviews/giver/:giverId", async (req, res) => {
  try {
    const giverId = req.params.giverId;

    const reviewsRef = db.collection("reviews").where("giverId", "==", giverId);
    const querySnapshot = await reviewsRef.get();

    if (querySnapshot.empty) {
      return res.status(404).send("Reviews not found for this giver");
    }

    const reviewsData = [];
    querySnapshot.forEach((doc) => {
      reviewsData.push({ id: doc.id, ...doc.data() });
    });

    res.status(200).json(reviewsData);
  } catch (error) {
    console.error("Error retrieving reviews:", error);
    res.status(500).send("Error retrieving reviews");
  }
});

app.post("/v1/api/reviews", async (req, res) => {
  try {
    const reviewData = req.body;

    // Add the review to the Firestore database
    const reviewRef = await db.collection("reviews").add(reviewData);
    const reviewId = reviewRef.id;

    return res.status(201).json({
      message: "Review added successfully",
      id: reviewId,
    });
  } catch (error) {
    console.error("Error adding review:", error);
    return res.status(500).json({
      error: "An error occurred while adding the review",
    });
  }
});

//------------ Reactions ------------

// Add
app.post("/v1/api/reactions", async (req, res) => {
  try {
    const data = req.body;
    const reactionRef = await db.collection("reactions").add(data); 

    const notisData = {giver: data.giver, reaction: reactionRef.id, view: false}

    await db.collection("notification").add(notisData)

    return res.status(200).json({
      message: "Reaction added successfully",
    });
  } catch (error) {
    console.error("Error adding reaction:", error);
    return res.status(500).json({
      error: "An error occurred while adding the reaction",
    });
  }
});

//------------Notis--------------------
app.post("/v1/api/notis/readNotis", async (req, res) => {
  try {
    const data = req.body;

    const notiSnap = await db.collection("notification").where("giver", "==", data.id).get()

    if(notiSnap.empty){
      return res.status(404).json({
        message: `No notifications found for the giver ID: ${data.id}`,
      });
    }

    const notifications = [];
    
    await Promise.all(
      notiSnap.docs.map(async (doc) => {
        const reactionDoc = await db.collection("reactions").doc(doc.data().reaction).get();
        const adopterDoc = await db.collection("adopters").doc(reactionDoc.data().adopter).get();
        const userDoc = await db.collection("users").doc(adopterDoc.data().user_id).get();

        notifications.push({ 
          message: `Nueva reaccion de ${userDoc.data().name}`, 
          view: doc.data().view 
        });
      })
    );


    return res.status(200).json({ notisList:notifications });
  } catch (error) {
    return res.status(500).json({
      error: "An error occurred while reading the notification",
    });
  }
});


exports.connect = onRequest(app);
