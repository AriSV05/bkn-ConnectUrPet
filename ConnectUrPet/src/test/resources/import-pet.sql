INSERT INTO Specie (id, name)
VALUES (1, 'Dog'),
       (2, 'Cat');

INSERT INTO Breed (id, specie_id, name)
VALUES (1, 1, 'Labrador'),
       (2, 1, 'Golden Retriever'),
       (3, 2, 'Siamese'),
       (4, 2, 'Persian');

INSERT INTO Pet (id, image, name, size, date, personality, sex, description, breed_id)
VALUES (1, 'pet1.jpg', 'Buddy', 'Medium', '2024-05-16', 'Friendly', 'Male', 'A playful and energetic dog.', 1),
       (2, 'pet2.jpg', 'Daisy', 'Small', '2024-05-17', 'Calm', 'Female', 'A gentle and loving cat.', 3),
       (3, 'pet3.jpg', 'Max', 'Large', '2024-05-18', 'Active', 'Male', 'A smart and loyal dog.', 2);


INSERT INTO Vaccine (id, specie_id, name)
VALUES (1, 1,  'rabia perruna'),
       (2, 1,  'moquillo'),
       (3, 2,  'rabia gatuna');

-- Inserting data into PetOfGiver
INSERT INTO pet_of_giver (pet_id, giver_id)
VALUES (1, 1),
       (2, 1);

-- Inserting data into PetOfAdopter
/*INSERT INTO pet_of_adopter (pet_id, adopter_id)
VALUES (3, 1);*/

INSERT INTO pet_vaccine (id,pet_id, vaccine_id)
VALUES (1, 1, 1),
       (2, 2, 3);



ALTER SEQUENCE pet_seq RESTART WITH 4;
ALTER SEQUENCE breed_seq RESTART WITH 5;
ALTER SEQUENCE specie_seq RESTART WITH 3;