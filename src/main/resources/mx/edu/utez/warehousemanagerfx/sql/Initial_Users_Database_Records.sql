-- Insert SUPERADMIN into USER_ACCOUNT
INSERT INTO USER_ACCOUNT (Id_User, Full_Name, Email, Phone, Username, Password_Key, Role_Type)
VALUES (1, 'Angel Yazveck Alcocer Duran', 'AngelAlcocer@utez.edu.mx', '555-111-2222', 'AngelYAD', 'AngelYAD', 'SUPERADMINISTRATOR');

-- Link in SUPERADMINISTRATOR table
INSERT INTO SUPERADMINISTRATOR (Id_User)
VALUES (1);


-- Insert ADMIN users into USER_ACCOUNT
INSERT INTO USER_ACCOUNT (Id_User, Full_Name, Email, Phone, Username, Password_Key, Role_Type)
VALUES (2, 'Carlos Martínez', 'carlos.martinez@example.com', '555-123-4567', 'cmartinez', 'cmartinez', 'ADMINISTRATOR');

INSERT INTO USER_ACCOUNT (Id_User, Full_Name, Email, Phone, Username, Password_Key, Role_Type)
VALUES (3, 'Laura Sánchez', 'laura.sanchez@example.com', '555-987-6543', 'lsanchez', 'lsanchez', 'ADMINISTRATOR');

INSERT INTO USER_ACCOUNT (Id_User, Full_Name, Email, Phone, Username, Password_Key, Role_Type)
VALUES (4, 'Miguel Torres', 'miguel.torres@example.com', '555-246-1357', 'mtorres', 'mtorres', 'ADMINISTRATOR');

INSERT INTO USER_ACCOUNT (Id_User, Full_Name, Email, Phone, Username, Password_Key, Role_Type)
VALUES (5, 'Andrea López', 'andrea.lopez@example.com', '555-864-3579', 'alopez', 'alopez', 'ADMINISTRATOR');

INSERT INTO USER_ACCOUNT (Id_User, Full_Name, Email, Phone, Username, Password_Key, Role_Type)
VALUES (6, 'Fernando Díaz', 'fernando.diaz@example.com', '555-753-9514', 'fdiaz', 'fdiaz', 'ADMINISTRATOR');

-- Insert into ADMINISTRATOR, linking with USER_ACCOUNT
INSERT INTO ADMINISTRATOR (Id_User, Id_Branch)
VALUES (2, 1); -- First admin assigned to branch 1

INSERT INTO ADMINISTRATOR (Id_User, Id_Branch)
VALUES (3, 2);

INSERT INTO ADMINISTRATOR (Id_User, Id_Branch)
VALUES (4, 3);

INSERT INTO ADMINISTRATOR (Id_User, Id_Branch)
VALUES (5, NULL); -- Not assigned to any branch yet

INSERT INTO ADMINISTRATOR (Id_User, Id_Branch)
VALUES (6, NULL); -- Not assigned to any branch yet

COMMIT;