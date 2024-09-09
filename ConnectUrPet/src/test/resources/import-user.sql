-- Insert into role table
INSERT INTO role (id, name) VALUES (1, 'ROLE_ADOPTER');
INSERT INTO role (id, name) VALUES (2, 'ROLE_GIVER');

-- Insert into privilege table
INSERT INTO privilege (id, name) VALUES (1, 'READ_PRIVILEGE');
INSERT INTO privilege (id, name) VALUES (2, 'WRITE_PRIVILEGE');

-- Insert into role_privilege table
INSERT INTO role_privilege (role_id, privilege_id) VALUES (1, 1);
INSERT INTO role_privilege (role_id, privilege_id) VALUES (1, 2);
INSERT INTO role_privilege (role_id, privilege_id) VALUES (2, 1);
INSERT INTO role_privilege (role_id, privilege_id) VALUES (2, 2);

-- Insert into user_information table
INSERT INTO user_information (id, create_date, name, email, password, description, location, enabled, token_expired) VALUES (1, '2023-01-01', 'John Doe', 'giver@example.com', '$2a$10$6hg/QTw8Th1EmYtg9/5HhOmRdg320A6J8DumNUqx.4AltXZAjp0VK', 'Description of John Doe', 'Location of John Doe', true, false);
INSERT INTO user_information (id, create_date, name, email, password, description, location, enabled, token_expired) VALUES (2, '2023-01-02', 'Jane Smith', 'adopter@example.com', '$2a$10$6hg/QTw8Th1EmYtg9/5HhOmRdg320A6J8DumNUqx.4AltXZAjp0VK', 'Description of Jane Smith', 'Location of Jane Smith', true, false);

-- Insert into user_role table
INSERT INTO user_role (user_id, role_id) VALUES (2, 1);
INSERT INTO user_role (user_id, role_id) VALUES (1, 2);

-- Insert into adopter table
INSERT INTO adopter (id, user_information_id) VALUES (1, 2);

-- Insert into giver table
INSERT INTO giver (id, user_information_id) VALUES (1, 1);

ALTER SEQUENCE user_information_seq RESTART WITH 3;
ALTER SEQUENCE adopter_seq RESTART WITH 2;
ALTER SEQUENCE giver_seq RESTART WITH 2;
ALTER SEQUENCE role_seq RESTART WITH 4;
ALTER SEQUENCE privilege_seq RESTART WITH 3;

/*--ANTES

-- Insert data into user_information table
INSERT INTO user_information (id, create_date, name, email, password, description, location, token_expired)
VALUES (1, '2024-05-12', 'John Doe', 'john@example.com', 'password123', 'ADOPTER', 'New York', false),
       (2, '2024-05-12', 'Jane Smith', 'jane@example.com', 'password456', 'ADOPTER', 'Los Angeles', false),
      (3, '2024-05-16', 'Giver 1', 'giver1@example.com', 'giverpassword1', 'GIVER', 'California', false),
      (4, '2024-05-16', 'Giver 2', 'giver2@example.com', 'giverpassword2', 'GIVER', 'Miami', false);

-- Insert data into Adopter table
INSERT INTO Adopter (id, user_Information_id)
VALUES (1, 1),
       (2, 2);

-- Insert data into Giver table
INSERT INTO Giver (id, user_Information_id)
VALUES (1, 3),
      (2, 4);

-- Insert data into Review table
--INSERT INTO Review (id, puntuation, text, adopterID, giverID)
--VALUES --(3, 4.5, 'Great experience!', 1, 1),
--      (4, 3.0, 'Good!', 1, 1);
--      (5, 4.0,'Very satisfied!', 2, 4);

-- Insert data into Role table
INSERT INTO Role (id, name, user_information_id)
VALUES (1, 'Adopter', 1),
      (2, 'Giver', 3);

-- Insert data into Privilege table
INSERT INTO Privilege (id, name, role_id)
VALUES (1, 'READ', 1),
       (2, 'WRITE', 1),
       (3, 'DELETE', 2);*/